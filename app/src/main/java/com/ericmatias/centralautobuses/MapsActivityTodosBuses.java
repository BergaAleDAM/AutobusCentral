package com.ericmatias.centralautobuses;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Esta clase la utlizamos para mostrar por pantalla la ultima ubicacion conocida de cada bus.
 * Es basicamente igual que la activity de Maps, pero con el cambio de en vez de printar sus ultimas 5 posiciones conocidas,
 * solo muestra la ultima, pero de cada bus.
 */
public class MapsActivityTodosBuses extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    getLastPositionAllBus glpab = new getLastPositionAllBus();
    LatLng[] arrayPosiciones;
    String[] matricula, data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_todos_buses);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        glpab.execute();
    }

    private class getLastPositionAllBus extends AsyncTask<Void, Void, Boolean> {

        public getLastPositionAllBus() {
        }

        protected Boolean doInBackground(Void... params) {

            boolean correcto;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://192.168.1.46:8080/WebClientRest/webresources/mapas/ultima/posiciones");
            get.setHeader("content-type", "application/json");

            try {
                HttpResponse resp = httpClient.execute(get);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray arrayPos = new JSONArray(respStr);
                arrayPosiciones = new LatLng[arrayPos.length()];
                matricula = new String[arrayPos.length()];
                data = new String[arrayPos.length()];
                for (int i = 0; i < arrayPos.length(); i++) {
                    JSONObject pos = arrayPos.getJSONObject(i);
                    matricula[i] = pos.getString("matricula");
                    double latitud = pos.getDouble("latitud");
                    double longitud = pos.getDouble("longitud");
                    data[i] = pos.getString("data");
                    arrayPosiciones[i] = new LatLng(latitud, longitud);
                }
                correcto = true;
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                correcto = false;
            }
            return correcto;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MapsActivityTodosBuses.this, "Correcto", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < arrayPosiciones.length; i++) {
                    mMap.addMarker(new MarkerOptions().position(arrayPosiciones[i]).title(matricula[i]).snippet(data[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.pruebabuscopia)));

                }
            } else {
                Toast.makeText(MapsActivityTodosBuses.this, "Liada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

