/*
 *  Copyright 2018 - 2022 Andre601
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

package site.purrbot.bot.listeners;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.WebhookManager;

import java.time.Instant;

public class ReadyListener extends ListenerAdapter{
    
    private final Logger LOGGER = (Logger)LoggerFactory.getLogger(ReadyListener.class);
    
    private final WebhookManager webhookManager;
    
    private int shards = 0;
    
    public ReadyListener(){
        this.webhookManager = new WebhookManager(PurrBot.getBot().getFileManager().getString("config", "webhooks.log", null));
    }
    
    @Override
    public void onReady(@NotNull ReadyEvent event){
        JDA jda = event.getJDA();
        int shardId = jda.getShardInfo().getShardId();
        
        shards++;
        LOGGER.info("Shard ready with {} guilds!", jda.getGuildCache().size());
    
        WebhookEmbed embed = new WebhookEmbedBuilder()
            .setColor(0x00FF00)
            .addField(new WebhookEmbed.EmbedField(
                true,
                "Guilds:",
                String.valueOf(jda.getGuildCache().size())
            ))
            .addField(new WebhookEmbed.EmbedField(
                true,
                "Shard:",
                String.valueOf(shardId)
            ))
            .setFooter(new WebhookEmbed.EmbedFooter("Ready at", null))
            .setTimestamp(Instant.now())
            .build();
        
        webhookManager.sendMessage(jda.getSelfUser().getEffectiveAvatarUrl(), String.format("Shard %d: Ready", shardId), embed);
        
        if(shards == jda.getShardInfo().getShardTotal()){
            PurrBot.getBot().getShardManager().setPresence(OnlineStatus.ONLINE, Activity.of(
                Activity.ActivityType.WATCHING,
                PurrBot.getBot().getFileManager().getString("random", "startup_msg", "Starting bot...")
            ));
    
            PurrBot.getBot().initUpdater();
            
            WebhookEmbed finished = new WebhookEmbedBuilder()
                .setColor(0x00FF00)
                .setTitle(new WebhookEmbed.EmbedTitle(String.format("\\%s ready!", jda.getSelfUser().getName()), null))
                .setDescription(String.format(
                    "\\%s is online and ready to bring you fun and a lot of nekos! <:catUwU:703924268022497340>",
                    jda.getSelfUser().getName()
                ))
                .build();
            
            webhookManager.sendMessage(jda.getSelfUser().getEffectiveAvatarUrl(), "Bot Ready!", finished);
            
            LOGGER.info(
                "Startup complete! Loaded {} vBOT_VERSION with {} shard(s) and {} guilds.",
                jda.getSelfUser().getAsTag(),
                jda.getShardInfo().getShardTotal(),
                PurrBot.getBot().getShardManager().getGuildCache().size()
            );
        }
    }
}
