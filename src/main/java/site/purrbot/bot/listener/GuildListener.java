/*
 * Copyright 2019 Andre601
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

package site.purrbot.bot.listener;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public class GuildListener extends ListenerAdapter{

    private Logger logger = (Logger)LoggerFactory.getLogger(GuildListener.class);

    private PurrBot bot;

    public GuildListener(PurrBot bot){
        this.bot = bot;
    }

    private boolean isBotGuild(Guild guild){
        long bots    = guild.getMembers().stream().filter(member -> member.getUser().isBot()).count();
        long members = guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count();

        return bots > (members + 2);
    }

    private void sendWebhook(Guild guild, String title, String text, Emotes emote, int color){
        Member owner = guild.getOwner();
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(color)
                .setThumbnailUrl(guild.getIconUrl())
                .setTitle(new WebhookEmbed.EmbedTitle(emote.getEmote(), null))
                .addField(new WebhookEmbed.EmbedField(
                        true, "Name", guild.getName()
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true, "Shard [Current/Total]", guild.getJDA().getShardInfo().getShardString()
                ))
                .addField(new WebhookEmbed.EmbedField(
                        false, "Owner", String.format(
                                "%s | %s (`%s`)", 
                                owner == null ? "Unknown" : owner.getAsMention(),
                                owner == null ? "Unknown" : owner.getUser().getName(),
                                owner == null ? "?" : owner.getId()
                        )
                ))
                .addField(new WebhookEmbed.EmbedField(
                        false, "Members", String.format(
                                "```yaml\n" +
                                "Total: %5d\n" +
                                "Bots:  %5d\n" +
                                "Users: %5d\n" +
                                "```",
                                guild.getMembers().size(),
                                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count(),
                                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count()
                        )   
                ))
                .setFooter(new WebhookEmbed.EmbedFooter(
                        String.format(
                                "Guild #%d",
                                bot.getShardManager().getGuildCache().size()
                        ),
                        null
                ))
                .setTimestamp(ZonedDateTime.now())
                .build();
        
        bot.getWebhookUtil().sendMsg(
                bot.getFileManager().getString("config", "webhooks.guild"),
                guild.getSelfMember().getUser().getEffectiveAvatarUrl(),
                title,
                text,
                embed
        );
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId())){
            if(guild.getOwner() == null){
                guild.leave().queue();
                return;
            }
            guild.getOwner().getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage(String.format(
                            "I left your Discord `%s` for the following reason:\n" +
                            "```\n" +
                            "[Auto Leave] Your Discord is blacklisted! Join [our Discord](%s) for the reason.\n" +
                            "```",
                            guild.getName(),
                            Links.DISCORD.getUrl()
                    )).queue(message -> {
                        logger.info("[Auto Leave] Reason: Blacklisted Guild. Send successful: Yes");
                        guild.leave().queue();
                    }, throwable -> {
                        logger.info("[Auto Leave] Reason: Blacklisted Guild. Send successful: No");
                        guild.leave().queue();
                    })
            );
            return;
        }

        if(isBotGuild(guild)) {
            if(guild.getOwner() == null){
                guild.leave().queue();
                return;
            }
            if(!guild.getOwner().getUser().getId().equals(IDs.ANDRE_601.getId())){
                guild.getOwner().getUser().openPrivateChannel().queue(channel ->
                        channel.sendMessage(String.format(
                                "I left your Discord `%s` for the following reason:\n" +
                                "```\n" +
                                "[Auto Leave] Your Discord has more bots than users.\n" +
                                "             Please reduce the amounts of bots you have\n" +
                                "             or invite more Members.\n" +
                                "```",
                                guild.getName()
                        )).queue(message -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: Yes");
                        }, throwable -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: No");
                        })
                );
                sendWebhook(
                        guild,
                        "Leave [Auto]",
                        String.format(
                                "ID: %s",
                                guild.getId()
                        ),
                        Emotes.LEFT_GUILD,
                        0xFF0000
                );

                return;
            }
        }

        bot.getDbUtil().addGuild(guild.getId());

        logger.info(String.format(
                "[Guild join] Name: %s (%s), Members: %d (Bots: %d, Users: %d)",
                guild.getName(),
                guild.getId(),
                guild.getMembers().size(),
                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count(),
                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count()
        ));
        
        sendWebhook(
                guild,
                "Join",
                String.format(
                        ".leave %s",
                        guild.getId()
                ),
                Emotes.JOINED_GUILD,
                0x00FF00
        );
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId()))
            return;

        if(isBotGuild(guild)){
            Member owner = guild.getOwner();
            if(owner == null)
                return;
            if(!owner.getId().equals(IDs.ANDRE_601.getId()))
                return;
        }

        bot.getDbUtil().delGuild(guild.getId());

        bot.invalidateCache(guild.getId());

        logger.info(String.format(
                "[Guild leave] Name: %s (%s), Members: %d (Bots: %d, Users: %d)",
                guild.getName(),
                guild.getId(),
                guild.getMembers().size(),
                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count(),
                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count()
        ));
    
        sendWebhook(
                guild,
                "Leave",
                String.format(
                        "ID: %s",
                        guild.getId()
                ),
                Emotes.LEFT_GUILD,
                0xFF0000
        );
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
        Guild guild = event.getGuild();

        if(isBotGuild(guild)){
            if(guild.getOwner() == null){
                guild.leave().queue();
                return;
            }
            if(!guild.getOwner().getUser().getId().equals(IDs.ANDRE_601.getId())) {
                guild.getOwner().getUser().openPrivateChannel().queue(channel ->
                        channel.sendMessage(String.format(
                                "I left your Discord `%s` for the following reason:\n" +
                                "```\n" +
                                "[Auto Leave] Your Discord has more bots than users.\n" +
                                "             Please reduce the amounts of bots you have\n" +
                                "             or invite more Members.\n" +
                                "```",
                                guild.getName()
                        )).queue(message -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: Yes");
                        }, throwable -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: No");
                        })
                );
    
                sendWebhook(
                        guild,
                        "Leave [Auto]",
                        String.format(
                                "ID: %s",
                                guild.getId()
                        ),
                        Emotes.LEFT_GUILD,
                        0xFF0000
                );
                return;
            }
        }

        if(event.getUser().isBot())
            return;

        if(bot.getWelcomeChannel(guild.getId()).equals("none"))
            return;

        TextChannel tc = guild.getTextChannelById(bot.getWelcomeChannel(guild.getId()));
        if(tc == null)
            return;

        if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_WRITE))
            return;

        String msg = bot.getWelcomeMsg(guild.getId());
        if(msg == null)
            msg = "Hello {mention}!";

        Message message = new MessageBuilder(bot.getMessageUtil().formatPlaceholders(msg, event.getMember())).build();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)) {
            InputStream is;

            try {
                is = bot.getImageUtil().getWelcomeImg(
                        event.getMember(),
                        bot.getWelcomeIcon(guild.getId()),
                        bot.getWelcomeBg(guild.getId()),
                        bot.getWelcomeColor(guild.getId())
                );
            }catch(IOException ex){
                is = null;
            }

            if(is == null){
                tc.sendMessage(message).queue();
                return;
            }

            tc.sendMessage(message).addFile(is, String.format(
                    "welcome_%s.jpg",
                    event.getUser().getId()
            )).queue();
        }else{
            tc.sendMessage(message).queue();
        }
    }

    @Override
    public void onTextChannelDelete(@Nonnull TextChannelDeleteEvent event){
        Guild guild = event.getGuild();
        String id = bot.getWelcomeChannel(guild.getId());

        if(id.equals("none"))
            return;

        TextChannel tc = guild.getTextChannelById(id);
        if(tc == null) {
            bot.setWelcomeChannel(guild.getId(), "none");
            return;
        }

        if(event.getChannel().getId().equals(id))
            bot.setWelcomeChannel(guild.getId(), "none");
    }
}
