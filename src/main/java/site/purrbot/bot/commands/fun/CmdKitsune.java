/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

@CommandDescription(
        name = "Kitsune",
        description = "Gives you a image of a kitsune (foxgirl)",
        triggers = {"kitsune", "foxgirl"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = 
                        "{p}kitsune"
                )
        }
)
public class CmdKitsune implements Command{

    private PurrBot bot;

    public CmdKitsune(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        String link = bot.getHttpUtil().getImage(API.IMG_KITSUNE);

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder kitsune = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Kitsune %s",
                        Emotes.ANIM_SHIROTAILWAG.getEmote()
                ), link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a cute kitsune...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(
                EmbedBuilder.ZERO_WIDTH_SPACE
        ).embed(kitsune.build()).queue());
    }
}
