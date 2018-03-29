package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.STATIC;

public class CmdHelp implements Command {

    private static String getFact(){
        return Main.getRandomFact().size() > 0 ? Main.getRandomFact().get(
                Main.getRandom().nextInt(Main.getRandomFact().size())) : "";
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if(!PermUtil.canSendEmbed(e.getMessage())){
            e.getTextChannel().sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        EmbedBuilder help = new EmbedBuilder();

        help.setAuthor(e.getJDA().getSelfUser().getName(), STATIC.URL, e.getJDA().getSelfUser().
                getEffectiveAvatarUrl());

        help.addField("Command-Prefix:", "All commands start with `" +
                STATIC.PREFIX + "`", false);

        help.addField("Commands:", "**Information**\n" +
                        "`Help` Well... You see the result. xD\n" +
                        "`Info` Sends some infos about me :3\n" +
                        "`Invite` Sends you a link to invite me. :*\n" +
                        "`Server` What Discord is that? :/\n" +
                        "`User` Get infos about someone :)\n" +
                        "\n" +
                        "**Fun**\n" +
                        "`Neko` Gives you a cute neko. OwO\n" +
                        "`Hug` Share some love? :?\n" +
                        "`Pat` Pats are nice. :D\n" +
                        "`Slap` Slaps someone >:)\n" +
                        "\n" +
                        "**NSFW**\n" +
                        "`Lewd` Gives you a lewd neko. >w<",
                false);
        help.addBlankField(false);

        help.addField("Random Fact:", getFact(), false);

        help.setFooter("Requested by " + e.getAuthor().getName() + "#" + e.getAuthor()
                .getDiscriminator(), e.getAuthor().getEffectiveAvatarUrl());

        e.getTextChannel().sendMessage(help.build()).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
