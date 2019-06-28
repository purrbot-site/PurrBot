package site.purrbot.bot.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

@CommandDescription(
        name = "Leave",
        description =
                "Let the bot leave a Guild.\n" +
                "Use `--pm` to send an optional PM to Guild Owner",
        triggers = {"leave", "bye"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value =
                        "{p}leave <guildID>\n" +
                        "{p}leave <guildID> [--pm <message>]"
                )
        }
)
public class CmdLeave implements Command{

    private PurrBot bot;

    public CmdLeave(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();
        ShardManager shardManager = bot.getShardManager();

        String pm = null;

        String id = args.split(" ")[0];

        if(args.split(" ").length > 2 && args.toLowerCase().contains("--pm"))
            pm = args.split("--pm")[1];

        String finalPm = pm;
        shardManager.getGuildById(id).getOwner().getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage(String.format(
                        "I left your Discord `%s` for the following reason:\n" +
                        "```\n" +
                        "%s\n" +
                        "```",
                        shardManager.getGuildById(id).getName(),
                        finalPm == null ? "No reason given" : finalPm
                )).queue(message -> shardManager.getGuildById(id).leave().queue(),
                        throwable -> shardManager.getGuildById(id).leave().queue()),
                throwable -> {
                    tc.sendMessage("Couldn't send PM to user!").queue();
                    shardManager.getGuildById(id).leave().queue();
                }
        );
    }
}
