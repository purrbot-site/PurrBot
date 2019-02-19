package com.andre601.purrbot.commands.info;

import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.andre601.purrbot.util.PermUtil;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Quote",
        description = "Quote a member.",
        triggers = {"quote"},
        attributes = {@CommandAttribute(key = "info")}
)
public class CmdQuote implements Command {

    private Message getMessage(String id, TextChannel channel){
        Message message;
        try{
            message = channel.getMessageById(id).complete();
        }catch(Exception ex){
            message = null;
        }

        return message;
    }

    private void sendQuoteEmbed(Message msg, String link, TextChannel channel) {
        EmbedBuilder quoteEmbed = EmbedUtil.getEmbed()
                .setAuthor(String.format(
                        "Quote from %s",
                        msg.getMember() == null ? "Unknown Member" : msg.getMember().getEffectiveName()
                ), msg.getJumpUrl(), msg.getAuthor().getEffectiveAvatarUrl())
                .setDescription(msg.getContentRaw())
                .setImage(link)
                .setFooter(MessageFormat.format(
                        "Posted in #{0}",
                        msg.getTextChannel().getName()
                ), null)
                .setTimestamp(msg.getCreationTime());

        channel.sendMessage(quoteEmbed.build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        String[] args = s.split(" ");
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(PermUtil.check(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length == 0){
            EmbedUtil.error(msg, MessageFormat.format(
                    "To few arguments!\n" +
                    "Usage: `{0}quote <messageID> [#channel]`",
                    DBUtil.getPrefix(guild)
            ));
            return;
        }

        if(msg.getMentionedChannels().isEmpty()){
            if(!PermUtil.check(tc, Permission.MESSAGE_HISTORY)){
                EmbedUtil.error(msg, "I need permissions to see the messages in this channel!");
                return;
            }

            Message quote = getMessage(args[0], tc);

            if(quote == null){
                EmbedUtil.error(msg, String.format(
                        "Couldn't find the message in %s\n" +
                        "Make sure, that the messageID is correct and that the message is in the right channel!",
                        tc.getAsMention()
                ));
                return;
            }
            if(!quote.getAttachments().isEmpty()){
                String link = quote.getAttachments().stream().filter(Message.Attachment::isImage).findFirst()
                        .map(Message.Attachment::getUrl).orElse(null);

                if(link == null && quote.getContentRaw().isEmpty()){
                    EmbedUtil.error(msg, "The quoted message had no valid image attached, nor any message!");
                    return;
                }

                sendQuoteEmbed(quote, link, tc);
            }else
            if(PermUtil.check(tc, Permission.MESSAGE_ATTACH_FILES)){
                try{
                    ImageUtil.getQuoteImage(tc, msg, quote);
                }catch(Exception ex){
                    sendQuoteEmbed(quote, null, tc);
                }
            }else{
                sendQuoteEmbed(quote, null, tc);
            }
            return;
        }

        TextChannel channel = msg.getMentionedChannels().get(0);
        if(channel.isNSFW() && !tc.isNSFW()){
            EmbedUtil.error(msg, String.format(
                    "The mentioned channel (%s) is labeled as NSFW, while this channel here isn't!\n" +
                    "The message wasn't quoted for safety.",
                    channel.getAsMention()
            ));
            return;
        }

        if(PermUtil.check(channel, Permission.MESSAGE_READ) && PermUtil.check(channel, Permission.MESSAGE_HISTORY)){
            Message quote = getMessage(args[0], channel);

            if(quote == null){
                EmbedUtil.error(msg, "The provided messageID was invalid!");
                return;
            }

            if(!quote.getAttachments().isEmpty()){
                String link = quote.getAttachments().stream().filter(Message.Attachment::isImage).findFirst()
                        .map(Message.Attachment::getUrl).orElse(null);

                if(link == null && quote.getContentRaw().isEmpty()){
                    EmbedUtil.error(msg, "The quoted message had no valid image attached, nor any message!");
                    return;
                }

                sendQuoteEmbed(quote, link, tc);
            }else
            if(PermUtil.check(tc, Permission.MESSAGE_ATTACH_FILES)){
                try{
                    ImageUtil.getQuoteImage(tc, msg, quote);
                }catch(Exception ex){
                    sendQuoteEmbed(quote, null, tc);
                }
            }else{
                sendQuoteEmbed(quote, null, tc);
            }
        }else{
            EmbedUtil.error(msg, "I need permissions to see the messages in the mentioned channel!");
        }
    }
}
