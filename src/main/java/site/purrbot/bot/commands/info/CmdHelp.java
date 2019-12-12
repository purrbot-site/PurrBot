/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Links;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Help",
        description = "Get a list of all commands or get info about a specific command.",
        triggers = {"help", "command", "commands"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}help [command]"
                )
        }
)
public class CmdHelp implements Command{

    private PurrBot bot;

    public CmdHelp(PurrBot bot){
        this.bot = bot;
    }

    private HashMap<String, String> categories = new LinkedHashMap<String, String>(){
        {
            put("fun",   "\uD83C\uDFB2");
            put("guild", "\uD83C\uDFAE");
            put("info",  "ℹ");
            put("nsfw",  "\uD83D\uDC8B");
            put("owner", "<:andre_601:411527902648074240>");
        }
    };

    private MessageEmbed commandHelp(Message msg, Command cmd, String prefix){
        CommandDescription desc = cmd.getDescription();
        String[] triggers = desc.triggers();
        EmbedBuilder commandInfo = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Command-help: %s",
                        desc.name()
                )).setDescription(desc.description())
                .addField("Usage options", String.format(
                        "```\n" +
                        "%s\n" +
                        "```\n" +
                        "`[]` = optional\n" +
                        "`<>` = required",
                        cmd.getAttribute("usage").replace("{p}", prefix)
                ), false).addField("Aliases:", String.format(
                        "`%s`",
                        String.join(", ", triggers)
                ), false);

        return commandInfo.build();
    }

    private boolean isCommand(Command cmd){
        return cmd.getDescription() != null || cmd.hasAttribute("description");
    }

    private String firstUppercase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    @Override
    public void execute(Message msg, String args) {
        TextChannel tc = msg.getTextChannel();
        String prefix = bot.getPrefix(msg.getGuild().getId());

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        Paginator.Builder builder = new Paginator.Builder().setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);

        HashMap<String, StringBuilder> builders = new LinkedHashMap<>();

        for(Map.Entry<String, String> category : categories.entrySet()){
            builders.put(category.getKey(), new StringBuilder());
        }

        for(Command cmd : bot.getCmdHandler().getCommands().stream().map(ca -> (Command)ca).collect(Collectors.toList())){
            String category = cmd.getAttribute("category");

            builders.get(category).append(String.format(
                    "[`%s%s`](%s '%s')\n",
                    prefix,
                    cmd.getDescription().name(),
                    Links.WEBSITE.getUrl(),
                    cmd.getDescription().description()
            ));
        }

        builder.addItems(String.format(
                "Use the \u25C0 or \u25B6 reactions to navigate through the pages and \u23F9 to cancel it.\n" +
                "Hover over a command name (PC only) or use `%shelp [command]` for more info about a specific " +
                "command.\n" +
                "\n" +
                "```\n" +
                "Categories:\n" +
                "  Fun\n" +
                "  Guild\n" +
                "  Info\n" +
                "  NSFW\n" +
                "```",
                prefix
        ));

        for(Map.Entry<String, StringBuilder> builderEntry : builders.entrySet()){
            if(builderEntry.getKey().equals("owner") && !bot.getPermUtil().isDeveloper(msg.getAuthor()))
                continue;

            if(!tc.isNSFW() && builderEntry.getKey().equals("nsfw")){
                builderEntry.getValue().setLength(0);
                builderEntry.getValue().append("`Run the help command in a NSFW channel to see this!`\n");
            }

            builder.addItems(String.format(
                    "Use the \u25C0 or \u25B6 reactions to navigate through the pages and \u23F9 to cancel it.\n" +
                    "Hover over a command name (PC only) or use `%shelp [command]` for more info about a specific " +
                    "command.\n" +
                    "\n" +
                    "%s **%s**\n" +
                    "%s\n",
                    prefix,
                    categories.get(builderEntry.getKey()),
                    firstUppercase(builderEntry.getKey()),
                    builderEntry.getValue()
            ));
        }

        if(args.length() != 0){
            Command command = (Command)bot.getCmdHandler().findCommand(args.split(" ")[0]);

            if(command == null || !isCommand(command)){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "This command doesn't exist!");
                return;
            }

            if(!tc.isNSFW() && command.getAttribute("category").equals("nsfw")){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please run this command in a NSFW-channel!");
                return;
            }

            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            builder.setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                    .setFinalAction(message -> {
                        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
                            message.clearReactions().queue();
                    })
                    .waitOnSinglePage(false)
                    .setItemsPerPage(1)
                    .setColor(new Color(0x36393F))
                    .build()
                    .display(tc);
        }
    }
}
