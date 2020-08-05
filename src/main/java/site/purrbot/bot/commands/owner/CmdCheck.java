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

package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

@CommandDescription(
        name = "Check",
        description =
                "Checks the Shards and prints their status.",
        triggers = {"check"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}check"),
                @CommandAttribute(key = "help", value = "{p}check")
        }
)
public class CmdCheck implements Command{
    
    private final PurrBot bot;
    
    public CmdCheck(PurrBot bot){
        this.bot = bot;
    }
    
    private void checkShards(Message msg){
        StringBuilder builder = new StringBuilder(msg.getContentRaw());
        int shards = 0;
        for(JDA shard : bot.getShardManager().getShardCache()){
            builder.append("\n").append(String.format(
                    "Shard %s [Status: %s]",
                    shard.getShardInfo().getShardId(),
                    shard.getStatus().name()
            ));
            msg.editMessage(builder.toString()).queue();
            shards++;
            
            if(shards == shard.getShardInfo().getShardTotal()){
                String message = builder.toString().replace(String.format(
                        "%s Performing Status checks on Shards. Please wait...",
                        Emotes.TYPING.getEmote()
                ),String.format(
                        "%s Status Check complete!",
                        Emotes.ACCEPT.getEmote()
                ));
                
                msg.editMessage(message).queue();
            }
        }
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        tc.sendMessage(String.format(
                "%s Performing Status checks on Shards. Please wait...",
                Emotes.TYPING.getEmote()
        )).queue(this::checkShards);
    }
}