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

    /**
     * Listens for when a member joins a guild.
     *
     * @param event
     *        A {@link net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent GuildMemberJoinEvent}.
     */
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getUser().isBot())
            return;

        Guild guild = event.getGuild();

        if(!DBUtil.getWelcomeChannel(guild).equals("none")){
            TextChannel tc = CmdWelcome.getChannel(guild);

            if(tc != null){
                if(!PermUtil.canWrite(tc))
                    return;

                //  Creating a new message with the MessageBuilder
                Message welcome = new MessageBuilder()
                        .append(MessageFormat.format(
                                "Welcome {0}!",
                                event.getUser().getAsMention()
                        )
                ).build();

                if(PermUtil.canUploadImage(tc)){
                    InputStream is = ImageUtil.getWelcomeImg(
                            event.getUser(),
                            event.getGuild(),
                            DBUtil.getImage(guild),
                            DBUtil.getColor(guild)
                    );

                    if(is == null) {
                        tc.sendMessage(welcome).queue();
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    ), welcome).queue();
                }else{
                    tc.sendMessage(welcome).queue();
                }
            }
        }
    }

    /**
     * Listens for when a channel in a guild gets deleted.
     * This is used for when a saved welcome-channel gets deleted, that the bot won't return errors because of it no
     * longer exists.
     *
     * If a saved welcome channel gets deleted, we run
     * {@link com.andre601.purrbot.commands.guild.CmdWelcome#resetChannel(Guild) CmdWelcome.resetChannel(Guild)} to
     * reset it.
     *
     * @param event
     *        A {@link net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent TextChannelDeleteEvent}.
     */
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        Guild g = event.getGuild();

        if(!DBUtil.getWelcomeChannel(g).equals("none")){
            TextChannel channel = g.getTextChannelById(DBUtil.getWelcomeChannel(g));
            if(event.getChannel() == channel){
                CmdWelcome.resetChannel(g);
            }
        }
    }

}
