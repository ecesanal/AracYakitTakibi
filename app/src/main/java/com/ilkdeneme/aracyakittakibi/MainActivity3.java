package com.ilkdeneme.aracyakittakibi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity3 extends AppCompatActivity {

    private Button araclisteleButton;
    private TextView aracBilgileriTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        araclisteleButton = findViewById(R.id.araclisteleButton);
        aracBilgileriTextView = findViewById(R.id.aracBilgileriTextView);

        araclisteleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int arac_id = 1;
                new RetrieveCarInfoTask().execute(arac_id);
            }
        });
    }

    private class RetrieveCarInfoTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int arac_id = params[0];
            String responseMessage = "";

            try {
                URL url = new URL("http://192.168.1.136/api/Arac/" + arac_id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    responseMessage = response.toString();
                } else {
                    responseMessage = "HTTP Bağlantı Hatası: " + responseCode;
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                responseMessage = "Bağlantı Hatası: " + e.getMessage();
            }

            return responseMessage;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject carObject = new JSONObject(result);
                String plaka = carObject.getString("arac_plaka");
                String aciklama = carObject.getString("arac_aciklama");
                String yakitTuru = carObject.getString("arac_yakit_turu");

                StringBuilder carInfo = new StringBuilder();
                carInfo.append("Plaka: ").append(plaka).append("\n")
                        .append("Açıklama: ").append(aciklama).append("\n")
                        .append("Yakıt Türü: ").append(yakitTuru).append("\n\n");

                aracBilgileriTextView.setText(carInfo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
