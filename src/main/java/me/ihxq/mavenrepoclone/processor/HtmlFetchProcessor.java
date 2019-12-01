package me.ihxq.mavenrepoclone.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xq.h
 * 2019/12/2 00:11
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlFetchProcessor implements Processor<String, String> {
    private OkHttpClient client = new OkHttpClient();

    private static final HtmlFetchProcessor INSTANCE = new HtmlFetchProcessor();

    public static HtmlFetchProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    public String process(String url) throws IOException {
        try {
            return this.fetch(url);
        } catch (IOException e) {
            try {
                return this.fetch(url);
            } catch (IOException ex) {
                throw e;
            }
        }
    }

    private String fetch(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
