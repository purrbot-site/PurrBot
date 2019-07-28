package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
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

        Member member = msg.getMember();
        if(member == null)
            return;

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(String.format(
                        "\\*enjoys the pat from %s",
                        member.getAsMention()
                )).queue();
                msg.addReaction("❤").queue();
            }else {
                tc.sendMessage("\\*purr™*").queue();
                msg.addReaction("❤").queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Don't you have a neko to pat %s? \\*points to herself*",
                    member.getAsMention()
            )).queue();
        }

        String link = bot.getHttpUtil().getImage(API.GIF_PAT);

        String pattetMembers = members.stream().filter(
                mem -> !mem.equals(guild.getSelfMember())
        ).filter(
                mem -> !mem.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(pattetMembers.isEmpty())
            return;

        tc.sendMessage(String.format(
                "%s Getting a pat-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s pats you %s",
                        member.getEffectiveName(),
                        pattetMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s pats you %s",
                        member.getEffectiveName(),
                        pattetMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
