package site.purrbot.bot.commands.fun;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
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

    private PurrBot manager;

    public CmdFakegit(PurrBot manager){
        this.manager = manager;
    }

    @Override
    public void execute(Message msg, String args) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        JSONObject json = manager.getHttpUtil().getFakeGit();

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--clear")){
            if(!manager.getPermUtil().hasPermission(tc, Permission.MANAGE_WEBHOOKS)){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "I need `manage webhook` permission to do this!");
                return;
            }

            List<Webhook> webhooks = tc.getWebhooks().complete();

            tc.sendMessage("Deleting all webhooks with name `PurrBot-Fakegit`. Please wait...").queue(message -> {
                webhooks.stream().filter(webhook -> webhook.getName().equals("PurrBot-Fakegit")).forEach(webhook ->
                        webhook.delete().reason("[Fakegit] Deleting Fakegit-Webhooks").queue()
                );
                tc.sendMessage("Removed all Webhooks!").queue();
            });
            return;
        }

        if(json == null){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API. Try again later.");
            return;
        }

        String link   = json.getString("permalink");
        String hash   = json.getString("hash").substring(0, 7);
        String commit = json.getString("commit_message");

        int color = 0x7289DA;

        MessageEmbed mEmbed = new EmbedBuilder()
                .setColor(color)
                .setAuthor(msg.getMember().getEffectiveName(), link, msg.getAuthor().getEffectiveAvatarUrl())
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
                        msg.getMember().getEffectiveName(),
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

        if(manager.getPermUtil().hasPermission(tc, Permission.MANAGE_WEBHOOKS)){
            try{
                manager.getWebhookUtil().sendMsg(tc, Links.GITHUB_AVATAR.getUrl(), "GitHub", null, wEmbed);
            }catch(Exception ignored){
                tc.sendMessage(mEmbed).queue();
            }
            return;
        }

        tc.sendMessage(mEmbed).queue();
    }
}
