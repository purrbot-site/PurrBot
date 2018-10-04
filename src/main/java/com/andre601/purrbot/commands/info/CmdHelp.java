package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import static com.andre601.purrbot.core.PurrBot.waiter;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Help",
        description =
                "See all available commands.\n" +
                "Add a command after `help` for additional info about that command.",
        triggers = {"help", "commands", "command"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdHelp implements Command {

    private static Paginator.Builder pBuilder =
            new Paginator.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    private static MessageEmbed commandHelp(Message msg, Command cmd, String prefix){
        CommandDescription description = cmd.getDescription();
        EmbedBuilder command = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(MessageFormat.format(
                        "Command: {0}",
                        description.name()
                ))
                .setDescription(description.description())
                .addField("Usage:", MessageFormat.format(
                        "`{0}{1}`",
                        prefix,
                        description.name()
                ), true)
                .addField("Aliases:", MessageFormat.format(
                        "`{0}`",
                        String.join(", ", description.triggers())
                ), true);

        return command.build();
    }

    private static boolean isCommand(Command command){
        return command.getDescription() != null || command.hasAttribute("description");
    }

    private static void usage(Message msg, String prefix){

        User user = msg.getAuthor();
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        Paginator page = pBuilder
                .setItems(MessageFormat.format(
                        "**Command-Prefix**: {0}\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Use the reactions to switch through the pages.\n" +
                        "Type `{0}help [command]` to get infos about a command!\n" +
                        "\n" +
                        "```\n" +
                        "Pages:\n" +
                        "  Fun\n" +
                        "  Informative\n" +
                        "  NSFW\n" +
                        "  Server\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact**:\n" +
                        "{1}",
                        prefix,
                        MessageUtil.getFact()
                ), MessageFormat.format(
                        "**Command-Prefix**: {0}\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Use the reactions to switch through the pages.\n" +
                        "Type `{0}help [command]` to get infos about a command!\n" +
                        "\n" +
                        "**Fun**:\n" +
                        "```\n" +
                        "Command:   Argument(s):\n" +
                        "\n" +
                        "Cuddle     <@user ...>\n" +
                        "Fakegit\n" +
                        "Gecg\n" +
                        "Hug        <@user ...>\n" +
                        "Kiss       <@user ...>\n" +
                        "Kitsune\n" +
                        "Neko       [-gif]\n" +
                        "           [-slide]\n" +
                        "Pat        <@user ...>\n" +
                        "Poke       <@user ...>\n" +
                        "Slap       <@user ...>\n" +
                        "Tickle     <@user ...>\n" +
                        "\n" +
                        "[optional]\n" +
                        "<required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact**:\n" +
                        "{1}",
                        prefix,
                        MessageUtil.getFact()
                ), MessageFormat.format(
                        "**Command-Prefix**: {0}\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Use the reactions to switch through the pages.\n" +
                        "Type `{0}help [command]` to get infos about a command!\n" +
                        "\n" +
                        "**Informative**:\n" +
                        "```\n" +
                        "Command:   Argument(s):\n" +
                        "\n" +
                        "Emote      <Emote>\n" +
                        "Help       [command]\n" +
                        "Info       [-here]\n" +
                        "Invite     [-here]\n" +
                        "Ping       [-api]\n" +
                        "Quote      <messageID> <#channel>\n" +
                        "Server\n" +
                        "Stats\n" +
                        "User       [@user]\n" +
                        "\n" +
                        "[optional]\n" +
                        "<required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact**:\n" +
                        "{1}",
                        prefix,
                        MessageUtil.getFact()
                ), MessageFormat.format(
                        "**Command-Prefix**: {0}\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Use the reactions to switch through the pages.\n" +
                        "Type `{0}help [command]` to get infos about a command!\n" +
                        "\n" +
                        "**NSFW**:\n" +
                        "```\n" +
                        "Command:   Argument(s):\n" +
                        "\n" +
                        "Fuck       <@user>\n" +
                        "Lewd       [-gif]\n" +
                        "           [-slide]\n" +
                        "Lesbian\n" +
                        "\n" +
                        "[optional]\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact**:\n" +
                        "{1}",
                        prefix,
                        MessageUtil.getFact()
                ), MessageFormat.format(
                        "**Command-Prefix**: {0}\n" +
                        "\n" +
                        "**Commands**:\n" +
                        "Use the reactions to switch through the pages.\n" +
                        "Type `{0}help [command]` to get infos about a command!\n" +
                        "\n" +
                        "**Server**:\n" +
                        "```\n" +
                        "Command:   Argument(s):\n" +
                        "\n" +
                        "Debug\n" +
                        "Prefix     [set <prefix>]\n" +
                        "           [reset]\n" +
                        "Welcome    [color <rgb:r,g,b|hex:#code>]\n" +
                        "           [reset]\n" +
                        "           [set <ChannelID> [image]]\n" +
                        "           [test [image]]\n" +
                        "\n" +
                        "[optional]\n" +
                        "<required>\n" +
                        "```\n" +
                        "\n" +
                        "**Random fact**:\n" +
                        "{1}",
                        prefix,
                        MessageUtil.getFact()
                ))
                .setText("")
                .setUsers(user, guild.getOwner().getUser())
                .setItemsPerPage(1)
                .waitOnSinglePage(true)
                .setFinalAction(message -> {
                    if(message != null)
                        message.delete().queue();
                })
                .build();

        page.display(tc);
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String prefix = DBUtil.getPrefix(guild);

        if(PermUtil.canDeleteMsg(tc))
            msg.delete().queue();

        if(s.length() != 0){
            Command command = PurrBot.COMMAND_HANDLER.findCommand(s.split(" ")[0]);

            if(command == null || !isCommand(command)){
                EmbedUtil.error(msg, "This command does not exist!");
                return;
            }
            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            usage(msg, prefix);
        }
    }
}
