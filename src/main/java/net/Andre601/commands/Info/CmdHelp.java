package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.STATIC;

public class CmdHelp implements Command {

    private static String getFact(){
        return Main.getRandomFact().size() > 0 ? Main.getRandomFact().get(
                Main.getRandom().nextInt(Main.getRandomFact().size())) : "";
    }

    public static void usage(Message msg, String command, String use, String description){

        EmbedBuilder usageEmbed = new EmbedBuilder();
        usageEmbed.setAuthor("Command: " + command, STATIC.URL,
                msg.getJDA().getSelfUser().getEffectiveAvatarUrl());
        usageEmbed.addField("Usage:", String.format(
                "`%s`",
                use
        ), false);
        usageEmbed.addField("Description:", description, false);

        msg.getTextChannel().sendMessage(usageEmbed.build()).queue();
    }

    public static void usage(Message msg){

        EmbedBuilder help = new EmbedBuilder();

        help.setAuthor("Help", STATIC.URL,
                msg.getJDA().getSelfUser().getEffectiveAvatarUrl());

        help.addField("Command-Prefix:", String.format(
                "All commands start with the prefix `%s`",
                STATIC.PREFIX
        ), false);

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

        help.setFooter("Requested by " + msg.getAuthor().getName() + "#" + msg.getAuthor()
                .getDiscriminator(), msg.getAuthor().getEffectiveAvatarUrl());

        msg.getTextChannel().sendMessage(help.build()).queue();
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

        if(args.length == 0){
            usage(e.getMessage());
            return;
        }

        switch (args[0].toLowerCase()){

            case "help":
                usage(e.getMessage(), "Help", String.format(
                        "`%shelp`",
                        STATIC.PREFIX
                ), "Shows you all available commands."
                );
                break;

            case "info":
                usage(e.getMessage(), "Info", String.format(
                        "`%sinfo`",
                        STATIC.PREFIX
                        ), "Sends you basic infos about the bot (A small description, version, used " +
                        "Library, ect)"
                );
                break;

            case "invite":
                usage(e.getMessage(), "Invite", String.format(
                        "`%sinvite`",
                        STATIC.PREFIX
                        ), "Sends you the invite-links for the bot and for the official Discord"
                );
                break;

            case "server":
                usage(e.getMessage(), "Server", String.format(
                        "`%sserver`",
                        STATIC.PREFIX
                        ), "Gives you basic server-infos like users online, verification level, ect."
                );
                break;

            case "user":
                usage(e.getMessage(), "User", String.format(
                        "`%suser [@user]`",
                        STATIC.PREFIX
                        ), "Gives you basic information about yourself.\n" +
                        "@Mention a user at the end of the command, to get infos about him/her."
                );
                break;

            case "neko":
                usage(e.getMessage(), "Neko", String.format(
                        "`%sneko`",
                        STATIC.PREFIX
                        ), "Sends a cute neko. 'nuf said."
                );
                break;

            case "hug":
                usage(e.getMessage(), "Hug", String.format(
                        "`%shug @user`",
                        STATIC.PREFIX
                        ), "Gives the mentioned user a hug."
                );
                break;

            case "pat":
                usage(e.getMessage(), "Pat", String.format(
                        "`%spat @user`",
                        STATIC.PREFIX
                        ), "Gives the mentioned user a pat."
                );
                break;

            case "slap":
                usage(e.getMessage(), "Slap", String.format(
                        "`%sslap @user`",
                        STATIC.PREFIX
                        ), "Slaps the mentioned user."
                );
                break;

            case "lewd":
                usage(e.getMessage(), "Lewd", String.format(
                        "`%slewd`",
                        STATIC.PREFIX
                        ), "Sends a lewd neko.\n" +
                        "Can only be used in NSFW-Channels."
                );
                break;

            default:
                usage(e.getMessage());
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
