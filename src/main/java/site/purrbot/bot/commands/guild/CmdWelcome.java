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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Links;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
                        "{p}welcome msg reset"
                )
        }
)
public class CmdWelcome implements Command{

    private PurrBot bot;

    public CmdWelcome(PurrBot bot){
        this.bot = bot;
    }

    private MessageEmbed welcomeSettings(User author, Guild guild, boolean hasImage){
        TextChannel tc = getWelcomeChannel(guild.getId());

        String[] color = bot.getWelcomeColor(guild.getId()).split(":");

        if(color.length < 2) {
            bot.setWelcomeColor(guild.getId(), "hex:ffffff");

            color = new String[2];

            color[0] = "hex";
            color[1] = "ffffff";
        }

        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(author, guild)
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
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.color_title"),
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.color_value")
                                .replace("{type}", color[0].toLowerCase())
                                .replace("{value}", color[1].toLowerCase()), 
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.image_title"),
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.image_value")
                                .replace("{background}", bot.getWelcomeBg(guild.getId()))
                                .replace("{icon}", bot.getWelcomeIcon(guild.getId())),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.message_title"),
                        bot.getMsg(guild.getId(), "purr.guild.welcome.embed.message_value")
                                .replace("{message}", bot.getWelcomeMsg(guild.getId())), 
                        false
                );

        if(hasImage)
            embed.addField(
                    bot.getMsg(guild.getId(), "purr.guild.welcome.embed.preview"), 
                    EmbedBuilder.ZERO_WIDTH_SPACE, 
                    false
            )
            .setImage("attachment://welcome.jpg");

