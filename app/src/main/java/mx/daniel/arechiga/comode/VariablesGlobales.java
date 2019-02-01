package mx.daniel.arechiga.comode;

/**
 * Created by Macbook on 8/11/16.
 */
public class VariablesGlobales {
    private static VariablesGlobales instance;
    private static int numero_notificaciones = 1;

    private VariablesGlobales(){

    }
    public void setNumero_notificaciones(int numero){
        VariablesGlobales.numero_notificaciones = numero;
    }

    public int getNumero_Notificaciones(){
        return VariablesGlobales.numero_notificaciones;
    }

    public static synchronized VariablesGlobales getInstance(){
        if(instance==null){
            instance= new VariablesGlobales();
        }
        return instance;
    }
}
