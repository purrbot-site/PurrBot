package com.andre601.purrbot.commands;

import com.andre601.purrbot.core.PurrBot;
import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        if(!ReadyListener.getReady()) return;

        CMD_EXECUTOR.execute(
                () -> {
                    Message msg = event.getMessage();
                    Guild guild = event.getGuild();

                    if(PermUtil.isBot(msg)) return;
                    if(PermUtil.isSelf(msg)) return;
                    if(PermUtil.isDM(msg)) return;
                    if(!DBUtil.hasPrefix(msg, guild)) return;

                    TextChannel tc = event.getTextChannel();
                    String prefix = DBUtil.getPrefix(guild);
                    String raw = msg.getContentRaw();

                    if(!PermUtil.canRead(tc)) return;
                    if(!PermUtil.canReadHistory(tc)) return;
                    if(!PermUtil.canWrite(tc)) return;
                    if(!PermUtil.canSendEmbed(tc)){
                        tc.sendMessage(MessageFormat.format(
                                "{0} I need permission to embed links in this channel!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                        return;
                    }
                    if(!PermUtil.canReact(tc)){
                        EmbedUtil.error(msg, "I need permission to add reactions!");
                        return;
                    }

                    EmbedBuilder info = EmbedUtil.getEmbed(msg.getAuthor()).setDescription(MessageFormat.format(
                            "My prefix on this guild is `{0}`",
                            prefix
                    ));

                    if(raw.startsWith(event.getJDA().getSelfUser().getAsMention()) &&
                            raw.length() == event.getJDA().getSelfUser().getAsMention().length()){
                        tc.sendMessage(info.build()).queue();
                        return;
                    }

                    if(raw.startsWith(guild.getSelfMember().getAsMention()) &&
                            raw.length() == guild.getSelfMember().getAsMention().length()){
                        tc.sendMessage(info.build()).queue();
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

                    Command command = HANDLER.findCommand(commandString.toLowerCase());
                    if(command == null) return;
                    if(command.hasAttribute("owner") && !PermUtil.isCreator(msg)) return;
                    if(command.hasAttribute("nsfw") && !tc.isNSFW()){
                        EmbedUtil.error(msg, MessageFormat.format(
                                MessageUtil.getRandomNotNSFW(),
                                msg.getAuthor().getName()
                        ));
                        return;
                    }
                    if(command.hasAttribute("manage_server") && !PermUtil.userIsAdmin(msg)){
                        EmbedUtil.error(msg, "You need the `manage server` permission!");
                        return;
                    }

                    try{
                        HANDLER.execute(command, msg, split.length > 1 ? split[1] : "");
                    }catch(Exception ex){
                        PurrBot.getLogger().error("Couldn't perform command!", ex);
                    }
                }
        );
    }

}
