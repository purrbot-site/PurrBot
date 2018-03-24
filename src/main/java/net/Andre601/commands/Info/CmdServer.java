package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.util.STATIC;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdServer implements Command {

    public String getLevel(Guild g){

        switch (g.getVerificationLevel().toString().toLowerCase()){

            case "high":
                return "(╯°□°）╯︵ ┻━┻";

            case "very_high":
                return "┻━┻ ミ ヽ(ಠ益ಠ)ﾉ 彡 ┻━┻";

            default:
                return g.getVerificationLevel().toString().toLowerCase();
        }
    }

    public int getBots(Guild g){
        int bot = 0;
        for(Member member : g.getMembers()){
            if(member.getUser().isBot()){
                bot++;
            }
        }
        return bot;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Guild g = e.getGuild();

        EmbedBuilder server = new EmbedBuilder();

        server.setAuthor("Serverinfo: " + g.getName(), STATIC.URL,
                e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        server.setThumbnail(g.getIconUrl());

        server.addField("Users", String.format(
                "**Total**: %s\n" +
                        "\n" +
                        "**Humans**: %s\n" +
                        "**Bots**: %s",
                g.getMembers().size(),
                g.getMembers().size() - getBots(g),
                getBots(g)
        ), true);

        server.addField("Server region",
                g.getRegion().getName(),
                true);

        server.addField("Verification level",
                getLevel(g),
                true);

        server.addField("Image", String.format("[`Link`](%s)",
                g.getIconUrl()), true);

        server.addField("Owner",
                g.getOwner().getAsMention(),
                true);
        server.setFooter(String.format(
                "Requested by %s#%s", e.getAuthor().getName(),
                e.getAuthor().getDiscriminator()
        ), e.getAuthor().getEffectiveAvatarUrl());

        tc.sendMessage(server.build()).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
