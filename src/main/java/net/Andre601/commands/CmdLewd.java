package net.Andre601.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.NekosLifeUtil;

public class CmdLewd implements Command {

    public String getLink(){
        try{
            return NekosLifeUtil.getLewd();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        String link = getLink();
        TextChannel tc = e.getTextChannel();

        if(tc.isNSFW()){
            try {
                EmbedBuilder neko = new EmbedBuilder();
                neko.setTitle("Lewd Neko " + NekosLifeUtil.getCat(), link);
                neko.setImage(link);
                neko.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                        .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

                tc.sendMessage("Getting a lewd neko...").queue(message -> {
                    message.editMessage(neko.build()).queue();
                });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            EmbedBuilder shy = new EmbedBuilder();
            shy.setDescription("Lewd nekos only show them self in NSFW-channels.");
            tc.sendMessage(shy.build()).queue();
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
