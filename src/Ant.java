/**
 * Created by Zortrox on 11/8/2016.
 */

import java.awt.*;
import java.util.*;

public class Ant {
	private Chromosome m_chromo;
	private double m_currX = 0;
	private double m_currY = 0;
	private double m_angle;
	private Point m_movePos = null;
	private double m_score;
	private Random rand = new Random();
	private int m_worldWidth;
	private int m_worldHeight;
	private boolean m_followingFood;
	private double m_lastFoodScent;
	private double m_lastColonyScent;
	private int m_pathLength;
	private boolean m_hasFood;
	private Colony m_col = null;

	private static final double SCENT_DECAY = 0.99;

	private class ColonyScentComparator implements Comparator<Scent> {
		@Override
		public int compare(Scent s1, Scent s2) {
			int comp = ((Double)s2.getColonyStrength(0)).compareTo(s1.getColonyStrength(0));
			return comp;
		}
	}
	private class FoodScentComparator implements Comparator<Scent> {
		@Override
		public int compare(Scent s1, Scent s2) {
			int comp = ((Double)s2.getFoodStrength(0)).compareTo(s1.getFoodStrength(0));
			return comp;
		}
	}
	private class FoodDistComparator implements Comparator<Point> {
		@Override
		public int compare(Point p1, Point p2) {
			int comp = ((Double)p1.distance(m_currX, m_currY)).compareTo(p2.distance(m_currX, m_currY));
			return comp;
		}
	}

	//create a new ant at position x, y
	public Ant(int x, int y, Colony col) {
		m_chromo = new Chromosome();
		m_col = col;
		m_worldWidth = m_col.getWorldWidth();
		m_worldHeight = m_col.getWorldHeight();

		reset();
	}

	//chromo - chromosome to start out with
	//x, y starting position
	public Ant(Chromosome chromo, int x, int y, Colony col) {
		m_chromo = new Chromosome(chromo.getGenes());
		m_col = col;
		m_worldWidth = m_col.getWorldWidth();
		m_worldHeight = m_col.getWorldHeight();

		reset();
	}

