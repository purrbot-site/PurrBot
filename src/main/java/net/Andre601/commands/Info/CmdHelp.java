package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.core.Main;
import net.Andre601.util.MessageUtil;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdHelp implements Command {

    private static String getFact(){
        return Main.getRandomFact().size() > 0 ? Main.getRandomFact().get(
                Main.getRandom().nextInt(Main.getRandomFact().size())) : "";
    }

    public static void usage(Message msg, String command, String use, String description){
        EmbedBuilder uEmbed = MessageUtil.getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Command: %s",
                        command
                ))
                .setDescription(String.format(
                        "**Usage**: `%s%s`\n" +
                        "\n" +
                        "**Description**:\n" +
                        "%s",
                        CmdPrefix.getPrefix(msg.getGuild()),
                        use,
                        description
                ));

        msg.getChannel().sendMessage(uEmbed.build()).queue();
    }

    public static void usage(Message msg){
        EmbedBuilder help = MessageUtil.getEmbed(msg.getAuthor())
                .setTitle("Help")
                .setDescription(String.format(
                       "**Command-Prefix**: `%s`\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Type `%shelp [command]`, to get infos about a command!\n" +
                        "\n" +
                        "__Informative__:\n" +
                        "`Help`\n" +
                        "`Info`\n" +
                        "`Invite`\n" +
                        "`Server`\n" +
                        "`User`\n" +
                        "\n" +
                        "__Fun__:\n" +
                        "`Cuddle`\n" +
                        "`Hug`\n" +
                        "`Neko`\n" +
                        "`Pat`\n" +
                        "`Slap`\n" +
                        "`Tickle`\n" +
                        "\n" +
                        "__NSFW__:\n" +
                        "`Lewd`\n" +
                        "\n" +
                        "__Server__:\n" +
                        "`Prefix`\n" +
                        "\n" +
                        "**Random Fact**:\n" +
                        "%s",
                        CmdPrefix.getPrefix(msg.getGuild()),
                        CmdPrefix.getPrefix(msg.getGuild()),
                        getFact()
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
                usage(msg, "Help", "help",
                        "Shows you all available commands."
                );
                break;

            case "info":
                usage(msg, "Info", "info",
                        "Sends you basic infos about the bot (A small description, version, used " +
                        "Library, ect)"
                );
                break;

            case "invite":
                usage(msg, "Invite", "invite",
                        "Sends you the invite-links for the bot and for the official Discord"
                );
                break;

            case "server":
                usage(msg, "Server",  "server",
                        "Gives you basic server-infos like users online, verification level, ect."
                );
                break;

            case "user":
                usage(msg, "User", "user [@user]",
                        "Gives you basic information about yourself.\n" +
                        "@Mention a user at the end of the command, to get infos about him/her."
                );
                break;

            case "neko":
                usage(msg, "Neko", "neko",
                        "Sends a cute neko. 'nuf said."
                );
                break;

            case "hug":
                usage(msg, "Hug", "hug <@user>",
                        "Gives the mentioned user a hug."
                );
                break;

            case "pat":
                usage(msg, "Pat", "pat <@user>",
                        "Gives the mentioned user a pat."
                );
                break;

            case "slap":
                usage(msg, "Slap", "slap <@user>",
                        "Slaps the mentioned user."
                );
                break;

            case "lewd":
                usage(msg, "Lewd", "lewd",
                        "Sends a lewd neko.\n" +
                        "Can only be used in NSFW-Channels."
                );
                break;

            case "prefix":
                usage(msg, "Prefix", "prefix [set <prefix>|reset]",
                        "Shows the currently used prefix in this Discord, if no argument is given.\n" +
                        "\n" +
                        "**Arguments:**\n" +
                        "`set <prefix>` Changes the prefix to the provided text.\n" +
                        "`reset` Resets the prefix to the default one.\n" +
                        "\n" +
                        "**Permissions**\n" +
                        "You need the `MANAGE_SERVER` permission, to either set or reset the prefix.");
                break;

            case "cuddle":
                usage(msg, "Cuddle", "cuddle <@user>",
                        "Cuddles the mentioned user."
                );
                break;

            case "tickle":
                usage(e.getMessage(), "Tickle", "tickle <@user>",
                        "Tickles the mentioned user."
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
