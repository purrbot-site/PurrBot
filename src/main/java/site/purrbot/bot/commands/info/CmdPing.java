package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;

@CommandDescription(
        name = "Ping",
        description =
                "Pong?\n" +
                "Use `--api` to get the Websocket-Ping",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}ping\n" +
                        "{p}ping --api"
                )
        }
)
public class CmdPing implements Command{

    private PurrBot manager;

    public CmdPing(PurrBot manager){
        this.manager = manager;
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(args.toLowerCase().contains("--api")){
            tc.sendMessage(String.format(
                    "%s Checking ping to Discord-API...",
                    Emotes.ANIM_TYPING.getEmote()
            )).queue(message -> message.editMessage(String.format(
                    manager.getMessageUtil().getRandomApiPingMsg(),
                    msg.getJDA().getPing()
            )).queue());
            return;
        }

        tc.sendMessage(String.format(
                "%s Checking message ping...",
                Emotes.ANIM_TYPING.getEmote()
        )).queue(message -> message.editMessage(String.format(
                manager.getMessageUtil().getRandomPingMsg(),
                msg.getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS)
        )).queue());
    }
}
