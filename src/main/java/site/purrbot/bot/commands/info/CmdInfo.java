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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Info",
        description =
                "Get some basic info about the bot.\n" +
                "\n" +
                "Use `--dm` to send it in DM.",
        triggers = {"info", "infos", "information"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}info [--dm]"
                )
        }
)
public class CmdInfo implements Command{

    private PurrBot bot;

    public CmdInfo(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        EmbedBuilder info = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setAuthor(msg.getJDA().getSelfUser().getName(),
                        Links.WEBSITE.getUrl(),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setThumbnail(msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField("About me", String.format(
                        "Oh hi there!\n" +
                        "I'm %s. A Bot for the ~Nya Discord.\n" +
                        "I was made by Andre_601 (<@%s>) with the help of JDA " +
                        "and a lot of free time. ;)\n",
                        guild.getSelfMember().getAsMention(),
                        IDs.ANDRE_601.getId()
                ), false)
                .addField("Commands", String.format(
                        "Use `%shelp` in this Discord for a list of commands.",
                        bot.getPrefix(guild.getId())
                ), false)
                .addField("Bot Info", String.format(
                        "Bot version: `BOT_VERSION`\n" +
                        "Library: [`JDA %s`](%s)",
                        JDAInfo.VERSION,
                        JDAInfo.GITHUB
                ), false)
                .addField("Bot Lists", String.format(
                        "[`Botlist.space`](%s)\n" +
                        "[`Discordextremelist.xyz`](%s)\n" +
                        "[`Discord.bots.gg`](%s)\n" +
                        "[`LBots.org`](%s)\n" +
                        "[`Top.gg`](%s)",
                        Links.BOTLIST_SPACE.getUrl(),
                        Links.DISCORDEXTREMELIST_XYZ.getUrl(),
                        Links.DISCORD_BOTS_GG.getUrl(),
                        Links.LBOTS_ORG.getUrl(),
                        Links.TOP_GG.getUrl()
                ), false)
                .addField("Other links", String.format(
                        "[`GitHub`](%s)\n" +
                        "[`Patreon`](%s)\n" +
                        "[`Support Discord`](%s)\n" +
                        "[`Twitter`](%s)\n" +
                        "[`Website`](%s)\n" +
                        "[`Wiki`](%s)\n",
                        Links.GITHUB.getUrl(),
                        Links.PATREON.getUrl(),
                        Links.DISCORD.getUrl(),
                        Links.TWITTER.getUrl(),
                        Links.WEBSITE.getUrl(),
                        Links.WIKI.getUrl()
                ), false);

        if(args.toLowerCase().contains("--dm")){
            msg.getAuthor().openPrivateChannel().queue(
                    pm -> pm.sendMessage(info.build()).queue(message ->
                            tc.sendMessage(String.format(
                                    "Check you DMs %s!",
                                    msg.getAuthor().getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    ), throwable -> tc.sendMessage(String.format(
                            "I can't send you a DM %s!",
                            msg.getAuthor().getAsMention()
                    )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
            );
            return;
        }

        tc.sendMessage(info.build()).queue();
    }
}
