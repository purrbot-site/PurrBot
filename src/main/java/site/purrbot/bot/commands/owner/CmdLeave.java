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

package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandDescription(
        name = "Leave",
        description =
                "Let the bot leave a Guild.\n" +
                "Use `--pm` to send an optional PM to Guild Owner",
        triggers = {"leave", "bye"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}leave <guildId> [--pm <message>]"),
                @CommandAttribute(key = "help", value = "{p}leave <guildId> [--pm <message>]")
        }
)
public class CmdLeave implements Command{

    Pattern pattern = Pattern.compile("--pm (?<message>.+)", Pattern.DOTALL);
    
    private final PurrBot bot;

    public CmdLeave(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        ShardManager shardManager = bot.getShardManager();

        String pm = null;
    
        String s = msg.getContentRaw();
        Matcher matcher = pattern.matcher(s);
        if(matcher.find())
            pm = matcher.group("message");

        String id = args[0];

        String finalPm = pm;
        Guild targetGuild = shardManager.getGuildById(id);

        if(targetGuild == null)
            return;

        Member owner = targetGuild.getOwner();
        if(owner == null){
            targetGuild.leave().queue();
            return;
        }

        owner.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(String.format(
                        "I left your Discord `%s` for the following reason:\n" +
                        "```\n" +
                        "%s\n" +
                        "```",
                        targetGuild.getName(),
                        finalPm == null ? "No reason given" : finalPm
                )))
                .queue(
                        message -> targetGuild.leave().queue(),
                        throwable -> targetGuild.leave().queue()
                );
        
        targetGuild.getOwner().getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage(String.format(
                        "I left your Discord `%s` for the following reason:\n" +
                        "```\n" +
                        "%s\n" +
                        "```",
                        targetGuild.getName(),
                        finalPm == null ? "No reason given" : finalPm
                )).queue(message -> targetGuild.leave().queue(),
                        throwable -> targetGuild.leave().queue()),
                throwable -> {
                    tc.sendMessage("Couldn't send PM to user!").queue();
                    targetGuild.leave().queue();
                }
        );
    }
}
