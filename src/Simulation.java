/**
 * Created by Zortrox on 11/7/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Simulation extends JPanel implements ActionListener {

	private JFrame frame;
	private JPanel controlPanel = new JPanel();
	boolean running = true;

	public static void main(String args[]){
		new Simulation();
	}

	public Simulation(){
		//sets up the frame
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

		Colony col = new Colony(200, 200, 700, 700, 10, 0.25f);

		while(running){
			if (!col.step()) {
				running = false;
			}
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
