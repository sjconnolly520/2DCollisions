import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

	private int ID;
	Object condVar;
	BodyCollector collector;
	List<MassiveBody> bodies;
	private int startBody, endBody;
	private int timeStep;
	private int numSteps;
	private int numWorkers;
	private CyclicBarrier barrier;
	private Point2D[][] forceMatrix;
	private static final double G = 6.67 * Math.pow(10, -11); // Universal
																// Gravitational
																// Constant

	public Worker(int ID, BodyCollector bodies, Point2D[][] forces, int startBody, int endBody, CyclicBarrier barrier, int numWorkers) {
		this.ID = ID;
		this.condVar = new Object();
		this.collector = bodies;
		this.bodies = bodies.getListOfBodies();
		this.timeStep = bodies.getTimeStep();
		this.numSteps = bodies.getNumSteps();
		this.forceMatrix = forces;
		this.startBody = startBody;
		this.endBody = endBody;
		this.barrier = barrier;
		this.numWorkers = numWorkers;
	}

	@Override
	public void run() {
		for (int i = 0; i < numSteps; i++) {
			calculateForces();
			
			// Wait for all threads to reconcile
			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e1) {
				e1.printStackTrace();
			}

			moveBodies();
			
			// Wait for all threads to reconcile
			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e1) {
				e1.printStackTrace();
			}
			
