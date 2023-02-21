/*
 *  Copyright 2018 - 2022 Andre601
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

package site.purrbot.bot.commands;

import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.util.CheckUtil;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandListener extends ListenerAdapter{
    
    private final Logger logger = LoggerFactory.getLogger(CommandListener.class);

    private final ThreadGroup CMD_THREAD = new ThreadGroup("CommandThread");
    private final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(CMD_THREAD, r, "CommandPool")
    );

    private final PurrBot bot;
    private final CommandHandler<Message> HANDLER;
    
    private String mention = null;

    public CommandListener(PurrBot bot, CommandHandler<Message> handler){
        this.bot = bot;
        this.HANDLER = handler;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){
        CMD_EXECUTOR.execute(() -> {
            // Ignore non-guild messages and non-text channels
            if(!event.isFromGuild() || event.getChannel().getType() != ChannelType.TEXT)
                return;
            
            // Ignore other bots (includes self)
            if(event.getAuthor().isBot())
                return;
    
            Member self = event.getGuild().getSelfMember();
            if(mention == null)
                mention = self.getAsMention();
            
            // Don't bother with channels Bot can't write in.
            if(!self.hasPermission(event.getChannel().asTextChannel(), Permission.MESSAGE_SEND))
                return;
    
            Message msg = event.getMessage();
            Member member = msg.getMember();
            if(member == null)
                return;
            
            // Ignore specific channels on the support server.
            if(event.getChannel().getId().equals(IDs.SUGGESTIONS))
                return;
            
            // Ignore news/Announcement channels.
            if(event.getChannel().getType() == ChannelType.NEWS)
                return;
            
            String raw = msg.getContentRaw();
            String guildId = event.getGuild().getId();
            
            // Check if the message is only the mention itself to send some info for the user.
            if(raw.equalsIgnoreCase(mention)){
                msg.reply(bot.getMsg(guildId, "misc.info", member.getEffectiveName())).queue();
                return;
            }
            
            String cmd;
            if(raw.startsWith(mention)){
                // Remove mention and any leading spaces.
                cmd = raw.substring(mention.length()).stripLeading();
            }else
            if(raw.toLowerCase(Locale.ROOT).startsWith(bot.getPrefix(guildId))){
                // Remove prefix
                cmd = raw.substring(bot.getPrefix(guildId).length());
            }else{
                // Text didn't start with mention nor prefix, so it's not a command to handle.
                return;
            }
            
            // The command starts with at least one whitespace which is not good.
            // Mention has leading spaces striped, so this only applies to text prefixes.
            if(cmd.isEmpty() || Character.isWhitespace(cmd.charAt(0)))
                return;
            
            // Give the new command String to handle further.
            handle(cmd, event.getChannel().asTextChannel(), msg, member);
        });
    }
    
    private void handle(String message, TextChannel tc, Message msg, Member member){
        // split at spaces with a max size of 2 (Command + arguments)
        String[] split = message.split("\\s+", 2);
        
        // Veeeeeery unlikely, but better safe than sorry.
        if(split.length == 0)
            return;
        
        Command cmd = (Command)HANDLER.findCommand(split[0].toLowerCase(Locale.ROOT));
        // No command was found -> No reply ("Unknown command" replies are bad)
        if(cmd == null)
            return;
        
        // Only bot-dev should run these commands.
        if(cmd.getAttribute("category").equals("owner") && !bot.getCheckUtil().isDeveloper(member))
            return;
        
        // Bot doesn't allow administrator for itself.
        if(bot.getCheckUtil().selfHasAdmin(member, tc))
            return;
        
        // Check required permissions for this command.
        if(CheckUtil.selfLacksPermissions(bot, tc, cmd.getPermissions()))
            return;
        
        // Don't allow age-restricted commands in normal channels.
        if(cmd.getAttribute("category").equals("nsfw") && !tc.isNSFW()){
            bot.getEmbedUtil().sendError(tc, member, "errors.nsfw.no_channel_random", true);
            return;
        }
        
        if(cmd.hasAttribute("manage_server") && CheckUtil.lacksPermission(bot, tc, member, Permission.MANAGE_SERVER))
            return;
        
        try{
            HANDLER.execute(cmd, msg, split.length == 1 ? "" : split[1], split[0]);
        }catch(Exception ex){
            logger.error("Unable to perform command {}!", split[0], ex);
            bot.getEmbedUtil().sendError(tc, member, "errors.unknown", ex.getMessage(), false);
        }
    }
}
