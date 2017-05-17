package com.example.dvjalexpablo.cronometro;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by DVJ ALEX PABLO on 12/05/2017.
 */

public class Cronometro implements Runnable {

    public static final long MILLIS_TO_MINUTES = 60000;
    public static final long MILLIS_TO_HOURS = 3600000;

    private Context mContext;
    private long mIniciarTiempo;

    private boolean estaCorriendo;

    public Cronometro(Context context) {
        this.mContext = context;
    }

    public void iniciar(long temp){
        //mIniciarTiempo = System.currentTimeMillis();
        mIniciarTiempo = temp;
        estaCorriendo = true;
    }

    public void detener(){
        estaCorriendo = false;
    }

    public void pausa(){

        estaCorriendo = false;
    }

    @Override
    public void run() {

        while (estaCorriendo){

            long desde = System.currentTimeMillis() - mIniciarTiempo;

            int segundos = (int) ((desde / 1000) % 60);
            int minutos = (int) (((desde / MILLIS_TO_MINUTES)) % 60);
            int horas = (int) ((desde / (MILLIS_TO_HOURS)) % 24);
            int millis = (int) desde % 1000;
            ((MainActivity)mContext).actualizaTiempo(String.format("%02d:%02d:%02d:%03d", horas, minutos,segundos,millis));


        }

    }
}
