package cn.net.miroku;

import cn.net.miroku.tool.JsonUtils;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

// ✅ 移除 @SpringBootTest，变成纯 Java 测试类
@DisplayName("OpenAISDK 外部集成测试（Docker 环境）")
public class OpenAISDKExternalTest {

    // ✅ 固定地址，不再注入端口
    private static final String BASE_URL = "http://127.0.0.1:8080/v1";
    private static final String LOGIN_URL = "http://127.0.0.1:8080/login";

    private OkHttpClient okHttpClient;
    private OpenAIClient client;

    @BeforeEach
    public void setUp() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(10))
                .connectionPool(new ConnectionPool(16, 5, java.util.concurrent.TimeUnit.MINUTES))
                .build();

        String apiKey = getApiKey();

        client = OpenAIOkHttpClient.builder()
                .baseUrl(BASE_URL)
                .timeout(Duration.ofSeconds(30))
                .apiKey(apiKey)
                .build();

        System.out.println("✅ 初始化成功 | baseUrl: " + BASE_URL);
    }

    /**
     * 通过 /login 接口获取 API Token（模拟真实调用流程）
     */
    private String getApiKey() {
        Request req = new Request.Builder()
                .url(LOGIN_URL)
                .post(RequestBody.create(new byte[0], MediaType.get("application/json")))
                .build();

        try (Response resp = okHttpClient.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException("登录请求失败: " + resp.code());
            }
            assert resp.body() != null;
            JsonNode json = JsonUtils.toJsonNode(resp.body().string());
            String token = json.path("data").path("tokenValue").asString();
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("获取 token 失败: " + json.toPrettyString());
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("登录流程异常", e);
        }
    }

    @DisplayName("测试非流式聊天补全")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "deepseek-chat",
            "doubao-seed-2-0-mini-260215",
            "doubao-1-5-pro-32k-250115",
            "qwen3.6-flash"
    })
    public void testCompletion(String model) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Please introduce yourself in Chinese")
                .model(model)
                .build();

        assertDoesNotThrow(() -> {
            ChatCompletion completion = client.chat().completions().create(params);

            assertThat(completion).isNotNull();
            assertThat(completion.id()).isNotEmpty().startsWith("Miroku");
            assertThat(completion.choices()).hasSize(1);
            assertThat(completion.choices().getFirst().message().content()).isNotEmpty();

            System.out.println("\n📦 非流式响应 | model: " + model);
            System.out.println("   id: " + completion.id());
            System.out.println("   content: " + completion.choices().getFirst().message().content());
            System.out.println();
        }, "模型 [" + model + "] 非流式调用异常");
    }

    @DisplayName("测试流式聊天补全")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "deepseek-chat",
            "doubao-seed-2-0-mini-260215",
            "doubao-1-5-pro-32k-250115",
            "qwen3.6-flash"
    })
    public void testCompletionStream(String model) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Please introduce Ultraman Orb in Chinese in a few short sentences")
                .model(model)
                .build();

        assertDoesNotThrow(() -> {
            try (StreamResponse<ChatCompletionChunk> streamResponse =
                         client.chat().completions().createStreaming(params)) {

                AtomicReference<String> id = new AtomicReference<>();
                AtomicInteger chunkCount = new AtomicInteger(0);
                StringBuilder content = new StringBuilder();

                streamResponse.stream().forEach(chunk -> {
                    int count = chunkCount.getAndIncrement();
                    if (count == 0) {
                        id.set(chunk.id());
                    }
                    content.append(chunk.choices().getFirst().delta().content().orElse(""));
                });

                String result = content.toString();
                String finalId = id.get();

                assertThat(finalId).isNotEmpty().startsWith("Miroku");
                assertThat(result).isNotEmpty();
                assertThat(chunkCount.get()).isGreaterThan(0);

                System.out.println("\n🌊 流式响应 | model: " + model);
                System.out.println("   id: " + finalId);
                System.out.println("   content: " + result.replaceAll("\n", "\n   "));
                System.out.println("   chunks: " + chunkCount.get());
                System.out.println();

            }
        }, "模型 [" + model + "] 流式调用异常");
    }
}