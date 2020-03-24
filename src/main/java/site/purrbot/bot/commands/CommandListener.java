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
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandListener extends ListenerAdapter{

    private Logger logger = (Logger)LoggerFactory.getLogger(CommandListener.class);

    private final ThreadGroup CMD_THREAD = new ThreadGroup("CommandThread");
    private final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(CMD_THREAD, r, "CommandPool")
    );

    private PurrBot bot;
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

                    String prefix = bot.getPrefix(guild.getId());

                    String raw = msg.getContentRaw();

                    String memberMention = "<@!" + guild.getJDA().getSelfUser().getId() + ">";
                    String userMention = "<@" + guild.getJDA().getSelfUser().getId() + ">";

                    if(!raw.toLowerCase().startsWith(prefix) && !raw.startsWith(userMention) && !raw.startsWith(memberMention)){

                        return;
                    }

                    TextChannel tc = event.getChannel();

                    if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_WRITE))
                        return;

                    Member member = msg.getMember();
                    if(member == null)
                        return;

                    if(raw.equalsIgnoreCase(userMention) || raw.equalsIgnoreCase(memberMention)){
                        tc.sendMessage(
                                bot.getMsg(guild.getId(), "misc.info", user.getAsMention())
                        ).queue();
                        return;
                    }

                    if(!raw.toLowerCase().startsWith(prefix))
                        return;

                    String[] args = split(raw, prefix.length());
                    String cmdString = args[0];

                    if(cmdString == null)
                        return;

                    Command command = (Command)HANDLER.findCommand(cmdString.toLowerCase());

                    if(command == null)
                        return;

                    if(command.getAttribute("category").equals("owner") && !user.getId().equals(IDs.ANDRE_601.getId()))
                        return;

                    if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_EMBED_LINKS)){
                        tc.sendMessage(
                                bot.getMsg(guild.getId(), "errors.missing_perms.self")
                                        .replace("{permission}", Permission.MESSAGE_EMBED_LINKS.getName())
                        ).queue();
                        return;
                    }
                    if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                        bot.getEmbedUtil().sendPermError(tc, user, Permission.MESSAGE_HISTORY, true);
                        return;
                    }
                    if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_ADD_REACTION)){
                        bot.getEmbedUtil().sendPermError(tc, user, Permission.MESSAGE_ADD_REACTION, true);
                        return;
                    }
                    if(command.getAttribute("category").equals("nsfw") && !tc.isNSFW()){
                        MessageEmbed notNsfw = bot.getEmbedUtil().getEmbed(user, guild)
                                .setColor(0xFF0000)
                                .setDescription(
                                        bot.getRandomMsg(guild.getId(), "errors.nsfw_random", member.getEffectiveName())
                                )
                                .build();
                        
                        tc.sendMessage(notNsfw).queue();
                        return;
                    }
                    if(command.hasAttribute("manage_server")){
                        if(!bot.getPermUtil().hasPermission(tc, msg.getMember(), Permission.MANAGE_SERVER)){
                            bot.getEmbedUtil().sendPermError(tc, user, Permission.MANAGE_SERVER, false);
                            return;
                        }
                    }
                    if(command.hasAttribute("guild_only") && !guild.getId().equals(IDs.GUILD.getId())){
                        MessageEmbed embed = bot.getEmbedUtil().getEmbed(user, guild)
                                .setColor(0xFF0000)
                                .setDescription(
                                        bot.getMsg(guild.getId(), "errors.guild_only")
                                                .replace("{link}", Links.DISCORD.getUrl())
                                )
                                .build();
                        
                        tc.sendMessage(embed).queue();
                        return;
                    }

                    try{
                        HANDLER.execute(command, msg, args[1] == null ? "" : args[1]);
                    }catch(Exception ex){
                        logger.error("Couldn't perform command!", ex);
                        bot.getEmbedUtil().sendError(tc, user, "errors.unknown", ex.getMessage());
                    }
                }
        );
    }

    private String[] split(String raw, int length){
        return Arrays.copyOf(raw.substring(length).trim().split("\\s+", 2), 2);
    }
}
