package com.fooddeliverytracker;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

@Path("/drivers/")
public class DriverResources {

	DriverService driverService;

	public DriverResources() {

		driverService = DriverService.getInstance();

	}

	@GET
	@Produces("application/json")
	@Path("/")
	public List<Driver> getCurrentDrivers() {
		System.out.println("Current Drivers");
		List<Driver> drivers = driverService.getDrivers();
		return drivers;
	}
	
	
	@GET
	@Produces("application/json")
	@Path("/{driverName}")
	public Driver getDriverById(@PathParam("driverName") String driverName) {
		System.out.println(driverName);
		Driver driver = driverService.getDriverByName(driverName);

		if (driver==null){
			throw new WebApplicationException(404);
		}
		return driver;
	}
	
	
	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/{driverName}")
	public Driver createOrUpdateDriver(@PathParam("driverName") String driverName,
			Driver driver) {
		driverService.saveDriver(driver);
		return driver;
	}
	
	
	
	

	
}
