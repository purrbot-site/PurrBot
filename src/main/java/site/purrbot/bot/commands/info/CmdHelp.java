/*
 * Copyright 2018 - 2020 Andre601
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
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.ErrorResponse;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;

@CommandDescription(
        name = "Help",
        description = "Get a list of all commands or get info about a specific command.",
        triggers = {"help", "command", "commands"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}help [command]"),
                @CommandAttribute(key = "help", value = "{p}help [command]")
        }
)
public class CmdHelp implements Command{

    private final PurrBot bot;

    public CmdHelp(PurrBot bot){
        this.bot = bot;
    }

    private final HashMap<String, String> categories = new LinkedHashMap<String, String>(){
        {
            put("fun",   "\uD83C\uDFB2");
            put("guild", "\uD83C\uDFAE");
            put("info",  "\u2139");
            put("nsfw",  "\uD83D\uDC8B");
            put("owner", "<:andre_601:411527902648074240>");
        }
    };

    private MessageEmbed commandHelp(Message msg, Command cmd, String prefix){
        CommandDescription desc = cmd.getDescription();
        String[] triggers = desc.triggers();
        Guild guild = msg.getGuild();
        
        EmbedBuilder commandInfo = bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                .setTitle(
                        bot.getMsg(guild.getId(), "purr.info.help.command_info.title")
                                .replace("{command}", desc.name())
                )
                .setDescription(desc.description())
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.help.command_info.usage_title"),
                        bot.getMsg(guild.getId(), "purr.info.help.command_info.usage_value")
                                .replace("{commands}", cmd.getAttribute("usage").replace("{p}", prefix)), 
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.help.command_info.aliases"), 
                        String.format(
                                "`%s`",
                                String.join(", ", triggers)
                        ), 
                        false
                );

        return commandInfo.build();
    }

    private boolean isCommand(Command cmd){
        return cmd.getDescription() != null || cmd.hasAttribute("description");
    }

    private MessageEmbed commandList(Member member, String icon, String titlePath, String commands){
        return bot.getEmbedUtil().getEmbed(member.getUser(), member.getGuild())
                .setDescription(bot.getMsg(member.getGuild().getId(), "purr.info.help.command_menu.description"))
                .addField(
                        String.format(
                                "%s %s",
                                icon,
                                bot.getMsg(member.getGuild().getId(), titlePath)
                        ),
                        commands,
                        false
                )
                .build();
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        String prefix = bot.getPrefix(msg.getGuild().getId());
        
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
    
        EmbedPaginator.Builder builder = new EmbedPaginator.Builder().setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES)
                .waitOnSinglePage(true)
                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                .wrapPageEnds(true)
                .addUsers(member.getUser())
                .setFinalAction(message -> {
                    if(guild.getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_MANAGE))
                        message.clearReactions().queue(null, ignore(ErrorResponse.UNKNOWN_MESSAGE));
                });
        
        if(guild.getOwner() != null)
            builder.addUsers(guild.getOwner().getUser());

        HashMap<String, StringBuilder> builders = new LinkedHashMap<>();

        for(Map.Entry<String, String> category : categories.entrySet()){
            builders.put(category.getKey(), new StringBuilder());
        }
    
        builder.addItems(commandList(
                member,
                "",
                "purr.info.help.command_menu.categories.title",
                bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.list")
        ));
        
        for(Command cmd : bot.getCmdHandler().getCommands().stream().map(ca -> (Command)ca).collect(Collectors.toList())){
            String category = cmd.getAttribute("category");

            if(builders.get(category).length() + cmd.getAttribute("help").length() > MessageEmbed.VALUE_MAX_LENGTH){
                builder.addItems(commandList(
                        member,
                        category,
                        "purr.info.help.command_menu.categories." + category,
                        builders.get(category).toString()
                ));
                builders.get(category).setLength(0);
            }
            
            builders.get(category)
                    .append("`")
                    .append(cmd.getAttribute("help").replace("{p}", prefix))
                    .append("`\n");
        }


        for(Map.Entry<String, StringBuilder> builderEntry : builders.entrySet()){
            if(builderEntry.getKey().equals("owner") && !bot.getPermUtil().isDeveloper(member.getUser()))
                continue;
            
            if(!tc.isNSFW() && builderEntry.getKey().equals("nsfw")){
                builderEntry.getValue().setLength(0);
                builderEntry.getValue().append(
                        bot.getMsg(guild.getId(), "purr.info.help.command_menu.nsfw_info")
                );
            }

            builder.addItems(commandList(
                    member,
                    categories.get(builderEntry.getKey()),
                    "purr.info.help.command_menu.categories." + builderEntry.getKey(),
                    builderEntry.getValue().toString()
            ));
        }

        if(args.length != 0){
            Command command = (Command)bot.getCmdHandler().findCommand(args[0]);

            if(command == null || !isCommand(command)){
                bot.getEmbedUtil().sendError(tc, member.getUser(), "purr.info.help.command_info.no_command");
                return;
            }

            if(!tc.isNSFW() && command.getAttribute("category").equals("nsfw")){
                bot.getEmbedUtil().sendError(tc, member.getUser(), "purr.info.help.command_info.command_nsfw");
                return;
            }

            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            
            builder.build().display(tc);
        }
    }
}
