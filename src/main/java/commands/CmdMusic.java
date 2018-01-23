package commands;

import audio.AudioInfo;
import audio.PlayerSendHandler;
import audio.TrackManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CmdMusic implements Command {

    private static final int PLAYLIST_LIMIT = 100;
    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYER = new HashMap<>();

    public CmdMusic(){
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(Guild g){
        AudioPlayer p = MANAGER.createPlayer();
        TrackManager m = new TrackManager(p);
        p.addListener(m);

        g.getAudioManager().setSendingHandler(new PlayerSendHandler(p));

        PLAYER.put(g, new AbstractMap.SimpleEntry<>(p, m));

        return p;
    }

    private boolean hasPlayer(Guild g){
        return PLAYER.containsKey(g);
    }

    private AudioPlayer getPlayer(Guild g){
        if(hasPlayer(g))
            return PLAYER.get(g).getKey();
        else
            return createPlayer(g);
    }

    private TrackManager getManager(Guild g){
        return PLAYER.get(g).getValue();
    }

    private boolean isIdle(Guild g){
        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;
    }

    private void loadTrack(String identifier, Member author, Message msg){

        Guild g = author.getGuild();
        getPlayer(g);

        MANAGER.setFrameBufferDuration(5000);
        MANAGER.loadItemOrdered(g, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(g).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for(int i = 0; i < (playlist.getTracks().size() > PLAYLIST_LIMIT ? PLAYLIST_LIMIT :
                         playlist.getTracks().size()); i++){
                    getManager(g).queue(playlist.getTracks().get(i), author);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {
                e.printStackTrace();
            }
        });
    }

    private void skip(Guild g){
        getPlayer(g).stopTrack();
    }

    private String getTimestamp(long milis){
        long sec = milis / 1000;
        long hours = Math.floorDiv(sec, 3600);
        sec = sec - (hours * 3600);
        long min = Math.floorDiv(sec, 60);
        sec = sec - (min * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    private String buildQueueMessage(AudioInfo info){
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "`[" + getTimestamp(length) + "]` " + title + "\n";
    }

    private void error(TextChannel tc, String title, String msg){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Music", "https://PowerPlugins.net",
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField(title, msg, false);

        tc.sendMessage(eb.build()).queue();
    }

    private void usage(TextChannel tc){

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Music", "https://PowerPlugins.net",
                tc.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField("Command:", "`" + STATIC.PREFIX + "Music`", false);

        eb.addField("Arguments:",
                "`play <url/title>` Adds a Music/Playlist to the queue.\n" +
                        "`skip` Skips the current music.\n" +
                        "`shuffle` Shuffles the current playlist." +
                        "`info` Shows info about current music.\n" +
                        "`queue <page>` Shows current playlist.\n" +
                        "`stop` Stops current music.", false);

        eb.addField("Info:", "The Music lets you add Musicvideos from YouTube by either adding a Link, " +
                "or by typing a title.", false);

        tc.sendMessage(eb.build()).queue();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        guild = e.getGuild();
        TextChannel tc = e.getTextChannel();

        if(args.length < 1){
            usage(tc);
            return;
        }

        switch(args[0].toLowerCase()) {

            case "play":
                if (args.length < 2) {
                    error(tc, "No URL/Name", "Please enter a valid source!");
                    return;
                }

                String input = Arrays.stream(args).skip(1).map(s -> " ").collect(Collectors.joining()).substring(1);

                if (!(input.startsWith("http://") || input.startsWith("https://")))
                    input = "ytsearch: " + input;

                loadTrack(input, e.getMember(), e.getMessage());
                break;

            case "skip":
                if (isIdle(guild)) {
                    error(tc, "No Music", "I don't have any music (yet)!\nPlease add music with `" +
                            STATIC.PREFIX + "music play <url/title>` while being in a Voicechannel.");
                    return;
                }

                for (int i = (args.length > 1 ? Integer.parseInt(args[1]) : 1); i == 1; i--){
                    skip(guild);
                }

                break;

            case "stop":
                if (isIdle(guild)){
                    error(tc, "No Music", "I don't have any music (yet)!\nPlease add music with `" +
                            STATIC.PREFIX + "music play <url/title>` while being in a Voicechannel.");
                    return;
                }

                getManager(guild).purgeQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();
                break;

            case "shuffle":
                if (isIdle(guild)){
                    error(tc, "No Music", "I don't have any music (yet)!\nPlease add music with `" +
                            STATIC.PREFIX + "music play <url/title>` while being in a Voicechannel.");
                    return;
                }

                getManager(guild).shuffleQueue();
                break;

            case "info":
                if (isIdle(guild)){
                    error(tc, "No Music", "I don't have any music (yet)!\nPlease add music with `" +
                            STATIC.PREFIX + "music play <url/title>` while being in a Voicechannel.");
                    return;
                }

                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();

                EmbedBuilder eb = new EmbedBuilder();

                eb.setAuthor("Music", "https://PowerPlugins.net", tc.getJDA().getSelfUser().
                        getEffectiveAvatarUrl());
                eb.addField("Current Music:", info.title, false);
                eb.addField("Duration:", "`[" + getTimestamp(track.getPosition()) + "/" +
                        getTimestamp(track.getDuration()) + "]`", true);
                eb.addField("Requested by:", info.author, true);

                tc.sendMessage(eb.build()).queue();
                break;

            case "queue":
                if (isIdle(guild)){
                    error(tc, "No Music", "I don't have any music (yet)!\nPlease add music with `" +
                            STATIC.PREFIX + "music play <url/title>` while being in a Voicechannel.");
                    return;
                }

                int sideNum = args.length > 1 ? Integer.parseInt(args[1]) : 1;

                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;

                getManager(guild).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessage(audioInfo)));

                if(tracks.size() > 10)
                    trackSublist = tracks.subList((sideNum - 1) * 10, (sideNum - 1) * 10 + 10);
                else
                    trackSublist = tracks;

                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumAll = tracks.size() >= 10 ? tracks.size()/10 : 1;

                EmbedBuilder eb2 = new EmbedBuilder();

                eb2.setAuthor("Music", "https://PowerPlugins.net", tc.getJDA().getSelfUser().
                        getEffectiveAvatarUrl());
                eb2.addField("Current Playlist:", "[" + getManager(guild).getQueue().size() + " tracks | " +
                        "Site `" + sideNum + "/" + sideNumAll + "`]", false);
                eb2.addField("Content:", out, false);

                tc.sendMessage(eb2.build()).queue();
                break;
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
