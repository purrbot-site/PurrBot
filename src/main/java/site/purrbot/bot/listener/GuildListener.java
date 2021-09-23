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

package site.purrbot.bot.listener;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.util.message.WebhookUtil;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class GuildListener extends ListenerAdapter{

    private final Logger logger = (Logger)LoggerFactory.getLogger(GuildListener.class);

    private final PurrBot bot;
    private final WebhookUtil webhookUtil;
    private final List<CommandData> commands = new ArrayList<>();
    private final List<Long> guildIds = new ArrayList<>();

    public GuildListener(PurrBot bot, CommandClient client){
        this.bot = bot;
        this.webhookUtil = new WebhookUtil(bot.getFileManager().getString("config", "webhooks.guild"));
        
        for(SlashCommand command : client.getSlashCommands())
            this.commands.add(command.buildCommandData());
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event){
        loadCommands(event.getGuild());
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
                        logger.info("[Guild Leave] {} (id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
                        guild.leave().queue();
                    }, throwable -> {
                        logger.info("[Guild Leave] {} (id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
                        guild.leave().queue();
                    });
            
            guild.retrieveOwner().queue(
                    owner -> sendWebhook(owner, guild, Action.AUTO_LEAVE),
                    e -> sendWebhook(null, guild, Action.AUTO_LEAVE)
            );
            return;
        }
        
        bot.getDbUtil().addGuild(guild.getId());
        
        logger.info("[Guild Join] {} (id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
        
        loadCommands(guild);
        
        guild.retrieveOwner().queue(
                owner -> sendWebhook(owner, guild, Action.JOIN),
                e -> sendWebhook(null, guild, Action.JOIN)
        );
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event){
        Guild guild = event.getGuild();
    
        bot.getDbUtil().delGuild(guild.getId());
    
        bot.invalidateCache(guild.getId());
    
        guildIds.remove(guild.getIdLong());

        if(bot.getBlacklist().contains(guild.getId()))
            return;
    
        logger.info("[Guild Leave] {} (id: {}, members: {})", guild.getName(), guild.getId(), guild.getMemberCount());
    
        guild.retrieveOwner().queue(
                owner -> sendWebhook(owner, guild, Action.LEAVE),
                e -> sendWebhook(null, guild, Action.LEAVE)
        );
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
        String mention = owner == null ? "Unknown" : owner.getAsMention();
        String name    = owner == null ? "Unknown" : owner.getUser().getName();
        String id      = owner == null ? "?" : owner.getId();
        
        String title = null;
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                .setThumbnailUrl(guild.getIconUrl())
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Name",
                        guild.getName()
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Shard [Current / Total]",
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
                                "Guild #%s",
                                bot.getMessageUtil().formatNumber(bot.getShardManager().getGuildCache().size())
                        ),
                        null
                ))
                .setTimestamp(ZonedDateTime.now());
        
        switch(action){
            case JOIN:
                title = "Joined Server";
                embed.setColor(0x00FF00);
                break;
            
            case LEAVE:
                title = "Left Server";
                embed.setColor(0xFF0000);
                break;
            
            case AUTO_LEAVE:
                title = "Left Server [Auto]";
                embed.setColor(0xFF0000);
                break;
        }
        
        webhookUtil.sendMsg(
                guild.getSelfMember().getUser().getEffectiveAvatarUrl(),
                title,
                action == Action.JOIN ? ".leave " + guild.getId() : null,
                embed.build()
        );
    }
    
    private void loadCommands(Guild guild){
        if(guildIds.contains(guild.getIdLong()))
            return;
        
        logger.info("Loading {} commands for Guild {} ({})...", commands.size(), guild.getName(), guild.getId());
        guild.updateCommands().addCommands(commands).queue(
            cmds -> logger.info("Loaded {} commands for Guild {} ({}) successfully!", cmds.size(), guild.getName(), guild.getId())
        );
        guildIds.add(guild.getIdLong());
    }
    
    enum Action{
        JOIN,
        LEAVE,
        AUTO_LEAVE
    }
}
