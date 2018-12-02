package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Links;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import static com.andre601.purrbot.core.PurrBot.waiter;

import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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

    private Paginator.Builder pBuilder;

    /**
     * A {@link java.util.LinkedHashMap LinkedHashMap<String, String>} for the categories and their corresponding
     * emojis.
     */
    private HashMap<String, String> categories = new LinkedHashMap<String, String>(){
        {
            put("fun", "\uD83C\uDFB2");
            put("guild", "\uD83C\uDFAE");
            put("info", "ℹ");
            put("nsfw", "\uD83D\uDC8B");
            put("owner", "<:andre_601:411527902648074240>");
        }
    };

    /**
     * Gives a {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed} with information.
     *
     * @param  msg
     *         Message that is used for the response.
     * @param  cmd
     *         A {@link com.github.rainestormee.jdacommand.Command Command object} that contains command info.
     * @param  prefix
     *         The used prefix of the guild.
     *
     * @return A MessageEmbed with the provided information and values.
     */
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

    /**
     * Check for if a command exists.
     *
     * @param  command
     *         A {@link com.github.rainestormee.jdacommand.Command Command object} to check.
     *
     * @return {@code true} when the command is not null or has the attribute {@code description}.
     */
    private static boolean isCommand(Command command){
        return command.getDescription() != null || command.hasAttribute("description");
    }

    /**
     * Makes the first character of the provided String uppercase.
     *
     * @param  word
     *         The String that gets uppercase on first character.
     *
     * @return String with the first letter being uppercase. (word -> Word)
     */
    private String firstUpperCase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String prefix = DBUtil.getPrefix(guild);

        if(PermUtil.canDeleteMsg(tc))
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
                    Links.GITHUB,
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
            Command command = PurrBot.COMMAND_HANDLER.findCommand(s.split(" ")[0]);

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
