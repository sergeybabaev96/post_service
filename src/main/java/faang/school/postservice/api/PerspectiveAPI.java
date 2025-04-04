package faang.school.postservice.api;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PerspectiveAPI {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().create();
    private final String apiKey;
    private final String apiUrl;

    @Autowired
    public PerspectiveAPI(
            @Value("${perspective-api.key}") String apiKey,
            @Value("${perspective-api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public boolean isContentToxic(String text) throws IOException {
        // 1. Проверка входных данных
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        // 2. Формирование запроса
        JsonObject request = new JsonObject();
        request.add("comment", new JsonObject());
        request.getAsJsonObject("comment").addProperty("text", text);

        JsonObject attributes = new JsonObject();
        attributes.add("TOXICITY", new JsonObject());
        request.add("requestedAttributes", attributes);

        RequestBody body = RequestBody.create(
                gson.toJson(request),
                MediaType.get("application/json; charset=utf-8")
        );

        // 3. Формирование URL (исправленная часть)
        HttpUrl url = HttpUrl.get(apiUrl)  // Используем HttpUrl.get() вместо parse()
                .newBuilder()
                .addQueryParameter("key", apiKey)
                .build();

        Request httpRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 4. Отправка запроса
        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                throw new IOException(String.format(
                        "API Error %d: %s\nRequest URL: %s\nRequest Body: %s",
                        response.code(),
                        errorBody,
                        httpRequest.url(),
                        gson.toJson(request)
                ));
            }

            // 5. Обработка ответа
            JsonObject responseJson = gson.fromJson(
                    response.body().charStream(),
                    JsonObject.class
            );

            return responseJson.getAsJsonObject("attributeScores")
                    .getAsJsonObject("TOXICITY")
                    .getAsJsonObject("summaryScore")
                    .get("value").getAsDouble() > 0.7;
        }
    }
}