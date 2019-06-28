package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

@CommandDescription(
        name = "Holo",
        description = "Gives a lovely image of Holo from the anime \"Spice and Wolf\"",
        triggers = {"holo", "spiceandwolf"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}holo")
        }
)
public class CmdHolo implements Command{

    private PurrBot bot;

    public CmdHolo(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();

        String link = bot.getHttpUtil().getImage(API.IMG_HOLO);

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        MessageEmbed holo = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Holo %s",
                        Emotes.BLOBHOLO.getEmote()
                ), link)
                .setImage(link)
                .build();

        tc.sendMessage(String.format(
                "%s Getting a cute/hot image of Holo...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(holo).queue());
    }
}
