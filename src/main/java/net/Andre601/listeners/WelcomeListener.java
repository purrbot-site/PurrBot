package net.Andre601.listeners;

import net.Andre601.commands.server.CmdWelcome;
import net.Andre601.util.ImageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class WelcomeListener extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Guild g = e.getGuild();

        if(CmdWelcome.getWelcomeChannel().containsKey(g)){
            TextChannel tc = CmdWelcome.getWelcomeChannel().get(g);

            if(tc != null){

                tc.sendMessage(String.format(
                        "Welcome %s",
                        e.getUser().getAsMention()
                )).queue();
                if(PermUtil.canUploadImage(tc)){
                    ImageUtil.createWelcomeImg(e.getUser(), g, tc);

                }
            }
        }

    }

}
