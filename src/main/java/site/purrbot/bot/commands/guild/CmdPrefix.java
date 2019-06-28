package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

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

    private void setPrefix(Message msg, String prefix){

        MessageEmbed embed = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setColor(0x00FF00)
                .setDescription(String.format(
                        "Prefix set to `%s`",
                        prefix
                ))
                .build();

        manager.setPrefix(msg.getGuild().getId(), prefix);

        msg.getTextChannel().sendMessage(embed).queue();
    }

    private void resetPrefix(Message msg){
        MessageEmbed embed = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setColor(0x00FF00)
                .setDescription("Prefix was changed back to `.`")
                .build();

        manager.setPrefix(msg.getGuild().getId(), ".");

        msg.getTextChannel().sendMessage(embed).queue();
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
                    manager.getPrefix(guild.getId())
            ));
            return;
        }

        if(args[0].equalsIgnoreCase("reset")){
            resetPrefix(msg);
        }else
        if(args[0].equalsIgnoreCase("set")){
            if(args.length == 1){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "You need to provide a prefix!");
            }else{
                setPrefix(msg, args[1].toLowerCase());
            }
        }else{
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), MessageFormat.format(
                    "You need to provide valid arguments!\n" +
                    "Usage: `{0}prefix <set <prefix>|reset>`",
                    manager.getPrefix(guild.getId())
            ));
        }
    }
}
