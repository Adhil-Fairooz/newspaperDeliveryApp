package com.example.miniproject_e2046314;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText ETUsername, ETPassword;
    TextView TVUserErrMsg,TVPassErrMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ETUsername = (EditText) findViewById(R.id.etUsername);
        ETPassword = (EditText) findViewById(R.id.etPassword);
        TVUserErrMsg = (TextView) findViewById(R.id.tvUsernameError);
        TVPassErrMsg = (TextView) findViewById(R.id.tvPasswordError);
    }

    public void login(View view) {
        String Username = ETUsername.getText().toString();
        String Password = ETPassword.getText().toString();
        Log.d("MainActivity", Username+","+Password);

        new ServerSignIn(this,TVUserErrMsg,TVPassErrMsg).execute(Username, Password);

    }
    public void clearFields(View v){
        ETUsername.setText("");
        ETPassword.setText("");
        TVUserErrMsg.setText("");
        TVPassErrMsg.setText("");
    }

}