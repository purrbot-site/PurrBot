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

package site.purrbot.bot.commands.nsfw;

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
        name = "Lewd",
        description =
        "Get images of naughty nekos. >w<\n" +
        "\n" +
        "Use `--gif` to get a gif of a lewd neko.",
        triggers = {"lewd", "lneko"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value =
                        "{p}lewd [--gif]"
                )
        }
)
public class CmdLewd implements Command{

    private PurrBot bot;

    public CmdLewd(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
        
        if(args.toLowerCase().contains("--gif") || args.toLowerCase().contains("—gif")){
            String gifLink = bot.getHttpUtil().getImage(API.GIF_NEKO_LEWD);
            if(gifLink == null){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
                return;
            }

            EmbedBuilder lewdgif = bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                    .setTitle(String.format(
                            "Lewd Neko %s",
                            Emotes.WAGTAIL.getEmote()
                    ), gifLink)
                    .setImage(gifLink);

            tc.sendMessage(String.format(
                    "%s Getting a lewd neko-gif...",
                    Emotes.LOADING.getEmote()
            )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(lewdgif.build()).queue());
            return;
        }

        String link = bot.getHttpUtil().getImage(API.IMG_NEKO_LEWD);
        if(link == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder lewd = bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                .setTitle(String.format(
                        "Lewd Neko %s",
                        Emotes.NEKOWO.getEmote()
                ), link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a lewd neko...",
                Emotes.LOADING.getEmote()
        )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(lewd.build()).queue());
    }
}
