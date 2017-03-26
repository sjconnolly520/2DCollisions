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
	
	List<MassiveBody> bodies = new ArrayList<>();
	MassiveBody b1;
	MassiveBody b2;
	
	Timer timer = new Timer(10, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
						
			b1.calculateAcceleration(b2);
			b1.calculateVelocity();
			b2.calculateVelocity();
			b1.calculatePosition();
			b2.calculatePosition();
			repaint();
		}
	});
	
	public RenderingPanel(BodyCollector bodies) {
		
		
		
		b1 = new MassiveBody();

		b1.setMass(100000);
		b1.setRadius(15);
		b1.setxPos(500);
		b1.setyPos(100);
		
		b2 = new MassiveBody();

		b2.setMass(200000);
		b2.setRadius(20);
		b2.setxPos(100);
		b2.setyPos(400);
		
		timer.start();
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.WHITE);
		g.setColor(Color.BLACK);
		
		g.fillOval((int)b1.getxPos(), (int)b1.getyPos(), (int)b1.getRadius(), (int)b1.getRadius());
		g.fillOval((int)b2.getxPos(), (int)b2.getyPos(), (int)b2.getRadius(), (int)b2.getRadius());
	}

}
