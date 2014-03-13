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
package net.ieldor.game.model.masks;

import net.ieldor.game.model.Entity;
import net.ieldor.io.PacketBuf;

/**
 * An update mask is an super class that is used to represent an single mask
 * that is updated in the update procedure of an {@link Entity}.
 * <p>
 * A mask consists of one main thing, and that is the <code>maskData</code> or
 * the <code>maskFlag</code>, these are writen as hexadecimal and these flags
 * are writen to the client. And of course, than we write the data contained
 * inside the actual mask, such as a <code>short, byte, integer</code>.
 * </p>
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public abstract class UpdateMask {

	/**
	 * The entity for the mask.
	 */
	private Entity entity;
	
	/**
	 * The data or flag for the mask.
	 */
	private int maskData;

	/**
	 * The priority of the mask.
	 */
	private int priority;

	/**
	 * Constructs a new UpdateMask instance.
	 * @param entity The entity for this mask.
	 * @param maskData The data written for the mask.
	 * @param priority The priority the mask must be updated.
	 */
	public UpdateMask(Entity entity, int maskData, int priority) {
		this.entity = entity;
		this.maskData = maskData;
		this.priority = priority;
	}
	
	/**
	 * Appends the data that must be written for the mask.
	 * @param builder The builder for writting data.
	 */
	public abstract void appendMask(PacketBuf buf);

	/**
	 * Gets the mask's entity.
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Gets the mask data.
	 * @return the maskData
	 */
	public int getMaskData() {
		return maskData;
	}

	/**
	 * Gets the priority.
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
}

