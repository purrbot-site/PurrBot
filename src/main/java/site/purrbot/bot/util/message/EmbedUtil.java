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

package site.purrbot.bot.util.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import site.purrbot.bot.PurrBot;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

public class EmbedUtil {
    
    private PurrBot bot;
    
    public EmbedUtil(PurrBot bot){
        this.bot = bot;
    }
    
    public EmbedBuilder getEmbed(){
        return new EmbedBuilder().setColor(0x36393F).setTimestamp(ZonedDateTime.now());
    }

    public EmbedBuilder getEmbed(User user, Guild guild){
        return getEmbed().setFooter(
                bot.getMsg(guild.getId(), "embed.footer", user.getAsTag()), 
                user.getEffectiveAvatarUrl()
        );
    }
    
    public void sendError(TextChannel tc, @Nullable User user, String path, @Nullable String reason){
        EmbedBuilder errorEmbed = user == null ? getEmbed() : getEmbed(user, tc.getGuild());

        errorEmbed.setColor(0xFF0000)
                .setDescription(bot.getMsg(tc.getGuild().getId(), path));

        if(reason != null)
            errorEmbed.addField(
                    "Error:",
                    reason,
                    false
            );

        tc.sendMessage(errorEmbed.build()).queue();
    }

    public void sendError(TextChannel tc, User user, String path){
        sendError(tc, user, path, null);
    }

}
