/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

@CommandDescription(
        name = "News",
        description = "Posts news to Discordservices.net",
        triggers = {"news"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}news <normal|error> <title> <message>"),
                @CommandAttribute(key = "help", value = "{p}news <normal|error> <title> <message>")
        }
)
public class CmdNews implements Command{
    
    private final PurrBot bot;
    
    public CmdNews(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(args.length < 3){
            MessageEmbed embed = bot.getEmbedUtil()
                    .getEmbed(member)
                    .setColor(0xFF0000)
                    .setDescription(String.format(
                            "**Not enough arguments!**\n" +
                            "**Usage**: `%snews <normal|error> <title> <message>`\n" +
                            "\n" +
                            "Note that `<title>` can't have spaces. You need to use `_`",
                            bot.getPrefix(guild.getId())
                    ))
                    .build();
            
            tc.sendMessage(embed).queue();
            return;
        }
        
        boolean error;
        if(args[0].equalsIgnoreCase("error")){
            error = true;
        }else
        if(args[0].equalsIgnoreCase("normal")){
            error = false;
        }else{
            MessageEmbed embed = bot.getEmbedUtil()
                    .getEmbed(member)
                    .setColor(0xFF0000)
                    .setDescription(
                            "**Invalid News Type!**\n" +
                            "The Type has to be either `normal` or `error`."
                    )
                    .build();
    
            tc.sendMessage(embed).queue();
            return;
        }
        
        bot.postNews(error, args[1], args[2]);
        msg.addReaction("\uD83D\uDC4D").queue();
    }
}
