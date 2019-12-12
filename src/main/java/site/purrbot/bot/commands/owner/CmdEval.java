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

package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@CommandDescription(
        name = "Eval",
        description = "Evaluates code... I guess.",
        triggers = {"eval"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = 
                        "{p}eval <code>"
                )
        }
)
public class CmdEval implements Command{

    private PurrBot bot;

    public CmdEval(PurrBot bot){
        this.bot = bot;
    }

    private void sendEvalEmbed(TextChannel tc, String input, String output, String footer, boolean success){
        String newMsg = input;

        String overflow = null;

        if(newMsg.length() > 2000){
            overflow = newMsg.substring(1999);
            newMsg = newMsg.substring(0, 1999);
        }

        EmbedBuilder embed = bot.getEmbedUtil().getEmbed()
                .setColor(success ? 0x00FF00 : 0xFF0000)
                .addField("Input", String.format(
                        "```java\n" +
                        "%s\n" +
                        "```",
                        newMsg
                ), false)
                .addField("Output", String.format(
                        "```java\n" +
                        "%s\n" +
                        "```",
                        output
                ), false)
                .setFooter(footer, null);

        tc.sendMessage(embed.build()).queue();
        if(overflow != null)
            sendEvalEmbed(tc, overflow, output, footer, success);
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "I need at least one argument!");
            return;
        }

        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");

        se.put("jda", msg.getJDA());
        se.put("shardManager", bot.getShardManager());
        se.put("guild", msg.getGuild());
        se.put("channel", msg.getTextChannel());
        se.put("msg", msg);

        List<String> split = new LinkedList<>();
        Collections.addAll(split, msg.getContentRaw().split(" "));
        split.remove(0);
        String statement = String.join(" ", split);

        long startTime = System.currentTimeMillis();

        try{
            String result = se.eval(statement).toString();

            sendEvalEmbed(tc, statement, result, String.format(
                    "Evaluated in %dms",
                    System.currentTimeMillis() - startTime
            ), true);
        }catch(ScriptException ex){
            sendEvalEmbed(tc, statement, ex.getMessage(), String.format(
                    "Evaluated in %dms",
                    System.currentTimeMillis() - startTime
            ), false);
        }
    }
}
