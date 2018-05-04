package net.Andre601.commands.owner;

import net.Andre601.commands.Command;
import net.Andre601.util.EmbedUtil;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShutdown implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if (!PermUtil.canWrite(e.getMessage()))
            return;

        if(!PermUtil.canSendEmbed(e.getMessage())){
            e.getChannel().sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(PermUtil.canDeleteMsg(e.getMessage()))
            e.getMessage().delete().queue();

        if(PermUtil.isCreator(e.getMessage())){

            EmbedBuilder shutdown = EmbedUtil.getEmbed(e.getAuthor())
                    .setDescription(MessageUtil.getRandomShutdown())
                    .setImage(MessageUtil.getRandomImage());

            e.getTextChannel().sendMessage(shutdown.build()).queue();

            System.out.println("[INFO] Disabling bot...");
            e.getJDA().shutdown();

        }else{

            EmbedBuilder noShutdown = EmbedUtil.getEmbed(e.getAuthor())
                    .setDescription(MessageUtil.getRandomNoShutdown())
                    .setImage(MessageUtil.getRandomNoImage());

            e.getTextChannel().sendMessage(noShutdown.build()).queue();
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
