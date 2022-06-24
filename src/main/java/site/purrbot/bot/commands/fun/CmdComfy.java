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

package site.purrbot.bot.commands.fun;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import site.purrbot.bot.commands.BotCommand;
import site.purrbot.bot.util.enums.ImageAPIEndpoints;
import site.purrbot.bot.util.http.ImageAPI;

public class CmdComfy extends BotCommand{
    
    public CmdComfy(){
        this.name = "comfy";
        this.help = "Makes you feel comfy.";
        
        this.reply = "purr.fun.comfy.loading";
        
        this.guildOnly = true;
    }
    
    @Override
    protected void handle(SlashCommandEvent event, InteractionHook hook, Guild guild, TextChannel tc, Member member){
        ImageAPI.createRequest(ImageAPIEndpoints.COMFY, hook, guild.getId())
            .withReplacement("{user}", member.getEffectiveName())
            .returnImage();
    }
}
