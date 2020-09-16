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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private MessageEmbed emoteInfo(Member member, Emote emote, Guild guild, @Nullable String link, int pos, int size){
        Emote e = bot.getShardManager().getEmoteById(emote.getId());
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
                        bot.getMsg(guild.getId(), "purr.info.emote.embed.link")
                                .replace("{link}", emote.getImageUrl()),
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

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        String s = msg.getContentRaw();
        if(s.toLowerCase().contains("--search") || s.toLowerCase().contains("—search")){
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
                        if(guild.getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_MANAGE))
                            message.clearReactions().queue(null, ignore(UNKNOWN_MESSAGE));
                    });
            
            if(guild.getOwner() != null)
                builder.addUsers(guild.getOwner().getUser());
            
            Map<Emote, String> emotes = new LinkedHashMap<>();
            for(Message message : tc.getIterableHistory().limit(100).complete()){
                if(message.getEmotes().isEmpty()){
                    continue;
                }
                
                String link = message.getJumpUrl();
                for(Emote emote : message.getEmotes()){
                    emotes.put(emote, link);
                }
            }
            
            if(emotes.isEmpty()){
                bot.getEmbedUtil().sendError(tc, member, "purr.info.emote.not_found");
                return;
            }
            
            int size = emotes.size();
            int pos = 0;
            for(Map.Entry<Emote, String> info : emotes.entrySet()){
                pos++;
                builder.addItems(emoteInfo(
                        member,
                        info.getKey(),
                        guild,
                        info.getValue(),
                        pos,
                        size
                ));
            }
            
            builder.build().display(tc);
            
            return;
        }

        if(msg.getEmotes().isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.info.emote.no_args");
            return;
        }

        tc.sendMessage(emoteInfo(member, msg.getEmotes().get(0), guild, null, 1, 1)).queue();

    }
}
