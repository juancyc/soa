package com.example.smartender;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public class WeatherHandler implements LocationListener {

    private Context context;
    private LocationManager mLocationManager;

    public WeatherHandler(Context context){
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public String getWeatherDaysData(){
        //Es casi igual a la que esta en MainFragment solo que esta la uso para poder acceder desde EventFragment
        //Esta devuelve el nombre de la ciudad
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try{
                double lat = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                double lon = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                String name = getLocationName(lat,lon);
                if(name.length() == 0){
                    return getLocationName(-34.6699075563091,-58.56386728584767);
                }
                return name;
            }catch (Exception e){
                double lat = -34.6699075563091;
                double lon = -58.56386728584767;
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                return getLocationName(lat,lon);
            }
        }
        return "";
    }

    private String getLocationName(double latitude, double longitude){
        if(latitude != 0.0 && longitude != 0.0){
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    String lugar = address.getLocality() + "," + address.getCountryName();
                    if(lugar.length() != 0 && !lugar.contains("null")){
                        String aux = Normalizer.normalize(lugar,Normalizer.Form.NFD);
                        aux = aux.replaceAll("[^\\p{ASCII}]", "");
                        return aux;
                    }else {
                        return "";
                    }
                }
            }catch (IOException e) {
                Toast.makeText(context,"No se puede obtener datos del clima",Toast.LENGTH_SHORT);
                return "";
            }

        }
        return "";
    }

    @Override
    public void onLocationChanged(Location location) {
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

    public Context getContext() {
        return context;
    }

    public static boolean isWheatherOK(int temp,int hum){
        if(temp >10 && hum <60)
            return true;
        return false;
    }

    public static String changeWheatherName(String data){
        if(data.equals("clear sky"))
            return "cielo despejado";

        if(data.equals("few clouds"))
            return "pocas nubes";

        if(data.equals("scattered clouds"))
            return "nubes dispersas";

        if(data.equals("broken clouds") || data.equals("clouds") || data.equals("overcast clouds"))
            return "nublado";

        if(data.equals("shower rain"))
            return "aguacero";

        if(data.equals("rain"))
            return "lluvia";

        if(data.equals("thunderstorm"))
            return "tormenta";

        if(data.equals("snow"))
            return "nevada";

        if(data.equals("light thunderstorm"))
            return "tormenta electrica";

        if(data.equals("drizzle"))
            return "llovisna";

        if(data.equals("smoke"))
            return "neblina";

        return data;
    }
}
