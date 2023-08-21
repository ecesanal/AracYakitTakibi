package com.ilkdeneme.aracyakittakibi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity2 extends AppCompatActivity {

    private EditText plakaEditText;
    private Button veriCekButton;
    private TextView baslangicTarihiTextView;
    private TextView bitisTarihiTextView;
    private TextView toplamYakitAlimiTextView;
    private TextView ortalamaTuketimTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setTitle("Aylık/Yıllık Raporlama");

        plakaEditText = findViewById(R.id.plakaEditText);
        veriCekButton = findViewById(R.id.veriCekButton);
        baslangicTarihiTextView = findViewById(R.id.baslangicTarihiTextView);
        bitisTarihiTextView = findViewById(R.id.bitisTarihiTextView);
        toplamYakitAlimiTextView = findViewById(R.id.toplamYakitAlimiTextView);
        ortalamaTuketimTextView = findViewById(R.id.ortalamaTuketimTextView);

        veriCekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aracPlaka = plakaEditText.getText().toString();
                if (!aracPlaka.isEmpty()) {
                    new FetchCarDataAsyncTask().execute(aracPlaka);
                } else {
                    clearTextViews();
                    baslangicTarihiTextView.setText("Lütfen araç plakasını giriniz.");
                }
            }
        });
    }

    private void clearTextViews() {
        baslangicTarihiTextView.setText("");
        bitisTarihiTextView.setText("");
        toplamYakitAlimiTextView.setText("");
        ortalamaTuketimTextView.setText("");
    }

    private class FetchCarDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL("http://192.168.1.136/api/AylikRapor/" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                result = response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = "Bağlantı hatası: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (result.startsWith("Bağlantı hatası")) {
                    clearTextViews();
                    baslangicTarihiTextView.setText(result);
                } else {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() > 0) {
                        JSONObject carDetailObject = jsonArray.getJSONObject(0);

                        String baslangicTarihi = carDetailObject.getString("baslangicTarihi");
                        String bitisTarihi = carDetailObject.getString("bitisTarihi");
                        double toplamYakitAlimi = carDetailObject.getDouble("toplamYakitAlimi");
                        double ortalamaTuketim = carDetailObject.getDouble("ortalamaTuketim");

                        baslangicTarihiTextView.setText("Başlangıç Tarihi: " + baslangicTarihi);
                        bitisTarihiTextView.setText("Bitiş Tarihi: " + bitisTarihi);
                        toplamYakitAlimiTextView.setText("Toplam Yakıt Alımı: " + toplamYakitAlimi);
                        ortalamaTuketimTextView.setText("Ortalama Tüketim: " + ortalamaTuketim);
                    } else {
                        clearTextViews();
                        baslangicTarihiTextView.setText("Veri bulunamadı.");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                clearTextViews();
                baslangicTarihiTextView.setText("JSON hatası: " + e.getMessage());
            }
        }
    }
}
