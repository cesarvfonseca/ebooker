package mx.edu.utags.ebookerapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mx.edu.utags.ebookerapp.library.JSONParser;

/*PANTALLA DE BIENVENIDA*/
public class inicio extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    String user,Carrera,Fecha,Grupo,Salon,Edificio,h1;
    public final int  NOTIFICACION_ID=1;
    TextView txt_usr,tvFecha;
    Spinner spCarrera,spEdificio,spSalon,spGrupo,spHorario;
    Button btnEnviar;

    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    //testing on Emulator:
//    private static final String REGISTER_URL = "http://192.168.2.163/eBooker/reserva.php";
    private static final String REGISTER_URL = "http://www.webdesigns.hol.es/eBooker/reserva.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        txt_usr= (TextView) findViewById(R.id.tvid);
        tvFecha= (TextView) findViewById(R.id.tvFecha);
        btnEnviar=(Button)findViewById(R.id.btnEnviar);

        spCarrera=(Spinner)findViewById(R.id.spCarrera);
        spEdificio=(Spinner)findViewById(R.id.spEdificio);
        spSalon=(Spinner)findViewById(R.id.spSalon);
        spGrupo=(Spinner)findViewById(R.id.spGrupo);
        spHorario=(Spinner)findViewById(R.id.spHorario);

        //ASIGNAR EDIFICIO AL SPINER POR MEDIO DEL ARRAYADAPTER
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.Aulas , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEdificio.setAdapter(spinner_adapter);

        btnEnviar.setOnClickListener((View.OnClickListener) this);
        //INVOCAR CAMBIO DE SALON POR EDIFICIO POR MEDIO DEL ITEM SELECCIONADO
        spEdificio.setOnItemSelectedListener(this);


        Bundle extras = getIntent().getExtras();
        //Obtenemos datos enviados en el intent.
        if (extras != null) {
            user  = extras.getString("user");//usuario
        }else{
            user="error";
        }

        txt_usr.setText(user+"@utags.edu.mx");//cambiamos texto al nombre del usuario logueado
        tvFecha.setText(fechaHoraActual());

        //NOTIFICACION
        notificacion();
    }

    //Definimos que para cuando se presione la tecla BACK no volvamos para atras
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // no hacemos nada.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String fechaHoraActual(){
        return new SimpleDateFormat( "yyyy-MM-dd", java.util.Locale.getDefault()).format(Calendar.getInstance().getTime());
    }
    //OBTENER DATOS PARTA ENVIARLOS A LA CONSULTA
    @Override
    public void onClick(View v) {
        Fecha=tvFecha.getText().toString();
        Grupo=spGrupo.getSelectedItem().toString();
        Salon=spSalon.getSelectedItem().toString();
        h1=spHorario.getSelectedItem().toString();
        Carrera=spCarrera.getSelectedItem().toString();
        Edificio=spEdificio.getSelectedItem().toString();

        new Reservar().execute();
    }

    //NOTIFICACIONES
    public void notificacion(){
        //Intent implicito
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.webdesigns.hol.es/eBooker/inicio.html"));
          Intent intent = new Intent(inicio.this,webView.class);
          PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        //Notificacion
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.comment);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.uta));
        builder.setContentTitle("Recuerda tus reservaciones!");
        builder.setContentText("Bienvenido "+user+"@utags.edu.mx");
        builder.setSubText("Continuar...");

        //Enviar notificacion
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICACION_ID, builder.build());
    }

    //CAMBIAR SALON DEPENDIENTE DEL EDIFICIO
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (spEdificio.getSelectedItem().toString()){
            case "Aula 1":
                ArrayAdapter spinner_adapter1 = ArrayAdapter.createFromResource( this, R.array.av , android.R.layout.simple_spinner_item);
                spinner_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(spinner_adapter1);
                break;
            case "Aula 2":
                ArrayAdapter spinner_adapter2 = ArrayAdapter.createFromResource( this, R.array.av , android.R.layout.simple_spinner_item);
                spinner_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(spinner_adapter2);
                break;
            case "Aula 3":
                ArrayAdapter spinner_adapter3 = ArrayAdapter.createFromResource( this, R.array.av , android.R.layout.simple_spinner_item);
                spinner_adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(spinner_adapter3);
                break;
            case "Aula 4":
                ArrayAdapter spinner_adapter4 = ArrayAdapter.createFromResource( this, R.array.av , android.R.layout.simple_spinner_item);
                spinner_adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(spinner_adapter4);
                break;
            case "Aula 5":
                ArrayAdapter spinner_adapter5 = ArrayAdapter.createFromResource( this, R.array.av , android.R.layout.simple_spinner_item);
                spinner_adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(spinner_adapter5);
                break;
            case "Aula 6":
                ArrayAdapter sat6 = ArrayAdapter.createFromResource( this, R.array.a6 , android.R.layout.simple_spinner_item);
                sat6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(sat6);
                break;
            case "Aula 7":
                ArrayAdapter sat7 = ArrayAdapter.createFromResource( this, R.array.a7 , android.R.layout.simple_spinner_item);
                sat7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(sat7);
                break;
            case "Vinculación":
                ArrayAdapter satv = ArrayAdapter.createFromResource( this, R.array.Vin , android.R.layout.simple_spinner_item);
                satv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(satv);
                break;
            case "A. Inteligentes":
                ArrayAdapter satI = ArrayAdapter.createFromResource( this, R.array.Aint    , android.R.layout.simple_spinner_item);
                satI.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSalon.setAdapter(satI);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class Reservar extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(inicio.this);
            pDialog.setMessage("Realizando reserva...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String profesor = user;
            String carrera = Carrera;
            String fecha = Fecha;
            String grupo = Grupo;
            String salon = Salon;
            String edificio = Edificio;
            String horario = h1;
            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("profesor", profesor));
                params.add(new BasicNameValuePair("carrera", carrera));
                params.add(new BasicNameValuePair("fecha", fecha));
                params.add(new BasicNameValuePair("spGrupo", grupo));
                params.add(new BasicNameValuePair("spSalon", salon));
                params.add(new BasicNameValuePair("edificio", edificio));
                params.add(new BasicNameValuePair("cbH1", horario));
                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Reserva realizada!", json.toString());
                    finish();
                    Intent login = new Intent(inicio.this,Login.class);
                    startActivity(login);
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(inicio.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

    //MENU DESPLEGABLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String opcion=item.toString();
        if (opcion.equalsIgnoreCase("salir")) {
            Intent login = new Intent(inicio.this,Login.class);
            startActivity(login);
            finish();
        }else if (opcion.equalsIgnoreCase("Reservas")) {
            finish();
            Intent Reservaciones = new Intent(inicio.this,Lista_Reservacion.class);
            Reservaciones.putExtra("user",user);
            startActivity(Reservaciones);
        }else if (opcion.equalsIgnoreCase("mapa")) {
            finish();
            Intent mapa = new Intent(inicio.this,mapa_Activity.class);
            mapa.putExtra("user",user);
            startActivity(mapa);
        }
        return super.onOptionsItemSelected(item);
    }

}

