package fr.madz.speedompteur;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements LocationListener {

    TextView tv_vitesse;
    ProgressBar progressBar;
    LocationManager locationManager;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // appele des permissions necessaire
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},100);
        } else {
            //si les acces sont pas ok un toast apparait pour demander les permissions
            doStuff();
        }
        this.onLocationChanged(null);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        progressBar = this.findViewById(R.id.progressBar);
    }

    @Override
    public void onLocationChanged(Location location) {
        float speed;
        int int_speed;

        if (location!=null){

            speed = location.getSpeed();
            speed = speed*3600/1000;

            int_speed = (int) speed;

            String strDouble = String.format("%.0f", speed);

            tv_vitesse = this.findViewById(R.id.tv_vitesse);
            tv_vitesse.setText(strDouble);

            progressBar.setProgress(int_speed);
        }
    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        Toast.makeText(this,"Waiting GPS Connection!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doStuff();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager = null;
    }

    @Override
    protected void onStop() {
        abortLocationFetching();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        locationManager = null;
        abortLocationFetching();
        super.onDestroy();
    }

    public void abortLocationFetching() {
        // Remove the listener you previously added
        if (locationManager != null && locationRequest != null) {
                locationManager.removeUpdates(this);
                locationManager = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            createHorizontalalLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            createVerticalLayout();
        }
    }

    public void createHorizontalalLayout(){
        setContentView(R.layout.activity_main_l);
    }
    public void createVerticalLayout(){
        setContentView(R.layout.activity_main);
    }

}