package com.example.lemme.temporizadorserviciosapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.lemme.temporizadorserviciosapp.ServicioTemporizador.*;

public class MainActivity extends Activity {

    private EditText segundosTemporizadorText;
    private EditText minutosTemporizadorText;
    private ServiceConnection conexionServicio;
    private BroadcastReceiver broadcastReceiver;
    private ServicioTemporizador servicio;
    private boolean atado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflateApplicationLayoutObjects();
        inicializarServiceConnection();
        atarAServicioTemporizador();
        inicializarBroadcastReceiver();

    }

    private void inflateApplicationLayoutObjects() {
        segundosTemporizadorText = (EditText) this.findViewById(R.id.segundosTemporizador);
        minutosTemporizadorText = (EditText) this.findViewById(R.id.minutosTemporizador);
    }

    private void inicializarServiceConnection() {
        conexionServicio = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocalBinder localBinder = (LocalBinder) service;
                servicio = localBinder.getService();
                atado = true;
                Log.i("servicio", "conectado");
                if(!servicio.isIndicador()) {
                    Intent intentServicio = new Intent(MainActivity.this, ServicioTemporizador.class);
                    startService(intentServicio);
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
        };
    }

    private void atarAServicioTemporizador() {
        Intent intent = new Intent(this, ServicioTemporizador.class);
        bindService(intent, conexionServicio, Context.BIND_AUTO_CREATE);
    }

    private void inicializarBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String segundos = String.valueOf(extras.getInt("segundos"));
                    String minutos = String.valueOf(extras.getInt("minutos"));
                    segundosTemporizadorText.setText(segundos);
                    minutosTemporizadorText.setText(minutos);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(atado) {
            unbindService(conexionServicio);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickIniciar(View view) {
        registerBroadcastReceiver("0", "0");
    }

    public void onClickDetener(View view) {
        try {
            unregisterReceiver(broadcastReceiver);
        }catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public void onClickContinuar(View view) {
        String segundos = segundosTemporizadorText.getText().toString();
        String minutos = minutosTemporizadorText.getText().toString();
        registerBroadcastReceiver(segundos, minutos);
    }

    private void registerBroadcastReceiver(String segundos, String minutos) {
        servicio.setSegundos(Integer.parseInt(segundos));
        servicio.setMinutos(Integer.parseInt(minutos));
        try {
            registerReceiver(broadcastReceiver, new IntentFilter("servicio"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
