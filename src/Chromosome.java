/**
 * Created by Zortrox on 11/8/2016.
 */

import java.util.Random;

public class Chromosome {

	private int m_supplymind;
	private int m_scentmind;
	private int m_sourcemind;
	private int m_bravery;
	private int m_stubborness;
	private int m_frustration;

	private Random rand = new Random();

	public Chromosome() {
		mutate(1.0f);
	}
	public Chromosome(int[] genes) {
		m_supplymind = genes[0];
		m_scentmind = genes[1];
		m_sourcemind = genes[2];
		m_bravery = genes[3];
		m_stubborness = genes[4];
		m_frustration = genes[5];
	}

	//percent - between 0 and 1
	public void mutate(double percent) {
		if (rand.nextDouble() < percent) m_supplymind = rand.nextInt(2000) + 1;
		if (rand.nextDouble() < percent) m_scentmind = rand.nextInt(101);
		if (rand.nextDouble() < percent) m_sourcemind = rand.nextInt(100) + 1;
		if (rand.nextDouble() < percent) m_bravery = rand.nextInt(101);
		if (rand.nextDouble() < percent) m_stubborness = rand.nextInt(101);
		if (rand.nextDouble() < percent) m_frustration = rand.nextInt(101);
	}

	public void cross(Chromosome other) {
		int[] otherGenes = other.getGenes();

		if (rand.nextDouble() < 0.5) m_supplymind = otherGenes[0];
		if (rand.nextDouble() < 0.5) m_scentmind = otherGenes[1];
		if (rand.nextDouble() < 0.5) m_sourcemind = otherGenes[2];
		if (rand.nextDouble() < 0.5) m_bravery = otherGenes[3];
		if (rand.nextDouble() < 0.5) m_stubborness = otherGenes[4];
		if (rand.nextDouble() < 0.5) m_frustration = otherGenes[5];
	}

	public int[] getGenes() {
		int[] gene = new int[6];

		gene[0] = m_supplymind;
		gene[1] = m_scentmind;
		gene[2] = m_sourcemind;
		gene[3] = m_bravery;
		gene[4] = m_stubborness;
		gene[5] = m_frustration;

		return gene;
	}

	public int getSupplyMind() {
		return m_supplymind;
	}
	public int getScentMind() {
		return m_scentmind;
	}
	public int getSourceMind() {
		return m_sourcemind;
	}
	public int getBravery() {
		return m_bravery;
	}
	public int getStubbornness() {
		return m_stubborness;
	}
	public int getFrustration() {
		return m_frustration;
	}
}
