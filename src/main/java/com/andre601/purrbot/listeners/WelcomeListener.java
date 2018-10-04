package com.andre601.purrbot.listeners;

import com.andre601.purrbot.commands.server.CmdWelcome;
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

import java.text.MessageFormat;

public class WelcomeListener extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if(e.getUser().isBot())
            return;

        Guild g = e.getGuild();

        if(!DBUtil.getWelcome(g).equals("none")){
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
                    ImageUtil.createWelcomeImg(e.getUser(), g, tc, msg, DBUtil.getImage(g), DBUtil.getColor(g));
                }else{
                    tc.sendMessage(msg).queue();
                }
            }
        }
    }

    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        Guild g = e.getGuild();

        if(!DBUtil.getWelcome(g).equals("none")){
            TextChannel channel = g.getTextChannelById(DBUtil.getWelcome(g));
            if(e.getChannel() == channel){
                CmdWelcome.resetChannel(g);
            }
        }
    }

}
