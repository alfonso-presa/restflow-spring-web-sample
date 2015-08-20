Sample web project for [RestFlow](https://github.com/alfonso-presa/restflow) project.

It shows how to use RestFlow along with Spring Web MVC and JPA to create a RESTful 
stateless resource with it's associated workflow.

## Running

Clone the repo and run

```
gradle bootRun
```

Ofcourse you need gradle in place to do this :-).

## Sample requests

To create a new Order run

```
curl --data "products=1&products=2&customer=Alfonso" http://localhost:8080/orders
```

Get all the orders with

```
curl  http://localhost:8080/orders
```

Get a single order

```
curl  http://localhost:8080/orders/1
```

Pay that order by running

```
curl --data "recipe=I_PAYED" http://localhost:8080/orders/1/actions/pay
curl  http://localhost:8080/orders/1
```

To mark the order as delivered run

```
curl --data "tracking=UPS-1321341" http://localhost:8080/orders/1/actions/send
```

You will notice that if you run the actions in a different order or if you don't provide 
the correct parameters you will get errors. That's controlled by RestFlow itself painlessly.
