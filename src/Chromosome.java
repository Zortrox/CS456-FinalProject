/**
 * Created by Zortrox on 11/8/2016.
 */

public class Chromosome {

	private int m_supplymind;
	private int m_scentmind;
	private int m_sourcemind;
	private int m_bravery;
	private int m_stubborness;
	private int m_frustration;

	public Chromosome() {

	}
	public Chromosome(int[] genes) {

	}

	public void mutate() {

	}
	public Chromosome cross(Chromosome other) {
		return new Chromosome();
	}

	public int getSupplyMind() {
		return m_supplymind;
	}
}
