package net.Andre601.commands.fun;

import net.Andre601.commands.Command;
import net.Andre601.util.HttpUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class CmdPat implements Command {

    public void usage(TextChannel tc){
        EmbedBuilder usage = new EmbedBuilder();
        usage.setDescription("Please provide a username after the command, " +
                "to share the pat!");

        tc.sendMessage(usage.build()).queue();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length < 1){
            usage(tc);
            return;
        }

        List<User> mentionedUsers = msg.getMentionedUsers();
        for (User user : mentionedUsers){
            if(user == msg.getJDA().getSelfUser()){
                if(PermUtil.canReact(e.getMessage()))
                    e.getMessage().addReaction("❤").queue();

                tc.sendMessage(String.format("%s \\*purr*",
                        msg.getMember().getAsMention())).queue();
                break;
            }
            if(user == msg.getAuthor()){
                tc.sendMessage("Why are you patting yourself?").queue();
                break;
            }
            String name = msg.getGuild().getMember(user).getAsMention();
            tc.sendMessage(String.format("%s gave you a pat %s", msg.getMember().
                    getEffectiveName(), name)).queue(message -> {
                try{
                    message.editMessage(
                            new EmbedBuilder().setImage(HttpUtil.getPat()).build()
                    ).queue();
                }catch (Exception ex){
                    ex.printStackTrace();
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
