import java.util.List;

import javax.swing.JFrame;

public class RenderFrame {
	
	final int WIDTH = 620, HEIGHT = 520;
	static int timeStep;
	
	public static void main(String[] args) {
		JFrame window = new JFrame("The Window Title");
		
		boolean random = false;
		if ( args.length > 0 && args[0].equals( "-r" ) ) {
			random = true;
		}
		
		BodyCollector bodies = new BodyCollector(random);
		RenderingPanel mainPanel = new RenderingPanel(bodies);
//		List<MassiveBody> allBodies = bodies.getListOfBodies();
//		bodies.addObserver(mainPanel);
		
		
		window.setContentPane(mainPanel);
	    window.setSize(620,520);
	    window.setLocation(100, 100);
	    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    window.setVisible(true);
	    
//	    for(int k = 0; k < 5; k++) {
//	    	System.out.println("k == " + k);
//			for (int i = 0; i < bodies.getListOfBodies().size() - 1; i++) {
//				for (int j = i + 1; j < bodies.getListOfBodies().size(); j++) {
////				bodies.get(i).calculateAcceleration(bodies.get(j));
//					bodies.getListOfBodies().get(i).calculateForces(bodies.getListOfBodies().get(j));
//				}
//			}
//		
//			for (MassiveBody body : bodies.getListOfBodies()) {
//				body.calculateVelocity();
//				body.calculatePosition();
////			body.moveBody();
//			}
//	    }
	    
//	    System.exit(0);
	}

}
