package com.ilkdeneme.aracyakittakibi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView carListView;
    private Button listCarsButton;
    private ArrayList<String> carList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carListView = findViewById(R.id.car_list_view);
        listCarsButton = findViewById(R.id.list_cars_button);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carList);
        carListView.setAdapter(adapter);

        carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject selectedCarObject = jsonArray.getJSONObject(position);
                    String selectedCarId = selectedCarObject.getString("arac_id");

                    Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                    intent.putExtra("selectedCarId", selectedCarId);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        listCarsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ListCarsTask().execute();
            }
        });
    }
    public void startSecondActivity(View view){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }


    private class ListCarsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://192.168.1.136/api/Arac");
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

                    String apiResponse = response.toString();

                    jsonArray = new JSONArray(apiResponse); // jsonArray tanımlanıyor
                    carList.clear(); // Önceki listeyi temizliyor
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject carObject = jsonArray.getJSONObject(i);
                        String carName = carObject.getString("arac_plaka");
                        carList.add(carName);
                    }

                    // Araç listesini güncelliyor
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
