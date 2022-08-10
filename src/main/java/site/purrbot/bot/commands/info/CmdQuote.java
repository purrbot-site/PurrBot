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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

@CommandDescription(
        name = "Quote",
        description = "purr.info.quote.description",
        triggers = {"quote"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}quote <messageId> [#channel]"),
                @CommandAttribute(key = "help", value = "{p}quote <messageId> [#channel]")
        }
)
public class CmdQuote implements Command{

    private final PurrBot bot;

    public CmdQuote(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(args.length == 0){
            bot.getEmbedUtil().sendError(tc, member, "purr.info.quote.few_args");
            return;
        }
        
        TextChannel targetChannel = msg.getMentions().getChannels(TextChannel.class).isEmpty() ? tc : msg.getMentions().getChannels(TextChannel.class).get(0);
        
        if(targetChannel.isNSFW() && !tc.isNSFW()){
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                .setColor(0xFF0000)
                .setDescription(
                    bot.getMsg(guild.getId(), "purr.info.quote.nsfw_channel")
                        .replace("{channel}", targetChannel.getAsMention())
                ).build();
            
            tc.sendMessageEmbeds(embed).queue();
            return;
        }
        
        if(!guild.getSelfMember().hasPermission(targetChannel, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY)){
            bot.getEmbedUtil().sendPermError(tc, member, targetChannel, Permission.MESSAGE_HISTORY, true);
            return;
        }
        
        targetChannel.retrieveMessageById(args[0]).queue(
            message -> {
                if(message.getAttachments().isEmpty() && message.getContentRaw().isEmpty()){
                    bot.getEmbedUtil().sendError(tc, member, "purr.info.quote.no_value");
                    return;
                }
                
                String memberName = getMemberName(guild.getId(), message.getMember());
                EmbedBuilder quote = bot.getEmbedUtil().getEmbed()
                    .setAuthor(
                        bot.getMsg(guild.getId(), "purr.info.quote.embed_basic.info", memberName, false),
                        null,
                        message.getAuthor().getEffectiveAvatarUrl()
                    )
                    .setDescription(message.getContentRaw())
                    .setFooter(
                        bot.getMsg(guild.getId(), "purr.info.quote.embed_basic.channel").replace("{channel}", targetChannel.getName())
                    )
                    .setTimestamp(message.getTimeCreated());
                
                if(!message.getAttachments().isEmpty()){
                    for(Message.Attachment attachment : message.getAttachments()){
                        if(!attachment.isImage())
                            continue;
                        
                        quote.setImage(attachment.getUrl());
                        break;
                    }
                }
                
                tc.sendMessageEmbeds(quote.build())
                  .setActionRow(Button.link(message.getJumpUrl(), bot.getMsg(guild.getId(), "purr.info.quote.button")))
                  .queue();
            },
            e -> {
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                    .setColor(0xFF0000)
                    .setDescription(
                        bot.getMsg(guild.getId(), "purr.info.quote.no_msg")
                            .replace("{channel}", targetChannel.getAsMention())
                    ).build();
                
                tc.sendMessageEmbeds(embed).queue();
            }
        );
    }
    
    private String getMemberName(String guildId, Member member){
        if(member == null)
            return bot.getMsg(guildId, "misc.unknown_user");
        
        return member.getEffectiveName();
    }
}
