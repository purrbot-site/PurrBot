package com.andre601.purrbot.util;

/*
 * ---------------------------------------------------------------------
 * Code from DiscordSRV (https://github.com/Scarsz/DiscordSRV)
 *
 * Original Copyright (c) Scasrsz (Scarsz lol) 2018 (https://scarsz.me)
 * ---------------------------------------------------------------------
 */

import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.google.common.io.CharStreams;
import com.google.gson.JsonObject;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang.StringUtils;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DebugUtil {

    public static String run(User requester, TextChannel tc){
        Guild guild = tc.getGuild();

        Map<String, String> files = new LinkedHashMap<>();
        try{
            files.put("Thank-You.yaml", String.join("\n", new String[]{
                    "#",
                    "# Thanks to Scarsz lol (http://scarsz.me/) for providing this site!",
                    "#",
                    "# GitHub: https://github.com/Scarsz",
                    "# SpigotMC: https://www.spigotmc.org/members/scarsz.149937/",
                    "# Discord: https://scarsz.me/discord",
                    "#"
            }));
            files.put("General-Info.yaml", String.join("\n", new String[]{
                    "#",
                    "# Info about who requested the debug.",
                    "#",
                    "",
                    "Requester: " + MessageUtil.getTag(requester) + " (" + requester.getId() + ")",
                    "",
                    "#",
                    "# Info about the bot-Settings (Welcome-settings, prefix, roles of the bot)",
                    "#",
                    "",
                    getWelcomeInfo(guild),
                    "",
                    "Guild-Prefix: " + getGuildPrefix(guild),
                    "Roles: " + guild.getSelfMember().getRoles().size(),
                    getOwnRoles(guild)
            }));
            files.put("Guild-Info.yaml", String.join("\n", new String[]{
                    "#",
                    "# Basic Guild-Info",
                    "#",
                    "",
                    "Guildname: "  + guild.getName(),
                    "Guild-ID: " + guild.getId(),
                    "Owner: " + MessageUtil.getTag(guild.getOwner().getUser()) + " (" +
                            guild.getOwner().getUser().getId() + ")",
                    "",
                    "Users:",
                    "  Total: " + guild.getMembers().toArray().length,
                    "  Humans: " + guild.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                    "  Bots: " + guild.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length,
                    "",
                    "TextChannels: " + guild.getTextChannels().size(),
                    getChannels(guild),
                    "",
                    "Roles: " + guild.getRoles().size(),
                    getRoles(guild)
            }));
            files.put("TextChannel-Permissions.yaml", String.join("\n", new String[]{
                    "#",
                    "# All Channels and their permission for the bot",
                    "#",
                    "",
                    getChannelPerms(guild)
            }));
        }catch(Exception ex){
            ex.printStackTrace();
            tc.sendMessage(MessageFormat.format(
                    "There was an issue with creating the files {0}.",
                    requester.getAsMention()
            )).queue();
        }

        return makeReport(files, requester);
    }

    private static TextChannel getChannel(String id, Guild guild){
        TextChannel channel;
        try{
            channel = guild.getTextChannelById(id);
        }catch (Exception ex){
            channel = null;
        }
        return channel;
    }

    private static String getChannels(Guild guild){
        StringBuilder sb = new StringBuilder();
        for(TextChannel tc : guild.getTextChannels()){
            sb.append("  ").append(tc.getName()).append(" (").append(tc.getId()).append(")\n");
        }
        return sb.toString();
    }

    private static String getRoles(Guild guild){
        StringBuilder sb = new StringBuilder();
        for(Role role : guild.getRoles()){
            if(role.isPublicRole()) continue;

            sb.append("  ").append(role.getName()).append(" (").append(role.getId()).append(")\n");
        }
        return sb.toString();
    }

    private static String getOwnRoles(Guild guild){
        StringBuilder sb = new StringBuilder();
        for(Role role : guild.getSelfMember().getRoles()){
            sb.append("  ").append(role.getName()).append(" (").append(role.getId()).append(")\n");
        }
        return sb.toString();
    }

    private static String getChannelPerms(Guild guild){
        List<String> output = new LinkedList<>();
        guild.getTextChannels().forEach(textChannel -> {
            if(textChannel != null){
                List<String> channels = new LinkedList<>();
                channels.add("Read:                " + PermUtil.check(textChannel, Permission.MESSAGE_READ));
                channels.add("Read History:        " + PermUtil.check(textChannel, Permission.MESSAGE_READ));
                channels.add("Write:               " + PermUtil.check(textChannel, Permission.MESSAGE_WRITE));
                channels.add("Embed Links:         " + PermUtil.check(textChannel, Permission.MESSAGE_EMBED_LINKS));
                channels.add("Manage messages:     " + PermUtil.check(textChannel, Permission.MESSAGE_MANAGE));
                channels.add("Add Reaction:        " + PermUtil.check(textChannel, Permission.MESSAGE_ADD_REACTION));
                channels.add("Attach files:        " + PermUtil.check(textChannel, Permission.MESSAGE_ATTACH_FILES));
                channels.add("Use external emojis: " + PermUtil.check(textChannel, Permission.MESSAGE_EXT_EMOJI));
                output.add(MessageFormat.format(
                        "{0} ({1})\n" +
                        "  {2}",
                        textChannel.getName(),
                        textChannel.getId(),
                        String.join("\n  ", channels)
                ));
            }
        });
        return String.join("\n", output);
    }

    private static String getWelcomeInfo(Guild guild){
        String channelId = DBUtil.getWelcomeChannel(guild);
        String colorInfo = DBUtil.getColor(guild);
        String colorType = colorInfo.split(":")[0];
        String colorValue = colorInfo.split(":")[1];
        return "Welcome-channel: " + getChannelNameAndId(channelId, guild) + "\n" +
                "  Color-Type:  " + colorType + "\n" +
                "  Color-Value: " + colorValue + "\n" +
                "  Image:       " + DBUtil.getImage(guild);
    }

    private static String getGuildPrefix(Guild guild){
        return DBUtil.getPrefix(guild);
    }

    private static String getChannelNameAndId(String id, Guild guild){
        TextChannel channel = getChannel(id, guild);

        if(channel != null){
            return MessageFormat.format(
                    "{0} ({1})",
                    channel.getName(),
                    channel.getId()
            );
        }else{
            return "No channel set";
        }
    }

    private static String makeReport(Map<String, String> toUploadingFiles, User requester){
        if(toUploadingFiles.size() == 0)
            return MessageFormat.format("{0} The files are 0 for some reason...", requester.getAsMention());

        Map<String, String> files = new LinkedHashMap<>();
        toUploadingFiles.forEach((fileName, fileContent) -> files.put((files.size() + 1) + "-" + fileName,
                StringUtils.isNotBlank(fileContent) ? fileContent : "blank"));

        try{
            String url = uploadDebug(files);
            return "New Debug created! " + url;
        }catch (Exception ex){
            return MessageFormat.format(
                    "There was an issue with making the debug!\n" +
                    "Reason: {0}",
                    ex.getMessage());
        }
    }

    private static String uploadDebug(Map<String, String> files){
        HttpURLConnection connection = null;

        try{
            connection = (HttpURLConnection) new URL("https://debug.scarsz.me/post").openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Purr-Bot/" + IDs.VERSION);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStream out = connection.getOutputStream();
            JsonObject payload = new JsonObject();
            payload.addProperty("description", "Purr-Bot Debug");

            JsonObject filesJson = new JsonObject();
            files.forEach((fileName, fileContent) -> {
                JsonObject file = new JsonObject();
                file.addProperty("content", fileContent);
                filesJson.add(fileName, file);
            });
            payload.add("files", filesJson);

            out.write(PurrBot.getGson().toJson(payload).getBytes(Charset.forName("UTF-8")));
            out.close();

            String rawOutput = CharStreams.toString(new InputStreamReader(connection.getInputStream()));
            connection.getInputStream().close();
            JsonObject output = PurrBot.getGson().fromJson(rawOutput, JsonObject.class);

            if(!output.has("url")) throw new RuntimeException("URL was not recieved. Debug failed!");
            return output.get("url").getAsString();
        }catch (Exception ex){
            if(connection != null) connection.disconnect();
            throw new RuntimeException(ex);
        }
    }
}
