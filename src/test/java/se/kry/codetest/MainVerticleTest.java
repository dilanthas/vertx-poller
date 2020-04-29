package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jdk.nashorn.internal.ir.LiteralNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class MainVerticleTest {

    Vertx vertx;

    @BeforeEach
    void prepare() {
        vertx = Vertx.vertx(new VertxOptions()
                .setMaxEventLoopExecuteTime(1000)
                .setPreferNativeTransport(true)
                .setFileResolverCachingEnabled(true));
    }

    @Test
    public void start_http_server() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();

        Vertx vertx = Vertx.vertx();
        vertx.createHttpServer()
                .requestHandler(req -> req.response().end())
                .listen(16969, testContext.completing());

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS)).isTrue();
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    public void shouldLaunchServer(VertxTestContext testContext) {

        // Given
        WebClient webClient = WebClient.create(vertx);
        Checkpoint deploymentCheckpoint = testContext.checkpoint();
        Checkpoint requestCheckpoint = testContext.checkpoint(10);
        String welcomeText = "Welcome to KRY";

        // When
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> {
            deploymentCheckpoint.flag();

            webClient.get(8080, "localhost", "/")
                    .as(BodyCodec.string())
                    .send(testContext.succeeding(resp -> {
                        testContext.verify(() -> {

                            // Then
                            assertThat(resp.statusCode()).isEqualTo(200);
                            assertThat(resp.body()).contains(welcomeText);
                            testContext.completeNow();
                        });
                    }));

        }));
    }

    @AfterEach
    void cleanup() {
        vertx.close();
    }

}
