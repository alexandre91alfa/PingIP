package com.example.pingaemmim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView pingList;
    private EditText edPing;
    private Editable ip;
    private Handler handler;
    private ProgressBar pG;
    private ArrayAdapter<String> adapter;
    private SQLiteDatabase bancoDados;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pingList = findViewById(R.id.pingList);
        edPing = findViewById(R.id.edPing);
        pG = findViewById(R.id.progressBar);
        handler = new Handler();
        if (queryData().isEmpty())
            CreateConnection();

        if (queryData() != null)
        {
            edPing.setText(queryData());
        }





        edPing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pingList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public void CreateConnection(){

        try{
            bancoDados = openOrCreateDatabase("ips", MODE_PRIVATE, null);
            //CREATE TABLE
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS ping(ip VARCHAR (20))");
            //DELETE TABLE
            //bancoDados.execSQL("DROP TABLE ping");
            //INSERT INTO
            bancoDados.execSQL("DELETE FROM ping");
            bancoDados.execSQL("INSERT INTO ping(ip) VALUES('192.168.100.1')");
            //DOING REQUEST
            //bancoDados.execSQL("SELECT ip FROM ping WHERE ip == 192",null);
            //bancoDados.execSQL("UPDATE ping SET id = '192.168.100.101' WHERE id = '192%' ");
            //cursor = bancoDados.rawQuery("SELECT ip FROM ping", null);



            //int index = cursor.getColumnIndex("ip");

            //cursor.moveToFirst();

            //Log.i("IP: ", cursor.getString(index));

        }catch (SQLException x)
        {

        }
    }

    public int insertData(String data){
        if(!data.isEmpty()){
        try {
            bancoDados = openOrCreateDatabase("ips", MODE_PRIVATE, null);
            bancoDados.execSQL("DELETE FROM ping");
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ping(ip) VALUES('");
            sql.append(data);
            sql.append("')");
            bancoDados.execSQL(sql.toString());
            bancoDados.close();
            return 1;
        }catch (SQLException x)
        {
            bancoDados.close();
            return 0;
        }
        }
        bancoDados.close();
        return 0;
    }

    public String queryData(){
        try{
            bancoDados = openOrCreateDatabase("ips", MODE_PRIVATE, null);
            cursor = bancoDados.rawQuery("SELECT ip FROM ping", null);
            int index = cursor.getColumnIndex("ip");

            cursor.moveToFirst();
            String ip = cursor.getString(index);
            bancoDados.close();
            return ip;
        }
        catch (SQLException x)
        {
            bancoDados.close();
            return "";
        }
    }

    public void onClick(View v){
        pG.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                insertData(edPing.getText().toString());
                if (!exePing().isEmpty()) {

                    adapter = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            exePing());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (exePing().get(1).contains("Unreachable"))
                            {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Statistics")
                                        .setMessage("Destino Inacessivel.")
                                        .show();
                                pG.setVisibility(View.INVISIBLE);
                                pingList.setVisibility(View.INVISIBLE);
                            }
                            else {
                                pingList.setAdapter(adapter);
                                pingList.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Comando Executado com sucesso!", Toast.LENGTH_SHORT).show();
                                pG.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pingList.setVisibility(View.INVISIBLE);
                            pG.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Ip incorreto", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private List<String> exePing() {

        ip = edPing.getText();
        List<String> listFetures = new ArrayList<>();

        try {
            String cmdPing = "ping -c 1 " + ip;
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmdPing);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLines;

            while ((inputLines = in.readLine()) != null) {
                listFetures.add(inputLines);
            }

            return listFetures;

        } catch (Exception x) {
            return null;
        }
    }
}
