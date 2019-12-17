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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.text.MessageFormat;

@CommandDescription(
        name = "Prefix",
        description = "Set or reset a prefix",
        triggers = {"prefix"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}prefix set <prefix>\n" +
                        "{p}prefix reset")
        }
)
public class CmdPrefix implements Command{

    private PurrBot bot;

    public CmdPrefix(PurrBot bot){
        this.bot = bot;
    }

    private void setPrefix(Message msg, String prefix){

        MessageEmbed embed = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setColor(0x00FF00)
                .setDescription(String.format(
                        "Prefix set to `%s`",
                        prefix
                ))
                .build();

        bot.setPrefix(msg.getGuild().getId(), prefix);

        msg.getTextChannel().sendMessage(embed).queue();
    }

    private void resetPrefix(Message msg){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .setColor(0x00FF00)
                .setDescription("Prefix was changed back to `.`")
                .build();

        bot.setPrefix(msg.getGuild().getId(), ".");

        msg.getTextChannel().sendMessage(embed).queue();
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length < 1){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                    "You need to provide arguments!\n" +
                    "Usage: `%sprefix <set <prefix>|reset>`",
                    bot.getPrefix(guild.getId())
            ));
            return;
        }

        if(args[0].equalsIgnoreCase("reset")){
            resetPrefix(msg);
        }else
        if(args[0].equalsIgnoreCase("set")){
            if(args.length == 1){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "You need to provide a prefix!");
            }else{
                setPrefix(msg, args[1].toLowerCase());
            }
        }else{
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), MessageFormat.format(
                    "You need to provide valid arguments!\n" +
                    "Usage: `{0}prefix <set <prefix>|reset>`",
                    bot.getPrefix(guild.getId())
            ));
        }
    }
}
