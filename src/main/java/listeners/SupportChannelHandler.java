package listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.STATIC;

import java.util.HashMap;

public class SupportChannelHandler extends ListenerAdapter {

    private static HashMap<TextChannel, String> active = new HashMap<>();

    public void onMessageReceived(MessageReceivedEvent e){
        HashMap<TextChannel, Guild> supportchan = commands.CmdSupport.getTextChannels();
        Guild g = e.getGuild();
        TextChannel tc = e.getTextChannel();

        if(supportchan.containsKey(tc)){
            if (e.getAuthor().getId() != e.getJDA().getSelfUser().getId()){
                TextChannel ntc = (TextChannel)g.getController().createTextChannel(
                        tc.getName() + " [" + e.getAuthor().getId() + "]")
                        .complete();
                if(tc.getParent() != null)
                    ntc.getManager().setParent(tc.getParent());

                g.getController().modifyTextChannelPositions().selectPosition(ntc).moveTo(tc.getPosition() + 1).queue();
                active.put(tc, e.getAuthor().getId());

                String msg = e.getMessage().getContentRaw();

                e.getMessage().delete().queue();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor(ntc.getName(), STATIC.URL, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
                eb.addField("Message", msg, false);
                eb.addField("Closing Ticket", "To close the ticket, type `" + STATIC.PREFIX +
                                "Close` in this channel.",
                        false);

                ntc.sendMessage(eb.build()).queue();

            }
        }
    }

    public void onTextChannelDelete(TextChannelDeleteEvent e){
        TextChannel tc = e.getChannel();
        HashMap<TextChannel, Guild> supportchannel = commands.CmdSupport.getTextChannels();

        if(active.containsKey(tc))
            active.remove(tc);

        if(supportchannel.containsKey(tc)){
            commands.CmdSupport.unsetChat(e.getChannel());
        }
    }

    public static HashMap<TextChannel, String> getActiveChannels(){
        return active;
    }
}
