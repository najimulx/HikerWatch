package com.example.dell.hikerwatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    boolean flagCredit=false;
    TextView credit;
    Location globalLocation;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        credit=(TextView)findViewById(R.id.textView2);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                globalLocation=location;
                try {
                    updateLocation(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);



        if(Build.VERSION.SDK_INT<23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,0,locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,0,locationListener);
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(loc!=null){
                    try {
                        updateLocation(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    public void onRequestPermissionResult(int requestcode,String[] permission,int[]grantResult){
        super.onRequestPermissionsResult(requestcode,permission,grantResult);
        if(grantResult.length>0&&grantResult[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,locationListener);
            }
        }
    }

    public void updateLocation(Location location) throws IOException {
        Log.i("Location",location.toString());

        TextView textViewLat = (TextView)findViewById(R.id.textViewLatitude);
        TextView textViewLong = (TextView)findViewById(R.id.textViewLongitude);
        TextView textViewAlt = (TextView)findViewById(R.id.textViewAltitude);
        TextView textViewAcc = (TextView)findViewById(R.id.textViewAccuracy);
        TextView textViewAdd = (TextView)findViewById(R.id.textViewLocality);

        textViewLat.setText("Latitude : "+location.getLatitude());
        textViewLong.setText("Longitude : "+location.getLongitude());
        textViewAlt.setText("Altitude : "+location.getAltitude());
        textViewAcc.setText("Accuracy : "+location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
        try {
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (listAddress!=null && listAddress.size()>0){
                Log.i("PlaceInfo",listAddress.get(0).toString());
                String throughfare="";
                String locality="";
                String admin="";
                String pin="";
                String country="";
                if(listAddress.get(0).getThoroughfare()!=null){
                    throughfare=listAddress.get(0).getThoroughfare();
                }
                if(listAddress.get(0).getLocality()!=null){
                    locality=listAddress.get(0).getLocality();
                }
                if(listAddress.get(0).getPostalCode()!=null){
                    pin=listAddress.get(0).getPostalCode();
                }
                if(listAddress.get(0).getAdminArea()!=null){
                    admin = listAddress.get(0).getAdminArea();
                }
                if(listAddress.get(0).getCountryName()!=null){
                    country= listAddress.get(0).getCountryName();
                }

                textViewAdd.setText("Address :\n"+throughfare+"\n"+locality+"  "+pin+"\n"+admin+"  "+country);
            }else{
                Log.i("PlaceInfo","NotFound");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void showCredit(View view){
        if (flagCredit==false){
            credit.setVisibility(View.VISIBLE);
            flagCredit=true;
        }else {
            credit.setVisibility(View.INVISIBLE);
            flagCredit=false;
        }

    }


}
