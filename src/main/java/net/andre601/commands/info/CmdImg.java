package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.util.PermUtil;
import net.andre601.util.Static;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static net.andre601.commands.server.CmdPrefix.getPrefix;
import static net.andre601.util.ImageUtil.createImg;

public class CmdImg implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        Guild g = e.getGuild();

        if(!PermUtil.canWrite(msg))
            return;

        if(!PermUtil.canUploadImage(tc)){
            tc.sendMessage(String.format(
                    "%s I need permission to upload images!",
                    msg.getAuthor().getAsMention()
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        if(PermUtil.canDeleteMsg(msg))
            msg.delete().queue();

        if(args.length == 0){
            tc.sendMessage(String.format(
                    "%s Please provide a URL!\n" +
                    "Example: `%simg %s`",
                    msg.getAuthor().getAsMention(),
                    getPrefix(g),
                    Static.AVATAR_URL
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        tc.sendTyping().queue();

        if(args[0].startsWith("neko:")){
            String link = args[0].replace("neko:", "https://cdn.nekos.life/neko/");
            createImg(link, msg);
            return;
        }

        createImg(args[0], msg);

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
