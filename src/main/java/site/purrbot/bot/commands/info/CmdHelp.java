package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
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
                        "{p}help\n" +
                        "{p}help command"
                )
        }
)
public class CmdHelp implements Command{

    private PurrBot manager;

    public CmdHelp(PurrBot manager){
        this.manager = manager;
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
        EmbedBuilder commandInfo = manager.getEmbedUtil().getEmbed(msg.getAuthor())
                .setTitle(String.format(
                        "Command-help: %s",
                        desc.name()
                )).setDescription(desc.description())
                .addField("Usage", String.format(
                        "```\n" +
                        "%s\n" +
                        "```",
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
        Guild guild = msg.getGuild();
        String prefix = manager.getPrefixes().get(guild.getId(), k -> manager.getDbUtil().getPrefix(guild.getId()));

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        Paginator.Builder builder = new Paginator.Builder().setEventWaiter(manager.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);

        HashMap<String, StringBuilder> builders = new LinkedHashMap<>();

        for(Map.Entry<String, String> category : categories.entrySet()){
            builders.put(category.getKey(), new StringBuilder());
        }

        for(Command cmd : manager.getCmdHandler().getCommands().stream().map(ca -> (Command)ca).collect(Collectors.toList())){
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
                "Use the reactions to navigate through the pages.\n" +
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
            if(builderEntry.getKey().equals("owner") && !manager.getPermUtil().isDeveloper(msg.getAuthor()))
                continue;

            if(!tc.isNSFW() && builderEntry.getKey().equals("nsfw")){
                builderEntry.getValue().setLength(0);
                builderEntry.getValue().append("`Run the help command in a NSFW channel to see this!`\n");
            }

            builder.addItems(String.format(
                    "Use the reactions to navigate through the pages.\n" +
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
            Command command = (Command)manager.getCmdHandler().findCommand(args.split(" ")[0]);

            if(command == null || !isCommand(command)){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "This command doesn't exist!");
                return;
            }

            if(!tc.isNSFW() && command.getAttribute("category").equals("nsfw")){
                manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please run this command in a NSFW-channel!");
                return;
            }

            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            builder.setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                    .setFinalAction(message -> {
                        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
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
