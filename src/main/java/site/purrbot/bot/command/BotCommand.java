/*
 *  Copyright 2018 - 2022 Andre601
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

package site.purrbot.bot.command;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.manager.command.CommandError;
import site.purrbot.bot.manager.string.MessageHandler;

import java.util.Arrays;

public abstract class BotCommand extends SlashCommand{
    
    protected String[] replyMsgPath = new String[]{"misc", "default_loading"};
    
    @Override
    protected void execute(SlashCommandEvent event){
        Guild guild = event.getGuild();
        if(guild == null){
            CommandError.fromStatic("Commands can only be executed in a Server.").send(event);
            return;
        }
    
        Member member = event.getMember();
        if(member == null){
            CommandError.fromPath(guild.getId(), "errors", "null_member").send(event);
            return;
        }
        
        BotCommand command = Arrays.stream(PurrBot.getBot().getCommandLoader().getCommands())
            .filter(cmd -> cmd.getName().equals(event.getName()))
            .findFirst()
            .orElse(null);
        
        if(command == null){
            CommandError.fromPath(guild.getId(), "errors", "no_command_found")
                .replace("{command}", event.getName())
                .send(event);
            return;
        }

        TextChannel tc = event.getTextChannel();

        if(command.nsfwOnly && !tc.isNSFW()){
            CommandError.fromPath(guild.getId(), "errors", "no_nsfw")
                .replace("{user}", member.getEffectiveName())
                .send(event);
            return;
        }
        
        event.reply(MessageHandler.getTranslation(guild.getId(), replyMsgPath).getMessage()).queue(
            hook -> handle(event, hook, guild, tc, member)
        );
    }
    
    protected abstract void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member);
}
