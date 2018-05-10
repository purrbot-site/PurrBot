package net.andre601.commands.info;

import com.jagrosh.jdautilities.menu.Paginator;
import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.util.EmbedUtil;
import net.andre601.util.MessageUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static net.andre601.core.Main.waiter;

public class CmdHelp implements Command {

    private static Paginator.Builder pBuilder =
            new Paginator.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    private static void usage(Message msg, String command, String use, String description, String args,
                             String permission){
        msg.getChannel().sendTyping().queue();
        EmbedBuilder uEmbed = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Command: %s",
                        command
                ))
                .setDescription(String.format(
                        "**Usage**: `%s%s`\n" +
                        "\n" +
                        "**Description**:\n" +
                        "%s\n" +
                        "\n" +
                        "**Arguments**:\n" +
                        "%s\n" +
                        "\n" +
                        "**Permission**:\n" +
                        "%s",
                        CmdPrefix.getPrefix(msg.getGuild()),
                        use,
                        description,
                        args,
                        permission
                ));

        msg.getChannel().sendMessage(uEmbed.build()).queue();
    }

    private static void usage(Message msg){
        msg.getChannel().sendTyping().queue();

        /*
        Paginator page = pBuilder
                .addItems(String.format(
                        "**Command-Prefix**: %s\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Use the reactions to switch through the pages.\n" +
                        "Type `%shelp [command]` to get infos about a command!\n" +
                        "\n" +
                        "```\n" +
                        "Pages:\n" +
                        "  Fun\n" +
                        "  Informative\n" +
                        "  NSFW\n" +
                        "  Server\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact:\n" +
                        "%s",
                        CmdPrefix.getPrefix(msg.getGuild()),
                        CmdPrefix.getPrefix(msg.getGuild()),
                        MessageUtil.getFact()
                ), String.format(
                        "**Fun**:\n" +
                        "\n" +
                        "```\n" +
                        "Cuddle <@user>\n" +
                        "Hug <@user>\n" +
                        "Kiss <@user>\n" +
                        "Neko [-slide]\n" +
                        "Pat <@user>\n" +
                        "Slap <@user>\n" +
                        "Tickle <@user>\n" +
                        "\n" +
                        "[optional] | <required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact:\n" +
                        "%s",
                        MessageUtil.getFact()
                ), String.format(
                        "**Informative**:\n" +
                        "\n" +
                        "```\n" +
                        "Help\n" +
                        "Info [-here]\n" +
                        "Invite [-here]\n" +
                        "Quote <messageID>\n" +
                        "Server\n" +
                        "Stats\n" +
                        "User [@user]\n" +
                        "\n" +
                        "[optional] | <required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact:\n" +
                        "%s",
                        MessageUtil.getFact()
                ), String.format(
                        "**NSFW**:\n" +
                        "\n" +
                        "```\n" +
                        "Lewd [-slide]\n" +
                        "\n" +
                        "[optional] | <required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact:\n" +
                        "%s",
                        MessageUtil.getFact()
                ), String.format(
                        "**Server**:\n" +
                        "\n" +
                        "```\n" +
                        "Prefix [set <prefix>|reset]\n" +
                        "Welcome [set <channelID>|reset|test]\n" +
                        "\n" +
                        "[optional] | <required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact:\n" +
                        "%s",
                        MessageUtil.getFact()
                ))
                .setFinalAction(message -> {
                    message.delete().queue();
                    msg.getTextChannel().sendMessage("Help closed!").queue(del ->
                            del.delete().queueAfter(5, TimeUnit.SECONDS));
                })
                .build();
        page.display(msg.getChannel());
        */
        EmbedBuilder help = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle("Help")
                .setDescription(String.format(
                        "**Command-Prefix**: `%s`\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Type `%shelp [command]`, to get infos about a command!\n" +
                        "\n" +
                        "```\n" +
                        "Informative:\n" +
                        " Help [command]\n" +
                        " info [-here]\n" +
                        " Invite [-here]\n" +
                        " Quote <MessageID>\n" +
                        " Server\n" +
                        " Stats\n" +
                        " User [@user]\n" +
                        "\n" +
                        "Fun:\n" +
                        " Cuddle <@user>\n" +
                        " Hug <@user>\n" +
                        " Kiss <@user>\n" +
                        " Neko [-slide]\n" +
                        " Pat <@user>\n" +
                        " Slap <@user>\n" +
                        " Tickle <@user>\n" +
                        "\n" +
                        "NSFW:\n" +
                        " Lewd [-slide]\n" +
                        "\n" +
                        "Server:\n" +
                        " Prefix [set <prefix>|reset]\n" +
                        " Welcome [set <ChannelID>|reset|test]\n" +
                        "\n" +
                        "[optional] <required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random Fact**:\n" +
                        "%s",
                        CmdPrefix.getPrefix(msg.getGuild()),
                        CmdPrefix.getPrefix(msg.getGuild()),
                        MessageUtil.getFact()
                ));

        msg.getChannel().sendMessage(help.build()).queue();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        Message msg = e.getMessage();

        if (!PermUtil.canWrite(msg))
            return;

        if(!PermUtil.canSendEmbed(e.getMessage())){
            e.getTextChannel().sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(e.getMessage()))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length == 0){
            usage(msg);
            return;
        }

        switch (args[0].toLowerCase()){

            case "help":
                usage(msg, "Help", "help [command]",
                        "Shows you all available commands.",
                        "`[command]` Gives info about a command.",
                        "`none`"
                );
                break;

            case "info":
                usage(msg, "info", "info [-here]",
                        "Sends you basic info about the bot (A small description, version, used " +
                        "Library, ect)",
                        "`-here` Sends the message in the channel in which you've run the command.",
                        "`none`"
                );
                break;

            case "invite":
                usage(msg, "Invite", "invite [-here]",
                        "Sends you the invite-links for the bot and for the official Discord.",
                        "`-here` Sends the message in the channel in which you've run the command.",
                        "none"
                );
                break;

            case "server":
                usage(msg, "Server",  "server",
                        "Gives you basic server-infos like users online, verification level, ect.",
                        "`none`",
                        "`none`"
                );
                break;

            case "user":
                usage(msg, "User", "user [@user]",
                        "Gives basic information about the mentioned user.",
                        "`<@user>` The user to get infos about (as mention).",
                        "`none`"
                );
                break;

            case "neko":
                usage(msg, "Neko", "neko [-slide]",
                        "Sends a cute neko. 'nuf said.",
                        "`-slide` Creates a slideshow with 30 images",
                        "`none`"
                );
                break;

            case "hug":
                usage(msg, "Hug", "hug <@user>",
                        "Gives the mentioned user a hug.",
                        "`<@user>` The user to hug (as mention).",
                        "`none`"
                );
                break;

            case "pat":
                usage(msg, "Pat", "pat <@user>",
                        "Gives the mentioned user a pat.",
                        "`<@user>` The user to pat (as mention).",
                        "`none`"
                );
                break;

            case "slap":
                usage(msg, "Slap", "slap <@user>",
                        "Slaps the mentioned user.",
                        "`<@slap>` The user to slap (as mention).",
                        "`none`"
                );
                break;

            case "lewd":
                usage(msg, "Lewd", "lewd [-slide]",
                        "Sends a lewd neko.\n" +
                        "Can only be used in NSFW-Channels.",
                        "`-slide` Creates a slideshow with 30 images",
                        "`none`"
                );
                break;

            case "prefix":
                usage(msg, "Prefix", "prefix [set <prefix>|reset]",
                        "Shows the currently used prefix in this Discord, if no argument is given.",
                        "`set <prefix>` Sets the prefix to the provided one.\n" +
                        "`reset` Resets the prefix to the default one.",
                        "`MANAGE_SERVER` for setting or resetting the prefix.");
                break;

            case "cuddle":
                usage(msg, "Cuddle", "cuddle <@user>",
                        "Cuddles the mentioned user.",
                        "`<@user>` The user to cuddle (as mention).",
                        "`none`"
                );
                break;

            case "tickle":
                usage(e.getMessage(), "Tickle", "tickle <@user>",
                        "Tickles the mentioned user.",
                        "`<@user>` The user to tickle (as mention).",
                        "`none`"
                );
                break;

            case "welcome":
                usage(e.getMessage(), "Welcome", "welcome [set <ChannelID>|reset|test]",
                        "Shows, sets or resets the Welcome-channel.",
                        "`set <ChannelID>` Sets a Channel as Welcome-Channel.\n" +
                        "`reset` Resets (removes) the welcome-channel.\n" +
                        "`test` Creates a welcome-image in the channel you currently are.",
                        "`MANAGE_SERVER`"
                );
                break;

            case "stats":
            case "stat":
                usage(e.getMessage(), "Stats", "stats",
                        "Shows some statistics of \\*Purr*",
                        "`none`",
                        "`none`"
                );
                break;

            case "kiss":
                usage(e.getMessage(), "Kiss", "kiss <@user>",
                        "Lets you kiss someone.",
                        "`<@user>` The user you want to kiss (as mention).",
                        "`none`"
                );
                break;

            case "quote":
                usage(e.getMessage(), "Quote", "quote <messageID>",
                        "Lets you quote a message.",
                        "`<messageID>` The ID of the message to quote.",
                        "`none`"
                );
                break;

            default:
                usage(msg);
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
