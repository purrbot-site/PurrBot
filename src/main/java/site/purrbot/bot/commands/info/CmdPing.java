package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

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

    public CmdPing(){
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        tc.sendMessage(String.format(
                "%s Checking ping. Please wait...",
                Emotes.ANIM_TYPING.getEmote()
        )).queue(message -> msg.getJDA().getRestPing().queue((time) -> message.editMessage(String.format(
                "%s Edit message: `%dms`\n" +
                "%s Discord: `%sms`\n" +
                "%s RestAction: `%sms`",
                Emotes.ANIM_CURSOR.getEmote(),
                msg.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS),
                Emotes.DISCORD.getEmote(),
                msg.getJDA().getGatewayPing(),
                Emotes.DOWNLOAD.getEmote(),
                time
        )).queue(), throwable -> message.editMessage(String.format(
                "%s Edit message: `%dms`\n" +
                "%s Discord: `%sms`",
                Emotes.ANIM_CURSOR.getEmote(),
                msg.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS),
                Emotes.DISCORD.getEmote(),
                msg.getJDA().getGatewayPing()
        )).queue()));
    }
}
