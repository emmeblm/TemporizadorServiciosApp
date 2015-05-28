package com.example.lemme.temporizadorserviciosapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    private EditText segundosTemporizadorText;
    private EditText minutosTemporizadorText;
    private ServiceConnectionImpl conexionServicio;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflateApplicationLayoutObjects();
        conexionServicio = new ServiceConnectionImpl(this);
        atarAServicioTemporizador();
        inicializarBroadcastReceiver();
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
        registerReceiver(broadcastReceiver, new IntentFilter("servicio"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(conexionServicio.isAtado()) {
            unbindService(conexionServicio);
        }
    }

    private void inflateApplicationLayoutObjects() {
        segundosTemporizadorText = (EditText) this.findViewById(R.id.segundosTemporizador);
        minutosTemporizadorText = (EditText) this.findViewById(R.id.minutosTemporizador);
    }

    private void atarAServicioTemporizador() {
        Intent intent = new Intent(this, ServicioTemporizador.class);
        bindService(intent, conexionServicio, Context.BIND_AUTO_CREATE);
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
        conexionServicio.setSegundosServicio(Integer.parseInt(segundosTemporizadorText.getText().toString()));
        conexionServicio.setMinutosServicio(Integer.parseInt(minutosTemporizadorText.getText().toString()));
    }

    public void onClickDetener(View view) {

    }
}
