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

package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@CommandDescription(
        name = "Message",
        description = "Sends a message as the bot to the specified channel",
        triggers = {"msg", "message", "send"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}msg <channelId> <message>"),
                @CommandAttribute(key = "help", value = "{p}msg <channelId> <message>")
        }
)
public class CmdMsg implements Command{

    private final PurrBot bot;

    public CmdMsg(PurrBot bot){
        this.bot = bot;
    }

    private boolean isValidChannel(String id){
        return bot.getShardManager().getTextChannelById(id) != null;
    }

    @Override
    public void execute(Message msg, String args) {
        List<String> split = new LinkedList<>();
        String channelID = args.split(" ")[0];
        Collections.addAll(split, args.split(" "));

        if(!isValidChannel(channelID)){
            bot.getEmbedUtil().sendError(
                    msg.getTextChannel(),
                    msg.getAuthor(),
                    "The provided ID was invalid. Make sure it's an actual channel-ID!"
            );
            return;
        }
        split.remove(0);

        if(split.isEmpty()){
            bot.getEmbedUtil().sendError(msg.getTextChannel(), msg.getAuthor(), "Please provide a message!");
            return;
        }

        TextChannel tc = bot.getShardManager().getTextChannelById(channelID);
        if(tc == null)
            return;

        tc.sendMessage(String.join(" ", split)).queue(
                message -> msg.addReaction("✅").queue()
        );
    }
}
