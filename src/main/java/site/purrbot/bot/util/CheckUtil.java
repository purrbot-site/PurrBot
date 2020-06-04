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

package site.purrbot.bot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;

public class CheckUtil{

    private final PurrBot bot;
    
    public CheckUtil(PurrBot bot){
        this.bot = bot;
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isDeveloper(Member member){
        return member.getId().equals(IDs.ANDRE_601);
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkPermission(TextChannel tc, Member member, Member target, Permission permission){
        if(target.equals(tc.getGuild().getSelfMember())){
            if(target.hasPermission(tc, permission)){
                return true;
            }else{
                if(permission.equals(Permission.MESSAGE_EMBED_LINKS))
                    tc.sendMessage(
                            bot.getMsg(tc.getGuild().getId(), "errors.missing_perms.self_channel")
                                    .replace("{channel}", tc.getAsMention())
                                    .replace("{permission}", permission.getName())
                    ).queue();
                else
                    bot.getEmbedUtil().sendPermError(tc, member, tc, permission, true);
                
                return false;
            }
        }else{
            if(target.hasPermission(tc, permission)){
                return true;
            }else{
                bot.getEmbedUtil().sendPermError(tc, member, tc, permission, false);
                return false;
            }
        }
    }
}