	public void reset() {
		m_currX = m_col.getX();
		m_currY = m_col.getY();
		m_angle = rand.nextDouble() * 2 * Math.PI - Math.PI;
		m_followingFood = true;
		m_hasFood = false;
		m_score = 0;
		m_pathLength = 0;
		m_lastFoodScent = 0;
		m_lastColonyScent = 0;
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
	public boolean step(Scent[][] arrScents, boolean[][] arrFood, long steps) {
		boolean gotFood = false;
		double dist;

		int antennaeDist = 5;
		double antennaeAngle = Math.PI / 5.0f;

		int pixelX = (int)(m_currX + 0.5f);
		int pixelY = (int)(m_currY + 0.5f);
		if (pixelX > m_worldWidth - 1) pixelX = m_worldWidth - 1;
		if (pixelX < 0) pixelX = 0;
		if (pixelY > m_worldHeight - 1) pixelY = m_worldHeight - 1;
		if (pixelY < 0) pixelY = 0;

		//update food scent trail
		m_lastFoodScent *= SCENT_DECAY;
		if (arrFood[pixelX][pixelY]) {
			if (!m_hasFood) {
				arrFood[pixelX][pixelY] = false;
				m_score += 10;
				m_hasFood = true;
				m_followingFood = false;

				m_angle -= Math.PI;
				if (m_angle < -Math.PI) m_angle += 2 * Math.PI;
				m_movePos = null;
			}

			arrScents[pixelX][pixelY].addFoodStrength(steps, 100);
			m_lastFoodScent = 100;
		}

		//update colony scent trail
		m_lastColonyScent *= SCENT_DECAY;
		if (m_col.getPos().distance(pixelX, pixelY) < 5) {
			if (!m_followingFood) {
				gotFood = true;
				m_score += 50;
				m_hasFood = false;
				m_followingFood = true;

				m_angle -= Math.PI;
				if (m_angle < -Math.PI) m_angle += 2 * Math.PI;
				m_movePos = null;
			}

			arrScents[pixelX][pixelY].addColonyStrength(steps, 100);
			m_lastColonyScent = 100;
		}

		if (m_movePos == null || (dist = m_movePos.distance(pixelX, pixelY)) < 2) {
			boolean bNotMoved = true;
			if (m_movePos != null) {
				m_currX = m_movePos.x;
				m_currY = m_movePos.y;
			}

			ArrayList<Scent> nearbyScents = new ArrayList<>();
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle);
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle - antennaeAngle);
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle + antennaeAngle);
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle - 2.0 * antennaeAngle);
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle + 2.0 * antennaeAngle);
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle - 3.0 * antennaeAngle);
			getScentsInLine(arrScents, nearbyScents, antennaeDist, m_angle + 3.0 * antennaeAngle);

			if (nearbyScents.size() > 0) {
				//create new object if no movement position specified yet
				if (m_movePos == null) {
					m_movePos = new Point(0, 0);
				}

				//move towards strongest food or colony scent
				if (m_followingFood) {
					//follow scent to food
					Collections.sort(nearbyScents, new FoodScentComparator());
					if (nearbyScents.get(0).getFoodStrength(steps) > followPathStrength()) {
						m_movePos.setLocation(nearbyScents.get(0).getX(), nearbyScents.get(0).getY());
						bNotMoved = false;
						m_pathLength++;
					}
				} else {
					//follow scent to nest
					Collections.sort(nearbyScents, new ColonyScentComparator());
					if (nearbyScents.get(0).getColonyStrength(steps) > followPathStrength()) {
						m_movePos.setLocation(nearbyScents.get(0).getX(), nearbyScents.get(0).getY());
						bNotMoved = false;
						m_pathLength++;
					}
				}
			}

			if (!m_hasFood) {
				//if food has entered field of smell, go to it
				ArrayList<Point> nearbyFood = new ArrayList<>();
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle);
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle - antennaeAngle);
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle + antennaeAngle);
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle - 2.0 * antennaeAngle);
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle + 2.0 * antennaeAngle);
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle - 3.0 * antennaeAngle);
				getFoodInLine(arrFood, nearbyFood, antennaeDist, m_angle + 3.0 * antennaeAngle);

				if (nearbyFood.size() > 0) {
					Collections.sort(nearbyFood, new FoodDistComparator());
					m_movePos.setLocation(nearbyFood.get(0));
				}
			}

			//if haven't moved, try somewhere random
			if (bNotMoved) {
				int randX = rand.nextInt(m_worldWidth);
				int randY = rand.nextInt(m_worldHeight);
				if (m_movePos == null) {
					m_angle = Math.atan2(randY, randX);
				}
				m_movePos = new Point(randX, randY);

				m_pathLength = 0;
			}

			dist = m_movePos.distance(pixelX, pixelY);
		}

		//update scent trails
		addFoodScent(arrScents, pixelX, pixelY, steps);
		addColonyScent(arrScents, pixelX, pixelY, steps);

		//move towards position
		double deltaX = (m_movePos.x - pixelX) / (dist>.1?dist:.1);
		double deltaY = (m_movePos.y - pixelY) / (dist>.1?dist:.1);
		m_currX = m_currX + deltaX;
		m_currY = m_currY + deltaY;
		m_angle = Math.atan2(deltaY, deltaX);
		if (m_angle > Math.PI) m_angle -= 2 * Math.PI;

		int prevPixelX = pixelX;
		int prevPixelY = pixelY;
		pixelX = (int)(m_currX + 0.5f);
		pixelY = (int)(m_currY + 0.5f);
		if (pixelX > m_worldWidth - 1) pixelX = m_worldWidth - 1;
		if (pixelX < 0) pixelX = 0;
		if (pixelY > m_worldHeight - 1) pixelY = m_worldHeight - 1;
		if (pixelY < 0) pixelY = 0;

		//update to use new scents
		if (arrScents[pixelX][pixelY].getColonyStrength(steps + 1) > m_lastColonyScent) {
			m_lastColonyScent = arrScents[pixelX][pixelY].getColonyStrength(steps + 1);
		}
		if (arrScents[pixelX][pixelY].getFoodStrength(steps + 1) > m_lastFoodScent) {
			m_lastFoodScent = arrScents[pixelX][pixelY].getFoodStrength(steps + 1);
		}

		//return if food was added to colony
		return gotFood;
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
	public void setPosition(int x, int y) {
		m_currX = x;
		m_currY = y;
	}

	//Bresenham's line algorithm
	private void getScentsInLine(Scent[][] arrScents, ArrayList<Scent> nearbyScents, int dist, double angle) {
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
				if (!nearbyScents.contains(arrScents[x][y]) && x != (int)(m_currX + 0.5f) && y != (m_currY + 0.5f)) {
					nearbyScents.add(arrScents[x][y]);
				}
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
	}

	private void getFoodInLine(boolean[][] arrFood, ArrayList<Point> nearbyFood, int dist, double angle) {
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
				Point foodLoc = new Point(x, y);
				if (arrFood[x][y] && !nearbyFood.contains(foodLoc)) {
					nearbyFood.add(foodLoc);
				}
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
	}

	private void addColonyScent(Scent[][] arrScents, int x, int y, long steps) {
		arrScents[x][y].addColonyStrength(steps, m_lastColonyScent * SCENT_DECAY);
	}

	private void addFoodScent(Scent[][] arrScents, int x, int y, long steps) {
		arrScents[x][y].addFoodStrength(steps, m_lastFoodScent * SCENT_DECAY);
	}

	//determine if path strength is strong enough to follow
	private int followPathStrength() {
		int strength = m_chromo.getBravery();
		strength *= Math.min(1, 1.0f * m_col.getSupply() / m_chromo.getSupplyMind());

		//don't follow the path if frustrated
		if (m_pathLength > m_chromo.getFrustration()) {
			strength = 100;
		}

		return strength;
	}
}
