package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;
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

    private PurrBot manager;

    public CmdMsg(PurrBot manager){
        this.manager = manager;
    }

    private boolean isValidChannel(String id){
        return manager.getShardManager().getTextChannelById(id) != null;
    }

    @Override
    public void execute(Message msg, String args) {
        List<String> split = new LinkedList<>();
        String channelID = args.split(" ")[0];
        Collections.addAll(split, args.split(" "));

        if(!isValidChannel(channelID)){
            manager.getEmbedUtil().sendError(
                    msg.getTextChannel(),
                    msg.getAuthor(),
                    "The provided ID was invalid. Make sure it's an actual channel-ID!"
            );
            return;
        }
        split.remove(0);

        if(split.isEmpty()){
            manager.getEmbedUtil().sendError(msg.getTextChannel(), msg.getAuthor(), "Please provide a message!");
            return;
        }

        manager.getShardManager().getTextChannelById(channelID).sendMessage(String.join(" ", split)).queue(
                message -> msg.addReaction("✅").queue()
        );
    }
}
