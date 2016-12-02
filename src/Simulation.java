
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
	private int stepLimit = 50000;
	private Colony col = new Colony(335, 335, gameWidth, gameHeight, 500, 0.25f, 0);
	private int bestEval = -100000; //best score
	private int sleepSpeed = 0;
	private boolean paused = false;
	private JTextArea bestColonyText;

	private JButton pause = new JButton("Pause");
	private JButton lines = new JButton("Trails (On)");
	private JButton draw = new JButton("Draw (On)");

	private class DrawArea extends JPanel implements ActionListener {
		public void paint(Graphics g) {
			g.setColor(new Color(226, 203, 183));
			g.fillRect(0, 0, gameWidth, gameHeight);
			col.draw(g);
		}

		@Override
		public void actionPerformed(ActionEvent a) {
			//pause and unpause
			if (a.getActionCommand().equals("pause")) {
				if (!paused) {
					paused = true;
					pause.setText("Resume");
				} else {
					paused = false;
					pause.setText("Pause");
				}
			}

			//turn lines on or off
			else if (a.getActionCommand().equals("lines")) {
				col.drawLines = !col.drawLines;

				if (col.drawLines) {
					lines.setText("Trails (On)");
				} else {
					lines.setText("Trails (Off)");
				}
			}

			//turn all drawing on or off
			else if (a.getActionCommand().equals("draw")) {
				col.draw = !col.draw;

				if (col.draw) {
					draw.setText("Draw (On)");
				} else {
					draw.setText("Draw (Off)");
				}
			}
		}
	}

	public static void main(String args[]) {
		new Simulation();
	}

	public Simulation() {
		// sets up the frame
		JFrame frame = new JFrame();
		frame.setTitle("Ant Colony Simulation");
		frame.setResizable(false);
		frame.setSize(gameWidth + 260, gameHeight + 30);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//writes best colony values to file
				try {
					FileWriter fw = new FileWriter("Best Colony.txt");
					fw.write(col.getGen() + "\n");
					fw.flush();

					ArrayList<Ant> ants = col.getAnts();
					for (int i = 0; i < ants.size(); i++) {
						Chromosome c = ants.get(i).getChromosome();

						fw.write(c.getSupplyMind() + " " + c.getScentMind() + " " + c.getSourceMind() + " "
								+ c.getBravery() + " " + c.getStubbornness() + " " + c.getFrustration() + "\n");
					}

					fw.close();

				} catch (IOException e1) {
					e1.printStackTrace();
				}

				System.exit(0);
			}
		});

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(gameWidth);
		pane.setDividerSize(0);

		// simulation area
		DrawArea drawArea = new DrawArea();
		pane.add(drawArea);

		// controls
		JPanel controlPanel = new JPanel();

		//pause button
		pause.setActionCommand("pause");
		pause.addActionListener(drawArea);
		controlPanel.add(pause);

		//lines button
		lines.setActionCommand("lines");
		lines.addActionListener(drawArea);
		controlPanel.add(lines);

		//drawing button
		draw.setActionCommand("draw");
		draw.addActionListener(drawArea);
		controlPanel.add(draw);

		////text areas for current, previous and best colonies
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

		//add current colony text
		colonyInfo(colonyText, col);

		//run the sim
		while (running) {
			//if the colony is not alive or is over the step limit
			if (!col.step() || col.getTotalSteps() > stepLimit) {
				//evaluate
				col.evaluate();
				previousColonyText.setText("Previous Colony Info:\n");
				colonyInfo(previousColonyText, col);

				//update best colony
				if (bestEval < col.getEvaluation()) {
					bestEval = col.getEvaluation();
					bestColonyText.setText("Best Colony:\n");
					colonyInfo(bestColonyText, col);
				}

				//get the next generation
				col.newGeneration();

				colonyText.setText("Current Colony Info:\n");
				colonyInfo(colonyText, col);
			}

			//draw the colony
			if (col.draw) {
				drawArea.paintImmediately(0, 0, gameWidth, gameHeight);
			} else {
				drawArea.repaint();
			}

			//pause
			try {
				while (paused) {
					Thread.sleep(10);
				}

				Thread.sleep(sleepSpeed);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//prints the information of colony col to the given text area
	public static void colonyInfo(JTextArea colonyText, Colony col) {
		colonyText.append("Generation: " + col.getGen() + "\n");
		colonyText.append("Total Ants: " + col.getNumAnts() + "\n");

		if (col.hasEvaluation) {
			colonyText.append("Colony Evaluation: " + col.getEvaluation() + "\n");
		}

		colonyText.append("\nAnts:\n\n");

		ArrayList<Ant> ants = col.getAnts();
		for (int i = 0; i < ants.size(); i++) {
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
