package net.Andre601.commands.nsfw;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.HttpUtil;

import java.util.concurrent.TimeUnit;

public class CmdLewd implements Command {

    private static String getRandomNSFW(){
        return Main.getRandomNoNSWF().size() > 0 ? Main.getRandomNoNSWF().get(
                Main.getRandom().nextInt(Main.getRandomFact().size())) : "";
    }

    public String getLink(){
        try{
            return HttpUtil.getLewd();
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

        if(PermUtil.canDeleteMsg(e.getMessage()))
            e.getMessage().delete().queue();

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(tc.isNSFW()){
            try {
                EmbedBuilder neko = new EmbedBuilder();
                neko.setTitle("Lewd Neko " + HttpUtil.getCat(), link);
                neko.setImage(link);
                neko.setFooter(String.format(
                        "Requested by: %s#%s | %s",
                        e.getAuthor().getName(),
                        e.getAuthor().getDiscriminator(),
                        Main.now()
                ), e.getAuthor().getEffectiveAvatarUrl());

                tc.sendMessage("Getting a lewd neko...").queue(message -> {
                    message.editMessage(neko.build()).queue();
                });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            tc.sendMessage(String.format(getRandomNSFW(),
                    e.getAuthor().getAsMention())).queue(msg -> {
                        msg.delete().queueAfter(10, TimeUnit.SECONDS);
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
