package net.andre601.commands.fun;

import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.util.constants.IDs;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.HttpUtil;
import net.andre601.util.PermUtil;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class CmdKiss implements Command {

    //  This sends a message, if it is executed.
    public void usage(Message msg){
        msg.getTextChannel().sendMessage(String.format(
                "%s Please mention a user at the end of the command to kiss him/her!\n" +
                "Example: `%skiss @*Purr*#6875`",
                msg.getAuthor().getAsMention(),
                CmdPrefix.getPrefix(msg.getGuild())
        )).queue();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        //  Permission-checks for write and embed Links-permission.
        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        //  Send usage-message defined above, if you only run the command with no args.
        if(args.length < 1){
            usage(e.getMessage());
            return;
        }

        String link = HttpUtil.getKiss();

        List<User> user = msg.getMentionedUsers();
        if(user.size() == 1){
            User u = user.get(0);
            //  mentioned user = own user (Bot) -> send message, add reaction and return.
            if(u == msg.getJDA().getSelfUser()){
                //  Special response for a certain user.
                if(!PermUtil.isBeta()){
                    if(msg.getAuthor().getId().equals(IDs.SPECIAL_USER)){
                        if (PermUtil.canReact(tc))
                            msg.addReaction("\uD83D\uDC8B").queue();

                        EmbedBuilder kiss = EmbedUtil.getEmbed()
                                .setImage(MessageUtil.getRandomKissImg());

                        tc.sendMessage(String.format(
                                "%s \\*Enjoys the kiss*",
                                msg.getAuthor().getAsMention()
                        )).queue(message -> message.editMessage(kiss.build()).queue());
                        return;
                    }
                    if(PermUtil.canReact(tc))
                        e.getMessage().addReaction("\uD83D\uDE26").queue();

                    tc.sendMessage(String.format("%s Sorry, but I'm already taken...",
                            msg.getMember().getAsMention())).queue();
                    return;

                }else{
                    if(PermUtil.canReact(tc))
                        e.getMessage().addReaction("\uD83D\uDE33").queue();

                    tc.sendMessage(String.format("%s Not on the first date!",
                            msg.getMember().getAsMention())).queue();
                    return;
                }
            }

            //  mentioned user = author of the message -> Send message and return.
            if(u == msg.getAuthor()){
                tc.sendMessage("I don't know, how you can actually kiss yourself... But ok.").queue();
                return;
            }
            String name = u.getAsMention();
            tc.sendMessage(String.format(
                    "%s gives you a kiss %s",
                    msg.getMember().getEffectiveName(),
                    name
            )).queue(message -> {
                if(link != null)
                    message.editMessage(EmbedUtil.getEmbed().setImage(link).build()).queue();
            });

        }else{
            String users = user.stream().map(User::getAsMention).collect(Collectors.joining(", "));
            tc.sendMessage(String.format(
                    "%s gives you a kiss %s",
                    msg.getMember().getEffectiveName(),
                    users
            )).queue(message -> {
                if(link != null)
                    message.editMessage(EmbedUtil.getEmbed().setImage(link).build()).queue();
            });
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
