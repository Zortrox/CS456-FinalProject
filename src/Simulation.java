/**
 * Created by Zortrox on 11/7/2016.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class Simulation extends JPanel implements ActionListener {

	private static JFrame frame;
	private JPanel controlPanel = new JPanel();
	private boolean running = true;
	private static int gameWidth = 670;
	private static int gameHeight = 670;
	private Colony col;
	private Colony bestColonly;
	private int sleepSpeed = 10;
	private boolean paused = false;
	private int generation = 0;
	private JTextArea colonyText = new JTextArea("Current Colony Info:\n", 10, 20);
	private JTextArea previousColonyText = new JTextArea("Previous Colony Info:\n", 10, 20);
	private JTextArea bestColonyText = new JTextArea("Best Colony Info:\n", 10, 20);
	
	JButton pause = new JButton("Pause");

	public static void main(String args[]){
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

		new Simulation();
	}

	public Simulation(){
		colonyText.setEditable(false);
		previousColonyText.setEditable(false);
		bestColonyText.setEditable(false);
		JScrollPane colonyScroll = new JScrollPane(colonyText);
		JScrollPane previousColonyScroll = new JScrollPane(previousColonyText);
		JScrollPane bestColonyScroll = new JScrollPane(bestColonyText);
		
		controlPanel.add(pause);
		controlPanel.add(new JButton("Start"));
		controlPanel.add(colonyScroll);
		controlPanel.add(previousColonyScroll);
		controlPanel.add(bestColonyScroll);
		
		pane.add(this);
		pane.add(controlPanel);
		
		pause.setActionCommand("pause");
		pause.addActionListener(this);
		
		frame.add(pane);

		col = new Colony(300, 300, gameWidth, gameHeight, 100, 0.25f, 0);
		
		colonyInfo(colonyText, col);
		
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
	
	public void colonyInfo(JTextArea colonyText, Colony col){
		colonyText.append("Generation: " + col.getGen() + "\n");
		colonyText.append("Total Ants: " + col.getNumAnts() + "\n");
		colonyText.append("\nAnts:\n\n");

		ArrayList<Ant> ants = col.getAnts();
		for(int i = 0; i < ants.size(); i++){
			colonyText.append("Ant " + i + ":\n");
			colonyText.append("    Frustration: " + ants.get(i).getChromosome().getFrustration() + "\n");
			colonyText.append("    Bravery: " + ants.get(i).getChromosome().getBravery() + "\n");
			colonyText.append("    Scent Mind: " + ants.get(i).getChromosome().getScentMind() + "\n");
			colonyText.append("    Source Mind: " + ants.get(i).getChromosome().getSourceMind() + "\n");
			colonyText.append("    Stubbornness: " + ants.get(i).getChromosome().getStubbornness() + "\n");
			colonyText.append("    Supply Mind: " + ants.get(i).getChromosome().getSupplyMind() + "\n\n");
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
