package org.triplea.services.latest.version;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import com.google.gson.Gson;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.triplea.IntegTestExtension;
import org.triplea.http.client.ServerPaths;
import org.triplea.http.client.latest.version.LatestVersionResponse;

@QuarkusTest
@ExtendWith(IntegTestExtension.class)
class LatestVersionControllerIntegrationTest {
  private static final Gson GSON = new Gson();

  private HttpClient httpClient;
  private String baseUrl;

  @ConfigProperty(name = "quarkus.http.test-port", defaultValue = "8081")
  int testPort;

  @BeforeEach
  void setUp() {
    httpClient = HttpClient.newHttpClient();
    baseUrl = "http://localhost:" + testPort;
  }

  /// Exercises the stubbed endpoint over real HTTP:
  ///
  /// 1. the path resolves — a 200, not the 404 the unimplemented endpoint used to return;
  /// 2. the body deserializes into a `LatestVersionResponse` recommending `NO_UPDATE`;
  /// 3. both non-action fields are populated, honoring their non-null contract — guarding
  ///    the future update-recommended path against emitting a null the client is told never
  ///    to see.
  @Test
  void servesNoUpdateResponse() throws IOException, InterruptedException {
    var httpRequest =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    baseUrl + "/" + ServerPaths.LATEST_VERSION_PATH + "?clientVersion=2.6.0.0"))
            .GET()
            .build();

    var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode(), is(200));

    var latestVersionResponse = GSON.fromJson(response.body(), LatestVersionResponse.class);
    assertThat(latestVersionResponse.getRecommendedUpdateAction(), is("NO_UPDATE"));
    assertThat(latestVersionResponse.getDownloadPageUrl(), is(notNullValue()));
    assertThat(latestVersionResponse.getUpgradeMessageHtml(), is(notNullValue()));
  }
}
