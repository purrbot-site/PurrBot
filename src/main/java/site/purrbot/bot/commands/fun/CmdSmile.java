package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.util.HttpUtil;

@CommandDescription(
        name = "Smile",
        description = "purr.fun.smile.description",
        triggers = {"smile", "grinning", "grin"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}smile"),
                @CommandAttribute(key = "help", value = "{p}smile")
        }
)
public class CmdSmile implements Command, HttpUtil.ImageAPI{
    
    private final PurrBot bot;
    
    public CmdSmile(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        if(guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
    
        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.fun.smile.loading")
        ).queue(message -> bot.getHttpUtil().handleRequest(this, member, message, true));
    }
    
    @Override
    public String getCategory(){
        return "fun";
    }
    
    @Override
    public String getEndpoint(){
        return "smile";
    }
    
    @Override
    public boolean isImgRequired(){
        return false;
    }
    
    @Override
    public boolean isNSFW(){
        return false;
    }
    
    @Override
    public boolean isRequest(){
        return false;
    }
}
