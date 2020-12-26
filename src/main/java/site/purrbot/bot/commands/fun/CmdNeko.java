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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.util.HttpUtil;

@CommandDescription(
        name = "Neko",
        description = "purr.fun.neko.description",
        triggers = {"neko", "catgirl"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}neko [--gif]"),
                @CommandAttribute(key = "help", value = "{p}neko [--gif]")
        }
)
public class CmdNeko implements Command, HttpUtil.ImageAPI{

    private final PurrBot bot;

    public CmdNeko(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        boolean isGif = bot.getMessageUtil().hasArg("gif", args);
        
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.fun.neko.loading")
        ).queue(message -> bot.getHttpUtil().handleRequest(this, member, message, isGif));
    }
    
    @Override
    public String getCategory(){
        return "fun";
    }
    
    @Override
    public String getEndpoint(){
        return "neko";
    }
    
    @Override
    public boolean isImgRequired(){
        return true;
    }
    
    @Override
    public boolean isNSFW(){
        return false;
    }
    
    @Override
    public boolean isRequest(){
        return false;
    }
}
