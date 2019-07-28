package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@CommandDescription(
        name = "Message",
        description = "Sends a message as the bot to the specified channel",
        triggers = {"msg", "message", "send"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "{p}msg <channelID> <message>")
        }
)
public class CmdMsg implements Command{

    private PurrBot bot;

    public CmdMsg(PurrBot bot){
        this.bot = bot;
    }

    private boolean isValidChannel(String id){
        return bot.getShardManager().getTextChannelById(id) != null;
    }

    @Override
    public void execute(Message msg, String args) {
        List<String> split = new LinkedList<>();
        String channelID = args.split(" ")[0];
        Collections.addAll(split, args.split(" "));

        if(!isValidChannel(channelID)){
            bot.getEmbedUtil().sendError(
                    msg.getTextChannel(),
                    msg.getAuthor(),
                    "The provided ID was invalid. Make sure it's an actual channel-ID!"
            );
            return;
        }
        split.remove(0);

        if(split.isEmpty()){
            bot.getEmbedUtil().sendError(msg.getTextChannel(), msg.getAuthor(), "Please provide a message!");
            return;
        }

        TextChannel tc = bot.getShardManager().getTextChannelById(channelID);
        if(tc == null)
            return;

        tc.sendMessage(String.join(" ", split)).queue(
                message -> msg.addReaction("✅").queue()
        );
    }
}
