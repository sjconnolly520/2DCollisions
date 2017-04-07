import java.awt.geom.Point2D;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JFrame;

public class RenderFrame {
	
	final static int WIDTH = 620;
	final static int HEIGHT = 520;
	static int timeStep;
	
	public static void main(String[] args) {
		JFrame window = new JFrame("2-D Collisons on N-Bodies");

		// If not enough arguments are given
		if (args.length < 4) {
			System.out.println("Function must be called using the following syntax:");
			System.out.println("nBodies <numberOfWorkers> <numberOfBodies> <massOfBodies> <numberOfTimeSteps>");
			System.out.println("followed by any elective options (-r, -wc, etc.)");
			System.exit(1);
		}
		
		// Parse command-line arguments
		int numWorkers = Integer.parseInt(args[0]);
		int numBodies = Integer.parseInt(args[1]);
		int bodyMass = Integer.parseInt(args[2]);
		int numTimeSteps = Integer.parseInt(args[3]);

		boolean random = false;
		boolean wallCollisions = false;
		
		// Determine if bodies are to be placed randomly or if wall collisions are to be registered
		if ( args.length > 4 ) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-r")) {
					random = true;
				}
								
				if (args[i].equals("-wc")) {
					wallCollisions = true;
				}
			}
		}
				
		
		Worker[] workers = new Worker[numWorkers];				// Create an array in which to store the threads.
		Point2D[][] forceMatrix = new Point2D[numWorkers][numBodies];	
		BodyCollector bodies = new BodyCollector(random, numBodies, wallCollisions, bodyMass, numTimeSteps);
		
//		figure out if this is a sequential program or a parallel one
		if(numWorkers == 1) {
			
//			set up the sequential panel
			RenderingPanelSequential mainPanel = new RenderingPanelSequential(bodies);	
			bodies.addObserver(mainPanel);
			window.setContentPane(mainPanel);
		    window.setSize(WIDTH,HEIGHT);
		    window.setLocation(100, 100);
		    window.setAlwaysOnTop(true);
		    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    window.setVisible(true);
		} else {
//			initialize force matrix, parallel JPanel, and start workers
			
			
//			initialize the force matrix to 0.0 in every cell
			for(int i = 0; i < numWorkers; i++) {
				for(int j = 0; j < numBodies; j++) {
					forceMatrix[i][j] = new Point2D.Double(0.0, 0.0);
				}
			}
			
//			initialize correct JPanel
			RenderingPanelParallel mainPanel = new RenderingPanelParallel(bodies);	
			for(MassiveBody body : bodies.getListOfBodies()) {
				body.addObserver(mainPanel);
			}
			
			// Create Barrier
			CyclicBarrier barrier = new CyclicBarrier(numWorkers);
			
			// Distribute MassiveBody objects "evenly" across workers
			int numBodiesPerWorker = numBodies / numWorkers;
			int numBodiesDistributed = 0;
			
			for (int i = 0; i < numWorkers; i++) {
				
				int startBody = numBodiesDistributed;
				int endBody;
				
				if (i < (numBodies % numWorkers)) {
					endBody = startBody + numBodiesPerWorker;
				}
				
				else {
					endBody = startBody + numBodiesPerWorker - 1;
				}
				
				workers[i] = new Worker(i, bodies, forceMatrix, startBody, endBody+1, barrier, numWorkers);
				numBodiesDistributed = endBody + 1;
				
			}
						
			
			// Timer start TODO:
			// Start each worker
			for (int i = 0; i < workers.length; i++) {
				workers[i].start();
			}
			
			
//			initialize the correct panel
		    window.setSize(WIDTH,HEIGHT);
		    window.setLocation(100, 100);
		    window.setContentPane(mainPanel);
		    window.setAlwaysOnTop(true);
		    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    window.setVisible(true);
			
//		    wait for the Workers to finish
		    for (int i = 0; i < numWorkers; i++) {
		    	try {
					workers[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
		    
		    // TODO: Timer stop;
		}
	    
	}

}
