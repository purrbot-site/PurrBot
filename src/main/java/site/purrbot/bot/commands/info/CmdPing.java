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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.time.temporal.ChronoUnit;

@CommandDescription(
        name = "Ping",
        description =
                "Pong I guess?",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}ping"
                )
        }
)
public class CmdPing implements Command{

    public CmdPing(){
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        tc.sendMessage(String.format(
                "%s Checking ping. Please wait...",
                Emotes.ANIM_TYPING.getEmote()
        )).queue(message -> msg.getJDA().getRestPing().queue((time) -> message.editMessage(String.format(
                "%s Edit message: `%dms`\n" +
                "%s Discord: `%sms`\n" +
                "%s RestAction: `%sms`",
                Emotes.EDIT.getEmote(),
                msg.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS),
                Emotes.DISCORD.getEmote(),
                msg.getJDA().getGatewayPing(),
                Emotes.DOWNLOAD.getEmote(),
                time
        )).queue(), throwable -> message.editMessage(String.format(
                "%s Edit message: `%dms`\n" +
                "%s Discord: `%sms`",
                Emotes.EDIT.getEmote(),
                msg.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS),
                Emotes.DISCORD.getEmote(),
                msg.getJDA().getGatewayPing()
        )).queue()));
    }
}
