package site.purrbot.bot.commands.nsfw;

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;
import site.purrbot.bot.constants.Emotes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Blowjob",
        description =
                "Get a gif of someone trying to get some *milk*",
        triggers = {"blowjob", "bj", "bjob"},
        attributes = {
                @CommandAttribute(key = "category", value = "nsfw"),
                @CommandAttribute(key = "usage", value = "{p}blowjob @user")
        }
)
public class CmdBlowjob implements Command{

    private Logger logger = (Logger)LoggerFactory.getLogger(CmdBlowjob.class);

    private PurrBot bot;

    public CmdBlowjob(PurrBot bot){
        this.bot = bot;
    }

    private ArrayList<String> queue = new ArrayList<>();

    private MessageEmbed getBJEmbed(Member requester, Member target, String link){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(String.format(
                        "%s is giving %s a blowjob!",
                        requester.getEffectiveName(),
                        target.getEffectiveName()
                ))
                .setImage(link)
                .build();
    }

    @Override
    public void execute(Message msg, String args){
        TextChannel tc = msg.getTextChannel();
        Member author = msg.getMember();
        Guild guild = msg.getGuild();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "Please mention the user you want to give a blowjob.");
            return;
        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(guild.getSelfMember())){
            if(bot.isBeta()){
                if(bot.getPermUtil().isSpecial(author.getUser().getId())){
                    tc.sendMessage(String.format(
                            "W-w-what?! N-no %s! Y-you have \\*Purr* for that. >////<",
                            author.getAsMention()
                    )).queue();
                    return;
                }
                tc.sendMessage(String.format(
                        "I-I'm confused now %s... How could you give me a blowjob?\n" +
                        "And I don't want one.",
                        author.getAsMention()
                )).queue();
                return;
            }else{
                if(bot.getPermUtil().isSpecial(author.getUser().getId())){
                    tc.sendMessage(String.format(
                            "Hehe. Okay sweetie... Let me get my *\"toys\"* And have some fun.\n" +
                            "\\*takes %s's hand and dissapears in her room*",
                            author.getAsMention()
                    )).queue();
                    return;
                }
                tc.sendMessage(String.format(
                        "\\*slaps %s* You perv!!! Get someone else to suck.",
                        author.getAsMention()
                )).queue();
                return;
            }
        }

        if(target.equals(msg.getMember())){
            tc.sendMessage(String.format(
                    "Oooooookay... If you want to suck yourself off, then I won't stop you %s.",
                    author.getAsMention()
            )).queue();
            return;
        }

        if(target.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, author.getUser(), "That would be like sticking your tongue in a socket.");
            return;
        }

        if(queue.contains(author.getUser().getId())){
            tc.sendMessage(String.format(
                    "Don't be that greedy %s and wait for the other request to be accepted or denied!",
                    author.getAsMention()
            )).queue();
            return;
        }

        queue.add(author.getUser().getId());
        tc.sendMessage(String.format(
                "Hey %s!\n" +
                "%s wants to give you a blowjob. Do you want that too?\n" +
                "Click  or  to either accept or deny the request.\n" +
                "\n" +
                "> **This request will time out in 1 minute!**",
                target.getAsMention(),
                author.getEffectiveName()
        )).queue(message -> message.addReaction("✅").queue(m -> message.addReaction("❌").queue(emote -> {
            EventWaiter waiter = bot.getWaiter();
            waiter.waitForEvent(
                    GuildMessageReactionAddEvent.class,
                    event -> {
                        MessageReaction.ReactionEmote emoji = event.getReactionEmote();
                        if(!emoji.getName().equals("✅") && !emoji.getName().equals("❌"))
                            return false;
                        if(event.getUser().isBot())
                            return false;
                        if(!event.getMember().equals(target))
                            return false;

                        return event.getMessageId().equals(message.getId());
                    },
                    event -> {
                        MessageReaction.ReactionEmote emoji = event.getReactionEmote();
                        if(emoji.getName().equals("❌")){
                            try{
                                message.delete().queue();
                            }catch(Exception ex){
                                logger.warn(String.format(
                                        "Couldn't delete own message for CmdBlowjob. Reason: %s",
                                        ex.getMessage()
                                ));
                            }

                            queue.remove(author.getUser().getId());
                            event.getChannel().sendMessage(String.format(
                                    "%s doesn't want to get sucked by you %s. :/",
                                    target.getEffectiveName(),
                                    author.getAsMention()
                            )).queue();
                            return;
                        }

                        if(emoji.getName().equals("✅")){
                            try{
                                message.delete().queue();
                            }catch(Exception ex){
                                logger.warn(String.format(
                                        "Couldn't delete own message for CmdBlowjob. Reason: %s",
                                        ex.getMessage()
                                ));
                            }

                            queue.remove(author.getUser().getId());
                            String link = bot.getHttpUtil().getImage(API.GIF_BLOW_JOB_LEWD);

                            event.getChannel().sendMessage(String.format(
                                    "%s accepted your request %s!",
                                    target.getEffectiveName(),
                                    author.getAsMention()
                            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));

                            if(link == null){
                                event.getChannel().sendMessage(String.format(
                                        "%s is giving %s a blowjob!",
                                        author.getEffectiveName(),
                                        target.getEffectiveName()
                                )).queue();
                                return;
                            }

                            event.getChannel().sendMessage(getBJEmbed(author, target, link)).queue();
                        }
                    }, 1, TimeUnit.MINUTES,
                    () -> {
                        try {
                            message.delete().queue();
                        }catch (Exception ex){
                            logger.warn(String.format(
                                    "Couldn't delete own message for CmdBlowjob. Reason: %s",
                                    ex.getMessage()
                            ));
                        }

                        queue.remove(author.getUser().getId());

                        tc.sendMessage(String.format(
                                "Looks like %s doesn't want a blowjob from you %s.",
                                target.getEffectiveName(),
                                author.getAsMention()
                        )).queue();
                    }
            );
        })));
    }
}
