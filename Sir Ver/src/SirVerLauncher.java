package src;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;


/**
 * @author Jamie Chisholm
 */
public class SirVerLauncher {

	public static void main(String[] args) {
		//Account information variables
		String botToken = "MzI2MTQ3ODQxMDYyMDc2NDE2.XUCL1A.fgsdXjRBaM2kPScHLI0NbLtzuOs";
		String clientID = "326147841062076416";
		
		JDA jda = null;
		
		jda = buildJDA(botToken);
		
		buildTimeController(jda);
	}
	
	/*
	 * Creates an asynch TimeControl object to trigger timed events
	 */
	
	/*
	 * Builds a JDA object based on account information
	 * Then creates an async event listener, and attaches it to the JDA object
	 */
	public static JDA buildJDA(String botToken) {
		JDA jda = null;			
		
		try{
			jda = JDABuilder.createDefault(botToken).addEventListeners(new MessageListener()).build();
		    jda.awaitReady(); //Blocking ensuring full loading of JDA
		    System.out.println("Finished Building JDA!");
		}
		catch (LoginException e){
			//Catches authentication errors
		    e.printStackTrace();
		}
		catch (InterruptedException e){
			//Catches interruptions to "jda.awaitReady" (likely caused by other threads)
		    e.printStackTrace();
		}
		
		return jda;
	}
	
	public static void buildTimeController(JDA jda) {
		//TO-DO: Fill Time Controller Stub
	}

}
