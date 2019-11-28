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

package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Lick",
        description = "Lets you lick one or multiple users.",
        triggers = {"lick"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}lick @user [@user ...]")
        }
)
public class CmdLick implements Command{

    private PurrBot bot;

    public CmdLick(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to lick.");
            return;
        }

        Member member = msg.getMember();
        if(member == null)
            return;

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(String.format(
                        "\\*blushes* W-why do you lick me %s?",
                        member.getAsMention()
                )).queue();
                msg.addReaction("\uD83D\uDE33").queue();
            }else{
                if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                    tc.sendMessage("B-but Sweetie... Not in public \\*blushes*").queue();
                }else {
                    tc.sendMessage(String.format(
                            "H-hey! I never allowed you to lick me %s!",
                            member.getAsMention()
                    )).queue();
                    msg.addReaction("\uD83D\uDE33").queue();
                }
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "I won't even ask how and *where* you lick yourself %s...",
                    member.getAsMention()
            )).queue();
        }

        String lickedMembers = members.stream()
                .filter(mem -> !mem.equals(guild.getSelfMember()))
                .filter(mem -> !mem.equals(msg.getMember()))
                .map(Member::getEffectiveName)
                .collect(Collectors.joining(", "));

        if(lickedMembers.isEmpty())
            return;
    
        String link = bot.getHttpUtil().getImage(API.GIF_LICK);

        tc.sendMessage(String.format(
                "%s Getting a lick-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s licks you %s",
                        MarkdownSanitizer.escape(member.getEffectiveName()),
                        MarkdownSanitizer.escape(lickedMembers)
                )).queue();
            }else{
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE)
                        .embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                                "%s licks you %s",
                                MarkdownSanitizer.escape(member.getEffectiveName()),
                                MarkdownSanitizer.escape(lickedMembers)
                        )).setImage(link).build()).queue();
            }
        });
    }
}
