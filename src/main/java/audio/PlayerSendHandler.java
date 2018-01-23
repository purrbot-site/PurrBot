package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class PlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer AUDIOPLAYER;
    private AudioFrame LASTFRAME;

    public PlayerSendHandler(AudioPlayer audioPlayer){
        this.AUDIOPLAYER = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        if(LASTFRAME == null){
            LASTFRAME = AUDIOPLAYER.provide();
        }

        return LASTFRAME != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        if(LASTFRAME == null){
            LASTFRAME = AUDIOPLAYER.provide();
        }

        byte[] data = LASTFRAME != null ? LASTFRAME.data : null;
        LASTFRAME = null;

        return data;
    }

    @Override
    public boolean isOpus(){
        return true;
    }
}
