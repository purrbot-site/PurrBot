package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class CmdEmote implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        Message msg = e.getMessage();
        TextChannel tc = e.getTextChannel();

        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            e.getTextChannel().sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length > 0){
            if(!msg.getEmotes().isEmpty()){
                Emote emote = msg.getEmotes().get(0);

                EmbedBuilder emoteInfo = EmbedUtil.getEmbed(msg.getAuthor())
                        .setDescription(MessageFormat.format(
                                "**Name**: `:{0}:`\n" +
                                "**ID**: `{1}`\n" +
                                "**Guild**: {2}\n" +
                                "[**Link**]({3})\n" +
                                "\n" +
                                "**Image**:",
                                emote.getName(),
                                emote.getId(),
                                (emote.getGuild() != null ?
                                        "`" + emote.getGuild().getName() + "` (`" + emote.getGuild().getId() + "`)":
                                "`Unknown`"),
                                emote.getImageUrl()
                        ))
                        .setImage(emote.getImageUrl());

                tc.sendMessage(emoteInfo.build()).queue();
            }else{
                tc.sendMessage(MessageFormat.format(
                        "{0} You need to mention a emote after the command!",
                        msg.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        }else{
            tc.sendMessage(MessageFormat.format(
                    "{0} You need to mention a emote after the command!",
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
