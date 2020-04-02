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
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.constants.Roles;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MEMBER;

public class GuildListener extends ListenerAdapter{

    private Logger logger = (Logger)LoggerFactory.getLogger(GuildListener.class);

    private PurrBot bot;

    public GuildListener(PurrBot bot){
        this.bot = bot;
    }
    
    private void sendWebhook(Action action, Guild guild){
        Member owner = guild.getOwner();
        
        String title = null;
        WebhookEmbed embed = null;
        
        switch(action){
            case JOIN:
                title = "Join";
                embed = new WebhookEmbedBuilder()
                        .setColor(0x00FF00)
                        .setThumbnailUrl(guild.getIconUrl())
                        .setTitle(new WebhookEmbed.EmbedTitle(Emotes.JOINED_GUILD.getEmote(), null))
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
                        .setTitle(new WebhookEmbed.EmbedTitle(Emotes.LEFT_GUILD.getEmote(), null))
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
                        .setTitle(new WebhookEmbed.EmbedTitle(Emotes.LEFT_GUILD.getEmote(), null))
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
                                false, 
                                "Leave reason", 
                                "`Blacklisted`"
                        ))
                        .addField(new WebhookEmbed.EmbedField(
                                false, "Members", String.format(
                                "```yaml\n" +
                                "Total: %5d\n" +
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
        
        bot.getWebhookUtil().sendMsg(
                bot.getFileManager().getString("config", "webhooks.guild"),
                guild.getSelfMember().getUser().getEffectiveAvatarUrl(),
                title,
                action == Action.JOIN ? ".leave " + guild.getId() : null,
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
            guild.getOwner().getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageFormat(
                            "I left your Discord `%s` for the following reason:\n" +
                            "```\n" +
                            "[Auto Leave] Your Discord is blacklisted! To find out why, join the\n" +
                            "             Support Discord: %s\n" +
                            "```",
                            guild.getName(),
                            Links.DISCORD.getUrl()
                    ))
                    .queue(message -> {
                        logger.info(String.format(
                                "[Guild Leave] Guild{name=\"%s\", members=%d}",
                                guild.getName(),
                                guild.getMemberCount()
                        ));
                        guild.leave().queue();
                    }, throwable -> {
                        logger.info(String.format(
                                "[Guild Leave] Guild{name=\"%s\", members=%d}",
                                guild.getName(),
                                guild.getMemberCount()
                        ));
                        guild.leave().queue();
                    });
            
            sendWebhook(Action.AUTO_LEAVE, guild);
            return;
        }

        bot.getDbUtil().addGuild(guild.getId());

        logger.info(String.format(
                "[Guild Join] Guild{name=\"%s\", id=%s, members=%d}",
                guild.getName(),
                guild.getId(),
                guild.getMemberCount()
        ));
        
        sendWebhook(Action.JOIN, guild);
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId()))
            return;

        bot.getDbUtil().delGuild(guild.getId());

        bot.invalidateCache(guild.getId());

        logger.info(String.format(
                "[Guild Leave] Guild{name\"%s\", id=%s, members=%d}",
                guild.getName(),
                guild.getId(),
                guild.getMemberCount()
        ));
    
        sendWebhook(Action.LEAVE, guild);
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

        if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_WRITE))
            return;

        String msg = bot.getWelcomeMsg(guild.getId());
        if(msg == null)
            msg = "Hello {mention}!";

        String message = bot.getMessageUtil().parsePlaceholders(msg, event.getMember());

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
    
    @Override
    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event){
        if(!bot.isBeta())
            return;
        
        if(event.getUser().isBot())
            return;
        
        Guild guild = event.getGuild();
        if(!guild.getId().equals(IDs.GUILD.getId()))
            return;
        
        Role role = guild.getRoleById(Roles.MEMBER.getId());
        if(event.getRoles().contains(role))
            giveRoles(event.getMember());
    }
    
    private void giveRoles(Member target){
        Guild guild = bot.getShardManager().getGuildById(IDs.GUILD.getId());
        
        if(guild == null)
            return;
        
        Role special       = guild.getRoleById(Roles.SPECIAL.getId());
        Role loves         = guild.getRoleById(Roles.LOVES.getId());
        Role person        = guild.getRoleById(Roles.PERSON.getId());
        Role notifications = guild.getRoleById(Roles.NOTIFICATIONS.getId());
        
        guild.modifyMemberRoles(target, Arrays.asList(special, loves, person, notifications), null)
                .reason(String.format(
                        "[Join Roles] Giving %s join roles.",
                        target.getEffectiveName()
                ))
                .queue();
    }
    
    enum Action{
        JOIN,
        LEAVE,
        AUTO_LEAVE
    }
}
