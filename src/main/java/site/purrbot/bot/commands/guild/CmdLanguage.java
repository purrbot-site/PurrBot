/*
 * Copyright 2019 Andre601
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

package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.util.file.lang.LangUtils;

import java.io.File;
import java.io.FilenameFilter;

@CommandDescription(
        name = "Language",
        description = 
                "Allows you to set the language used in the Discord.\n" +
                "Just write the command without any arguments, to see the currently used and all available languages.",
        triggers = {"language", "lang"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}language\n" +
                        "{p}language set <language>\n" +
                        "{p}language reset"
                )
        }
)
public class CmdLanguage implements Command{
    private PurrBot bot;
    
    public CmdLanguage(PurrBot bot){
        this.bot = bot;
    }
    
    private FilenameFilter filter = ((dir, name) -> name.endsWith(".json"));
    private File folder = new File("lang/");
    
    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.isEmpty() ? new String[0] : s.split("\\s+");
        
        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
    
        Member member = msg.getMember();
        if(member == null)
            return;
        
        if(args.length < 1){
            MessageEmbed embed = bot.getEmbedUtil().getEmbed(member.getUser(), guild)
                    .setDescription(bot.getMsg(guild.getId(), "purr.guild.language.embed.description"))
                    .addField(
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.curr_lang_title"),
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.curr_lang_value"),
                            true
                    )
                    .addField(
                            bot.getMsg(guild.getId(), "purr.guild.language.embed.available_lang_title"),
                            getLangs(),
                            true
                    )
                    .build();
            
            tc.sendMessage(embed).queue();
        }else{
            if(args[0].toLowerCase().equals("reset")){
                bot.setLanguage(guild.getId(), "en");
                
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member.getUser(), guild)
                        .setColor(0x00FF00)
                        .setDescription(
                                bot.getMsg(guild.getId(), "purr.guild.language.language_reset")
                        )
                        .build();
                
                tc.sendMessage(embed).queue();
            }else
            if(args[0].toLowerCase().equals("set")){
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, member.getUser(), "purr.guild.language.few_args");
                    return;
                }
                
                if(!LangUtils.getFileManager().exists(args[1].toLowerCase())){
                    bot.getEmbedUtil().sendError(tc, member.getUser(), "purr.guild.language.invalid_lang");
                    return;
                }
                
                bot.setLanguage(guild.getId(), args[1].toLowerCase());
    
                MessageEmbed embed = bot.getEmbedUtil().getEmbed(member.getUser(), guild)
                        .setColor(0x00FF00)
                        .setDescription(
                                bot.getMsg(guild.getId(), "purr.guild.language.language_set")
                        )
                        .build();
                
                tc.sendMessage(embed).queue();
            }else{
                bot.getEmbedUtil().sendError(tc, member.getUser(), "purr.guild.language.invalid_args");
            }
        }
    }
    
    private String getLangs(){
        File[] files = folder.listFiles(filter);
        if(files == null || files.length == 0)
            return "";
        
        StringBuilder builder = new StringBuilder();
        for(File file : files){
            builder.append(String.format(
                    "`%s`",
                    file.getName().replace(".json", "")
            )).append(", ");
        }
        
        return builder.toString();
    }
}
