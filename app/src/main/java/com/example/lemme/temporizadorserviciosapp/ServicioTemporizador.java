package com.example.lemme.temporizadorserviciosapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lemme on 5/27/15.
 */
public class ServicioTemporizador extends Service {
    private static final String TAG = "EjemploServicio";
    private int segundos;
    private int minutos;
    private IBinder myBinder = new LocalBinder();
    private Timer timer;

    private boolean indicador;

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "servicio creado");
        timer = new Timer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "servicio destruido");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                segundos++;
                if(segundos == 60) {
                    segundos = 0;
                    minutos++;
                }
                Log.i(TAG, String.valueOf(segundos));
                if(segundos >= 0) {
                    publishResults(segundos, minutos);
                }
            }
        }, 1000, 1*1*1000);
        indicador = true;
        Log.i(TAG, "servicio iniciado");
        this.startForeground(0, null);
        return START_STICKY;
    }

    private void publishResults(int segundos, int minutos) {
        Intent intent = new Intent("servicio");
        Bundle bundle = new Bundle();
        bundle.putInt("segundos", segundos);
        bundle.putInt("minutos", minutos);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class LocalBinder extends Binder {
        public ServicioTemporizador getService() {
            return ServicioTemporizador.this;
        }
    }

    public boolean isIndicador() {
        return indicador;
    }
}
