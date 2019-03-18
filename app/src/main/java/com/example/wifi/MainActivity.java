package com.example.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    String ssid, ip, mac, strength, signalLevel, speed, frequency;
    TextView details,txtfile,database;
    SQLiteDatabase db;
    Button analyze,checkHistory;
    String signal = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("WifiDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS details(ssid VARCHAR, strength VARCHAR, signalLevel VARCHAR, signal VARCHAR, speed VARCHAR, frequency VARCHAR, ip VARCHAR, mac VARCHAR);");

        analyze = (Button) findViewById(R.id.button);
        analyze.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayDetails(v);
            }
        });

        checkHistory = (Button) findViewById(R.id.button2);
//        txtfile=(TextView)findViewById(R.id.textView4);
        details = (TextView) findViewById(R.id.textView);
        //final Intent i = new Intent(MainActivity.this, HistoryActivity.class);

        checkHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                display(db);
//                String text= (String) details.getText();
//                try {
//                    InputStream inputStream = getAssets().open("myfile.txt");
//                    int size=inputStream.available();
//                    byte buffer[]=new byte[size];
//                    inputStream.read(buffer);
//                    text=new String(buffer);
//                } catch(IOException e){
//                    e.printStackTrace();
//                }
//                txtfile.setText(text);
//                Toast.makeText(getApplicationContext(),"The file read operation is finished successfully",Toast.LENGTH_SHORT).show();
            }
        });

    }
    @SuppressLint("SetTextI18n")
    public void displayDetails(View view) {

        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        wifiInfo.getSSID();
        details = (TextView)findViewById(R.id.textView);

        int signal_strength = wifiInfo.getRssi();

        if (signal_strength > -50) {
            signal = "Excellent";
        } else if (signal_strength <  -50 && signal_strength > -60) {
            signal = "Good";
        } else if (signal_strength < -60 && signal_strength > -70) {
            signal = "Fair";
        } else if (signal_strength < -70 && signal_strength > -100) {
            signal = "Weak";
        }

        ssid = wifiInfo.getSSID();
        strength = Integer.toString(wifiInfo.getRssi());
        signalLevel = Integer.toString(WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5));
        speed = Integer.toString(wifiInfo.getLinkSpeed());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            frequency = Float.toString((float) wifiInfo.getFrequency() / 1000);
        }
        ip = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        mac = wifiInfo.getMacAddress();

        if (signal != null) {
            String info = "SSID: " + ssid + "\nStrength: " + strength + "dBm" + "\nSignal Level: " + signalLevel + "/5" + "\nSignal Strength: " + signal + "\nSpeed: " + speed + "Mbps" + "\nFrequency: " + frequency + "\nIP Address: " + ip + "\nMAC Address: " + mac + "GHz" + "\nHidden SSID: " + wifiInfo.getHiddenSSID();
            storeInDB(ssid, strength, signalLevel, signal, speed, frequency, ip, mac);
            details.setText(info);

        } else
            details.setText("No WiFi");

        db.close();
    }

        public void storeInDB(String ssid, String strength, String signalLevel, String signal, String speed, String frequency, String ip, String mac) {
           db.execSQL("INSERT INTO details VALUES('" + ssid + "','" + strength + "','" + signalLevel + "','" + signal + "','" + speed + "','" + frequency + "','" + ip + "','" + mac + "');");
           Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
    }
    StringBuffer buffer=new StringBuffer();
    public void display(SQLiteDatabase db){

        database = (TextView) findViewById(R.id.textView4);
        database.setMovementMethod(new ScrollingMovementMethod());

        Cursor c=db.rawQuery("SELECT ssid FROM details", null);
        if(c.getCount()!=0)
        {
            c.moveToFirst();
            while (c.moveToNext())
                {
                    buffer.append("ID: ").append(c.getInt(0)).append("\t\t");
                    buffer.append("SSID: ").append(c.getString(1)).append("\t\t");
                    buffer.append("Strength: ").append(c.getInt(2)).append("\t\t");
//                    buffer.append("Strength"+strength);
//                    buffer.append("SignalLevel"+signalLevel);
//                    buffer.append("Speed"+speed);
//                    buffer.append("Frequency"+frequency);
//                    buffer.append("IP"+ip);
                }


            database.setText(buffer);
            Toast.makeText(getApplicationContext(), "Successfully Saved",Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(getApplicationContext(), "No records!",Toast.LENGTH_SHORT).show();
        }
    }

}
