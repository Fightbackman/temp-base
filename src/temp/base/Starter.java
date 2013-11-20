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

import temp.base.config.BaseConfiguration;

import java.io.IOException;

/**
 * Provides the main-method to load the basic configuration and initialize the ModuleLinker
 */
public final class Starter {

	public static void main(String[] args) {
		//Set Thread-Information in ModuleLinker for Interrupt
		//ModuleLinker.setMainThread(Thread.currentThread());

		//Load Core-Config
		BaseConfiguration baseConfiguration = null;
		try {
			baseConfiguration = ConfigurationLoader.parseConfig(BaseConfiguration.class, "config/config.yaml");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		//Initialize Modules
		ModuleLinker.init(baseConfiguration);

		//Wait for Interrupt
		/*try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
		}

		//Start Stop-Routine
		ModuleLinker.onStop();*/
	}
}

