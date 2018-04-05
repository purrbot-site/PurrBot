package net.Andre601.commands.owner;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShutdown implements Command {

    private static String getRandomShutdown(){
        return Main.getRandomShutdownText().size() > 0 ? Main.getRandomShutdownText().get(
                Main.getRandom().nextInt(Main.getRandomShutdownText().size())) : "";
    }

    private static String getRandomNoShutdown(){
        return Main.getRandomNoShutdownText().size() > 0 ? Main.getRandomNoShutdownText().get(
                Main.getRandom().nextInt(Main.getRandomNoShutdownText().size())) : "";
    }

    private static String getRandomImage(){
        return Main.getRandomShutdownImage().size() > 0 ? Main.getRandomShutdownImage().get(
                Main.getRandom().nextInt(Main.getRandomShutdownImage().size())) : "";
    }

    private static String getRandomNoImage(){
        return Main.getRandomNoShutdownImage().size() > 0 ? Main.getRandomNoShutdownImage().get(
                Main.getRandom().nextInt(Main.getRandomNoShutdownImage().size())) : "";
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if(!PermUtil.canSendEmbed(e.getMessage())){
            e.getChannel().sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(PermUtil.isCreator(e.getMessage())){
            e.getMessage().delete().queue();

            EmbedBuilder shutdown = MessageUtil.getEmbed(e.getAuthor())
                    .setDescription(getRandomShutdown())
                    .setImage(getRandomImage());

            e.getTextChannel().sendMessage(shutdown.build()).queue();

            System.out.println("[INFO] Disabling bot...");
            e.getJDA().shutdown();

        }else{
            e.getMessage().delete().queue();

            EmbedBuilder noShutdown = MessageUtil.getEmbed(e.getAuthor())
                    .setDescription(getRandomNoShutdown())
                    .setImage(getRandomNoImage());

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
