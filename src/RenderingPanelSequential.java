import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import javax.swing.Timer;


public class RenderingPanelSequential extends JPanel implements Observer{

	private static final long serialVersionUID = -719329737123739014L;
	final static int WIDTH = 620;
	final static int HEIGHT = 520;
	
	List<MassiveBody> bodies;
	MassiveBody b1;
	MassiveBody b2;
	int timeStep;
	int numSteps;
	long startTime;
	BodyCollector initializedBodies;
	
	
	public RenderingPanelSequential(BodyCollector bodies) {
		setBackground(Color.WHITE);
		this.initializedBodies = bodies;
		this.bodies = bodies.getListOfBodies();
		this.timeStep = bodies.getTimeStep();
		this.numSteps = bodies.getNumSteps();
		
//		start the timer for sequential version
		startTime = System.nanoTime();
		
//		had to use this initialization to have customized time steps
		timer = new Timer(timeStep, new TimerListener());
		timer.start();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (MassiveBody body : this.bodies) {
			g.setColor(body.getColor());
			g.fillOval((int)body.getXPosForDrawing(), (int)body.getYPosForDrawing(), 2*(int)body.getRadius(), 2*(int)body.getRadius());
		}
		
	}	
	
	
//	used for initialization in constructor
	Timer timer;

	class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			
			for (int i = 0; i < bodies.size() - 1; i++) {
				for (int j = i + 1; j < bodies.size(); j++) {
					bodies.get(i).calculateForces(bodies.get(j));
				}
			}
			
			for (MassiveBody body : bodies) {
//					body.calculateVelocity();
//					TODO done separately below
//					body.calculatePosition();
				body.moveBody();
			}
			
			if (initializedBodies.wallCollisonsActive()) {
				for (MassiveBody body : bodies) {
					body.checkForWallCollision();
				}
			}
			
			repaint();
			
			initializedBodies.reduceNumSteps();
			
		}
		
	}

	@Override
	public void update(Observable newBodies, Object arg) {
		
		BodyCollector tempBodies = (BodyCollector) newBodies;
		int currentStep = tempBodies.getNumSteps();
		
//		stop the timer after the requested number of time steps
		if(currentStep <= 0) {
			System.out.println("stopping simulation");
			timer.stop();
			
//			print out computation time and number of collisions
		    long endTime = System.nanoTime();
		    long seconds = (long) ((endTime-startTime) * .0000000001);
		    long milliseconds = (long) (((endTime-startTime) % 1000000000) * .0000001);
		    System.out.println("computation time: " + seconds + " seconds " + milliseconds + " milliseconds");
		    System.out.println("number of collisions: " + tempBodies.getTotalCollisions());
		    
		}
		
	}
}


