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

package site.purrbot.bot.util.message;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.util.HttpUtil;

import java.awt.*;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dv8tion.jda.api.exceptions.ErrorResponseException.ignore;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

public class MessageUtil {

    private final PurrBot bot;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd. MMM yyyy HH:mm:ss");
    
    private final Pattern placeholder = Pattern.compile("(\\{(.+?)})", Pattern.CASE_INSENSITIVE);
    private final Pattern rolePattern = Pattern.compile("(\\{r_(name|mention):(\\d+)})", Pattern.CASE_INSENSITIVE);
    private final Pattern channelPattern = Pattern.compile("(\\{c_(name|mention):(\\d+)})", Pattern.CASE_INSENSITIVE);
    
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
    
    private final Cache<String, String> queue = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    public MessageUtil(PurrBot bot){
        this.bot = bot;
    }

    public String getRandomShutdownImg(){
        return bot.getShutdownImg().isEmpty() ? "" : bot.getShutdownImg().get(
                bot.getRandom().nextInt(bot.getShutdownImg().size())
        );
    }

    public String getRandomShutdownMsg(){
        return bot.getShutdownMsg().isEmpty() ? "" : bot.getShutdownMsg().get(
                bot.getRandom().nextInt(bot.getShutdownMsg().size())
        );
    }

    public String getRandomStartupMsg(){
        return bot.getStartupMsg().isEmpty() ? "Starting bot..." : bot.getStartupMsg().get(
                bot.getRandom().nextInt(bot.getStartupMsg().size())
        );
    }

    public String formatTime(LocalDateTime time){
        LocalDateTime utcTime = LocalDateTime.from(time.atOffset(ZoneOffset.UTC));
        return utcTime.format(timeFormat) + " UTC";
    }

    public Color getColor(String input){
        input = input.toLowerCase();
        if(!input.equals("random") && !(input.startsWith("hex:") || input.startsWith("rgb:")))
            return null;
        
        Color color = null;
        
        if(input.equals("random")){
            int r = bot.getRandom().nextInt(256);
            int g = bot.getRandom().nextInt(256);
            int b = bot.getRandom().nextInt(256);
    
            return new Color(r, g, b);
        }
        
        String type = input.split(":")[0];
        
        switch(type){
            case "hex":
                input = input.replace("hex:", "");
                if(input.isEmpty())
                    return null;
                
                color = Color.decode(input.startsWith("#") ? input : "#" + input);
                break;
            
            case "rgb":
                input = input.replace("rgb:", "");
                if(input.isEmpty())
                    return null;
                
                String[] rgb = Arrays.copyOf(input.split(","), 3);
                
                try{
                    color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                }catch(Exception ignored){
                    return null;
                }
        }
        
        return color;
    }

    public void sendWelcomeMsg(TextChannel tc, String message, Member member, InputStream file){
        Guild guild = member.getGuild();
    
        message = formatPlaceholders(message, member);
        
        if(file == null || !guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
            tc.sendMessage(message).queue();
            return;
        }
        
        tc.sendMessage(message)
          .addFile(file, String.format(
                  "welcome_%s.jpg",
                  member.getId()
          ))
          .queue();
    }
    
    public String formatPlaceholders(String msg, Member member){
        Guild guild = member.getGuild();
        
        Matcher roleMatcher = rolePattern.matcher(msg);
        if(roleMatcher.find()){
            StringBuffer buffer = new StringBuffer();
            do{
                Role role = guild.getRoleById(roleMatcher.group(3));
                if(role == null)
                    continue;
                
                switch(roleMatcher.group(2).toLowerCase()){
                    case "name":
                        roleMatcher.appendReplacement(buffer, role.getName());
                        break;
                    
                    case "mention":
                        roleMatcher.appendReplacement(buffer, role.getAsMention());
                        break;
                }
            }while(roleMatcher.find());
            
            roleMatcher.appendTail(buffer);
            msg = buffer.toString();
        }
        
        Matcher channelMatcher = channelPattern.matcher(msg);
        if(channelMatcher.find()){
            StringBuffer buffer = new StringBuffer();
            do{
                GuildChannel channel = guild.getGuildChannelById(channelMatcher.group(3));
                if(channel == null)
                    continue;
                
                switch(channelMatcher.group(2).toLowerCase()){
                    case "name":
                        channelMatcher.appendReplacement(buffer, channel.getName());
                        break;
                    
                    case "mention":
                        if(!channel.getType().equals(ChannelType.TEXT))
                            continue;
                        
                        TextChannel tc = guild.getTextChannelById(channelMatcher.group(3));
                        if(tc == null)
                            continue;
                        
                        channelMatcher.appendReplacement(buffer, tc.getAsMention());
                        break;
                }
            }while(channelMatcher.find());
            
            channelMatcher.appendTail(buffer);
            msg = buffer.toString();
        }
        
        Matcher matcher = placeholder.matcher(msg);
        if(matcher.find()){
            StringBuffer buffer = new StringBuffer();
            do{
                switch(matcher.group(2).toLowerCase()){
                    case "mention":
                        matcher.appendReplacement(buffer, member.getAsMention());
                        break;
                    
                    case "name":
                    case "username":
                        matcher.appendReplacement(buffer, member.getEffectiveName());
                        break;
                    
                    case "guild":
                    case "server":
                        matcher.appendReplacement(buffer, guild.getName());
                        break;
                    
                    case "count":
                    case "members":
                        matcher.appendReplacement(buffer, String.valueOf(guild.getMemberCount()));
                        break;
                    
                    case "count_formatted":
                    case "members_formatted":
                        matcher.appendReplacement(buffer, formatNumber(guild.getMemberCount()));
                        break;
                    
                    case "tag":
                        matcher.appendReplacement(buffer, member.getUser().getAsTag());
                }
            }while(matcher.find());
            
            matcher.appendTail(buffer);
            msg = buffer.toString();
        }
        
        return msg;
    }

