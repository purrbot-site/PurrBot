package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackManager extends AudioEventAdapter{

    private final AudioPlayer PLAYER;
    private final Queue<AudioInfo> QUEUE;

    public TrackManager(AudioPlayer player){
        this.PLAYER = player;
        this.QUEUE = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track, Member author){
        AudioInfo info = new AudioInfo(track, author);
        QUEUE.add(info);

        if(PLAYER.getPlayingTrack() == null){
            PLAYER.playTrack(track);
        }
    }

    public Set<AudioInfo> getQueue(){
        return new LinkedHashSet<>(QUEUE);
    }

    public AudioInfo getInfo(AudioTrack track){
        return QUEUE.stream().filter(info -> info.getTrack().equals(track)).findFirst().orElse(null);
    }

    public void purgeQueue(){
        QUEUE.clear();
    }

    public void shuffleQueue(){
        List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        purgeQueue();
        QUEUE.addAll(cQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track){
        AudioInfo info = QUEUE.element();
        VoiceChannel vChan = info.getAuthor().getVoiceState().getChannel();

        if(vChan == null)
            player.stopTrack();
        else
            info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason){
        Guild g = QUEUE.poll().getAuthor().getGuild();

        if(QUEUE.isEmpty())
            g.getAudioManager().closeAudioConnection();
        else
            player.playTrack(QUEUE.element().getTrack());
    }

}
