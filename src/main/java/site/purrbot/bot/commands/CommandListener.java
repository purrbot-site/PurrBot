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

package site.purrbot.bot.commands;

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter{
    
    private final Logger logger = (Logger)LoggerFactory.getLogger(CommandListener.class);

    private final ThreadGroup CMD_THREAD = new ThreadGroup("CommandThread");
    private final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(CMD_THREAD, r, "CommandPool")
    );

    private final PurrBot bot;
    private final CommandHandler<Message> HANDLER;

    public CommandListener(PurrBot bot, CommandHandler<Message> handler){
        this.bot = bot;
        this.HANDLER = handler;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){

        CMD_EXECUTOR.execute(
                () -> {
                    Message msg = event.getMessage();
                    Guild guild = event.getGuild();
                    User user = event.getAuthor();

                    if(user.isBot())
                        return;

                    Pattern prefixPattern = Pattern.compile(
                            Pattern.quote(bot.getPrefix(guild.getId())) + "(?<command>[^\\s].*)", 
                            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
                    );

                    String raw = msg.getContentRaw();

                    String memberMention = "<@!" + guild.getJDA().getSelfUser().getId() + ">";
                    String userMention = "<@" + guild.getJDA().getSelfUser().getId() + ">";
    
                    Matcher matcher = prefixPattern.matcher(raw);
                    
                    if(!matcher.matches() && !(raw.equals(userMention) || raw.equals(memberMention)))
                        return;
                    
                    TextChannel tc = event.getChannel();
                    Member self = guild.getSelfMember();

                    Member member = msg.getMember();
                    if(member == null)
                        return;
                    
                    if(raw.equalsIgnoreCase(userMention) || raw.equalsIgnoreCase(memberMention)){
                        if(!self.hasPermission(tc, Permission.MESSAGE_WRITE))
                            return;
                        
                        tc.sendMessage(
                                bot.getMsg(guild.getId(), "misc.info", user.getAsMention())
                        ).queue();
                        return;
                    }
                    
                    raw = matcher.group("command");

                    String[] args = Arrays.copyOf(raw.split("\\s+", 2), 2);

                    if(args[0] == null)
                        return;

                    Command command = (Command)HANDLER.findCommand(args[0].toLowerCase());
    
                    if(command == null)
                        return;
    
                    if(!self.hasPermission(tc, Permission.MESSAGE_WRITE)){
                        user.openPrivateChannel()
                                .flatMap(channel -> channel.sendMessage(bot.getEmbedUtil().getPermErrorEmbed(
                                        member,
                                        guild,
                                        tc,
                                        Permission.MESSAGE_WRITE,
                                        true,
                                        true
                                )))
                                .queue(
                                        null,
                                        error -> logger.warn(String.format(
                                                "I lack the permission to send messages in %s (%s)",
                                                guild.getName(),
                                                guild.getId()
                                        ))
                                );
                        return;
                    }

                    if(command.getAttribute("category").equals("owner") && !bot.getCheckUtil().isDeveloper(member))
                        return;
                    
                    if(self.hasPermission(Permission.ADMINISTRATOR)){
                        bot.getEmbedUtil().sendError(tc, member, "errors.administrator");
                        return;
                    }
                    
                    if(!bot.getCheckUtil().checkPermission(tc, member, self, Permission.MESSAGE_EMBED_LINKS))
                        return;
                    
                    if(!bot.getCheckUtil().checkPermission(tc, member, self, Permission.MESSAGE_HISTORY))
                        return;
                    
                    if(!bot.getCheckUtil().checkPermission(tc, member, self, Permission.MESSAGE_ADD_REACTION))
                        return;
                    
                    if(!bot.getCheckUtil().checkPermission(tc, member, self, Permission.MESSAGE_EXT_EMOJI))
                        return;
                    
                    if(command.getAttribute("category").equals("nsfw") && !tc.isNSFW()){
                        MessageEmbed notNsfw = bot.getEmbedUtil().getEmbed(member)
                                .setColor(0xFF0000)
                                .setDescription(
                                        bot.getRandomMsg(guild.getId(), "errors.nsfw_random", member.getEffectiveName())
                                )
                                .build();
                        
                        tc.sendMessage(notNsfw).queue();
                        return;
                    }
                    
                    if(command.hasAttribute("manage_server")){
                        if(!bot.getCheckUtil().checkPermission(tc, member, member, Permission.MESSAGE_MANAGE))
                            return;
                    }

                    try{
                        HANDLER.execute(command, msg, args[1] == null ? "" : args[1]);
                    }catch(Exception ex){
                        logger.error("Couldn't perform command!", ex);
                        bot.getEmbedUtil().sendError(tc, member, "errors.unknown", ex.getMessage());
                    }
                }
        );
    }
}