//			// Wait for timeblock
			try {
				
				synchronized (condVar) {
					condVar.wait(timeStep);
				}
//				condVar.wait(timeStep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void calculateForces() {

		for (int i = startBody; i < endBody; i++) {
			// TODO figure out if this loop should go from i..n
			for (int j = i + 1; j < bodies.size(); j++) {
				// Calculate distance of i from j
				MassiveBody first = bodies.get(i);
				MassiveBody second = bodies.get(j);
				double distance = calculateDistance(first, second);

				// calculate whether distance < some small number here, and
				// handle accordingly
				// TODO figure out what this small number should be
				if (distance <= (2 * collector.getRadius())) {
					System.out.println("collision detected");
					// TODO back up a time step?
					// recalculate velocities using collision equations

					// what is the distance? is it < 2r? if so, reset positions
					// as if they were 2r apart

					// compute x- and y-velocities for this body after collision
					// NOTE Bree has no idea how to make this look nicer
					double v1fx = (((second.getxVel() * (second.getxPos() - first.getxPos())
							* (second.getxPos() - first.getxPos()))
							+ (second.getyVel() * (second.getxPos() - first.getxPos())
									* (second.getyPos() - first.getyPos())))
							+ ((first.getxVel() * (second.getyPos() - first.getyPos())
									* (second.getyPos() - first.getyPos()))
									- (first.getyVel() * (second.getxPos() - first.getxPos())
											* (second.getyPos() - first.getyPos()))))
							/ (((second.getxPos() - first.getxPos()) * (second.getxPos() - first.getxPos()))
									+ ((second.getyPos() - first.getyPos()) * (second.getyPos() - first.getyPos())));

					double v1fy = (((second.getxVel()
							* ((second.getxPos() - first.getxPos()) * (second.getyPos() - first.getyPos()))
							+ (second.getyVel() * (second.getyPos() - first.getyPos())
									* (second.getyPos() - first.getyPos()))))
							- ((first.getxVel() * (second.getyPos() - first.getyPos())
									* (second.getxPos() - first.getxPos()))
									+ (first.getyVel() * (second.getxPos() - first.getxPos())
											* (second.getxPos() - first.getxPos()))))
							/ (((second.getxPos() - first.getxPos()) * (second.getxPos() - first.getxPos()))
									+ ((second.getyPos() - first.getyPos()) * (second.getyPos() - first.getyPos())));

					// compute x- and y-velocities for second body after
					// collision
					double v2fx = (((first.getxVel()
							* ((second.getxPos() - first.getxPos()) * (second.getxPos() - first.getxPos()))
							+ (first.getyVel() * (second.getxPos() - first.getxPos())
									* (second.getyPos() - first.getyPos()))))
							+ ((second.getxVel() * (second.getyPos() - first.getyPos())
									* (second.getyPos() - first.getyPos()))
									- (second.getyVel() * (second.getxPos() - first.getxPos())
											* (second.getyPos() - first.getyPos()))))
							/ (((second.getxPos() - first.getxPos()) * (second.getxPos() - first.getxPos()))
									+ ((second.getyPos() - first.getyPos()) * (second.getyPos() - first.getyPos())));

					double v2fy = (((first.getxVel()
							* ((second.getxPos() - first.getxPos()) * (second.getyPos() - first.getyPos()))
							+ (first.getyVel() * (second.getyPos() - first.getyPos())
									* (second.getyPos() - first.getyPos()))))
							- ((second.getxVel() * (second.getyPos() - first.getyPos())
									* (second.getxPos() - first.getxPos()))
									+ (second.getyVel() * (second.getxPos() - first.getxPos())
											* (second.getxPos() - first.getxPos()))))
							/ (((second.getxPos() - first.getxPos()) * (second.getxPos() - first.getxPos()))
									+ ((second.getyPos() - first.getyPos()) * (second.getyPos() - first.getyPos())));

					// update velocities
					first.setxVel(v1fx);
					first.setyVel(v1fy);
					second.setxVel(v2fx);
					second.setyVel(v2fy);
				}

				// Calculate axial forces on objects due to gravity
				double gravForceMag = (G * collector.getMass() * second.getMass()) / (distance * distance);

				double directionX = second.getxPos() - first.getxPos();
				double directionY = second.getyPos() - first.getyPos();

				// update force for first body
				double newXFirst = forceMatrix[ID][i].getX() + gravForceMag * directionX / distance;
				double newYFirst = forceMatrix[ID][i].getY() + gravForceMag * directionY / distance;
				forceMatrix[ID][i].setLocation(newXFirst, newYFirst);

				// update force for second body
				double newXSecond = forceMatrix[ID][j].getX() + gravForceMag * directionX / distance;
				double newYSecond = forceMatrix[ID][j].getY() + gravForceMag * directionY / distance;
				forceMatrix[ID][j].setLocation(newXSecond, newYSecond);

			}
		}

	}

	public double calculateDistance(MassiveBody first, MassiveBody other) {
		return Math.sqrt(((first.getxPos() - other.getxPos()) * (first.getxPos() - other.getxPos()))
				+ ((first.getyPos() - other.getyPos()) * (first.getyPos() - other.getyPos())));
	}

	public void moveBodies() {

		Point2D point = new Point2D.Double(0.0, 0.0);

		for (int i = startBody; i < endBody; i++) {
			double forceX = 0.0;
			double forceY = 0.0;

			for (int j = 0; j < numWorkers; j++) {
				forceX += forceMatrix[j][i].getX();
				forceY += forceMatrix[j][i].getY();
				forceMatrix[j][i].setLocation(0.0, 0.0);
			}

			// multiplied by 1000 to scale, otherwise it doesn't move very fast
			double xDeltaV = (forceX / bodies.get(i).getMass()) * timeStep * 1000;
			double yDeltaV = (forceY / bodies.get(i).getMass()) * timeStep * 1000;
			double xDeltaP = ((bodies.get(i).getxVel() + (xDeltaV / 2))) * timeStep * 1000;
			double yDeltaP = ((bodies.get(i).getyVel() + (yDeltaV / 2))) * timeStep * 1000;

			// set body's velocity
			bodies.get(i).setxVel(bodies.get(i).getxVel() + xDeltaV);
			bodies.get(i).setyVel(bodies.get(i).getyVel() + yDeltaV);

			// set body's position
			bodies.get(i).setPosXY(bodies.get(i).getxPos() + xDeltaP, bodies.get(i).getyPos() + yDeltaP);
			
		}
	}

}
