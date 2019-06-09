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
        name = "Slap",
        description = "Slap someone!",
        triggers = {"slap"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}slap @user [@user ...]")
        }
)
public class CmdSlap implements Command{

    private PurrBot manager;

    public CmdSlap(PurrBot manager){
        this.manager = manager;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to slap.");
            return;
        }

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            tc.sendMessage("Nuuuu... Why hurting me? T^T").queue();
            msg.addReaction("\uD83D\uDC94").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "\\*Holds arm of %s* NO! You won't hurt yourself.",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = manager.getHttpUtil().getImage(API.GIF_SLAP);

        String slapedMembers = members.stream().filter(
                member -> !member.equals(guild.getSelfMember())
        ).filter(
                member -> !member.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(slapedMembers.isEmpty()) return;

        tc.sendMessage(String.format(
                "%s Getting a slap-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s slaps you %s",
                        msg.getMember().getEffectiveName(),
                        slapedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(manager.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s slaps you %s",
                        msg.getMember().getEffectiveName(),
                        slapedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
