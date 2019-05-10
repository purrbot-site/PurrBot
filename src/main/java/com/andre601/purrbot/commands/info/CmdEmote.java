package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

@CommandDescription(
        name = "Emote",
        description =
                "Get info about a emote (custom emoji)\n" +
                "Use `--search` to search for an emote in the past 100 messages.",
        triggers = {"emote", "e"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdEmote implements Command {

    private MessageEmbed getEmoteEmbed(User author, Message message, String link){
        Emote emote = message.getEmotes().get(0);

        return EmbedUtil.getEmbed(author)
                .setDescription(link == null ? "Here's some info about the requested emote. ^-^" : String.format(
                        "Found a [message](%s) with at least one emote! ^-^",
                        link
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
                        emote.getGuild() == null ?
                                "Unknown Guild" :
                                emote.getGuild().getName()
                ), true)
                .addField("Image", String.format(
                        "[`Link`](%s)",
                        emote.getImageUrl()
                ), false)
                .setImage(emote.getImageUrl())
                .build();
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--search")){
            if(!PermUtil.check(tc, Permission.MESSAGE_HISTORY)){
                EmbedUtil.error(msg, "I need permissions to see the message history of this channel!");
                return;
            }

            Message emoteMessage = tc.getIterableHistory().stream().limit(100).filter(
                    history -> !history.getEmotes().isEmpty()
            ).findFirst().orElse(null);

            if(emoteMessage == null){
                EmbedUtil.error(msg, "Wasn't able to find a emote in the previous 100 messages!");
                return;
            }

            tc.sendMessage(getEmoteEmbed(msg.getAuthor(), emoteMessage, emoteMessage.getJumpUrl())).queue();

            return;
        }

        if(msg.getEmotes().isEmpty()){
            EmbedUtil.error(msg, "Please provide an `:emote:` or use `-search` to search chat-history for one.");
            return;
        }

        tc.sendMessage(getEmoteEmbed(msg.getAuthor(), msg, null)).queue();

    }
}
