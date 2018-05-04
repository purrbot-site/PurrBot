package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.util.EmbedUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdStats implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        JDA jda = e.getJDA();

        if(!PermUtil.canWrite(msg))
            return;

        if(!PermUtil.canSendEmbed(msg)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        tc.sendTyping().queue();
        EmbedBuilder stats = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor("Purr-Bot Stats")
                .addField("Guilds:", String.valueOf(jda.getGuilds().size()), true)
                .addField("VoiceChannels:", String.valueOf(jda.getVoiceChannels().size()), true)
                .addField("TextChannels:", String.valueOf(jda.getTextChannels().size()), true)
                .addField("Total Members:", String.valueOf(jda.getUsers().stream().toArray().length), true)
                .addField("Humans:", String.valueOf(jda.getUsers().stream().filter(user ->
                        !user.isBot()).toArray().length), true)
                .addField("Bots:", String.valueOf(jda.getUsers().stream().filter(user ->
                        user.isBot()).toArray().length), true);

        tc.sendMessage(stats.build()).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
