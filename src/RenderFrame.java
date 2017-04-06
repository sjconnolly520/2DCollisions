import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JFrame;

public class RenderFrame {
	
	final static int WIDTH = 620;
	final static int HEIGHT = 520;
	static int timeStep;
	
	public static void main(String[] args) {
		JFrame window = new JFrame("The Window Title");
		int numWorkers = Integer.parseInt(args[0]);
		Worker[] workers = new Worker[numWorkers];
		int numBodies = Integer.parseInt(args[1]);
		Point2D[][] forceMatrix = new Point2D[numWorkers][numBodies];
		
		boolean random = false;
		if ( args.length > 4 && args[0].equals( "-r" ) ) {
			random = true;
		}
		
		BodyCollector bodies = new BodyCollector(random, numBodies);
		
		if (args.length == 0) {
			System.out.println("More arguments needed!");
			System.exit(1);
		}
		
//		figure out if this is a sequential program or a parallel one
		if(numWorkers == 1) {
			
//			set up the sequential panel
			RenderingPanelSequential mainPanel = new RenderingPanelSequential(bodies);	
			bodies.addObserver(mainPanel);
			window.setContentPane(mainPanel);
		    window.setSize(WIDTH,HEIGHT);
		    window.setLocation(100, 100);
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
			bodies.addObserver(mainPanel);
			
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
				
				workers[i] = new Worker(i, bodies, forceMatrix, startBody, endBody+1, barrier, numWorkers);		// FIXME: Might be endBody (not +1)
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
		    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    window.setVisible(true);
			
//		    wait for the Workers to finish
		    
		    for (int i = 0; i < numBodies; i++) {
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
