package com.andre601.purrbot.commands.guild;

import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
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

    /**
     * Gets the prefix from the database.
     *
     * @param  guild
     *         A {@link net.dv8tion.jda.core.entities.Guild Guild object} used for identification.
     * @return The saved prefix of the guild.
     */
    private static String getPrefix(Guild guild){
        String prefix = DBUtil.getPrefix(guild);

        if(prefix == null){
            DBUtil.setPrefix(".", guild.getId());
            return ".";
        }else{
            return prefix;
        }
    }

    /**
     * Sets the prefix to the provided one.
     * The prefix won't be changed, if {@param prefix} is the default one ({@code .}).
     *
     * @param msg
     *        Messages that is used for the response.
     * @param guild
     *        A {@link net.dv8tion.jda.core.entities.Guild Guild object} for identification.
     * @param prefix
     *        The String that should be saved.
     */
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

    /**
     * Resets the prefix to the default one ({@code .}).
     * It won't reset it, when it's already the default one.
     *
     * @param msg
     *        Messages that is used for the response.
     * @param guild
     *        A {@link net.dv8tion.jda.core.entities.Guild Guild object} for identification.
     */
    private void resetPrefix(Message msg, Guild guild){
        String prefix = getPrefix(guild);
        if(prefix.equals(".")){
            EmbedUtil.error(msg, "This guild doesn't have a own prefix set!");
        }else {
            DBUtil.resetPrefix(guild.getId());
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

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
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
