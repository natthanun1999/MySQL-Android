package com.mobile.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    JSONArray data;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onUpdate(position);
            }
        });

        GetMethodTask task = new GetMethodTask();
        task.execute("http://192.168.132.2/mobile/index.php");
    }

    public void onInsert(View v) {
        Intent intent = new Intent(this, InsertActivity.class);
        startActivity(intent);
    }

    public void onUpdate(int index) {
        JSONObject reader = data.optJSONObject(index);

        Log.e("OBJECT", reader.toString());

        String id = reader.optString("Id");
        String username = reader.optString("username");
        String pass = reader.optString("pass");

        Intent intent = new Intent(this, UpdateActivity.class);

        intent.putExtra("Id", id);
        intent.putExtra("username", username);
        intent.putExtra("password", pass);

        startActivity(intent);
    }

    public void onRefresh(View v) {
        GetMethodTask task = new GetMethodTask();
        task.execute("http://192.168.132.2/mobile/index.php");

        Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_LONG).show();
    }

    private class GetMethodTask extends AsyncTask<String, Void, String> {
        private String readStream(InputStream inputStream) {
            BufferedReader reader = null;
            StringBuilder sb = new StringBuilder();

            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";

                while((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        }

        private String downloadText(String strUrl) {
            String strResult = "";

            try {
                URL url = new URL(strUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                strResult = readStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return strResult;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadText(urls[0]);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String[] values = null;

            try {
                data = new JSONArray(result);
                values = new String[data.length()];

                for (int n = 0; n < data.length(); n++) {
                    JSONObject reader = data.optJSONObject(n);
                    values[n] = reader.optString("username");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            listView.setAdapter(adapter);
        }
    }
}
