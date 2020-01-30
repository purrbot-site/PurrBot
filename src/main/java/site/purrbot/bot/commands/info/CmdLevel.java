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

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.io.File;
import java.text.DecimalFormat;

@CommandDescription(
        name = "Level",
        description =
                "Shows your level on the ~Nya guild.\n" +
                "Mention a user to see his/her progress.",
        triggers = {"level", "lvl"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "guild_only"),
                @CommandAttribute(key = "usage", value =
                        "{p}level [@user]"
                )
        }
)
public class CmdLevel implements Command{

    private PurrBot bot;

    public CmdLevel(PurrBot bot){
        this.bot = bot;
    }

    private void sendLevelEmbed(TextChannel tc, User requester, Member member){
        String id = member.getUser().getId();

        long xp = bot.getXp(id);
        long level = bot.getLevel(id);

        double reqXpDouble = bot.getLevelManager().reqXp(level);
        long reqXpLong = (long)reqXpDouble;

        Double progress = (xp / reqXpDouble) * 100;

        String imageName = level >= 30 ? String.format("progress_%s.png", id) : String.format("progress_%s.gif", id);
        File image = bot.getLevelManager().getImage(level);

        EmbedBuilder levelEmbed = bot.getEmbedUtil().getEmbed(requester, tc.getGuild())
                .setDescription(
                        bot.getMsg(tc.getGuild().getId(), "purr.info.level.info", member.getEffectiveName())
                )
                .addField(
                        bot.getMsg(tc.getGuild().getId(), "purr.info.level.level"), 
                        String.format(
                                "`%d`",
                                level
                        ),
                        true
                )
                .addField(
                        bot.getMsg(tc.getGuild().getId(), "purr.info.level.xp"), 
                        String.format(
                                "`%d/%d (%s%%)`",
                                xp,
                                reqXpLong,
                                new DecimalFormat("###.##").format(progress)
                        ), 
                        true
                )
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .setImage(String.format("attachment://%s", imageName));

        tc.sendMessage(levelEmbed.build()).addFile(image, imageName).queue();
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        User author = msg.getAuthor();

        if(bot.isBeta()){
            bot.getEmbedUtil().sendError(tc, author, "snuggle.info.level.purr_only");
            return;
        }

        if(!msg.getMentionedMembers().isEmpty()){
            if(msg.getMentionedMembers().get(0).getUser().isBot()){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "purr.info.level.bot");
                return;
            }

            sendLevelEmbed(tc, author, msg.getMentionedMembers().get(0));
            return;
        }

        if(msg.getMember() == null)
            return;

        sendLevelEmbed(tc, author, msg.getMember());
    }
}
