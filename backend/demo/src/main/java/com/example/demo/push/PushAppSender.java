package com.example.demo.push;

import com.example.demo.common.exception.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushAppSender implements PushSender {

    @Value("${push.api.base-url}")
    private String baseUrl;

    public void send(PushMessage message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(message);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json; charset=utf-8")
            );

            Request apiRequest = new Request.Builder()
                .url(baseUrl)
                .post(body)
                .build();

            try (Response response = client.newCall(apiRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    log.info("푸시전송 성공!");
                } else {
                    throw new ApplicationException("API 호출 실패: " + response.code());
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
