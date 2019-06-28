package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Kiss",
        description = "Lets you share some kisses with others!",
        triggers = {"kiss", "love", "kissu"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}kiss @user [@user ...]")
        }
)
public class CmdKiss implements Command{

    private PurrBot bot;

    public CmdKiss(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to kiss.");
            return;
        }

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        Member purr = members.stream().filter(member -> member.getUser().getId().equals(IDs.PURR.getId()))
                .findFirst().orElse(null);
        Member snuggle = members.stream().filter(member -> member.getUser().getId().equals(IDs.SNUGGLE.getId()))
                .findFirst().orElse(null);

        if(bot.isBeta()){
            if(members.contains(guild.getSelfMember())){
                tc.sendMessage(String.format(
                        "Wha-? Okay. B-but only on my cheeck %s. \\*Lets you kiss her cheek*",
                        msg.getMember().getAsMention()
                )).queue();
            }else
            if(purr != null && members.contains(purr)){
                if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                    tc.sendMessage(String.format(
                            "W-why do you kiss my sister through my help %s? G-go and kiss her yourself... %s",
                            msg.getMember().getAsMention(),
                            Emotes.VANILLABLUSH.getEmote()
                    )).queue();
                }else{
                    tc.sendMessage(String.format(
                            "No! N-no kissing of my sister through my help %s!",
                            msg.getMember().getAsMention()
                    )).queue();
                }
            }
        }else{
            if(members.contains(guild.getSelfMember())){
                if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                    tc.sendMessage(String.format(
                            "\\*Enjoys the kiss from %s*",
                            msg.getMember().getAsMention()
                    )).queue(message -> {
                        MessageEmbed kiss = bot.getEmbedUtil().getEmbed().setImage(
                                bot.getMessageUtil().getRandomKissImg()
                        ).build();

                        message.editMessage(kiss).queue();
                        msg.addReaction("\uD83D\uDC8B").queue();
                    });
                }else{
                    tc.sendMessage(String.format(
                            "Okay. But I only allow you to kiss my cheek %s. \\*Lets you kiss her cheek*",
                            msg.getMember().getAsMention()
                    )).queue();
                }
            }else
            if(snuggle != null && members.contains(snuggle)){
                tc.sendMessage(String.format(
                        "No kissing of my little sister with my help %s!",
                        msg.getMember().getAsMention()
                )).queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "I have no idea, how you can actually kiss yourself %s... With a mirror?",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String kissedMembers = members.stream()
                .filter(member -> !member.equals(guild.getSelfMember()))
                .filter(member -> !member.equals(msg.getMember()))
                .filter(member -> !member.equals(purr))
                .filter(member -> !member.equals(snuggle))
                .map(Member::getEffectiveName).collect(Collectors.joining(", "));

        String link = bot.getHttpUtil().getImage(API.GIF_KISS);

        if(kissedMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a kiss-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s kisses you %s",
                        msg.getMember().getEffectiveName(),
                        kissedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s kisses you %s",
                        msg.getMember().getEffectiveName(),
                        kissedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
