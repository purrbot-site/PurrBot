/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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

    private PurrBot bot;

    public CmdEmote(PurrBot bot){
        this.bot = bot;
    }

    private MessageEmbed emoteInfo(User user, Emote emote, @Nullable String link){

        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(user)
                .setTitle("Emote")
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

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.toLowerCase().contains("--search")){
            if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "I need permission to see the channel history!");
                return;
            }

            Message emoteMessage = tc.getIterableHistory().stream().limit(100).filter(
                    history -> !history.getEmotes().isEmpty()
            ).findFirst().orElse(null);

            if(emoteMessage == null){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't find an emote in past 100 messages.");
                return;
            }

            tc.sendMessage(emoteInfo(msg.getAuthor(), emoteMessage.getEmotes().get(0), emoteMessage.getJumpUrl())).queue();
            return;
        }

        if(msg.getEmotes().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide an `:emote:` or use `--search`");
            return;
        }

        tc.sendMessage(emoteInfo(msg.getAuthor(), msg.getEmotes().get(0), null)).queue();

    }
}
