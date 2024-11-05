package com.example.miniproject_e2046314;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ServerSignIn extends AsyncTask<String,String,String> {
    public static final String localhost = "192.168.8.103";
    public static String UserID;
    public static String user_Name;
    public static String user_pass;
    private TextView msg,psmg;
    private Context context;
    private String outputuser,outputPass;

    public ServerSignIn(MainActivity context, TextView msg,TextView psmg){
        this.context = (Context) context;
        this.msg = msg;
        this.psmg = psmg;

    }
    private void resetTV(){
        this.msg.setText("");
        this.psmg.setText("");
    }
    @Override
    protected String doInBackground(String... arg0) {
        try {
            String username = (String) arg0[0];
            String password = (String) arg0[1];
            Log.d("serverSignin", username+","+password);

            String link="http://"+localhost+"/XamppProjects/MiniProjectServerFiles/MiniProjectServerFiles/login.php";
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data+= "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response

            while((line = reader.readLine()) != null) {
                sb.append(line);

                break;
            }
            String[] responseText = null;
            if(!sb.toString().equalsIgnoreCase("error")){
                responseText = sb.toString().split(",");

                user_pass = responseText[0];
                UserID = responseText[1];
                user_Name = responseText[2];
            }

            if(sb.toString().equalsIgnoreCase("error")){
                this.outputuser = "Incorrect Username Entered";
                this.outputPass = "";
            }else{
                if(responseText[0].equals(password)){
                    this.outputuser = "Login Success";
                    this.outputPass = "";

                    Intent i = new Intent(this.context,Home.class);
                    context.startActivity(i);
                }else{
                    this.outputPass = "Incorrect Password";
                    this.outputuser = "";
                }
            }
            Log.d("ServerSignIn", sb.toString());
            return sb.toString();

        }catch (Exception ex){
            outputuser = ex.getMessage();
            return new String("Exception: " + ex.getMessage());
        }
    }
    @Override
    protected void onPostExecute(String result){
        this.resetTV();

        this.msg.setText(this.outputuser);
        this.psmg.setText(this.outputPass);
    }
}
