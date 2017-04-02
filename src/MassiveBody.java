import java.awt.Color;
import java.awt.Point;
import java.util.Observable;

// Created 3/24/2017
public class MassiveBody {
	
	private double xPos, yPos;         // position
	private double radius;             // dimensions for drawing
	private double xVel = 0, yVel = 0; // velocities along x, y axes
	private double xAcc = 0, yAcc = 0; // accelerations along x, y axes
	private double mass = 1;           // default = 1
	private static final double G = 6.67 * Math.pow(10, -11); // Universal
																// Gravitational
																// Constant
	private Color color;
	private boolean stationary = false; // stationary body is centered during
										// simulation; Likely just for testing
	
	private double xForce = 0, yForce = 0; // initial forces
	private int name;

	public MassiveBody() {
		color = generateRandomColor();
	}

	/**
	 * 
	 * Calculates the x-axis distance between two MassiveBody objects.
	 * 
	 * @param other
	 *            The other MassiveBody object gravitationally influencing this
	 *            MassiveBody object
	 * @return The x-axis distance between two MassiveBody objects.
	 */
	public double calculateDistX(MassiveBody other) {
		return this.getxPos() - other.getxPos();
	}

	/**
	 * 
	 * Calculates the y-axis distance between two MassiveBody objects.
	 * 
	 * @param other
	 *            The other MassiveBody object gravitationally influencing this
	 *            MassiveBody object.
	 * @return The y-axis distance between two MassiveBody objects.
	 */
	public double calculateDistY(MassiveBody other) {
		return this.getyPos() - other.getyPos();
	}

	/**
	 * 
	 * Calculates the total distance between this and the other MassiveBody
	 * object.
	 * 
	 * @param dist_x
	 *            X-axis distance between this and the other MassiveBody object.
	 * @param dist_y
	 *            Y-axis distance between this and the other MassiveBody object.
	 * @return The total distance between this and the other MassiveBody object.
	 */
	public double calculateDistance(double dist_x, double dist_y) {
		return Math.sqrt((dist_x * dist_x) + (dist_y * dist_y));
	}

	/**
	 * 
	 * Calculates the acceleration of MassiveBody objects due to the gravity of
	 * other MassiveBody objects.
	 * 
	 * @param other
	 *            The MassiveBody object which is exerting influence on this
	 *            MassiveBody object
	 */
	public void calculateAcceleration(MassiveBody other) {
		System.out.println("calculating acceleration");
		// Calculate distance of this from other
		double deltaX = calculateDistX(other);
		double deltaY = calculateDistY(other);
		double distance = calculateDistance(deltaX, deltaY);
		
//		TODO calculate whether distance < 2r here, and print a message
//		if(distance < (other.radius + this.radius)){
//			System.out.println("distance < 2r");
//		back it up a time step
//		}

		// Calculate axial forces on objects due to gravity
		double gravForceMag = (G * mass * other.getMass()) / (distance * distance);
		double forceX = Math.abs(gravForceMag * (deltaX / distance));
		double forceY = Math.abs(gravForceMag * (deltaY / distance));		

		// Calculate axial accelerations due to gravity and add to current axial
		// accelerations
		// FIXME: Stephen feels certain that this can be made more efficient.
		// Also, THIS DOESNT WORK!!!
		if (this.getxPos() < other.getxPos()) {
			this.addToXAcc(forceX / this.getMass());
			other.addToXAcc(-forceX / other.getMass());
		} else {
			this.addToXAcc(-forceX / this.getMass());
			other.addToXAcc(forceX / other.getMass());
		}

		if (this.getyPos() < other.getyPos()) {
			this.addToYAcc(forceY / this.getMass());
			other.addToYAcc(-forceY / other.getMass());
		} else {
			this.addToYAcc(-forceY / this.getMass());
			other.addToYAcc(forceY / other.getMass());
		}

	}
	
	public void calculateForces(MassiveBody other) {
		// Calculate distance of this from other
		double deltaX = calculateDistX(other);
		double deltaY = calculateDistY(other);
		double distance = calculateDistance(deltaX, deltaY);
		
//		TODO calculate whether distance < 2r here, and print a message
		if(distance < (other.radius + this.radius)){
			System.out.println("distance < 2r");
		}
		
		// Calculate axial forces on objects due to gravity
		double gravForceMag = (G * mass * other.getMass()) / (distance * distance);
		
		double directionX = other.getxPos() - this.getxPos(); 
//		System.out.println("directionX is " + directionX);
		double directionY = other.getyPos() - this.getyPos();
//		System.out.println("directionY is " + directionY);
		
		this.setXForce(this.getXForce() + gravForceMag*directionX/distance);
//		System.out.println("this x force is " + this.getXForce());
		other.setXForce(other.getXForce() - gravForceMag*directionX/distance);
//		System.out.println("other x force is " + other.getXForce());
		this.setYForce(this.getYForce() + gravForceMag*directionY/distance);
//		System.out.println("this y force is " + this.getYForce());
		other.setYForce(other.getYForce() - gravForceMag*directionY/distance);
//		System.out.println("other y force is " + other.getYForce());
		
//		TODO decide if this is needed
		this.addToXAcc(this.getXForce()/this.getMass());
		other.addToXAcc(other.getXForce()/other.getMass());
		this.addToYAcc(this.getYForce()/this.getMass());
		other.addToYAcc(other.getYForce()/other.getMass());
	}

