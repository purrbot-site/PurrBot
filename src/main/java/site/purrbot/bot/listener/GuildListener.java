package site.purrbot.bot.listener;

import ch.qos.logback.classic.Logger;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;
import site.purrbot.bot.constants.Links;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public class GuildListener extends ListenerAdapter{

    private Logger logger = (Logger)LoggerFactory.getLogger(GuildListener.class);

    private PurrBot bot;

    public GuildListener(PurrBot bot){
        this.bot = bot;
    }

    private boolean isBotGuild(Guild guild){
        long bots    = guild.getMembers().stream().filter(member -> member.getUser().isBot()).count();
        long members = guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count();

        return bots > (members + 2);
    }

    private void sendWebhook(Guild guild, Type type, boolean autoLeave){
        int color;
        String event;
        String content;
        String emote;

        switch(type){
            case JOIN:
                color   = 0x00FF00;
                event   = "Joined Guild";
                content = String.format(".leave %s", guild.getId());
                emote   = Emotes.JOINED_GUILD.getEmote();
                break;

            case LEAVE:
                color = 0xFF0000;
                event   = "Left Guild";
                content = String.format("ID: %s", guild.getId());
                emote   = Emotes.LEFT_GUILD.getEmote();
                break;

            case UNKNOWN:
            default:
                color   = 0x7F8C8D;
                event   = "Unknown Action";
                content = "???";
                emote   = "";
        }

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(color)
                .setThumbnailUrl(guild.getIconUrl())
                .setTitle(new WebhookEmbed.EmbedTitle(emote, null))
                .addField(new WebhookEmbed.EmbedField(
                        true, "Name", guild.getName()
                ))
                .addField(new WebhookEmbed.EmbedField(
                        true, "Shard [Current/Total]", guild.getJDA().getShardInfo().getShardString()
                ))
                .addField(new WebhookEmbed.EmbedField(
                        false, "Owner", String.format(
                                "%s | %s (`%s`)",
                                guild.getOwner().getAsMention(),
                                guild.getOwner().getUser().getName(),
                                guild.getOwner().getUser().getId()
                        )
                ))
                .addField(new WebhookEmbed.EmbedField(
                        false, "Members", String.format(
                                "```\n" +
                                "Total: %5d\n" +
                                "Bots:  %5d\n" +
                                "Users: %5d\n" +
                                "```",
                                guild.getMembers().size(),
                                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count(),
                                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count()
                        )
                ))
                .setFooter(new WebhookEmbed.EmbedFooter(
                        String.format(
                                "Guild #%d",
                                bot.getShardManager().getGuildCache().size()
                        ),
                        null
                ))
                .setTimestamp(ZonedDateTime.now())
                .build();

        bot.getWebhookUtil().sendMsg(
                bot.getgFile().getString("config", "guild-webhook"),
                guild.getSelfMember().getUser().getEffectiveAvatarUrl(),
                autoLeave ? "Auto-left" : event,
                content,
                embed
        );


    }

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId())){
            guild.getOwner().getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage(String.format(
                            "I left your Discord `%s` for the following reason:\n" +
                            "```\n" +
                            "[Auto Leave] Your Discord is blacklisted! Join [our Discord](%s) for the reason.\n" +
                            "```",
                            guild.getName(),
                            Links.DISCORD.getUrl()
                    )).queue(message -> {
                        logger.info("[Auto Leave] Reason: Blacklisted Guild. Send successful: Yes");
                        guild.leave().queue();
                    }, throwable -> {
                        logger.info("[Auto Leave] Reason: Blacklisted Guild. Send successful: No");
                        guild.leave().queue();
                    })
            );
            return;
        }

        if(isBotGuild(guild)) {
            if(!guild.getOwner().getUser().getId().equals(IDs.ANDRE_601.getId())){
                guild.getOwner().getUser().openPrivateChannel().queue(channel ->
                        channel.sendMessage(String.format(
                                "I left your Discord `%s` for the following reason:\n" +
                                "```\n" +
                                "[Auto Leave] Your Discord has more bots than users.\n" +
                                "```",
                                guild.getName()
                        )).queue(message -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: Yes");
                        }, throwable -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: No");
                        })
                );
                sendWebhook(guild, Type.LEAVE, true);

                return;
            }
        }

        bot.getDbUtil().addGuild(guild.getId());

        logger.info(String.format(
                "[Guild join] Name: %s (%s), Members: %d (Bots: %d, Users: %d)",
                guild.getName(),
                guild.getId(),
                guild.getMembers().size(),
                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count(),
                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count()
        ));

        sendWebhook(guild, Type.JOIN, false);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event){
        Guild guild = event.getGuild();

        if(bot.getBlacklist().contains(guild.getId()))
            return;

        if(isBotGuild(guild))
            if(!guild.getOwner().getUser().getId().equals(IDs.ANDRE_601.getId()))
                return;

        bot.getDbUtil().delGuild(guild.getId());

        bot.invalidateCache(guild.getId());

        logger.info(String.format(
                "[Guild leave] Name: %s (%s), Members: %d (Bots: %d, Users: %d)",
                guild.getName(),
                guild.getId(),
                guild.getMembers().size(),
                guild.getMembers().stream().filter(member -> member.getUser().isBot()).count(),
                guild.getMembers().stream().filter(member -> !member.getUser().isBot()).count()
        ));

        sendWebhook(guild, Type.LEAVE, false);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event){
        Guild guild = event.getGuild();

        if(isBotGuild(guild)){
            if(!guild.getOwner().getUser().getId().equals(IDs.ANDRE_601.getId())) {
                guild.getOwner().getUser().openPrivateChannel().queue(channel ->
                        channel.sendMessage(String.format(
                                "I left your Discord `%s` for the following reason:\n" +
                                "```\n" +
                                "[Auto Leave] Your Discord has more bots than users.\n" +
                                "```",
                                guild.getName()
                        )).queue(message -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: Yes");
                        }, throwable -> {
                            guild.leave().queue();
                            logger.info("[Auto Leave] Reason: Bot-Discord. Send successful: No");
                        })
                );

                sendWebhook(guild, Type.LEAVE, true);
                return;
            }
        }

        if(event.getUser().isBot())
            return;

        if(bot.getWelcomeChannel(guild.getId()).equals("none"))
            return;

        TextChannel tc = guild.getTextChannelById(bot.getWelcomeChannel(guild.getId()));

        if(tc == null)
            return;

        if(!bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_WRITE))
            return;

        String msg = bot.getWelcomeMsg(guild.getId());

        if(msg == null)
            msg = "Hello {mention}!";

        Message message = new MessageBuilder(bot.getMessageUtil().formatPlaceholders(msg, event.getMember())).build();

        if(bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)) {
            InputStream is;

            try {
                is = bot.getImageUtil().getWelcomeImg(
                        event.getUser(),
                        guild.getMembers().size(),
                        bot.getWelcomeImg(guild.getId()),
                        bot.getWelcomeColor(guild.getId())
                );
            }catch(IOException ex){
                is = null;
            }

            if(is == null){
                tc.sendMessage(message).queue();
                return;
            }

            tc.sendMessage(message).addFile(is, String.format(
                    "welcome_%s.png",
                    event.getUser().getId()
            )).queue();
        }else{
            tc.sendMessage(message).queue();
        }
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event){
        Guild guild = event.getGuild();
        String id = bot.getWelcomeChannel(guild.getId());

        if(id.equals("none"))
            return;

        TextChannel tc = guild.getTextChannelById(id);
        if(tc == null) {
            bot.setWelcomeChannel(guild.getId(), id);
            return;
        }

        if(event.getChannel().getId().equals(id))
            bot.setWelcomeChannel(guild.getId(), "none");
    }

    private enum Type{
        JOIN,
        LEAVE,
        UNKNOWN
    }
}
