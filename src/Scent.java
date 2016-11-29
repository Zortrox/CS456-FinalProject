/**
 * Created by Zortrox on 11/22/2016.
 */

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Scent {
	private int m_x;
	private int m_y;
	private double m_strength;
	private long m_lastSteps;

	public Scent(int x, int y) {
		m_x = x;
		m_y = y;
		m_strength = 0;
		m_lastSteps = 0;
	}

	public void draw(Graphics g, long steps) {
		//m_lastSteps++;
		double str = getStrength(steps);
		if (str > 0) {
			int alpha = (int)(str / 100 * 255) > 255 ? 255 : (int)(str / 500 * 255);
			g.setColor(new Color(255, 150, 150, alpha));
			g.drawRect(m_x, m_y, 1, 1);
		}
	}

	public void addStrength(long steps) {
		m_lastSteps = steps;
		m_strength += 20;
	}

	public double getStrength(long steps) {
		return m_strength - Math.max(0.1 * (steps - m_lastSteps), 0);
	}
}
