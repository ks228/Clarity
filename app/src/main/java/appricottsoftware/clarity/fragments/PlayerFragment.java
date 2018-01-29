package appricottsoftware.clarity.fragments;

import android.app.Activity;
import android.drm.DrmStore;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import appricottsoftware.clarity.R;
import appricottsoftware.clarity.models.Podcast;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerFragment extends Fragment /*implements View.OnClickListener*/ {

    // Collapsed state view elements
    @BindView(R.id.rl_collapse) ConstraintLayout rlCollapse;
    @BindView(R.id.iv_collapse_cover) ImageView ivCollapseCover;
    @BindView(R.id.tv_collapse_title) TextView tvCollapseTitle;
    @BindView(R.id.tv_collapse_description) TextView tvCollapseDescription;
    @BindView(R.id.ib_collapse_play_pause) ImageButton ibCollapsePlayPause;
    @BindView(R.id.ib_collapse_skip) ImageButton ibCollapseSkip;

    // Expanded state view elements
    @BindView(R.id.rl_expand) ConstraintLayout rlExpand;
    @BindView(R.id.ib_expand_like) ImageButton ibExpandLike;
    @BindView(R.id.ib_expand_dislike) ImageButton ibExpandDislike;
    @BindView(R.id.ib_expand_play_pause) ImageButton ibExpandPlayPause;
    @BindView(R.id.ib_expand_skip) ImageButton ibExpandSkip;
    @BindView(R.id.tv_expand_speed) TextView tvExpandSpeed;
    @BindView(R.id.sb_expand_seek) AppCompatSeekBar sbExpandSeek;
    @BindView(R.id.tv_expand_time_elapsed) TextView tvExpandTimeElapsed;
    @BindView(R.id.tv_expand_time_remaining) TextView tvExpandTimeRemaining;
    @BindView(R.id.tv_expand_title) TextView tvExpandTitle;
    @BindView(R.id.tv_expand_description) TextView tvExpandDescription;
    @BindView(R.id.iv_expand_cover) ImageView ivExpandCover;

    private static final String TAG = "PlayerFragment";
    private static String coverURL = "";

    private MediaControllerCompat.Callback mediaControllerCallback;
    private Activity fragmentActvity;
    private MediaControllerCompat mediaController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Initialize view lookups, listeners
        fragmentActvity = getActivity();

        // Set marquee scrolling
        tvExpandTitle.setSelected(true);
        tvExpandDescription.setSelected(true);
        tvCollapseTitle.setSelected(true);
        tvCollapseDescription.setSelected(true);

        // Initialize onclick listeners
//        ibCollapsePlayPause.setOnClickListener(this);
//        ibCollapseSkip.setOnClickListener(this);
//
//        ibExpandPlayPause.setOnClickListener(this);
//        ibExpandLike.setOnClickListener(this);
//        ibExpandDislike.setOnClickListener(this);
//        tvExpandSpeed.setOnClickListener(this);

        // Set media controller callback
//        mediaControllerCallback = getMediaControllerCallback();
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaController = MediaControllerCompat.getMediaController(fragmentActvity);
        onConnected();
        try {
        } catch(Exception e) {
            Log.e(TAG, "onStart: " + e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mediaController != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
        }
    }

    // TODO: merge with setcontrolonclick
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ib_collapse_play_pause:
//            case R.id.ib_expand_play_pause:
//                PlaybackStateCompat playbackState = mediaController.getPlaybackState();
//                int state = playbackState == null ? PlaybackStateCompat.STATE_NONE : playbackState.getState();
//                if(state == PlaybackStateCompat.STATE_STOPPED
//                        || state == PlaybackStateCompat.STATE_PAUSED
//                        || state == PlaybackStateCompat.STATE_NONE) {
//                    Log.d(TAG, "onClick: play");
//                    playAudio();
//                } else if(state == PlaybackStateCompat.STATE_PLAYING
//                        || state == PlaybackStateCompat.STATE_BUFFERING
//                        || state == PlaybackStateCompat.STATE_CONNECTING) {
//                    Log.d(TAG, "onClick: pause");
//                    pauseAudio();
//                }
//
//            default:
//                Log.d(TAG, "onClick: listener not specified");
//                break;
//        }
//    }

    public void onConnected() {
        if(mediaController != null) {
            onMetadataChanged(mediaController.getMetadata());
            onPlaybackStateChanged(mediaController.getPlaybackState());
            mediaController.registerCallback(mediaControllerCallback);
        }
    }

    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.e(TAG, "onPlaybackStateChanged: " + state.toString());
        switch(state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                Log.d(TAG, "onPlaybackStateChanged: State is playing " + state.getState());
                setPlayControls();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                Log.d(TAG, "onPlaybackStateChanged: State is paused " + state.getState());
                setPauseControls();
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                Log.d(TAG, "onPlaybackStateChanged: State is stopped " + state.getState());
                setPauseControls();
                break;
            case PlaybackStateCompat.STATE_ERROR:
                Log.e(TAG, "onPlaybackStateChanged: Error state " + state.getErrorMessage());
                break;
            default:
                Log.d(TAG, "onPlaybackStateChanged: Unhandled case " + state.getState());
                break;
        }
    }

    // Load changed details into player fragment
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        if(metadata != null) {
            tvCollapseTitle.setText(metadata.getDescription().getTitle());
            tvExpandTitle.setText(metadata.getDescription().getTitle());
            tvCollapseDescription.setText(metadata.getDescription().getDescription());
            tvCollapseDescription.setText(metadata.getDescription().getDescription());
            Glide.with(getContext()).load(metadata.getDescription().getIconUri()).into(ivCollapseCover);
            Glide.with(getContext()).load(metadata.getDescription().getIconUri()).into(ivExpandCover);
        }
    }

    public void buildTransportControls(@NonNull final Activity activity) {
        setControlOnClick(activity, ibCollapsePlayPause);
        setControlOnClick(activity, ibExpandPlayPause);
    }

    private void setControlOnClick(@NonNull final Activity activity, ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pbState = MediaControllerCompat.getMediaController(activity).getPlaybackState().getState();
                if(pbState == PlaybackStateCompat.STATE_PLAYING) {
                    setPause(fragmentActvity);
                } else {
                    setPlay(fragmentActvity);
                }
            }
        });
    }

    private void setPlay(@NonNull final Activity activity) {
        MediaControllerCompat.getMediaController(activity).getTransportControls().play();
    }

    private void setPause(@NonNull final Activity activity) {
        MediaControllerCompat.getMediaController(activity).getTransportControls().pause();
    }

    private void setPlayControls() {
        ibCollapsePlayPause.setImageResource(R.drawable.ic_player_pause);
        ibExpandPlayPause.setImageResource(R.drawable.ic_player_pause);
    }

    private void setPauseControls() {
        ibCollapsePlayPause.setImageResource(R.drawable.ic_player_play);
        ibExpandPlayPause.setImageResource(R.drawable.ic_player_play);
    }
    //TODO: Remove these functions
