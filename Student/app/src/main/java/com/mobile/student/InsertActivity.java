package com.mobile.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class InsertActivity extends AppCompatActivity {

    EditText userBox, passBox;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        userBox = findViewById(R.id.userBox);
        passBox = findViewById(R.id.passBox);
    }

    public void onInsert(View v) {
        user = userBox.getText().toString();
        pass = passBox.getText().toString();

        PostMethodTask task = new PostMethodTask();
        task.execute("http://192.168.132.2/mobile/insert.php");
    }

    private class PostMethodTask extends AsyncTask<String, Void, String> {

        private String getPostDataString(JSONObject obj) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = obj.keys();

            while (itr.hasNext()) {
                String key = itr.next();
                Object value = obj.get(key);

                if (first) first = false;
                else result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }

            return result.toString();
        }

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

        private void writeStream(OutputStream outputStream) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("username", user);
                    obj.put("password", pass);

                    writer.write(getPostDataString(obj));

                    Log.e("JSON Input", obj.toString());

                    writer.flush();
                    writer.close();
                    outputStream.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String downloadText(String strUrl) {
            String strResult = "";

            try {
                URL url = new URL(strUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setReadTimeout(15000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);

                writeStream(con.getOutputStream());

                con.connect();
                int responseCode = con.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    strResult = readStream(con.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
            super.onPostExecute(result);

            Log.e("Response", " : " + result);
            Toast.makeText(getApplicationContext(), "Response : " + result, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(InsertActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
