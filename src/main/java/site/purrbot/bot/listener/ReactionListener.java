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

package site.purrbot.bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter{
    
    private final PurrBot bot;
    
    public ReactionListener(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event){
        if(!bot.isBeta())
            return;
        
        if(!event.getGuild().getId().equals(IDs.GUILD))
            return;
        
        Guild guild = event.getGuild();
        
        Role nekoholic = guild.getRoleById(IDs.NEKOHOLIC);
        Role neko_lover = guild.getRoleById(IDs.NEKO_LOVER);
        
        if(nekoholic == null || neko_lover == null)
            return;
            
        Member member = event.getMember();
        if(member.getUser().isBot())
            return;
        
        if(!member.getRoles().contains(nekoholic) && !member.getRoles().contains(neko_lover))
            return;
    
        MessageReaction.ReactionEmote emote = event.getReactionEmote();
        if(emote.isEmoji())
            return;
        
        String id = event.getMessageId();
        TextChannel channel = event.getChannel();
    
        if(!channel.getId().equals(IDs.SUGGESTIONS))
            return;
        
        channel.retrieveMessageById(id).queue(message -> {
            if(emote.getId().equals(Emotes.ACCEPT.getId())){
                handleResult(message, guild, Result.ACCEPTED);
            }else
            if(emote.getId().equals(Emotes.CANCEL.getId())){
                handleResult(message, guild, Result.DENIED);
            }
        });
    }
    
    private void handleResult(Message msg, Guild guild, Result result){
        String url = msg.getAuthor().getEffectiveAvatarUrl();
        
        TextChannel tc;
        MessageEmbed embed;
        switch(result){
            case ACCEPTED:
                tc = guild.getTextChannelById(IDs.ACCEPTED_SUGGESTIONS);
                embed = new EmbedBuilder()
                        .setColor(0x00FF00)
                        .setDescription(msg.getContentRaw())
                        .setFooter("Suggested by " + msg.getAuthor().getName(), url)
                        .build();
                msg.delete().queue();
                break;
            
            case DENIED:
                tc = guild.getTextChannelById(IDs.DENIED_SUGGESTIONS);
                embed = new EmbedBuilder()
                        .setColor(0xFF0000)
                        .setDescription(msg.getContentRaw())
                        .setFooter("Suggested by " + msg.getAuthor().getName(), url)
                        .build();
                msg.delete().queue();
                break;
                
            default:
                return;
        }
        
        if(tc == null)
            return;
        
        tc.sendMessage(embed).queue();
    }
    
    private enum Result{
        ACCEPTED,
        DENIED
    }
}
