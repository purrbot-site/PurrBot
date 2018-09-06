package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.commands.server.CmdPrefix;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emojis;
import com.andre601.purrbot.commands.Command;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CmdCuddle implements Command{

    //  This sends a message, if it is executed.
    public void usage(Message msg){
        msg.getTextChannel().sendMessage(String.format(
                        "%s Please mention a user at the end of the command to cuddle with!\n" +
                        "Example: `%scuddle @*Purr*#6875`",
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
        if(args.length == 0){
            usage(e.getMessage());
            return;
        }

        String link = HttpUtil.getCuddle();

        //  Getting all mentioned users in the message and store them in a list.
        List<User> user = msg.getMentionedUsers();

        if(user.isEmpty()){
            usage(e.getMessage());
            return;
        }

        if(user.size() == 1){
            //  If amount of mentioned users equals 1: Get the first mentioned user.
            User u = user.get(0);
            //  mentioned user = own user -> send message, add reaction and return.
            if(u == msg.getJDA().getSelfUser()){
                if(PermUtil.canReact(tc))
                    e.getMessage().addReaction("❤").queue();

                tc.sendMessage(String.format("%s \\*Enjoys the cuddle*",
                        msg.getMember().getAsMention())).queue();
                return;
            }

            //  mentioned user = author of the message -> Send message and return.
            if(u == msg.getAuthor()){
                tc.sendMessage("Do you have no one to cuddle with?").queue();
                return;
            }
            //  Saving the mentioned user as String
            String name = u.getAsMention();
            tc.sendMessage(Emojis.IMG_LOADING + " Getting a cuddle-gif...").queue(message -> {
                if(link != null)
                    message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                            "{0} cuddles with you {1}",
                            msg.getMember().getEffectiveName(),
                            name
                    )).setImage(link).build()).queue();
                else
                    message.editMessage(MessageFormat.format(
                            "{0} cuddles with you {1}",
                            msg.getMember().getEffectiveName(),
                            name
                    )).queue();
            });

        }else{
            //  Storing all mentioned users as String, seperated by a comma (@user1, @user2, @user3, ...)
            String users = user.stream().map(User::getAsMention).collect(Collectors.joining(", "));
            tc.sendMessage(Emojis.IMG_LOADING + " Getting a cuddle-gif...").queue(message -> {
                if(link != null)
                    message.editMessage("\u200B").embed(EmbedUtil.getEmbed().setDescription(MessageFormat.format(
                            "{0} cuddles with you {1}",
                            msg.getMember().getEffectiveName(),
                            users
                    )).setImage(link).build()).queue();
                else
                    message.editMessage(MessageFormat.format(
                            "{0} cuddles with you {1}",
                            msg.getMember().getEffectiveName(),
                            users
                    )).queue();
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
