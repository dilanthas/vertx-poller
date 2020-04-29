# Poller service created using Vertx
Poller micro service running on the jvm where user can add and delete services for health check.

# Requirements
Java 8

# API endpoints
1. Get all services - Get endpoint
```http://localhost:8080/service```

2. Add new service
````http://localhost:8080/service````

sample request 
````
{
	"name":"Test",
	"url":"www.kry.se"
}
````

3. Delete services
````
http://localhost:8080/service?name=Test
````

#Notes
All the Critical issues are fixed but due to the time limitation some of the requirements from the wish list are not implemented.

1. Simultaneous writes sometimes causes strange behavior : This is partially handled by increasing the number of instances to 10
2. A user (with a different cookie/local storage) should not see the services added by another user - Not implemented



