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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import javax.annotation.Nullable;

@CommandDescription(
        name = "Emote",
        description =
                "Get info about a emote (custom emoji)\n" +
                "`--search` to search for an emote in the past 100 messages.",
        triggers = {"emote", "e"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}emote <:emote:>\n" +
                        "{p}emote <--search>"
                )
        }
)
public class CmdEmote implements Command{

    private PurrBot bot;

    public CmdEmote(PurrBot bot){
        this.bot = bot;
    }

    private MessageEmbed emoteInfo(User user, Emote emote, Guild guild, @Nullable String link){
        Emote e = bot.getShardManager().getEmoteById(emote.getId());
        Guild emoteGuild = null;
        if(e != null)
            emoteGuild = e.getGuild();

        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(user, guild)
                .setTitle(bot.getMsg(guild.getId(), "purr.info.emote.embed.emote"))
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
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();
        Guild guild = msg.getGuild();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--search") || args.toLowerCase().contains("—search")){
            if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                        .setColor(0xFF0000)
                        .setDescription(
                                bot.getMsg(msg.getGuild().getId(), "errors.missing_perms.self")
                                        .replace("{permission}", Permission.MESSAGE_HISTORY.getName())
                        )
                        .build();
                
                tc.sendMessage(embed).queue();
                return;
            }

            Message emoteMessage = tc.getIterableHistory().stream().limit(100).filter(
                    history -> !history.getEmotes().isEmpty()
            ).findFirst().orElse(null);

            if(emoteMessage == null){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "purr.info.emote.not_found");
                return;
            }

            tc.sendMessage(emoteInfo(msg.getAuthor(), emoteMessage.getEmotes().get(0), guild, emoteMessage.getJumpUrl())).queue();
            return;
        }

        if(msg.getEmotes().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "purr.info.emote.no_args");
            return;
        }

        tc.sendMessage(emoteInfo(msg.getAuthor(), msg.getEmotes().get(0), guild, null)).queue();

    }
}
