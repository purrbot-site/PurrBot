package net.andre601.commands.fun;

import net.andre601.commands.Command;
import net.andre601.util.HttpUtil;
import net.andre601.util.PermUtil;
import net.andre601.util.messagehandling.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.text.MessageFormat;

public class CmdFakegit implements Command {


    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        Message msg = e.getMessage();
        Guild g = e.getGuild();
        TextChannel tc = e.getTextChannel();

        JSONObject json = HttpUtil.getFakeGit();

        if(!PermUtil.canWrite(tc))
            return;

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                msg.addReaction("🚫").queue();

            return;
        }

        if(json == null){
            tc.sendMessage(MessageFormat.format(
                    "{0} There was an issue with the API...",
                    msg.getAuthor().getAsMention()
            )).queue();
            return;
        }

        String link = json.getString("permalink");
        String hash = json.getString("hash").substring(0, 6);
        String commit_message = json.getString("commit_message");

        EmbedBuilder fakeGit = EmbedUtil.getEmbed()
                .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl())
                .setTitle(MessageFormat.format(
                        "[{0}:{1}] 1 new commit",
                        g.getName().replace(" ", "_"),
                        tc.getName()
                ), link)
                .setDescription(MessageFormat.format(
                        "[`{0}`]({1}) {2}",
                        hash,
                        link,
                        commit_message
                ));

        tc.sendMessage(fakeGit.build()).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
