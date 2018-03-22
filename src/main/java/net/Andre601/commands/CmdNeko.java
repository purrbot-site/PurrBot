package net.Andre601.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.NekosLifeUtil;

public class CmdNeko implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();

        try {
            EmbedBuilder neko = new EmbedBuilder();
            neko.setTitle("Neko " + NekosLifeUtil.getCat());
            neko.setImage(NekosLifeUtil.getNeko());
            neko.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                    .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

            tc.sendMessage("Getting a cute neko...").queue(message -> {
                message.editMessage(neko.build()).queue();
            });
        }catch (Exception ex){
            ex.printStackTrace();
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
