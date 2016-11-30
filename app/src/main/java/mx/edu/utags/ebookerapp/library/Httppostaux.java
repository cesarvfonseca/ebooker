package mx.edu.utags.ebookerapp.library;

/*Importando librerias*/
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
/*CLASE AUXILIAR PARA EL ENVIO DE PETICIONES A NUESTRO SISTEMA
 * Y MANEJO DE RESPUESTA.*/
public class Httppostaux{

    InputStream is = null;//Nos permitira escribir en el web service
    String result = "";//Cadena que recibira la respuesta del web service

    //METODO QUE RECIBE LOS DATOS DEL WEB SERVICE
    public JSONArray getserverdata(ArrayList<NameValuePair> parameters, String urlwebserver ){
        //Conecta via http y envia un post.
        httppostconnect(parameters,urlwebserver);
        if (is!=null){//si obtuvo una respuesta
            getpostresponse();
            return getjsonarray();
        }else{//Si no obtiene respuesta
            return null;
        }
    }//FIN DE LA CLASE getserverdata


    //Peticion al web service via HTTP
    private void httppostconnect(ArrayList<NameValuePair> parametros, String urlwebserver){

        //Creando la conexion HTTP
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlwebserver);
            httppost.setEntity(new UrlEncodedFormEntity(parametros));
            //Ejecutar la peticion enviando datos por el metodo POST
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
        }
    }

    /*Convertir la respuesta del Web Service a cadena*/
    public void getpostresponse(){
        //Convierte respuesta a String
        try{
            //Clase lectora bajo el estandar iso-8859-1 para signos especiales
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();//Construir cadena contenedora
            String line = null;//Linea receptorad del codigo
            while ((line = reader.readLine()) != null) {//Leer lo rebido por linea
                sb.append(line + "\n");
            }
            is.close();//Cerrar conexion
            result=sb.toString();//Convertir los datos recibidos a tipo cadena (String)
            Log.e("getpostresponse"," result= "+sb.toString());//Log para mostrar el resultado en consola, finalidad de pruebas
        //Manejar las excepciones y errores para evitar caida del sistema
        }catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }
    }

    /*Metodo convertir la cadena a arreglo*/
    public JSONArray getjsonarray(){
        //Parse a los datos JSON
        try{
            JSONArray jArray = new JSONArray(result);//Crear arreglo del tipo JSON
            return jArray;//Regresamos el arreglo
        }
        //Manejar las excepciones y errores para evitar caida del sistema
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
            return null;
        }
    }

}





