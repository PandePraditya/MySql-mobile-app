package com.example.todolist;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText txtNama, txtNim;
    private Button btnInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNama = findViewById(R.id.txtNama);
        txtNim = findViewById(R.id.txtNim);
        btnInsert = findViewById(R.id.btnSubmit);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = txtNama.getText().toString().trim();
                String nim = txtNim.getText().toString().trim();

                if (nama.isEmpty() || nim.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Mohon masukkan username dan password", Toast.LENGTH_SHORT).show();
                } else {
                    InsertDataAsyncTask insertDataAsyncTask = new InsertDataAsyncTask();
                    insertDataAsyncTask.execute(nama, nim);
                }
            }
        });
    }

    private class InsertDataAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String nama = params[0];
            String nim = params[1];

            int responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR; // Default nya internal server error

            try {
                // pakai 10.0.2.2 untuk local dari emulator, localhost tidak mau
                URL url = new URL("http://10.0.2.2/andro_register/script_register.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST"); // method="POST"

                // Untuk mengirim informasi ke server
                urlConnection.setDoOutput(true);
                try (OutputStream outputStream = urlConnection.getOutputStream();
                     BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))) {

                    // Format data yang akan dikirim
                    String postData = "nama=" + nama + "&nim=" + nim;
                    bufferedWriter.write(postData);
                    bufferedWriter.flush();
                }

                responseCode = urlConnection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace(); // Exception artinya selain
                // Menampilkan kesalahan yang terjadi saat 'try' tidak berhasil
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Data berhasil dikirim
                Toast.makeText(MainActivity.this, "Data berhasil dimasukkan", Toast.LENGTH_SHORT).show();
            } else {
                // Tangani error
                String errorMessage = "Error: ";
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    errorMessage += "Internal Server Error"; // gunakan += agar bisa gabung string, tanda = tidak bisa
                } else {
                    errorMessage += responseCode;
                }
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
