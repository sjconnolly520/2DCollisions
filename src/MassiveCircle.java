import java.awt.*;
import java.util.Random;

public class MassiveCircle {

	private static final int Y_STEP = 5;
    private static final int X_STEP = 5;
    public String name;
    public int 
    public Color color;
    public int xCoord;
    public int yCoord;

	public float massInKg, radiusInMeters, xVelocity, yVelocity, xCoord, yCoord;
	
	public MassiveCircle(float mass, float radius, float xVelocity, float yVelocity, float xCoord, float yCoord) {
		this.massInKg = mass;
		this.radiusInMeters = radius;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}
	
	// Generate a new circle
	public MassiveCircle() {
		Random rng = new Random();
		
		
	}
	
    public void move() {
        newLocation.x += X_STEP;
        newLocation.y += Y_STEP;
        //How set new location ?
        //It doesn't work
        this.shape.getBounds().setLocation(newLocation);        
        System.out.println(String.format("New location is [%d,%d]",newLocation.x, newLocation.y));
        System.out.println(String.format("Move to [%d,%d]", this.shape.getBounds().getLocation().x, this.shape.getBounds().getLocation().y));       
    }
	
	
}
