import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

//import Final Project.src.Colony; Project.src.Colony;

/**
 * Created by Zortrox on 11/7/2016.
 */
public class Simulation extends JPanel implements ActionListener{

	// Colony col = new Colony(x, y, width, height, numAnts, mutatePercent);
	// while(true) {
	// if (!col.step([can put number here])) {
	// break;
	// }
	// }

	public JFrame frame;
	public JPanel controlPanel = new JPanel();
	boolean running = true;

	public static void main(String args[]) {
		new Simulation();
	}

	public Simulation() {
		// sets up the frame
		frame = new JFrame();
		frame.setTitle("Ant Colony Simulation");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(900, 700);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(700);
		pane.add(this);
		pane.add(controlPanel);

		frame.add(pane);

		Colony colony = new Colony();

		while (running) {

		}
	}

	public void paint(Graphics g) {
		g.fillRect(0, 0, 700, 700);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
