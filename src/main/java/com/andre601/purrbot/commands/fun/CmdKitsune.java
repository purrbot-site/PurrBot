package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.API;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandDescription(
        name = "Kitsune",
        description = "Gives you a image of a kitsune (foxgirl)",
        triggers = {"kitsune", "foxgirl"},
        attributes = {
                @CommandAttribute(key = "fun"),
                @CommandAttribute(key = "usage", value = "{p}kitsune")
        }
)
public class CmdKitsune implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        String link = HttpUtil.getImage(API.IMG_KITSUNE, 0);

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder gecg = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle("Kitsune UwU", link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a cute kitsune...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(
                EmbedBuilder.ZERO_WIDTH_SPACE
        ).embed(gecg.build()).queue());
    }
}
