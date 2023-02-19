/*
 *  Copyright 2018 - 2022 Andre601
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

package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.List;
import java.util.Locale;

@CommandDescription(
        name = "Prefix",
        description = "purr.guild.prefix.description",
        triggers = {"prefix"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}prefix set <prefix>\n" +
                        "{p}prefix reset"
                ),
                @CommandAttribute(key = "help", value = "{p}prefix <set <prefix>|reset>")
        }
)
public class CmdPrefix implements Command{

    private final PurrBot bot;

    public CmdPrefix(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, List<Member> members, String... args){
        if(args.length < 1){
            bot.getEmbedUtil().sendError(tc, member, "purr.guild.prefix.few_args");
            return;
        }

        if(args[0].equalsIgnoreCase("reset")){
            resetPrefix(tc, member);
        }else
        if(args[0].equalsIgnoreCase("set")){
            if(args.length == 1){
                bot.getEmbedUtil().sendError(tc, member, "purr.guild.prefix.no_prefix");
            }else{
                setPrefix(tc, member, args[1].toLowerCase(Locale.ROOT));
            }
        }else{
            bot.getEmbedUtil().sendError(tc, member, "purr.guild.prefix.invalid_args");
        }
    }
    
    private void setPrefix(TextChannel tc, Member member, String prefix){
        bot.setPrefix(member.getGuild().getId(), prefix);
        
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                .setColor(0x00FF00)
                .setDescription(
                        bot.getMsg(member.getGuild().getId(), "purr.guild.prefix.set")
                )
                .build();
        
        tc.sendMessageEmbeds(embed).queue();
    }
    
    private void resetPrefix(TextChannel tc, Member member){
        bot.setPrefix(member.getGuild().getId(), bot.isBeta() ? "p.." : "p.");
        
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                .setColor(0x00FF00)
                .setDescription(
                        bot.getMsg(member.getGuild().getId(), "purr.guild.prefix.reset")
                )
                .build();
        
        tc.sendMessageEmbeds(embed).queue();
    }
}
