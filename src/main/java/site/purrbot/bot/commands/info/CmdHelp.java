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
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.ButtonEmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.CommandUtil;
import site.purrbot.bot.util.message.EmbedUtil;

import java.util.*;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

public class CmdHelp extends SlashCommand{
    
    public CmdHelp(PurrBot bot){
        
        this.name = "purrhelp";
        this.help = "Lists all commands or provides info about an existing command/category.";
        
        this.children = new SlashCommand[]{
            new Category(bot),
            new Command(bot),
            new All(bot)
        };
    }
    
    @Override
    protected void execute(SlashCommandEvent event){}
    
    
    private static String getCommandString(SlashCommand command){
        StringBuilder commandBuilder = new StringBuilder("/" + command.getName());
        int commandLength = command.getName().length() + 1; // Length of /<command>
        
        if(command.getChildren().length != 0){
            appendChildCommands(command.getChildren(), commandBuilder, commandLength);
        }else{
            appendCommandOptions(command, commandBuilder, commandLength + 1);
        }
        
        return commandBuilder.toString();
    }
    
    private static void appendChildCommands(SlashCommand[] childCommands, StringBuilder builder, int spacing){
        boolean first = true;
        for(SlashCommand childCommand : childCommands){
            if(first){
                builder.append(" ");
                first = false;
            }else{
                builder.append("\n")
                    .append(" ".repeat(spacing + 1));
            }
            
            builder.append(childCommand.getName());
            
            appendCommandOptions(childCommand, builder, spacing + 3);
        }
    }
    
    private static void appendCommandOptions(SlashCommand command, StringBuilder builder, int spacing){
        if(command.getOptions().isEmpty())
            return;
        
        for(OptionData option : command.getOptions()){
            builder.append("\n")
                .append(" ".repeat(spacing))
                .append(option.isRequired() ? "<" : "[");
            
            switch(option.getType()){
                case USER:
                    builder.append("User: ");
                    break;
                
                case BOOLEAN:
                    builder.append("Boolean: ");
                    break;
                
                case CHANNEL:
                    builder.append("Channel: ");
                    break;
                
                case INTEGER:
                    builder.append("Integer: ");
                    if(option.getMinValue() != null && option.getMaxValue() != null){
                        builder.append(option.getMinValue())
                            .append("-")
                            .append(option.getMaxValue())
                            .append(option.isRequired() ? ">" : "]");
                        continue;
                    }
                    break;
            }
            
            builder.append(option.getName())
                .append(option.isRequired() ? ">" : "]");
        }
    }
    
    private static class Category extends SlashCommand implements site.purrbot.bot.commands.Command{
        
        private final PurrBot bot;
        
        public Category(PurrBot bot){
            this.bot = bot;
            
            this.name = "category";
            this.help = "Lists all commands of the provided category.";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "category", "The category to list commands from")
                    .setRequired(true)
                    .addChoice("fun", "fun")
                    .addChoice("guild", "guild")
                    .addChoice("info", "info")
                    .addChoice("nsfw", "nsfw")
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            this.convert(this, event, bot);
        }
    
        @Override
        public void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            String category = event.optString("category");
            if(category == null){
                CommandUtil.sendTranslatedError(hook, bot, guild, "purr.info.help.no_category");
                return;
            }
            
            if(category.equalsIgnoreCase("nsfw") && !tc.isNSFW()){
                CommandUtil.sendTranslatedError(hook, bot, guild, "purr.info.help.no_nsfw_channel");
                return;
            }
            
            switch(category.toLowerCase(Locale.ROOT)){
                case "fun":
                case "guild":
                case "info":
                case "nsfw":
                    List<SlashCommand> commands = findCommands(event, category);
                    if(commands.isEmpty()){
                        CommandUtil.sendTranslatedError(hook, bot, guild, "purr.info.help.no_commands");
                        return;
                    }
                    
                    hook.deleteOriginal().queue(v -> sendList(commands, tc));
                    break;
                default:
                    String msg = bot.getMsg(guild.getId(), "purr.info.help.unknown_category").replace("{category}", category);
                    CommandUtil.sendError(hook, msg);
            }
        }
        
        private void sendList(List<SlashCommand> commands, TextChannel channel){
            ButtonEmbedPaginator.Builder builder = new ButtonEmbedPaginator.Builder()
                .waitOnSinglePage(true)
                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                .wrapPageEnds(true)
                .setFinalAction(msg -> msg.editMessage(msg).setActionRows(Collections.emptyList()).queue(
                    null,
                    ignore(UNKNOWN_MESSAGE)
                ));
            
            StringJoiner joiner = new StringJoiner("\n");
            
            for(SlashCommand command : commands){
                String commandString = getCommandString(command);
                if(joiner.length() + commandString.length() >= MessageEmbed.DESCRIPTION_MAX_LENGTH){
                    builder.addItems(joiner.toString());
                    joiner = new StringJoiner("\n");
                }
    
                joiner.add(commandString);
            }
            
            builder.build().display(channel);
        }
        
        private List<SlashCommand> findCommands(SlashCommandEvent event, String category){
            return event.getClient().getSlashCommands().stream()
                .filter(command -> command.getCategory().getName().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        }
    }
    
    private static class Command extends SlashCommand implements site.purrbot.bot.commands.Command{
        
        private final PurrBot bot;
        
        public Command(PurrBot bot){
            this.bot = bot;
            
            this.name = "command";
            this.help = "Lets you see detailed info about a command";
            
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "command", "The command to get information from")
                    .setRequired(true)
            );
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            this.convert(this, event, bot);
        }
    
        @Override
        public void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            String name = event.optString("command");
            if(name == null){
                CommandUtil.sendTranslatedError(hook, bot, guild, "purr.info.help.no_command");
                return;
            }
            
            SlashCommand command = event.getClient().getSlashCommands().stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
            if(command == null){
                CommandUtil.sendTranslatedError(hook, bot, guild, "purr.info.help.unknown_command");
                return;
            }
            
            EmbedBuilder commandInfo = EmbedUtil.getEmbed()
                .setTitle(bot.getMsg(guild.getId(), "purr.info.help.command_info.title").replace("{command}", command.getName()))
                .setDescription(command.getHelp());
            
            StringBuilder commandSyntax = new StringBuilder("```").append("\n");
            String commandString = getCommandString(command);
            if(commandSyntax.length() + commandString.length() + 5 >= MessageEmbed.VALUE_MAX_LENGTH){
                commandSyntax.append("Too long to display.");
            }else{
                commandSyntax.append(commandString);
            }
            
            commandSyntax.append("\n").append("```");
            
            commandInfo.addField(
                bot.getMsg(guild.getId(), "purr.info.help.command_info.syntax"),
                commandSyntax.toString(),
                false
            );
            
            hook.editOriginalEmbeds(commandInfo.build()).queue();
        }
    }
    
    private static class All extends SlashCommand implements site.purrbot.bot.commands.Command{
        
        private final PurrBot bot;
        
        public All(PurrBot bot){
            this.bot = bot;
        }
        
        @Override
        protected void execute(SlashCommandEvent event){
            this.convert(this, event, bot);
        }
    
        @Override
        public void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
            hook.editOriginalEmbeds(
                EmbedUtil.getTranslatedEmbed(guild, bot, "purr.info.help.commands").build()
            ).queue();
        }
    }
}
