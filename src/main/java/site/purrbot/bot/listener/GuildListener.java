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

package site.purrbot.bot.listener;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.util.message.WebhookUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public class GuildListener extends ListenerAdapter{

    private final Logger logger = (Logger)LoggerFactory.getLogger(GuildListener.class);

    private final PurrBot bot;
    private final WebhookUtil webhookUtil;

    public GuildListener(PurrBot bot){
        this.bot = bot;
        this.webhookUtil = new WebhookUtil(bot.getFileManager().getString("config", "webhooks.guild"));
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId())){
            if(guild.getOwner() == null){
                guild.leave().queue();
                return;
            }
            guild.getOwner().getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageFormat(
                            "I left your Discord `%s` for the following reason:\n" +
                            "```\n" +
                            "[Auto Leave] Your Discord is blacklisted! To find out why, join the\n" +
                            "             Support Discord: %s\n" +
                            "```",
                            guild.getName(),
                            Links.DISCORD
                    ))
                    .queue(message -> {
                        logger.info("[Guild Leave] {}(id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
                        guild.leave().queue();
                    }, throwable -> {
                        logger.info("[Guild Leave] {}(id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
                        guild.leave().queue();
                    });
            
            guild.retrieveOwner().queue(
                    owner -> sendWebhook(owner, guild, Action.AUTO_LEAVE),
                    e -> sendWebhook(null, guild, Action.AUTO_LEAVE)
            );
            return;
        }
        
        bot.getDbUtil().addGuild(guild.getId(), guild.getLocale());
        
        logger.info("[Guild Join] {}(id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
        
        guild.retrieveOwner().queue(
                owner -> sendWebhook(owner, guild, Action.JOIN),
                e -> sendWebhook(null, guild, Action.JOIN)
        );
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId()))
            return;

        bot.getDbUtil().delGuild(guild.getId());

        bot.invalidateCache(guild.getId());
    
        logger.info("[Guild Leave] {}(id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
    
        guild.retrieveOwner().queue(
                owner -> sendWebhook(owner, guild, Action.LEAVE),
                e -> sendWebhook(null, guild, Action.LEAVE)
        );
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
        Guild guild = event.getGuild();

        if(event.getUser().isBot())
            return;

        if(bot.getWelcomeChannel(guild.getId()).equals("none"))
            return;

        TextChannel tc = guild.getTextChannelById(bot.getWelcomeChannel(guild.getId()));
        if(tc == null)
            return;

        if(!guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_WRITE))
            return;

        String msg = bot.getWelcomeMsg(guild.getId());
        if(msg == null)
            msg = "Hello {mention}!";
        
        InputStream image;
    
        try {
            image = bot.getImageUtil().getWelcomeImg(
                    event.getMember(),
                    bot.getWelcomeIcon(guild.getId()),
                    bot.getWelcomeBg(guild.getId()),
                    bot.getWelcomeColor(guild.getId())
            );
        }catch(IOException ex){
            image = null;
        }
        
        bot.getMessageUtil().sendWelcomeMsg(tc, msg, event.getMember(), image);
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
    
    private void sendWebhook(Member owner, Guild guild, Action action) {
        String title = null;
        WebhookEmbed embed = null;
        
        String mention = owner == null ? "Unknown" : owner.getAsMention();
        String name    = owner == null ? "Unknown" : owner.getUser().getName();
        String id      = owner == null ? "?" : owner.getId();
        
        switch(action){
            case JOIN:
                title = "Join";
                embed = new WebhookEmbedBuilder()
                        .setColor(0x00FF00)
                        .setThumbnailUrl(guild.getIconUrl())
                        .setTitle(new WebhookEmbed.EmbedTitle(Emotes.PLUS.getEmote(), null))
                        .addField(new WebhookEmbed.EmbedField(
                                true, "Name", guild.getName()
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                true, "Shard [Current/Total]", guild.getJDA().getShardInfo().getShardString()
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false,
                                "Owner",
                                String.format(
                                        "%s | %s (`%s`)", 
                                        mention, 
                                        MarkdownSanitizer.escape(name), 
                                        id
                                )
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false,
                                "Members", 
                                String.format(
                                        "```yaml\n" + 
                                        "Total: %d\n" + 
                                        "```", 
                                guild.getMemberCount()
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
                break;
            
            case LEAVE:
                title = "Leave";
                embed = new WebhookEmbedBuilder()
                        .setColor(0xFF0000)
                        .setThumbnailUrl(guild.getIconUrl())
                        .setTitle(new WebhookEmbed.EmbedTitle(Emotes.MINUS.getEmote(), null))
                        .addField(new WebhookEmbed.EmbedField(
                                true, "Name", guild.getName()
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                true, "Shard [Current/Total]", guild.getJDA().getShardInfo().getShardString()
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false, "Owner", String.format(
                                "%s | %s (`%s`)",
                                mention,
                                MarkdownSanitizer.escape(name),
                                id
                        )
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false,
                                "Members",
                                String.format(
                                        "```yaml\n" + 
                                        "Total: %d\n" + 
                                        "```", 
                                        guild.getMemberCount()
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
                break;
            
            case AUTO_LEAVE:
                title = "Leave [Auto]";
                embed = new WebhookEmbedBuilder()
                        .setColor(0xFF0000)
                        .setThumbnailUrl(guild.getIconUrl())
                        .setTitle(new WebhookEmbed.EmbedTitle(Emotes.MINUS.getEmote(), null))
                        .addField(new WebhookEmbed.EmbedField(
                                true,
                                "Name",
                                guild.getName()
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                true,
                                "Shard [Current/Total]",
                                guild.getJDA().getShardInfo().getShardString()
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false,
                                "Owner",
                                String.format(
                                        "%s | %s (`%s`)", 
                                        mention, 
                                        MarkdownSanitizer.escape(name), 
                                        id
                                )
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false,
                                "Leave reason",
                                "`Blacklisted`"
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false,
                                "Members",
                                String.format(
                                        "```yaml\n" + 
                                        "Total: %d\n" + 
                                        "```", 
                                        guild.getMemberCount()
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
                break;
        }
        
        webhookUtil.sendMsg(
                guild.getSelfMember().getUser().getEffectiveAvatarUrl(),
                title,
                action == Action.JOIN ? ".leave " + guild.getId() : null,
                embed
        );
    }
    
    enum Action{
        JOIN,
        LEAVE,
        AUTO_LEAVE
    }
}
