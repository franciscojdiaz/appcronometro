package com.example.dvjalexpablo.cronometro;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private TextView txtcrono;
    private Button btnvuelta;
    private Button btninipaus;
    private Button btndetener;

    private TextView edit_vueltas;

    private ScrollView scvuelta;

    private Context mContext;
    private Cronometro mCronometro;
    private Thread mThreadCronometro;

    String arr [] = new String[100];

    private int vuelta = 1;
    private int k =0, sw = 0;
    private String tiempop, edoTextColor, edocolorbtnini, nombbtnini;
    private boolean corriendo = false;
    private long temp = System.currentTimeMillis(), generado;;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //generado

        ;
        txtcrono =(TextView)findViewById(R.id.txtcrono);


        mContext = this;

        btnvuelta=(Button)findViewById(R.id.btnvuelta);
        btninipaus=(Button)findViewById(R.id.btninipaus);
        btndetener=(Button)findViewById(R.id.btndetener);

        edit_vueltas =(TextView)findViewById(R.id.edit_vueltas);

        scvuelta =(ScrollView)findViewById(R.id.sv_vueltas);

        btnvuelta.setTextColor(getResources().getColor(R.color.negro));;
        btninipaus.setTextColor(getResources().getColor(R.color.negro));;
        btndetener.setTextColor(getResources().getColor(R.color.negro));;
        edit_vueltas.setTextColor(getResources().getColor(R.color.negro));;
        edit_vueltas.setEnabled(false);

        reestaurarEstado(savedInstanceState);

        btninipaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPausar();

            }
        });

        btndetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        detener();
            }
        });


        btnvuelta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCronometro == null){
                    return;
                }
                edit_vueltas.setText("");
                String cad =  "         " +String.valueOf(vuelta).trim() +"                      "+ String.valueOf(txtcrono.getText()).trim() +"\n";
                cargaInfo(cad);
                vuelta++;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putLong("generado",generado);
        outState.putString("edotextcolor",edoTextColor);
        outState.putString("edocolorbtnini",edocolorbtnini);
        outState.putString("nombbtnini",nombbtnini);
        outState.putBoolean("corriendo",corriendo);
    }


    public void reestaurarEstado(Bundle savedInstanceState){

        if(savedInstanceState != null){
            generado = savedInstanceState.getLong("generado");
            edoTextColor = savedInstanceState.getString("edotextcolor");
            edocolorbtnini = savedInstanceState.getString("edocolorbtnini");
            nombbtnini = savedInstanceState.getString("nombbtnini");
            corriendo = savedInstanceState.getBoolean("corriendo");


            if(corriendo == true) {

                mCronometro = new Cronometro(mContext);
                mThreadCronometro = new Thread(mCronometro);
                mThreadCronometro.start();
                mCronometro.iniciar(generado);

                txtcrono.setTextSize(35);
                txtcrono.setTextColor(getResources().getColor(R.color.negro));


                /////////////////////////
               if(edoTextColor.equals("rojo")){
                    txtcrono.setBackground(getResources().getDrawable(R.drawable.txt_circle2));
            }else{
                txtcrono.setBackground(getResources().getDrawable(R.drawable.txt_circle));
              }


                if(edocolorbtnini.equals("rojo")){
                    btninipaus.setBackground(getResources().getDrawable(R.drawable.txt_circle2));
                }else{
                    btninipaus.setBackground(getResources().getDrawable(R.drawable.txt_circle));
                }
                if(nombbtnini.equals("PAUSAR")){
                    btninipaus.setText("PAUSAR");
                }else {
                    btninipaus.setText("INICIAR");
                }

                ////////////////////////

            }
            else {

            txtcrono.setText("\n"+"\n"+"   00:00:00:000");
            txtcrono.setTextSize(38);
            txtcrono.setTextColor(getResources().getColor(R.color.negro));
            }

        }else{


            txtcrono.setText("\n"+"\n"+"   00:00:00:000");
            txtcrono.setTextSize(38);
            txtcrono.setTextColor(getResources().getColor(R.color.negro));

        }

    }


    public void detener(){

        btninipaus.setTextColor(getResources().getColor(R.color.negro));;
        if (mCronometro != null){

            mCronometro.detener();
            mThreadCronometro.interrupt();
            mThreadCronometro = null;
            mCronometro = null;
            btninipaus.setText("INICIAR");
            temp = System.currentTimeMillis();
            corriendo = false;
            if(arr.length > 0 ){
                for(int a = 1; a < 100; a++){

                    arr[a] = null;
                }
            }
            btninipaus.setBackground(getResources().getDrawable(R.drawable.txt_circle));
        }

    }

    public void iniciarPausar(){

        String nombbtn =  btninipaus.getText().toString();
        if(nombbtn.equals("INICIAR")) {
            if (mCronometro == null) {

                mCronometro = new Cronometro(mContext);
                mThreadCronometro = new Thread(mCronometro);
                mThreadCronometro.start();
                mCronometro.iniciar(temp);
                generado = temp;
                vuelta = 1;
                edit_vueltas.setText("");

                edoTextColor = "rojo";
                edocolorbtnini = "rojo";
                corriendo = true;
            }

            btninipaus.setText("PAUSAR");
            nombbtnini = "PAUSAR";
            btninipaus.setTextColor(getResources().getColor(R.color.rojo));
            btninipaus.setBackground(getResources().getDrawable(R.drawable.txt_circle2));

            txtcrono.setBackground(getResources().getDrawable(R.drawable.txt_circle));


        }else{

            txtcrono.setBackground(getResources().getDrawable(R.drawable.txt_circle2));
            btninipaus.setTextColor(getResources().getColor(R.color.negro));;
            btninipaus.setBackground(getResources().getDrawable(R.drawable.txt_circle));

            new ProcesoTitila().execute();

            if (mCronometro != null){

                tiempop = String.valueOf(txtcrono.getText()).trim();
                mCronometro.pausa();
                mThreadCronometro.interrupt();
                mThreadCronometro = null;
                mCronometro = null;
                btninipaus.setText("INICIAR");
                nombbtnini = "INICIAR";

                edoTextColor = "verde";
                edocolorbtnini = "verde";
                corriendo = false;

            }


        }

    }

    public void cargaInfo(String cadena) {
         arr[vuelta] = cadena;
           System.out.println(arr[vuelta].toString());

            for (int i = vuelta; i > 0; i--) {
                edit_vueltas.append(arr[i].toString());
                scvuelta.post(new Runnable() {
                    @Override
                    public void run() {
                        scvuelta.smoothScrollTo(0, edit_vueltas.getBottom());
                    }
                });

            }

    }

    public void actualizaTiempo(final String time){

        //generado = time;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtcrono.setText("\n"+"\n"+"   "+time);
            }
        });

    }

    //////////////////////////////////////////////
    private class ProcesoTitila extends AsyncTask<Void, Integer, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            for (int i = 2; ; i++) {

                if (i < 2000) {
                    publishProgress(1);
                }
                if (i > 2000 && i < 4000) {
                    publishProgress(2);
                }
                    if (i > 4000 && i < 6000) {
                        publishProgress(1);
                    }
                    if (i > 6000 && i < 8000) {
                        publishProgress(2);
                    }
                    if (i > 8000 && i < 10000) {
                        publishProgress(1);
                    }
                    if (i > 10000 && i < 12000) {
                        publishProgress(2);
                        break;
                    }

                }

            return null;
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int dato = values[0].intValue();

            if(dato == 1){
                txtcrono.setTextColor(getResources().getColor(R.color.blanco));;
            }
            else{
                txtcrono.setTextColor(getResources().getColor(R.color.negro));;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }
}
