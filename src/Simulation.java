/**
 * Created by Zortrox on 11/7/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Simulation extends JPanel implements ActionListener {

	private JFrame frame;
	private JPanel controlPanel = new JPanel();
	private boolean running = true;
	private int gameWidth = 670;
	private int gameHeight = 670;
	private Colony col;

	public static void main(String args[]){
		new Simulation();
	}

	public Simulation(){
		//sets up the frame
		frame = new JFrame();
		frame.setTitle("Ant Colony Simulation");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(gameWidth + 200, gameHeight + 30);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(gameWidth);
		pane.add(this);
		pane.add(controlPanel);

		frame.add(pane);

		col = new Colony(300, 300, gameWidth, gameHeight, 100, 0.25f);

		while(running){
			if (!col.step()) {
				running = false;
			}
			paintImmediately(0, 0, gameWidth, gameHeight);

			try {
				Thread.sleep(10);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void paint(Graphics g) {
		g.setColor(new Color(226, 203, 183));
		g.fillRect(0, 0, gameWidth, gameHeight);
		col.draw(g);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
