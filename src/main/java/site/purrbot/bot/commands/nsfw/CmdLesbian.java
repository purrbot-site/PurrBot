package site.purrbot.bot.commands.nsfw;

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
        name = "Lesbian",
        description = "Gives you a gif of lesbians",
        triggers = {"lesbian", "les"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}lesbian")
        }
)
public class CmdLesbian implements Command{

    private PurrBot manager;

    public CmdLesbian(PurrBot manager){
        this.manager = manager;
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        String link = manager.getHttpUtil().getImage(API.GIF_LES_LEWD);

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(link == null){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder les = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle("Lesbian", link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting hot lesbians...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(
                EmbedBuilder.ZERO_WIDTH_SPACE
        ).embed(les.build()).queue());
    }
}
