package net.Andre601.listeners;

import net.Andre601.commands.server.CmdWelcome;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.net.URL;
import java.net.URLConnection;

public class WelcomeListener extends ListenerAdapter {
    public static final String[] UA = {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"};

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Guild g = e.getGuild();

        String n = String.format("%s.png", e.getUser().getName());

        if(CmdWelcome.getWelcomeChannel().containsKey(g)){
            TextChannel tc = CmdWelcome.getWelcomeChannel().get(g);

            if(tc != null){
                try {
                    URL url = new URL("https://neko.wilz.ml/api/welcomeneko?avatarurl=" +
                            e.getUser().getEffectiveAvatarUrl() +
                            "?size=1024&username=" +
                            e.getUser().getName() +
                            "%23" +
                            e.getUser().getDiscriminator() +
                            "&pos=" +
                            g.getMembers().size() +
                            "&color=black&token=cf4a5a918d05d55aaf1405dbab3f965e");
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty(UA[0], UA[1]);

                    tc.sendMessage(String.format(
                            "%s",
                            e.getUser().getAsMention()
                    )).queue(msg -> {
                        try {
                            msg.getTextChannel().sendFile(connection.getInputStream(), n, msg).queue();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    });

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

    }

}
