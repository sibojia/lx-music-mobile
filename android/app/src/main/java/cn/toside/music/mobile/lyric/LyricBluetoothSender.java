package cn.toside.music.mobile.lyric;

import static java.lang.Integer.min;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;


public class LyricBluetoothSender {
  MediaSession mediaSession = null;
  ReactApplicationContext reactAppContext = null;
  AudioManager audioManager = null;
  MediaSessionCompat mediaSessionCompat = null;
  private static final boolean useCompat = true;
  private static final boolean useIntent = false;

  public LyricBluetoothSender(ReactApplicationContext reactContext) {
    this.reactAppContext = reactContext;
    this.audioManager = (AudioManager) this.reactAppContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
  }

  private void initMediasession() {
    if (useIntent)
      return;

    if (useCompat) {
      if (mediaSessionCompat == null) {
        mediaSessionCompat = new MediaSessionCompat(reactAppContext.getApplicationContext(), "BluetoothlyricCompat");
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
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
      return;
    }
    //capture media events like play, stop
    //you don't actually use these callbacks
    //but you have to have this in order to pretend to be a media application
    if (mediaSession == null) {
      Log.d("Lyric", "init mediaSession");
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

  public void sendLyricLine(String lyricShow, String artistShow, String albumShow, long currentTime) {
    // Reference: https://gist.github.com/davidalbers/953eb949314fd6607616
    if(audioManager.isBluetoothA2dpOn()) {
      if (useIntent) {
        // Reference: https://www.cnblogs.com/fuyaozhishang/p/7639675.html
        Intent mediaIntent =new Intent("com.android.music.metachanged");
        mediaIntent.putExtra("artist", artistShow);
        mediaIntent.putExtra("track", lyricShow);
        mediaIntent.putExtra("album", albumShow);
        mediaIntent.putExtra("playing", true);
        this.reactAppContext.sendBroadcast(mediaIntent);
        Log.d("Lyric", "Sent Intent with lyric " + lyricShow + "/" + artistShow + "/" + albumShow);
        return;
      }

      initMediasession();

      if(useCompat) {
        MediaMetadataCompat metadataCompat = new MediaMetadataCompat.Builder()
          .putString(MediaMetadata.METADATA_KEY_TITLE, lyricShow)
          .putString(MediaMetadata.METADATA_KEY_ARTIST, artistShow)
          .putString(MediaMetadata.METADATA_KEY_ALBUM, albumShow)
//          .putString(MediaMetadata.ME)
          .build();
        PlaybackStateCompat stateCompat = new PlaybackStateCompat.Builder()
          .setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_STOP |
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO)
          .setState(PlaybackStateCompat.STATE_REWINDING, currentTime, 1.0f, SystemClock.elapsedRealtime())
          .build();
        if (!mediaSessionCompat.isActive()) {
          mediaSessionCompat.setActive(true);
        }
        mediaSessionCompat.setPlaybackState(stateCompat);
        mediaSessionCompat.setMetadata(metadataCompat);
        Log.d("Lyric", "updateMediaSessioCompat with lyric " + lyricShow + "/" + artistShow + "/" + albumShow);
      }
      else {
        MediaMetadata metadata = new MediaMetadata.Builder()
          .putString(MediaMetadata.METADATA_KEY_TITLE, lyricShow)
          .putString(MediaMetadata.METADATA_KEY_ARTIST, artistShow)
          .putString(MediaMetadata.METADATA_KEY_ALBUM, albumShow)
//        .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, artistShow)
//        .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 10)
//        .putLong(MediaMetadata.METADATA_KEY_DURATION, 100)
          .build();
        if (!mediaSession.isActive()) {
          mediaSession.setActive(true);
        }
        PlaybackState state = new PlaybackState.Builder()
          .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
            PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
            PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
          .setState(PlaybackState.STATE_PAUSED, currentTime, 1.0f, SystemClock.elapsedRealtime())
          .build();
//      mediaSession.setPlaybackState(state);
        state = new PlaybackState.Builder()
          .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
            PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
            PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
          .setState(PlaybackState.STATE_PLAYING, currentTime, 1.0f, SystemClock.elapsedRealtime())
          .build();
        mediaSession.setPlaybackState(state);
        mediaSession.setMetadata(metadata);
        Log.d("Lyric", "updateMediaSession with lyric " + lyricShow + "/" + artistShow + "/" + albumShow);
      }
      Toast.makeText(this.reactAppContext.getApplicationContext(), lyricShow, Toast.LENGTH_SHORT).show();
    }
  }

  public void release() {
    if (useIntent)
      return;
    if(useCompat) {
      if(mediaSessionCompat != null) {
        if(mediaSessionCompat.isActive()) mediaSessionCompat.setActive(false);
        Log.d("Lyric", "release mediaSession");
      }
      return;
    }
    if(mediaSession != null) {
      if (mediaSession.isActive()) mediaSession.setActive(false);
      Log.d("Lyric", "release mediaSession");
    }
  }
}
