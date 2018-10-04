package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Emote",
        description = "Get info about a emote (custom emoji)",
        triggers = {"emote", "e"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdEmote implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(msg.getEmotes().isEmpty()){
            EmbedUtil.error(msg, "You need to provide an Emote!");
            return;
        }

        Emote emote = msg.getEmotes().get(0);

        EmbedBuilder emoteInfo = EmbedUtil.getEmbed(msg.getAuthor())
                .addField("Name:", MessageFormat.format(
                        "`:{0}:`",
                        emote.getName()
                ), true)
                .addField("ID:", MessageFormat.format(
                        "`{0}`",
                        emote.getId()
                ), true)
                .addField("Guild", MessageFormat.format(
                        "{0}",
                        (emote.getGuild() == null ? "`Unknown`" :
                        "`" + emote.getGuild().getName() + "` (`" + emote.getGuild().getId() + "`)")
                ), false)
                .addField("Image:", MessageFormat.format(
                        "[`Link`]({0})",
                        emote.getImageUrl()
                ), false)
                .setImage(emote.getImageUrl());

        tc.sendMessage(emoteInfo.build()).queue();
    }
}
