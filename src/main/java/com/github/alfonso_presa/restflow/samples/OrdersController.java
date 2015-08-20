package com.github.alfonso_presa.restflow.samples;

import com.apresa.restflow.fsm.Event;
import com.apresa.restflow.fsm.StateMachineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdersController {

    @Autowired
    OrderRepository repository;

    @Autowired
    OrderFlow flow;

    @RequestMapping(value="/orders", produces="application/json", method = RequestMethod.GET)
    public Iterable<Order> getOrders() {
        return repository.findAll();
    }

    @RequestMapping(value="/orders/{id}", produces="application/json", method = RequestMethod.GET)
    public Order getOrder(@PathVariable("id") Long id) {
        return repository.findOne(id);
    }

    @RequestMapping(value="/orders/{id}/actions/pay", produces="application/json", method = RequestMethod.POST)
    public ResponseEntity<Order> payOrder(@PathVariable("id") Long id, @RequestParam("recipe") String recipe) {
        Order order = repository.findOne(id);
        Event ev = Event.build("PAY").param("payment", recipe);
        return applyEventAndSave(order, ev);
    }

    @RequestMapping(value="/orders/{id}/actions/send", produces="application/json", method = RequestMethod.POST)
    public ResponseEntity<Order> sendOrder(@PathVariable("id") Long id, @RequestParam("tracking") String tracking) {
        Order order = repository.findOne(id);
        Event ev = Event.build("SEND").param("tracking", tracking);
        return applyEventAndSave(order, ev);
    }
    
    @RequestMapping(value="/orders", produces="application/json", method = RequestMethod.POST)
     public ResponseEntity<Order> createOrder(@RequestParam("products") String[] prods, @RequestParam("customer") String customer ) {
        Order order = new Order();
        Event ev = Event.build("PLACE").param("products", prods).param("customer", customer);
        return applyEventAndSave(order, ev);
    }

    private ResponseEntity<Order> applyEventAndSave(Order order, Event ev) {
        try {
            if(flow.raise(ev, order)) {
                repository.save(order);
                return new ResponseEntity<>(order, HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (StateMachineException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
