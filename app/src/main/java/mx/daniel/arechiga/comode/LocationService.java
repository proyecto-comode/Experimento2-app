package mx.daniel.arechiga.comode;

import android.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Macbook on 22/11/16.
 */

public class LocationService extends Service {

    Double latitud = 0.0, longitud = 0.0;
    String localizacion = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Obteniendo la ubicacion por network para posteriormente cuando se desee poder obtener la localizacion...
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion local = new Localizacion();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, (LocationListener) local);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, (LocationListener) local);
        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(loc != null){
            latitud = loc.getLatitude();
            longitud = loc.getLongitude();
        }else{
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null){
                latitud = loc.getLatitude();
                longitud = loc.getLongitude();
            }
        }
    }

    //Clase listener para escuchar los cambios de localizacion y actualizar la localizacion...
    public class Localizacion implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {

            longitud = location.getLongitude();
            latitud = location.getLatitude();
            setlocalizacion();
            new EnviarActualizacion().execute();

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
    }

    //Metodo para guardar la localizacion del dispositivo...
    public void setlocalizacion(){
        // Elemento geocoder en el contexto
        Geocoder geocoder = new Geocoder(this);
        // Elemento list que contendra la direccion
        List<Address> direcciones = null;

        // Funcion para obtener coger el nombre desde el geocoder
        try {
            direcciones = geocoder.getFromLocation(latitud, longitud,1);
        } catch (Exception e) {
            Log.d("Error", "Error en geocoder:"+e.toString());
        }

        // Funcion que determina si se obtuvo resultado o no
        if(direcciones != null && direcciones.size() > 0 ) {

            // Creamos el objeto address
            Address direccion = direcciones.get(0);

            // Creamos el string a partir del elemento direccion..
            localizacion = String.format("%s,%s,%s", direccion.getLocality(), direccion.getAdminArea(), direccion.getCountryName());
        }
    }

    //Ejecucion de operacion Enviar Preferencias en un hilo separado de la interfaz del usuario....
    private class EnviarActualizacion extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            //Obteniendo el token del dispositivo....
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token","No se guardo el token del dispositivo");
            String loc = localizacion;

            //Enviando los datos para crear la ontologia del dispositivo....
            String url_servidor = "http://148.202.119.34:8080/Webservice_COMoDE/Ontologia_Dispositivo";
            HttpClient cliente = new DefaultHttpClient();
            HttpPost post = new HttpPost(url_servidor);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
            postParameters.add(new BasicNameValuePair("cond","actualizacion"));
            postParameters.add(new BasicNameValuePair("token",token));
            postParameters.add(new BasicNameValuePair("loc",loc));
            try {
                post.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));
                cliente.execute(post);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Preferencias Guardadas";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Nada jajjajaja
        }
    }

}
