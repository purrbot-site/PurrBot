package listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutochannelHandler extends ListenerAdapter {

    List<VoiceChannel> active = new ArrayList<>();

    public void onGuildVoiceJoin(GuildVoiceJoinEvent e){

        HashMap<VoiceChannel, Guild> autochans = commands.CmdAutoChannel.getAutoChannels();
        VoiceChannel vc = e.getChannelJoined();
        Guild g = e.getGuild();

        if(autochans.containsKey(vc)){
            VoiceChannel nvc = (VoiceChannel)g.getController().createVoiceChannel(
                    vc.getName() + " [" + e.getMember().getUser().getId() + "]")
                    .setBitrate(vc.getBitrate())
                    .setUserlimit(vc.getUserLimit())
                    .complete();

            if(vc.getParent() != null)
                nvc.getManager().setParent(vc.getParent()).queue();

            g.getController().modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
            g.getController().moveVoiceMember(e.getMember(), nvc).queue();
            active.add(nvc);
        }

    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent e){

        HashMap<VoiceChannel, Guild> autochans = commands.CmdAutoChannel.getAutoChannels();
        Guild g = e.getGuild();

        VoiceChannel vc = e.getChannelJoined();

        if(autochans.containsKey(vc)){
            VoiceChannel nvc = (VoiceChannel)g.getController().createVoiceChannel(
                    vc.getName() + " [" + e.getMember().getUser().getId() + "]")
                    .setBitrate(vc.getBitrate())
                    .setUserlimit(vc.getUserLimit())
                    .complete();

            if(vc.getParent() != null)
                nvc.getManager().setParent(vc.getParent()).queue();

            g.getController().modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
            g.getController().moveVoiceMember(e.getMember(), nvc).queue();
            active.add(nvc);
        }

        vc = e.getChannelLeft();
        if(active.contains(vc) && vc.getMembers().size() == 0){
            active.remove(vc);
            vc.delete().queue();
        }

    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e){

        VoiceChannel vc = e.getChannelLeft();

        if(active.contains(vc) && vc.getMembers().size() == 0){
            active.remove(vc);
            vc.delete().queue();
        }

    }

    public void onVoiceChannelDelete(VoiceChannelDeleteEvent e){

        HashMap<VoiceChannel, Guild> autochans = commands.CmdAutoChannel.getAutoChannels();

        if(autochans.containsKey(e.getChannel())){
            commands.CmdAutoChannel.unsetChan(e.getChannel());
        }

    }

}
