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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

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
    
    private final PurrBot bot;

    public CmdMsg(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        if(args.length <= 1){
            msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue();
            return;
        }
        
        TextChannel channel = bot.getShardManager().getTextChannelById(args[0]);
        if(channel == null){
            msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue();
            return;
        }
        
        if(args[1].equalsIgnoreCase("msg")){
            String content = args[2].replaceAll("\\{#(\\d+)}", "<#$1>");
            
            channel.sendMessage(content).queue(
                    message -> msg.addReaction(Emoji.fromFormatted(Emotes.ACCEPT.getEmote())).queue(),
                    failure -> msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue()
            );
        }else
        if(args[1].equalsIgnoreCase("edit")){
            String msgId = args[2].split("\\s+")[0];
            
            Message message = channel.retrieveMessageById(msgId).complete();
            if(message == null || !message.getAuthor().equals(guild.getSelfMember().getUser())){
                tc.sendMessage("Invalid message! It was either null or I'm not the author of it.").queue(
                        m -> msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue()
                );
                return;
            }
            
            String content = args[2].substring(msgId.length() + 1).replaceAll("\\{#(\\d+)}", "<#$1>");
            message.editMessage(content).queue(
                    m -> msg.addReaction(Emoji.fromFormatted(Emotes.ACCEPT.getEmote())).queue(),
                    failure -> msg.addReaction(Emoji.fromFormatted(Emotes.DENY.getEmote())).queue());
        }else
        if(args[1].equalsIgnoreCase("embed")){
            String content = args[2].replaceAll("\\{#(\\d+)}", "<#$1>");
            
            MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                    .setTimestamp(null)
                    .setDescription(content)
                    .build();
            
            channel.sendMessageEmbeds(embed).queue();
        }else{
            tc.sendMessage("Invalid message type! Allowed are `msg`, `edit` and `embed`").queue();
        }
    }
}
