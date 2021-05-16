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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.io.IOException;
import java.io.InputStream;

@CommandDescription(
        name = "Welcome",
        description = "purr.guild.welcome.description",
        triggers = {"welcome"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}welcome\n" +
                        "{p}welcome bg set <image>\n" +
                        "{p}welcome bg reset\n" +
                        "{p}welcome channel set <#channel>\n" +
                        "{p}welcome channel reset\n" +
                        "{p}welcome color set <color>\n" +
                        "{p}welcome color reset\n" +
                        "{p}welcome icon set <icon>\n" +
                        "{p}welcome icon reset\n" +
                        "{p}welcome msg set <message>\n" +
                        "{p}welcome msg reset\n" +
                        "{p}welcome test"
                ),
                @CommandAttribute(key = "help", value = "{p}welcome [options]")
        }
)
public class CmdWelcome implements Command{

    private final PurrBot bot;

    public CmdWelcome(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(args.length < 1){
            tc.sendTyping().queue(v -> {
                InputStream image;
                try{
                    image = bot.getImageUtil().getWelcomeImg(
                            msg.getMember(),
                            bot.getWelcomeIcon(guild.getId()),
                            bot.getWelcomeBg(guild.getId()),
                            bot.getWelcomeColor(guild.getId())
                    );
                }catch(IOException ex){
                    image = null;
                }
                
                welcomeSettings(tc, member, image);
            });
            return;
        }

        switch (args[0].toLowerCase()){
            case "bg":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.few_args");
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(tc, member, Type.BACKGROUND);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.no_bg");
                        return;
                    }
                    if(!bot.getWelcomeBg().contains(args[2].toLowerCase()) && !args[2].equalsIgnoreCase("random")){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_bg");
                        return;
                    }
                    update(tc, member, Type.BACKGROUND, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_args");
                }
                break;

            case "channel":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.few_args");
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(tc, member, Type.CHANNEL);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(msg.getMentionedChannels().isEmpty()){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.no_channel");
                        return;
                    }
                    if(msg.getMentionedChannels().get(0) == null){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_channel");
                        return;
                    }
                    update(tc, member, Type.CHANNEL, msg.getMentionedChannels().get(0).getId());
                }else{
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_args");
                }
                break;

