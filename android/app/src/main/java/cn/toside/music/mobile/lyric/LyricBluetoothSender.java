package cn.toside.music.mobile.lyric;

import static java.lang.Integer.min;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;


public class LyricBluetoothSender {
  MediaSession mediaSession = null;
  ReactApplicationContext reactAppContext = null;
  AudioManager audioManager = null;

  public LyricBluetoothSender(ReactApplicationContext reactContext) {
    this.reactAppContext = reactContext;
    this.audioManager = (AudioManager) this.reactAppContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
  }

  private void initMediasession() {
    //capture media events like play, stop
    //you don't actually use these callbacks
    //but you have to have this in order to pretend to be a media application
    if (mediaSession == null) {
      Log.i("Lyric", "init mediaSession");
      mediaSession = new MediaSession(reactAppContext.getApplicationContext(), "Bluetoothlyric");
      mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
      mediaSession.setCallback(new MediaSession.Callback() {
        @Override
        public void onPlay() {
          super.onPlay();
        }

        @Override
        public void onPause() {
          super.onPause();
          release();
        }

        @Override
        public void onSkipToNext() {
          super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
          super.onSkipToPrevious();
        }

        @Override
        public void onStop() {
          super.onStop();
          release();
        }
      });
    }
  }

  public void sendLyricLine(String lyricLine, String fakeSingerLine, String albumLine, long currentTime) {
    String lyricShow = lyricLine.substring(0, min(lyricLine.length(), 30));
    String artistShow = fakeSingerLine.substring(0, min(fakeSingerLine.length(), 30));
    String albumShow = albumLine.substring(0, min(albumLine.length(), 30));
    if(audioManager.isBluetoothA2dpOn()) {
      initMediasession();
      MediaMetadata metadata = new MediaMetadata.Builder()
        .putString(MediaMetadata.METADATA_KEY_TITLE, lyricShow)
        .putString(MediaMetadata.METADATA_KEY_ARTIST, artistShow)
        .putString(MediaMetadata.METADATA_KEY_ALBUM, albumShow)
//        .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, artistShow)
//        .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 10)
//        .putLong(MediaMetadata.METADATA_KEY_DURATION, 100)
        .build();
      if(!mediaSession.isActive()) {
        mediaSession.setActive(true);
      }
      PlaybackState state = new PlaybackState.Builder()
        .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
          PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
          PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
        .setState(PlaybackState.STATE_PAUSED, currentTime, 1.0f, SystemClock.elapsedRealtime())
        .build();
      mediaSession.setPlaybackState(state);
      state = new PlaybackState.Builder()
        .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
          PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
          PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
        .setState(PlaybackState.STATE_PLAYING, currentTime, 1.0f, SystemClock.elapsedRealtime())
        .build();
      mediaSession.setPlaybackState(state);
      mediaSession.setMetadata(metadata);
//      Log.i("Lyric", "updateMediaSession with lyric " + lyricShow + "/" + artistShow + "/" + albumShow);
//      Toast.makeText(this.reactAppContext.getApplicationContext(), lyricShow, Toast.LENGTH_SHORT).show();
    }
  }

  public void release() {
    if(mediaSession != null) {
      mediaSession.setActive(false);
      Log.i("Lyric", "release mediaSession");
    }
  }
}
