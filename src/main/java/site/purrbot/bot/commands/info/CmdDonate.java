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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Donate",
        description = "purr.info.donate.description",
        triggers = {"donate", "donation", "donations", "donator"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}donate [--dm]"),
                @CommandAttribute(key = "help", value = "{p}donate [--dm]")
        }
)
public class CmdDonate implements Command{
    
    private final PurrBot bot;
    
    public CmdDonate(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                .setDescription(bot.getMsg(guild.getId(), "purr.info.donate.embed.description"))
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.donate.embed.donators_title"),
                        bot.getMsg(guild.getId(), "purr.info.donate.embed.donators_value"),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        getDonators(),
                        false
                )
                .build();
        
        if(bot.getMessageUtil().hasArg("dm", args)){
            member.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(embed))
                    .queue(
                            message -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.donate.dm_success", member.getAsMention())
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS)),
                            error -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.donate.dm_failure", member.getAsMention())
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    );
            
            return;
        }
        
        tc.sendMessage(embed).queue();
    }
    
    private String getDonators(){
        List<String> ids = bot.getDonators();
        
        return ids.stream().map(userId -> bot.getShardManager().getUserById(userId))
                .filter(Objects::nonNull)
                .map(user -> String.format("%s (%s)", user.getAsTag(), user.getAsMention()))
                .collect(Collectors.joining("\n"));
    }
}
