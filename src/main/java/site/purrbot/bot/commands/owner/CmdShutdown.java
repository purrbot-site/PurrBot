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

    private PurrBot bot;

    public CmdShutdown(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                .setDescription(bot.getMessageUtil().getRandomShutdownMsg())
                .setImage(bot.getMessageUtil().getRandomShutdownImg())
                .build();

        tc.sendMessage(embed).queue(message -> {
            logger.info("Disabling bot...");
            System.exit(0);
        });
    }
}
