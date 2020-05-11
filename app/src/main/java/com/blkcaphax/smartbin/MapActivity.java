package com.blkcaphax.smartbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private WaitDialog waitDialog;
    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        waitDialog = new WaitDialog(this);
        waitDialog.showLoadindDialog();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapActivity.this);
        //fetchLocation();
        this.getSupportActionBar().setTitle("SmartBin | Map");
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap gmap = googleMap;
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
                MapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        CustomMarker customMarker = null;
                        while (i < finalJaArray.length()){
                            try {
                                JSONObject binJo = new JSONObject(finalJaArray.get(i).toString());
                                customMarker = new CustomMarker(binJo);
                                gmap.addMarker(customMarker.getMarkerOptions());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                        //gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), 18.0f));
                        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(customMarker.getLocation(), 15.0f));
                        MapActivity.this.waitDialog.hideLoadingDialog();
                    }
                });
            }
        }).start();


    }
}

class CustomMarker{
    private MarkerOptions markerOptions;
    private LatLng location;
    private int binId;
    private int binlevel;
    CustomMarker(JSONObject jo){
        this.markerOptions = new MarkerOptions();

        String[] loc;
        try {
            loc = ((String) jo.get("binlocation")).split(",");
            this.binId = Integer.parseInt((String) jo.get("binid"));
            this.binlevel = Integer.parseInt((String) jo.get("binlevel"));
            if(this.binlevel <90) {
                this.markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.grid_bin_ic));
            }else{
                this.markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.grid_bin_red_ic));
            }
            this.location = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.markerOptions.position(this.location);
        this.markerOptions.title("Bin Id: "+this.binId+" | Bin Level:"+this.binlevel+"%");
    }

    MarkerOptions getMarkerOptions(){
        return  this.markerOptions;
    }

    int getBinId(){
        return this.binId;
    }

    int getBinlevel(){
        return this.binlevel;
    }

    LatLng getLocation(){
        return this.location;
    }
}
