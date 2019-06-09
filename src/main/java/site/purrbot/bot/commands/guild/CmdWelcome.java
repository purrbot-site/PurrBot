package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Links;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

@CommandDescription(
        name = "Welcome",
        description =
                "Greet people with a welcome message and Image!\n" +
                "You can customize stuff like the image, the message and textcolor in the image.",
        triggers = {"welcome"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value =
                        "{p}welcome\n" +
                        "{p}welcome channel <set <#channel>|reset>\n" +
                        "{p}welcome color <set <color>|reset>\n" +
                        "{p}welcome image <set <image>|reset>\n" +
                        "{p}welcome msg <set <message>|reset>\n" +
                        "{p}welcome test [image] [color] [msg]"
                )
        }
)
public class CmdWelcome implements Command{

    private PurrBot manager;

    public CmdWelcome(PurrBot manager){
        this.manager = manager;
    }

    private MessageEmbed welcomeSettings(User author, String id){
        TextChannel tc = getWelcomeChannel(id);

        String[] color = manager.getDbUtil().getWelcomeColor(id).split(":");

        if(color.length < 2) {
            manager.getDbUtil().setWelcomeColor(id, "hex:ffffff");

            color = new String[2];

            color[0] = "hex";
            color[1] = "ffffff";
        }

        return manager.getEmbedUtil().getEmbed(author)
                .setTitle("Current welcome settings")
                .setDescription(String.format(
                        "Here is a list of all current settings for this Discord!\n" +
                        "To change some settings use `%swelcome [subcommand]`",
                        manager.getDbUtil().getPrefix(id)
                ))
                .addField(
                        "Subcommands",
                        "`channel <set #channel|reset>` Change the welcome channel.\n" +
                        "`image <set image|reset>` Change the image.\n" +
                        "`color <set color|reset>` Change the font color. It has to start with rgb: or hex:\n" +
                        "`msg <set message|reset>` Change the welcome message.",
                        false)
                .addField("Channel", String.format(
                        "%s",
                        tc == null ? "`No channel set`" : tc.getAsMention()
                ), true)
                .addField("Color", String.format(
                        "Type: `%s`\n" +
                        "Value: `%s`",
                        color[0].toLowerCase(),
                        color[1].toLowerCase()
                ), true)
                .addField("Image", String.format(
                        "`%s`",
                        manager.getDbUtil().getWelcomeImg(id)
                ),true)
                .addField("Message", String.format(
                        "```\n" +
                        "%s\n" +
                        "```",
                        manager.getDbUtil().getWelcomeMsg(id)
                ), false).build();
    }

    private void update(Message msg, Type type, String value){
        String id = msg.getGuild().getId();

        switch(type){
            case CHANNEL:
                manager.getDbUtil().setWelcomeChannel(id, value);
                break;

            case IMAGE:
                manager.getDbUtil().setWelcomeImg(id, value);
                break;

            case COLOR:
                manager.getDbUtil().setWelcomeColor(id, value);
                break;

            case MESSAGE:
                manager.getDbUtil().setWelcomeMsg(id, value);
        }

        msg.getTextChannel().sendMessage(
                manager.getEmbedUtil().getEmbed(msg.getAuthor())
                        .setColor(0x00FF00)
                        .setDescription(String.format(
                                "%s updated to %s",
                                type.toString().toLowerCase(),
                                type == Type.CHANNEL ? msg.getGuild().getTextChannelById(value).getAsMention() : value
                        ))
                        .build()
        ).queue();
    }

    private void reset(Message msg, Type type){
        String id = msg.getGuild().getId();

        switch(type){
            case CHANNEL:
                manager.getDbUtil().setWelcomeChannel(id, "none");
                break;

            case IMAGE:
                manager.getDbUtil().setWelcomeImg(id, "none");
                break;

            case COLOR:
                manager.getDbUtil().setWelcomeColor(id, "none");
                break;

            case MESSAGE:
                manager.getDbUtil().setWelcomeMsg(id, "none");
        }

        msg.getTextChannel().sendMessage(
                manager.getEmbedUtil().getEmbed(msg.getAuthor())
                        .setColor(0x00FF00)
                        .setDescription(String.format(
                                "%s was resetted",
                                type.toString().toLowerCase()
                        ))
                        .build()
        ).queue();
    }

    private TextChannel getWelcomeChannel(String id){
        if(manager.getDbUtil().getWelcomeChannel(id).equals("none")) return null;

        return manager.getShardManager().getGuildById(id).getTextChannelById(manager.getDbUtil().getWelcomeChannel(id));
    }

    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(manager.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length == 0){
            tc.sendMessage(welcomeSettings(msg.getAuthor(), guild.getId())).queue();
            return;
        }

