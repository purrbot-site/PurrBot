package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.util.EmbedUtil;
import net.andre601.util.MessageUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
        Message message;
        Guild g = e.getGuild();

        if(PermUtil.canDeleteMsg(msg))
            msg.delete().queue();

        if(args.length == 1 && args[0].matches("[0-9]{18,22}")){
            tc.sendTyping().queue();
            try {
                Message quotemsg= tc.getMessageById(args[0].trim()).complete();
                message = quotemsg;
            //  We ignore this error here, or else the console/error-channel could get spammed
            }catch (Exception ignored){
                tc.sendMessage(String.format(
                        "%s That is not a valid MessageID!",
                        e.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            if(message == null){
                tc.sendMessage(String.format(
                        "%s That is not a valid MessageID!",
                        e.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            String messageRaw = message.getContentRaw();

            EmbedBuilder quote = EmbedUtil.getEmbed()
                    .setAuthor(String.format(
                            "Quote from %s",
                            MessageUtil.getTag(message.getAuthor())
                    ), null, message.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(String.format(
                            "`%s`\n" +
                                    "%s",
                            MessageUtil.formatTime(message.getCreationTime().toLocalDateTime()),
                            messageRaw
                    ));
            tc.sendMessage(quote.build()).queue();
        }else{
            tc.sendMessage(String.format(
                    "%s You need to provide a valid MessageID!\n" +
                    "Example: `%squote %s`",
                    msg.getAuthor().getAsMention(),
                    CmdPrefix.getPrefix(g),
                    msg.getId()
            )).queue();
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
