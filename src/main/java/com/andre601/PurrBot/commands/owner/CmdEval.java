package com.andre601.PurrBot.commands.owner;

import com.andre601.PurrBot.util.PermUtil;
import com.andre601.PurrBot.commands.Command;
import com.andre601.PurrBot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdEval implements Command {



    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        Message msg = e.getMessage();
        TextChannel tc = e.getTextChannel();

        if(!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(PermUtil.isCreator(msg)){
            if(args.length == 0){
                e.getTextChannel().sendMessage("I need at least 1 argument.").queue(del -> del.delete()
                        .queueAfter(5, TimeUnit.SECONDS));
                return;
            }

            ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
            se.put("event", e);
            se.put("jda", e.getJDA());
            se.put("guild", e.getGuild());
            se.put("channel", e.getChannel());
            se.put("msg", msg);
            se.put("embed", new net.dv8tion.jda.core.EmbedBuilder());

            List<String> splitContent = new LinkedList<>();
            Collections.addAll(splitContent, msg.getContentRaw().split(" "));
            splitContent.remove(0);
            String statement = String.join(" ", splitContent);
            long startTime = System.currentTimeMillis();

            try{
                String result = se.eval(statement).toString();
                EmbedUtil.sendEvalEmbed(e.getTextChannel(), String.format(
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
                EmbedUtil.sendEvalEmbed(e.getTextChannel(), String.format(
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

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
