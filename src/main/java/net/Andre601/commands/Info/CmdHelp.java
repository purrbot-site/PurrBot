package net.Andre601.commands.Info;

import net.Andre601.commands.Command;
import net.Andre601.commands.server.CmdPrefix;
import net.Andre601.core.Main;
import net.Andre601.util.PermUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.Andre601.util.STATIC;

public class CmdHelp implements Command {

    private static String getFact(){
        return Main.getRandomFact().size() > 0 ? Main.getRandomFact().get(
                Main.getRandom().nextInt(Main.getRandomFact().size())) : "";
    }

    public static void usage(Message msg, String command, String use, String description){

        EmbedBuilder usageEmbed = new EmbedBuilder();
        usageEmbed.setAuthor(
                "Command: " + command, STATIC.URL,
                msg.getJDA().getSelfUser().getEffectiveAvatarUrl());
        usageEmbed.addField("Usage:", String.format(
                "`%s%s`",
                CmdPrefix.getPrefix(msg.getGuild()),
                use
        ), false);
        usageEmbed.addField("Description:", description, false);

        usageEmbed.setFooter(String.format(
                "Requested by: %s#%s | %s",
                msg.getAuthor().getName(),
                msg.getAuthor().getDiscriminator(),
                Main.now()
        ), msg.getAuthor().getEffectiveAvatarUrl());

        msg.getTextChannel().sendMessage(usageEmbed.build()).queue();
    }

    public static void usage(Message msg){

        EmbedBuilder help = new EmbedBuilder();

        help.setAuthor("Help", STATIC.URL,
                msg.getJDA().getSelfUser().getEffectiveAvatarUrl());

        help.addField("Command-Prefix:", String.format(
                "All commands start with the prefix `%s`",
                CmdPrefix.getPrefix(msg.getGuild())
        ), false);

        help.addField("Commands:", String.format(
                "type `%shelp [command]` to get infos about a command.\n" +
                "\n" +
                "**Information**\n" +
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
                "`Lewd` Gives you a lewd neko. >w<\n" +
                "\n" +
                "**Server**\n" +
                "`prefix` Get or set the prefix for the Discord.",
                CmdPrefix.getPrefix(msg.getGuild())
                ),
                false);
        help.addBlankField(false);

        help.addField("Random Fact:", getFact(), false);

        help.setFooter(String.format(
                "Requested by: %s#%s | %s",
                msg.getAuthor().getName(),
                msg.getAuthor().getDiscriminator(),
                Main.now()
        ), msg.getAuthor().getEffectiveAvatarUrl());

        msg.getTextChannel().sendMessage(help.build()).queue();
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
            usage(e.getMessage());
            return;
        }

        switch (args[0].toLowerCase()){

            case "help":
                usage(e.getMessage(), "Help", "help",
                        "Shows you all available commands."
                );
                break;

            case "info":
                usage(e.getMessage(), "Info", "info",
                        "Sends you basic infos about the bot (A small description, version, used " +
                        "Library, ect)"
                );
                break;

            case "invite":
                usage(e.getMessage(), "Invite", "invite",
                        "Sends you the invite-links for the bot and for the official Discord"
                );
                break;

            case "server":
                usage(e.getMessage(), "Server",  "server",
                        "Gives you basic server-infos like users online, verification level, ect."
                );
                break;

            case "user":
                usage(e.getMessage(), "User", "user [@user]",
                        "Gives you basic information about yourself.\n" +
                        "@Mention a user at the end of the command, to get infos about him/her."
                );
                break;

            case "neko":
                usage(e.getMessage(), "Neko", "neko",
                        "Sends a cute neko. 'nuf said."
                );
                break;

            case "hug":
                usage(e.getMessage(), "Hug", "hug <@user>",
                        "Gives the mentioned user a hug."
                );
                break;

            case "pat":
                usage(e.getMessage(), "Pat", "pat <@user>",
                        "Gives the mentioned user a pat."
                );
                break;

            case "slap":
                usage(e.getMessage(), "Slap", "slap <@user>",
                        "Slaps the mentioned user."
                );
                break;

            case "lewd":
                usage(e.getMessage(), "Lewd", "lewd",
                        "Sends a lewd neko.\n" +
                        "Can only be used in NSFW-Channels."
                );
                break;

            case "prefix":
                usage(e.getMessage(), "Prefix", "prefix [set <prefix>|reset]",
                        "Shows the currently used prefix in this Discord, if no argument is given.\n" +
                                "\n" +
                                "**Arguments:**\n" +
                                "`set <prefix>` Changes the prefix to the provided text.\n" +
                                "`reset` Resets the prefix to the default one.\n" +
                                "\n" +
                                "**Permissions**\n" +
                                "You need the `MANAGE_SERVER` permission, to either set or reset the prefix.");
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
