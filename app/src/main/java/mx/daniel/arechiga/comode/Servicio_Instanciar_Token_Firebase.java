package mx.daniel.arechiga.comode;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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

/**
 * Created by GUAPO on 29/08/2016.
 */
public class Servicio_Instanciar_Token_Firebase extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        //Conseguir actualizacion del token...
        String token = FirebaseInstanceId.getInstance().getToken();
        RegistrarToken(token);

    }

    //Metodo para registrar el token en las preferencias y en la bd remota para poder utilizarlo despues...
    private void RegistrarToken(String token){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token);
        editor.commit();

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
        postParameters.add(new BasicNameValuePair("token",token));
        String url_servidor = "http://148.202.119.34:8080/Webservice_COMoDE/Registrar_Dispositivo";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url_servidor);
        try {
            request.setEntity(new UrlEncodedFormEntity(postParameters));
            httpClient.execute(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
 