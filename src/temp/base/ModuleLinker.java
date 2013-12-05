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
import temp.base.config.InvalidConfigurationException;
import temp.base.config.ModuleConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.*;
import org.slf4j.LoggerFactory;

/**
 * Links the modules and controls pushes and pulls between them.
 */
public final class ModuleLinker {
	private static ExecutorService executorService;

	private static Map<String, ModuleConnector> modules;
	private static Map<String, List<ModuleConnector>> pushMap;

	private static Thread shutdownThread;


	/**
	 * An empty Object-Array.
	 *
	 * It is recommended for pushes without arguments to use this to not have to create a new Array.
	 */
	public static final Object[] emptyArgs = new Object[0];

	static {
		executorService = Executors.newCachedThreadPool();
		modules = new HashMap<>();
		pushMap = new ConcurrentHashMap<>();

		shutdownThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ModuleLinker.onStop();
			}
		}, "shutdown");
	}

	/**
	 * Initializes ModuleLinker with the given BaseConfiguration. Also every ModuleConnector with load=true will be loaded and
	 * initialized
	 * @param baseConfiguration The parsed Configuration.
	 */
	static void init(final BaseConfiguration baseConfiguration) {
		if (baseConfiguration.moduleConfigs == null || baseConfiguration.moduleConfigs.size() == 0) {
			throw new InvalidConfigurationException("no Modules given");
		}
		for (ModuleConfig moduleConfig : baseConfiguration.moduleConfigs) {
			if (moduleConfig.load) {
				if (modules.containsKey(moduleConfig.name)) {
					throw new InvalidConfigurationException(" is defined twice");
				}
				ModuleConnector module = moduleConfig.createModule();
				if (module != null) {
					modules.put(moduleConfig.name, module);
				} else {
					//TODO: log - (Warning) moduleConfig.name returned null as new ModuleConnector (probably an error)
				}
			}
		}
		if (modules.size() == 0) {
			throw new InvalidConfigurationException("no modules configured to be loaded");
		}

		//ShutdownHook
		Runtime.getRuntime().addShutdownHook(shutdownThread);

		for (ModuleConnector module : modules.values()) {
			module.init();
		}
		for (ModuleConnector module : modules.values()) {
			module.onInitEnd();
		}
	}

	/**
	 * Stops the Modules and shuts down the executor Service
	 */
	static void onStop() {
		for (ModuleConnector module : modules.values()) {
			module.onStop();
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		if (!executorService.isTerminated()) {
			executorService.shutdownNow();
		}
	}

	/**
	 * Interrupts the mainThread to start the Stop-Progress
	 */
	static void stop() {
		System.exit(0);
	}

	/**
	 * Routes an push-event
	 * @param type Push-Type
	 * @param args Arguments
	 */
	static void onPush(String type, Object[] args) {
		List<ModuleConnector> moduleConnectors = pushMap.get(type);
		if (moduleConnectors != null) {
			for (ModuleConnector module : moduleConnectors) {
				module.onPush(type, args);
			}
		}
		moduleConnectors = pushMap.get("ALL");
		if (moduleConnectors != null) {
			for (ModuleConnector module : moduleConnectors) {
				module.onPush(type, args);
			}
		}
	}

	/**
	 * Queues a Push-Event.
	 * @param type Push-Type
	 * @param args Arguments
	 */
	public static void push(String type, Object[] args) {
		if (!executorService.isShutdown()) {
			executorService.execute(new PushRunnable(type, args));
		}
	}

	/**
	 * Queues a Push-Event without arguments.
	 * @param type Push-Type
	 */
	public static void push(String type) {
		push(type, emptyArgs);
	}

	/**
	 * Registers a ModuleConnector so it receives the specified type of pushes.
	 * @param module
	 * @param type
	 */
	public static synchronized void registerPush(ModuleConnector module, String type) {
		List<ModuleConnector> list = pushMap.get(type);
		if (list == null) {
			list = new ArrayList<>();
			pushMap.put(type, list);
		}
		list.add(module);
	}

	/**
	 * Unregisters a ModuleConnector from receiving the specified type of pushes.
	 * @param module
	 * @param type
	 * @return Returns false if the module wasn't registered for the push,
	 * else true
	 */
	public static synchronized boolean unregisterPush(ModuleConnector module, String type) {
		List<ModuleConnector> list = pushMap.get(type);
		if (list != null) {
			return list.remove(list);
		}
		return false;
	}

	/**
	 * Prints the Message to stderr
	 * @param message Message to be displayed in stderr
	 */
	public static void reportError(String message) {
		reportError(message, false);
	}

	/**
	 * Prints the Message to stderr and stops the program if fatal is true.
	 * @param message Message to be displayed in stderr
	 * @param fatal Stops the program if true
	 */
	public static void reportError(String message, boolean fatal) {
		System.err.println(message);
		if (fatal) {
			stop();
		}
	}
}
