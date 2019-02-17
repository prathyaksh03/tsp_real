package com.real.vrp.drone;

import java.util.Collection;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.real.vrp.drone.util.SolutionPrinter;
import com.real.vrp.drone.util.SolutionPrinter.Print;

/**
 * Vehicle routing problem for real
 *
 */
public class App {
	public static void main(String[] args) {
		/*
		 * get a vehicle type-builder and build a type with the typeId "vehicleType" and
		 * a capacity of 2
		 */
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("drone")
				.addCapacityDimension(0, 2);
		// Converted km per hour into meter per second
		vehicleTypeBuilder.setMaxVelocity(16.6667);
		VehicleType vehicleType = vehicleTypeBuilder.build();

		/*
		 * get a vehicle-builder and build a vehicle located at
		 * "METRO SYSTEMS GmbH, Metro-Straße 12, 40235 Dusseldorf, Germany" with type
		 * "drone"
		 */
		Builder vehicleBuilder = VehicleImpl.Builder.newInstance("Metrostrasse 12, 40235 Düsseldorf ");
		vehicleBuilder.setStartLocation(loc(Coordinate.newInstance(51.2351091, 6.8255326)));
		vehicleBuilder.setType(vehicleType);
		VehicleImpl vehicle1 = vehicleBuilder.build();

		/*
		 * get a vehicle-builder and build a vehicle located at
		 * "Am Albertussee 1, 40549 Düsseldorf" with type "drone"
		 */
		Builder vehicleBuilder2 = VehicleImpl.Builder.newInstance("Am Albertussee 1, 40549 Düsseldorf");
		vehicleBuilder2.setStartLocation(loc(Coordinate.newInstance(51.2369123, 6.7238759)));
		vehicleBuilder2.setType(vehicleType);
		VehicleImpl vehicle2 = vehicleBuilder2.build();

		/*
		 * build pickups and deliveries at the required locations, each with a
		 * capacity-demand of 1.
		 */
		/** Schiessstraße 31, 40549 Düsseldorf **/
		Pickup pickup1 = (Pickup) Pickup.Builder.newInstance("Schiessstraße 31, 40549 Düsseldorf").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2375581, 6.7200899))).build();
		/** Friedrichstraße 152, 40217 Düsseldorf **/
		Pickup pickup2 = (Pickup) Pickup.Builder.newInstance("Friedrichstraße 152, 40217 Düsseldorf").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2088948, 6.778425))).build();
		/** Breslauer Str. 2, 41460 Neuss **/
		Pickup pickup3 = (Pickup) Pickup.Builder.newInstance("Breslauer Str. 2, 41460 Neuss").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2023642, 6.717797))).build();
		/** Bataverstraße 93, 41462 Neuss **/
		Pickup pickup4 = (Pickup) Pickup.Builder.newInstance("Bataverstraße 93, 41462 Neuss").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2313282, 6.6849186))).build();
		/** Am Sandbach 30, 40878 Ratingen **/
		Pickup pickup5 = (Pickup) Pickup.Builder.newInstance("Am Sandbach 30, 40878 Ratingen").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2966233, 6.8314022))).build();

		/** Kronprinzenstraße 88, 40217 Düsseldorf**/
		Delivery delivery1 = (Delivery) Delivery.Builder.newInstance("Kronprinzenstraße 88, 40217 Düsseldorf").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2117289, 6.7701235))).build();
		/** Kaiserstraße 2, 40479 Düsseldorf **/
		Delivery delivery2 = (Delivery) Delivery.Builder.newInstance("Kaiserstraße 2, 40479 Düsseldorf").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.235204, 6.7785196))).build();
		/** Wildenbruchstraße 2, 40545 Düsseldorf **/
		Delivery delivery3 = (Delivery) Delivery.Builder.newInstance("Wildenbruchstraße 2, 40545 Düsseldorf").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2294622, 6.7469827))).build();
		/** Schlesische Straße 5, 40231 Düsseldorf **/
		Delivery delivery4 = (Delivery) Delivery.Builder.newInstance("Schlesische Straße 5, 40231 Düsseldorf").addSizeDimension(0, 1)
				.setLocation(loc(Coordinate.newInstance(51.2081411, 6.8311757))).build();

		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addVehicle(vehicle1);
		vrpBuilder.addVehicle(vehicle2);

		vrpBuilder.setFleetSize(FleetSize.INFINITE);

		/*
		 * add pickups and deliveries to the problem
		 */
		vrpBuilder.addJob(pickup1).addJob(pickup2).addJob(pickup3).addJob(pickup4).addJob(pickup5).addJob(delivery1)
				.addJob(delivery2).addJob(delivery3).addJob(delivery4);

		VehicleRoutingProblem problem = vrpBuilder.build();

		/*
         * get the algorithm out-of-the-box.
		 */
        VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);

		/*
		 * and search a solution
		 */
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
		 * get the best
		 */
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		SolutionPrinter.print(problem, bestSolution, Print.VERBOSE);

	}

	private static Location loc(Coordinate coordinate) {
		return Location.Builder.newInstance().setCoordinate(coordinate).build();
	}
}
