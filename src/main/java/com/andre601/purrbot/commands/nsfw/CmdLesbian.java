package com.andre601.purrbot.commands.nsfw;

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
        name = "Lesbian",
        description = "Gives you a gif of lesbians",
        triggers = {"lesbian", "les"},
        attributes = {
                @CommandAttribute(key = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}lesbian")
        }
)
public class CmdLesbian implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        String link = HttpUtil.getImage(API.GIF_LES_LEWD, 0);

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder les = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle("Lesbian O//w//O", link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting hot lesbians...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(
                EmbedBuilder.ZERO_WIDTH_SPACE
        ).embed(les.build()).queue());
    }
}