	/**
	 * 
	 * Updates the axial velocities of this MassiveBody object based on the
	 * axial accelerations of the object. Once new velocities are calculated,
	 * accelerations are set to 0.0.
	 * 
	 */
	public void calculateVelocity() {
		
		setxVel(getxVel() + getxAcc() * 1000000); // Times 1000000 for rendering
													// purposes
		setyVel(getyVel() + getyAcc() * 1000000);

		// Set the acceleration to 0 before the next set of calculations occur.
		setxAcc(0.0);
		setyAcc(0.0);
	}

	/**
	 * 
	 * Updates the axial positions of this MassiveBody object based on the axial
	 * velocities of the object.
	 * 
	 */
	public void calculatePosition() {
		setxPos(getxPos() + getxVel());
		setyPos(getyPos() + getyVel());
	}
	
	public void moveBody() {
//		TODO decide if timeStep needs to be multiplied by .001
//		double xDeltaV = this.getXForce()/this.getMass()* BodyCollector.timeStep;
		double xDeltaV = (this.getXForce()/this.getMass())*1000;
//		double yDeltaV = this.getYForce()/this.getMass()* BodyCollector.timeStep;
		double yDeltaV = (this.getYForce()/this.getMass())*1000;
//		double xDeltaP = (this.getxVel() + (xDeltaV/2)) * BodyCollector.timeStep;
		double xDeltaP = ((this.getxVel() + (xDeltaV/2)))*1000;
//		double yDeltaP = (this.getyVel() + (yDeltaV/2)) * BodyCollector.timeStep;
		double yDeltaP = ((this.getyVel() + (yDeltaV/2)))*1000;
		
		
	//	set this body's velocity
		this.setxVel(this.getxVel() + xDeltaV);
//		System.out.println("velocity X is " + this.getxVel());
		this.setyVel(this.getyVel() + yDeltaV);
//		System.out.println("velocity Y is " + this.getyVel());
		
//		set this body's position
		this.setxPos(this.getxPos() + xDeltaV);
//		System.out.println("pos x is " + this.getxPos());
		this.setyPos(this.getyPos() + yDeltaV);
//		System.out.println("pos y is " + this.getyPos());
		
		this.setXForce(0.0);
		this.setYForce(0.0);
	}

	public void addToXAcc(double changeInAcc) {
		xAcc += changeInAcc;
	}

	public void addToYAcc(double chanceInAcc) {
		yAcc += chanceInAcc;
	}

	///////////////////////////
	/// Getters and Setters ///
	///////////////////////////
	/**
	 * @return the yPos
	 */
	public double getyPos() {
		return yPos;
	}

	/**
	 * @param yPos
	 *            the yPos to set
	 */
	public void setyPos(double yPos) {
		this.yPos = yPos;
	}

	/**
	 * @return the xPos
	 */
	public double getxPos() {
		return xPos;
	}

	/**
	 * @param xPos
	 *            the xPos to set
	 */
	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * @return the xVel
	 */
	public double getxVel() {
		return xVel;
	}

	/**
	 * @param xVel
	 *            the xVel to set
	 */
	public void setxVel(double xVel) {
		this.xVel = xVel;
	}

	/**
	 * @return the yVel
	 */
	public double getyVel() {
		return yVel;
	}

	/**
	 * @param yVel
	 *            the yVel to set
	 */
	public void setyVel(double yVel) {
		this.yVel = yVel;
	}

	/**
	 * @return the xAcc
	 */
	public double getxAcc() {
		return xAcc;
	}

	/**
	 * @param xAcc
	 *            the xAcc to set
	 */
	public void setxAcc(double xAcc) {
		this.xAcc = xAcc;
	}

	/**
	 * @return the yAcc
	 */
	public double getyAcc() {
		return yAcc;
	}

	/**
	 * @param yAcc
	 *            the yAcc to set
	 */
	public void setyAcc(double yAcc) {
		this.yAcc = yAcc;
	}

	/**
	 * @return the mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass
	 *            the mass to set
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	/**
	 * @return the force for x
	 */
	public double getXForce() {
		return xForce;
	}

	/**
	 * @param mass
	 *            the force for x to set
	 */
	public void setXForce(double force) {
		this.xForce = force;
	}	
	
	/**
	 * @return the force for y
	 */
	public double getYForce() {
		return yForce;
	}

	/**
	 * @param mass
	 *            the force for y to set
	 */
	public void setYForce(double force) {
		this.yForce = force;
	}
	
	public int getName(){
		return this.name;
	}
	
	public void setName(int name) {
		this.name = name;
	}

	/**
	 * @return the stationary
	 */
	public boolean isStationary() {
		return stationary;
	}

	/**
	 * @param stationary
	 *            the stationary to set
	 */
	public void setStationary(boolean stationary) {
		this.stationary = stationary;
	}

	/**
	 * @return The color of the object
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            The color of the object
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return A Random color for the object to be rendered in.
	 */
	public Color generateRandomColor() {
		int R = (int) (Math.random() * 256);
		int G = (int) (Math.random() * 256);
		int B = (int) (Math.random() * 256);
		return new Color(R, G, B);
	}

}
