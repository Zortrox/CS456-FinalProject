/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class Ant {
	private Chromosome m_chromo;
	private double m_currX = 0;
	private double m_currY = 0;
	private double m_angle = -1.0;
	private Point m_movePos = null;
	private double m_score;
	private Random rand = new Random();
	private int m_worldWidth;
	private int m_worldHeight;
	private boolean m_followingScent;

	private class ScentComparator implements Comparator<Scent> {
		@Override
		public int compare(Scent s1, Scent s2) {
			int comp = ((Double)s2.getStrength(0)).compareTo(s1.getStrength(0));

			return comp;
		}
	}

	//create a new ant at position x, y
	public Ant(int x, int y, int width, int height) {
		m_chromo = new Chromosome();
		m_currX = x;
		m_currY = y;
		m_worldWidth = width;
		m_worldHeight = height;
		m_followingScent = false;
	}

	//chromo - chromosome to start out with
	//x, y starting position
	public Ant(Chromosome chromo, int x, int y, int width, int height) {
		m_chromo = new Chromosome(chromo.getGenes());
		m_currX = x;
		m_currY = y;
		m_worldWidth = width;
		m_worldHeight = height;
	}

	public void draw(Graphics g) {
		g.fillRect((int)m_currX, (int)m_currY, 2, 2);
		g.drawLine((int)m_currX, (int)m_currY, (int)(m_currX + 4*Math.cos(m_angle)), (int)(m_currY + 4*Math.sin(m_angle)));
	}

	//move to position x, y
	public void move(int x, int y) {
		m_movePos = new Point(x, y);
	}

	//perform action with current state
	public void step(Scent[][] arrScents, boolean[][] arrFood, long steps) {
		double dist;

		int prevX = (int) m_currX;
		int prevY = (int) m_currY;
		if (prevX >= 0 && prevX < m_worldWidth && prevY >= 0 && prevY < m_worldHeight) {
			arrScents[prevX][prevY].addStrength(steps);
		}

		//temp move conditions
		if (m_movePos == null || (dist = m_movePos.distance(m_currX, m_currY)) < 2 && !m_followingScent) {
			int randX = rand.nextInt(m_worldWidth);
			int randY = rand.nextInt(m_worldHeight);
			if (m_movePos == null) {
				m_angle = Math.atan2(randY, randX);
			}
			m_movePos = new Point(randX, randY);
			dist = m_movePos.distance(m_currX, m_currY);
		} else if ((dist = m_movePos.distance(m_currX, m_currY)) < 2 && m_followingScent) {
			m_followingScent = false;
		}

		//move towards position
		double xDisp = (m_movePos.x - m_currX) / dist;
		double yDisp = (m_movePos.y - m_currY) / dist;
		m_currX = m_currX + xDisp;
		m_currY = m_currY + yDisp;

		double cross = Math.cos(m_angle) * yDisp - Math.sin(m_angle) * xDisp;
		if (cross < 0) {
			m_angle -= 0.1f;
		} else if (cross > 0) {
			m_angle += 0.1f;
		}
		m_angle = Math.atan2(yDisp, xDisp);
		if (m_angle > Math.PI) m_angle -= 2 * Math.PI;

		int antennaeDist = 3;
		double antennaeAngle = 0.2;
		ArrayList<Scent> nearbyScents = new ArrayList<>();

		ArrayList<Scent> scents1 = getScentsInLine(arrScents, antennaeDist, m_angle - antennaeAngle);
		ArrayList<Scent> scents2 = getScentsInLine(arrScents, antennaeDist, m_angle);
		ArrayList<Scent> scents3 = getScentsInLine(arrScents, antennaeDist, m_angle + antennaeAngle);

		//copy all, removing duplicates
		for (int i = 0; i < scents1.size(); i++) {
			if (!nearbyScents.contains(scents1.get(i))) {
				nearbyScents.add(scents1.get(i));
			}
		}
		for (int i = 0; i < scents2.size(); i++) {
			if (!nearbyScents.contains(scents2.get(i))) {
				nearbyScents.add(scents2.get(i));
			}
		}
		for (int i = 0; i < scents3.size(); i++) {
			if (!nearbyScents.contains(scents3.get(i))) {
				nearbyScents.add(scents3.get(i));
			}
		}

		//determine which scent to follow
		if (nearbyScents.size() > 0) {
			Collections.sort(nearbyScents, new ScentComparator());
			if (nearbyScents.get(0).getStrength(steps) > m_chromo.getBravery()) {
				m_movePos.setLocation(nearbyScents.get(0).getX(), nearbyScents.get(0).getY());
				m_followingScent = true;
			}
		}

		/*
		int sX1 = (int)(m_currX + antennaeDist * Math.cos(m_angle - antennaeAngle));
		int sY1 = (int)(m_currY + antennaeDist * Math.sin(m_angle - antennaeAngle));
		int sX2 = (int)(m_currX + antennaeDist * Math.cos(m_angle));
		int sY2 = (int)(m_currY + antennaeDist * Math.sin(m_angle));
		int sX3 = (int)(m_currX + antennaeDist * Math.cos(m_angle + antennaeAngle));
		int sY3 = (int)(m_currY + antennaeDist * Math.sin(m_angle + antennaeAngle));

		//if 3 scents in front of ant are greater than its bravery
		//move towards the strongest one
		Scent scent1 = null;
		Scent scent2 = null;
		Scent scent3 = null;
		if (sX1 >= 0 && sX1 < m_worldWidth && sY1 >= 0 && sY1 < m_worldHeight && arrScents[sX1][sY1].getStrength(steps) > m_chromo.getBravery()) {
			scent1 = arrScents[sX1][sY1];
		}
		if (sX2 >= 0 && sX2 < m_worldWidth && sY2 >= 0 && sY2 < m_worldHeight && arrScents[sX2][sY2].getStrength(steps) > m_chromo.getBravery()) {
			scent2 = arrScents[sX2][sY2];
		}
		if (sX3 >= 0 && sX3 < m_worldWidth && sY3 >= 0 && sY3 < m_worldHeight && arrScents[sX3][sY3].getStrength(steps) > m_chromo.getBravery()) {
			scent3 = arrScents[sX3][sY3];
		}
		if (scent1 != null && scent2 != null && scent3 != null) {
			if (scent2.getStrength(steps) >= scent1.getStrength(steps)) {
				if (scent2.getStrength(steps) >= scent3.getStrength(steps)) {
					m_movePos.setLocation(sX2, sY2);
				} else {
					m_movePos.setLocation(sX3, sY3);
				}
			} else if (scent1.getStrength(steps) >= scent3.getStrength(steps)) {
				m_movePos.setLocation(sX1, sY1);
			} else {
				m_movePos.setLocation(sX3, sY3);
			}
			m_followingScent = true;
		} else if (scent1 != null && scent2 != null) {
			if (scent2.getStrength(steps) >= scent1.getStrength(steps)) {
				m_movePos.setLocation(sX2, sY2);
			} else {
				m_movePos.setLocation(sX1, sY1);
			}
			m_followingScent = true;
		} else if (scent3 != null && scent2 != null) {
			if (scent2.getStrength(steps) >= scent3.getStrength(steps)) {
				m_movePos.setLocation(sX2, sY2);
			} else {
				m_movePos.setLocation(sX3, sY3);
			}
			m_followingScent = true;
		} else if (scent1 != null && scent3 != null) {
			if (scent1.getStrength(steps) >= scent3.getStrength(steps)) {
				m_movePos.setLocation(sX1, sY1);
			} else {
				m_movePos.setLocation(sX3, sY3);
			}
			m_followingScent = true;
		} else if (scent1 != null) {
			m_movePos.setLocation(sX1, sY1);
			m_followingScent = true;
		} else if (scent2 != null){
			m_movePos.setLocation(sX2, sY2);
			m_followingScent = true;
		}  else if (scent3 != null){
			m_movePos.setLocation(sX3, sY3);
			m_followingScent = true;
		} else if (m_followingScent) {
			//if following a scent, but no more scent found
			//REWORKING TO ADD ANGLE SO ANTS AREN'T JUMPY

			ArrayList<Scent> nearbyScents = new ArrayList<>();
			double[][] scentLocs = {
					{m_currX, m_currY - 2},
					{m_currX - 1, m_currY - 1},
					{m_currX, m_currY - 1},
					{m_currX + 1, m_currY - 1},
					{m_currX - 2, m_currY},
					{m_currX - 1, m_currY},
					{m_currX + 1, m_currY},
					{m_currX + 2, m_currY},
					{m_currX - 1, m_currY + 1},
					{m_currX, m_currY + 1},
					{m_currX + 1, m_currY + 1},
					{m_currX, m_currY + 2}};

			for (int i = 0; i < scentLocs.length; i++) {
				if (scentLocs[i][0] >= 0 && scentLocs[i][0] < m_worldWidth && scentLocs[i][1] >= 0 && scentLocs[i][1] < m_worldHeight) {
					if (arrScents[ (int)scentLocs[i][0] ][ (int)scentLocs[i][1] ].getStrength(steps) > m_chromo.getBravery()) {
						nearbyScents.add(arrScents[ (int)scentLocs[i][0] ][ (int)scentLocs[i][1] ]);
					}
				}
			}
			if (nearbyScents.size() > 0) {
				Collections.sort(nearbyScents, new ScentComparator());
				int scentX = nearbyScents.get(0).getX();
				int scentY = nearbyScents.get(0).getY();
				if (Math.abs((Math.atan2(scentY, scentX) % (2 * 3.14159)) - (m_angle % (2 * 3.14159))) < 0.5f && nearbyScents.size() > 1) {
					m_movePos.setLocation(nearbyScents.get(1).getX(), nearbyScents.get(1).getY());
				} else {
					m_movePos.setLocation(scentX, scentY);
				}
			} else {
				m_followingScent = false;
			}
		} else {
			m_followingScent = false;
		}*/

	}

	public void cross(Ant other) {
		m_chromo.cross(other.getChromosome());
	}

	//get chromosome to cross
	public Chromosome getChromosome() {
		return m_chromo;
	}

	public void mutate(double percent) {
		m_chromo.mutate(percent);
	}

	//get the score to sort ant performance
	public double getScore() {
		return m_score;
	}

	public Point getPosition() {
		return new Point((int)m_currX, (int)m_currY);
	}

	//Bresenham's line algorithm
	public ArrayList<Scent> getScentsInLine(Scent[][] arrScents, int dist, double angle) {
		ArrayList<Scent> lineScents = new ArrayList<>();

		int x = (int)m_currX;
		int y = (int)m_currY;
		int w = (int)(dist * Math.cos(angle));
		int h = (int)(dist * Math.sin(angle));
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
		if (w < 0) dx1 = -1; else if (w > 0) dx1 = 1;
		if (h < 0) dy1 = -1; else if (h > 0) dy1 = 1;
		if (w < 0) dx2 = -1; else if (w > 0) dx2 = 1;
		int longest = Math.abs(w) ;
		int shortest = Math.abs(h) ;
		if (longest <= shortest) {
			longest = Math.abs(h) ;
			shortest = Math.abs(w) ;
			if (h < 0) dy2 = -1; else if (h > 0) dy2 = 1;
			dx2 = 0 ;
		}
		int numerator = longest >> 1 ;
		for (int i=0;i<=longest;i++) {
			if (x >= 0 && x < m_worldWidth && y >= 0 && y < m_worldHeight) {
				lineScents.add(arrScents[x][y]);
			}
			numerator += shortest ;
			if (!(numerator<longest)) {
				numerator -= longest ;
				x += dx1 ;
				y += dy1 ;
			} else {
				x += dx2 ;
				y += dy2 ;
			}
		}

		return lineScents;
	}
}
