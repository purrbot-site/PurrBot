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
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Cuddle",
        description = "Cuddle someone UwU",
        triggers = {"cuddle", "cuddles", "snuggle", "snuggles", "squeeze"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}cuddle @user [@user ...]")
        }
)
public class CmdCuddle implements Command{

    private PurrBot bot;

    public CmdCuddle(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to cuddle.");
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
                        "\\*snuggles up at %s*",
                        member.getAsMention()
                )).queue();
                msg.addReaction("❤").queue();
            }else{
                tc.sendMessage(String.format(
                        "\\*enjoys the cuddle from %s*",
                        member.getAsMention()
                )).queue();
                msg.addReaction("❤").queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Why do you cuddle yourself %s?\n" +
                    "You can cuddle me if you want... %s",
                    member.getAsMention(),
                    Emotes.VANILLABLUSH.getEmote()
            )).queue();
        }

        String link = bot.getHttpUtil().getImage(API.GIF_CUDDLE);

        String cuddledMembers = members.stream().filter(
                mem -> !mem.equals(guild.getSelfMember())
        ).filter(
                mem -> !mem.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(cuddledMembers.isEmpty())
            return;

        tc.sendMessage(String.format(
                "%s Getting a cuddle-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s cuddles with you %s",
                        member.getEffectiveName(),
                        cuddledMembers
                )).queue();
            }else{
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE).embed(
                        bot.getEmbedUtil().getEmbed().setDescription(String.format(
                                "%s cuddles with you %s",
                                member.getEffectiveName(),
                                cuddledMembers
                        )).setImage(link).build()
                ).queue();
            }
        });
    }
}
