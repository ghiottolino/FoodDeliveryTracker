package com.fooddeliverytracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

	private static OrderService instance;

	public Map<String, Order> ordersMap = new HashMap<String, Order>();

	public static OrderService getInstance() {
		if (instance == null) {
			instance = new OrderService();
			// Order order = new Order();
			// order.setOrderId("301222");
			// order.setStatus("On its way");
			// Position position = new Position();
			// position.setLatitude(47.670417);
			// position.setLongitude(9.155700);
			// order.setPosition(position);
			// order.setDeliveryAddress("Seilerstr. 7 Konstanz");
			// order.setDeliveryTime(new Date());
			//
			// instance.ordersMap.put(order.getOrderId(), order);
		}

		return instance;
	}

	public List<Order> getOrders() {
		Collection<Order> orderCollection = ordersMap.values();
		List<Order> orders = new ArrayList<Order>(orderCollection);
		return orders;
	}

	public Order getOrderById(String orderId) {
		return ordersMap.get(orderId);

	}

	public void saveOrder(Order order) {
		ordersMap.put(order.getOrderId(), order);
	}
}
