package com.ericmatias.centralautobuses;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.BlockedNumberContract;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class VerUnBusActivity extends AppCompatActivity implements View.OnClickListener{

    TextView verMatricula, errorMatricula;
    String matriculaSelect;
    String[] spinDefault = {"Selecciona una matricula"};
    ImageButton btnInfo, btnAtras;
    Spinner spin;
    Button btnIniciarMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_un_bus);
        iniciarValores();
        setAdaptador(spinDefault);
        obtenerAutobusesSpinner oas = new obtenerAutobusesSpinner();
        oas.execute();
    }

    /**
     * Metodo onClick donde podemos ver un solo bus (ocultara las demas opciones)
     * Mostrar todos los buses
     * Enseñar la informacion con una imagen que tenemos
     * Boton para ir atras en caso de haberle dado a ver un bus en concreto
     * Y por ultimo una vez seleccionado en el spinner la matricula ver la ruta de ese autobus en concreto.
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnInfo):
                mostrarInfo();
                break;
            case (R.id.btnAtras):
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case (R.id.btnIniciarMapa):
                if (spin.getSelectedItem().toString().equals("Selecciona una matricula")) {
                    errorMatricula.setVisibility(View.VISIBLE);
                } else {
                    Intent i = new Intent(this, MapsActivity.class);
                    matriculaSelect = spin.getSelectedItem().toString();
                    i.putExtra("matricula", matriculaSelect);
                    startActivity(i);
                    break;
                }
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
        alertDialog.setMessage(getString(R.string.instruccion2));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Metodo que adapta los valores del spinner con los valores de las matriculas de la BBDD
     * @param array
     */
    public void setAdaptador(String[] array) {
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, array);
        spin.setAdapter(adaptador);
    }

    /**
     * Clase interna donde ejecutamos la tarea asincrona.
     */
    private class obtenerAutobusesSpinner extends AsyncTask<Void, Void, Boolean> {
        //Declaramos un array para las matriculas.
        String[] arrayMatriculas;

        public obtenerAutobusesSpinner() {
        }

        /**
         * Ejecutamos en segundo plano el siguiente metodo, en el cual hacemos una peticion GET para obtener todos los buses
         * Una vez obetnidas son pasadas a un objeto JSON. De este lo pasamos al array de String creado anteriormente para guardar todas las matriculas
         *
         * @param params
         * @return
         */
        protected Boolean doInBackground(Void... params) {

            boolean correcto;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://192.168.1.46:8080/WebClientRest/webresources/mapas/todas/matriculas");
            get.setHeader("content-type", "application/json");

            try {
                HttpResponse resp = httpClient.execute(get);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray arrayBus = new JSONArray(respStr);
                arrayMatriculas = new String[arrayBus.length()];
                for (int i = 0; i < arrayBus.length(); i++) {
                    JSONObject bus = arrayBus.getJSONObject(i);
                    String matricula = bus.getString("matricula");
                    arrayMatriculas[i] = matricula;
                }
                correcto = true;
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                correcto = false;
            }
            return correcto;
        }

        /**
         * Metodo utilizado para una vez realizada la tarea en segundo plano decirnos si hemos podido o no
         * listar los datos de la BBDD, en caso de poder listarlos nos enseñara en el spinner las matriculas
         *
         * @param result
         */
        protected void onPostExecute(Boolean result) {

            if (result) {
                setAdaptador(arrayMatriculas);
            } else {
                Toast.makeText(VerUnBusActivity.this, "No se ha podido listar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void iniciarValores() {
        btnInfo = (ImageButton) findViewById(R.id.btnInfo);
        btnIniciarMapa = (Button) findViewById(R.id.btnIniciarMapa);
        btnAtras = (ImageButton) findViewById(R.id.btnAtras);
        verMatricula = (TextView) findViewById(R.id.selMatricula);
        errorMatricula = (TextView) findViewById(R.id.errorMatricula);
        spin = (Spinner) findViewById(R.id.spinner);
        btnAtras.setOnClickListener(this);
        btnIniciarMapa.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
    }
}
