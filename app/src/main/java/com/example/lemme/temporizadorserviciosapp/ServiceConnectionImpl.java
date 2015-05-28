package com.example.lemme.temporizadorserviciosapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.lemme.temporizadorserviciosapp.ServicioTemporizador.LocalBinder;

/**
 * Created by lemme on 5/27/15.
 */
public class ServiceConnectionImpl implements ServiceConnection {
    private boolean atado;

    private ServicioTemporizador servicio;

    private MainActivity mainActivity;
    public ServiceConnectionImpl(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.atado = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LocalBinder atador = (LocalBinder) service;
        servicio = atador.getService();
        atado = true;
        Log.i("servicio", "conectado");
        if(!servicio.isIndicador()) {
            Intent intentServicio = new Intent(mainActivity, ServicioTemporizador.class);
            servicio.startService(intentServicio);
            Log.i("servicio", "no corriendo");
        } else {
            Log.i("servicio", "corriendo");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        atado = false;
        Log.i("servicio", "desconectado");
    }

    public boolean isAtado() {
        return atado;
    }

    public void setSegundosServicio(int segundosServicio) {
        servicio.setSegundos(segundosServicio);
    }

    public void setMinutosServicio(int minutosServicio) {
        servicio.setMinutos(minutosServicio);
    }
}
