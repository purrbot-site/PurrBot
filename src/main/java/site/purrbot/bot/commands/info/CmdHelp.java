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

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public class CmdHelp extends SlashCommand implements Command{

    private final PurrBot bot;
    private final HashMap<String, String> categories = new LinkedHashMap<>();

    private final Logger logger = (Logger)LoggerFactory.getLogger("Command - Help");
    
    public CmdHelp(PurrBot bot){
        this.bot = bot;
        
        this.name = "purrhelp";
        this.help = "Lists all commands or provides info about an existing command/category.";
        this.guildOnly = true;
        
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "name", "The name of the command or category."),
            new OptionData(OptionType.BOOLEAN, "category", "If the name should be treated as category.")
        );
        
        categories.put("fun", "\uD83C\uDFB2");
        categories.put("guild", "\uD83C\uDFAE");
        categories.put("info", "\u2139");
        categories.put("nsfw", "\uD83D\uDC8B");
        categories.put("owner", "<:andre_601:730944556119359588>");
    }
    
    @Override
    protected void execute(SlashCommandEvent event){
        String name = bot.getCommandUtil().getString(event, "name", null);
        boolean category = bot.getCommandUtil().getBoolean(event, "category", false);
        
        Guild guild = event.getGuild();
        if(guild == null){
            bot.getEmbedUtil().sendGuildError(event);
            return;
        }
        
        event.deferReply().queue(hook -> {
            if(name == null){
                showMenu(hook, guild, event.getMember(), event.getTextChannel());
            }else{
                showHelp(hook, event.getTextChannel(), event.getGuild(), event.getMember(), name, category);
            }
        });
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        String prefix = bot.getPrefix(msg.getGuild().getId());

        if(args.length > 0){
            Command command = (Command)bot.getCmdHandler().findCommand(args[0]);

            if(command == null || !isCommand(command)){
                showHelpMenu(member, tc, args[0]);
                return;
            }

            if(!tc.isNSFW() && command.getAttribute("category").equals("nsfw")){
                bot.getEmbedUtil().sendError(tc, member, "purr.info.help.command_info.command_nsfw");
                return;
            }

            tc.sendMessageEmbeds(commandHelp(member, command, prefix))
                    .setActionRow(Button.link(
                            "https://docs.purrbot.site/bot/commands#" + command.getDescription().name().toLowerCase(Locale.ROOT),
                            bot.getMsg(guild.getId(), "purr.info.help.command_info.docs")
                    ))
                    .queue();
        }else{
            showHelpMenu(member, tc, null);
        }
    }
    
    private void showHelp(InteractionHook hook, TextChannel tc, Guild guild, Member member, String name, boolean isCategory){
        hook.deleteOriginal().queue();
        
        
        if(isCategory){
            StringBuilder builder = new StringBuilder();
            EmbedPaginator.Builder paginator = getPaginator(member, tc);
            
            for(SlashCommand command : getSlashCommands(name)){
                String commandName = getSlashCommandSyntax(command);
                if(builder.length() + commandName.length() + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                    paginator.addItems(slashCommandList(member, name.toLowerCase(Locale.ROOT), builder.toString()));
                    
                    builder.setLength(0);
                }
                
                if(builder.length() > 0)
                    builder.append("\n");
                
                builder.append(commandName);
            }
            
            paginator.build().display(tc);
        }else{
            for(SlashCommand command : getSlashCommands(null)){
                if(!command.getName().equalsIgnoreCase(name))
                    continue;
                
                showCommandHelp(guild, tc, member, command);
                return;
            }
            
            bot.getEmbedUtil().sendError(hook, guild, member, "purr.info.help.command_not_found");
        }
    }
    
    private void showCommandHelp(Guild guild, TextChannel tc, Member member, SlashCommand command){
        String syntax = getSlashCommandSyntax(command);
        
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
            .setTitle(
                bot.getMsg(guild.getId(), "purr.info.help.command_info.title")
                   .replace("{command}", command.getName())
            ).setDescription(bot.getMsg(guild.getId(), String.format(
                "purr.%s.%s.description",
                command.getCategory().getName().toLowerCase(Locale.ROOT),
                command.getName()
            ))).addField(
                bot.getMsg(guild.getId(), "purr.info.help.command_info.usage_title"),
                bot.getMsg(guild.getId(), "purr.info.help.command_info.usage_value")
                    .replace("{commands}", syntax),
                false
            ).build();
        
        tc.sendMessageEmbeds(embed).queue();
    }
    
    private List<SlashCommand> getSlashCommands(String category){
        return this.getClient().getSlashCommands().stream()
            .sorted(Comparator.comparing(SlashCommand::getName))
            .filter(command -> {
                if(category == null)
                    return true;
                
                return command.getCategory().getName().equalsIgnoreCase(category);
            })
            .collect(Collectors.toList());
    }
    
    private String getSlashCommandSyntax(SlashCommand command){
        List<OptionData> options = command.getOptions();
        StringBuilder builder = new StringBuilder("/").append(command.getName());
        
        if(options.isEmpty())
            return builder.toString();
        
        for(OptionData option : options){
            if(option.isRequired())
                builder.append("\n  <").append(option.getName()).append(">");
            else
                builder.append("\n  [").append(option.getName()).append("]");
        }
        
        return builder.toString();
    }
    
    private void showMenu(InteractionHook hook, Guild guild, Member member, TextChannel tc){
        hook.deleteOriginal().queue();
        EmbedPaginator.Builder builder = getPaginator(member, tc);
        
        builder.addItems(
            commandList(
                member,
                "title",
                String.join(
                    "\n",
                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.fun"),
                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.guild"),
                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.info"),
                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.nsfw")
                )
            )
        );
        
        builder.addItems(getHelpPages(
            "fun",
            guild,
            member,
            false
        )).addItems(getHelpPages(
            "guild",
            guild,
            member,
            false
        )).addItems(getHelpPages(
            "info",
            guild,
            member,
            false
        )).addItems(getHelpPages(
            "nsfw",
            guild,
            member,
            true
        ));
        
        if(bot.getCheckUtil().isDeveloper(member)){
            builder.addItems(getHelpPages(
                "owner",
                guild,
                member,
                false
            ));
        }
        
        builder.build().display(tc);
    }
    
    private void showHelpMenu(Member member, TextChannel tc, String category){
        Guild guild = tc.getGuild();
        String prefix = bot.getPrefix(guild.getId());
        
        if(category == null){
            EmbedPaginator.Builder builder = getPaginator(member, tc);
    
            builder.addItems(
                    commandList(
                            member,
                            "title",
                            String.join(
                                    "\n",
                                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.fun"),
                                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.guild"),
                                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.info"),
                                    bot.getMsg(guild.getId(), "purr.info.help.command_menu.categories.nsfw")
                            )
                    )
            );
            
            builder.addItems(getHelpPages(
                    "fun",
                    member,
                    prefix
            )).addItems(getHelpPages(
                    "guild",
                    member,
                    prefix
            )).addItems(getHelpPages(
                    "info",
                    member,
                    prefix
            )).addItems(getHelpPages(
                    "nsfw",
                    member,
                    prefix,
                    tc.isNSFW()
            ));
            
            if(bot.getCheckUtil().isDeveloper(member)){
                builder.addItems(getHelpPages(
                        "owner",
                        member,
                        prefix
                ));
            }
            
            builder.build().display(tc);
        }else{
            String cat = getCategory(guild.getId(), category);
            
            if(cat == null){
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                        .setColor(0xFF0000)
                        .setDescription(
                                bot.getMsg(guild.getId(), "purr.info.help.command_info.no_command")
                                        .replace("{command}", category)
                        )
                        .build();
                
                tc.sendMessageEmbeds(embed).queue();
                return;
            }
            
            List<MessageEmbed> pages = getHelpPages(cat, member, prefix, tc.isNSFW());
            
            if(pages.size() == 1){
                tc.sendMessageEmbeds(pages.get(0)).queue();
                return;
            }
            
            EmbedPaginator.Builder builder = getPaginator(member, tc);
            
            builder.addItems(pages).build().display(tc);
        }
        
    }
    
    private List<MessageEmbed> getHelpPages(String category, Guild guild, Member member, boolean isNsfw){
        List<MessageEmbed> embeds = new LinkedList<>();
        
        if(category.equals("nsfw") && !isNsfw){
            embeds.add(commandList(
                member,
                category,
                bot.getMsg(guild.getId(), "purr.info.help.command_menu.nsfw_info")
            ));
            return embeds;
        }
        
        StringBuilder builder = new StringBuilder();
        for(SlashCommand command : getSlashCommands(category)){
            String commandSyntax = getSlashCommandSyntax(command);
            if(builder.length() + commandSyntax.length() + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                embeds.add(slashCommandList(
                    member,
                    category,
                    builder.toString()
                ));
                
                builder.setLength(0);
            }
            
            if(builder.length() > 0)
                builder.append("\n");
            
            builder.append(commandSyntax);
            
        }
        
        embeds.add(slashCommandList(
            member,
            category,
            builder.toString()
        ));
        
        return embeds;
    }
    
    private List<MessageEmbed> getHelpPages(String category, Member member, String prefix){
        return getHelpPages(category, member, prefix, false);
    }
    
    private List<MessageEmbed> getHelpPages(String category, Member member, String prefix, boolean isNsfw){
        List<MessageEmbed> embeds = new LinkedList<>();
    
        if(category.equals("nsfw") && !isNsfw){
            embeds.add(commandList(
                    member,
                    category,
                    bot.getMsg(member.getGuild().getId(), "purr.info.help.command_menu.nsfw_info")
            ));
            return embeds;
        }
        
        StringBuilder builder = new StringBuilder();
        List<Command> commands = getCommands(category);
        
        
        for(Command command : commands){
            if(builder.length() + command.getAttribute("help").length() + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                embeds.add(commandList(
                        member,
                        category,
                        builder.toString()
                ));
                
                builder.setLength(0);
            }
            
            builder.append(command.getAttribute("help").replace("{p}", prefix))
                   .append("\n");
        }
        
        embeds.add(commandList(
                member,
                category,
                builder.toString()
        ));
        
        return embeds;
    }
    
    private List<Command> getCommands(String category){
        return bot.getCmdHandler().getCommands().stream()
                .map(cmd -> (Command)cmd)
                .sorted(Comparator.comparing(cmd -> cmd.getDescription().name()))
                .filter(cmd -> cmd.getAttribute("category").equals(category))
                .collect(Collectors.toList());
    }
    
    private EmbedPaginator.Builder getPaginator(Member member, TextChannel tc){
        EmbedPaginator.Builder builder = new EmbedPaginator.Builder()
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES)
                .waitOnSinglePage(true)
                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                .wrapPageEnds(true)
                .addUsers(member.getUser())
                .setFinalAction(message -> {
                    if(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
                        message.clearReactions().queue(
                                null,
                                e -> logger.warn("Couldn't clear reactions from message.")
                        );
                });
        
        if(tc.getGuild().getOwner() != null)
            builder.addUsers(tc.getGuild().getOwner().getUser());
        
        return builder;
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
    
    private MessageEmbed slashCommandList(Member member, String title, String commands){
        String guildId = member.getGuild().getId();
        
        return bot.getEmbedUtil().getEmbed(member)
            .addField(
                bot.getMsg(guildId, "purr.info.help.command_menu.description.nav_title"),
                bot.getMsg(guildId, "purr.info.help.command_menu.description.nav_value"),
                false
            ).addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                bot.getMsg(guildId, "purr.info.help.command_menu.description.command_value"),
                false
            ).addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                bot.getMsg(guildId, "purr.info.help.command_menu.description.support"),
                false
            ).addField(
                String.format(
                    "%s%s",
                    title.equals("title") ? "" : categories.get(title) + " ",
                    bot.getMsg(guildId, "purr.info.help.command_menu.categories." + title)
                ),
                String.format(
                    "```\n" +
                    "%s\n" +
                    "```",
                    commands
                ),
                false
            ).addField(
                bot.getMsg(guildId, "purr.info.help.command_menu.support_title"),
                bot.getMsg(guildId, "purr.info.help.command_menu.support_value"),
                false
            ).build();
    }
    
    private MessageEmbed commandList(Member member, String title, String commands){
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
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        bot.getMsg(id, "purr.info.help.command_menu.description.support"),
                        false
                )
                .addField(
                        String.format(
                                "%s%s",
                                title.equals("title") ? "" : categories.get(title) + " ",
                                bot.getMsg(id, "purr.info.help.command_menu.categories." + title)
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
    
    // Method to check, if text equals either a translated category name or the english one.
    private String getCategory(String id, String text){
        String fun   = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.fun");
        String guild = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.guild");
        String info  = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.info");
        String nsfw  = "cat:" + bot.getMsg(id, "purr.info.help.command_menu.categories.nsfw");
        
        if(text.equalsIgnoreCase(fun) || text.equalsIgnoreCase("cat:fun")){
            return "fun";
        }else
        if(text.equalsIgnoreCase(guild) || text.equalsIgnoreCase("cat:guild")){
            return "guild";
        }else
        if(text.equalsIgnoreCase(info) || text.equalsIgnoreCase("cat:info")){
            return "info";
        }else
        if(text.equalsIgnoreCase(nsfw) || text.equalsIgnoreCase("cat:nsfw")){
            return "nsfw";
        }else{
            return null;
        }
    }
}
