package com.blkcaphax.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class DashActivity extends AppCompatActivity {
    private ImageButton btnMap;
    private ImageButton btnAlert;
    private ImageButton btnList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        btnMap = findViewById(R.id.dash_btn_map);
        btnAlert = findViewById(R.id.dash_btn_alert);
//        btnList = findViewById(R.id.dash_btn_bin);
        attachListener();
        this.getSupportActionBar().setTitle("SmartBin | Dashboard");
    }

    private void attachListener(){
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        WebConnection webConnection = new WebConnection();
//                        String respone;
//                        try {
//                            respone = webConnection.send("action=getBins");
//                            JSONArray jsonArray = new JSONArray(respone);
//                            Log.d("res", "run: "+jsonArray.get(0));;
//                            Log.d("res", "run: "+jsonArray.get(1));;
//                        } catch (IOException | JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alertActivity = new Intent(v.getContext(), AlertActivity.class);
                startActivity(alertActivity);
            }
        });

//        btnList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent binListActivity = new Intent(v.getContext(), BinListActivity.class);
//                startActivity(binListActivity);
//            }
//        });
    }
}
