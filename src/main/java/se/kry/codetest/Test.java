package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

public class Test {

    public static void main(String args[]){
        Vertx vertx = Vertx.vertx();

        WebClient client = WebClient.create(vertx,

        new WebClientOptions().setVerifyHost(false).setSsl(false).setTrustAll(true));


        client.get("www.facebook.com")
                .send(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("Got HTTP response body");

                    } else {
                        ar.cause().printStackTrace();
                    }
                });
    }
}
