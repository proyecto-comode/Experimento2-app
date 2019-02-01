package mx.daniel.arechiga.comode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by GUAPO on 30/08/2016.
 */
public class Servicio_Notificaciones_Firebase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String body = remoteMessage.getNotification().getBody();
        String titulo = remoteMessage.getNotification().getTitle();
        String id = remoteMessage.getData().get("Id_Notificacion");
        MostrarNotificacion(body,titulo,id);

    }

    //Metodo para mostrar la notifcacion recibida...
    private void MostrarNotificacion(String Body, String titulo, String id){

        int icon = R.drawable.ic_notificaciones;
        long[] vibracion = {500,500,500,500,500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Creando manager de la notificacion para notificarla...
        Notification notificacion;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,BandejaNotificaciones.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Id_Notificacion",id);
        intent.putExtra("Titulo",titulo);
        intent.putExtra("Mensaje",Body);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificacion = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(titulo)
                .setContentText(Body)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setVibrate(vibracion)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.WHITE, 1000, 500)
                .build();

        //Codigo para saber el numero para la notificacion ...
        VariablesGlobales g = VariablesGlobales.getInstance();
        int numero = g.getNumero_Notificaciones();
        // Construir la notificación y emitirla
        notificationManager.notify(numero, notificacion);
        //Incrementar en uno el numero de notificaciones que van para la siguiente notificacion..
        numero++;
        //Actualizar el numero...
        g.setNumero_notificaciones(numero);
    }


}