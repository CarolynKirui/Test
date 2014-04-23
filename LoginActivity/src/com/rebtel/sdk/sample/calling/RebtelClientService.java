package com.rebtel.sdk.sample.calling;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rebtel.sdk.android.Call;
import com.rebtel.sdk.android.ClientListener;
import com.rebtel.sdk.android.ClientRegistration;
import com.rebtel.sdk.android.RebtelClient;
import com.rebtel.sdk.android.RebtelError;
import com.rebtel.sdk.android.RebtelSDK;

public class RebtelClientService {

    static final String LOG_TAG = RebtelClientService.class.getSimpleName();

    private RebtelClient rebtelClient = null;

    private Context context;

    public void start(Context context, String userName) {
        this.context = context.getApplicationContext();
        rebtelClient = RebtelSDK.getRebtelClient(context, userName, "enter-application-key",
                "enter-application-secret", "sdksandbox.rebtel.com");

        rebtelClient.addClientListener(new RebtelClientListener());
        rebtelClient.start();
    }

    public void stop() {
        rebtelClient.stop();
    }

    public boolean isStarted() {
        return rebtelClient.isStarted();
    }

    public RebtelClient getRebtelClient() {
        return rebtelClient;
    }

    private class RebtelClientListener implements ClientListener {

        @Override
        public void onClientFailed(RebtelClient client, RebtelError error) {
            Log.e(LOG_TAG, "RebtelClient error: " + error);
        }

        @Override
        public void onClientStarted(RebtelClient client) {
            Log.d(LOG_TAG, "RebtelClient started");
            client.startListeningOnActiveConnection();
        }

        @Override
        public void onClientStopped(RebtelClient client) {
            Log.d(LOG_TAG, "RebtelClient stoped");
        }

        @Override
        public void onIncomingCall(RebtelClient client, Call call) {
            Log.d(LOG_TAG, "Incoming call");
            CurrentCall.currentCall = call;
            Intent intent = new Intent(context, IncomingCallScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        @Override
        public void onLogMessage(int level, String area, String message) {

            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    return;
                case Log.ERROR:
                    Log.e(area, message);
                    return;
                case Log.INFO:
                    Log.i(area, message);
                    return;
                case Log.VERBOSE:
                    Log.v(area, message);
                    return;
                case Log.WARN:
                    Log.w(area, message);
                    return;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(RebtelClient client, ClientRegistration clientRegistration) {
        }

    }
}
