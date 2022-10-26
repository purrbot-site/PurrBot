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

package site.purrbot.bot.commands.fun;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Ship",
        description = "purr.fun.ship.description",
        triggers = {"ship", "shipping"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}ship <@user> [@user]"),
                @CommandAttribute(key = "help", value = "{p}ship <@user> [@user]")
        }
)
public class CmdShip implements Command{

    private final PurrBot bot;
    private final Random random = new Random();
    
    Cache<String, Integer> cache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    public CmdShip(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        Member member1;
        Member member2;

        List<Member> members = msg.getMentions().getMembers();

        if (members.isEmpty()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.ship.no_mention");
            return;
        }


        if(members.size() > 1){
            member1 = members.get(0);
            member2 = members.get(1);
        }else{
            member1 = member;
            member2 = members.get(0);
        }

        if(member1 == null || member2 == null)
            return;

        if(member1.getId().equals(IDs.PURR) || member2.getId().equals(IDs.PURR)){
            if(bot.isBeta()){
                if((bot.isSpecial(member1.getId()) || bot.isSpecial(member2.getId())) && bot.isSpecial(member.getId())){
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "snuggle.fun.ship.special_user", member.getAsMention())
                    ).queue();
                    return;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.fun.ship.mention_purr", member.getAsMention())
                ).queue();
            }else{
                if((bot.isSpecial(member1.getId()) || bot.isSpecial(member2.getId())) && bot.isSpecial(member.getId())){
                    byte[] img = bot.getImageUtil().getShipImg(member1, member2, 100);
                    
                    if(img != null && guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
                        tc.sendMessage(
                                bot.getMsg(guild.getId(), "purr.fun.ship.special_user", member.getAsMention())
                            )
                            .addFiles(FileUpload.fromData(img, String.format("love_%s_%s.png", member1.getId(), member2.getId())))
                            .queue();
                        return;
                    }
                    
                    tc.sendMessage(
                            bot.getMsg(guild.getId(), "purr.fun.ship.special_user", member.getAsMention())
                    ).queue();
                    return;
                }
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.fun.ship.mention_purr", member.getAsMention())
                ).queue();
            }
            msg.addReaction(Emoji.fromUnicode("\uD83D\uDE33")).queue();
            
            return;
        }

        if(member1.getId().equals(IDs.SNUGGLE) || member2.getId().equals(IDs.SNUGGLE)){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.fun.ship.mention_snuggle", member.getAsMention())
                ).queue();
            }else{
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "purr.fun.ship.mention_snuggle", member.getAsMention())
                ).queue();
            }
            return;
        }

        if(member1.equals(member) && member2.equals(member)){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.ship.mention_self", member.getAsMention())
            ).queue();
            return;
        }

        if(member1.getUser().isBot() || member2.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, member, "purr.fun.ship.target_bot");
            return;
        }
        
        Integer result = cache.get(String.format("%s:%s", member1.getId(), member2.getId()), k -> random.nextInt(101));
        if(result == null){
            result = random.nextInt(101);
            cache.put(String.format("%s:%s", member1.getId(), member2.getId()), result);
        }
        
        byte[] image;
        
        image = bot.getImageUtil().getShipImg(member1, member2, result);

        if(image == null || !guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
            MessageCreateData message = new MessageCreateBuilder()
                .addContent(String.format("`%d%%` | %s", result, getMessage(result, guild.getId())))
                .build();

            tc.sendMessage(message).queue();
            return;
        }

        tc.sendMessage(getMessage(result, guild.getId()))
            .addFiles(FileUpload.fromData(image, String.format("love_%s_%s.png", member1.getId(), member2.getId())))
            .queue();

    }
    
    private String getMessage(int chance, String id){
        if(chance == 100){
            return bot.getMsg(id, "purr.fun.ship.results.100");
        }else
        if((chance <= 99) && (chance > 90)){
            return bot.getMsg(id, "purr.fun.ship.results.91_99");
        }else
        if((chance <= 90) && (chance > 80)){
            return bot.getMsg(id, "purr.fun.ship.results.81_90");
        }else
        if((chance <= 80) && (chance > 70)){
            return bot.getMsg(id, "purr.fun.ship.results.71_80");
        }else
        if(chance == 70){
            return bot.getMsg(id, "purr.fun.ship.results.61_70");
        }else
        if(chance == 69){
            return bot.getMsg(id, "purr.fun.ship.results.69");
        }else
        if((chance <= 68) && (chance > 60)){
            return bot.getMsg(id, "purr.fun.ship.results.61_70");
        }else
        if((chance <= 60) && (chance > 50)){
            return bot.getMsg(id, "purr.fun.ship.results.51_60");
        }else
        if((chance <= 50) && (chance > 40)) {
            return bot.getMsg(id, "purr.fun.ship.results.41_50");
        }else
        if((chance <= 40) && (chance > 30)) {
            return bot.getMsg(id, "purr.fun.ship.results.31_40");
        }else
        if((chance <= 30) && (chance > 20)) {
            return bot.getMsg(id, "purr.fun.ship.results.21_30");
        }else
        if((chance <= 20) && (chance > 10)) {
            return bot.getMsg(id, "purr.fun.ship.results.11_20");
        }else
        if((chance <= 10) && (chance > 0)) {
            return bot.getMsg(id, "purr.fun.ship.results.1_10");
        }else{
            return bot.getMsg(id, "purr.fun.ship.results.0");
        }
    }
}
