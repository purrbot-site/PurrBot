package com.andre601.purrbot.listeners;

import com.andre601.purrbot.commands.guild.CmdWelcome;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.InputStream;
import java.text.MessageFormat;

public class WelcomeListener extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if(e.getUser().isBot())
            return;

        Guild g = e.getGuild();

        if(!DBUtil.getWelcomeChannel(g).equals("none")){
            TextChannel tc = CmdWelcome.getChannel(g);

            if(tc != null){
                if(!PermUtil.canWrite(tc))
                    return;

                //  Creating a new message with the MessageBuilder
                Message msg = new MessageBuilder()
                        .append(MessageFormat.format(
                                "Welcome {0}!",
                                e.getUser().getAsMention()
                        )
                ).build();

                if(PermUtil.canUploadImage(tc)){
                    InputStream is = ImageUtil.getWelcomeImg(
                            e.getUser(),
                            e.getGuild(),
                            DBUtil.getImage(g),
                            DBUtil.getColor(g)
                    );

                    if(is == null) {
                        tc.sendMessage(msg).queue();
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    ), msg).queue();
                }else{
                    tc.sendMessage(msg).queue();
                }
            }
        }
    }

    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        Guild g = e.getGuild();

        if(!DBUtil.getWelcomeChannel(g).equals("none")){
            TextChannel channel = g.getTextChannelById(DBUtil.getWelcomeChannel(g));
            if(e.getChannel() == channel){
                CmdWelcome.resetChannel(g);
            }
        }
    }

}
