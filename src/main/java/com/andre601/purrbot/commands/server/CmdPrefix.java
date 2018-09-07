package com.andre601.purrbot.commands.server;

import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.constants.Errors;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class CmdPrefix implements Command{

    private static Map<Guild, String> guildPrefix = new HashMap<>();

    public static String getPrefix(Guild g){

        return DBUtil.getPrefix(g);
    }

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    public void currPrefix(Message msg, Guild g){
        msg.getTextChannel().sendMessage(String.format(
                "%s My prefix on this guild is `%s`",
                msg.getAuthor().getAsMention(),
                getPrefix(g)
        )).queue();
    }

    public void setPrefix(Message msg, Guild g, String prefix){
        if(prefix.equals(".")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s Why do you want to set the prefix to `.`?\n" +
                    "Use `%sprefix reset` to reset it to the default one.",
                    msg.getAuthor().getAsMention(),
                    getPrefix(g)
            )).queue();
            return;
        }

        DBUtil.setPrefix(prefix, g.getId());

        EmbedBuilder prefixSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Prefix set to `%s`",
                        prefix
                ))
                .setColor(Color.GREEN);

        msg.getChannel().sendMessage(prefixSet.build()).queue();
    }

    public void resetPrefix(Message msg, Guild g){
        String prefix = DBUtil.getPrefix(g);
        if(prefix.equals(".")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s There is no prefix set for this Guild!\n" +
                    "The default prefix is `.`!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }else {
            DBUtil.resetPrefix(g.getId());
            EmbedBuilder prefixReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Prefix was reset successfully!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(prefixReset.build()).queue();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        Guild g = e.getGuild();

        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage(Errors.NO_EMBED).queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length == 0){
            currPrefix(msg, e.getGuild());
            return;
        }

        if(PermUtil.userIsAdmin(msg)){
            tc.sendMessage(MessageFormat.format(
                    "{0} {1}",
                    msg.getAuthor().getAsMention(),
                    Errors.NOT_ADMIN
            )).queue();
            return;
        }

        switch (args[0].toLowerCase()){

            case "set":
                if(args.length == 1){
                    tc.sendMessage(String.format(
                            "%s Please provide a prefix!",
                            e.getAuthor().getAsMention()
                    )).queue();
                    break;
                }else{
                    setPrefix(msg, g, args[1]);
                    break;
                }
            case "reset":
                    resetPrefix(msg, g);
                    break;

            default:
                currPrefix(msg, g);
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
