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

package site.purrbot.bot.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import site.purrbot.bot.PurrBot;

import javax.annotation.Nonnull;
import java.io.InputStream;

public class MemberListener extends ListenerAdapter{
    
    private final PurrBot bot;
    
    public MemberListener(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
        Guild guild = event.getGuild();
    
        // Don't handle joins when the Guild has the Member screening active
        if(guild.getFeatures().contains("MEMBER_VERIFICATION_GATE_ENABLED"))
            return;
        
        if(event.getUser().isBot())
            return;
        
        sendWelcomeMessage(guild, event.getMember());
    }
    
    @Override
    public void onGuildMemberUpdatePending(@NotNull GuildMemberUpdatePendingEvent event){
        if(event.getNewPending())
            return;
        
        if(event.getUser().isBot())
            return;
        
        sendWelcomeMessage(event.getGuild(), event.getMember());
    }
    
    private void sendWelcomeMessage(Guild guild, Member member){
        String guildId = guild.getId();
        if(bot.getWelcomeChannel(guildId).equals("none"))
            return;
        
        TextChannel tc = guild.getTextChannelById(bot.getWelcomeChannel(guildId));
        if(tc == null)
            return;
        
        if(!guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_SEND))
            return;
        
        String message = bot.getWelcomeMsg(guildId);
        if(message == null)
            message = "Welcome {mention}!";
        
        InputStream image = bot.getImageUtil().getWelcomeImg(
                member,
                bot.getWelcomeIcon(guildId),
                bot.getWelcomeBg(guildId),
                bot.getWelcomeColor(guildId)
        );
        
        bot.getMessageUtil().sendWelcomeMsg(tc, message, member, image);
    }
}
