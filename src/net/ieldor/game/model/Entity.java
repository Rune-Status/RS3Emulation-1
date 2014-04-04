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

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

import net.ieldor.game.model.masks.UpdateMask;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.model.region.Region;
import net.ieldor.game.model.update.UpdateFlags;

/**
 * Represents either an Mob (eg: Skeleton, Spider, Dwarf) or a Player
 * (controlled by a user input).
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class Entity {

	/**
	 * The index of the entity.
	 */
	private int index;
	
	/**
	 * The position of the entity.
	 */
	private Position position, lastPosition;
	
	/**
	 * The region of the entity.
	 */
	private Region currentRegion;
	
	/**
	 * The directions of walking and running.
	 */
	private Direction walkDir, runDir;
	
	private UpdateFlags updateFlags;
	
	private boolean teleported;
	
	/**
	 * The queue of update masks.
	 */
	private PriorityQueue<UpdateMask> updateMasks = new PriorityQueue<UpdateMask>(10, new Comparator<UpdateMask>() {

		@Override
		public int compare(UpdateMask o1, UpdateMask o2) {
			return o1.getPriority() - o2.getPriority();
		}
	});
	
	/**
	 * The local players in the entities viewport.
	 */
	private List<Player> localPlayers = new LinkedList<Player>();
	
	/**
	 * The movement pulse.
	 */
	private MovementPulse movementPulse = new MovementPulse(this);
	
	/**
	 * Schedules a region change flag.
	 */
	private boolean regionChange = false;
	
	/**
	 * The is running flag.
	 */
    public boolean isRunning;
    
    /**
     * The run energy amount.
     */
    public int runEnergy = 100;

	/**
	 * Constructs a new {@code Entity} instance.
	 * @param position The position.
	 */
	public Entity(Position position) {
		this.position = position;
		updateFlags = new UpdateFlags();
	}
	
	/**
	 * Gets the walk direction.
	 * @return the walkDir
	 */
	public Direction getWalkDir() {
		return walkDir;
	}
	
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	/**
	 * Sets the walk direction.
	 * @param walkDir the walkDir to set
	 */
	public void setWalkDir(Direction walkDir) {
		this.walkDir = walkDir;
	}

	/**
	 * Gets the run direction.
	 * @return the runDir
	 */
	public Direction getRunDir() {
		return runDir;
	}

	/**
	 * Sets the run direction.
	 * @param runDir the runDir to set
	 */
	public void setRunDir(Direction runDir) {
		this.runDir = runDir;
	}
	
	/**
	 * Gets the last position.
	 * @return the lastPosition
	 */
	public Position getLastPosition() {
		return lastPosition;
	}

	/**
	 * Sets the last position.
	 * @param lastPosition the lastPosition to set
	 */
	public void setLastPosition(Position lastPosition) {
		this.lastPosition = lastPosition;
	}
	
	public void addUpdateMask(UpdateMask mask) {
		for (UpdateMask m : updateMasks)
			if (m.getMaskData() == mask.getMaskData())
				return;
		
		updateMasks.add(mask);
	}

	public boolean isTeleported() {
		return teleported;
	}

	public void setTeleported(boolean teleported) {
		this.teleported = teleported;
	}
	
	/**
	 * The queue of update masks.
	 * @return The masks.
	 */
	public PriorityQueue<UpdateMask> getUpdateMasks() {
		return updateMasks;
	}

	/**
	 * Gets the current region
	 * @return the region
	 */
	public Region getCurrentRegion() {
		return currentRegion;
	}

	/**
	 * Sets the current region
	 * @param currentRegion the region to set to
	 */
	public void setCurrentRegion(Region currentRegion) {
		this.currentRegion = currentRegion;
	}

	/**
	 * Gets the position.
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the position.
	 * @param position the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Gets the index.
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Checks if the entity is active
	 * @return true if active, false otherwise
	 */
	public boolean isActive() {
		return index != -1;
	}
	
	/**
	 * Gets the list of local players.
	 * @return the localPlayers
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * Gets the movement pulse
	 * @return the movementPulse
	 */
	public MovementPulse getMovementPulse() {
		return movementPulse;
	}
	
	/**
	 * Schedules a region change process.
	 * @param regionChange The region change to set.
	 */
	public void scheduleRegionChange(boolean regionChange) {
		this.regionChange = regionChange;
	}
	
	/**
	 * Checks if a region change must occur.
	 * @return The region change flag.
	 */
	public boolean hasScheduledRegionChange() {
		return regionChange;
	}
    
	/**
	 * Checks if is running.
	 * @return The is running.
	 */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Sets the player is running.
     * @param running The running.
     */
    public void setRunning(boolean running) {
        this.isRunning = running;
    }
            
    /**
     * Gets the run energy.
     * @return The run energy.
     */
    public int getRunEnergy() {
        return runEnergy;
    }

    /**
     * Sets the run energy.
     * @param runEnergy The run energy.
     */
    public void setRunEnergy(int runEnergy) {
        this.runEnergy = runEnergy;
    }
}