            case "color":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.few_args");
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(tc, member, Type.COLOR);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.no_color");
                        return;
                    }

                    if(bot.getMessageUtil().getColor(args[2].toLowerCase()) == null){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_color");
                        return;
                    }

                    update(tc, member, Type.COLOR, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_args");
                }
                break;

            case "icon":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.few_args");
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(tc, member, Type.ICON);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.no_icon");
                        return;
                    }
                    if(!bot.getWelcomeIcon().contains(args[2].toLowerCase()) && !args[2].equalsIgnoreCase("random")){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_icon");
                        return;
                    }
                    update(tc, member, Type.ICON, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_args");
                }
                break;

            case "msg":
                if(args.length == 1){
                    String roleId = member.getRoles().size() >= 1 ? member.getRoles().get(0).getId() : guild.getPublicRole().getId();
                    
                    MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                            .addField(
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.placeholders.message_title"),
                                    getMsg(bot.getWelcomeMsg(guild.getId())),
                                    false
                            )
                            .addField(
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.placeholders.placeholders_title"),
                                    "`{count}`\n" +
                                    "`{members}`\n" +
                                    "\n" +
                                    "`{count_formatted}`\n" +
                                    "`{members_formatted}`\n" +
                                    "\n" +
                                    "`{guild}`\n" +
                                    "`{server}`\n" +
                                    "\n" +
                                    "`{mention}`\n" +
                                    "\n" +
                                    "`{name}`\n" +
                                    "`{username}`\n" +
                                    "\n" +
                                    "`{c_mention:<id>}`\n" +
                                    "`{c_name:<id>}`\n" +
                                    "\n" +
                                    "`{r_mention:<id>}`\n" +
                                    "`{r_name:<id>}`\n" +
                                    "\n" +
                                    "`{tag}`",
                                    true
                            )
                            .addField(
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.placeholders.result_title"),
                                    bot.getMessageUtil().formatPlaceholders(String.format(
                                            "{count}\n" +
                                            "{members}\n" +
                                            "\n" +
                                            "{count_formatted}\n" +
                                            "{members_formatted}\n" +
                                            "\n" +
                                            "{guild}\n" +
                                            "{server}\n" +
                                            "\n" +
                                            "{mention}\n" +
                                            "\n" +
                                            "{name}\n" +
                                            "{username}\n" +
                                            "\n" +
                                            "{c_mention:%s}\n" +
                                            "{c_name:%s}\n" +
                                            "\n" +
                                            "{r_mention:%s}\n" +
                                            "{r_name:%s}\n" +
                                            "\n" +
                                            "{tag}",
                                            tc.getId(),
                                            tc.getId(),
                                            roleId,
                                            roleId
                                    ), member),
                                    true
                            )
                            .addField(
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.placeholders.note_title"),
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.placeholders.note_value"),
                                    false
                            )
                            .build();
                    
                    tc.sendMessage(embed).queue();
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(tc, member, Type.MESSAGE);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.no_msg");
                        return;
                    }
                    update(tc, member, Type.MESSAGE, args[2]);
                }
                break;
            
            case "test":
                tc.sendTyping().queue(v -> {
                    InputStream image;
                    try{
                        image = bot.getImageUtil().getWelcomeImg(
                                member,
                                bot.getWelcomeIcon(guild.getId()),
                                bot.getWelcomeBg(guild.getId()),
                                bot.getWelcomeColor(guild.getId())
                        );
                    }catch(IOException ex){
                        image = null;
                    }
                    
                    bot.getMessageUtil().sendWelcomeMsg(tc, bot.getWelcomeMsg(guild.getId()), member, image);
                });
                break;

            default:
                tc.sendTyping().queue(v -> {
                    InputStream image;
                    try{
                        image = bot.getImageUtil().getWelcomeImg(
                                member,
                                bot.getWelcomeIcon(guild.getId()),
                                bot.getWelcomeBg(guild.getId()),
                                bot.getWelcomeColor(guild.getId())
                        );
                    }catch(IOException ex){
                        image = null;
                    }
    
                    welcomeSettings(tc, member, image);
                });
        }
    }
    
    private String getMsg(String msg){
        return String.format(
                "```md\n" +
                "%s\n" +
                "```",
                msg.length() > 1000 ? msg.substring(0, 990) + "..." : msg
        );
    }
    
    private void welcomeSettings(TextChannel tc, Member member, InputStream file){
        Guild guild = tc.getGuild();
        String welcomeChannel = getWelcomeChannel(guild.getId());
        
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(member)
                .setTitle(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.title")
                )
                .setDescription(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.current_settings")
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.subcommands_title"),
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.subcommands_value"),
                        false
                )
                .addField(bot.getMsg(guild.getId(), "purr.guild.welcome.embed.channel"), String.format(
                        "%s",
                        welcomeChannel
                ), false)
                .addField(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.image_title"),
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.image_value")
                                .replace("{background}", bot.getWelcomeBg(guild.getId()))
                                .replace("{icon}", bot.getWelcomeIcon(guild.getId()))
                                .replace("{color}", bot.getWelcomeColor(guild.getId())),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.message_title"),
                        getMsg(bot.getWelcomeMsg(guild.getId())),
                        false
                );
        
        if(file == null || !guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
            tc.sendMessage(embed.build()).queue();
        }else{
            embed.addField(
                    bot.getMsg(guild.getId(), "purr.guild.welcome.embed.preview"),
                    EmbedBuilder.ZERO_WIDTH_SPACE,
                    false
            )
                    .setImage("attachment://welcome_preview.jpg");
            
            tc.sendMessage(embed.build())
                    .addFile(file, "welcome_preview.jpg")
                    .queue();
        }
        
    }
    
    private void update(TextChannel tc, Member member, Type type, String value){
        String id = member.getGuild().getId();
        
        switch(type){
            case BACKGROUND:
                bot.setWelcomeBg(id, value);
                value = "`" + value + "`";
                break;
            
            case CHANNEL:
                bot.setWelcomeChannel(id, value);
                value = "<#" + value + ">";
                break;
            
            case COLOR:
                bot.setWelcomeColor(id, value);
                value = "`" + value + "`";
                break;
            
            case ICON:
                bot.setWelcomeIcon(id, value);
                value = "`" + value + "`";
                break;
            
            case MESSAGE:
                bot.setWelcomeMsg(id, value);
                value = getMsg(value);
        }
        
        tc.sendMessage(
                bot.getEmbedUtil().getEmbed(member)
                        .setColor(0x00FF00)
                        .setDescription(
                                bot.getMsg(id, "purr.guild.welcome.set")
                                        .replace("{type}", firstUpperCase(type.name()))
                                        .replace("{value}", value)
                        )
                        .build()
        ).queue();
    }
    
    private void reset(TextChannel tc, Member member, Type type){
        String id = member.getGuild().getId();
        
        switch(type){
            case BACKGROUND:
                bot.setWelcomeBg(id, "color_white");
                break;
            
            case CHANNEL:
                bot.setWelcomeChannel(id, "none");
                break;
            
            case COLOR:
                bot.setWelcomeColor(id, "hex:000000");
                break;
            
            case ICON:
                bot.setWelcomeIcon(id, "purr");
                break;
            
            case MESSAGE:
                bot.setWelcomeMsg(id, "Welcome {mention}!");
        }
        
        tc.sendMessage(
                bot.getEmbedUtil().getEmbed(member)
                        .setColor(0x00FF00)
                        .setDescription(
                                bot.getMsg(id, "purr.guild.welcome.reset")
                                        .replace("{type}", firstUpperCase(type.name()))
                        )
                        .build()
        ).queue();
    }
    
    private String getWelcomeChannel(String id){
        if(bot.getWelcomeChannel(id).equals("none"))
            return bot.getMsg(id, "purr.guild.welcome.embed.no_channel");
        
        Guild guild = bot.getShardManager().getGuildById(id);
        if(guild == null)
            return bot.getMsg(id, "purr.guild.welcome.embed.no_channel");
        
        return String.format("<#%s>", bot.getWelcomeChannel(id));
    }
    
    private String firstUpperCase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    private enum Type{
        BACKGROUND,
        CHANNEL,
        COLOR,
        ICON,
        MESSAGE
    }
}
