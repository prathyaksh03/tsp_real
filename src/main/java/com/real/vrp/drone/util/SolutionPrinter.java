package com.real.vrp.drone.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

public class SolutionPrinter {
	// Wrapping System.out into a PrintWriter
	private static final PrintWriter SYSTEM_OUT_AS_PRINT_WRITER = new PrintWriter(System.out);

	/**
	 * Enum to indicate verbose-level.
	 * <p>
	 * <p>
	 * Print.CONCISE and Print.VERBOSE are available.
	 *
	 * @author stefan schroeder
	 */
	public enum Print {

		CONCISE, VERBOSE
	}

	private static class Jobs {
		int nServices;
		int nShipments;
		int nBreaks;

		public Jobs(int nServices, int nShipments, int nBreaks) {
			super();
			this.nServices = nServices;
			this.nShipments = nShipments;
			this.nBreaks = nBreaks;
		}
	}

	/**
	 * Prints costs and #vehicles to stdout (out.println).
	 *
	 * @param solution the solution to be printed
	 */
	public static void print(VehicleRoutingProblemSolution solution) {
		print(SYSTEM_OUT_AS_PRINT_WRITER, solution);
		SYSTEM_OUT_AS_PRINT_WRITER.flush();
	}

	/**
	 * Prints costs and #vehicles to the given writer
	 *
	 * @param out      the destination writer
	 * @param solution the solution to be printed
	 */
	public static void print(PrintWriter out, VehicleRoutingProblemSolution solution) {
		out.println("[costs=" + solution.getCost() + "]");
		out.println("[#vehicles=" + solution.getRoutes().size() + "]");
	}

	/**
	 * Prints costs and #vehicles to the to stdout (out.println).
	 *
	 * @param out      the destination writer
	 * @param solution the solution to be printed
	 */
	public static void print(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution, Print print) {
		print(SYSTEM_OUT_AS_PRINT_WRITER, problem, solution, print);
		SYSTEM_OUT_AS_PRINT_WRITER.flush();
	}

	/**
	 * Prints costs and #vehicles to the given writer
	 *
	 * @param out      the destination writer
	 * @param solution the solution to be printed
	 */
	public static void print(PrintWriter out, VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution,
			Print print) {
		String leftAlign = "| %-13s | %-8s | %n";

		out.format("+--------------------------+%n");
		out.printf("| problem                  |%n");
		out.format("+---------------+----------+%n");
		out.printf("| indicator     | value    |%n");
		out.format("+---------------+----------+%n");

		out.format(leftAlign, "noJobs", problem.getJobs().values().size());
		Jobs jobs = getNuOfJobs(problem);
		out.format(leftAlign, "noServices", jobs.nServices);
		out.format(leftAlign, "fleetsize", problem.getFleetSize().toString());
		out.format("+--------------------------+%n");

		String leftAlignSolution = "| %-13s | %-40s | %n";
		out.format("+----------------------------------------------------------+%n");
		out.printf("| solution                                                 |%n");
		out.format("+---------------+------------------------------------------+%n");
		out.printf("| indicator     | value                                    |%n");
		out.format("+---------------+------------------------------------------+%n");
		out.format(leftAlignSolution, "noVehicles", solution.getRoutes().size());
		out.format("+----------------------------------------------------------+%n");

		if (print.equals(Print.VERBOSE)) {
			printVerbose(out, problem, solution);
		}
	}

	private static void printVerbose(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution) {
		printVerbose(SYSTEM_OUT_AS_PRINT_WRITER, problem, solution);
		SYSTEM_OUT_AS_PRINT_WRITER.flush();
	}

	private static void printVerbose(PrintWriter out, VehicleRoutingProblem problem,
			VehicleRoutingProblemSolution solution) {
		String leftAlgin = "| %-7s | %-20s | %-21s | %-39s | %-20s | %-8s | %-18s |%n";
		out.format(
				"+----------------------------------------------------------------------------------------------------------------+----------------------+----------+--------------------+%n");
		out.printf(
				"| detailed solution                                                                                                                                                     |%n");
		out.format(
				"+---------+------------------------------------+-----------------------+-----------------------------------------+----------------------+----------+--------------------+%n");
		out.printf(
				"| route   | Depot                              | activity              | job                                     | Total travel duration|Type      | Total delivery time|%n");
		int routeNu = 1;

		List<VehicleRoute> list = new ArrayList<VehicleRoute>(solution.getRoutes());
		Collections.sort(list, new com.graphhopper.jsprit.core.util.VehicleIndexComparator());
		for (VehicleRoute route : list) {
			long totalDuration = 0;
			out.format(
					"+---------+------------------------------------+-----------------------+-----------------------------------------+----------------------+----------+--------------------+%n");
			double costs = 0;
			out.format(leftAlgin, routeNu, getVehicleString(route), route.getStart().getName(), "-", "-", "-", "-");
			TourActivity prevAct = route.getStart();
			for (TourActivity act : route.getActivities()) {
				String jobId;
				if (act instanceof TourActivity.JobActivity) {
					jobId = ((TourActivity.JobActivity) act).getJob().getId();
				} else {
					jobId = "-";
				}
				double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(),
						prevAct.getEndTime(), route.getDriver(), route.getVehicle());
				c += problem.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(),
						route.getVehicle());
				costs += c;
				double distance = GeoTimeCalculator.distance(prevAct.getLocation().getCoordinate(),
						act.getLocation().getCoordinate());
				totalDuration += GeoTimeCalculator.getTime(distance, route.getVehicle().getType().getMaxVelocity());
				String durationString = GeoTimeCalculator.getFormattedTime(totalDuration);
				String type = "delivery".equals(act.getName()) ? "Customer" : "-";
				String deliveryTime = "delivery".equals(act.getName()) ? durationString : "-";
				out.format(leftAlgin, routeNu, getVehicleString(route), act.getName(), jobId, durationString, type,
						deliveryTime);
				prevAct = act;
			}
			double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(),
					prevAct.getEndTime(), route.getDriver(), route.getVehicle());
			c += problem.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(),
					route.getDriver(), route.getVehicle());
			costs += c;
			out.format(leftAlgin, routeNu, getVehicleString(route), route.getEnd().getName(), "-", "-", "-", "-");
			routeNu++;
		}
		out.format(
				"+----------------------------------------------------------------------------------------------------------------+----------------------+----------+--------------------+%n");
		if (!solution.getUnassignedJobs().isEmpty()) {
			out.format("+----------------+%n");
			out.format("| unassignedJobs |%n");
			out.format("+----------------+%n");
			String unassignedJobAlgin = "| %-14s |%n";
			for (Job j : solution.getUnassignedJobs()) {
				out.format(unassignedJobAlgin, j.getId());
			}
			out.format("+----------------+%n");
		}
	}

	private static String getVehicleString(VehicleRoute route) {
		return route.getVehicle().getId();
	}

	private static Jobs getNuOfJobs(VehicleRoutingProblem problem) {
		int nShipments = 0;
		int nServices = 0;
		int nBreaks = 0;
		for (Job j : problem.getJobs().values()) {
			if (j instanceof Shipment) {
				nShipments++;
			}
			if (j instanceof Service) {
				nServices++;
			}
			if (j instanceof Break) {
				nBreaks++;
			}
		}
		return new Jobs(nServices, nShipments, nBreaks);
	}

}