    public String getBotGame(long guilds){
        String game = bot.isBeta() ? "My sister on %s guilds." : "https://purrbot.site | %s Guilds";
        
        
        return String.format(game, formatNumber(guilds));
    }
    
    public String replaceLast(String input, String target, String replacement){
        if(!input.contains(target))
            return input;
        
        StringBuilder builder = new StringBuilder(input);
        builder.replace(input.lastIndexOf(target), input.lastIndexOf(target) + 1, replacement);
        
        return builder.toString();
    }
    
    public String formatNumber(long number){
        return decimalFormat.format(number);
    }
    
    public String getQueueString(Member member){
        return member.getId() + ":" + member.getGuild().getId();
    }
    
    public void handleReactionEvent(TextChannel tc, Member author, Member target, HttpUtil.ImageAPI api){
        Guild guild = tc.getGuild();
        String path = "purr." + api.getCategory() + "." + api.getEndpoint() + ".";
        
        if(queue.getIfPresent(queueString(api.getEndpoint(), guild.getId(), author.getId())) != null){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), path + "request.open", author.getAsMention())
            ).queue();
            return;
        }
        
        tc.sendMessage(
                bot.getMsg(
                        guild.getId(),
                        path + "request.message",
                        author.getEffectiveName(),
                        target.getAsMention()
                )
        ).queue(message -> message.addReaction(Emotes.ACCEPT.getNameAndId())
                .flatMap(v -> message.addReaction(Emotes.CANCEL.getNameAndId()))
                .queue(
                        v -> handle(message, path, api, guild, author, target),
                        e -> bot.getEmbedUtil().sendError(tc, author, "errors.request_error")
                )
        );
    }
    
    private void handle(Message msg, String path, HttpUtil.ImageAPI api, Guild guild, Member author, Member target){
        queue.put(queueString(
                api.getEndpoint(),
                guild.getId(),
                author.getId()
        ), target.getId());
    
        EventWaiter eventWaiter = bot.getWaiter();
        eventWaiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                event -> {
                    MessageReaction.ReactionEmote emote = event.getReactionEmote();
                    if(!emote.isEmote())
                        return false;
                
                    if(!emote.getId().equals(Emotes.ACCEPT.getId()) && !emote.getId().equals(Emotes.CANCEL.getId()))
                        return false;
                
                    if(event.getUser().isBot())
                        return false;
                
                    if(!event.getMember().equals(target))
                        return false;
                
                    return event.getMessageId().equals(msg.getId());
                },
                event -> {
                    TextChannel channel = event.getChannel();
                    queue.invalidate(queueString(api.getEndpoint(), guild.getId(), author.getId()));
                
                    if(event.getReactionEmote().getId().equals(Emotes.CANCEL.getId())){
                        msg.delete().queue();
                        channel.sendMessage(MarkdownSanitizer.escape(
                                bot.getMsg(
                                        guild.getId(),
                                        path + "request.denied",
                                        author.getAsMention(),
                                        target.getEffectiveName()
                                )
                        )).queue();
                    }else{
                        bot.getHttpUtil().handleRequest(api, author, msg, target.getEffectiveName(), true);
                    }
                },
                1, TimeUnit.MINUTES,
                () -> {
                    TextChannel channel = msg.getTextChannel();
                    msg.delete().queue(null, ignore(UNKNOWN_MESSAGE));
                    queue.invalidate(queueString(api.getEndpoint(), guild.getId(), author.getId()));
                
                    channel.sendMessage(MarkdownSanitizer.escape(
                            bot.getMsg(
                                    guild.getId(),
                                    "request.timed_out",
                                    author.getAsMention(),
                                    target.getEffectiveName()
                            )
                    )).queue();
                });
    }
    
    public void editMessage(Message msg, String path, Member author, String target, String link){
        Guild guild = msg.getGuild();
        
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(
                                guild.getId(),
                                path + "message",
                                author.getEffectiveName(),
                                target
                        )
                ));
        
        if(link != null)
            embed.setImage(link);
        
        if(guild.getSelfMember().hasPermission(msg.getTextChannel(), Permission.MESSAGE_MANAGE))
            msg.clearReactions().queue();
        
        msg.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE)
                .embed(embed.build())
                .queue(message -> message.getTextChannel().sendMessage(author.getAsMention())
                                .embed(acceptedEmbed(guild.getId(), target, message.getJumpUrl()))
                                .queue(
                                        del -> del.delete().queueAfter(10, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE))
                                ),
                        e -> msg.getTextChannel().sendMessage(embed.build()).queue(message ->
                                message.getTextChannel().sendMessage(author.getAsMention())
                                       .embed(acceptedEmbed(guild.getId(), target, message.getJumpUrl()))
                                       .queue(
                                               del -> del.delete().queueAfter(10, TimeUnit.SECONDS, null, ignore(UNKNOWN_MESSAGE))
                                       )
                        )
                );
    }
    
    private MessageEmbed acceptedEmbed(String id, String name, String link){
        return bot.getEmbedUtil().getEmbed()
                .setDescription(MarkdownSanitizer.escape(
                        bot.getMsg(
                                id,
                                "request.accepted"
                        )
                        .replace("{target}", name)
                        .replace("{link}", link)
                ))
                .setTimestamp(null)
                .build();
    }
    
    private String queueString(String api, String guildId, String authorId){
        return api + ":" + guildId + ":" + authorId;
    }
}
