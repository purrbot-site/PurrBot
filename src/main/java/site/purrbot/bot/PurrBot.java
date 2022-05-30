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

package site.purrbot.bot;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.commands.BotCommand;
import site.purrbot.bot.commands.fun.CmdBite;
import site.purrbot.bot.util.DBManager;
import site.purrbot.bot.util.FileManager;
import site.purrbot.bot.util.GuildSettingsManager;
import site.purrbot.bot.util.constants.IDs;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Random;

public class PurrBot{
    
    private final Logger LOGGER = (Logger)LoggerFactory.getLogger(PurrBot.class);
    
    private static PurrBot bot;
    
    // JDA stuff
    private ShardManager shardManager = null;
    
    // JDA-Chewtils stuff
    private CommandClient commandClient;
    private final EventWaiter eventWaiter = new EventWaiter();
    
    // Bot stuff
    private FileManager fileManager;
    private DBManager dbManager;
    private GuildSettingsManager guildSettingsManager;
    
    private final Random random = new Random();
    
    // ============================================================================================
    
    public static void main(String[] args){
        try{
            (bot = new PurrBot()).startBot();
        }catch(LoginException ex){
            new PurrBot().LOGGER.warn("Unable to login to Discord!", ex);
        }
    }
    
    public void initUpdater(){
        
    }
    
    // Getters
    public static PurrBot getBot(){
        return bot;
    }
    
    public ShardManager getShardManager(){
        return shardManager;
    }
    
    public CommandClient getCommandClient(){
        return commandClient;
    }
    
    public EventWaiter getEventWaiter(){
        return eventWaiter;
    }
    
    public FileManager getFileManager(){
        return fileManager;
    }
    
    public DBManager getDbManager(){
        return dbManager;
    }
    
    public GuildSettingsManager getGuildSettingsManager(){
        return guildSettingsManager;
    }
    
    public int getNextRandomInt(int limit){
        if(limit > 0)
            return random.nextInt(limit);
        
        return random.nextInt();
    }
    
    public BotCommand[] getCommands(){
        return new BotCommand[]{
            new CmdBite()
        };
    }
    
    private void startBot() throws LoginException{
        fileManager = new FileManager();
        dbManager = new DBManager();
        guildSettingsManager = new GuildSettingsManager();
        
        loadFiles();
        
        commandClient = new CommandClientBuilder()
            .setOwnerId(IDs.ANDRE_601)
            .addSlashCommands(getCommands())
            .build();
    
        MessageAction.setDefaultMentions(EnumSet.of(
            Message.MentionType.ROLE,
            Message.MentionType.USER
        ));
        
        shardManager = DefaultShardManagerBuilder.createDefault(getFileManager().getString("config", "bot-token", null))
            .disableIntents(GatewayIntent.GUILD_VOICE_STATES)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .disableCache(CacheFlag.VOICE_STATE)
            .setChunkingFilter(ChunkingFilter.include(Long.parseLong(IDs.SERVER)))
            .setMemberCachePolicy(MemberCachePolicy.OWNER)
            .addEventListeners(
                commandClient
            )
            .setShardsTotal(-1)
            .setActivity(Activity.of(Activity.ActivityType.PLAYING, "Game"))
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .build();
    }
    
    private void loadFiles(){
        fileManager.addFile("config")
            .addFile("data")
            .addFile("random")
            // Translation files
            .addLanguage("de-CH")
            .addLanguage("de-DE")
            .addLanguage("en")
            .addLanguage("en-OWO")
            .addLanguage("es-ES")
            .addLanguage("fr-FR")
            .addLanguage("ko-KR")
            .addLanguage("pt-BR")
            .addLanguage("ru-RU")
            .addLanguage("tr-TR");
    }
}
