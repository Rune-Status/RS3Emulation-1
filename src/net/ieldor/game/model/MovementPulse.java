/*
 * This file is part of Ieldor.
 *
 * Ieldor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ieldor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ieldor.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ieldor.game.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import net.ieldor.game.model.player.Player;

/**
 * An cycle that is used to process the players movement.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class MovementPulse {

	/**
	 * The maximum size of the queue. If any additional steps are added, they
	 * are discarded.
	 */
	private static final int MAXIMUM_SIZE = 128;

	/**
	 * Represents a single point in the queue.
	 * 
	 * @author Graham Edgecombe
	 */
	private static final class Point {

		/**
		 * The point's position.
		 */
		private final Position position;

		/**
		 * The direction to walk to this point.
		 */
		private final Direction direction;

		/**
		 * Creates a point.
		 * 
		 * @param position
		 *            The position.
		 * @param direction
		 *            The direction.
		 */
		public Point(Position position, Direction direction) {
			this.position = position;
			this.direction = direction;
		}

		@Override
		public String toString() {
			return Point.class.getName() + " [direction=" + direction + ", position=" + position + "]";
		}
	}
	
	/**
	 * The queue of directions.
	 */
	private Deque<Point> points = new ArrayDeque<Point>();

	/**
	 * The old queue of directions.
	 */
	private Deque<Point> oldPoints = new ArrayDeque<Point>();

	/**
	 * Flag indicating if this queue (only) should be ran.
	 */
	private boolean runningQueue;

	/**
	 * The entity for this movement cycle.
	 */
	private Entity entity;
	
	/**
	 * Creates a new EntityMovementCycle for the entity. 
	 * @param entity The entity we're creating for.
	 */
	public MovementPulse(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Called every pulse, updates the queue.
	 */
	public void pulse() {
		Position position = entity.getPosition();
		Direction first = Direction.NONE;
		Direction second = Direction.NONE;
		Point next = points.poll();
		if (next != null) {
			first = next.direction;
			position = next.position;
			
			if (runningQueue || entity.isRunning()) {
				if (entity.getRunEnergy() > 0) {
					next = points.poll();
					if (next != null) {
						second = next.direction;
						position = next.position;
						if (!(entity instanceof Player))
							entity.setRunEnergy(entity.getRunEnergy() - 1);
					}
				} else {
					if (entity.isRunning()) {
						entity.setRunning(false);
						runningQueue = false;
					}
					runningQueue = false;
				}
			}
		}	
		int rx = entity.getLastPosition().getRegionX();
		int ry = entity.getLastPosition().getRegionY();
		
		if((rx - entity.getPosition().getRegionX()) >= 4) {
			entity.scheduleRegionChange(true);
		} else if((rx - entity.getPosition().getRegionX()) <= -4) {
			entity.scheduleRegionChange(true);
		}
		if((ry - entity.getPosition().getRegionY()) >= 4) {
			entity.scheduleRegionChange(true);
		} else if((ry - entity.getPosition().getRegionY()) <= -4) {
			entity.scheduleRegionChange(true);
		}
		
		entity.setWalkDir(first);
		entity.setRunDir(second);
		entity.setPosition(position);
	}
	
	/**
	 * Adds the first step to the queue, attempting to connect the server and
	 * client position by looking at the previous queue.
	 * 
	 * @param clientConnectionPosition
	 *            The first step.
	 * @return {@code true} if the queues could be connected correctly,
	 *         {@code false} if not.
	 */
	public boolean addFirstStep(Position clientConnectionPosition) {
		Position serverPosition = entity.getPosition();
		int deltaX = clientConnectionPosition.getX() - serverPosition.getX();
		int deltaY = clientConnectionPosition.getY() - serverPosition.getY();
		if (Direction.isConnectable(deltaX, deltaY)) {
			points.clear();
			oldPoints.clear();

			addStep(clientConnectionPosition);
			return true;
		}
		Queue<Position> travelBackQueue = new ArrayDeque<Position>();
		Point oldPoint;
		while ((oldPoint = oldPoints.pollLast()) != null) {
			Position oldPosition = oldPoint.position;
			deltaX = oldPosition.getX() - serverPosition.getX();
			deltaY = oldPosition.getX() - serverPosition.getY();
			travelBackQueue.add(oldPosition);
			if (Direction.isConnectable(deltaX, deltaY)) {
				points.clear();
				oldPoints.clear();
				for (Position travelBackPosition : travelBackQueue) {
					addStep(travelBackPosition);
				}
				addStep(clientConnectionPosition);
				return true;
			}
		}
		oldPoints.clear();
		return false;
	}

	/**
	 * Adds a step to the queue.
	 * 
	 * @param step
	 *            The step to add.
	 */
	public void addStep(Position step) {
		Point last = getLast();
		int x = step.getX();
		int y = step.getY();
		int deltaX = x - last.position.getX();
		int deltaY = y - last.position.getY();
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}
			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}
			addStep(x - deltaX, y - deltaY);
		}
	}

	/**
	 * Adds a step.
	 * 
	 * @param x
	 *            The x coordinate of this step.
	 * @param y
	 *            The y coordinate of this step.
	 */
	private void addStep(int x, int y) {
		if (points.size() >= MAXIMUM_SIZE) {
			return;
		}
		Point last = getLast();
		int deltaX = x - last.position.getX();
		int deltaY = y - last.position.getY();
		Direction direction = Direction.fromDeltas(deltaX, deltaY);
		if (direction != Direction.NONE) {
			Point p = new Point(new Position(x, y, entity.getPosition().getHeight()), direction);
			points.add(p);
			oldPoints.add(p);
		}
	}

	/**
	 * Gets the last point.
	 * 
	 * @return The last point.
	 */
	private Point getLast() {
		Point last = points.peekLast();
		if (last == null) {
			return new Point(entity.getPosition(), Direction.NONE);
		}
		return last;
	}

	/**
	 * Gets the last position.
	 * @return the last position.
	 */
	public Position getLastPosition() {
		Point last = getLast();
		if (last != null)
			return last.position;
		return null;
	}
	
	/**
	 * Sets the running queue flag.
	 * 
	 * @param running
	 *            The running queue flag.
	 */
	public void setRunningQueue(boolean running) {
		this.runningQueue = running;
	}

	/**
	 * Gets the running queue flag.
	 * 
	 * @return True if the player is running, false if not.
	 */
	public boolean getRunningQueue() {
		return runningQueue;
	}

	/**
	 * Resets the movemeny cycle.
	 */
	public void reset() {
		points.clear();
		oldPoints.clear();
		entity.setWalkDir(Direction.NONE);
		entity.setRunDir(Direction.NONE);
	}
}