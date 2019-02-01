package mx.daniel.arechiga.comode;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class BandejaNotificaciones extends AppCompatActivity {

    String id_notificacion = "";
    String action = "";
    TextView txt_titulo;
    TextView txt_mensaje;
    Button oportuno, inoportuno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja_notificaciones);

        txt_titulo = (TextView) findViewById(R.id.txt_Titulo_Notificacion);
        txt_mensaje = (TextView) findViewById(R.id.txt_Mensaje_Notificacion);
        oportuno = (Button) findViewById(R.id.bt_oportuno);
        inoportuno = (Button) findViewById(R.id.bt_inoportuno);

        Bundle datos = this.getIntent().getExtras();
        if(datos != null) {
            txt_titulo.setText(datos.getString("Titulo"));
            txt_mensaje.setText(datos.getString("Mensaje"));
            id_notificacion = datos.getString("Id_Notificacion");
        }

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        VariablesGlobales g = VariablesGlobales.getInstance();
        int numero = g.getNumero_Notificaciones();
        nm.cancel(numero-1);
    }

    public void opcionOportuno(View view){
        action = "Oportuno";
        inoportuno.setEnabled(false);
        new EnviarSeleccionNotificacion().execute();
    }

    public void opcionInoportuno(View view){
        action = "Inoportuno";
        oportuno.setEnabled(false);
        new EnviarSeleccionNotificacion().execute();
    }

    //Ejecucion de operacion Enviar Actualizacion Oportuno o Inoportuno notificacion en un hilo separado de la interfaz del usuario....
    private class EnviarSeleccionNotificacion extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            //Enviando los datos para crear la ontologia del dispositivo....
            String url_servidor = "http://148.202.119.34:8080/Webservice_COMoDE/Actualizar_Estadistica_Notificacion";
            HttpClient cliente = new DefaultHttpClient();
            HttpPost post = new HttpPost(url_servidor);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
            postParameters.add(new BasicNameValuePair("id_notificacion",id_notificacion));
            postParameters.add(new BasicNameValuePair("action",action));
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
            return "Gracias por su opinion";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BandejaNotificaciones.this,MainActivity.class);
            startActivity(intent);
            finish();


        }
    }
}
