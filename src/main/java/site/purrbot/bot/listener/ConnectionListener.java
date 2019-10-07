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

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;

import java.time.ZonedDateTime;

public class ConnectionListener extends ListenerAdapter{

    private PurrBot bot;
    private final String URL;

    public ConnectionListener(PurrBot bot){
        this.bot = bot;

        this.URL = bot.getgFile().getString("config", "log-webhook");
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event){
        JDA jda = event.getJDA();

        WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                .setColor(0xFF0000)
                .setTitle(new WebhookEmbed.EmbedTitle(Emotes.STATUS_DISCONNECT.getEmote(), null))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Shard:",
                        String.valueOf(jda.getShardInfo().getShardId())
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Affected Guilds:",
                        String.valueOf(jda.getGuilds().size())
                ))
                .setFooter(new WebhookEmbed.EmbedFooter("Disconnected at", null))
                .setTimestamp(event.getTimeDisconnected());

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

        bot.getWebhookUtil().sendMsg(
                URL,
                jda.getSelfUser().getEffectiveAvatarUrl(),
                "Disconnected",
                embed.build()
        );
    }

    @Override
    public void onResume(@NotNull ResumedEvent event){
        JDA jda = event.getJDA();

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(0x00FF00)
                .setTitle(new WebhookEmbed.EmbedTitle(Emotes.STATUS_READY.getEmote(), null))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Shard:",
                        String.valueOf(jda.getShardInfo().getShardId())
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true,
                        "Affected Guilds:",
                        String.valueOf(jda.getGuilds().size())
                ))
                .setFooter(new WebhookEmbed.EmbedFooter("Resumed at", null))
                .setTimestamp(ZonedDateTime.now())
                .build();

        bot.getWebhookUtil().sendMsg(
                URL,
                jda.getSelfUser().getEffectiveAvatarUrl(),
                "Resumed session",
                embed
        );
    }

}
