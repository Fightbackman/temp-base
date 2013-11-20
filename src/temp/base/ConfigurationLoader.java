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

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Static class to parse YAML-Configuration
 */
public class ConfigurationLoader {
	private ConfigurationLoader() {
	}

	public static <T> T parseConfig(Class<T> rootClass, Reader reader) throws IOException {
		YamlReader yamlReader = new YamlReader(reader);
		T result = yamlReader.read(rootClass);
		yamlReader.close();
		return result;
	}

	public static <T> T parseConfig(Class<T> rootClass, String file) throws IOException {
		T result;
		try (Reader reader = new FileReader(new File(file))) {
			result = parseConfig(rootClass, reader);
		}
		return result;
	}
}
