package com.blkcaphax.smartbin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    WaitDialog waitDialog;
    private String key="";
    EditText editTextUsername;
    EditText editTextPassword;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnLogin);
        this.waitDialog = new WaitDialog(this);
        this.waitDialog.showLoadindDialog();
        this.editTextUsername = (EditText) findViewById(R.id.username);
        this.editTextPassword = (EditText) findViewById(R.id.password);
        this.attachBtnListener();
        try {
            InputStream inputStream = openFileInput("key.json");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            JSONObject jo = new JSONObject(line);
            this.key = jo.getString("key");
            Log.d("res", "key: "+this.key);
        } catch (IOException | JSONException e) {
            MainActivity.this.waitDialog.hideLoadingDialog();
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebConnection webConnection = new WebConnection();
                try {
                    String res = webConnection.send("key="+key);
                    Log.d("res", "res: "+res);
                    if(res.equals("OK")){
                        MainActivity.this.waitDialog.hideLoadingDialog();
                        Intent dashActivity = new Intent(MainActivity.this, DashActivity.class);
                        dashActivity.putExtra("key",MainActivity.this.key);
                        startActivity(dashActivity);
                    }else{
                        MainActivity.this.waitDialog.hideLoadingDialog();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void attachBtnListener(){

        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = MainActivity.this.editTextUsername.getText().toString();
                final String password = MainActivity.this.editTextPassword.getText().toString();
                final WaitDialog dialog = new WaitDialog(MainActivity.this);
                //dialog.showLoadindDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebConnection webConnection = new WebConnection();
                        try {
                            String res = webConnection.send("username="+username+"&"+"password="+password);
                            if(res.equals("-1")){
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,"Invalid username or password.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //dialog.hideLoadingDialog();
                                return;
                            }
                            MainActivity.this.key = res;
                            JSONObject jo = new JSONObject();
                            jo.put("key",MainActivity.this.key);
                            MainActivity.this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(MainActivity.this.openFileOutput("key.json", Context.MODE_PRIVATE)));
                            bufferedWriter.write(jo.toString());
                            bufferedWriter.close();
                            //dialog.hideLoadingDialog();
                            Intent dashActivity = new Intent(MainActivity.this, DashActivity.class);
                            dashActivity.putExtra("key",MainActivity.this.key);
                            startActivity(dashActivity);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

    }
}
