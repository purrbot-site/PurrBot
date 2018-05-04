package net.Andre601.commands.fun;

import net.Andre601.commands.Command;
import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.util.EmbedUtil;
import net.Andre601.util.HttpUtil;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class CmdTickle implements Command {

    public void usage(Message msg){
        msg.getTextChannel().sendMessage(String.format(
                "%s Please mention a user at the end of the command to tickle!\n" +
                        "Example: `%stickle @*Purr*#6875`",
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

        if (!PermUtil.canWrite(msg))
            return;

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length < 1){
            usage(e.getMessage());
            return;
        }

        List<User> mentionedUsers = msg.getMentionedUsers();
        for (User user : mentionedUsers){
            if(user == msg.getJDA().getSelfUser()){
                if(PermUtil.canReact(e.getMessage()))
                    e.getMessage().addReaction("😂").queue();

                tc.sendMessage(String.format("N-no... I can't lau- \\*uncontrollable laughter*",
                        msg.getMember().getAsMention())).queue();
                break;
            }
            if(user == msg.getAuthor()){
                tc.sendMessage("Why are you tickling yourself?").queue();
                break;
            }
            String name = msg.getGuild().getMember(user).getAsMention();
            tc.sendMessage(String.format(
                    "%s tickles %s",
                    msg.getMember().getEffectiveName(),
                    name
            )).queue(message -> {
                try{
                    message.editMessage(
                            EmbedUtil.getEmbed().setImage(HttpUtil.getTickle()).build()
                    ).queue();
                }catch (Exception ignored){
                }
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
