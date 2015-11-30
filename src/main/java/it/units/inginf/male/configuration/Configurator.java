/*
 * Copyright (C) 2015 Machine Learning Lab - University of Trieste, 
 * Italy (http://machinelearning.inginf.units.it/)  
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.units.inginf.male.configuration;

import com.google.gson.Gson;

import java.io.*;

/**
 * @author MaleLabTs
 */
public class Configurator {


    public static Configuration configure(String json) throws IOException {
        return configure(new BufferedReader( new StringReader(json)) );
    }

    public static Configuration configureFile(String filename) throws IOException {
        return configure(new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(new File(filename)))));
    }

    public static Configuration configure(BufferedReader r) throws IOException {

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }

        String json = sb.toString();
        return configureFromJson(json);
    }

    public static Configuration configureFromJson(String jsonConfiguration) {
        Gson gson = new Gson();
        Configuration configuration = gson.fromJson(jsonConfiguration, Configuration.class);
        configuration.setup();
        return configuration;
    }
}
