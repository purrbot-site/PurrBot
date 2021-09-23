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
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.PurrBot;

import java.util.*;
import java.util.stream.Collectors;

public class CmdHelp extends SlashCommand{

    private final PurrBot bot;
    
    public CmdHelp(PurrBot bot){
        this.bot = bot;
        
        this.name = "purrhelp";
        this.help = "Lists all commands or provides info about an existing command/category.";
        
        this.options = Collections.singletonList(
            new OptionData(OptionType.STRING, "name", "The name of the command or category.")
        );
    }
    
    @Override
    protected void execute(SlashCommandEvent event){
        String name = bot.getCommandUtil().getString(event, "name", null);
        
        Guild guild = event.getGuild();
        if(guild == null){
            bot.getEmbedUtil().sendGuildError(event);
            return;
        }
        
        event.deferReply().queue(hook -> {
            if(name == null){
                MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                    .setDescription(
                        bot.getMsg(guild.getId(), "purr.info.help.command_info.link")
                    ).build();
                
                hook.editOriginalEmbeds(embed).queue();
            }else{
                showHelp(hook, event.getTextChannel(), event.getGuild(), event.getMember(), name);
            }
        });
    }
    
    private void showHelp(InteractionHook hook, TextChannel tc, Guild guild, Member member, String name){
        for(SlashCommand command : getSlashCommands()){
            if(!command.getName().equalsIgnoreCase(name))
                continue;
            
            showCommandHelp(guild, tc, member, command);
            return;
        }
        
        bot.getEmbedUtil().sendError(hook, guild, member, "purr.info.help.command_not_found");
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
    
    private List<SlashCommand> getSlashCommands(){
        return this.getClient().getSlashCommands().stream()
            .sorted(Comparator.comparing(SlashCommand::getName))
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
}
