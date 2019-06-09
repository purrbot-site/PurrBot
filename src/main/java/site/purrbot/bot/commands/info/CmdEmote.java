package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import javax.annotation.Nullable;

@CommandDescription(
        name = "Emote",
        description =
                "Get info about a emote (custom emoji)\n" +
                "`--search` to search for an emote in the past 100 messages.",
        triggers = {"emote", "e"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}emote :emote:\n" +
                        "{p}emote --search"
                )
        }
)
public class CmdEmote implements Command{

    private PurrBot manager;

    public CmdEmote(PurrBot manager){
        this.manager = manager;
    }

    private MessageEmbed emoteInfo(User user, Emote emote, @Nullable String link){

        EmbedBuilder embed = manager.getEmbedUtil().getEmbed(user)
                .setTitle(String.format(
                        "Emote-Info: %s",
                        emote
                ))
                .addField("Name", String.format(
                        "`:%s:`",
                        emote.getName()
                ), true)
                .addField("ID", String.format(
                        "`%s`",
                        emote.getId()
                ), true)
                .addField("Guild", String.format(
                        "`%s`",
                        emote.getGuild() != null ? emote.getGuild().getName() : "Unknown Guild"
                ), true)
                .addField("Image", String.format(
                        "[`Link`](%s)",
                        emote.getImageUrl()
                ), true)
                .setThumbnail(emote.getImageUrl());

        if(link != null)
            embed.addField("Message", String.format(
                    "[`Link`](%s)",
                    link
            ), true);

        return embed.build();
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--search")){
            if(!manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "I need permission to see the channel history!");
                return;
            }

            Message emoteMessage = tc.getIterableHistory().stream().limit(100).filter(
                    history -> !history.getEmotes().isEmpty()
            ).findFirst().orElse(null);

            if(emoteMessage == null){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't find an emote in past 100 messages.");
                return;
            }

            tc.sendMessage(emoteInfo(msg.getAuthor(), emoteMessage.getEmotes().get(0), emoteMessage.getJumpUrl())).queue();
            return;
        }

        if(msg.getEmotes().isEmpty()){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide an `:emote:` or use `--search`");
            return;
        }

        tc.sendMessage(emoteInfo(msg.getAuthor(), msg.getEmotes().get(0), null)).queue();

    }
}
