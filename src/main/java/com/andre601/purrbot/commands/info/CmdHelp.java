package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.commands.Command;
import com.github.rainestormee.jdacommand.AbstractCommand;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import static com.andre601.purrbot.core.PurrBot.COMMAND_HANDLER;
import static com.andre601.purrbot.core.PurrBot.waiter;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Help",
        description =
                "See all available commands.\n" +
                "Add a command after `help` for additional info about that command.",
        triggers = {"help", "commands", "command"},
        attributes = {
                @CommandAttribute(key = "info"),
                @CommandAttribute(key = "usage", value = "help [command]")
        }
)
public class CmdHelp implements Command {

    private Paginator.Builder pBuilder;

    private HashMap<String, String> categories = new LinkedHashMap<String, String>(){
        {
            put("fun", "\uD83C\uDFB2");
            put("guild", "\uD83C\uDFAE");
            put("info", "ℹ");
            put("nsfw", "\uD83D\uDC8B");
            put("owner", "<:andre_601:411527902648074240>");
        }
    };

    private static MessageEmbed commandHelp(Message msg, Command cmd, String prefix){
        CommandDescription description = cmd.getDescription();
        EmbedBuilder command = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Command: %s",
                        description.name()
                ))
                .setDescription(description.description())
                .addField("Usage:", String.format(
                        "`%s%s`",
                        prefix,
                        cmd.getAttribute("usage")
                ), true)
                .addField("Aliases:", String.format(
                        "`%s`",
                        String.join(", ", description.triggers())
                ), true);

        return command.build();
    }

    private static boolean isCommand(Command command){
        return command.getDescription() != null || command.hasAttribute("description");
    }

    private String firstUpperCase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String prefix = DBUtil.getPrefix(guild);

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        pBuilder = new Paginator.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

        HashMap<String, StringBuilder> builders = new LinkedHashMap<>();

        for(Map.Entry<String, String> category : categories.entrySet()){
            builders.put(category.getKey(), new StringBuilder());
        }

        for(Command command : PurrBot.COMMAND_HANDLER.getCommands()){
            String category;

            if(!command.hasAttribute("owner")){
                category = builders.keySet().stream().filter(command::hasAttribute).findFirst().get();
            }else{
                category = "owner";
            }

            builders.get(category).append(String.format(
                    "[`%s%s`](%s '%s')\n",
                    prefix,
                    command.getDescription().name(),
                    Links.WEBSITE.getLink(),
                    command.getDescription().description()
            ));
        }

        pBuilder.addItems(String.format(
                "Use the reactions, to navigate through the pages.\n" +
                "Run `%shelp [command]` or hover over a command, to get more information!\n" +
                "\n" +
                "```\n" +
                "Pages:\n" +
                "  Fun\n" +
                "  Guild\n" +
                "  Info\n" +
                "  NSFW\n" +
                "```\n" +
                "\n" +
                "**Random fact**:\n" +
                "%s",
                prefix,
                MessageUtil.getFact()
        ));

        for(Map.Entry<String, StringBuilder> builderEntry : builders.entrySet()){
            if(builderEntry.getKey().equals("owner") && !PermUtil.isCreator(msg)){
                continue;
            }
            if(!tc.isNSFW() && builderEntry.getKey().equals("nsfw")){
                builderEntry.getValue().setLength(0);
                builderEntry.getValue().append("`Run the command in a NSFW-labeled channel!\n`");
            }

            pBuilder.addItems(String.format(
                    "Use the reactions, to navigate through the pages.\n" +
                            "Run `%shelp [command]` or hover over a command, to get more information!\n" +
                            "\n" +
                            "%s **%s**\n" +
                            "%s" +
                            "\n" +
                            "**Random fact**:\n" +
                            "%s",
                    prefix,
                    categories.get(builderEntry.getKey()),
                    firstUpperCase(builderEntry.getKey()),
                    builderEntry.getValue(),
                    MessageUtil.getFact()
            ));
        }

        if(s.length() != 0){
            Command command = (Command)PurrBot.COMMAND_HANDLER.findCommand(s.split(" ")[0]);

            if(command == null || !isCommand(command)){
                EmbedUtil.error(msg, "This command does not exist!");
                return;
            }

            if(!tc.isNSFW() && command.hasAttribute("nsfw")){
                EmbedUtil.error(msg, "Please run this command in an NSFW-labeled channel!");
                return;
            }
            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            pBuilder.setText("")
                    .setFinalAction(message -> {
                        if(message != null)
                            message.delete().queue();
                    })
                    .waitOnSinglePage(false)
                    .setItemsPerPage(1)
                    .setColor(new Color(54, 57, 63))
                    .build()
                    .display(tc);
        }
    }
}
