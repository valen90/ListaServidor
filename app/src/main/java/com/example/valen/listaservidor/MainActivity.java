package com.example.valen.listaservidor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private String IP_Server = "iesayala.ddns.net";
    private String url_consulta = "http://"+IP_Server+"/valen/php.php";
    private JSONArray jSONArray;
    private DevuelveJSON devuelveJSON;
    private Toolbar myTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devuelveJSON = new DevuelveJSON();

        getSettings();
        new ListaPeliculas().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dir,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        devuelveJSON = new DevuelveJSON();
        getSettings();
        new ListaPeliculas().execute();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, main_preferences.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        public void getSettings () {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        IP_Server = sp.getString("IP","iesayala.ddns.net");
        String s2 = sp.getString("PHP", "/valen/php.php");
        url_consulta = "http://"+IP_Server+s2;
        //Toast.makeText(getApplicationContext(), url_consulta, Toast.LENGTH_LONG).show();
    }

    class ListaPeliculas extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONArray doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql","SELECT Peliculas.NOMBRE, Secciones.DESCRIPCION FROM Peliculas,Secciones WHERE Peliculas.IdSeccion = Secciones.ID");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPost);
                if (jSONArray != null) {
                    return jSONArray;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                //Toast.makeText(MainActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                JSONObject jsonObject;
                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<String>>();
                for (int i = 0; i < json.length(); i++) {
                    try {
                        jsonObject = json.getJSONObject(i);
                        if(listDataChild.containsKey(jsonObject.getString("DESCRIPCION"))){
                            listDataChild.get(jsonObject.getString("DESCRIPCION")).add(jsonObject.getString("NOMBRE"));
                        }else{
                            listDataHeader.add(jsonObject.getString("DESCRIPCION"));
                            listDataChild.put(jsonObject.getString("DESCRIPCION"),(new ArrayList<String>()));
                            listDataChild.get(jsonObject.getString("DESCRIPCION")).add(jsonObject.getString("NOMBRE"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                prepareListData();
            } else {
                Toast.makeText(MainActivity.this, "JSON Array nulo", Toast.LENGTH_LONG).show();
            }
        }

    }
    private void prepareListData() {

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

    }
}
