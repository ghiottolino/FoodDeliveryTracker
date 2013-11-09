package com.fooddeliverytracker;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

@Path("/")
public class OrderResources {

	OrderService orderService;

	public OrderResources() {

		orderService = OrderService.getInstance();

	}

	@GET
	@Produces("application/json")
	@Path("/orders")
	public List<Order> getCurrentOrders() {
		System.out.println("Current Orders");
		List<Order> orders = orderService.getOrders();
		return orders;
	}
	
	
	@GET
	@Produces("application/json")
	@Path("/orders/{orderId}")
	public Order getOrderById(@PathParam("orderId") String orderId) {
		System.out.println(orderId);
		Order order = orderService.getOrderById(orderId);

		if (order==null){
			throw new WebApplicationException(404);
		}
		return order;
	}
	
	
	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/orders/{orderId}")
	public Order createOrUpdateOrder(@PathParam("orderId") String orderId,
			Order order) {
		orderService.saveOrder(order);

		return order;
	}
	
	
	
	

	
}
