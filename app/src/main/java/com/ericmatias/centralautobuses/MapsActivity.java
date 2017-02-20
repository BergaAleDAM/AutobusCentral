package com.ericmatias.centralautobuses;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String matricula;
    LatLng[] arrayPosiciones;
    String[] fecha;
    ObtenerUbicaciones ou = new ObtenerUbicaciones();

    /**
     * Metodo on create donde haremos primero un Fragmento del map.
     * Tambien recibimos la matricula por parametro que nos llega por un intent.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        matricula = getIntent().getStringExtra("matricula");
        Toast.makeText(MapsActivity.this, matricula, Toast.LENGTH_SHORT).show();
    }


    /**
     * Metodo utilizado para una vez este, este preparado lanzar la tarea AsyncTask.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ou.execute();

    }

    /**
     * Metodo que utilizamos para pintar la linea de recorrido del bus.
     * @param ubicaciones
     */
    public void pintarLineaMapa(List<LatLng> ubicaciones) {
        mMap.addPolyline(new PolylineOptions().addAll(ubicaciones).color(Color.RED));
    }

    /**
     * Clase interna que utilizamos para obtener las diferentes posiciones que ha recorrido un bus y despues printarlas en el mapa.
     */
    private class ObtenerUbicaciones extends AsyncTask<Void, Void, Boolean> {
        /**
         * En segundo plano realizamos una peticion GET, obtenemos a partir de la matricula las ultimas 5 posiciones conocidas del bus.
         * Una vez obtenidas pasamos del objeto JSON las posiciones a un String[] para después printar por pantalla las posiciones.
         * @param params
         * @return
         */
        protected Boolean doInBackground(Void... params) {

            boolean resul;

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet get =
                    new HttpGet("http://192.168.1.46:8080/WebClientRest/webresources/mapas/cincoUltimasPosiciones/" + matricula);

            get.setHeader("content-type", "application/json");

            try {
                HttpResponse resp = httpClient.execute(get);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray arrayPos = new JSONArray(respStr);
                arrayPosiciones = new LatLng[arrayPos.length()];
                fecha = new String[arrayPos.length()];
                for (int i = 0; i < arrayPos.length(); i++) {
                    JSONObject pos = arrayPos.getJSONObject(i);
                    matricula = pos.getString("matricula");
                    double latitud = pos.getDouble("latitud");
                    double longitud = pos.getDouble("longitud");
                    fecha[i] = pos.getString("data");
                    arrayPosiciones[i] = new LatLng(latitud, longitud);
                }
                resul = true;
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                resul = false;
            }
            return resul;
        }

        /**
         * Este metodo se ejecuta despues de la tarea en segundo plano y nos informa si habian o no ubicaciones.
         * En caso de haber ubicaciones, las marcara en el mapa y enseñara(en caso de tocar en el marcador) la hora de dicha posicion)
         * @param result
         */
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MapsActivity.this, "Correcto", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < arrayPosiciones.length; i++) {
                    mMap.addMarker(new MarkerOptions().position(arrayPosiciones[i]).title(matricula).snippet(fecha[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.pruebabuscopia)));
                }
                List<LatLng> arrayParaPintar = Arrays.asList(arrayPosiciones);
                pintarLineaMapa(arrayParaPintar);
            } else {
                Toast.makeText(MapsActivity.this, "No ubicaciones", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
