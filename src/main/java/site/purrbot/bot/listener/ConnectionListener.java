package site.purrbot.bot.listener;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;

import java.time.ZonedDateTime;

public class ConnectionListener extends ListenerAdapter{

    private PurrBot manager;
    private final String URL;

    public ConnectionListener(PurrBot manager){
        this.manager = manager;

        this.URL = manager.getgFile().getString("config", "log-webhook");
    }

    @Override
    public void onDisconnect(DisconnectEvent event){
        JDA jda = event.getJDA();

        WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                .setColor(0xFF0000)
                .setTitle(new WebhookEmbed.EmbedTitle(Emotes.STATUS_DISCONNECT.getEmote(), null))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Affected Guilds:",
                        String.valueOf(jda.getGuilds().size())
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Shard:",
                        String.valueOf(jda.getShardInfo().getShardId())
                ))
                .setFooter(new WebhookEmbed.EmbedFooter("Disconnected at", null))
                .setTimestamp(event.getDisconnectTime());

        if(event.getCloseCode() != null)
            embed.addField(new WebhookEmbed.EmbedField(
                    false,
                    "Reason:",
                    String.format(
                            "```\n" +
                            "%d: %s\n" +
                            "```",
                            event.getCloseCode().getCode(),
                            event.getCloseCode().getMeaning()
                    )
            ));

        manager.getWebhookUtil().sendMsg(
                URL,
                jda.getSelfUser().getEffectiveAvatarUrl(),
                "Disconnected",
                embed.build()
        );
    }

    @Override
    public void onResume(ResumedEvent event){
        JDA jda = event.getJDA();

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(0x00FF00)
                .setTitle(new WebhookEmbed.EmbedTitle(Emotes.STATUS_READY.getEmote(), null))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Affected Guilds:",
                        String.valueOf(jda.getGuilds().size())
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Shard:",
                        String.valueOf(jda.getShardInfo().getShardId())
                ))
                .setFooter(new WebhookEmbed.EmbedFooter("Resumed at", null))
                .setTimestamp(ZonedDateTime.now())
                .build();

        manager.getWebhookUtil().sendMsg(
                URL,
                jda.getSelfUser().getEffectiveAvatarUrl(),
                "Resumed session",
                embed
        );
    }

}
