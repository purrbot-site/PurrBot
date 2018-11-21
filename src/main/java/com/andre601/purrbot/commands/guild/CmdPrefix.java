package com.andre601.purrbot.commands.guild;

import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;
import java.text.MessageFormat;

@CommandDescription(
        name = "Prefix",
        description = "Set or reset a prefix",
        triggers = {"prefix"},
        attributes = {@CommandAttribute(key = "manage_server"), @CommandAttribute(key = "guild")}
)
public class CmdPrefix implements Command {

    public static String getPrefix(Guild g){
        String prefix = DBUtil.getPrefix(g);

        if(prefix == null){
            DBUtil.setPrefix(".", g.getId());
            return ".";
        }else{
            return prefix;
        }
    }

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    public static void currPrefix(Message msg, Guild g){
        msg.getTextChannel().sendMessage(String.format(
                "%s My prefix on this guild is `%s`",
                msg.getAuthor().getAsMention(),
                getPrefix(g)
        )).queue();
    }

    private void setPrefix(Message msg, Guild guild, String prefix){
        if(prefix.equals(".")){
            EmbedUtil.error(msg, MessageFormat.format(
                    "The default prefix of the bot is `.`\n" +
                    "Use `{0}prefix reset` to resez it back to `.`",
                    getPrefix(guild)
            ));
            return;
        }

        DBUtil.setPrefix(prefix, guild.getId());
        EmbedBuilder prefixSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Prefix set to `%s`",
                        prefix
                ))
                .setColor(Color.GREEN);

        msg.getChannel().sendMessage(prefixSet.build()).queue();
    }

    private void resetPrefix(Message msg, Guild g){
        String prefix = getPrefix(g);
        if(prefix.equals(".")){
            EmbedUtil.error(msg, "This guild doesn't have a own prefix set!");
        }else {
            DBUtil.resetPrefix(g.getId());
            EmbedBuilder prefixReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Prefix was reset successfully!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(prefixReset.build()).queue();
        }
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(args.length < 1){
            EmbedUtil.error(msg, MessageFormat.format(
                    "You need to provide arguments!\n" +
                    "Usage: `{0}prefix <set <prefix>|reset>`",
                    DBUtil.getPrefix(guild)
            ));
            return;
        }

        if(args[0].equalsIgnoreCase("reset")){
            resetPrefix(msg, guild);
        }else
        if(args[0].equalsIgnoreCase("set")){
            if(args.length == 1){
                EmbedUtil.error(msg, "You need to provide a prefix!");
            }else{
                setPrefix(msg, guild, args[1]);
            }
        }else{
            EmbedUtil.error(msg, MessageFormat.format(
                    "You need to provide valid arguments!\n" +
                            "Usage: `{0}prefix <set <prefix>|reset>`",
                    DBUtil.getPrefix(guild)
            ));
        }
    }
}
