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
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

@CommandDescription(
        name = "Emote",
        description = "purr.info.emote.description",
        triggers = {"emote", "e"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}emote <:emote:>\n" +
                        "{p}emote <--search>"
                ),
                @CommandAttribute(key = "help", value = "{p}emote <:emote:|--search>")
        }
)
public class CmdEmote implements Command{
    
    private final PurrBot bot;
    
    public CmdEmote(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(bot.getMessageUtil().hasArg("search", args)){
            if(!guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                bot.getEmbedUtil().sendPermError(tc, member, Permission.MESSAGE_HISTORY, true);
                return;
            }
            
            EmbedPaginator.Builder builder = new EmbedPaginator.Builder().setEventWaiter(bot.getWaiter())
                    .setTimeout(1, TimeUnit.MINUTES)
                    .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                    .waitOnSinglePage(true)
                    .addUsers(member.getUser())
                    .setFinalAction(message -> {
                        if(guild.getSelfMember().hasPermission(message.getGuildChannel(), Permission.MESSAGE_MANAGE))
                            message.clearReactions().queue(null, ignore(UNKNOWN_MESSAGE));
                    });
            
            if(guild.getOwner() != null)
                builder.addUsers(guild.getOwner().getUser());
            
            tc.getIterableHistory()
                    .cache(false)
                    .takeAsync(100)
                    .thenApply(List::stream)
                    .thenApply(stream -> stream.filter(message -> !message.getMentions().getCustomEmojis().isEmpty()))
                    .thenApply(stream -> stream.collect(Collectors.toMap(
                            Message::getJumpUrl,
                            m -> m.getMentions().getCustomEmojis(),
                            (existing, replacement) -> existing,
                            LinkedHashMap::new
                    )))
                    .thenAccept(emotes -> send(builder, tc, member, emotes));
            return;
        }
        
        if(msg.getMentions().getCustomEmojis().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.info.emote.no_args");
            return;
        }
        
        tc.sendMessageEmbeds(emoteInfo(member, msg.getMentions().getCustomEmojis().get(0), guild, null, 1, 1)).queue();
        
    }
    
    private void send(EmbedPaginator.Builder builder, TextChannel tc, Member member, Map<String, List<CustomEmoji>> emotes){
        int pos = 0;
        
        int size = emotes.values().stream()
                .mapToInt(List::size)
                .sum();
        
        for(Map.Entry<String, List<CustomEmoji>> entry : emotes.entrySet()){
            for(CustomEmoji emote : entry.getValue()){
                pos++;
                builder.addItems(emoteInfo(
                        member,
                        emote,
                        member.getGuild(),
                        entry.getKey(),
                        pos,
                        size
                ));
            }
        }
        
        builder.build().display(tc);
    }
    
    private MessageEmbed emoteInfo(Member member, CustomEmoji emote, Guild guild, @Nullable String link, int pos, int size){
        RichCustomEmoji e = bot.getShardManager().getEmojiById(emote.getId());
        Guild emoteGuild = null;
        if(e != null)
            emoteGuild = e.getGuild();
        
        String path = size > 1 ? "purr.info.emote.embed.emote_multiple" : "purr.info.emote.embed.emote_single";
        
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(member)
                .setTitle(
                    bot.getMsg(guild.getId(), path)
                        .replace("{current}", String.valueOf(pos))
                        .replace("{total}", String.valueOf(size))
                )
                .addField(
                    bot.getMsg(guild.getId(), "purr.info.emote.embed.name"),
                    String.format(
                        "`:%s:`",
                        emote.getName()
                    ),
                    true
                )
                .addField(
                    bot.getMsg(guild.getId(), "purr.info.emote.embed.id"),
                    String.format(
                        "`%s`",
                        emote.getId()
                    ),
                    true
                )
                .addField(
                    bot.getMsg(guild.getId(), "purr.info.emote.embed.guild"),
                    String.format(
                        "`%s`", 
                        emoteGuild != null ? emoteGuild.getName() : bot.getMsg(guild.getId(), "purr.info.emote.unknown_guild")
                    ),
                    true
                )
                .addField(
                    bot.getMsg(guild.getId(), "purr.info.emote.embed.image"),
                    bot.getMsg(guild.getId(), "purr.info.emote.embed.link").replace("{link}", emote.getImageUrl()),
                    true
                )
                .setThumbnail(emote.getImageUrl());
        
        if(link != null)
            embed.addField(
                bot.getMsg(guild.getId(), "purr.info.emote.embed.message"),
                bot.getMsg(guild.getId(), "purr.info.emote.embed.link")
                    .replace("{link}", link),
                true
            );
        
        return embed.build();
    }
}
