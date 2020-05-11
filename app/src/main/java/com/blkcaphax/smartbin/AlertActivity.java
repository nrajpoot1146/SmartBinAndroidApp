package com.blkcaphax.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AlertActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        linearLayout = (LinearLayout) findViewById(R.id.ln);
        this.getSupportActionBar().setTitle("SmartBin | Notification");

        new Thread(new Runnable() {
            @Override
            public void run() {
                WebConnection webConnection = new WebConnection();
                String response;
                JSONArray jsonArray = null;
                try {
                    response = webConnection.send("action=getBins");
                    jsonArray = new JSONArray(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                final JSONArray finalJaArray = jsonArray;

                AlertActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        CustomMarker customMarker = null;
                        while (i < finalJaArray.length()){
                            try {
                                JSONObject binJo = new JSONObject(finalJaArray.get(i).toString());
                                int binId = Integer.parseInt((String) binJo.get("binid"));
                                int binlevel = Integer.parseInt((String) binJo.get("binlevel"));
                                if(binlevel >= 90) {
                                    TextView textView = new TextView(AlertActivity.this);
                                    textView.setTextColor(Color.BLACK);
                                    textView.setTextSize(20);
                                    textView.setText("Alert! the bin with BinID:"+ binId +" has reached it's maximum limit, needs to be serviced soon.");
                                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                                    AlertActivity.this.linearLayout.addView(textView);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                    }
                });
            }
        }).start();

    }
}
