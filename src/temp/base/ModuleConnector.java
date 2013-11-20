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
 * Abstract Base for ModuleConnectors
 */
public abstract class ModuleConnector {

	/**
	 * Initializes the Module
	 */
	public abstract void init();

	/**
	 * Called after all Modules are initialized
	 */
	protected abstract void onInitEnd();

	/**
	 * Is called when the program gets stopped
	 */
	protected abstract void onStop();

	/**
	 * Is called when the Module receives a Push-Event
	 * @param type
	 * @param args
	 */
	protected abstract void onPush(String type, Object[] args);

	/**
	 * Is called when a module wants to pull from the current module
	 * @param key
	 * @return Result of the Pull
	 */
	protected abstract Object onPull(String key);

}
