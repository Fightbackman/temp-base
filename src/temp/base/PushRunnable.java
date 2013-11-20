/*
 * TEMP
 * Copyright (C) 2012-2013 Simon Roosen, Lukas Glitt, Olaf Matticzk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package temp.base;

/**
 * Implements Runnable for the Push-Event-System.
 *
 * PushRunnable represents a Push-Event with a set of arguments.
 * Therefor PushRunnable is immutable and should be released after usage.
 */
public class PushRunnable implements Runnable {
	private String type;
	private Object[] args;

	/**
	 * New PushRunnable with the specified type and arguments
	 * @param type Specifies the Push-Type
	 * @param args The arguments to be passed through to the receivers
	 */
	public PushRunnable(String type, Object[] args) {
		this.type = type;
		this.args = args;
	}

	/**
	 * Overrides the run method from Runnable to call the onPush Method
	 */
	@Override
	public void run() {
		ModuleLinker.onPush(type, args);
	}
}