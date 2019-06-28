package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;
import site.purrbot.bot.constants.IDs;

import javax.swing.plaf.metal.MetalMenuBarUI;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@CommandDescription(
        name = "User",
        description = "Gives you information about yourself or a mentioned user",
        triggers = {"user", "member", "userinfo", "memberinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value =
                        "{p}user\n" +
                        "{p}user @user"
                )
        }
)
public class CmdUser implements Command{

    private PurrBot bot;

    public CmdUser(PurrBot bot){
        this.bot = bot;
    }

    private String getRoles(Member member){
        List<Role> roles = member.getRoles();

        if(roles.size() <= 1)
            return "`No other roles`";

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < roles.size(); i++){
            Role role = roles.get(i);
            String name = String.format("%s", role.getName().replace("`", "'"));

            if(sb.length() + name.length() + 20 > MessageEmbed.VALUE_MAX_LENGTH){
                int rolesLeft = roles.size() - i;

                sb.append("**__+").append(rolesLeft).append(" more__**");
                break;
            }

            sb.append(name).append("\n");
        }

        return sb.toString();
    }

    private String getNickname(Member member){
        String nick = member.getNickname();

        return nick.length() > 25 ? nick.substring(0, 24) + "..." : nick;
    }

    private String getGame(Game game){
        String type;

        switch(game.getType()){
            default:
            case DEFAULT:
                type = "Playing";
                break;

            case WATCHING:
                type = "Watching";
                break;

            case LISTENING:
                type = "Listening to";
                break;

            case STREAMING:
                type = "Streaming";
                break;
        }

        return String.format(
                "%s %s",
                type,
                game.getName().length() > 25 ? game.getName().substring(0, 24) + "..." : game.getName()
        );
    }

    @Override
    public void execute(Message msg, String args) {
        Member member;
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            member = msg.getMember();
        }else{
            member = msg.getMentionedMembers().get(0);
        }

        String imgName = String.format("userinfo_%s.png", member.getUser().getId());

        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(msg.getAuthor())
                .addField(
                        String.format(
                            "%s %s %s",
                            member.getUser().getName(),
                            msg.getGuild().getOwner().equals(member) ? Emotes.OWNER.getEmote() : "",
                            member.getUser().isBot() ? Emotes.BOT.getEmote() : ""
                        ),
                        String.format(
                            "```yaml\n" +
                            "%s" +
                            "ID:   %s\n" +
                            "%s\n" +
                            "```",
                            member.getNickname() == null ? "" : "Nick: " + getNickname(member) + "\n",
                            member.getUser().getId(),
                            member.getGame() == null ? "" : "Game: " + getGame(member.getGame())
                        ),
                        false
                )
                .addField(
                        "Avatar",
                        String.format(
                                "[`Avatar URL`](%s)",
                                member.getUser().getEffectiveAvatarUrl()
                        ),
                        true
                )
                .addField(
                        "Highest Role",
                        member.getRoles().isEmpty() ? "`No roles assigned`" : member.getRoles().get(0).getAsMention(),
                        true
                )
                .addField(
                        "Other Roles",
                        getRoles(member),
                        false
                )
                .addField(
                        "Dates",
                        String.format(
                                "```yaml\n" +
                                "Account created: %s\n" +
                                "Guild joined:    %s\n" +
                                "```",
                                bot.getMessageUtil().formatTime(LocalDateTime.from(member.getUser().getCreationTime())),
                                bot.getMessageUtil().formatTime(LocalDateTime.from(member.getJoinDate()))
                        ),
                        false
                );

        if(msg.getGuild().getId().equals(IDs.GUILD.getId()) && !member.getUser().isBot() && !bot.isBeta())
            embed.addField(
                    "XP",
                    String.format(
                            "`%d/%d`",
                            bot.getDbUtil().getXp(member.getUser().getId()),
                            (long)bot.getLevelManager().reqXp(bot.getDbUtil().getLevel(member.getUser().getId()))
                    ),
                    true
            )
            .addField(
                    "Level",
                    String.format(
                            "`%d`",
                            bot.getDbUtil().getLevel(member.getUser().getId())
                    ),
                    true
            );

        InputStream is;
        try {
            is = bot.getImageUtil().getStatusAvatar(
                    member.getUser().getEffectiveAvatarUrl(),
                    member.getOnlineStatus().toString()
            );
        }catch(IOException ex){
            is = null;
        }

        if(is == null){
            tc.sendMessage(embed.setThumbnail(member.getUser().getEffectiveAvatarUrl()).build()).queue();
            return;
        }

        tc.sendFile(is, String.format("%s", imgName)).embed(embed.setThumbnail(String.format(
                "attachment://%s",
                imgName
                )
        ).build()).queue();
    }
}
