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
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;

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
                    
                    if(event.getChannel().getId().equals(IDs.SUGGESTIONS))
                        return;
                    
                    if(event.getChannel().isNews())
                        return;
                    
                    Pattern prefixPattern = Pattern.compile(
                            Pattern.quote(bot.getPrefix(guild.getId())) + "(?<command>[^\\s].*)", 
                            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
                    );
                    Pattern mentionPattern = Pattern.compile("<@!?(\\d+)>");

                    String raw = msg.getContentRaw();
    
                    Matcher commandMatcher = prefixPattern.matcher(raw);
                    Matcher mentionMatcher = mentionPattern.matcher(raw);
                    
                    if(!commandMatcher.matches() && !mentionMatcher.matches())
                        return;
                    
                    TextChannel tc = event.getChannel();
                    Member self = guild.getSelfMember();

                    Member member = msg.getMember();
                    if(member == null)
                        return;
                    
                    if(mentionMatcher.matches()){
                        if(!self.hasPermission(tc, Permission.MESSAGE_WRITE))
                            return;
                        
                        if(!mentionMatcher.group(1).equalsIgnoreCase(self.getId()))
                            return;
                        
                        tc.sendMessage(
                                bot.getMsg(guild.getId(), "misc.info", user.getAsMention())
                        ).queue();
                        return;
                    }
                    
                    raw = commandMatcher.group("command");

                    String[] args = Arrays.copyOf(raw.split("\\s+", 2), 2);

                    if(args[0] == null)
                        return;

                    Command command = (Command)HANDLER.findCommand(args[0].toLowerCase());
    
                    if(command == null)
                        return;
    
                    if(!self.hasPermission(tc, Permission.MESSAGE_WRITE)){
                        if(self.hasPermission(tc, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI))
                            msg.addReaction(Emotes.CANCEL.getNameAndId()).queue();
                        
                        return;
                    }

                    if(command.getAttribute("category").equals("owner") && bot.getCheckUtil().notDeveloper(member))
                        return;
                    
                    if(bot.getCheckUtil().hasAdmin(member, tc))
                        return;
                    
                    if(bot.getCheckUtil().lacksPermission(tc, member, true, tc, Permission.MESSAGE_EMBED_LINKS))
                        return;
                    
                    if(bot.getCheckUtil().lacksPermission(tc, member, true, tc, Permission.MESSAGE_HISTORY))
                        return;
                    
                    if(bot.getCheckUtil().lacksPermission(tc, member, true, tc, Permission.MESSAGE_ADD_REACTION))
                        return;
                    
                    if(bot.getCheckUtil().lacksPermission(tc, member, true, tc, Permission.MESSAGE_EXT_EMOJI))
                        return;
                    
                    if(command.getAttribute("category").equals("nsfw") && !tc.isNSFW()){
                        bot.getEmbedUtil().sendError(tc, member, "errors.nsfw_random", true);
                        return;
                    }
                    
                    if(command.hasAttribute("manage_server")){
                        if(bot.getCheckUtil().lacksPermission(tc, member, Permission.MANAGE_SERVER))
                            return;
                    }

                    try{
                        HANDLER.execute(command, msg, args[1] == null ? "" : args[1], args[0]);
                    }catch(Exception ex){
                        logger.error("Couldn't perform command {}!", args[0], ex);
                        bot.getEmbedUtil().sendError(tc, member, "errors.unknown", ex.getMessage(), false);
                    }
                }
        );
    }
}
