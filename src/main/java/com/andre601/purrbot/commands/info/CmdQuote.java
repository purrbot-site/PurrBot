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

    /**
     * Checks if a {@link net.dv8tion.jda.core.entities.Message Message} exists (is valid).
     *
     * @param  id
     *         ID of the message.
     * @param  channel
     *         A {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} where the message should be found.
     *
     * @return {@code null} if no message could be found. Else the Message itself.
     */
    private Message getMessage(String id, TextChannel channel){
        Message message;
        try{
            message = channel.getMessageById(id).complete();
        }catch(Exception ex){
            message = null;
        }

        return message;
    }

    /**
     * Method to send a {@link net.dv8tion.jda.core.entities.MessageEmbed MessageEmbed} to the provided channel.
     *
     * @param msg
     *        A {@link net.dv8tion.jda.core.entities.Message Message object} that is used for the quote information.
     * @param channel
     *        The {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} the message should be send in.
     */
    private void sendQuoteEmbed(Message msg, TextChannel channel) {
        EmbedBuilder quoteEmbed = EmbedUtil.getEmbed()
                .setAuthor(String.format(
                        "Quote from %s",
                        msg.getMember().getEffectiveName()
                ), msg.getJumpUrl(), msg.getAuthor().getEffectiveAvatarUrl())
                .setDescription(msg.getContentRaw())
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

        if(PermUtil.canDeleteMsg(tc))
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
            if(!PermUtil.canReadHistory(tc)){
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
            if(PermUtil.canUploadImage(tc)){
                try{
                    ImageUtil.getQuoteImage(tc, msg, quote);
                }catch(Exception ex){
                    sendQuoteEmbed(quote, tc);
                }
            }else{
                sendQuoteEmbed(quote, tc);
            }
            return;
        }

        TextChannel channel = msg.getMentionedChannels().get(0);
        if(PermUtil.canRead(channel) && PermUtil.canReadHistory(channel)){
            Message quote = getMessage(args[0], channel);

            if(quote == null){
                EmbedUtil.error(msg, "The provided messageID was invalid!");
                return;
            }

            if(PermUtil.canUploadImage(tc)){
                try{
                    ImageUtil.getQuoteImage(tc, msg, quote);
                }catch(Exception ex){
                    sendQuoteEmbed(quote, tc);
                }
            }else{
                sendQuoteEmbed(quote, tc);
            }
        }else{
            EmbedUtil.error(msg, "I need permissions to see the messages in the mentioned channel!");
        }
    }
}
