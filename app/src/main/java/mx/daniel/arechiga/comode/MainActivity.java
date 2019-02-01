package mx.daniel.arechiga.comode;


import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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


public class MainActivity extends AppCompatActivity {

    //Declarando las variables para todos los checkbox...
    CheckBox juegosmesa, cine, museo, literatura, videojuegos, rest, picnic, parque,
            deporte, turismo, arte, caminar, esp, ingles, salud, computacion, tecnologia,
            naturaleza, sociales, humanidades, economicas, administrativas, artes, exactas;

    String localizacion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Instanciando los chekbox con su referencia al objeto...

        //Actividades de interior...
        juegosmesa = (CheckBox) findViewById(R.id.cb_juegos_mesa);
        cine = (CheckBox) findViewById(R.id.cb_cine);
        museo = (CheckBox) findViewById(R.id.cb_museo);
        literatura = (CheckBox) findViewById(R.id.cb_literatura);
        videojuegos = (CheckBox) findViewById(R.id.cb_video_juegos);
        rest = (CheckBox) findViewById(R.id.cb_restaurante);

        //Actividades de exterior...
        picnic = (CheckBox) findViewById(R.id.cb_picnic);
        parque = (CheckBox) findViewById(R.id.bc_parque);
        deporte = (CheckBox) findViewById(R.id.cb_deporte);
        turismo = (CheckBox) findViewById(R.id.cb_turismo);
        arte = (CheckBox) findViewById(R.id.bc_arte);
        caminar = (CheckBox) findViewById(R.id.cb_caminar);

        //Lenguajes preferidos...
        esp = (CheckBox) findViewById(R.id.cb_espaniol);
        ingles = (CheckBox) findViewById(R.id.cb_ingles);

        //Areas de interes...
        salud = (CheckBox) findViewById(R.id.cb_salud);
        computacion = (CheckBox) findViewById(R.id.cb_computacion);
        tecnologia = (CheckBox) findViewById(R.id.cb_tecnologia);
        naturaleza = (CheckBox) findViewById(R.id.cb_naturaleza);
        sociales = (CheckBox) findViewById(R.id.cb_sociales);
        humanidades = (CheckBox) findViewById(R.id.cb_humanidades);
        economicas = (CheckBox) findViewById(R.id.cb_Economicas);
        administrativas = (CheckBox) findViewById(R.id.cb_adminsitrativas);
        artes = (CheckBox) findViewById(R.id.cb_artes);
        exactas = (CheckBox) findViewById(R.id.cb_exactas);

