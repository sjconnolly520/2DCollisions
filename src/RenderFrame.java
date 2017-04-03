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
		
		window.setContentPane(mainPanel);
	    window.setSize(620,520);
	    window.setLocation(100, 100);
	    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    window.setVisible(true);
	    
	}

}
