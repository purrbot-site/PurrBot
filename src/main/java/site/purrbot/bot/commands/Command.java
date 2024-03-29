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

import com.github.rainestormee.jdacommand.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.collections4.Bag;
import site.purrbot.bot.PurrBot;

import java.util.*;

public interface Command extends AbstractCommand<Message>{
    
    /*
     * We convert the default "execute(Message, String, String)" to our own "run(Guild, TextChannel, Message, Member, String...)"
     * method to have a better command system.
     * 
     * This method isn't automatically implemented into the classes, since it's being overridden here.
     */
    @Override
    default void execute(Message message, String s, String trigger){
        String[] args = s.isEmpty() ? new String[0] : s.split("\\s+", 3);
        
        if(message.getMember() == null)
            return;
        
        if(args.length > 0 && args[0].equalsIgnoreCase("help")){
            showHelp(message, trigger, message.getMember());
            return;
        }
        
        
        // Get all members mentioned through the args
        List<Member> members = resolveMembers(message.getGuild(), message);
        
        // We trigger the below method to run the commands.
        run(message.getGuild(), message.getChannel().asTextChannel(), message, message.getMember(), members, args);
    }
    
    /*
     * Allows to define a collection of Permissions required for the command to work properly.
     * Defaults to the Default set of permissions (Embed links, add reactions, use ext. emojis)
     * 
     * Send message perms are always checked separately before these permissions, so they aren't
     * included here.
     */
    default EnumSet<Permission> getPermissions(){
        return EnumSet.of(
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EXT_EMOJI
        );
    }
    
    default List<Member> resolveMembers(Guild guild, Message msg){
        Bag<Member> memberBag = msg.getMentions().getMembersBag();
        
        // If the message starts with the bots mention, remove first appearance from the bag.
        if(msg.getContentRaw().startsWith(guild.getSelfMember().getAsMention()))
            memberBag.remove(guild.getSelfMember(), 1);
        
        return new ArrayList<>(memberBag.uniqueSet());
    }
    
    default void showHelp(Message msg, String trigger, Member sender){
        Guild guild = msg.getGuild();
        
        if(!this.hasAttribute("usage")){
            PurrBot.get().getEmbedUtil().sendError(msg.getGuildChannel().asTextChannel(), sender, "errors.no_help_info");
            return;
        }
    
        EmbedBuilder embed = PurrBot.get().getEmbedUtil().getEmbed(sender)
            .setTitle(
                PurrBot.get().getMsg(guild.getId(), "misc.command_help.title")
                    .replace("{command}", trigger.toLowerCase(Locale.ROOT))
            )
            .setDescription(
                PurrBot.get().getMsg(guild.getId(), this.getDescription().description())
            )
            .addField(
                PurrBot.get().getMsg(guild.getId(), "misc.command_help.required_permissions"),
                MarkdownUtil.codeblock(
                    beautifyPerms(guild.getSelfMember(), msg.getGuildChannel().asTextChannel())
                ),
                false
            )
            .addField(
                PurrBot.get().getMsg(guild.getId(), "misc.command_help.command_usage"),
                MarkdownUtil.codeblock(
                    this.getAttribute("usage").replace("{p}", PurrBot.get().getPrefix(guild.getId()))
                ),
                false
            );
        
        msg.replyEmbeds(embed.build()).addActionRow(Button.link(
            "https://docs.purrbot.site/bot/commands#" + this.getDescription().name().toLowerCase(Locale.ROOT),
            PurrBot.get().getMsg(guild.getId(), "misc.command_help.more_info")
        )).queue();
    }
    
    default String beautifyPerms(Member self, TextChannel tc){
        StringJoiner joiner = new StringJoiner("\n");
        
        // The help command wouldn't show without send message perms, so we know this is always true.
        joiner.add("✅ " + Permission.MESSAGE_SEND.getName());
        
        for(Permission permission : getPermissions()){
            joiner.add(
                (self.hasPermission(tc, permission) ? "✅" : "❌") + " " +
                permission.getName()
            );
        }
        
        return joiner.toString();
    }
    
    /*
     * This is the method we use in the commands to provide the information for easier handling.
     */
    void run(Guild guild, TextChannel tc, Message msg, Member member, List<Member> members, String... args);
}
