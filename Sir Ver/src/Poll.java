/**
 * 
 */
package src;

import java.util.ArrayList;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;



/**
 * @author Jamie Chisholm
 *
 */
public class Poll extends Thread{
	
	MessageReceivedEvent event = null;
	JDA jda = null;
	TextChannel pollChannel = null;
	
	long responseInterval = 15000;							//1 day in milliseconds (86400000)						
	String pollChannelName = "dungeon-scrolls";
	String pollAnnouncementText = "It's time for this weeks D&D availability poll. Click on the ðŸ‘� below ALL days you "
			+ "are available for. If you don't have any days available for, there's an option for that at the bottom (you still need to respond).";
	String pollResultsText = "The following day(s) are available for everybody:\n";
	
	public Poll(MessageReceivedEvent event) {
		this.event = event;
		jda = event.getJDA();													//Add JDA object to this class
		
		//Get the target text channel for the poll
		for(TextChannel channel : jda.getTextChannelsByName(pollChannelName,false)) {
			if(channel.getGuild().getIdLong() == event.getGuild().getIdLong()) {		//Making sure the target text channel is in the correct server
				pollChannel = channel;
			}
		}
		if(pollChannel == null) {
			System.out.println("No matching text channel found");
		}
	}
	
	public void run() {
		ArrayList<Message> pollMessages = generatePoll();
		
		//Checking to see of all 8 messages are stored
		if(pollMessages.isEmpty()) {		
			//Poll generation has failed
			pollChannel.sendMessage("Poll creation has failed. Message ID list is empty.").queue();
		}
		else {		
			//Poll creation success
			//Add example reactions for users to follow (none needed for announcement message)
			int i = 0;
			for(Message msg : pollMessages) {
				if(i>0) {
					msg.addReaction("ðŸ‘�").queue();		//Provide base emote so users only need to click once to vote
				}
				else {
					msg.pin().queue();				//Pin announcement message
				}
				i++;
			}
			
			//Wait for responses
			try {
				Thread.sleep(responseInterval);
				sumarizePoll(pollMessages);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sumarizePoll(ArrayList<Message> pollMessages) {
		try {
				//Updating original messages
				Message msg;
				for(int i = 0; i<pollMessages.size(); i++) {
					msg = pollMessages.get(i);
					msg = pollChannel.retrieveMessageById(msg.getId()).complete();
					pollMessages.set(i,msg);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				PollResult pollResult = new PollResult(pollMessages);
				
				if(pollResult.allResponded()) {
					if(pollResult.hasAgreedDay()) {
						pollMessages.get(0).unpin().queue();		//Remove original announcement from pinned messages
						String resultAnnouncement = pollResultsText;
						for(String day : pollResult.getAvailableDays()) {
							resultAnnouncement.concat(day + "\n");
						}
						
					}
					else {
						pollChannel.sendMessage("No common day found. Please look at the original poll to see results. The original post will remain pinned temporarily.");
						wait(responseInterval);
						pollMessages.get(0).unpin().queue();
					}
					
				}
				//Not all people responded, remind them & wait for responses again
				else if(pollResult.getNonResponders() != null){											
					ArrayList<User> nonResponders = pollResult.getNonResponders();
					String responseReminder = "The following users have not yet responded to the poll:\n";
					for(User user : nonResponders) {
						responseReminder.concat("@" + user.getName() + "\n");
					}
					responseReminder.concat("You can locate the original poll in the pinned messages for this text channel. Please react with a ðŸ‘�  to indicate your free days.");
					pollChannel.sendMessage(responseReminder).queue();
					
					wait(responseInterval);				//Wait, & then recurse
					sumarizePoll(pollMessages);
				}
				else {
					String errorMessage = "pollresult.getNonResponders() has returned null";
					System.out.println(errorMessage);
				}
				
			}
		catch(Exception e) {
			//Exceptions here can happen when any of the original poll messages are deleted
			String errorMsg = "Automatic poll summary has failed. The poll has been removed from pinned "
					+ "messages, but a manual summary of the poll can still be done. \nError Message:\n" + e;
			
			System.out.println(e);
			pollChannel.sendMessage(errorMsg).queue();
			pollMessages.get(0).unpin().queue();
		}
		
	}
	
	private ArrayList<Message> generatePoll() {
		String announcementText = pollAnnouncementText;
		String rateLimitErrorMsg = "Error building poll. I have been rate limited by Discord. Please contact server owner for more.";
		
		ArrayList<Message> pollMessages = new ArrayList<Message>();		
		
		/*
		 * We need to use the complete() method to post these, as queue() does not return the message object
		 * We need the message object to be able to listen to the responses by ID
		 * complete() does not have built in rate-limit protection like queue()
		 * Therefore, we need to catch exceptions in case we are rate-limited by Discord
		 * We also need to account for rate limiting
		 */
		try { 
				ArrayList<String> messageContents = new ArrayList<String>();
				messageContents.add(announcementText);
				messageContents.add("Saturday");
				messageContents.add("Sunday");
				messageContents.add("Monday");
				messageContents.add("Tuesday");
				messageContents.add("Wednesday");
				messageContents.add("Thursday");
				messageContents.add("Friday");
				messageContents.add("Not available this week");
				
				for(String msg : messageContents) {
					pollMessages.add( pollChannel.sendMessage(msg).complete() );
					Thread.sleep(142);		//Delay between messages to prevent rate limiting by Discord
				}
			
				if(false) {															//This if doesn't itself do anything, but the compiler needs it
					pollChannel.sendMessage(rateLimitErrorMsg).complete(false);
				}
		}
		catch(RateLimitedException e) {
			System.out.println("Discord has rate-limited output. Can retry after " + e.getRetryAfter());
		}
		catch(Exception e) {
			System.out.println("An error has occered:\n" + e.toString());
		}
		
		return pollMessages;
	}
}