        switch (args[0].toLowerCase()){
            case "channel":
                if(args.length < 2){
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome channel <set <#channel>|reset>`",
                            manager.getDbUtil().getPrefix(guild.getId())
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.CHANNEL);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(msg.getMentionedChannels().isEmpty()){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please mention a valid textchannel!");
                        return;
                    }
                    if(msg.getMentionedChannels().get(0) == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "The provided channel was invalid!");
                        return;
                    }
                    update(msg, Type.CHANNEL, msg.getMentionedChannels().get(0).getId());
                }else{
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome channel <set <#channel>|reset>`",
                            manager.getDbUtil().getPrefix(guild.getId())
                    ));
                }
                break;

            case "image":
                if(args.length < 2){
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome image <set <image>|reset>`\n" +
                            "\n" +
                            "A list of available images can be found on the [wiki](%s)",
                            manager.getDbUtil().getPrefix(guild.getId()),
                            Links.WIKI.getUrl()
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.IMAGE);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide an image!");
                        return;
                    }
                    if(!manager.getgFile().getStringlist("random", "images").contains(args[0].toLowerCase())){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Invalid image!");
                        return;
                    }
                    update(msg, Type.IMAGE, args[2].toLowerCase());
                }else{
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome image <set <image>|reset>`\n" +
                            "\n" +
                            "A list of available images can be found on the [wiki](%s)",
                            manager.getDbUtil().getPrefix(guild.getId()),
                            Links.WIKI.getUrl()
                    ));
                }
                break;

            case "color":
                if(args.length < 2){
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "To few arguments!\n" +
                            "Usage: `%swelcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
                            manager.getDbUtil().getPrefix(guild.getId())
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.COLOR);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide a color-type and value!");
                        return;
                    }

                    Color color = manager.getMessageUtil().getColor(args[2].toLowerCase());

                    if(color == null){
                        manager.getEmbedUtil().sendError(
                                tc,
                                msg.getAuthor(),
                                "Please provide a valid color type and value!");
                        return;
                    }

                    update(msg, Type.COLOR, args[2].toLowerCase());
                }else{
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                            "Invalid argument!\n" +
                            "Usage: `%swelcome color <set <rgb:r,g,b|hex:#rrggbb>|reset>`",
                            manager.getDbUtil().getPrefix(guild.getId())
                    ));
                }
                break;

            case "msg":
                if(args.length < 2){
                    manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
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
                            manager.getDbUtil().getWelcomeMsg(guild.getId()),
                            manager.getDbUtil().getPrefix(guild.getId())
                    ));
                    return;
                }
                if(args[1].equalsIgnoreCase("reset")){
                    reset(msg, Type.MESSAGE);
                }else
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length < 3){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Please provide a message!");
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i = 2; i < args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    update(msg, Type.MESSAGE, sb.toString());
                }
                break;
            case "test":
                if(args.length == 1){
                    InputStream is;

                    try{
                        is = manager.getImageUtil().getWelcomeImg(
                                msg.getAuthor(),
                                guild.getMembers().size(),
                                manager.getDbUtil().getWelcomeImg(guild.getId()),
                                manager.getDbUtil().getWelcomeColor(guild.getId())
                        );
                    }catch(IOException ex){
                        is = null;
                    }

                    if(is == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();
                }else
                if(args.length == 2){
                    if(!manager.getWelcomeImg().contains(args[1].toLowerCase())){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                                "%s is not a valid image name!\n" +
                                "A list of available images can be found on the [wiki](%s)!",
                                args[1].toLowerCase(),
                                Links.WIKI.getUrl()
                        ));
                        return;
                    }
                    InputStream is;

                    try{
                        is = manager.getImageUtil().getWelcomeImg(
                                msg.getAuthor(),
                                guild.getMembers().size(),
                                args[1].toLowerCase(),
                                manager.getDbUtil().getWelcomeColor(guild.getId())
                        );
                    }catch(IOException ex){
                        is = null;
                    }

                    if(is == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();

                }else
                if(args.length == 3){
                    if(!manager.getWelcomeImg().contains(args[1].toLowerCase())){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                                "%s is not a valid image name!\n" +
                                "A list of available images can be found on the [wiki](%s)!",
                                args[1].toLowerCase(),
                                Links.WIKI.getUrl()
                        ));
                        return;
                    }
                    Color color = manager.getMessageUtil().getColor(args[2].toLowerCase());
                    if(color == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(),
                                "Invalid color type or value!\n" +
                                "Make sure it starts with `hex:` or `rgb:` and has the right values!\n" +
                                "For example: `hex:ffffff` or `rgb:255,255,255`"
                        );
                        return;
                    }
                    InputStream is;

                    try{
                        is = manager.getImageUtil().getWelcomeImg(
                                msg.getAuthor(),
                                guild.getMembers().size(),
                                args[1].toLowerCase(),
                                args[2].toLowerCase()
                        );
                    }catch(IOException ex){
                        is = null;
                    }

                    if(is == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();
                }else{
                    if(!manager.getWelcomeImg().contains(args[1].toLowerCase())){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                                "%s is not a valid image name!\n" +
                                "A list of available images can be found on the [wiki](%s)!",
                                args[1].toLowerCase(),
                                Links.WIKI.getUrl()
                        ));
                        return;
                    }
                    Color color = manager.getMessageUtil().getColor(args[2].toLowerCase());
                    if(color == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(),
                                "Invalid color type or value!\n" +
                                "Make sure it starts with `hex:` or `rgb:` and has the right values!\n" +
                                "For example: `hex:ffffff` or `rgb:255,255,255`"
                        );
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i =3; i < args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    InputStream is;

                    try{
                        is = manager.getImageUtil().getWelcomeImg(
                                msg.getAuthor(),
                                guild.getMembers().size(),
                                args[1].toLowerCase(),
                                args[2].toLowerCase()
                        );
                    }catch(IOException ex){
                        is = null;
                    }

                    if(is == null){
                        manager.getEmbedUtil().sendError(tc, msg.getAuthor(), "Couldn't generate image. Try again later.");
                        return;
                    }

                    tc.sendMessage(manager.getMessageUtil().formatPlaceholders(sb.toString(), msg.getMember()))
                            .addFile(is, String.format(
                                    "%s.png",
                                    System.currentTimeMillis()
                            )).queue();
                }
                break;

            default:
                tc.sendMessage(welcomeSettings(msg.getAuthor(), guild.getId())).queue();
        }
    }

    private enum Type{
        CHANNEL,
        IMAGE,
        COLOR,
        MESSAGE
    }
}
