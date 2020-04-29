package se.kry.codetest.service;

import com.sun.istack.internal.NotNull;
import com.sun.javaws.exceptions.InvalidArgumentException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import se.kry.codetest.DBConnector;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static se.kry.codetest.service.ServiceStatus.UNKNOWN;

public class DataService {

    public DBConnector dbConnector;

    public DataService(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    /**
     * Add new service to the database
     *
     * @param name
     * @return
     */
    public Future<ResultSet> addNewService(String name, String url) {

        Future<ResultSet> queryResultFuture = Future.future();

        if (url == null || url.length() == 0) {
            queryResultFuture.fail(new IllegalArgumentException("Service url cannot be empty"));
            return queryResultFuture;
        }
        if(!isValidURL(url)){
            queryResultFuture.fail(new IllegalArgumentException(String.format("Invalid URL %s",url)));
            return queryResultFuture;
        }
        // If name not provided set the url as the name
        if (name == null) {
            name = url;
        }
        String addNewServiceQuery = "INSERT INTO service (id,  name,url, status) VALUES (NULL, ?, ?,?)";
        JsonArray params = new JsonArray().add(name).add(url).add(UNKNOWN.name());

        dbConnector.query(addNewServiceQuery, params).setHandler(response -> {

            if (response.failed()) {
                queryResultFuture.fail(response.cause());
            } else {
                queryResultFuture.complete(response.result());
            }
        });
        return queryResultFuture;
    }

    public boolean isValidURL(String url) {

        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }

        return true;
    }

    /**
     * Get all the services from the database
     *
     * @return
     */
    public Future<List<JsonObject>> getAllServices() {
        String addNewServiceQuery = "SELECT * FROM service";

        Future<List<JsonObject>> queryResultFuture = Future.future();

        dbConnector.query(addNewServiceQuery).setHandler(response -> {

            if (response.failed()) {
                queryResultFuture.fail(response.cause());
            } else {
                queryResultFuture.complete(response.result().getRows());
            }
        });
        return queryResultFuture;
    }

    /**
     * Delete service by name
     *
     * @param name
     * @return
     */
    public Future<Void> deleteServiceByName(String name) {

        String deleteSql = "DELETE FROM service WHERE name = ?";
        JsonArray params = new JsonArray().add(name);

        Future<Void> queryResultFuture = Future.future();

        dbConnector.updateQueryWithParam(deleteSql, params).setHandler(response -> {
            if (response.failed()) {
                queryResultFuture.fail(response.cause());
            } else {
                queryResultFuture.complete();
            }
        });


        return queryResultFuture;
    }

    public Future<Void> updateServiceStatus(int id, boolean status) {

        String serviceStatus = status ? ServiceStatus.OK.name() : ServiceStatus.FAIL.name();

        String updateQuery = "UPDATE service set status = ? , last_updated = ? WHERE id = ?";
        JsonArray params = new JsonArray().add(serviceStatus).add(Instant.ofEpochMilli(System.currentTimeMillis())).add(id);
        Future<Void> queryResultFuture = Future.future();

        dbConnector.query(updateQuery, params).setHandler(response -> {
            if (response.failed()) {
                queryResultFuture.fail(response.cause());
            } else {
                queryResultFuture.complete();
            }
        });

        return queryResultFuture;
    }
}
