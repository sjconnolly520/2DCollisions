import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
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
			System.out.println("RenderFrame <numberOfWorkers> <numberOfBodies> <massOfBodies> <numberOfTimeSteps>");
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
			
			Worker[] workers = new Worker[numWorkers];				// Create an array in which to store the threads.
//			initialize force matrix, parallel JPanel, and start workers
			Point2D[][] forceMatrix = new Point2D[numWorkers][numBodies];
			
			
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
			
//			create array to hold barrier timings
			long[] barrierTimings = new long[numWorkers];
			
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
				
				workers[i] = new Worker(i, bodies, forceMatrix, startBody, endBody+1, barrier, numWorkers, barrierTimings);
				numBodiesDistributed = endBody + 1;
				
			}
						
			long startTime = System.nanoTime();

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
		    
		    long totalBarrierTime = 0;
//		    wait for the Workers to finish
		    for (int i = 0; i < numWorkers; i++) {
		    	try {
		    		workers[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
		    
//		    calculate total time spent on barriers
		    for(long timing : barrierTimings) {
		    	totalBarrierTime += timing;
		    }
		  
		    
		    try {
		    	
				FileWriter fw = new FileWriter("finalOutput.txt");
				BufferedWriter bw = new BufferedWriter(fw);
				List<MassiveBody> bodiesList = bodies.getListOfBodies();
				
				for (int i = 0; i < numBodies; i++) {
					MassiveBody currBody = bodiesList.get(i);
					String str = "Body " + i + ": \n\tPosition = (" + currBody.getxPos() + ", " + currBody.getyPos() + ")"
							+ "\n\tVelocity = (" + currBody.getxVel() + ", " + currBody.getyVel() + ")" +"\n\n";
					bw.write(str);
				}
				
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    
		    // Timer stop
		    long endTime = System.nanoTime();
		    
//		    calculate computation time
		    long seconds = (long) ((endTime-startTime) * 0.000000001);
		    long milliseconds = (long) (((endTime-startTime) % 1000000000) * 0.000001);
		    
//		    print out computation time
		    System.out.println("computation time: " + seconds + " seconds " + milliseconds + " milliseconds");
		    
//		    print out number of collisions
		    System.out.println("number of collisions: " + bodies.getTotalCollisions());
		    
		    System.out.println("percentage of time spent on barriers: " + (totalBarrierTime/(endTime-startTime)) * 100 + "%");
		}  
	}
}
