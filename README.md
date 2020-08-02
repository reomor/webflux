# [Build Reactive RESTFUL APIs using Spring Boot/WebFlux](https://www.udemy.com/course/build-reactive-restful-apis-using-spring-boot-webflux/)

Практические задание по курсу

```shell script
curl http://localhost:8081/client/items | json_pp
curl http://localhost:8081/client/items/5f26962b20d7047cb618e6aa | json_pp
curl -d '{"id":null,"description":"desc","price":1.99}' -H "Content-Type: application/json" -X POST http://localhost:8081/client/items | json_pp
curl -d '{"id":null,"description":"desc","price":1.99}' -H "Content-Type: application/json" -X PUT http://localhost:8081/client/items/2a5b37b2-de9c-4f2b-babd-bd0132033438 | json_pp
curl http://localhost:8081/client/items/5f26962b20d7047cb618e6aa -X DELETE
```

> spring-boot 2.3.1 <br>
> webflux <br>
> mongodb-reactive <br>