package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
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

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Guild g = e.getGuild();

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

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
                g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
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
                "Requested by %s#%s | %s",
                e.getAuthor().getName(),
                e.getAuthor().getDiscriminator(),
                Main.now()
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
