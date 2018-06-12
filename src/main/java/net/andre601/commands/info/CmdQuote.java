package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.andre601.util.PermUtil;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
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
        Guild g = e.getGuild();
        Message message;

        if(!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length == 1 && args[0].matches("[0-9]{18,22}")){

            tc.sendTyping().queue();
            try{
                //  Making a new Message to apply the messageById and later add this to the original one.
                message = tc.getMessageById(args[0].trim()).complete();

            //  We won't do anything here, because that would spam the console.
            }catch (Exception ignored){
                tc.sendMessage(String.format(
                        "%s Couldn't find the message. Is it even in the same channel?",
                        e.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            //  Getting the raw content of the message
            String messageRaw = message.getContentRaw();

            if(message == null){
                tc.sendMessage(String.format(
                        "%s Message is empty/null!",
                        msg.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            EmbedBuilder quote = EmbedUtil.getEmbed()
                    .setAuthor(String.format(
                            "Quote from %s",
                            MessageUtil.getTag(message.getAuthor())
                    ), null, message.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(String.format(
                            "%s",
                            messageRaw
                    ))
                    .setFooter("Posted:", null)
                    .setTimestamp(message.getCreationTime());
            tc.sendMessage(quote.build()).queue();
            /*
            try {
                e.getJDA().getTextChannels().parallelStream().forEach(textChannel -> {
                    textChannel.getMessageById(args[0].trim()).queue(message -> {
                        if (message != null) {
                            String messageRaw = message.getContentRaw();

                            EmbedBuilder quote = EmbedUtil.getEmbed()
                                    .setAuthor(String.format(
                                            "Quote from %s",
                                            MessageUtil.getTag(message.getAuthor())
                                    ), null, message.getAuthor().getEffectiveAvatarUrl())
                                    .setDescription(String.format(
                                            "%s",
                                            messageRaw
                                    ))
                                    .setFooter(String.format(
                                            "Posted in #%s at",
                                            message.getTextChannel().getName()
                                    ), null)
                                    .setTimestamp(message.getCreationTime());
                            tc.sendMessage(quote.build()).queue();
                        } else {
                            tc.sendMessage(String.format(
                                    "%s That is not a valid MessageID!",
                                    e.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                        }
                    }, failure -> {
                        System.out.println("Something went wrong while handling textChannel.getMessageById " +
                                "in CmdQuote.java");
                    });
                });
            }catch (Exception ignored){
                tc.sendMessage(String.format(
                        "%s You need to provide a valid MessageID!\n" +
                                "Example: `%squote %s`",
                        msg.getAuthor().getAsMention(),
                        CmdPrefix.getPrefix(g),
                        msg.getId()
                )).queue();
            }
            */
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
