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

package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.OldCommand;
import site.purrbot.bot.constants.Links;
import site.purrbot.bot.util.file.lang.LangUtils;

import java.util.Collections;
import java.util.List;

@CommandDescription(
        name = "Language",
        description = "purr.guild.language.description",
        triggers = {"language", "lang"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}language\n" +
                        "{p}language set <language>\n" +
                        "{p}language reset"
                ),
                @CommandAttribute(key = "help", value = "{p}language [set <language>|reset]")
        }
)
public class CmdLanguage implements OldCommand{
    private final PurrBot bot;
    
    public CmdLanguage(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(args.length < 1){
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                    .setDescription(
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.description")
                                    .replace("{flag}", LangUtils.Language.getEmote(bot.getLanguage(guild.getId())))
                                    .replace("{language}", bot.getMsg(guild.getId(), "misc.language"))
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.available_lang_title"),
                            getLanguages(),
                            false
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.translators_title"),
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.translators_value")
                                    .replace("{link}", Links.TRANSLATORS),
                            false
                    )
                    .build();
            
            tc.sendMessageEmbeds(embed).queue();
        }else{
            if(args[0].equalsIgnoreCase("reset")){
                bot.setLanguage(guild.getId(), "en");
                
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                        .setColor(0x00FF00)
                        .setDescription("Language reset back to `English (en)`!")
                        .build();
                
                tc.sendMessageEmbeds(embed).queue();
            }else
            if(args[0].equalsIgnoreCase("set")){
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.language.few_args");
                    return;
                }
                
                if(!getLangList().contains(args[1].toLowerCase())){
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.language.invalid_lang");
                    return;
                }
                
                bot.setLanguage(guild.getId(), args[1].toLowerCase());
    
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                        .setColor(0x00FF00)
                        .setDescription(
                                bot.getMsg(guild.getId(), "purr.guild.language.language_set")
                        )
                        .build();
                
                tc.sendMessageEmbeds(embed).queue();
            }else{
                bot.getEmbedUtil().sendError(tc, member, "purr.guild.language.invalid_args");
            }
        }
    }
    
    private List<String> getLangList(){
        return bot.getFileManager().getLanguages();
    }
    
    private String getLanguages(){
        List<String> langs = getLangList();
        Collections.sort(langs);
        
        StringBuilder builder = new StringBuilder();
        for(String language : langs){
            if(builder.length() > 1)
                builder.append("\n");
            
            builder.append(LangUtils.Language.getString(language));
        }
        
        return builder.toString();
    }
}
