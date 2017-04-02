import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import javax.swing.Timer;
//import java.util.Timer;
//import java.util.TimerTask;

public class RenderingPanel extends JPanel {

	private static final long serialVersionUID = -719329737123739014L;
	
	List<MassiveBody> bodies;
	MassiveBody b1;
	MassiveBody b2;
	int timeStep;
//	DeltaTime timer;
	
	// Note: 10ms was the default time step
//	Timer timer; 
	
	Timer timer = new Timer(10, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
//			System.out.println("timeStep is " + timeStep);
//			System.out.println("Timer triggered");
			for (int i = 0; i < bodies.size() - 1; i++) {
				for (int j = i + 1; j < bodies.size(); j++) {
//					System.out.println("calculating acceleration");
					bodies.get(i).calculateAcceleration(bodies.get(j));
//					bodies.get(i).calculateForces(bodies.get(j));
				}
			}
			
			for (MassiveBody body : bodies) {
				body.calculateVelocity();
				body.calculatePosition();
//				body.moveBody();
			}
			
			repaint();
						
		}
	});
	
//	Timer timer = new Timer(timeStep, new TimerListener());
//	
	class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("timeStep is " + timeStep);
//			System.out.println("Timer triggered");
			for (int i = 0; i < bodies.size() - 1; i++) {
				for (int j = i + 1; j < bodies.size(); j++) {
					bodies.get(i).calculateAcceleration(bodies.get(j));
//					bodies.get(i).calculateForces(bodies.get(j));
				}
			}
			
			for (MassiveBody body : bodies) {
				body.calculateVelocity();
				body.calculatePosition();
//				body.moveBody();
			}
			
			repaint();
		}
		
	}
	
	public RenderingPanel(BodyCollector bodies) {
		setBackground(Color.WHITE);
		this.bodies = bodies.getListOfBodies();
		
//		for(MassiveBody body : this.bodies) {
//			body.addObserver(this);
//		}
		this.timeStep = bodies.getTimeStep();
//		timer = new Timer(timeStep, new TimerListener());
		timer.start();
		
	}
	
	
	public void paintComponent(Graphics g) {
//		System.out.println("in paintcomponent");
		super.paintComponent(g);
//		Graphics2D g2 = (Graphics2D) g;
		
		for (MassiveBody body : this.bodies) {
			System.out.println("body x: " + body.getxPos() + " body y: " + body.getyPos());
			g.setColor(body.getColor());
			g.fillOval((int)body.getxPos(), (int)body.getyPos(), (int)body.getRadius(), (int)body.getRadius());
		}
		
	}

	
//	class DeltaTime {
//		
//		private Timer timer;
//		private int timeStep;
//		
//		public DeltaTime(int timeStep) {
//			this.timeStep = timeStep;
//			timer = new Timer();
//			timer.schedule(new MoveTask(), 0, this.timeStep);
//		}
//		
//		class MoveTask extends TimerTask {
//
//			@Override
//			public void run() {
//			System.out.println("Timer triggered");
//			for (int i = 0; i < bodies.size() - 1; i++) {
//				for (int j = i + 1; j < bodies.size(); j++) {
//					bodies.get(i).calculateAcceleration(bodies.get(j));
////					bodies.get(i).calculateForces(bodies.get(j));
//				}
//			}
//			
//			for (MassiveBody body : bodies) {
//				body.calculateVelocity();
//				body.calculatePosition();
////				body.moveBody();
//			}
//			
//			repaint();
//				
//			}
//			
//		}
//	}
	
}


