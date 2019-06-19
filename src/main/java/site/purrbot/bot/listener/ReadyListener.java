package site.purrbot.bot.listener;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.time.ZonedDateTime;

public class ReadyListener extends ListenerAdapter{

    private Logger logger = (Logger)LoggerFactory.getLogger(ReadyListener.class);

    private PurrBot manager;
    private boolean ready = false;
    private int shards = 0;

    public ReadyListener(PurrBot manager){
        this.manager = manager;
    }

    /**
     * Returns if the bot is ready for use.
     *
     * @return If the bot is ready to use.
     */
    public boolean isReady() {
        return ready;
    }

    private void setReady(boolean ready){
        this.ready = ready;
    }

    @Override
    public void onReady(ReadyEvent event){
        ShardManager shardManager = manager.getShardManager();
        JDA jda = event.getJDA();

        shards += 1;

        logger.info(String.format(
                "Shard %d (%d Guilds) ready!",
                shards,
                jda.getGuilds().size()
        ));

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

        manager.getWebhookUtil().sendMsg(
                manager.getgFile().getString("config", "log-webhook"),
                jda.getSelfUser().getEffectiveAvatarUrl(),
                "Shard ready!",
                embed
        );

        if(shards == jda.getShardInfo().getShardTotal()){
            setReady(true);

            shardManager.setPresence(OnlineStatus.ONLINE, Game.of(Game.GameType.WATCHING, String.format(
                    manager.getMessageUtil().getBotGame(),
                    shardManager.getGuildCache().size()
            )));

            logger.info(String.format(
                    "Loaded Bot %s vBOT_VERSION with %d shard(s) and %d guilds!",
                    jda.getSelfUser().getAsTag(),
                    shardManager.getShardCache().size(),
                    shardManager.getGuildCache().size()
            ));
        }
    }
}
