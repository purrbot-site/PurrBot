/*
 *  Copyright 2018 - 2021 Andre601
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 *  and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 *  OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;

@CommandDescription(
        name = "Help",
        description = "purr.info.help.description",
        triggers = {"help", "command", "commands"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}help [command]"),
                @CommandAttribute(key = "help", value = "{p}help [command]")
        }
)
public class CmdHelp implements Command{

    private final PurrBot bot;
    private final HashMap<String, String> categories = new LinkedHashMap<>();

    public CmdHelp(PurrBot bot){
        this.bot = bot;
        
        categories.put("fun", "\uD83C\uDF82");
        categories.put("guild", "\uD83C\uDFAE");
        categories.put("info", "\u2139");
        categories.put("nsfw", "\uD83D\uDC88");
        categories.put("owner", "<:andre_601:730944556119359588>");
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        String prefix = bot.getPrefix(msg.getGuild().getId());
        
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length != 0){
            Command command = (Command)bot.getCmdHandler().findCommand(args[0]);

            if(command == null || !isCommand(command)){
                int cat = getCategoryPage(guild.getId(), args[0]);
                
                if(cat == -1){
                    MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                            .setColor(0xFF0000)
                            .setDescription(
                                    bot.getMsg(guild.getId(), "purr.info.help.command_info.no_command")
                                            .replace("{command}", args[0])
                            )
                            .build();
    
                    tc.sendMessage(embed).queue();
                }else{
                    showHelpMenu(member, tc, cat);
                }
                return;
            }

            if(!tc.isNSFW() && command.getAttribute("category").equals("nsfw")){
                bot.getEmbedUtil().sendError(tc, member, "purr.info.help.command_info.command_nsfw");
                return;
            }

            tc.sendMessage(commandHelp(member, command, prefix)).queue();
        }else{
            showHelpMenu(member, tc, 1);
        }
    }
    
    private void showHelpMenu(Member member, TextChannel channel, int page){
        Guild guild = channel.getGuild();
        String prefix = bot.getPrefix(guild.getId());
        
        EmbedPaginator.Builder builder = new EmbedPaginator.Builder()
                .setEventWaiter(bot.getWaiter())
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
                String.join(
                        "\n",
                        bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.fun"),
                        bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.guild"),
                        bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.info"),
                        bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.nsfw")
                )
        ));
    
        for(Command cmd : getCommands()){
            String category = cmd.getAttribute("category");
        
            if(builders.get(category).length() + cmd.getAttribute("help").length()  + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                builder.addItems(commandList(
                        member,
                        categories.get(category),
                        "purr.info.help.command_menu.categories." + category,
                        builders.get(category).toString()
                ));
                builders.get(category).setLength(0);
            }
        
            builders.get(category)
                    .append(cmd.getAttribute("help").replace("{p}", prefix))
                    .append("\n");
        }
        
        for(Map.Entry<String, StringBuilder> builderEntry : builders.entrySet()){
            if(builderEntry.getKey().equals("owner") && bot.getCheckUtil().notDeveloper(member))
                continue;
        
            if(!channel.isNSFW() && builderEntry.getKey().equals("nsfw")){
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
        
        builder.build().paginate(channel, page);
    }
    
    private MessageEmbed commandHelp(Member member, Command cmd, String prefix){
        CommandDescription desc = cmd.getDescription();
        String[] triggers = desc.triggers();
        Guild guild = member.getGuild();
        
        EmbedBuilder commandInfo = bot.getEmbedUtil().getEmbed(member)
                .setTitle(
                        bot.getMsg(guild.getId(), "purr.info.help.command_info.title")
                                .replace("{command}", desc.name())
                )
                .setDescription(
                        bot.getMsg(guild.getId(), desc.description())
                )
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
        String id = member.getGuild().getId();
        return bot.getEmbedUtil().getEmbed(member)
                .addField(
                        bot.getMsg(id, "purr.info.help.command_menu.description.nav_title"),
                        bot.getMsg(id, "purr.info.help.command_menu.description.nav_value"),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        bot.getMsg(id, "purr.info.help.command_menu.description.command_value"),
                        false
                )
                .addField(
                        String.format(
                                "%s %s",
                                icon,
                                bot.getMsg(id, titlePath)
                        ),
                        String.format(
                                "```\n" +
                                "%s\n" +
                                "```",
                                commands
                        ),
                        false
                )
                .addField(
                        bot.getMsg(id, "purr.info.help.command_menu.support_title"),
                        bot.getMsg(id, "purr.info.help.command_menu.support_value"),
                        false
                )
                .build();
    }
    
    private List<Command> getCommands(){
        return bot.getCmdHandler().getCommands().stream()
                .map(cmd -> (Command)cmd)
                .sorted(Comparator.comparing(cmd -> cmd.getDescription().name()))
                .collect(Collectors.toList());
    }
    
    // Method to check, if text equals either a translated category name or the english one.
    private int getCategoryPage(String id, String text){
        String fun   = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.fun");
        String guild = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.guild");
        String info  = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.info");
        String nsfw  = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.nsfw");
        
        if(text.equalsIgnoreCase(fun) || text.equalsIgnoreCase("cat:fun")){
            return 2;
        }else
        if(text.equalsIgnoreCase(guild) || text.equalsIgnoreCase("cat:guild")){
            return 3;
        }else
        if(text.equalsIgnoreCase(info) || text.equalsIgnoreCase("cat:info")){
            return 4;
        }else
        if(text.equalsIgnoreCase(nsfw) || text.equalsIgnoreCase("cat:nsfw")){
            return 5;
        }else{
            return -1;
        }
    }
}
