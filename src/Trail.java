/**
 * Created by Zortrox on 11/21/2016.
 */

import java.awt.*;

public class Trail {

	private Point m_startPoint;
	private Ant m_ant;
	private Point m_endPoint;
	private double m_startStrength;
	private double m_endStrength;

	public Trail(int startX, int startY, Ant ant) {
		m_ant = ant;
		m_startPoint = new Point(startX, startY);
		m_endPoint = m_ant.getPosition();
		m_startStrength = 100.0f;
		m_endStrength = 100.0f;
	}

	public void draw(Graphics g) {
		if (m_endStrength > 0) {
			g.drawLine(m_startPoint.x, m_startPoint.y, m_endPoint.x, m_endPoint.y);
		}
	}

	public void step() {
		m_startStrength -= 0.1f;
		if (m_ant == null) {
			m_endStrength -= 0.1f;
		} else {
			m_endPoint = m_ant.getPosition();
		}
	}

	public void end() {
		m_ant = null;
	}

}
