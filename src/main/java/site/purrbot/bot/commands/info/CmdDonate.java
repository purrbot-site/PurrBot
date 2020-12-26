package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Donate",
        description = "purr.info.donate.description",
        triggers = {"donate", "donation", "donations", "donator"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}donate [--dm]"),
                @CommandAttribute(key = "help", value = "{p}donate [--dm]")
        }
)
public class CmdDonate implements Command{
    
    private final PurrBot bot;
    
    public CmdDonate(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        MessageEmbed embed = bot.getEmbedUtil().getEmbed(member)
                .setDescription(bot.getMsg(guild.getId(), "purr.info.donate.embed.description"))
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.donate.embed.donators_title"),
                        bot.getMsg(guild.getId(), "purr.info.donate.embed.donators_value"),
                        false
                )
                .addField(
                        EmbedBuilder.ZERO_WIDTH_SPACE,
                        getDonators(),
                        false
                )
                .build();
        
        if(bot.getMessageUtil().hasArg("dm", args)){
            member.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(embed))
                    .queue(
                            message -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.donate.dm_success", member.getAsMention())
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS)),
                            error -> tc.sendMessage(
                                    bot.getMsg(guild.getId(), "purr.info.donate.dm_failure", member.getAsMention())
                            ).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS))
                    );
            
            return;
        }
        
        tc.sendMessage(embed).queue();
    }
    
    private String getDonators(){
        List<String> ids = bot.getDonators();
        
        return ids.stream().map(userId -> bot.getShardManager().getUserById(userId))
                .filter(Objects::nonNull)
                .map(user -> String.format("%s (%s)", user.getAsTag(), user.getAsMention()))
                .collect(Collectors.joining("\n"));
    }
}
