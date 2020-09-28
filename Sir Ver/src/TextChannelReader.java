/**
 * 
 */
package src;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Member;

/**
 * @author Jamie Chisholm
 *
 */
public class TextChannelReader {
	protected User author;
	protected Message message;
	protected String msg;
	protected boolean bot;
	protected Guild guild;
	protected TextChannel textChannel;
	protected Member member;
	protected String name;
	protected boolean isBot;
	protected MessageReceivedEvent event;
	
	public MessageReceivedEvent getEvent() {
		return event;
	}
	
	public Boolean getIsBot() {
		return isBot;
	}
	
	public void setIsBot(Boolean isBot) {
		this.isBot = isBot;
	}
	
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isBot() {
		return bot;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public TextChannel getTextChannel() {
		return textChannel;
	}

	public void setTextChannel(TextChannel textChannel) {
		this.textChannel = textChannel;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TextChannelReader(MessageReceivedEvent event) {
        author = event.getAuthor();                
        message = event.getMessage();           
        msg = message.getContentDisplay();
        isBot = author.isBot();
        this.event = event;

        boolean bot = author.isBot();
		
        guild = event.getGuild();             
        textChannel = event.getTextChannel(); 
        member = event.getMember();          

        if (message.isWebhookMessage())
        {
            name = author.getName();                //If this is a Webhook message, then there is no Member associated
        }                                           // with the User, thus we default to the author for name.
        else
        {
            name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
        }                                           // otherwise it will default to their username. (User#getName())

        System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
	}
	
	
}
