package com.rebtel.sdk.sample.calling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rebtel.sdk.android.Call;

public class MakeCallActivity extends Activity {

    static final String LOG_TAG = MakeCallActivity.class.getSimpleName();

    private RebtelClientService rebtelClientService;

    private EditText callName;

    private TextView loggedInName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(buttonClickListener);
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(buttonClickListener);

        callName = (EditText) findViewById(R.id.call_name);
        loggedInName = (TextView) findViewById(R.id.logged_in_name);

        String userName = getIntent().getStringExtra("REBTEL_USERNAME");

        loggedInName.setText(userName);

        rebtelClientService = new RebtelClientService();
        rebtelClientService.start(this, userName);
    }

    @Override
    public void onDestroy() {
        if (rebtelClientService.isStarted()) {
            rebtelClientService.stop();
        }

        super.onDestroy();
    }

    private void stopButtonClicked() {
        if (rebtelClientService.isStarted()) {
            rebtelClientService.stop();
        }

        finish();
    }

    private void callButtonClicked() {
        String userName = callName.getText().toString();

        if (userName.length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter an alias to call", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Call call = rebtelClientService.getRebtelClient().call(userName);
        CurrentCall.currentCall = call;
        startActivity(new Intent(this, CallScreenActivity.class));
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.call_button:
                    callButtonClicked();
                    break;

                case R.id.stop_button:
                    stopButtonClicked();
                    break;

            }
        }
    };

}
