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

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.pagination.ButtonListener;
import site.purrbot.bot.util.pagination.Paginator;

import java.util.*;
import java.util.stream.Collectors;

public class CmdHelp extends SlashCommand{

    private final PurrBot bot;
    private final ButtonListener listener;
    private final HashMap<String, String> categories = new LinkedHashMap<>();
    
    public CmdHelp(PurrBot bot, ButtonListener listener){
        this.bot = bot;
        this.listener = listener;
        
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
                showMenu(hook, guild, event.getMember());
            }else{
                showHelp(hook, event.getTextChannel(), event.getGuild(), event.getMember(), name, category);
            }
        });
    }
    
    private void showHelp(InteractionHook hook, TextChannel tc, Guild guild, Member member, String name, boolean isCategory){
        if(isCategory){
            StringBuilder builder = new StringBuilder();
            Paginator paginator = new Paginator(hook);
            listener.addListener(paginator.getId(), paginator::onButtonClick);
            
            for(SlashCommand command : getSlashCommands(name)){
                String commandName = getSlashCommandSyntax(command);
                if(builder.length() + commandName.length() + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                    paginator.addPage(getCommandList(guild.getId(), name.toLowerCase(Locale.ROOT), builder.toString()));
                    
                    builder.setLength(0);
                }
                
                if(builder.length() > 0)
                    builder.append("\n");
                
                builder.append(commandName);
            }
            
            hook.editOriginal(paginator.getCurrent())
                .setActionRows(paginator.getButtons())
                .queue();
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
        
        for(OptionData option : options){
            if(option.isRequired())
                builder.append("\n  <").append(option.getName()).append(">");
            else
                builder.append("\n  [").append(option.getName()).append("]");
        }
        
        return builder.toString();
    }
    
    private void showMenu(InteractionHook hook, Guild guild, Member member){
        Paginator paginator = new Paginator(hook);
        listener.addListener(paginator.getId(), paginator::onButtonClick);
        
        paginator.addPage(getCommandList(
            guild.getId(),
            "title",
            String.join(
                "\n",
                bot.getMsg(guildId, ""),
                bot.getMsg(guildId, ""),
                bot.getMsg(guildId, ""),
                bot.getMsg(guildId, "")
            )
        )).addPages(getHelpPages(
            "fun",
            guild,
            false
        )).addPages(getHelpPages(
            "guild",
            guild,
            false
        )).addPages(getHelpPages(
            "info",
            guild,
            false
        )).addPages(getHelpPages(
            "nsfw",
            guild,
            true
        ));
        
        if(bot.getCheckUtil().isDeveloper(member)){
            paginator.addPages(getHelpPages(
                "owner",
                guild,
                false
            ));
        }
        
        hook.editOriginal(paginator.getCurrent())
            .setActionRows(paginator.getButtons())
            .queue();
    }
    
    private List<Message> getHelpPages(String category, Guild guild, boolean isNsfw){
        List<Message> messages = new LinkedList<>();
        
        if(category.equals("nsfw") && !isNsfw){
            messages.add(getCommandList(
                guild.getId(),
                category,
                bot.getMsg(guild.getId(), "purr.info.help.command_menu.nsfw_info")
            ));
            return messages;
        }
        
        StringBuilder builder = new StringBuilder();
        for(SlashCommand command : getSlashCommands(category)){
            String commandSyntax = getSlashCommandSyntax(command);
            if(builder.length() + commandSyntax.length() + 10 > MessageEmbed.VALUE_MAX_LENGTH){
                messages.add(getCommandList(
                    guild.getId(),
                    category,
                    builder.toString()
                ));
                
                builder.setLength(0);
            }
            
            if(builder.length() > 0)
                builder.append("\n");
            
            builder.append(commandSyntax);
            
        }
        
        messages.add(getCommandList(
            guild.getId(),
            category,
            builder.toString()
        ));
        
        return messages;
    }
    
    private Message getCommandList(String guildId, String title, String commands){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
            .addField(
                bot.getMsg(guildId, ""),
                bot.getMsg(guildId, ""),
                false
            )
            .addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                bot.getMsg(guildId, ""),
                false
            )
            .addField(
                EmbedBuilder.ZERO_WIDTH_SPACE,
                bot.getMsg(guildId, ""),
                false
            )
            .addField(
                String.format(
                    "%s%s",
                    title == null ? "" : categories.get(title) + " ",
                    bot.getMsg(guildId, "")
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
                bot.getMsg(guildId, ""),
                bot.getMsg(guildId, ""),
                false
            )
            .build();
        
        return new MessageBuilder(embed).build();
    }
}
