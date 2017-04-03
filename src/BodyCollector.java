import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;

public class BodyCollector {
	
	private int timeStep;              // time step for this simulation
	private int numSteps;              // the number of time steps for this simulation
	private double mass;                  // the mass of each body
	private double radius;                // the radius of each body
	private List<MassiveBody> bodies = new ArrayList<>();
	int numBodies;
	Scanner scanner = new Scanner(System.in);
	final double minMass = 50000, maxMass = 500000;
	final double minRadius = 10, maxRadius = 200;
	final int WIDTH = 620, HEIGHT = 520;
	
	/**
	 * 
	 * Constructor. Creates and manages a list of MassiveBody objects.
	 * 
	 * @param randomize		Boolean value describing whether the user wants to generate MassiveBody objects randomly or generate them based on specific values. 
	 */
	public BodyCollector(boolean randomize) {
		
		System.out.print("How many bodies would you like to simulate?: ");
		numBodies = scanner.nextInt();
		
//		NOTE extra questions added by Bree
		System.out.print("What time step do you want (in ms)?: ");
		timeStep = scanner.nextInt();
		
		System.out.print("What is the radius of the bodies (in meters)?: ");
		radius = scanner.nextFloat();
		
		System.out.print("What is the mass of the bodies (in grams)?: ");
		mass = scanner.nextFloat();
		
		
		if (randomize) {
			generateRandomBodies();
		}
		else {
			getUserBodyData();
		}
	}
	
	/**
	 * Prompts the user for required information for each of the bodies he/she wants to simulate.
	 */
	private void getUserBodyData() {
		
		for (int i = 0; i < numBodies; i++) {
			MassiveBody newBody = new MassiveBody(timeStep);
			newBody.setName(i);
			
			// User prompts
//			System.out.print("What is the radius of body " + (i+1) + " (in meters)?: ");
//			float radius = scanner.nextFloat();
			newBody.setRadius(radius);
			
//			System.out.print("What is the mass of body " + (i+1) + " (in grams)?: ");
//			float mass = scanner.nextFloat();
			newBody.setMass(mass);
			
			System.out.print("What is the x location of body " + (i+1) + "?: ");
			float xLoc = scanner.nextFloat();
			newBody.setxPos(xLoc);
			
			System.out.print("What is the y location of body " + (i+1) + "?: ");
			float yLoc = scanner.nextFloat();
			newBody.setyPos(yLoc);
			
			// While there is a location conflict with another body, ask the user for a new location
			while (!isValidLocation(newBody)) {				
				System.out.println("There is a conflict with creating a body at that location.");
				System.out.println("Please try another location.");
				
				System.out.print("What is the x location of body " + (i+1) + "?: ");
				xLoc = scanner.nextFloat();
				newBody.setxPos(xLoc);
				
				System.out.print("What is the y location of body " + (i+1) + "?: ");
				yLoc = scanner.nextFloat();
				newBody.setyPos(yLoc);
			}
	
			bodies.add(newBody);
		}
		
	}

	/**
	 * Generates MassiveBody objects within a hardcoded range of Masses, Radiuses, x- and y-locations.
	 */
	private void generateRandomBodies() {
		
		Random rng = new Random();			// Random number generator
		
		for (int i = 0; i < numBodies; i++) {
			MassiveBody newBody = new MassiveBody(timeStep);
			
			// Generate random value for MassiveBody's mass
			newBody.setMass(rng.nextDouble() * (maxMass - minMass) + minMass);
			
			// Generate random value for MassiveBody's radius
			double randRadius = rng.nextDouble() * (maxRadius - minRadius) + minRadius;
			newBody.setRadius(randRadius);
			
			// Calculate bounding area for this MassiveBody object
			double minXLocForThisBody = randRadius + 1, maxXLocForThisBody = WIDTH - randRadius - 1;
			double minYLocForThisBody = randRadius + 1, maxYLocForThisBody = HEIGHT - randRadius - 1;
			
			// Generate random values for MassiveBody's x- and y-locations.
			double xLoc = rng.nextDouble() * (maxXLocForThisBody - minXLocForThisBody) + minXLocForThisBody;
			double yLoc = rng.nextDouble() * (maxYLocForThisBody - minYLocForThisBody) + minYLocForThisBody;
			newBody.setxPos(xLoc);
			newBody.setyPos(yLoc);
			
			// While there is a location conflict with another body, generate a new random location
			while(!isValidLocation(newBody)) {
				xLoc = rng.nextDouble() * (maxXLocForThisBody - minXLocForThisBody) + minXLocForThisBody;
				yLoc = rng.nextDouble() * (maxYLocForThisBody - minYLocForThisBody) + minYLocForThisBody;
				newBody.setxPos(xLoc);
				newBody.setyPos(yLoc);
			}
			
			bodies.add(newBody);
		}
	}
	
	/**
	 * Checks if a MassiveBody object is placed in an acceptable location. 
	 * Acceptable locations are fully bounded by the frame and not overlapping of any other MassiveBody objects.
	 * 
	 * @param newBody	The MassiveBody object which is to be placed in the simulation
	 * @return 			Boolean value describing whether or not the location is acceptable
	 */
	private boolean isValidLocation(MassiveBody newBody) {
		
		double xLoc = newBody.getxPos();
		double yLoc = newBody.getyPos();
		double radius = newBody.getRadius();
		
		// Check if the MassiveBody object is contained within the window
		if ((xLoc - radius) <= 0 || (xLoc + radius) >= WIDTH) {			// Check that the MassiveBody object is contained laterally
			return false;
		}
		
		if ((yLoc - radius) <= 0 || (yLoc + radius) >= HEIGHT) {		// Check that the MassiveBody object is contained vertically
			return false;
		}
		
		// Check if the MassiveBody object (newBody) overlaps another MassiveBody object (compBody) already in simulation.
		for (MassiveBody compBody : bodies) {
			
			double xDist = Math.abs(newBody.calculateDistX(compBody));
			double yDist = Math.abs(newBody.calculateDistY(compBody));
			double actualDistance = newBody.calculateDistance(xDist, yDist);				// The magnitude of the ACTUAL distance between the centers of the two MassiveBody objects
			double minAcceptableDistance = newBody.getRadius() + compBody.getRadius();		// The magnitude of the MINIMUM ACCEPTABLE distance between the centers of the two MassiveBody objects
			
			// If the distance between the MassiveBody objects is less than the minimum acceptable distance, return false.
			if (actualDistance <= minAcceptableDistance) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @return		Returns the list of MassiveBody objects being simulated
	 */
	public List<MassiveBody> getListOfBodies() {
		return bodies;
	}
	
	/**
	 * 
	 * Adds a given MassiveBody object to the list of MassiveBody objects. Probably primarily for testing.
	 * 
	 * @param newBody		MassiveBody object to be added to the list
	 */
	public void addToListOfBodies(MassiveBody newBody) {
		bodies.add(newBody);
	}
	
	/**
	 * @return     Returns the time step for this simulation
	 * @return
	 */
	public int getTimeStep() {
		return timeStep;
	}

}
