package se.kry.codetest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Start {

    public static void main(String[] args) {

        //DeploymentOptions options = new DeploymentOptions().setInstances(10);
        Vertx.vertx().deployVerticle(new MainVerticle());
    }
}
