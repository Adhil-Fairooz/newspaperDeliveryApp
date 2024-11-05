package com.example.miniproject_e2046314;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ChangeUserPassword extends AppCompatActivity {
    private static final String TAG = ChangeUserPassword.class.getSimpleName();

    TextView tvCurrentPassErr,tvNewPassErr,tvReNewPassErr,tvGeneralErr;
    EditText etCurrentPass,etNewpass,etReNewPass;
    Button btnChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_password);
        tvCurrentPassErr = (TextView) findViewById(R.id.tvCurrentPasswordError);
        tvNewPassErr = (TextView) findViewById(R.id.tvNewPasswordError);
        tvReNewPassErr = (TextView) findViewById(R.id.tvRepeatNewPasswordError);
        tvGeneralErr = (TextView) findViewById(R.id.tvGeneralError);

        etCurrentPass = (EditText) findViewById(R.id.etCurrentPassword);
        etNewpass = (EditText) findViewById(R.id.etNewPassword);
        etReNewPass = (EditText) findViewById(R.id.etRepeatNewPassword);

        btnChange = findViewById(R.id.btnChangePassword);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTV();
                String currentPass = etCurrentPass.getText().toString();
                String newPass = etNewpass.getText().toString();
                String reNewPass = etReNewPass.getText().toString();
                if(newPass.equals(reNewPass)){
                    if(currentPass.equals(ServerSignIn.user_pass)){
                        ChangePassAsynTask changePasswordTask = new ChangePassAsynTask();
                        changePasswordTask.execute(newPass);
                    }else{
                        tvCurrentPassErr.setText("Incorrect Current Password");
                    }
                }else{
                    tvNewPassErr.setText("Password does not match");
                    tvReNewPassErr.setText("Password does not match");
                }

            }
        });
    }

    private class ChangePassAsynTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... args) {
            boolean success = false;
            try{
                String NewPassword = args[0];
                String link="http://"+ServerSignIn.localhost+"/MyProjects/MiniProjectServerFiles/changePass.php";
                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(NewPassword, "UTF-8");
                data+= "&" + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(ServerSignIn.UserID, "UTF-8");

                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write(data);
                writer.flush();
                writer.close();
                outputStream.close();


                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);

                    break;
                }
                return sb.toString();

            }
            catch(Exception ex){
                Log.d("change", "doInBackground Change Pass: ");
                return ex.getMessage().toString();
            }
        }
        @Override
        protected void onPostExecute(String result){
            tvGeneralErr.setText(result);
        }
    }
    private void resetTV(){
        tvCurrentPassErr.setText("");
        tvNewPassErr.setText("");
        tvGeneralErr.setText("");
        tvReNewPassErr.setText("");
    }
}