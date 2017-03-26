import javax.swing.JFrame;

public class RenderFrame {
	
	public static void main(String[] args) {
		JFrame window = new JFrame("The Window Title");

		boolean random = true;
		
		BodyCollector bodies = new BodyCollector(random);
		RenderingPanel mainPanel = new RenderingPanel(bodies);
		window.setContentPane(mainPanel);
	    window.setSize(620,520);
	    window.setLocation(100, 100);
	    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    window.setVisible(true);
	}

}
