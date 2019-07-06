package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
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

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(String.format(
                        "\\*blushes* W-why do you lick me %s?",
                        msg.getMember().getAsMention()
                )).queue();
                msg.addReaction("\uD83D\uDE33").queue();
            }else{
                tc.sendMessage(String.format(
                        "H-hey! I never allowed you to lick me %s!",
                        msg.getMember().getAsMention()
                )).queue();
                msg.addReaction("\uD83D\uDE33").queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "I won't even ask how and *where* you lick yourself %s...",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = bot.getMessageUtil().getRandomLickImg();

        String lickedMembers = members.stream().filter(
                member -> !member.equals(guild.getSelfMember())
        ).filter(
                member -> !member.equals(msg.getMember())
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
                        msg.getMember().getEffectiveName(),
                        lickedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s licks you %s",
                        msg.getMember().getEffectiveName(),
                        lickedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
