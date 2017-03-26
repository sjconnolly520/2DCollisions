import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BodyCollector {
	
	List<MassiveBody> bodies = new ArrayList<>();
	int numBodies;
	Scanner scanner = new Scanner(System.in);
	
	
	public BodyCollector(boolean randomize) {
		
		System.out.println("How many bodies would you like to simulate?: ");
		numBodies = scanner.nextInt();
		
		if (randomize) {
			generateRandomBodies();
		}
		else {
			getUserBodyData();
		}
		
		
	}

	private void getUserBodyData() {
		// TODO Prompt user for mass, radius, xPos, yPos in a for-loop
		
	}

	private void generateRandomBodies() {
		// TODO Generate specified number of bodies randomly (will need to find adequate/reasonable ranges)
		
	}

}
