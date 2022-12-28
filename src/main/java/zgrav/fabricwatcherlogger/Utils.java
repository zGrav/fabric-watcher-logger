package zgrav.fabricwatcherlogger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final String MODID = "zgrav-fabric-watcher-logger";

    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static final String WEBHOOK_URL = "WEBHOOK_URL_HERE";

    public static int sendToDiscord(String content) {
        String payload = String.format("{\"content\": \"" + MODID + " - %s\"}", content);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WEBHOOK_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 204) {
                LOGGER.error("Error sending message to Discord webhook: " + response.statusCode() + " " + response.body());
                return 0;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error sending message to Discord webhook", e);
            return 0;
        }

        return 1;
    }
}
