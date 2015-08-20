package com.github.alfonso_presa.restflow.samples;

import com.apresa.restflow.AbstractBeanFlow;
import com.apresa.restflow.annotations.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity(name="OrderTable")
class Order {
	@StateReference
	public OrderStatus status = OrderStatus.INITIAL;
	
	public String[] products;
	public Float price;
	public String customer;
	public String track;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long id;
}

enum OrderStatus{
	INITIAL,
	PLACED,
	PAYED,
	SENT
}

@Component
@Flow(OrderStatus.class)
@Transition(event = "PLACE", from="INITIAL", to = "PLACED")
@Transition(event = "PAY", from="PLACED", to = "PAYED")
@Transition(event = "SEND", from="PAYED", to = "SENT")
public class OrderFlow extends AbstractBeanFlow<Order> {
	@Guard("PLACE")
	private boolean checkParams(Order order, @EventParam("products") String[] products, @EventParam("customer") String customer){
		return products != null && customer != null;
	}

	@On("PLACE")
	private void fillOrderData(Order order, @EventParam("products") String[] products, @EventParam("customer") String customer){
		order.products = products;
		order.customer = customer;
	}

	@OnState("PLACED")
	private void calculatePrice(Order order){
		order.price = 10f; //TODO: this should be calculated depending on the products and the customer
	}

	@Guard("PAY")
	private boolean checkPayment(Order order, @EventParam("payment") String recipe){
		return recipe != null;
	}

	@Guard("SEND")
	private boolean checkPayment(Order order, @EventParam("tracking") Object track){
		return track != null;
	}

	@On("SEND")
	private void fillOrderTracking(Order order, @EventParam("tracking") String track){
		order.track = track;
	}

}
