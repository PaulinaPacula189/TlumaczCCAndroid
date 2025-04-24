package com.example.tlumaczccandroid;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText inputEditText;
    private Button translateButton;
    private TextView translatedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEditText = findViewById(R.id.inputEditText);
        translateButton = findViewById(R.id.translateButton);
        translatedTextView = findViewById(R.id.translatedTextView);

        // Umożliwiamy działanie sieci na głównym wątku (do testów!)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        translateButton.setOnClickListener(v -> {
            String textToTranslate = inputEditText.getText().toString();

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("q", textToTranslate)
                    .add("source", "pl")
                    .add("target", "en")
                    .add("format", "text")
                    .build();

            Request request = new Request.Builder()
                    .url("https://libretranslate.de/translate")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String translated = responseBody.split("\"translatedText\":\"")[1].split("\"")[0];
                    runOnUiThread(() -> translatedTextView.setText(translated));
                } else {
                    runOnUiThread(() -> translatedTextView.setText("Błąd: " + response.code()));
                }
            } catch (IOException e) {
                Log.e("TranslateError", "Błąd tłumaczenia", e);
                runOnUiThread(() -> translatedTextView.setText("Błąd: " + e.getMessage()));
            }
        });
    }
}
