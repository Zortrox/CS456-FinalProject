/**
 * Created by Zortrox on 11/8/2016.
 */

import java.util.Random;

public class Chromosome {

	//"genes" that change the ant behavior
	private int m_supplymind;	//how much supply the colony has
	private int m_scentmind;	//not used
	private int m_sourcemind;	//not used
	private int m_bravery;		//whether to follow a trail or forge its own path
	private int m_stubborness;	//not used
	private int m_frustration;	//how long to follow a path

	//max values for the genes
	private static final int MAX_SUPPLY = Colony.MAX_SUPPLY;
	private static final int MAX_SCENT = 101;
	private static final int MAX_SOURCE = 100;
	private static final int MAX_BRAVERY = 101;
	private static final int MAX_STUBBORN = 101;
	private static final int MAX_FRUSTRATE = 10000;

	private Random rand = new Random();

	public Chromosome() {
		randomize();
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
	//mutates the genes by 0-5% based on max gene amount
	public void mutate(double percent) {
		if (rand.nextDouble() < percent) {
			int change = rand.nextInt((int)(MAX_SUPPLY * 0.05)) + 1;
			if (rand.nextDouble() > 0.5) {
				m_supplymind += change;
				if (m_supplymind > MAX_SUPPLY) m_supplymind = MAX_SUPPLY;
			}
			else {
				m_supplymind -= change;
				if (m_supplymind < 1) m_supplymind = 1;
			}
		}
		if (rand.nextDouble() < percent) {
			int change = rand.nextInt((int)(MAX_SCENT * 0.05)) + 1;
			if (rand.nextDouble() > 0.5) {
				m_scentmind += change;
				if (m_scentmind >= MAX_SCENT) m_scentmind = MAX_SCENT - 1;
			}
			else {
				m_scentmind -= change;
				if (m_scentmind < 0) m_scentmind = 0;
			}
		}
		if (rand.nextDouble() < percent) {
			int change = rand.nextInt((int)(MAX_SOURCE * 0.05)) + 1;
			if (rand.nextDouble() > 0.5) {
				m_sourcemind += change;
				if (m_sourcemind > MAX_SOURCE) m_sourcemind = MAX_SOURCE;
			}
			else {
				m_sourcemind -= change;
				if (m_sourcemind < 1) m_sourcemind = 1;
			}
		}
		if (rand.nextDouble() < percent) {
			int change = rand.nextInt((int)(MAX_BRAVERY * 0.05)) + 1;
			if (rand.nextDouble() > 0.5) {
				m_bravery += change;
				if (m_bravery >= MAX_BRAVERY) m_bravery = MAX_BRAVERY - 1;
			}
			else {
				m_bravery -= change;
				if (m_bravery < 0) m_bravery = 0;
			}
		}
		if (rand.nextDouble() < percent) {
			int change = rand.nextInt((int)(MAX_STUBBORN * 0.05)) + 1;
			if (rand.nextDouble() > 0.5) {
				m_stubborness += change;
				if (m_stubborness >= MAX_STUBBORN) m_stubborness = MAX_STUBBORN - 1;
			}
			else {
				m_stubborness -= change;
				if (m_stubborness < 0) m_stubborness = 0;
			}
		}
		if (rand.nextDouble() < percent) {
			int change = rand.nextInt((int)(MAX_FRUSTRATE * 0.05)) + 1;
			if (rand.nextDouble() > 0.5) {
				m_frustration += change;
				if (m_frustration >= MAX_FRUSTRATE) m_frustration = MAX_FRUSTRATE - 1;
			}
			else {
				m_frustration -= change;
				if (m_frustration < 0) m_frustration = 0;
			}
		}
	}

	//initializes the genes to random values up to the gene's max
	public void randomize() {
		m_supplymind = rand.nextInt(Colony.MAX_SUPPLY) + 1;
		m_scentmind = rand.nextInt(MAX_SCENT);
		m_sourcemind = rand.nextInt(MAX_SOURCE) + 1;
		m_bravery = rand.nextInt(MAX_BRAVERY);
		m_stubborness = rand.nextInt(MAX_STUBBORN);
		m_frustration = rand.nextInt(MAX_FRUSTRATE);
	}

	//cross one chromosome to another
	//randomly chooses whether to accept the current gene or the new one (50/50)
	public void cross(Chromosome other) {
		int[] otherGenes = other.getGenes();

		if (rand.nextDouble() < 0.5) m_supplymind = otherGenes[0];
		if (rand.nextDouble() < 0.5) m_scentmind = otherGenes[1];
		if (rand.nextDouble() < 0.5) m_sourcemind = otherGenes[2];
		if (rand.nextDouble() < 0.5) m_bravery = otherGenes[3];
		if (rand.nextDouble() < 0.5) m_stubborness = otherGenes[4];
		if (rand.nextDouble() < 0.5) m_frustration = otherGenes[5];
	}

	//returns all genes in an array
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

	//return each gene individually
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
