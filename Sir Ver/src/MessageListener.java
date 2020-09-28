package src;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter{
	/*
	 * Author: Jamie Chisholm
	 * 
     * For the @Override
     * This method is overriding a method in the parent ListenerAdapter class. The override 
     * guarantees that this method will be used over the parent method. 
     *
     * Method being overridden:
     * {@link net.dv8tion.jda.core.hooks.ListenerAdapter ListenerAdapter}
     *
     * @param event
     *          An event containing information about a {@link net.dv8tion.jda.core.entities.Message Message} that was
     *          sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
    	//General event information
        JDA jda = event.getJDA();                       //JDA api core

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            String textChannel = event.getTextChannel().getName(); //The TextChannel that this message was sent to                     
            
            if(textChannel.equals("bot-commands")) {
            	//Create a Command object
            	Command command = new Command(event);
            	command.execute();
            }
            else if(textChannel.equals("welcome")) {
            	//TO-DO: Auto Permissioner stub
            }
            else {
            	//TO-DO: Reddit image scraper
            }
        }
    }
}
