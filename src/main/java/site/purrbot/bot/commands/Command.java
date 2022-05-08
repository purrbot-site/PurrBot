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

package site.purrbot.bot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.CommandUtil;

public interface Command{
    
    default void convert(SlashCommand command, SlashCommandEvent event, PurrBot bot){
        Guild guild = event.getGuild();
        if(!event.isFromGuild() || guild == null){
            CommandUtil.sendError(event, "Commands can only be used in a Server.");
            return;
        }
        
        Member member = event.getMember();
        if(member == null){
            CommandUtil.sendTranslatedError(event, bot, guild, "errors.member.not_found");
            return;
        }
    
        if(command.getCategory().getName().equalsIgnoreCase("nsfw") && !event.getTextChannel().isNSFW()){
            CommandUtil.sendTranslatedRandomError(event, bot, guild, "errors.no_nsfw_channel", member);
            return;
        }
        
        event.deferReply().queue(hook -> handle(event, hook, guild, event.getTextChannel(), member));
    }
    
    void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member);
}
