package com.rebtel.sdk.sample.calling;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rebtel.sdk.android.Call;
import com.rebtel.sdk.android.CallEndCause;
import com.rebtel.sdk.android.CallListener;
import com.rebtel.sdk.android.PushPair;
import com.rebtel.sdk.android.RebtelError;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends Activity {

    static final String LOG_TAG = CallScreenActivity.class.getSimpleName();

    private Call call;

    private TextView callState;

    private long mcallStartNew;

    private AudioPlayer audioPlayer;

    private Timer timer;

    private MyTask durationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        call = CurrentCall.currentCall;
        call.addCallListener(new RebtelCallListener());

        TextView callerName = (TextView) findViewById(R.id.remoteUser);
        callerName.setText(call.getRemoteUserId());
        callState = (TextView) findViewById(R.id.callState);
        callState.setText(call.getState().toString());

        Button endCall = (Button) findViewById(R.id.hangup_button);
        endCall.setOnClickListener(endButtonClickListener);

        mcallStartNew = System.currentTimeMillis();

        audioPlayer = new AudioPlayer(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        durationTask.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        timer = new Timer();
        durationTask = new MyTask();
        timer.schedule(durationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {
        // Nope, hang up instead!
    }

    private void endCall() {
        call.hangup();
        finish();
    }

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateCallDurationNew();
                }
            });
        }
    }

    ;

    private void updateCallDurationNew() {
        TextView callDuration = (TextView) findViewById(R.id.call_duration);
        long timespan = System.currentTimeMillis() - mcallStartNew;
        callDuration.setText(formatTimespan(timespan));
    }

    private CharSequence formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private class RebtelCallListener implements CallListener {

        @Override
        public void onCallAnswered(Call call) {
            audioPlayer.stopProgressTone();
            Log.d(LOG_TAG, "Call answered");
        }

        @Override
        public void onCallEnded(Call call) {
            audioPlayer.stopProgressTone();
            CallEndCause cause = call.getDetails().getEndCause();
            RebtelError error = call.getDetails().getError();

            Log.d(LOG_TAG, "Call ended, cause: " + cause.toString());

            if (error.getMessage().length() > 0) {
                Toast toast = Toast.makeText(CallScreenActivity.this, error.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(LOG_TAG, "Call established");

            audioPlayer.stopProgressTone();

            callState.setText(call.getState().toString());
        }

        @Override
        public void onCallReceivedOnRemoteEnd(Call call) {
            Log.d(LOG_TAG, "Remote end started ringing");

            audioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            Log.d(LOG_TAG, "Should send push");
        }

    }

    private OnClickListener endButtonClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            audioPlayer.stopProgressTone();
            endCall();
        }
    };
}
