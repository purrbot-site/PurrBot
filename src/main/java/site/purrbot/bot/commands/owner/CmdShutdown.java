package site.purrbot.bot.commands.owner;

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

@CommandDescription(
        name = "Shutdown",
        description = "Disables the bot",
        triggers = {"shutdown", "sleep"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}shutdown")
        }
)
public class CmdShutdown implements Command{

    private Logger logger = (Logger)LoggerFactory.getLogger(CmdShutdown.class);

    private PurrBot manager;

    public CmdShutdown(PurrBot manager){
        this.manager = manager;
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        MessageEmbed embed = manager.getEmbedUtil().getEmbed()
                .setDescription(manager.getMessageUtil().getRandomShutdownMsg())
                .setImage(manager.getMessageUtil().getRandomShutdownImg())
                .build();

        tc.sendMessage(embed).queue(message -> {
            logger.info("Disabling bot...");
            System.exit(0);
        });
    }
}
