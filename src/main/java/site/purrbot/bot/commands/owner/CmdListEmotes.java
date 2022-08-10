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
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CommandDescription(
        name = "Listemotes",
        description = "Lists all emotes the bot uses",
        triggers = {"listemotes", "le"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}listemotes"),
                @CommandAttribute(key = "help", value = "{p}listemotes")
        }
)
public class CmdListEmotes implements Command{
    
    private final PurrBot bot;
    
    public CmdListEmotes(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(args.length == 0){
            msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue();
            return;
        }
        
        Guild targetGuild = bot.getShardManager().getGuildById(args[0]);
        if(targetGuild == null){
            msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue();
            return;
        }
    
        List<RichCustomEmoji> emotes = new ArrayList<>(targetGuild.getEmojis());
        emotes.sort(Comparator.comparing(RichCustomEmoji::getName));
        
        StringBuilder builder = new StringBuilder(String.format(
                "Emotes for Guild %s (%d emotes)\n" +
                "\n",
                targetGuild.getName(),
                emotes.size()
        ));
        int i = 0;
        for(RichCustomEmoji emote : emotes){
            if(emote.isAnimated())
                continue;
            
            i++;
            if(builder.length() + getEmoteString(emote).length() + 100 > Message.MAX_CONTENT_LENGTH){
                tc.sendMessageFormat(
                        "```\n" +
                        "%s\n" +
                        "```",
                        builder.toString()
                ).queue();
                
                builder.setLength(0);
            }
            
            if(builder.length() > 0)
                builder.append("\n");
            
            builder.append(getEmoteString(emote));
            
            if(i % 10 == 0)
                builder.append("\n");
        }
        
        tc.sendMessageFormat(
                "```\n" +
                "%s\n" +
                "```",
                builder.toString()
        ).queue();
    }
    
    private String getEmoteString(RichCustomEmoji emote){
        return String.format(
                "%s `:%s: - %s`",
                emote.getAsMention(),
                emote.getName(),
                emote.getAsMention()
        );
    }
}
