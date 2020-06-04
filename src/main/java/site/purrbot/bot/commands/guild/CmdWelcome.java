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
        description =
                "Greet people with a welcome message and Image!\n" +
                "You can customize stuff like the background, icon, message and text color in the image.",
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
                        "{p}welcome msg <message>\n" +
                        "{p}welcome msg reset" +
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

    private String getMsg(String msg){
        return String.format(
                "```md\n" +
                "%s\n" +
                "```",
                msg.length() > 1000 ? msg.substring(0, 990) + "..." : msg
        );
    }
    
    private MessageEmbed welcomeSettings(Member member, Guild guild, boolean hasImage){
        TextChannel tc = getWelcomeChannel(guild.getId());

        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(member)
                .setTitle("Current welcome settings")
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
                        tc == null ? bot.getMsg(guild.getId(), "purr.guild.welcome.embed.no_channel") : tc.getAsMention()
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

        if(hasImage)
            embed.addField(
                    bot.getMsg(guild.getId(), "purr.guild.welcome.embed.preview"), 
                    EmbedBuilder.ZERO_WIDTH_SPACE, 
                    false
            )
            .setImage("attachment://welcome_preview.jpg");

        return embed.build();
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

    private TextChannel getWelcomeChannel(String id){
        if(bot.getWelcomeChannel(id).equals("none")) 
            return null;
        
        Guild guild = bot.getShardManager().getGuildById(id);
        if(guild == null)
            return null;
        
        return guild.getTextChannelById(bot.getWelcomeChannel(id));
    }

    private String firstUpperCase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length < 1){
            InputStream is;
            try{
                is = bot.getImageUtil().getWelcomeImg(
                        msg.getMember(),
                        bot.getWelcomeIcon(guild.getId()),
                        bot.getWelcomeBg(guild.getId()),
                        bot.getWelcomeColor(guild.getId())
                );
            }catch(IOException ex){
                is = null;
            }

            if(is == null){
                tc.sendMessage(welcomeSettings(member, guild, false)).queue();
                return;
            }

            tc.sendMessage(welcomeSettings(member, guild, true))
                    .addFile(is, "welcome_preview.jpg")
                    .queue();
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
                        bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_bg");
                        return;
                    }
                    update(tc, member, Type.ICON, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, member, "purr.guild.welcome.invalid_args");
                }
                break;

            case "msg":
                if(args.length == 1){
                    MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                            .addField(
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.message_title"),
                                    getMsg(bot.getWelcomeMsg(guild.getId())),
                                    false
                            )
                            .addField(
                                    bot.getMsg(guild.getId(), "purr.guild.welcome.placeholders_title"),
                                    "`{count}`\n" +
                                    "`{guild}`\n" +
                                    "`{mention}`\n" +
                                    "`{name}`\n" +
                                    "`{r_mention:<id>}`\n" +
                                    "`{r_name:<id>}`",
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
                
                if(image == null || !guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
                    tc.sendMessage(bot.getMessageUtil().parsePlaceholders(
                            bot.getWelcomeMsg(guild.getId()),
                            member
                    )).queue();
                    return;
                }
                
                tc.sendMessage(bot.getMessageUtil().parsePlaceholders(
                        bot.getWelcomeMsg(guild.getId()),
                        member
                ))
                .addFile(image, "welcome_preview.jpg")
                .queue();
                break;

            default:
                InputStream is;
                try{
                    is = bot.getImageUtil().getWelcomeImg(
                            member,
                            bot.getWelcomeIcon(guild.getId()),
                            bot.getWelcomeBg(guild.getId()),
                            bot.getWelcomeColor(guild.getId())
                    );
                }catch(IOException ex){
                    is = null;
                }

                if(is == null || !guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
                    tc.sendMessage(welcomeSettings(member, guild, false)).queue();
                    return;
                }

                tc.sendMessage(welcomeSettings(member, guild, true))
                        .addFile(is, "welcome_preview.jpg")
                        .queue();
        }
    }

    private enum Type{
        BACKGROUND,
        CHANNEL,
        COLOR,
        ICON,
        MESSAGE
    }
}
