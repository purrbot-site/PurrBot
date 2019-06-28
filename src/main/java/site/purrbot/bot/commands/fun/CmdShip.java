package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.IDs;

import java.util.List;
import java.util.Random;

@CommandDescription(
        name = "Ship",
        description = "Checks how well you and someone else match. Mention two people to ship them instead.",
        triggers = {"ship", "shipping"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}ship @user [@user]")
        }
)
public class CmdShip implements Command{

    private PurrBot bot;

    public CmdShip(PurrBot bot){
        this.bot = bot;
    }

    private String getMessage(int chance){

        if(chance == 100){
            return "Perfect love! ❤";
        }else
        if((chance <= 99) && (chance > 90)){
            return "Don't forget to invite me to your wedding.";
        }else
        if((chance <= 90) && (chance > 80)){
            return "I can imagine them marrying each other.";
        }else
        if((chance <= 80) && (chance > 70)){
            return "\\*pushes both to each other*";
        }else
        if((chance <= 70) && (chance > 60)){
            return "This will hold for some time.";
        }else
        if((chance <= 60) && (chance > 50)){
            return "Kiss already!";
        }else
        if((chance <= 50) && (chance > 40)) {
            return "Already a couple I guess?";
        }else
        if((chance <= 40) && (chance > 30)) {
            return "I can actually imagine you as a couple.";
        }else
        if((chance <= 30) && (chance > 20)) {
            return "Friendzone+ (With some \"extras\")";
        }else
        if((chance <= 20) && (chance > 10)) {
            return "Welcome to the friendzone!";
        }else
        if((chance <= 10) && (chance > 0)) {
            return "Not even friends.";
        }else{
            return "If love is heat, then you're an ice block.";
        }
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();

        Member member1;
        Member member2;

        List<Member> members = msg.getMentionedMembers();

        if (members.isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Mention at least one user to ship!");
            return;
        }


        if(members.size() > 1)
            member2 = members.get(1);
        else
            member2 = msg.getMember();

        member1 = members.get(0);

        if(member1 == msg.getGuild().getSelfMember()){
            if(!bot.isBeta()) {
                if(bot.getPermUtil().isSpecial(member2.getUser().getId())){
                    tc.sendMessage(String.format(
                            "%s Aww sweetie. You know we will always be a 100.\n" +
                            "You don't need this command for that. ^-^",
                            member2.getAsMention()
                    )).queue();
                    return;
                }
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "N-no! You can't ship with me. >.<");
                return;
            }
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "D-don't ship with me. >~<");
            return;
        }

        if(member2 == msg.getGuild().getSelfMember()) {
            if(!bot.isBeta()){
                if(bot.getPermUtil().isSpecial(member1.getUser().getId())){
                    tc.sendMessage(String.format(
                            "%s Naw sweetie. You know we will always be a 100.\n" +
                            "You don't need a test for that ^-^",
                            member1.getAsMention()
                    )).queue();
                    return;
                }
                bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "N-no! You can't ship with me. >.<");
                return;
            }
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "D-don't ship with me. >~<");
            return;
        }

        if((member1 == msg.getMember()) && (member2 == msg.getMember())){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(),
                    "Nu! You can't ship yourself! Get someone else to ship with."
            );
            return;
        }

        if(member1.getUser().getId().equals(IDs.SNUGGLE.getId()) || member2.getUser().getId().equals(IDs.SNUGGLE.getId())){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "No shipping with my sister!.");
            return;
        }

        if(member1.getUser().isBot() || member2.getUser().isBot()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "Why do you ship with bots? O_O");
            return;
        }

        Random random = new Random();

        int result = random.nextInt(101);

        byte[] image = bot.getImageUtil().getShipImg(member1, member2, result);

        if(image == null || !bot.getPermUtil().hasPermission(tc, Permission.MESSAGE_ATTACH_FILES)){
            Message message = new MessageBuilder(String.format(
                    "`%d%%` | %s",
                    result,
                    getMessage(result)
            )).build();

            tc.sendMessage(message).queue();
            return;
        }

        tc.sendMessage(getMessage(result)).addFile(image, String.format(
                "love_%s_%s.png",
                member1.getUser().getId(),
                member2.getUser().getId()
        )).queue();

    }
}
