package mx.edu.utags.ebookerapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.utags.ebookerapp.library.Httppostaux;

public class Login extends Activity {
    /**Se llama cuando la actividad es creada */

    EditText user;
    EditText pass;
    Button blogin;
    TextView registrar;
    Httppostaux post;
    //String IP_Server="192.168.1.11";//IP DE NUESTRO PC
    String IP_Server="www.webdesigns.hol.es";//IP DE NUESTRO PC

    //private static final String URL_connect = "http://www.webdesigns.hol.es/eBooker/acces.php";
    String URL_connect="http://"+IP_Server+"/eBooker/acces.php";//ruta en donde estan nuestros archivos

    boolean result_back;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        post=new Httppostaux();

        user= (EditText) findViewById(R.id.etUsuario);
        pass= (EditText) findViewById(R.id.etContra);
        blogin= (Button) findViewById(R.id.login);
        registrar=(TextView) findViewById(R.id.register);

        user.setText("");
        pass.setText("");

        validarConexion();
        //Login button action
        blogin.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                //Extreamos datos de los EditText
                String usuario=user.getText().toString();
                String passw=pass.getText().toString();

                //verificamos si estan en blanco
                if( checklogindata( usuario , passw )==true){

                    //si pasamos esa validacion ejecutamos el asynctask pasando el usuario y clave como parametros

                    new asynclogin().execute(usuario,passw);


                }else{
                    //si detecto un error en la primera validacion vibrar y mostrar un Toast con un mensaje de error.
                    err_login();
                }

            }
        });

        registrar.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                //Abre el navegador al formulario adduser.html
               /* String url = "http://"+IP_Server+"/eBooker/adduser.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/
                Intent register = new Intent(Login.this,Register.class);
                startActivity(register);
            }
        });

    }

    //COMPROBAR CONEXION A INTERNET
    public static boolean compruebaConexion(Context context) {
        boolean connected = false;
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = conn.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    void validarConexion(){
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(),"No hay conexión a internet", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //ACABA COMPROBAR CONEXION A INTERNET

    //vibra y muestra un Toast
    public void err_login(){
        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(250);
        Toast toast1 = Toast.makeText(getApplicationContext(),"Error: Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT);
        toast1.show();
        pass.setText("");
    }


    /*Valida el estado del logueo solamente necesita como parametros el usuario y passw*/
    public boolean loginstatus(String username ,String password ) {
        int logstatus=-1;

    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
    	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("usuario",username));
        postparameters2send.add(new BasicNameValuePair("password",password));

        //realizamos una peticion y como respuesta obtenes un array JSON
        JSONArray jdata=post.getserverdata(postparameters2send, URL_connect);

      		/*como estamos trabajando de manera local el ida y vuelta sera casi inmediato
      		 * para darle un poco realismo decimos que el proceso se pare por unos segundos para poder
      		 * observar el progressdialog
      		 * la podemos eliminar si queremos
      		 */
        SystemClock.sleep(950);

        //si lo que obtuvimos no es null
        if (jdata!=null && jdata.length() > 0){

            JSONObject json_data; //creamos un objeto JSON
            try {
                json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
                logstatus=json_data.getInt("logstatus");//accedemos al valor
                Log.e("loginstatus","logstatus= "+logstatus);//muestro por log que obtuvimos
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //validamos el valor obtenido
            if (logstatus==0){// [{"logstatus":"0"}]
                Log.e("loginstatus ", "invalido");
                return false;
            }
            else{// [{"logstatus":"1"}]
                Log.e("loginstatus ", "valido");
                return true;
            }

        }else{	//json obtenido invalido verificar parte WEB.
            Log.e("JSON  ", "ERROR");
            return false;
        }

    }


    //validamos si no hay ningun campo en blanco
    public boolean checklogindata(String username ,String password ){

        if 	(username.equals("") || password.equals("")){
            Log.e("Login ui", "checklogindata user or pass error");
            return false;

        }else{

            return true;
        }

    }

/*		CLASE ASYNCTASK
 *
 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir
 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
 * ademas observariamos el mensaje de que la app no responde.
 */

    class asynclogin extends AsyncTask< String, String, String > {

        String user,pass;
        protected void onPreExecute() {
            //para el progress dialog
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Autenticando....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            //obtnemos usr y pass
            user=params[0];
            pass=params[1];

            //enviamos y recibimos y analizamos los datos en segundo plano.
            if (loginstatus(user,pass)==true){
                return "ok"; //login valido
            }else{
                return "err"; //login invalido
            }

        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error*/
        protected void onPostExecute(String result) {

            pDialog.dismiss();//ocultamos progess dialog.
            Log.e("onPostExecute=",""+result);

            if (result.equals("ok")){

                Intent i=new Intent(Login.this, inicio.class);
                i.putExtra("user",user);
                startActivity(i);
                finish();
            }else{
                err_login();
            }

        }

    }

}




//-----------------------------------------------------------------------