//    private void playAudio() {
//        if(mediaController != null) {
//            mediaController.getTransportControls().play();
//        }
//    }
//
//    private void pauseAudio() {
//        if(mediaController != null) {
//            mediaController.getTransportControls().pause();
//        }
//    }

//    private void testPlay() {
//        // Test bench
//        Episode testEpisode = new Episode();
//        testEpisode.setAudio("http://traffic.libsyn.com/nsf/nsf253-itunes.mp3?dest-id=63713");
//        play(testEpisode);
//    }

    // Hide/show view elements to make fragment full screen
    public void openPanel() {
        rlExpand.setVisibility(View.VISIBLE);
        rlCollapse.setVisibility(View.GONE);
    }

    // Hide/show view elements to make fragment bottom strip
    public void closePanel() {
        rlExpand.setVisibility(View.GONE);
        rlCollapse.setVisibility(View.VISIBLE);
    }

    public void loadPlaylist(ArrayList<Podcast> podcasts) {

    }

//    public void play(Episode episode) {
//        try {
//            mediaPlayer.setDataSource(episode.getAudio());
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mediaPlayer.start();
//                }
//            });
//            Log.e("PlayerFragment", "Playing audio");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void resume() {
//        mediaPlayer.start();
    }

//    public void pause() {
//        mediaPlayer.pause();


//    public void skip() {
//
//    }

//    private MediaControllerCompat.Callback getMediaControllerCallback() {
//        return new MediaControllerCompat.Callback() {
//            @Override
//            public void onPlaybackStateChanged(PlaybackStateCompat state) {
//                Log.d(TAG, "MediaControllerCompat.Callback: onPlaybackStateChanged to " + state.getState());
//                PlayerFragment.this.onPlaybackStateChanged(state);
//            }
//
//            @Override
//            public void onMetadataChanged(MediaMetadataCompat metadata) {
//                Log.d(TAG, "MediaControllerCompat.Callback: onMetadataChanged to " + metadata.getDescription().getMediaId() + " " + metadata.getDescription().getTitle());
//                PlayerFragment.this.onMetadataChanged(metadata);
//            }
//        };
//    }
}
