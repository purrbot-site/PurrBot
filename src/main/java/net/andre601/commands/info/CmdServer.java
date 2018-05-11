package net.andre601.commands.info;

import net.andre601.commands.Command;
import net.andre601.util.EmbedUtil;
import net.andre601.util.MessageUtil;
import net.andre601.util.PermUtil;
import net.andre601.util.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;

public class CmdServer implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Guild g = e.getGuild();

        if (!PermUtil.canWrite(e.getMessage()))
            return;

        if(!PermUtil.canSendEmbed(e.getMessage())){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }
        tc.sendTyping().queue();
        EmbedBuilder server = EmbedUtil.getEmbed()
                .setAuthor("Serverinfo: " + g.getName(), Static.URL,
                        e.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setThumbnail(g.getIconUrl())
                .addField("Users", String.format(
                        "**Total**: %s\n" +
                                "\n" +
                                "**Humans**: %s\n" +
                                "**Bots**: %s",
                        g.getMembers().size(),
                        g.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                        g.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length
                ), true)
                .addField("Server region",
                        g.getRegion().getName(), true)
                .addField("Verification level",
                        MessageUtil.getLevel(g), true)
                .addField("Image", String.format(
                        "[`Link`](%s)",
                        g.getIconUrl()
                ), true)
                .addField("Owner",
                        g.getOwner().getAsMention(), true)
                .addField("Created", String.valueOf(MessageUtil.formatTime(
                        LocalDateTime.from(g.getCreationTime()))), true);

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
