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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.message.WebhookUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReadyListener extends ListenerAdapter{

    private final Logger logger = (Logger)LoggerFactory.getLogger(ReadyListener.class);

    private final PurrBot bot;
    private final WebhookUtil webhookUtil;
    
    private int shards = 0;

    public ReadyListener(PurrBot bot){
        this.bot = bot;
        this.webhookUtil = new WebhookUtil(bot.getFileManager().getString("config", "webhooks.log"));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event){
        ShardManager shardManager = bot.getShardManager();
        JDA jda = event.getJDA();

        shards++;
        logger.info("{} guilds ready!", jda.getGuildCache().size());
    
        WebhookEmbed embed = new WebhookEmbedBuilder()
            .setColor(0x00FF00)
            .addField(new WebhookEmbed.EmbedField(
                true,
                "Guilds:",
                String.valueOf(jda.getGuilds().size())
            ))
            .addField(new WebhookEmbed.EmbedField(
                true,
                "Shard:",
                String.valueOf(jda.getShardInfo().getShardId())
            ))
            .setFooter(new WebhookEmbed.EmbedFooter("Ready at", null))
            .setTimestamp(ZonedDateTime.now())
            .build();
    
        webhookUtil.sendMsg(
            jda.getSelfUser().getEffectiveAvatarUrl(),
            String.format(
                "Shard %d: Loaded",
                event.getJDA().getShardInfo().getShardId()
            ),
            embed
        );
        
        if(shards == jda.getShardInfo().getShardTotal()){
            shardManager.setPresence(OnlineStatus.ONLINE, Activity.of(
                    Activity.ActivityType.WATCHING,
                    bot.getMessageUtil().getBotGame(shardManager.getGuildCache().size())
            ));
            
            bot.startUpdater();

            WebhookEmbed finished = new WebhookEmbedBuilder()
                    .setColor(0x00FF00)
                    .setTitle(new WebhookEmbed.EmbedTitle(String.format(
                            "\\%s ready!",
                            jda.getSelfUser().getName()
                    ), null))
                    .setDescription(String.format(
                            "\\%s is online and ready to bring you fun and nekos. <:catUwU:703924268022497340>",
                            jda.getSelfUser().getName()
                    ))
                    .build();
            
            webhookUtil.sendMsg(
                    jda.getSelfUser().getEffectiveAvatarUrl(),
                    "Ready!",
                    finished
            );
            
            logger.info(
                    "Loaded Bot {} vBOT_VERSION with {} shard(s) and {} guilds!",
                    jda.getSelfUser().getAsTag(),
                    shardManager.getShardCache().size(),
                    bot.getMessageUtil().formatNumber(shardManager.getGuildCache().size())
            );
        }
    }
}
