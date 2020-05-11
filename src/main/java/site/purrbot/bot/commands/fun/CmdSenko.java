/*
 * Copyright 2018 - 2020 Andre601
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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

@CommandDescription(
        name = "Senko",
        description = "Gets an image of Senko from the anime 'Sewayaki Kitsune no Senko-san'.",
        triggers = {"senko", "senko-san"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}senko"),
                @CommandAttribute(key = "help", value = "{p}senko")
        }
)
public class CmdSenko implements Command{
    
    private final PurrBot bot;
    
    public CmdSenko(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        Guild guild = msg.getGuild();
        String link = bot.getHttpUtil().getImage(API.IMG_SENKO);
    
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
    
        if(link == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "errors.api_error");
            return;
        }
    
        EmbedBuilder senko = bot.getEmbedUtil().getEmbed(msg.getAuthor(), guild)
                .setTitle(bot.getMsg(guild.getId(), "purr.fun.senko.title"), link)
                .setImage(link);
    
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.fun.senko.loading")
        ).queue(message -> message.editMessage(
                EmbedBuilder.ZERO_WIDTH_SPACE
        ).embed(senko.build()).queue());
    }
}
