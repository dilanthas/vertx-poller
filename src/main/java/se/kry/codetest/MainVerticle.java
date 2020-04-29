package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.kry.codetest.service.DataService;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

    private DBConnector connector;
    private DataService dataService;
    private BackgroundPoller poller ;

    private void init(){
        connector = new DBConnector(vertx);
        dataService = new DataService(connector);
        poller = new BackgroundPoller(dataService,vertx);
    }

    @Override
    public void start(Future<Void> startFuture) {

        // Init dependencies
        init();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices());

        setRoutes(router);

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        System.out.println("KRY code test service started");
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }

    private void setRoutes(Router router) {
        router.route("/*").handler(StaticHandler.create());

        setGetRoutes(router);

        setPostRoutes(router);

        setDeleteRoutes(router);

    }

    private void setGetRoutes(Router router) {

        router.get("/service").handler(req -> {

            dataService.getAllServices().setHandler(response ->{
                if(response.failed()){
                    req.response().setStatusCode(500).end("Error adding new service");
                }else{
                    req.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonArray(response.result()).encode());
                }
            });

        });
    }

    private void setPostRoutes(Router router){

        router.post("/service").handler(req -> {
            JsonObject jsonBody = req.getBodyAsJson();
            dataService.addNewService(jsonBody.getString("name"),jsonBody.getString("url")).setHandler(response->{
                if(response.failed()){
                    req.response().setStatusCode(500).end(new JsonObject().put("error",response.cause().getMessage()).encode());
                }else{
                    req.response().setStatusCode(200).end("Service successfully added");
                }
            });

        });

    }

    private void setDeleteRoutes(Router router){

        router.delete("/service").handler(req -> {
            List<String> serviceNames = req.queryParam("name");
            if(serviceNames == null || serviceNames.size() == 0){
                req.response().setStatusCode(400).end("Service name not provided");
            }else{
                dataService.deleteServiceByName(serviceNames.get(0)).setHandler(response->{
                    if(response.failed()){
                        req.response().setStatusCode(400).end(new JsonObject().put("error",response.cause().getMessage()).encode());
                    }else{
                        req.response().setStatusCode(200).end("Service deleted successfully ");
                    }
                });
            }


        });

    }

}