        return embed.build();
    }

    private void update(Message msg, Type type, String value){
        String id = msg.getGuild().getId();

        switch(type){
            case BACKGROUND:
                bot.setWelcomeBg(id, value);
                break;

            case CHANNEL:
                bot.setWelcomeChannel(id, value);
                break;

            case COLOR:
                bot.setWelcomeColor(id, value);
                break;

            case ICON:
                bot.setWelcomeIcon(id, value);
                break;

            case MESSAGE:
                bot.setWelcomeMsg(id, value);
        }

        msg.getTextChannel().sendMessage(
                bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                        .setColor(0x00FF00)
                        .setDescription(String.format(
                                "%s updated to %s",
                                type.toString().toLowerCase(),
                                type == Type.CHANNEL ? Objects.requireNonNull(msg.getGuild().getTextChannelById(value))
                                        .getAsMention() : value
                        ))
                        .build()
        ).queue();
    }

    private void reset(Message msg, Type type){
        String id = msg.getGuild().getId();

        switch(type){
            case BACKGROUND:
                bot.setWelcomeBg(id, "none");
                break;

            case CHANNEL:
                bot.setWelcomeChannel(id, "none");
                break;

            case COLOR:
                bot.setWelcomeColor(id, "none");
                break;

            case ICON:
                bot.setWelcomeIcon(id, "purr");
                break;

            case MESSAGE:
                bot.setWelcomeMsg(id, "none");
        }

        msg.getTextChannel().sendMessage(
                bot.getEmbedUtil().getEmbed(msg.getAuthor(), msg.getGuild())
                        .setColor(0x00FF00)
                        .setDescription(String.format(
                                "%s was resetted",
                                type.toString().toLowerCase()
                        ))
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

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.isEmpty() ? new String[0] : s.split("\\s+");

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length < 1){
            InputStream is;
            try{
                is = bot.getImageUtil().getWelcomeImg(
                        msg.getAuthor(),
                        guild.getMembers().size(),
                        bot.getWelcomeIcon(guild.getId()),
                        bot.getWelcomeBg(guild.getId()),
                        bot.getWelcomeColor(guild.getId())
                );
            }catch(IOException ex){
                is = null;
            }

            if(is == null){
                tc.sendMessage(welcomeSettings(msg.getAuthor(), guild, false)).queue();
                return;
            }

            tc.sendMessage(welcomeSettings(msg.getAuthor(), guild, true))
                    .addFile(is, "welcome.jpeg")
                    .queue();
            return;
        }

        switch (args[0].toLowerCase()){
            case "bg":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome bg <set <bg>|reset>`\n" +
                            "\n" +
                            "A list of available backgrounds can be found on the [wiki](%s)",
                            bot.getPrefix(guild.getId()),
                            Links.WIKI.getUrl()
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.BACKGROUND);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide a background name!");
                        return;
                    }
                    if(!bot.getWelcomeBg().contains(args[2].toLowerCase()) && !args[2].equalsIgnoreCase("random")){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                                "Invalid background name `%s`!",
                                args[2].toLowerCase()
                        ));
                        return;
                    }
                    update(msg, Type.BACKGROUND, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome bg <set <bg>|reset>`\n" +
                            "\n" +
                            "A list of available backgrounds can be found on the [wiki](%s)",
                            bot.getPrefix(guild.getId()),
                            Links.WIKI.getUrl()
                    ));
                }
                break;

            case "channel":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome channel <set <#channel>|reset>`",
                            bot.getPrefix(guild.getId())
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.CHANNEL);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(msg.getMentionedChannels().isEmpty()){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention a valid textchannel!");
                        return;
                    }
                    if(msg.getMentionedChannels().get(0) == null){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "The provided channel was invalid!");
                        return;
                    }
                    update(msg, Type.CHANNEL, msg.getMentionedChannels().get(0).getId());
                }else{
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome channel <set <#channel>|reset>`",
                            bot.getPrefix(guild.getId())
                    ));
                }
                break;

            case "color":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
                            bot.getPrefix(guild.getId())
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.COLOR);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide a color-type and value!");
                        return;
                    }

                    Color color = bot.getMessageUtil().getColor(args[2].toLowerCase());

                    if(color == null){
                        bot.getEmbedUtil().sendError(
                                tc,
                                msg.getAuthor(),
                                "Please provide a valid color type and value!");
                        return;
                    }

                    update(msg, Type.COLOR, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
                            bot.getPrefix(guild.getId())
                    ));
                }
                break;

            case "icon":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome icon <set <icon>|reset>`\n" +
                            "\n" +
                            "A list of available icons can be found on the [wiki](%s)",
                            bot.getPrefix(guild.getId()),
                            Links.WIKI.getUrl()
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.ICON);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide a icon name!");
                        return;
                    }
                    if(!bot.getWelcomeIcon().contains(args[2].toLowerCase()) && !args[2].equalsIgnoreCase("random")){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                                "Invalid icon name `%s`!",
                                args[2].toLowerCase()
                        ));
                        return;
                    }
                    update(msg, Type.ICON, args[2].toLowerCase());
                }else{
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome icon <set <icon>|reset>`\n" +
                            "\n" +
                            "A list of available backgrounds can be found on the [wiki](%s)",
                            bot.getPrefix(guild.getId()),
                            Links.WIKI.getUrl()
                    ));
                }
                break;

            case "msg":
                if(args.length < 2){
                    bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "The current message is:\n" +
                            "```\n" +
                            "%s\n" +
                            "```\n" +
                            "Use `%swelcome msg set <message>` to change it.\n" +
                            "\n" +
                            "**Placeholders**:\n" +
                            "`{mention}` - Mention of the joined user\n" +
                            "`{name}` - Name of the joined user\n" +
                            "`{guild}` - Name of the guild\n" +
                            "`{count}` - Member count of the guild",
                            bot.getWelcomeMsg(guild.getId()),
                            bot.getPrefix(guild.getId())
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.MESSAGE);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide a message!");
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i = 2; i < args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    update(msg, Type.MESSAGE, sb.toString());
                }
                break;

            default:
                InputStream is;
                try{
                    is = bot.getImageUtil().getWelcomeImg(
                            msg.getAuthor(),
                            guild.getMembers().size(),
                            bot.getWelcomeIcon(guild.getId()),
                            bot.getWelcomeBg(guild.getId()),
                            bot.getWelcomeColor(guild.getId())
                    );
                }catch(IOException ex){
                    is = null;
                }

                if(is == null){
                    tc.sendMessage(welcomeSettings(msg.getAuthor(), guild, false)).queue();
                    return;
                }

                tc.sendMessage(welcomeSettings(msg.getAuthor(), guild, true))
                        .addFile(is, "welcome.jpg")
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
