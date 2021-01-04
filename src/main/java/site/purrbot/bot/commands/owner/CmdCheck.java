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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        tc.sendMessage(String.format(
                "%s Performing Status checks on Shards. Please wait...",
                Emotes.TYPING.getEmote()
        )).queue(this::checkShards);
    }
    
    private void checkShards(Message msg){
        List<JDA> shards = new ArrayList<>(bot.getShardManager().getShards());
        StringBuilder builder = new StringBuilder("```");
        int checkedShards = 0;
    
        shards.sort(Comparator.comparing(jda -> jda.getShardInfo().getShardId()));
        
        for(JDA shard : shards){
            if(builder.length() + getShardStatus(shard).length() > Message.MAX_CONTENT_LENGTH){
                builder.append("\n```");
                msg.getTextChannel().sendMessage(builder.toString()).queue();
                builder = new StringBuilder("```");
            }
            
            builder.append(getShardStatus(shard));
            checkedShards++;
            
            if(checkedShards == shards.size())
                msg.editMessage(String.format("%s Status Check complete!", Emotes.ACCEPT.getEmote())).queue();
            
        }
        
        if(builder.length() > 0){
            String text = builder.toString();
            if(!text.endsWith("```"))
                text += "\n```";
            msg.getTextChannel().sendMessage(text).queue();
        }
    }
    
    private String getShardStatus(JDA jda){
        return String.format("\nShard %2d [Status: %s]", jda.getShardInfo().getShardId(), jda.getStatus().name());
    }
}
