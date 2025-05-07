package com.tlumaczccandroid;



import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;
import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    private EditText editTextInput;
    private TextView textViewResult;
    private static final String API_KEY = "AIzaSyDGvcG-hUqXUBz1O9IH5uiGZOny3X7t3iU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.editTextInput);
        textViewResult = findViewById(R.id.textViewResult);
        Button buttonTranslate = findViewById(R.id.buttonTranslate);

        buttonTranslate.setOnClickListener(view -> {
            String text = editTextInput.getText().toString().trim();
            if (!text.isEmpty()) {
                translateText(text);
            } else {
                Toast.makeText(MainActivity.this, "Wpisz tekst do przetłumaczenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateText(String text) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://translation.googleapis.com/language/translate/v2"))
                .newBuilder()
                .addQueryParameter("q", text)
                .addQueryParameter("target", "en") // Możesz zmienić "en" na inny język docelowy
                .addQueryParameter("format", "text")
                .addQueryParameter("key", API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> textViewResult.setText("Błąd połączenia: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray translations = jsonObject
                                .getJSONObject("data")
                                .getJSONArray("translations");
                        String translatedText = translations.getJSONObject(0).getString("translatedText");

                        runOnUiThread(() -> textViewResult.setText(translatedText));
                    } catch (Exception e) {
                        runOnUiThread(() -> textViewResult.setText("Błąd  odpowiedzi"));
                    }
                } else {
                    runOnUiThread(() -> textViewResult.setText("Błąd: " + response.message()));
                }
            }
        });
    }
}
