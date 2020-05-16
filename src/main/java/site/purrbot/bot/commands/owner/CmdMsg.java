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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandDescription(
        name = "Message",
        description = "Sends a message as the bot to the specified channel",
        triggers = {"msg", "message", "send"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}msg <channelId> <message>"),
                @CommandAttribute(key = "help", value = "{p}msg <channelId> <message>")
        }
)
public class CmdMsg implements Command{
    
    private final Pattern imagePattern;
    private final Pattern textPattern;
    
    private final PurrBot bot;

    public CmdMsg(PurrBot bot){
        this.bot = bot;
        
        this.imagePattern = Pattern.compile("--img=\"(?<image>http(?:s)?://.+\\.(?:png|jpg|gif))\"");
        this.textPattern = Pattern.compile("--msg=\"(?<message>.+)\"", Pattern.DOTALL | Pattern.MULTILINE);
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        Pattern text = Pattern.compile(
                Pattern.quote(bot.getPrefix(guild.getId())) + "(?:msg|message|send) (?<id>\\d+) (?<msg>.+)", 
                Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = text.matcher(msg.getContentRaw());
        if(!matcher.matches())
            return;
        
        String id = matcher.group("id");
        String message = matcher.group("msg");
    
        TextChannel channel = bot.getShardManager().getTextChannelById(id);
        if(channel == null)
            return;

        if(msg.getContentRaw().contains("--embed")){
            Matcher imageMatcher = imagePattern.matcher(message);
            Matcher textMatcher = textPattern.matcher(message);
    
            EmbedBuilder embed = new EmbedBuilder().setColor(0x802F3136);
            
            if(textMatcher.find())
                embed.setDescription(textMatcher.group("message"));
            
            if(imageMatcher.find())
                embed.setImage(imageMatcher.group("image"));
            
            channel.sendMessage(embed.build()).queue(
                    mes -> msg.addReaction("\u2705").queue()
            );
            
            return;
        }
        
        channel.sendMessage(message).queue(
                mes -> msg.addReaction("\u2705").queue()
        );
    }
}
