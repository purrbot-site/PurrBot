package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.andre601.util.PermUtil;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdQuote implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        Guild g = e.getGuild();
        Message message;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length <= 1){
            tc.sendMessage(MessageFormat.format(
                    "{0} To few arguments! You need to provide a message and channel-id!\n" +
                    "The syntax is `{1}quote <messageID> <channelID>`",
                    msg.getAuthor().getAsMention(),
                    CmdPrefix.getPrefix(g)
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        if(args[0].matches("[0-9]{18,22}") && args[1].matches("[0-9]{18,22}")){
            try{
                //  Try to get the Message.
                message = g.getTextChannelById(args[1].trim()).getMessageById(args[0].trim()).complete();
            }catch (Exception ex){
                //  Set message to null.
                message = null;
            }

            //  If message is null -> Send error-message and return.
            if(message == null){
                tc.sendMessage(MessageFormat.format(
                        "{0} Unable to find message! Are the IDs correct?",
                        msg.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            //  Get the raw content of the message (Above check makes sure, that message exists)
            String quoteMsg = message.getContentRaw();

            //  Creating embed for quote.
            EmbedBuilder quote = EmbedUtil.getEmbed()
                    .setAuthor(MessageFormat.format(
                            "Quote from {0}",
                            MessageUtil.getTag(message.getAuthor())
                    ), null, message.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(quoteMsg)
                    .setFooter(MessageFormat.format(
                            "Posted in #{0}",
                            g.getTextChannelById(args[1].trim()).getName()
                    ),null)
                    .setTimestamp(message.getCreationTime());

            tc.sendMessage(quote.build()).queue();
        }else{
            tc.sendMessage(MessageFormat.format(
                    "{0} Unable to find message! Are the IDs correct?",
                    msg.getAuthor().getAsMention()
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
