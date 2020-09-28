package src;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.entities.Member;

/**
 * @author Jamie Chisholm
 *
 */

public class PollResult {
	
	private ArrayList<Message> pollMessages = null;
	private ArrayList<ArrayList<User>> pollResponses = null;
	private ArrayList<User>userList = null;
	private String voteReaction = "ðŸ‘�";

	public PollResult(ArrayList<Message> pollMessages) {
		this.pollMessages = pollMessages;
		
		build();
	}
	
	private void build() {		
		pollResponses = new ArrayList<ArrayList<User>>();
		userList = new ArrayList<User>();
		
		List<Member> allMembers = pollMessages.get(0).getGuild().getMembers();
		
		//Build an array of users expected to respond
		for(Member member : allMembers) {						//For each member
			List<Role> roles = member.getRoles();				//Get that members roles
			for(Role role : roles) {
				if(role.getName().contentEquals("D&D")) {		//If they have the "D&D" role, add them to the list of users expected to respond
					userList.add(member.getUser());
				}
			}
		}

		for(int i2 = 1; i2<pollMessages.size(); i2++) {						//Iterating through poll message days (i2=1 to skip the announcement message)
			pollResponses.add(getRespondantUsers(pollMessages.get(i2)));	//Call getRespondantUsers, store arrays in another array
		}
		
		
	}
	
	private ArrayList<User> getRespondantUsers(Message message) {
		ArrayList<User> respondants = new ArrayList<User>();
		
		List<MessageReaction>reactions = message.getReactions();
		
		for(MessageReaction reaction : reactions) {
			//String thisReaction = reaction.getReactionEmote().getEmote().toString();
			String thisReaction = reaction.getReactionEmote().getEmoji();
			if( thisReaction.equals(voteReaction) ) {		//Check that the reaction is the correct type (otherwise individual users could vote multiple times)
				ReactionPaginationAction users = reaction.retrieveUsers();
				users.forEach( 									//Builds an array of respondent user objects
							(user) -> respondants.add(user) 
						);			
			}
		}
		
		return respondants;
	}
	
	public boolean allResponded() {
		//Checks to see if all guild members with the D&D role have reacted to at least once on any day
		ArrayList<User> uniqueRespondants = new ArrayList<User>();
		
		for(ArrayList<User> list : pollResponses) {
			for(User user : list) {
				if(!uniqueRespondants.contains(user)) {
					uniqueRespondants.add(user);
				}
			}
		}
		
		boolean result = false;
		if(uniqueRespondants.size() == userList.size() ) {
			result = true;
		}
		
		return result;
	}

	public ArrayList<User> getNonResponders() {
		ArrayList<User> nonResponders = userList;
		
		for(ArrayList<User> list : pollResponses) {
			for(User user : list) {
				if(nonResponders.contains(user)) {
					nonResponders.remove(user);
				}
			}
		}
		
		return nonResponders;
	}

	public boolean hasAgreedDay() {
		Boolean result = false;
		
		for(ArrayList<User> day : pollResponses) {
			if(day.size() == userList.size() ) {
				result = true;
			}
		}
		
		return result;
	}

	public ArrayList<String> getAvailableDays() {
		ArrayList<String> result = new ArrayList<String>();
		
		int i = 0;
		for(ArrayList<User> day : pollResponses) {
			if(day.size() == userList.size() ) {
				switch(i) {				
					case 0:
						result.add("Saturday");
						break;
						
					case 1:
						result.add("Sunday");
						break;
						
					case 2: 
						result.add("Monday");
						
					case 3: 
						result.add("Tuesday");
						break;
						
					case 4:
						result.add("Wednesday");
						break;
						
					case 5:
						result.add("Thursday");
						break;
						
					case 6:
						result.add("Friday");
						break;
						
					default:
						result.add("No D&D this week");
				}
			}
			i++;
		}
		
		return result;
	}
	
	
}
