package se.kry.codetest.migrate;

import io.vertx.core.Vertx;
import se.kry.codetest.DBConnector;

public class DBMigration {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    initSchema(vertx);
  }

  protected static void initSchema(Vertx vertx){
    DBConnector connector = new DBConnector(vertx);

    connector.query("DROP TABLE IF EXISTS service").setHandler(done -> {
      if(done.succeeded()){
        System.out.println("completed db migrations");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });

    connector.query("CREATE TABLE IF NOT EXISTS service (\n" +
            "  id integer PRIMARY KEY AUTOINCREMENT,\n" +
            "  name varchar(128) NOT NULL,\n" +
            "  url varchar(128) NOT NULL,\n" +
            "  created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  last_updated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  status varchar(10) NOT NULL\n" +
            ") ").setHandler(done -> {
      if(done.succeeded()){
        System.out.println("completed db migrations");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });
  }
}
