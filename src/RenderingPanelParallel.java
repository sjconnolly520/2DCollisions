import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;


public class RenderingPanelParallel extends JPanel implements Observer{

	private static final long serialVersionUID = -719329737123739014L;
	
	List<MassiveBody> bodies;
	MassiveBody b1;
	MassiveBody b2;
	int timeStep;
	int numSteps;
	BodyCollector initializedBodies;
	
	
	public RenderingPanelParallel(BodyCollector bodies) {
		setBackground(Color.WHITE);
		this.bodies = bodies.getListOfBodies();		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (MassiveBody body : this.bodies) {
			g.setColor(body.getColor());
			g.fillOval((int)body.getXPosForDrawing(), (int)body.getYPosForDrawing(), 2*(int)body.getRadius(), 2*(int)body.getRadius());
		}
		
	}	

	@Override
	public void update(Observable newBodies, Object arg) {
		repaint();
	}
}


