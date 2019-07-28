package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.io.IOException;
import java.io.InputStream;

@CommandDescription(
        name = "Quote",
        description =
                "Quote a message of a member from a channel.\n" +
                "You have to mention a channel when the message isn't in the same one.",
        triggers = {"quote"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}quote <messageID>\n" +
                        "{p}quote <messageID> [#channel]")
        }
)
public class CmdQuote implements Command{

    private PurrBot bot;

    public CmdQuote(PurrBot bot){
        this.bot = bot;
    }

    private Message getMessage(String id, TextChannel channel){
        Message message;
        try{
            message = channel.retrieveMessageById(id).complete();
        }catch(Exception ex){
            message = null;
        }

        return message;
    }

    private void sendQuoteEmbed(Message msg, String link, TextChannel channel) {
        EmbedBuilder quoteEmbed = bot.getEmbedUtil().getEmbed()
                .setAuthor(String.format(
                        "Quote from %s",
                        msg.getMember() == null ? "Unknown Member" : msg.getMember().getEffectiveName()
                ), msg.getJumpUrl(), msg.getAuthor().getEffectiveAvatarUrl())
                .setDescription(msg.getContentRaw())
                .setImage(link)
                .setFooter(String.format(
                        "Posted in #%s",
                        msg.getTextChannel().getName()
                ), null)
                .setTimestamp(msg.getTimeCreated());

        channel.sendMessage(quoteEmbed.build()).queue();
    }

    @Override
    public void execute(Message msg, String s){
        String[] args = s.split(" ");
        TextChannel tc = msg.getTextChannel();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_MANAGE))
            msg.delete().queue();

        if(args.length == 0){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                    "To few arguments!\n" +
                    "Usage: `%squote <messageID> [#channel]`",
                    bot.getPrefix(msg.getGuild().getId())
            ));
            return;
        }

        if(msg.getMentionedChannels().isEmpty()){
            if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_HISTORY)){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "I need permission to see the message history!");
                return;
            }

            Message quote = getMessage(args[0], tc);

            if(quote == null){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
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
                    bot.getEmbedUtil().sendError(
                            tc,
                            msg.getAuthor(),
                            "The quoted message doesn't have any images, nor a message itself!"
                    );
                    return;
                }

                sendQuoteEmbed(quote, link, tc);
            }else{
                InputStream is;

                try{
                    is = bot.getImageUtil().getQuoteImg(quote);
                }catch(IOException ex){
                    is = null;
                }

                if(is == null){
                    sendQuoteEmbed(quote, null, tc);
                    return;
                }
                String name = String.format("quote_%s.png", quote.getId());

                MessageEmbed embed = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                        .setDescription(String.format(
                                "Quote from %s in %s [`[Link]`](%s)",
                                quote.getMember() == null ? "`Unknown Member`" : quote.getMember().getEffectiveName(),
                                quote.getTextChannel().getAsMention(),
                                quote.getJumpUrl()
                        ))
                        .setImage(String.format(
                                "attachment://%s",
                                name
                        ))
                        .build();

                tc.sendFile(is, name).embed(embed).queue();
            }
            return;
        }

        TextChannel channel = msg.getMentionedChannels().get(0);
        if(channel.isNSFW() && !tc.isNSFW()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), String.format(
                    "The mentioned channel (%s) is a NSFW channel, while this channel here isn't!\n" +
                    "I won't post quotes from NSFW channels in non-NSFW channels.",
                    channel.getAsMention()
            ));
            return;
        }

        if(bot.getPermUtil().hasPermission(channel, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)){
            Message quote = getMessage(args[0], channel);

            if(quote == null){
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "The provided ID was invalid!");
                return;
            }

            if(!quote.getAttachments().isEmpty()){
                String link = quote.getAttachments().stream().filter(Message.Attachment::isImage).findFirst()
                        .map(Message.Attachment::getUrl).orElse(null);

                if(link == null && quote.getContentRaw().isEmpty()){
                    bot.getEmbedUtil().sendError(
                            tc,
                            msg.getAuthor(),
                            "The quoted message doesn't have any images, nor a message itself!"
                    );
                    return;
                }

                sendQuoteEmbed(quote, link, tc);
            }else{
                InputStream is;

                try{
                    is = bot.getImageUtil().getQuoteImg(quote);
                }catch(IOException ex){
                    is = null;
                }

                if(is == null){
                    sendQuoteEmbed(quote, null, tc);
                    return;
                }
                String name = String.format("quote_%s.png", quote.getId());

                MessageEmbed embed = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                        .setDescription(String.format(
                                "Quote from %s in %s [`[Link]`](%s)",
                                quote.getMember() == null ? "`Unknown Member`" : quote.getMember().getEffectiveName(),
                                quote.getTextChannel().getAsMention(),
                                quote.getJumpUrl()
                        ))
                        .setImage(String.format(
                                "attachment://%s",
                                name
                        ))
                        .build();

                tc.sendFile(is, name).embed(embed).queue();
            }
        }else{
            bot.getEmbedUtil().sendError(
                    tc,
                    msg.getAuthor(),
                    "I need permissions to see messages in the mentioned channel!"
            );
        }
    }
}
