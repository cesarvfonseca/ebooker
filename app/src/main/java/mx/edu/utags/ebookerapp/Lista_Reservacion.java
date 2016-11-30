package mx.edu.utags.ebookerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import mx.edu.utags.ebookerapp.library.Biodata;

public class Lista_Reservacion extends ActionBarActivity implements OnClickListener, AdapterView.OnItemSelectedListener {

    Biodata biodata = new Biodata();
    TableLayout tabelBiodata;

    ArrayList<Button> buttonEdit = new ArrayList<Button>();
    ArrayList<Button> buttonDelete = new ArrayList<Button>();

    JSONArray arrayBiodata;

    String user;
    ImageView ivPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_reservacion);

        ivPerfil=(ImageView)findViewById(R.id.ivPerfil);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        tabelBiodata = (TableLayout) findViewById(R.id.tableBiodata);
//        buttonTambahBiodata = (Button) findViewById(R.id.buttonTambahBiodata);
//        buttonTambahBiodata.setOnClickListener(this);

        TableRow barisTabel = new TableRow(this);
        barisTabel.setBackgroundColor(Color.GRAY);

        TextView viewHeaderId = new TextView(this);
        TextView columnaHorario = new TextView(this);
        TextView columnaFecha = new TextView(this);
        TextView viewHeaderAction = new TextView(this);

        viewHeaderId.setText("ID");
        columnaHorario.setText("Horario");
        columnaFecha.setText("Fecha");
        viewHeaderAction.setText("Action");

        viewHeaderId.setPadding(15, 10, 15, 10);
        columnaHorario.setPadding(15, 10, 15, 10);
        columnaFecha.setPadding(15, 10, 15, 10);
        viewHeaderAction.setPadding(15, 10, 15, 10);

        barisTabel.addView(viewHeaderId);
        barisTabel.addView(columnaHorario);
        barisTabel.addView(columnaFecha);
        barisTabel.addView(viewHeaderAction);

        Bundle extras = getIntent().getExtras();
        //Obtenemos datos enviados en el intent.
        if (extras != null) {
            user = extras.getString("user");//usuario
        }

        tabelBiodata.addView(barisTabel, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        try {

//            arrayBiodata = new JSONArray(biodata.tampilBiodata());
            arrayBiodata = new JSONArray(biodata.tampilBiodata(user));//PROFESOR

            for (int i = 0; i < arrayBiodata.length(); i++) {
                JSONObject jsonChildNode = arrayBiodata.getJSONObject(i);
                String fecha = jsonChildNode.optString("fecha");
                String horario = jsonChildNode.optString("h1");
                String id = jsonChildNode.optString("idRESERVA");

                System.out.println("Horario :" + horario);
                System.out.println("Fecha :" + fecha);
                System.out.println("ID :" + id);

                barisTabel = new TableRow(this);

                if (i % 2 == 0) {
                    barisTabel.setBackgroundColor(Color.LTGRAY);
                }

                TextView viewId = new TextView(this);
                viewId.setText(id);
                viewId.setPadding(15, 10, 15, 10);
                barisTabel.addView(viewId);

                TextView viewNama = new TextView(this);
                viewNama.setText(fecha);
                viewNama.setPadding(15, 10, 15, 10);
                barisTabel.addView(viewNama);

                TextView viewAlamat = new TextView(this);
                viewAlamat.setText(horario);
                viewAlamat.setPadding(15, 10, 15, 10);
                barisTabel.addView(viewAlamat);

                buttonEdit.add(i, new Button(this));
                buttonEdit.get(i).setId(Integer.parseInt(id));
                buttonEdit.get(i).setTag("Edit");
                buttonEdit.get(i).setText("Edit");
                buttonEdit.get(i).setOnClickListener(this);
//                barisTabel.addView(buttonEdit.get(i));

                buttonDelete.add(i, new Button(this));
                buttonDelete.get(i).setId(Integer.parseInt(id));
                buttonDelete.get(i).setTag("Delete");
                buttonDelete.get(i).setText("Eliminar");
                buttonDelete.get(i).setOnClickListener(this);
                barisTabel.addView(buttonDelete.get(i));


                tabelBiodata.addView(barisTabel, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ivPerfil.setImageBitmap(getBitmapFromURL("http://webdesigns.hol.es/eBooker/imagenes/"+user+".jpg"));

    }

    //CARGAR IMAGEN DE USUARIO
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
    //CARGAR IMAGEN DE USUARIO


    public void onClick(View view) {

   /*
    * Melakukan pengecekan pada data array, agar sesuai dengan index
    * masing-masing button
    */
        for (int i = 0; i < buttonEdit.size(); i++) {

    /* jika yang diklik adalah button edit */
            if (view.getId() == buttonEdit.get(i).getId() && view.getTag().toString().trim().equals("Edit")) {
                // Toast.makeText(MainActivity.this, "Edit : " +
                // buttonEdit.get(i).getId(), Toast.LENGTH_SHORT).show();
                int id = buttonEdit.get(i).getId();
                getDataByID(id);

            } /* jika yang diklik adalah button delete */
            else if (view.getId() == buttonDelete.get(i).getId() && view.getTag().toString().trim().equals("Delete")) {
                // Toast.makeText(MainActivity.this, "Delete : " +
                // buttonDelete.get(i).getId(), Toast.LENGTH_SHORT).show();
                int id = buttonDelete.get(i).getId();
                deleteBiodata(id);

            }
        }
//        }
    }

    public void deleteBiodata(int id) {
        biodata.deleteBiodata(id);

  /* restart acrtivity */
        finish();
        startActivity(getIntent());

    }

    public void getDataByID(int id) {

        String namaEdit = null, alamatEdit = null;
        JSONArray arrayPersonal;

        try {

            arrayPersonal = new JSONArray(biodata.getBiodataById(id));

            for (int i = 0; i < arrayPersonal.length(); i++) {
                JSONObject jsonChildNode = arrayPersonal.getJSONObject(i);
                namaEdit = jsonChildNode.optString("h1");
                alamatEdit = jsonChildNode.optString("AULAS_idAULAS");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout layoutInput = new LinearLayout(this);
        layoutInput.setOrientation(LinearLayout.VERTICAL);

        // buat id tersembunyi di alertbuilder
        final TextView viewId = new TextView(this);
        viewId.setText(String.valueOf(id));
        viewId.setTextColor(Color.TRANSPARENT);
        layoutInput.addView(viewId);

        final EditText editNama = new EditText(this);
        editNama.setText(namaEdit);
        layoutInput.addView(editNama);

        final EditText editAlamat = new EditText(this);
        editAlamat.setText(alamatEdit);
        layoutInput.addView(editAlamat);

        AlertDialog.Builder builderEditBiodata = new AlertDialog.Builder(this);
        builderEditBiodata.setIcon(R.drawable.mod4);
        builderEditBiodata.setTitle("Update Biodata");
        builderEditBiodata.setView(layoutInput);
        builderEditBiodata.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nama = editNama.getText().toString();
                String alamat = editAlamat.getText().toString();

                System.out.println("Nama : " + nama + " Alamat : " + alamat);
                String laporan = biodata.updateBiodata(viewId.getText().toString(), editNama.getText().toString(),
                        editAlamat.getText().toString());


                Toast.makeText(Lista_Reservacion.this, laporan, Toast.LENGTH_SHORT).show();

    /* restart acrtivity */
                finish();
                startActivity(getIntent());
            }

        });

        builderEditBiodata.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builderEditBiodata.show();

    }

    //MENU DESPLEGABLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lista2, menu);

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
            Intent login = new Intent(Lista_Reservacion.this,Login.class);
            startActivity(login);
        }else if (opcion.equalsIgnoreCase("Inicio")) {
            finish();
            Intent Inicio = new Intent(Lista_Reservacion.this,inicio.class);
            Inicio.putExtra("user",user);
            startActivity(Inicio);
        }else if (opcion.equalsIgnoreCase("mapa")) {
            finish();
            Intent mapa = new Intent(Lista_Reservacion.this, mapa_Activity.class);
            mapa.putExtra("user", user);
            startActivity(mapa);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}