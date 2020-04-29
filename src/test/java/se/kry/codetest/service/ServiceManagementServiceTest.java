package se.kry.codetest.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.ResultSet;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import se.kry.codetest.DBConnector;

@ExtendWith(VertxExtension.class)
public class ServiceManagementServiceTest {

    private ServiceManagementService service;
    private DBConnector dbConnector;

    @BeforeEach
    public void init(){

        dbConnector = Mockito.mock(DBConnector.class);
        service = new ServiceManagementService(dbConnector);
    }

    @Test
    public void shouldFailWhenURLIsNullWhenAddingService(Vertx vertx, VertxTestContext testContext){

        // Given
        String name = "Test";
        String url = null;

        // When
        Future<ResultSet> result = service.addNewService(name,url);

        // Then
        Assertions.assertTrue(result.failed());
        testContext.completeNow();
    }
}
