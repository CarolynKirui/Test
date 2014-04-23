package com.rebtel.sdk.sample.calling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText loginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(loginButtonClickListener);

        loginName = (EditText) findViewById(R.id.login_name);
    }

    private void loginClicked() {

        String userName = loginName.getText().toString();

        if (userName.length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Intent intent = new Intent(this, MakeCallActivity.class);
        intent.putExtra("REBTEL_USERNAME", userName);

        startActivity(intent);
    }

    private OnClickListener loginButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            loginClicked();
        }
    };

}
