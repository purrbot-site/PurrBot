package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
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
        name = "Neko",
        description =
                "Gives you a lovely neko (catgirl)\n" +
                "\n" +
                "You can use additional args in the command.\n" +
                "`--gif` for a gif\n" +
                "`--slide` for a slideshow with 30 images\n" +
                "Both arguments can be combined.",
        triggers = {"neko", "catgirl"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value =
                        "{p}neko\n" +
                        "{p}neko --gif\n" +
                        "{p}neko --slide\n" +
                        "{p}neko --gif --slide")
        }
)
public class CmdNeko implements Command{

    private PurrBot manager;
    private Slideshow.Builder sBuilder;

    public CmdNeko(PurrBot manager){
        this.manager = manager;
        sBuilder = new Slideshow.Builder().setEventWaiter(manager.getWaiter()).setTimeout(1, TimeUnit.MINUTES);
    }

    private static List<String> nekoUserID = new ArrayList<>();

    @Override
    public void execute(Message msg, String args) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--slide")){
            if(nekoUserID.contains(msg.getAuthor().getId())){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(),
                        "Only one slideshow per user!\n" +
                        "Please use or close your other one."
                );
                return;
            }
            tc.sendTyping().queue();

            nekoUserID.add(msg.getAuthor().getId());
            String urls;
            if(args.toLowerCase().contains("--gif")){
                urls = manager.getHttpUtil().getImage(API.GIF_NEKO, 20);
            }else{
                urls = manager.getHttpUtil().getImage(API.IMG_NEKO, 20);
            }

            if(urls == null){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
                return;
            }

            Slideshow slideshow = sBuilder
                    .setUsers(msg.getAuthor(), guild.getOwner().getUser())
                    .setText("Neko-slideshow!")
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
                    .setFinalAction(message -> {
                        if(manager.getPermUtil().hasPermission(message.getTextChannel(), Permission.MESSAGE_MANAGE))
                            message.clearReactions().queue();

                        nekoUserID.remove(msg.getAuthor().getId());
                    })
                    .build();
            slideshow.display(tc);
            return;
        }

        if(args.toLowerCase().contains("--gif")){
            String link = manager.getHttpUtil().getImage(API.GIF_NEKO);
            if(link == null){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
                return;
            }
            EmbedBuilder nekogif = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                    .setTitle(String.format(
                            "Neko %s",
                            Emotes.ANIM_WAGTAIL.getEmote()
                    ), link)
                    .setImage(link);

            tc.sendMessage(String.format(
                    "%s Getting a cute neko-gif...",
                    Emotes.ANIM_LOADING.getEmote()
            )).queue(message -> message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(nekogif.build()).queue());
            return;
        }

        String link = manager.getHttpUtil().getImage(API.IMG_NEKO);

        if(link == null){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't reach the API! Try again later.");
            return;
        }

        tc.sendMessage(String.format(
                "%s Getting a cute neko...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            EmbedBuilder neko = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                    .setTitle(String.format(
                            "Neko %s",
                            Emotes.NEKOWO.getEmote()
                    ), link)
                    .setImage(link);

            if(link.equals("https://cdn.nekos.life/v3/sfw/img/neko/neko_079.jpg")){
                if(manager.isBeta()){
                    neko.setDescription("That is me! >w<");
                    message.addReaction("❤").queue();
                }else{
                    Emote snuggle = manager.getShardManager().getEmoteById(Emotes.SNUGGLE.getId());
                    neko.setDescription("That is my little sister!");

                    if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_EXT_EMOJI))
                        message.addReaction(snuggle).queue();
                }
            }else
            if(link.equals("https://cdn.nekos.life/v3/sfw/img/neko/neko_139.png")){
                if(manager.isBeta()){
                    Emote purr = manager.getShardManager().getEmoteById(Emotes.PURR.getId());
                    neko.setDescription("That is my big sister!");

                    if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_EXT_EMOJI))
                        message.addReaction(purr).queue();
                }else{
                    neko.setDescription("T-that is me! OwO");
                    message.addReaction("❤").queue();
                }
            }

            //  Editing the message to add the image ("should" prevent issues with empty embeds)
            message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(neko.build()).queue();
        });
    }
}
