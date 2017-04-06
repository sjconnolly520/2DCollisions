import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JFrame;

public class RenderFrame {
	
	final int WIDTH = 620, HEIGHT = 520;
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
		
		BodyCollector bodies = new BodyCollector(random);
		
//		figure out if this is a sequential program or a parallel one
		if(numWorkers > 1) {
			
//			set up the sequential panel
			RenderingPanelSequential mainPanel = new RenderingPanelSequential(bodies);	
			bodies.addObserver(mainPanel);
			window.setContentPane(mainPanel);
		    window.setSize(620,520);
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
			
//			TODO turn this into a for-loop
			workers[0] = new Worker(0, bodies, forceMatrix, startBody, endBody);
			workers[0].start();
			
			
//			initialize the correct panel
		    window.setSize(620,520);
		    window.setLocation(100, 100);
		    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		    window.setVisible(true);
			
//		    wait for the Workers to finish
			try {
				workers[0].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
//	    window.setSize(620,520);
//	    window.setLocation(100, 100);
//	    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//	    window.setVisible(true);
	    
	}

}
