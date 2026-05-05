package cn.net.miroku;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

// 集成测试 在随机端口启动服务
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("OpenAISDK集成测试")
public class OpenAISDKTest {
    // 注入随机端口
    @LocalServerPort
    private int randomPort;
    private OpenAIClient client;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + randomPort + "/v1";
        client = OpenAIOkHttpClient.builder()
                .baseUrl(baseUrl)
                .apiKey("sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                // 30秒超时
                .timeout(Duration.ofSeconds(30))
                .build();
        System.out.println("initialization is successful.\n baseUrl: " + baseUrl);
    }

    @DisplayName("测试非流式聊天补全")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"deepseek-chat", "doubao-seed-2-0-mini-260215",
            "doubao-1-5-pro-32k-250115"})
    public void testCompletion(String model) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Please introduce yourself in Chinese")
                .model(model)
                .build();
        assertDoesNotThrow(() -> {
            ChatCompletion completion = client.chat().completions().create(params);

            assertThat(completion).isNotNull();
            assertThat(completion.id()).isNotEmpty();
            assertThat(completion.choices()).hasSize(1);
            assertThat(completion.choices().getFirst().message().content()).isNotEmpty();

            System.out.println("\n\nid: " + completion.id());
            System.out.println("content: " + completion.choices().getFirst().message().content());
            System.out.println("\n");
        }, "模型：" + model + "解析出错");
    }

    @DisplayName("测试流式聊天补全")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"deepseek-chat", "doubao-seed-2-0-mini-260215",
            "doubao-1-5-pro-32k-250115"})
    public void testCompletionStream(String model) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("""
                    Please introduce Ultraman Orb in Chinese in a few short sentences
                    """)
                .model(model)
                .build();

        /*assertDoesNotThrow(() -> {

        });*/

        try (StreamResponse<ChatCompletionChunk> streamResponse = client.chat().completions().createStreaming(params)) {
            streamResponse.stream().forEach(chunk -> {
                System.out.println(chunk);
            });
            System.out.println("No more chunks!");
        }
    }
}
