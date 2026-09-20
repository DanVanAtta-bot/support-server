package org.triplea.services.latest.version;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.triplea.http.client.ServerPaths;
import org.triplea.http.client.latest.version.LatestVersionResponse;

/// Tells a client whether its engine version is current and, if not, how to upgrade.
///
/// Stubbed to always recommend no update: comparing `clientVersion` against the latest
/// engine release is not yet implemented, so the parameter is accepted but unused. This
/// turns the previously unimplemented endpoint into a benign 200 that the engine's update
/// check reads as "up to date".
@Path(ServerPaths.LATEST_VERSION_PATH)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class LatestVersionController {

  @GET
  public LatestVersionResponse fetchLatestVersion(
      @QueryParam("clientVersion") String clientVersion) {
    return LatestVersionResponse.builder()
        .recommendedUpdateAction(LatestVersionResponse.RecommendedAction.NO_UPDATE.name())
        .upgradeMessageHtml("")
        .downloadPageUrl("https://triplea-game.org/download/")
        .build();
  }
}
