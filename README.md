# Vehicle routing problem.

This application is created as a solution for the task asked by the interviewer.

Problem: 
There are fleet of drones located at two depot location. There are 5 pickup centers and 4 test delivery customers.
The goods can be pre-picked traveling at around 60kmph en-route from pickup centers and unload at on of the nearest depot or deliver directly to its customer. If the goods are already in depot then those can directly be delivered to customer without going again to pickup centers.

Solution:
This problem is a classic example of vehicle routing problem(VRP) derived from famous traveling salesman problem. For programming we make use of library called JSprit which has the algorithm for solving such VRP. The problem statement clearly defines en-route pickup and delivery with multiple depot. There are no waiting times or service times. Fleet size in each depot is assumed to be infinite, as size information is not given. The capacity of each drone is considered with weight 2 and the good deliverable weight of 1 factor.

The solution displayed indicates the information as to which customer will be served by which pickup locations and depots.

Please run the program to see the solution output.

To run the program please go to class com.real.vrp.drone.App.java
The output is also placed in output folder in text format.
