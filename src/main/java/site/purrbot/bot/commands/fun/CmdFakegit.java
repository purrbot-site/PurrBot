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

package site.purrbot.bot.commands.fun;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONObject;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Links;

import java.util.List;

@CommandDescription(
        name = "Fakegit",
        description =
                "Create a webhook message that looks like a real GitHub-commit.\n" +
                "Use `--clear` to remove all webhooks with the name `PurrBot-Fakegit` (Requires manage webhook perms)",
        triggers = {"fakegit", "git"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value =
                        "{p}fakegit\n" +
                        "{p}fakegit --clear"
                )
        }
)
public class CmdFakegit implements Command{

    private PurrBot bot;

    public CmdFakegit(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        JSONObject json = bot.getHttpUtil().getFakeGit();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--clear")){
            if(!bot.getPermUtil().hasPermission(tc, Permission.MANAGE_WEBHOOKS)){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "I need `manage webhook` permission to do this!");
                return;
            }

            List<Webhook> webhooks = tc.retrieveWebhooks().complete();

            tc.sendMessage("Deleting all webhooks with name `PurrBot-Fakegit`. Please wait...").queue(message -> {
                webhooks.stream().filter(webhook -> webhook.getName().equals("PurrBot-Fakegit")).forEach(webhook ->
                        webhook.delete().reason("[Fakegit] Deleting Fakegit-Webhooks").queue()
                );
                tc.sendMessage("Removed all Webhooks!").queue();
            });
            return;
        }

        if(json == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API. Try again later.");
            return;
        }

        String link   = json.getString("permalink");
        String hash   = json.getString("hash").substring(0, 7);
        String commit = json.getString("commit_message");

        String name = msg.getMember() == null ? "Unknown Member" : msg.getMember().getEffectiveName();

        int color = 0x7289DA;

        MessageEmbed mEmbed = new EmbedBuilder()
                .setColor(color)
                .setAuthor(name, link, msg.getAuthor().getEffectiveAvatarUrl())
                .setTitle(String.format(
                        "[`%s:%s`] 1 new commit",
                        guild.getName().replace(" ", "\\_"),
                        tc.getName()
                ), link)
                .setDescription(String.format(
                        "[`%s`](%s) %s",
                        hash,
                        link,
                        commit
                ))
                .build();

        WebhookEmbed wEmbed = new WebhookEmbedBuilder()
                .setColor(color)
                .setAuthor(new WebhookEmbed.EmbedAuthor(
                        name,
                        link,
                        msg.getAuthor().getEffectiveAvatarUrl()
                ))
                .setTitle(new WebhookEmbed.EmbedTitle(String.format(
                        "[`%s:%s`] 1 new commit",
                        guild.getName().replace(" ", "_"),
                        tc.getName()
                ), link))
                .setDescription(String.format(
                        "[`%s`](%s) %s",
                        hash,
                        link,
                        commit
                ))
                .build();

        if(bot.getPermUtil().hasPermission(tc, Permission.MANAGE_WEBHOOKS)){
            try{
                bot.getWebhookUtil().sendMsg(tc, Links.GITHUB_AVATAR.getUrl(), "GitHub", null, wEmbed);
            }catch(Exception ignored){
                tc.sendMessage(mEmbed).queue();
            }
            return;
        }

        tc.sendMessage(mEmbed).queue();
    }
}
