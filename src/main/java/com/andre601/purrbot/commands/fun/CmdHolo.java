package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandDescription(
        name = "Holo",
        description = "Gives a image of Holo from \"Spice and wolf\"",
        triggers = {"holo", "spiceandwolf"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdHolo implements Command {

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        String link = HttpUtil.getImage("holo", "url");

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            EmbedUtil.error(msg, "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder holo = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "%s",
                        link.replace("https://cdn.nekos.life/holo/", "")
                ), link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a cute image of holo...",
                Emotes.LOADING
        )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(holo.build()).queue());
    }
}
