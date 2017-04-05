import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class RenderingPanel extends JPanel{

	private static final long serialVersionUID = -719329737123739014L;
	
	List<MassiveBody> bodies;
	MassiveBody b1;
	MassiveBody b2;
	
	Timer timer = new Timer(10, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			for (int i = 0; i < bodies.size() - 1; i++) {
				for (int j = i + 1; j < bodies.size(); j++) {
					bodies.get(i).calculateAcceleration(bodies.get(j));
				}
			}
			
			for (MassiveBody body : bodies) {
				body.calculateVelocity();
				body.calculatePosition();
			}
			
			repaint();
						
		}
	});
	
	public RenderingPanel(BodyCollector bodies) {
		
		this.bodies = bodies.getListOfBodies();
		timer.start();
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.WHITE);
		
		for (MassiveBody body : bodies) {
			g.setColor(body.getColor());
			g.fillOval((int)body.getxPos(), (int)body.getyPos(), (int)body.getRadius(), (int)body.getRadius());
		}
		
	}

}
