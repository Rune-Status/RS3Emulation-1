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
package net.ieldor.network.packet.context;

import net.ieldor.network.packet.PacketContext;

/**
 * An {@link PacketContext} that handles stores the command of an command decoder.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class CommandContext implements PacketContext {

	/**
	 * The command.
	 */
	private String command;
	
	/**
	 * The arguments of the command.
	 */
	private String[] arguments;

	/**
	 * Constructs a new {@code CommandContext} instance.
	 * @param command The command.
	 * @param arguments The command arguments.
	 */
	public CommandContext(String command, String... arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	/**
	 * Gets the command.
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Gets the arguments.
	 * @return the arguments
	 */
	public String[] getArguments() {
		return arguments;
	}
	
}
