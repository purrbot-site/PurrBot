package site.purrbot.bot.commands.nsfw;

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Fuck",
        description =
                "Wanna fuck someone?\n" +
                "Mention a user, to send a request.\n" +
                "The mentioned user can accept it by clicking on the ✅, deny it by clicking on ❌ or let it time out.",
        triggers = {"fuck", "sex"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}fuck @user")
        }
)
public class CmdFuck implements Command{

    private Logger logger = (Logger)LoggerFactory.getLogger(CmdFuck.class);

    private PurrBot bot;

    public CmdFuck(PurrBot bot){
        this.bot = bot;
    }

    private ArrayList<String> alreadyInQueue = new ArrayList<>();

    private int getRandomPercent(){
        return bot.getRandom().nextInt(10);
    }

    private EmbedBuilder getFuckEmbed(Member member1, Member member2, String url){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(String.format(
                        "%s and %s are having sex!",
                        member1.getEffectiveName(),
                        member2.getEffectiveName()
                ))
                .setImage(url);
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        Member author = msg.getMember();
        Guild guild = msg.getGuild();

        if(author == null)
            return;

        if(msg.getMentionedUsers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "Please mention a user to fuck.");
            return;
        }

        Member member = msg.getMentionedMembers().get(0);

        if(member.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(String.format(
                        "\\*Slaps %s* N-NO! Not with me!",
                        author.getAsMention()
                )).queue();
                return;
            }
            if(bot.getPermUtil().isSpecial(msg.getAuthor().getId())){
                int random = getRandomPercent();

                if(random >= 1 && random <= 3) {
                    tc.sendMessage(String.format(
                            bot.getMessageUtil().getRandomAcceptFuckMsg(),
                            author.getAsMention()
                    )).queue();
                    return;
                }else{
                    tc.sendMessage(String.format(
                            bot.getMessageUtil().getRandomDenyFuckMsg(),
                            author.getAsMention()
                    )).queue();
                    return;
                }
            }else{
                tc.sendMessage(String.format(
                        "\\*Slaps %s* Nononononono! Not with me!",
                        msg.getAuthor().getAsMention()
                )).queue();
                return;
            }
        }

        if(member.equals(author)){
            tc.sendMessage(String.format(
                    "How can you actually fuck yourself %s?! (And no. Masturbation is not a valid answer)",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        if(member.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "You can't fuck with bots! >-<");
            return;
        }

        if(alreadyInQueue.contains(author.getId())){
            tc.sendMessage(String.format(
                    "You already have an open request for someone to fuck with you %s!\n" +
                    "Please wait until the person accepts or denies it, or the request times out.",
                    author.getAsMention()
            )).queue();
            return;
        }

        alreadyInQueue.add(author.getId());
        tc.sendMessage(String.format(
                "Hey %s!\n" +
                "%s wants to have sex with you. Do you want that too?\n" +
                "Click ✅ to accept or ❌ to deny the request.\n" +
                "\n" +
                "> **This request will time out in 1 minute!**",
                member.getAsMention(),
                author.getEffectiveName()
        )).queue(message -> message.addReaction("✅").queue(m -> message.addReaction("❌").queue(emote -> {
                EventWaiter waiter = bot.getWaiter();
                waiter.waitForEvent(
                        GuildMessageReactionAddEvent.class,
                        ev -> {
                            MessageReaction.ReactionEmote emoji = ev.getReactionEmote();
                            if(!emoji.getName().equals("✅") && !emoji.getName().equals("❌")) return false;
                            if(ev.getUser().isBot()) return false;
                            if(!ev.getMember().equals(member)) return false;

                            return ev.getMessageId().equals(message.getId());
                        },
                        ev -> {
                            if(ev.getReactionEmote().getName().equals("❌")){
                                try{
                                    if(message != null) 
                                        message.delete().queue();
                                }catch(Exception ex){
                                    logger.warn(String.format(
                                            "Couldn't delete own message for CmdFuck. Reason: %s",
                                            ex.getMessage()
                                    ));
                                }

                                alreadyInQueue.remove(author.getId());

                                ev.getChannel().sendMessage(String.format(
                                        "%s doesn't want to lewd with you %s. >.<",
                                        member.getEffectiveName(),
                                        author.getAsMention()
                                )).queue();
                                return;
                            }

                            if(ev.getReactionEmote().getName().equals("✅")){
                                try{
                                    if(message != null)
                                        message.delete().queue();
                                }catch(Exception ex){
                                    logger.warn(String.format(
                                            "Couldn't delete own message for CmdFuck. Reason: %s",
                                            ex.getMessage()
                                    ));
                                }

                                alreadyInQueue.remove(author.getId());

                                String link = bot.getHttpUtil().getImage(API.GIF_FUCK_LEWD);

                                ev.getChannel().sendMessage(String.format(
                                        "%s accepted your invite %s! 0w0",
                                        member.getEffectiveName(),
                                        author.getAsMention()
                                )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                                if(link == null){
                                    ev.getChannel().sendMessage(String.format(
                                            "%s and %s are having sex!",
                                            author.getEffectiveName(),
                                            member.getEffectiveName()
                                    )).queue();
                                    return;
                                }

                                ev.getChannel().sendMessage(
                                        getFuckEmbed(author, member, link).build()
                                ).queue();

                            }
                        }, 1, TimeUnit.MINUTES,
                        () -> {
                            try {
                                if(message != null)
                                    message.delete().queue();
                            }catch (Exception ex){
                                logger.warn(String.format(
                                        "Couldn't delete own message for CmdFuck. Reason: %s",
                                        ex.getMessage()
                                ));
                            }

                            alreadyInQueue.remove(author.getId());

                            tc.sendMessage(String.format(
                                    "Looks like %s doesn't want to have sex with you %s. ._.",
                                    member.getEffectiveName(),
                                    author.getAsMention()
                            )).queue();
                        }
            );
        })));
    }
}
