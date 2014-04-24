package com.rebtel.sdk.sample.calling;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();

    private Context context;

    private MediaPlayer mPlayer;

    private AudioTrack n1progressTone;

    public AudioPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    public void playRingtone() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Respect silent mode
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                try {
                    mPlayer.setDataSource(context,
                            Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.phone_loud1));
                    mPlayer.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not setup media player for ringtone");
                    mPlayer = null;
                    return;
                }
                mPlayer.setLooping(true);
                mPlayer.start();
                break;
        }
    }

    public void stopRingtone() {

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void playProgressTone() {
        stopProgressTone();
        try {
            n1progressTone = createProgressTone(context);
            n1progressTone.play();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not play progress tone");
        }
    }

    public void stopProgressTone() {
        if (n1progressTone != null) {
            n1progressTone.stop();
            n1progressTone.release();
            n1progressTone = null;
        }
    }

    private static AudioTrack createProgressTone(Context context) throws IOException {
        final int sampleRate = 16000;

        AssetFileDescriptor fd = context.getResources().openRawResourceFd(R.raw.progress_tone);

        int length = (int) fd.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[(int) length];

        readFileToBytes(fd, data);

        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;

    }

    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;

        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (int) (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }
}
