package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import se.kry.codetest.service.DataService;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BackgroundPoller {

    private WebClient webClient;
    private DataService service;

    private int DEFAULT_WEB_PORT = 80;
    private long DEFAULT_TIME_OUT = 5000;

    public BackgroundPoller(DataService service, Vertx vertx) {
        this.service = service;
        webClient = WebClient.create(vertx, new WebClientOptions().setVerifyHost(false).setSsl(false).setTrustAll(true));
    }

    public Future<List<String>> pollServices() {

        service.getAllServices().setHandler(response -> {

            for (JsonObject service : response.result()) {
                String url = service.getString("url");
                int id = service.getInteger("id");

                checkService(service.getString("url")).setHandler(status -> {
                    updateServiceStatus(id, url, status.result());
                });
            }
        });
        return Future.failedFuture("TODO");

    }

    private void updateServiceStatus(int id, String url, Boolean result) {
        service.updateServiceStatus(id, result).setHandler(response -> {

            if (response.succeeded()) {
                System.out.println(String.format("Service updated successfully : %s", url));
            } else {
                System.out.println(String.format("Error updating service : %s", url));

            }
        });
    }

    public Future<Boolean> checkService(String url) {
        Future<Boolean> result = Future.future();
        webClient
                .get( DEFAULT_WEB_PORT,url,"/")
                .timeout(DEFAULT_TIME_OUT)
                .send(response -> {
                    if (response.succeeded()) {
                        result.complete(true);
                    } else {
                        result.complete(false);
                    }
                });

        return result;

    }
}
