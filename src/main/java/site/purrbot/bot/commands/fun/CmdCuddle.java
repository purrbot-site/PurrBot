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
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Cuddle",
        description = "Cuddle someone UwU",
        triggers = {"cuddle", "cuddles", "snuggle", "snuggles"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}cuddle @user [@user ...]")
        }
)
public class CmdCuddle implements Command{

    private PurrBot manager;

    public CmdCuddle(PurrBot manager){
        this.manager = manager;
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to cuddle.");
            return;
        }

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage(String.format(
                    "\\*enjoys the cuddle from %s*",
                    msg.getMember().getAsMention()
            )).queue();
            msg.addReaction("❤").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Why do you cuddle yourself %s?\n" +
                    "You can cuddle me if you want... %s",
                    msg.getMember().getAsMention(),
                    Emotes.VANILLABLUSH.getEmote()
            )).queue();
        }

        String link = manager.getHttpUtil().getImage(API.GIF_CUDDLE);

        String cuddledMembers = members.stream().filter(
                member -> !member.equals(guild.getSelfMember())
        ).filter(
                member -> member.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(cuddledMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a cuddle-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s cuddles with you %s",
                        msg.getMember().getEffectiveName(),
                        cuddledMembers
                )).queue();
            }else{
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(
                        manager.getEmbedUtil().getEmbed().setDescription(String.format(
                                "%s cuddles with you %s",
                                msg.getMember().getEffectiveName(),
                                cuddledMembers
                        )).setImage(link).build()
                ).queue();
            }
        });
    }
}
