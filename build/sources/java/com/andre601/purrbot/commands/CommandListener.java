package com.andre601.purrbot.commands;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.LevelUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.constants.Emotes;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.util.constants.Links;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("CommandExecutor");
    private static final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(THREAD_GROUP, r, "CommandPool")
    );

    private final CommandHandler HANDLER;

    public CommandListener(CommandHandler handler){
        this.HANDLER = handler;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(!ReadyListener.isReady()) return;

        CMD_EXECUTOR.execute(
                () -> {
                    Message msg = event.getMessage();
                    Guild guild = event.getGuild();

                    if(PermUtil.isBot(msg)) return;
                    if(PermUtil.isSelf(msg)) return;
                    if(PermUtil.isDM(msg)) return;
                    if(!DBUtil.hasPrefix(msg, guild)){
                        if(guild.getId().equals(IDs.GUILD.getId()) && !PermUtil.isBeta()) {
                            LevelUtil.giveXP(msg.getMember(), false, event.getTextChannel());
                        }
                        return;
                    }

                    TextChannel tc = event.getTextChannel();
                    String prefix = DBUtil.getPrefix(guild);
                    String raw = msg.getContentRaw();

                    if(!PermUtil.check(tc, Permission.MESSAGE_READ)) return;
                    if(!PermUtil.check(tc, Permission.MESSAGE_HISTORY)) return;
                    if(!PermUtil.check(tc, Permission.MESSAGE_WRITE)) return;

                    if(raw.equalsIgnoreCase(guild.getSelfMember().getAsMention())){
                        tc.sendMessage(String.format(
                                "%s Hi there! My prefix on this guild is `%s`\n" +
                                "You can run `%shelp` for a list of my commands.",
                                msg.getAuthor().getAsMention(),
                                prefix,
                                prefix
                        )).queue();
                        return;
                    }

                    String[] split = raw.split("\\s+", 2);
                    String commandString;

                    try{
                        if(raw.startsWith(prefix))
                            commandString = split[0].substring(prefix.length());
                        else
                            commandString = split[0].substring(guild.getSelfMember().getAsMention().length());
                    }catch(Exception ex){
                        return;
                    }

                    Command command = (Command)HANDLER.findCommand(commandString.toLowerCase());
                    if(command == null) return;
                    if(command.hasAttribute("owner") && !msg.getAuthor().getId().equals(IDs.ANDRE_601.getId())) return;
                    if(!PermUtil.check(tc, Permission.MESSAGE_EMBED_LINKS)){
                        tc.sendMessage(MessageFormat.format(
                        "{0} I need permission to embed links in this channel!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                        return;
                    }
                    if(!PermUtil.check(tc, Permission.MESSAGE_ADD_REACTION)){
                        EmbedUtil.error(msg, "I need permission to add reactions!");
                        return;
                    }
                    if(command.hasAttribute("nsfw") && !tc.isNSFW()){
                        EmbedUtil.error(msg, String.format(
                                MessageUtil.getRandomNotNSFW(),
                                msg.getAuthor().getName()
                        ));
                        return;
                    }
                    if(command.hasAttribute("manage_server")){
                        if(!PermUtil.check(msg.getMember(), Permission.MANAGE_SERVER)){
                            EmbedUtil.error(msg, "You need the `manage server` permission!");
                            return;
                        }
                    }
                    if(command.hasAttribute("guild_only") && !guild.getId().equals(IDs.GUILD.getId())){
                        EmbedUtil.error(msg, String.format(
                                "This command can only be used [in my Discord](%s)!",
                                Links.DISCORD_INVITE.getLink()
                        ));
                        return;
                    }

                    try{
                        if(guild.getId().equals(IDs.GUILD.getId()) && !PermUtil.isBeta()){
                            LevelUtil.giveXP(msg.getMember(), true, msg.getTextChannel());
                        }

                        //noinspection unchecked
                        HANDLER.execute(command, msg, split.length > 1 ? split[1] : "");
                    }catch(Exception ex){
                        PurrBot.getLogger().error("Couldn't perform command!", ex);
                        EmbedUtil.error(msg, String.format(
                                "Uhm... This is a bit embarrassing now, but I had an error with a command. %s\n" +
                                "Please [join my server](%s) or report the issue [on GitHub](%s)\n" +
                                "\n" +
                                "**Cause of error**:\n" +
                                "`%s`",
                                Emotes.UHM.getEmote(),
                                Links.DISCORD_INVITE.getLink(),
                                Links.GITHUB.getLink(),
                                ex.getMessage()
                        ));
                    }
                }
        );
    }

}
