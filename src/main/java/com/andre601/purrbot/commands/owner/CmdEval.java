package com.andre601.purrbot.commands.owner;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@CommandDescription(
        name = "Eval",
        description = "Evaluates code... or something like that",
        triggers = {"eval"},
        attributes = {
                @CommandAttribute(key = "owner"),
                @CommandAttribute(key = "usage", value = "eval <code>")
        }
)
public class CmdEval implements Command {

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        if(s.length() == 0){
            EmbedUtil.error(msg, "I need at least one argument!");
            return;
        }

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        se.put("jda", msg.getJDA());
        se.put("shardManager", ReadyListener.getShardManager());
        se.put("guild", msg.getGuild());
        se.put("channel", msg.getTextChannel());
        se.put("msg", msg);

        List<String> splitContent = new LinkedList<>();
        Collections.addAll(splitContent, msg.getContentRaw().split(" "));
        splitContent.remove(0);
        String statement = String.join(" ", splitContent);
        long startTime = System.currentTimeMillis();

        try{
            String result = se.eval(statement).toString();
            EmbedUtil.sendEvalEmbed(tc, String.format(
                    "**Input**:\n" +
                    "```java\n" +
                    "%s\n" +
                    "```\n" +
                    "**Result**:\n" +
                    "```java\n" +
                    "%s\n" +
                    "```",
                    statement,
                    result
            ),String.format(
                    "Evaluated in %sms",
                    (System.currentTimeMillis() - startTime)
            ), Color.GREEN);
        }catch (Exception ex){
            EmbedUtil.sendEvalEmbed(tc, String.format(
                    "**Error while evaluating following input**:\n" +
                    "```java\n" +
                    "%s\n" +
                    "```\n" +
                    "**Error**:\n" +
                    "```java\n" +
                    "%s\n" +
                    "```",
                    statement,
                    ex
            ), null, Color.RED);
        }
    }
}
