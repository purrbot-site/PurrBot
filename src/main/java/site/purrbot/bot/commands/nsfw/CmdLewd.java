package site.purrbot.bot.commands.nsfw;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Lewd",
        description =
        "Get images of naughty nekos. >w<\n" +
        "\n" +
        "You can use additional args in the command.\n" +
        "`--gif` for a gif\n" +
        "`--slide` for a slideshow with 30 images\n" +
        "Both arguments can be combined.",
        triggers = {"lewd"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value =
                        "{p}lewd\n" +
                        "{p}lewd --gif\n" +
                        "{p}lewd --slide\n" +
                        "{p}lewd --gif --slide")
        }
)
public class CmdLewd implements Command{

    private PurrBot manager;
    private Slideshow.Builder sBuilder;

    public CmdLewd(PurrBot manager){
        this.manager = manager;
        sBuilder = new Slideshow.Builder().setEventWaiter(manager.getWaiter()).setTimeout(1, TimeUnit.MINUTES);
    }

    private static List<String> lewdUserID = new ArrayList<>();

    @Override
    public void execute(Message msg, String args){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--slide")){
            if(lewdUserID.contains(msg.getAuthor().getId())){
                tc.sendMessage(String.format(
                        "%s You can only have one Slideshow at a time!",
                        msg.getAuthor().getAsMention()
                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            tc.sendTyping().queue();

            lewdUserID.add(msg.getAuthor().getId());
            String urls;
            if(args.toLowerCase().contains("--gif")){
                urls = manager.getHttpUtil().getImage(API.GIF_NEKO_LEWD, 20);
            }else{
                urls = manager.getHttpUtil().getImage(API.IMG_NEKO_LEWD, 20);
            }

            if(urls == null){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
                return;
            }

            Slideshow slideshow = sBuilder
                    .setUsers(msg.getAuthor(), guild.getOwner().getUser())
                    .setText("Lewd-slideshow!")
                    .setDescription(String.format(
                            "Use the reactions to navigate through the images!\n" +
                            "Only the author of the command (`%s`) and the Guild-Owner (`%s`) " +
                            "can use the navigation!\n" +
                            "\n" +
                            "__**Slideshows may take a while to update!**__",
                            msg.getAuthor().getAsTag().replace("`", "'"),
                            guild.getOwner().getUser().getAsTag().replace("`", "'")
                    ))
                    .setUrls(urls.replace("\"", "").split(","))
                    .setFinalAction(
                            message -> {
                                if(manager.getPermUtil().hasPermission(message.getTextChannel(), Permission.MESSAGE_MANAGE))
                                    message.delete().queue();

                                lewdUserID.remove(msg.getAuthor().getId());
                            }
                    ).build();
            slideshow.display(tc);
            return;
        }
        if(args.toLowerCase().contains("--gif")){
            String gifLink = manager.getHttpUtil().getImage(API.GIF_NEKO_LEWD);
            if(gifLink == null){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
                return;
            }

            EmbedBuilder lewdgif = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                    .setTitle(String.format(
                            "Lewd Neko %s",
                            Emotes.ANIM_WAGTAIL.getEmote()
                    ), gifLink)
                    .setImage(gifLink);

            tc.sendMessage(String.format(
                    "%s Getting a lewd neko-gif...",
                    Emotes.ANIM_LOADING.getEmote()
            )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(lewdgif.build()).queue());
            return;
        }

        String link = manager.getHttpUtil().getImage(API.IMG_NEKO_LEWD);
        if(link == null){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        EmbedBuilder lewd = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Lewd Neko %s",
                        Emotes.NEKOWO.getEmote()
                ), link)
                .setImage(link);

        tc.sendMessage(String.format(
                "%s Getting a lewd neko...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(lewd.build()).queue());
    }
}
