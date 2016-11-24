/**
 * Created by Zortrox on 11/7/2016.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class Simulation {
	private boolean running = true;
	private int gameWidth = 670;
	private int gameHeight = 670;
	private Colony col = new Colony(300, 300, gameWidth, gameHeight, 20, 0.25f, 0);
	private Colony bestColony;
	private int sleepSpeed = 10;
	private boolean paused = false;
	private int generation = 0;
	private JTextArea bestColonyText;

	private JButton pause = new JButton("Pause");

	private class DrawArea extends JPanel implements ActionListener {
		public void paint (Graphics g) {
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

	public static void main(String args[]){
		new Simulation();
	}

	public Simulation(){
		//sets up the frame
		JFrame frame = new JFrame();
		frame.setTitle("Ant Colony Simulation");
		frame.setResizable(false);
		frame.setSize(gameWidth + 260, gameHeight + 30);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(bestColony != null){
					try {
						FileWriter fw = new FileWriter("Best Colony.txt");
						
						ArrayList<Ant> ants = col.getAnts();
						for(int i = 0; i < ants.size(); i++){
							fw.write("Ant " + i + ":\n");
							fw.write("    Frustration: " + ants.get(i).getChromosome().getFrustration() + "\n");
							fw.write("    Bravery: " + ants.get(i).getChromosome().getBravery() + "\n");
							fw.write("    Scent Mind: " + ants.get(i).getChromosome().getScentMind() + "\n");
							fw.write("    Source Mind: " + ants.get(i).getChromosome().getSourceMind() + "\n");
							fw.write("    Stubbornness: " + ants.get(i).getChromosome().getStubbornness() + "\n");
							fw.write("    Supply Mind: " + ants.get(i).getChromosome().getSupplyMind() + "\n\n");
						}
						
						fw.close();

					} catch (IOException e1) {e1.printStackTrace();}
				}
				
				System.exit(0);
			}
		});

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(gameWidth);
		pane.setDividerSize(0);

		//simulation area
		DrawArea drawArea = new DrawArea();
		pane.add(drawArea);

		//controls
		JPanel controlPanel = new JPanel();

		pause.setActionCommand("pause");
		pause.addActionListener(drawArea);
		controlPanel.add(pause);

		controlPanel.add(new JButton("Start"));

		JTextArea colonyText = new JTextArea("Current Colony Info:\n", 10, 20);
		colonyText.setEditable(false);
		JScrollPane colonyScroll = new JScrollPane(colonyText);
		controlPanel.add(colonyScroll);

		JTextArea previousColonyText = new JTextArea("Previous Colony Info:\n", 10, 20);
		previousColonyText.setEditable(false);
		JScrollPane previousColonyScroll = new JScrollPane(previousColonyText);
		controlPanel.add(previousColonyScroll);

		bestColonyText = new JTextArea("Best Colony Info:\n", 10, 20);
		bestColonyText.setEditable(false);
		JScrollPane bestColonyScroll = new JScrollPane(bestColonyText);
		controlPanel.add(bestColonyScroll);

		pane.add(controlPanel);
		
		frame.add(pane);
		frame.setVisible(true);
		
		colonyInfo(colonyText, col);

		while(running){
			if (!col.step()) {
				running = false;
			}
			drawArea.paintImmediately(0, 0, gameWidth, gameHeight);

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
	
	public static void colonyInfo(JTextArea colonyText, Colony col) {
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
}
