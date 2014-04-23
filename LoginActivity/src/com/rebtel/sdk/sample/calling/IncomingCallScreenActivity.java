package com.rebtel.sdk.sample.calling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rebtel.sdk.android.Call;
import com.rebtel.sdk.android.CallEndCause;
import com.rebtel.sdk.android.CallListener;
import com.rebtel.sdk.android.PushPair;

import java.util.List;

public class IncomingCallScreenActivity extends Activity {

    static final String LOG_TAG = IncomingCallScreenActivity.class.getSimpleName();

    private Call call;

    private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incoming);

        call = CurrentCall.currentCall;

        call.addCallListener(new RebtelCallListener());

        Button answer = (Button) findViewById(R.id.answer_button);
        Button decline = (Button) findViewById(R.id.decline_button);

        answer.setOnClickListener(incomingClickListener);
        decline.setOnClickListener(incomingClickListener);

        TextView callerName = (TextView) findViewById(R.id.remoteUser);
        callerName.setText(call.getRemoteUserId());

        audioPlayer = new AudioPlayer(this);
        audioPlayer.playRingtone();
    }

    private void answerClicked() {
        audioPlayer.stopRingtone();
        call.answer();
        startActivity(new Intent(this, CallScreenActivity.class));
    }

    private void declineClicked() {
        audioPlayer.stopRingtone();
        call.hangup();
        finish();
    }

    private class RebtelCallListener implements CallListener {

        @Override
        public void onCallAnswered(Call call) {
            Log.d(LOG_TAG, "Call answered");
        }

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();

            Log.d(LOG_TAG, "Call ended, cause: " + cause.toString());

            audioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(LOG_TAG, "Call established");
        }

        @Override
        public void onCallReceivedOnRemoteEnd(Call call) {
            Log.d(LOG_TAG, "Remote end started ringing");

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            Log.d(LOG_TAG, "Whatever");
        }
    }

    private OnClickListener incomingClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.answer_button:
                    answerClicked();
                    break;

                case R.id.decline_button:
                    declineClicked();
                    break;
            }

        }
    };
}