        //Cargando las preferencias de los los chekbox que ya habia seleccionado anteriormente...
        cargarpreferencias();
        Intent intent = new Intent(MainActivity.this,LocationService.class);
        startService(intent);

    }

    //Metodo para verificar lo que se va a enviar...
    public void EnviarInformacion(View view){

        //Metodo para verificar si tiene conexion a internet....
        Metodos metodos = new Metodos();
        Boolean conexion = metodos.isOnline(getApplicationContext());
        if (!conexion) {
            Toast.makeText(getApplicationContext(),"Para enviar tus preferencias y poder recibir las notificaciones necesitas de conexion a internet",Toast.LENGTH_SHORT).show();
        }else{
            guardarpreferencias();
            new EnviarPreferencias().execute();
        }
    }

    //Ejecucion de operacion Enviar Preferencias en un hilo separado de la interfaz del usuario....
    private class EnviarPreferencias extends AsyncTask<String, Void, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("   Cargando");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            //Obteniendo el token del dispositivo....
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token","No se guardo el token del dispositivo");
            String loc = localizacion;
            String caract_dis = getcaracteristicas_dispositivo();
            String act_interior = getactividades_interior();
            String act_exterior = getactividades_exterior();
            String leng_pref = getlenguajes_preferidos();
            String areas_interes = getareas_interes();

            //Enviando los datos para crear la ontologia del dispositivo....
            String url_servidor = "http://148.202.119.34:8080/Webservice_COMoDE/Ontologia_Dispositivo";
            HttpClient cliente = new DefaultHttpClient();
            HttpPost post = new HttpPost(url_servidor);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
            postParameters.add(new BasicNameValuePair("cond","instancia"));
            postParameters.add(new BasicNameValuePair("token",token));
            postParameters.add(new BasicNameValuePair("loc",loc));
            postParameters.add(new BasicNameValuePair("carac",caract_dis));
            postParameters.add(new BasicNameValuePair("in",act_interior));
            postParameters.add(new BasicNameValuePair("out",act_exterior));
            postParameters.add(new BasicNameValuePair("leng",leng_pref));
            postParameters.add(new BasicNameValuePair("areas",areas_interes));
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
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para obtener las actividades de interior preferentes del dispositivo...
    public String getactividades_interior() {
        String cadena = "";
        if (juegosmesa.isChecked()){
            cadena = "Juegos de Mesa" + ",";
        }
        if (cine.isChecked()){
            cadena = cadena + "Cine" + ",";
        }
        if (museo.isChecked()){
            cadena = cadena + "Museo" + ",";
        }
        if (literatura.isChecked()){
            cadena = cadena + "Literatura" + ",";
        }
        if (videojuegos.isChecked()){
            cadena = cadena + "Video Juegos" + ",";
        }
        if (rest.isChecked()){
            cadena = cadena + "Restaurante"  + ",";
        }

        return cadena;
    }

    //Metodo para obtener las actividades de exterior preferentes del dispositivo...
    public String getactividades_exterior() {
        String cadena = "";
        if (picnic.isChecked()){
            cadena = "Picnic" + ",";
        }
        if (parque.isChecked()){
            cadena = cadena + "Parque" + ",";
        }
        if (deporte.isChecked()){
            cadena = cadena + "Deporte" + ",";
        }
        if (turismo.isChecked()){
            cadena = cadena + "Turismo" + ",";
        }
        if (arte.isChecked()){
            cadena = cadena + "Arte" + ",";
        }
        if (caminar.isChecked()){
            cadena = cadena + "Caminar" + ",";
        }

        return cadena;
    }

    //Metodo para obtener los lenguagues preferentes del dispositivo...
    public String getlenguajes_preferidos() {
        String cadena = "";
        if (esp.isChecked()){
            cadena = "Español" + ",";
        }
        if (ingles.isChecked()){
            cadena = cadena + "Ingles" + ",";
        }

        return cadena;
    }

    //Metodo para obtener los sitios web preferentes del dispositivo...
    public String getareas_interes() {
        String cadena = "";
        if (salud.isChecked()){
            cadena = "Salud" + ",";
        }
        if (computacion.isChecked()){
            cadena = cadena + "Computacion" + ",";
        }
        if (tecnologia.isChecked()){
            cadena = cadena + "Tecnologia" + ",";
        }
        if (naturaleza.isChecked()){
            cadena = cadena + "Naturaleza" + ",";
        }
        if (sociales.isChecked()){
            cadena = cadena + "Sociales" + ",";
        }
        if (humanidades.isChecked()){
            cadena = cadena + "Humanidades" + ",";
        }
        if (economicas.isChecked()){
            cadena = cadena + "Economicas" + ",";
        }
        if (administrativas.isChecked()){
            cadena = cadena + "Administrativas" + ",";
        }
        if (artes.isChecked()){
            cadena = cadena + "Artes" + ",";
        }
        if (exactas.isChecked()){
            cadena = cadena + "Exactas" + ",";
        }

        return cadena;
    }

    //Metodo para obtener las caracterisitcas del dispositivo...
    public String getcaracteristicas_dispositivo(){
        String cadena = "";

        cadena = Build.VERSION.RELEASE +","
                +Build.BOARD +","
                +Build.DISPLAY +","
                +Build.HARDWARE +","
                +Build.MANUFACTURER +","
                +Build.MODEL;

        return cadena;
    }

    //Metodo para cargar las preferencias en caso de que las halla con anterioridad...
    public void cargarpreferencias(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        String sjuegosmesa = sharedPreferences.getString("juegosmesa","nada");
        String scine = sharedPreferences.getString("cine","nada");
        String smuseo = sharedPreferences.getString("museo","nada");
        String sliteratura = sharedPreferences.getString("literatura","nada");
        String svideojuegos = sharedPreferences.getString("videojuegos","nada");
        String srest = sharedPreferences.getString("rest","nada");
        String spicnic = sharedPreferences.getString("picnic","nada");
        String sparque = sharedPreferences.getString("parque","nada");
        String sdeporte = sharedPreferences.getString("deporte","nada");
        String sturismo = sharedPreferences.getString("turismo","nada");
        String sarte = sharedPreferences.getString("arte","nada");
        String scaminar = sharedPreferences.getString("caminar","nada");
        String sesp = sharedPreferences.getString("esp","nada");
        String singles = sharedPreferences.getString("ingles","nada");
        String ssalud = sharedPreferences.getString("salud","nada");
        String scomputacion = sharedPreferences.getString("computacion","nada");
        String stecnologia = sharedPreferences.getString("tecnologia","nada");
        String snaturaleza = sharedPreferences.getString("naturaleza","nada");
        String ssociales = sharedPreferences.getString("sociales","nada");
        String shumanidades = sharedPreferences.getString("humanidades","nada");
        String seconomicas = sharedPreferences.getString("economicas","nada");
        String sadministrativas = sharedPreferences.getString("administrativas","nada");
        String sartes = sharedPreferences.getString("artes","nada");
        String sexactas = sharedPreferences.getString("exactas","nada");

        if (sjuegosmesa.equals("true")){
            juegosmesa.setChecked(true);
        }else{
            juegosmesa.setChecked(false);
        }
        if (scine.equals("true")){
            cine.setChecked(true);
        }else{
            cine.setChecked(false);
        }
        if (smuseo.equals("true")){
            museo.setChecked(true);
        }else{
            museo.setChecked(false);
        }
        if (sliteratura.equals("true")){
            literatura.setChecked(true);
        }else{
            literatura.setChecked(false);
        }
        if (svideojuegos.equals("true")){
            videojuegos.setChecked(true);
        }else{
            videojuegos.setChecked(false);
        }
        if (srest.equals("true")){
            rest.setChecked(true);
        }else{
            rest.setChecked(false);
        }
        if (spicnic.equals("true")){
            picnic.setChecked(true);
        }else{
            picnic.setChecked(false);
        }
        if (sparque.equals("true")){
            parque.setChecked(true);
        }else{
            parque.setChecked(false);
        }
        if (sdeporte.equals("true")){
            deporte.setChecked(true);
        }else{
            deporte.setChecked(false);
        }
        if (sturismo.equals("true")){
            turismo.setChecked(true);
        }else{
            turismo.setChecked(false);
        }
        if (sarte.equals("true")){
            arte.setChecked(true);
        }else{
            arte.setChecked(false);
        }
        if (scaminar.equals("true")){
            caminar.setChecked(true);
        }else{
            caminar.setChecked(false);
        }
        if (sesp.equals("true")){
            esp.setChecked(true);
        }else{
            esp.setChecked(false);
        }
        if (singles.equals("true")){
            ingles.setChecked(true);
        }else{
            ingles.setChecked(false);
        }
        if (ssalud.equals("true")){
            salud.setChecked(true);
        }else{
            salud.setChecked(false);
        }
        if (scomputacion.equals("true")){
            computacion.setChecked(true);
        }else{
            computacion.setChecked(false);
        }
        if (stecnologia.equals("true")){
            tecnologia.setChecked(true);
        }else{
            tecnologia.setChecked(false);
        }
        if (snaturaleza.equals("true")){
            naturaleza.setChecked(true);
        }else{
            naturaleza.setChecked(false);
        }
        if (ssociales.equals("true")){
            sociales.setChecked(true);
        }else{
            sociales.setChecked(false);
        }
        if (shumanidades.equals("true")){
            humanidades.setChecked(true);
        }else{
            humanidades.setChecked(false);
        }
        if (seconomicas.equals("true")){
            economicas.setChecked(true);
        }else{
            economicas.setChecked(false);
        }
        if (sadministrativas.equals("true")){
            administrativas.setChecked(true);
        }else{
            administrativas.setChecked(false);
        }
        if (sartes.equals("true")){
            artes.setChecked(true);
        }else{
            artes.setChecked(false);
        }
        if (sexactas.equals("true")){
            exactas.setChecked(true);
        }else{
            exactas.setChecked(false);
        }


    }

    //Metodo para guardar preferencias...
    public void guardarpreferencias(){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (juegosmesa.isChecked()){
            editor.putString("juegosmesa","true");
        }else{
            editor.putString("juegosmesa","false");
        }
        if (cine.isChecked()){
            editor.putString("cine","true");
        }else{
            editor.putString("cine","false");
        }
        if (museo.isChecked()){
            editor.putString("museo","true");
        }else{
            editor.putString("museo","false");
        }
        if (literatura.isChecked()){
            editor.putString("literatura","true");
        }else{
            editor.putString("literatura","false");
        }
        if (videojuegos.isChecked()){
            editor.putString("videojuegos","true");
        }else{
            editor.putString("videojuegos","false");
        }
        if (rest.isChecked()){
            editor.putString("rest","true");
        }else{
            editor.putString("rest","false");
        }
        if (picnic.isChecked()){
            editor.putString("picnic","true");
        }else{
            editor.putString("picnic","false");
        }
        if (parque.isChecked()){
            editor.putString("parque","true");
        }else{
            editor.putString("parque","false");
        }
        if (deporte.isChecked()){
            editor.putString("deporte","true");
        }else{
            editor.putString("deporte","false");
        }
        if (turismo.isChecked()){
            editor.putString("turismo","true");
        }else{
            editor.putString("turismo","false");
        }
        if (arte.isChecked()){
            editor.putString("arte","true");
        }else{
            editor.putString("arte","false");
        }
        if (caminar.isChecked()){
            editor.putString("caminar","true");
        }else{
            editor.putString("caminar","false");
        }
        if (esp.isChecked()){
            editor.putString("esp","true");
        }else{
            editor.putString("esp","false");
        }
        if (ingles.isChecked()){
            editor.putString("ingles","true");
        }else{
            editor.putString("ingles","false");
        }
        if (salud.isChecked()){
            editor.putString("salud","true");
        }else{
            editor.putString("salud","false");
        }
        if (computacion.isChecked()){
            editor.putString("computacion","true");
        }else{
            editor.putString("computacion","false");
        }
        if (tecnologia.isChecked()){
            editor.putString("tecnologia","true");
        }else{
            editor.putString("tecnologia","false");
        }
        if (naturaleza.isChecked()){
            editor.putString("naturaleza","true");
        }else{
            editor.putString("naturaleza","false");
        }
        if (sociales.isChecked()){
            editor.putString("sociales","true");
        }else{
            editor.putString("sociales","false");
        }
        if (humanidades.isChecked()){
            editor.putString("humanidades","true");
        }else{
            editor.putString("humanidades","false");
        }
        if (economicas.isChecked()){
            editor.putString("economicas","true");
        }else{
            editor.putString("economicas","false");
        }
        if (administrativas.isChecked()){
            editor.putString("administrativas","true");
        }else{
            editor.putString("administrativas","false");
        }
        if (artes.isChecked()){
            editor.putString("artes","true");
        }else{
            editor.putString("artes","false");
        }
        if (exactas.isChecked()){
            editor.putString("exactas","true");
        }else{
            editor.putString("exactas","false");
        }
        editor.commit();

    }


}
