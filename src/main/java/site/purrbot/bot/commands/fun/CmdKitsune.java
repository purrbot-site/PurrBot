package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

@CommandDescription(
        name = "Kitsune",
        description = "Gives you a image of a kitsune (foxgirl)",
        triggers = {"kitsune", "foxgirl"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}kitsune")
        }
)
public class CmdKitsune implements Command{

    private PurrBot bot;

    public CmdKitsune(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        String link = bot.getHttpUtil().getImage(API.IMG_KITSUNE);

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder gecg = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Kitsune %s",
                        Emotes.ANIM_SHIROTAILWAG.getEmote()
                ), link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a cute kitsune...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(
                EmbedBuilder.ZERO_WIDTH_SPACE
        ).embed(gecg.build()).queue());
    }
}
