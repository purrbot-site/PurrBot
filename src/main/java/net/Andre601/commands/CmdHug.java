package net.Andre601.commands;

import net.Andre601.util.NekosLifeUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class CmdHug implements Command {

    public void usage(TextChannel tc){
        EmbedBuilder usage = new EmbedBuilder();
        usage.setDescription("Please provide a username after the command, " +
                "to share the hug!");

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

        if(args.length < 1){
            usage(tc);
            return;
        }

        List<User> mentionedUsers = msg.getMentionedUsers();
        for (User user : mentionedUsers){
            if(user == msg.getJDA().getSelfUser()){
                tc.sendMessage(String.format("Awwwwww.... Thank you for the hug %s. :heart:",
                        msg.getMember().getAsMention())).queue();
                break;
            }
            if(user == msg.getAuthor()){
                tc.sendMessage("You wanna hug yourself? Are you lonely?").queue();
                break;
            }
            String name = msg.getGuild().getMember(user).getAsMention();
            tc.sendMessage(String.format("%s you got a hug from %s", name, msg.getMember().
            getEffectiveName())).queue(message -> {
                try{
                    message.editMessage(
                            new EmbedBuilder().setImage(NekosLifeUtil.getHug()).build()
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
