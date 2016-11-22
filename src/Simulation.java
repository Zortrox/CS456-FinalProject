/**
 * Created by Zortrox on 11/7/2016.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class Simulation extends JPanel implements ActionListener {

	private JFrame frame;
	private JPanel controlPanel = new JPanel();
	private boolean running = true;
	private int gameWidth = 670;
	private int gameHeight = 670;
	private Colony col;
	private Colony bestColonly;
	private int sleepSpeed = 10;
	private boolean paused = false;
	private int generation = 0;
	private JTextArea colonyText = new JTextArea("Current Colony Info:\n", 10, 20);
	
	JButton pause = new JButton("Pause");

	public static void main(String args[]){
		new Simulation();
	}

	public Simulation(){
		//sets up the frame
		frame = new JFrame();
		frame.setTitle("Ant Colony Simulation");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(gameWidth + 250, gameHeight + 30);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(gameWidth);
		pane.setDividerSize(0);
		
		pane.add(this);
		pane.add(controlPanel);
		
		pause.setActionCommand("pause");
		pause.addActionListener(this);
		
		colonyText.setEditable(false);
		JScrollPane colonyScroll = new JScrollPane(colonyText);
		
		controlPanel.add(pause);
		controlPanel.add(new JButton("Start"));
		controlPanel.add(colonyScroll);
		
		colonyText.append("Generation: " + generation + "\n");
		colonyText.append("Total Ants: ");
		
		frame.add(pane);

		col = new Colony(300, 300, gameWidth, gameHeight, 100, 0.25f, 0);

		
//		for(int i = 0;){
//			
//		}
		
		while(running){
			if (!col.step()) {
				running = false;
			}
			paintImmediately(0, 0, gameWidth, gameHeight);

			try {
				while(paused){
					Thread.sleep(10);
				}
				
				Thread.sleep(sleepSpeed);
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
	public void actionPerformed(ActionEvent a) {
		if(a.getActionCommand().equals("pause")){
			if(!paused){
				paused = true;
				pause.setText("Resume");
			}
			else{
				paused = false;
				pause.setText("Pause");
			}
		}
	}
}
