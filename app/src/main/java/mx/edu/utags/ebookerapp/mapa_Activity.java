package mx.edu.utags.ebookerapp;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by cesarvfonseca on 12/04/2016.
 */
public class mapa_Activity extends ActionBarActivity {
//    ObtenerWebService hiloconexion;
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alert = null;
//    TextView resultado;


    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_activity);

        Bundle extras = getIntent().getExtras();
        //Obtenemos datos enviados en el intent.
        if (extras != null) {
            user  = extras.getString("user");//usuario
        }

        WebView browser = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl("http://www.webdesigns.hol.es/eBooker/mapa.html");
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String opcion = item.toString();
        if (opcion.equalsIgnoreCase("salir")) {
            finish();
        }else  if (opcion.equalsIgnoreCase("reservas")) {
            finish();
            Intent reservas = new Intent(mapa_Activity.this,Lista_Reservacion.class);
            reservas.putExtra("user", user);
            startActivity(reservas);
        } else  if (opcion.equalsIgnoreCase("inicio")) {
            finish();
            Intent inicio = new Intent(mapa_Activity.this,inicio.class);
            inicio.putExtra("user", user);
            startActivity(inicio);
        }
        return super.onOptionsItemSelected(item);
    }

}