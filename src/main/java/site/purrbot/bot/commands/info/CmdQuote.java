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
import net.dv8tion.jda.api.interactions.components.Button;
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

        if(msg.getMentionedChannels().isEmpty()){
            if(!guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                bot.getEmbedUtil().sendPermError(tc, member, Permission.MESSAGE_HISTORY, true);
                return;
            }

            Message quote = getMessage(args[0], tc);

            if(quote == null){
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                        .setColor(0xFF0000)
                        .setDescription(
                                bot.getMsg(guild.getId(), "purr.info.quote.no_msg")
                                        .replace("{channel}", tc.getAsMention())
                        )
                        .build();
                
                tc.sendMessageEmbeds(embed).queue();
                return;
            }
            
            sendMsg(tc, quote, member);
            return;
        }

        TextChannel channel = msg.getMentionedChannels().get(0);
        if(channel.isNSFW() && !tc.isNSFW()){
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                    .setColor(0xFF0000)
                    .setDescription(
                            bot.getMsg(guild.getId(), "purr.info.quote.nsfw_channel")
                                    .replace("{channel}", channel.getAsMention())
                    )
                    .build();
            
            tc.sendMessageEmbeds(embed).queue();
            return;
        }

        if(guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)){
            Message quote = getMessage(args[0], channel);

            if(quote == null){
                bot.getEmbedUtil().sendError(tc, member, "purr.info.quote.invalid_id");
                return;
            }
            
            sendMsg(tc, quote, member);
        }else{
            bot.getEmbedUtil().sendPermError(tc, member, channel, Permission.MESSAGE_HISTORY, true);
        }
    }
    
    private void sendMsg(TextChannel tc, Message quote, Member member){
        if(!quote.getAttachments().isEmpty()){
            String link = quote.getAttachments().stream()
                    .filter(Message.Attachment::isImage)
                    .findFirst()
                    .map(Message.Attachment::getUrl)
                    .orElse(null);
            
            if(link == null && quote.getContentRaw().isEmpty()){
                bot.getEmbedUtil().sendError(tc, member, "purr.info.quote.no_value");
                return;
            }
            
            sendQuoteEmbed(quote, link, tc);
        }else{
            byte[] bytes = bot.getImageUtil().getQuoteImg(quote);
            
            if(bytes == null){
                sendQuoteEmbed(quote, null, tc);
                return;
            }
            
            sendImgEmbed(member, quote, bytes, tc);
        }
    }
    
    private Message getMessage(String id, TextChannel channel){
        Message message;
        try{
            message = channel.retrieveMessageById(id).complete();
        }catch(Exception ex){
            message = null;
        }
        
        return message;
    }
    
    private String getMember(Guild guild, Member member){
        if(member == null)
            return bot.getMsg(guild.getId(), "misc.unknown_user");
        
        return member.getEffectiveName();
    }
    
    private void sendQuoteEmbed(Message quote, String link, TextChannel channel) {
        Guild guild = quote.getGuild();
        String name = getMember(guild, quote.getMember());
        
        EmbedBuilder quoteEmbed = bot.getEmbedUtil().getEmbed()
                .setAuthor(
                        bot.getMsg(guild.getId(), "purr.info.quote.embed_basic.info", name, false),
                        null,
                        quote.getAuthor().getEffectiveAvatarUrl()
                )
                .setDescription(quote.getContentRaw())
                .setImage(link)
                .setFooter(
                        bot.getMsg(guild.getId(), "purr.info.quote.embed_basic.channel")
                                .replace("{channel}", quote.getTextChannel().getName()),
                        null
                )
                .setTimestamp(quote.getTimeCreated());
        
        channel.sendMessageEmbeds(quoteEmbed.build())
               .setActionRow(Button.link(quote.getJumpUrl(), bot.getMsg(guild.getId(), "purr.info.quote.button")))
               .queue();
    }
    
    private void sendImgEmbed(Member member, Message quote, byte[] bytes, TextChannel channel){
        Guild guild = member.getGuild();
        String filename = String.format("quote_%s.png", quote.getId());
        String name = getMember(guild, quote.getMember());
        
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                .setDescription(
                        bot.getMsg(guild.getId(), "purr.info.quote.embed_image.info", name)
                                .replace("{channel}", quote.getTextChannel().getAsMention())
                                .replace("{link}", quote.getJumpUrl())
                )
                .setImage(String.format(
                        "attachment://%s",
                        filename
                ))
                .build();
        
        channel.sendMessageEmbeds(embed)
               .addFile(bytes, filename)
               .setActionRow(Button.link(quote.getJumpUrl(), bot.getMsg(guild.getId(), "purr.info.quote.button")))
               .queue();
    }
}
