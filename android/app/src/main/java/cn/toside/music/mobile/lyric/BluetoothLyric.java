package cn.toside.music.mobile.lyric;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BluetoothLyric extends LyricPlayer {
  LyricBluetoothSender lyricBluetoothSender = null;
  ReactApplicationContext reactAppContext;

  boolean isSendBluetoothLyric = true;
  // String lastText = "LX Music ^-^";
  List lines = new ArrayList();
  String lyricText = "";
  String titleText = "";
  String singerText = "";
  String albumText = "";

  BluetoothLyric(ReactApplicationContext reactContext, float playbackRate) {
    this.reactAppContext = reactContext;
    this.playbackRate = playbackRate;
    this.lyricBluetoothSender = new LyricBluetoothSender(reactContext);
  }


  private void setLyricToSendBluetooth(int lineNum) {
    if (!isSendBluetoothLyric || lyricBluetoothSender == null) return;
    if (lineNum >= 0 && lineNum < lines.size()) {
      HashMap line = (HashMap) lines.get(lineNum);
      if (line != null) {
        String fakeSingerLine = titleText + "-" + singerText;
        lyricBluetoothSender.sendLyricLine((String) line.get("text"), fakeSingerLine, albumText, getCurrentTime());
        Log.d("Lyric", "send by lyric " + line.get("text"));
      }
    }
  }


  public void setLyric(String lyric, String title, String singer, String album) {
    lyricText = lyric;
    titleText = title;
    singerText = singer;
    albumText = album;
    super.setLyric(lyric, new ArrayList<String>());
    Log.d("Lyric", "set bt lyric " + title);
  }

  @Override
  public void onSetLyric(List lines) {
    this.lines = lines;
  }

  @Override
  public void onPlay(int lineNum) {
    Log.d("Lyric", "bt on play " + lineNum);
    if(this.isSendBluetoothLyric) setLyricToSendBluetooth(lineNum);
    else lyricBluetoothSender.release();
  }

  public void pauseLyric() {
    pause();
    lyricBluetoothSender.release();
  }

  public void toggleSendBluetoothLyric(boolean isSendBluetoothLyric) {
    this.isSendBluetoothLyric = isSendBluetoothLyric;
    Log.d("Lyric", "toggle bt " + isSendBluetoothLyric);
  }
}
