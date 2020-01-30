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

package site.purrbot.bot.util.message;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

import javax.annotation.Nullable;

public class WebhookUtil {

    public void sendMsg(String url, String avatar, String name, String message, @Nullable WebhookEmbed embed){

        WebhookClient client = new WebhookClientBuilder(url).build();
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setAvatarUrl(avatar)
                .setUsername(name)
                .setContent(message);

        if(embed != null)
            builder.addEmbeds(embed);

        client.send(builder.build());
        client.close();
    }

    public void sendMsg(String url, String avatar, String name, WebhookEmbed embed){
        sendMsg(url, avatar, name, null, embed);
    }
    
    public void sendMsg(String url, String avatar, String name, String message){
        sendMsg(url, avatar, name, message, null);
    }

}
