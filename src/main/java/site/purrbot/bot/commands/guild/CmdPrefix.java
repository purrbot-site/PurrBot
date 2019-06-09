package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.awt.Color;
import java.text.MessageFormat;

@CommandDescription(
        name = "Prefix",
        description = "Set or reset a prefix",
        triggers = {"prefix"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}prefix set <prefix>\n" +
                        "{p}prefix reset")
        }
)
public class CmdPrefix implements Command{

    private PurrBot manager;

    public CmdPrefix(PurrBot manager){
        this.manager = manager;
    }

    /**
     * Gets the prefix from the database.
     *
     * @param  id
     *         The ID of the guild.
     *
     * @return The saved prefix of the guild.
     */
    private String getPrefix(String id){
        String prefix = manager.getDbUtil().getPrefix(id);

        if(prefix == null){
            prefix = ".";
            manager.getDbUtil().setPrefix(id, prefix);

            return prefix;
        }else{
            return prefix;
        }
    }

    private void update(Message msg, Type type, String value){

        EmbedBuilder embed = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setColor(0x00FF00);

        switch(type){
            case SET:
                embed.setDescription(String.format(
                        "Prefix changed to `%s`",
                        value
                ));

                manager.getDbUtil().setPrefix(msg.getGuild().getId(), value);
                break;

            case RESET:
                embed.setDescription("Prefix resetted!");
                manager.getDbUtil().setPrefix(msg.getGuild().getId(), ".");
        }

        msg.getTextChannel().sendMessage(embed.build()).queue();

    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length < 1){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                    "You need to provide arguments!\n" +
                    "Usage: `%sprefix <set <prefix>|reset>`",
                    manager.getDbUtil().getPrefix(guild.getId())
            ));
            return;
        }

        if(args[0].equalsIgnoreCase("reset")){
            update(msg, Type.RESET, null);
        }else
        if(args[0].equalsIgnoreCase("set")){
            if(args.length == 1){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "You need to provide a prefix!");
            }else{
                update(msg, Type.SET, args[1].toLowerCase());
            }
        }else{
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), MessageFormat.format(
                    "You need to provide valid arguments!\n" +
                    "Usage: `{0}prefix <set <prefix>|reset>`",
                    manager.getDbUtil().getPrefix(guild.getId())
            ));
        }
    }

    private enum Type{
        SET,
        RESET
    }
}
