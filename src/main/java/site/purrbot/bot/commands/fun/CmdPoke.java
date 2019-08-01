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
        name = "Poke",
        description = "Poke one or multiple people!",
        triggers = {"poke", "poking", "boop", "boops"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}poke @user [@user ...]")
        }
)
public class CmdPoke implements Command{

    private PurrBot bot;

    public CmdPoke(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to poke.");
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
                        "\\*covers face* W-why poking me %s? >~<",
                        member.getAsMention()
                )).queue();
                msg.addReaction("\uD83D\uDE16").queue();
            }else {
                tc.sendMessage("Nya! Do nu poke me! >-<").queue();
                msg.addReaction("\uD83D\uDE16").queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Why do you poke yourself %s?",
                    member.getAsMention()
            )).queue();
        }

        String link = bot.getHttpUtil().getImage(API.GIF_POKE);

        String pokedMembers = members.stream().filter(
                mem -> !mem.equals(guild.getSelfMember())
        ).filter(
                mem -> !mem.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(pokedMembers.isEmpty())
            return;

        tc.sendMessage(String.format(
                "%s Getting a poke-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s pokes you %s",
                        member.getEffectiveName(),
                        pokedMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s pokes you %s",
                        member.getEffectiveName(),
                        pokedMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
