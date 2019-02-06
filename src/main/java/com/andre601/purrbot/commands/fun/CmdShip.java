package com.andre601.purrbot.commands.fun;

import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.messagehandling.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Random;

@CommandDescription(
        name = "Ship",
        description = "Checks how well you and someone else match. Mention two people to ship them instead.",
        triggers = {"ship", "shipping"},
        attributes = {@CommandAttribute(key = "fun")}
)
public class CmdShip implements Command {

    public Message getMessage(int chance){
        Message message;

        if(chance == 100){
            message = new MessageBuilder("Perfect love! ❤").build();
        }else
        if((chance <= 99) && (chance > 90)){
            message = new MessageBuilder("Don't forget to invite me to your wedding.").build();
        }else
        if((chance <= 90) && (chance > 80)){
            message = new MessageBuilder("I can imagine them marrying each other.").build();
        }else
        if((chance <= 80) && (chance > 70)){
            message = new MessageBuilder("\\*pushes both to each other*").build();
        }else
        if((chance <= 70) && (chance > 60)){
            message = new MessageBuilder("This will hold for some time.").build();
        }else
        if((chance <= 60) && (chance > 50)){
            message = new MessageBuilder("Kiss already!").build();
        }else
        if((chance <= 50) && (chance > 40)) {
            message = new MessageBuilder("Already a couple I guess?").build();
        }else
        if((chance <= 40) && (chance > 30)) {
            message = new MessageBuilder("I can actually imagine you as a couple.").build();
        }else
        if((chance <= 30) && (chance > 20)) {
            message = new MessageBuilder("Friendzone+ (With some \"extras\")").build();
        }else
        if((chance <= 20) && (chance > 10)) {
            message = new MessageBuilder("Welcome to the friendzone!").build();
        }else
        if((chance <= 10) && (chance > 0)) {
            message = new MessageBuilder("Not even friends.").build();
        }else{
            message = new MessageBuilder("If love is heat, then you're an ice block.").build();
        }

        return message;
    }

    @Override
    public void execute(Message msg, String s){
        TextChannel tc = msg.getTextChannel();
        Member member1;
        Member member2;

        if (msg.getMentionedMembers().isEmpty()){
            EmbedUtil.error(msg, "Mention at least one user to ship!");
            return;
        }

        if(msg.getMentionedMembers().size() > 1){
            member2 = msg.getMentionedMembers().get(1);
        }else{
            member2 = msg.getMember();
        }

        member1 = msg.getMentionedMembers().get(0);

        if(member1 == msg.getGuild().getSelfMember() || member2 == msg.getGuild().getSelfMember()){
            EmbedUtil.error(msg, "N-no! Y-you can't ship with me! >.<");
            return;
        }

        if((member1 == msg.getMember()) && (member2 == msg.getMember())){
            EmbedUtil.error(msg, "Nu! You can't ship yourself! Get someone else to ship with.");
            return;
        }

        if(member1.getUser().isBot() || member2.getUser().isBot()){
            EmbedUtil.error(msg, "Why do you ship with bots? O_O");
            return;
        }

        Random random = new Random();

        int result = random.nextInt(101);

        byte[] image = ImageUtil.getShipImage(member1, member2, result);

        if(image == null || !PermUtil.canUploadImage(tc)){
            Message message = new MessageBuilder().append(String.format(
                    "`%d",
                    result
            )).append("%` | ").append(getMessage(result).getContentRaw()).build();

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
