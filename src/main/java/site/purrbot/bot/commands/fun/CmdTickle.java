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
        name = "Tickle",
        description = "Tickle someone until he/she laughs",
        triggers = {"tickle"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}tickle @user [@user ...]")
        }
)
public class CmdTickle implements Command{

    private PurrBot bot;

    public CmdTickle(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention at least one user to tickle.");
            return;
        }

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage("\\*can't hold back and starts laughing*").queue();
                msg.addReaction("\uD83D\uDE02").queue();
            }else {
                tc.sendMessage("N-no... Please I... I c-can't \\*starts laughing*").queue();
                msg.addReaction("\uD83D\uDE02").queue();
            }
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(String.format(
                    "Alright... If you really want to tickle yourself... \\*tickles %s*",
                    msg.getMember().getAsMention()
            )).queue();
        }

        String link = bot.getHttpUtil().getImage(API.GIF_TICKLE);
        String tickledMembers = members.stream().filter(
                member -> !member.equals(guild.getSelfMember())
        ).filter(
                member -> !member.equals(msg.getMember())
        ).map(Member::getEffectiveName).collect(Collectors.joining(", "));

        if(tickledMembers.isEmpty())
            return;

        tc.sendMessage(String.format(
                "%s Getting a tickle-gif...",
                Emotes.ANIM_LOADING.getEmote()
        )).queue(message -> {
            if(link == null){
                message.editMessage(String.format(
                        "%s tickles you %s",
                        msg.getMember().getEffectiveName(),
                        tickledMembers
                )).queue();
            }else{
                message.editMessage(
                        EmbedBuilder.ZERO_WIDTH_SPACE
                ).embed(bot.getEmbedUtil().getEmbed().setDescription(String.format(
                        "%s tickles you %s",
                        msg.getMember().getEffectiveName(),
                        tickledMembers
                )).setImage(link).build()).queue();
            }
        });
    }
}
