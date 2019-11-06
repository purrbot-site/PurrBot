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
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

@CommandDescription(
        name = "Neko",
        description =
                "Gives you a lovely neko (catgirl)\n" +
                "\n" +
                "You can use additional args in the command.\n" +
                "`--gif` for a gif\n" +
                "`--slide` for a slideshow with 30 images\n" +
                "Both arguments can be combined.",
        triggers = {"neko", "catgirl"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = 
                        "{p}neko\n" +
                        "{p}neko --gif\n"
                )
        }
)
public class CmdNeko implements Command{

    private PurrBot bot;

    public CmdNeko(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--gif")){
            String link = bot.getHttpUtil().getImage(API.GIF_NEKO);
            if(link == null){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
                return;
            }
            EmbedBuilder nekogif = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                    .setTitle(String.format(
                            "Neko %s",
                            Emotes.ANIM_WAGTAIL.getEmote()
                    ), link)
                    .setImage(link);

            tc.sendMessage(String.format(
                    "%s Getting a cute neko-gif...",
                    Emotes.ANIM_LOADING.getEmote()
            )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(nekogif.build()).queue());
            return;
        }

        String link = bot.getHttpUtil().getImage(API.IMG_NEKO);

        if(link == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        tc.sendMessage(String.format(
                "%s Getting a cute neko...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            EmbedBuilder neko = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                    .setTitle(String.format(
                            "Neko %s",
                            Emotes.NEKOWO.getEmote()
                    ), link)
                    .setImage(link);

            if(link.equals("https://cdn.nekos.life/v3/sfw/img/neko/neko_079.jpg")){
                if(bot.isBeta()){
                    neko.setDescription("That is me! >w<");
                    message.addReaction("❤").queue();
                }else{
                    neko.setDescription("That is my little sister!");

                    if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_EXT_EMOJI))
                        message.addReaction(Emotes.SNUGGLE.getNameAndId()).queue();
                }
            }else
            if(link.equals("https://cdn.nekos.life/v3/sfw/img/neko/neko_139.png")){
                if(bot.isBeta()){
                    neko.setDescription("That is my big sister!");

                    if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_EXT_EMOJI))
                        message.addReaction(Emotes.PURR.getNameAndId()).queue();
                }else{
                    neko.setDescription("T-that is me! OwO");
                    message.addReaction("❤").queue();
                }
            }

            //  Editing the message to add the image ("should" prevent issues with empty embeds)
            message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(neko.build()).queue();
        });
    }
}
