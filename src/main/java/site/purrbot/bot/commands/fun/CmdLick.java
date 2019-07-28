package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
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

        String link = bot.getMessageUtil().getRandomLickImg();

        String lickedMembers = members.stream().filter(
                mem -> !mem.equals(guild.getSelfMember())
        ).filter(
                mem -> !mem.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(lickedMembers.isEmpty())
            return;

        tc.sendMessage(String.format(
                "%s Getting a lick-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link.isEmpty()){
                message.editMessage(String.format(
                        "%s licks you %s",
                        member.getEffectiveName(),
                        lickedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s licks you %s",
                        member.getEffectiveName(),
                        lickedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
