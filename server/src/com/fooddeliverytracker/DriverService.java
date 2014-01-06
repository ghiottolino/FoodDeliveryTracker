package com.fooddeliverytracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverService {

	private static DriverService instance;

	public Map<String, Driver> driversMap = new HashMap<String, Driver>();

	public static DriverService getInstance() {
		if (instance == null) {
			instance = new DriverService();
		}

		return instance;
	}

	public List<Driver> getDrivers() {
		Collection<Driver> driverCollection = driversMap.values();
		List<Driver> drivers = new ArrayList<Driver>(driverCollection);
		return drivers;
	}

	public Driver getDriverByName(String name) {
		return driversMap.get(name);

	}

	public void saveDriver(Driver driver) {
		driversMap.put(driver.getName(), driver);
	}
}
