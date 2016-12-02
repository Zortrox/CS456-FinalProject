/**
 * Created by Zortrox on 11/22/2016.
 */

import java.awt.*;

public class Scent {
	private int m_x;
	private int m_y;
	private double m_foodStrength;
	private double m_colonyStrength;
	private static final double SCENT_DECAY = 0.998;
	private long m_lastSteps;

	public Scent(int x, int y) {
		m_x = x;
		m_y = y;
		m_foodStrength = 0;
		m_colonyStrength = 0;
		m_lastSteps = 0;
	}

	public void draw(Graphics g, long steps) {
		Color color;
		double str = 0;
		double fStr = getFoodStrength(steps);
		double nStr = getColonyStrength(steps);

		if (fStr > nStr) {
			str = fStr;
			if (str < 0) str = 0;
			int alpha = (int)((str / 100 * 255) > 255 ? 255 : str / 100 * 255);
			color = new Color(191, 66, 76, alpha);
		}
		else {
			str = nStr;
			if (str < 0) str = 0;
			int alpha = (int)((str / 100 * 255) > 255 ? 255 : str / 100 * 255);
			color = new Color(56, 172, 30, alpha);
		}
		if (str > 0) {
			g.setColor(color);
			g.drawRect(m_x, m_y, 1, 1);
		}
	}

	public void addColonyStrength(long steps, double strength) {
		m_lastSteps = steps;
		m_colonyStrength += strength;
		if ( m_colonyStrength > 100)  m_colonyStrength = 100;
	}

	public double getColonyStrength(long steps) {
		return  m_colonyStrength * Math.pow(SCENT_DECAY, Math.max(steps - m_lastSteps, 0));
	}

	public void addFoodStrength(long steps, double strength) {
		m_lastSteps = steps;
		m_foodStrength += strength;
		if (m_foodStrength > 100) m_foodStrength = 100;
	}

	public double getFoodStrength(long steps) {
		return m_foodStrength * Math.pow(SCENT_DECAY, Math.max(steps - m_lastSteps, 0));
	}

	public int getX() {
		return m_x;
	}
	public int getY() {
		return m_y;
	}
}
