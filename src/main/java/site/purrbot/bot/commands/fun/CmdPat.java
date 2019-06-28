package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Pat",
        description = "Lets you pat someone.",
        triggers = {"pat", "patting", "pet"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}pat @user [@user ...]")
        }
)
public class CmdPat implements Command{

    private PurrBot bot;

    public CmdPat(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to pat.");
            return;
        }

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("\\*purr™*").queue();
            msg.addReaction("❤").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Don't you have a neko to pat %s? \\*points to herself*",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = bot.getHttpUtil().getImage(API.GIF_PAT);

        String pattetMembers = members.stream().filter(
                member -> !member.equals(guild.getSelfMember())
        ).filter(
                member -> !member.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(pattetMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a pat-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s pats you %s",
                        msg.getMember().getEffectiveName(),
                        pattetMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s pats you %s",
                        msg.getMember().getEffectiveName(),
                        pattetMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
