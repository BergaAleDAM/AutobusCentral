package com.ericmatias.centralautobuses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnVariosBuses, btnUnBus;
    ImageButton btnInfo;

    /**
     * Metodo on create donde inicializan los valores, se adapta el spinner al valor por defecto y se crea un objeto para recuperar los buses.
     * Despues se ejecuta la AsyncTask
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciarValores();
    }

    /**
     * Metodo onClick donde podemos ver un solo bus (ocultara las demas opciones)
     * Mostrar todos los buses
     * Enseñar la informacion con una imagen que tenemos
     * Boton para ir atras en caso de haberle dado a ver un bus en concreto
     * Y por ultimo una vez seleccionado en el spinner la matricula ver la ruta de ese autobus en concreto.
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnUnBus):
                Intent i = new Intent(this, VerUnBusActivity.class);
                startActivity(i);
                break;
            case (R.id.btnTodosBuses):
                Intent intent = new Intent(this, MapsActivityTodosBuses.class);
                startActivity(intent);
                break;
            case (R.id.btnInfo):
                mostrarInfo();
                break;
        }
    }

    /**
     * Metodo que utilizamos para crear un AlertDialog en caso de presionar en la imagen de informacion
     * Se mostrara toda la informacion correspondiente con nuestra activity acutal.
     *
     */
    public void mostrarInfo() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Instrucciones");
        alertDialog.setMessage(getString(R.string.instruccion1));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Metodo que nos inicia todos los valores (botones, spinners, etc..)
     */
    public void iniciarValores() {
        btnUnBus = (Button) findViewById(R.id.btnUnBus);
        btnVariosBuses = (Button) findViewById(R.id.btnTodosBuses);
        btnInfo = (ImageButton) findViewById(R.id.btnInfo);
        btnUnBus.setOnClickListener(this);
        btnVariosBuses.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
    }
}
