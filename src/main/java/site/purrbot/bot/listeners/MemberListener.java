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

package site.purrbot.bot.listeners;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.util.WelcomeManager;

public class MemberListener extends ListenerAdapter{
    
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
        Guild guild = event.getGuild();
        if(guild.getFeatures().contains("MEMBER_VERIFICATION_GATE_ENABLED") && event.getMember().isPending())
            return;
        
        handleWelcome(guild, event.getMember());
    }
    
    @Override
    public void onGuildMemberUpdatePending(@NotNull GuildMemberUpdatePendingEvent event){
        if(event.getNewPending())
            return;
        
        handleWelcome(event.getGuild(), event.getMember());
    }
    
    private void handleWelcome(Guild guild, Member member){
        String channelId = PurrBot.getBot().getGuildSettingsManager().getWelcomeChannel(guild.getId());
        if(channelId == null || channelId.isEmpty())
            return;
        
        TextChannel tc = guild.getTextChannelById(channelId);
        if(tc == null)
            return;
        
        String msg = WelcomeManager.formatPlaceholders(
            PurrBot.getBot().getGuildSettingsManager().getWelcomeMessage(guild.getId()),
            guild,
            member
        );
    
        MessageBuilder msgBuilder = new MessageBuilder(msg);
        
    }
}
