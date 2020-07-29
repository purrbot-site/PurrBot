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

package site.purrbot.bot.commands.nsfw;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

@CommandDescription(
        name = "Cum",
        description = "Cum from a lot of pleasure.",
        triggers = {"cum", "cumming"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value =
                        "{p}cum"
                ),
                @CommandAttribute(key = "help", value = "{p}cum")
        }
)
public class CmdCum implements Command{
    
    private final PurrBot bot;
    
    public CmdCum(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
        
        String link = bot.getHttpUtil().getImage(API.GIF_CUM_LEWD);
        if(link == null){
            bot.getEmbedUtil().sendError(tc, member, "errors.api_error");
            return;
        }
    
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setDescription(
                        bot.getMsg(guild.getId(), "purr.nsfw.cum.message", member.getEffectiveName())
                )
                .setImage(link)
                .build();
        
        tc.sendMessage(bot.getMsg(guild.getId(), "purr.nsfw.cum.loading")).queue(message -> 
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(embed).queue()
        );
    }
}
