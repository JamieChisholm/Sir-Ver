/**
 * 
 */
package src;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;





/**
 * @author Jamie Chisholm
 *
 */
public class Command extends TextChannelReader{

	public Command(MessageReceivedEvent event) {
		super(event);
	}

	public void execute() {
		String msg = getMsg();
		
		//Large nested if/else determining what method is to be done
		//based on the given command
		if(msg.startsWith("!move")) {
			move();
		}
		else if(msg.startsWith("!poll")) {
			poll();
		}
		else if(msg.startsWith("!help")) {
			help();
		}
		else if(!getIsBot()){
			getTextChannel().sendMessage("Sorry, that's not a recognised command. If you want to see a full list of commands, try !help").queue();
		}
	}

	private void move() {
		List<VoiceChannel> channels = getGuild().getVoiceChannels();
		Boolean destinationValid = false;
    	VoiceChannel destination = null;
    	Boolean isInVoice = false;
    	VoiceChannel originChannel = null;
    	List<Member> membersToMove = null;
    	
		//Get voice channels
    	for(VoiceChannel i : channels ) {        		
    		
    		//Get the members of the current channel iteration
    		List<Member> members = i.getMembers();	
    		
    		//Check for the author in channel									
    		for(Member member : members) {
    			if(member.getUser().equals(author)) {
    				isInVoice = true;
    				originChannel = i;
    				membersToMove = members;
    			}
    		}	
    	}
    	
    	if(isInVoice) {
    		//Check that the specified destination channel is valid
    		String targetChannelName = msg.replaceFirst("!move ","");
    		
    		for(VoiceChannel target : channels) {
    			if( targetChannelName.contentEquals(target.getName()) ) {
    				destinationValid = true;
    				destination = target;
    			}
    		}
    	}
    	else {
    		message.getTextChannel().sendMessage("You're not in a voice channel").queue();
    	}
    	
    	//Move members
    	if(destinationValid) {
    		Long iterator = (long)0;
    		for(Member currMem : membersToMove ) {
    			try {
        			guild.moveVoiceMember(currMem, destination).completeAfter(iterator,TimeUnit.SECONDS);
        			System.out.println("Moving Member: " + currMem.getUser().getName());
        			iterator += (long)0.1;
    			}
    			catch (Exception e) {
    				System.out.println("Error moving member " + currMem.getUser().getName());
    			}
    		}
    	}
	}
	
	private void poll() {
		new Poll(event).start();
	}
	
	private void help() {
		//TO-DO: Fill help stub
		getTextChannel().sendMessage("The help feature is still in development").queue();		//Placeholder Response
	}
}
