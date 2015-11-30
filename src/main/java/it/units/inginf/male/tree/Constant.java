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
package it.units.inginf.male.tree;

import java.util.*;

/**
 *
 * @author MaleLabTs
 */
 
public class Constant extends Leaf<String> {

    transient private final boolean charClass;
    transient private final boolean escaped;

    static final Set<String> allowedClasses = new HashSet<>(
            Arrays.asList("\\w", "\\d", ".", "\\b", "\\s")
    );

    public Constant(String value) {
        super(value);
        charClass = allowedClasses.contains(value);
        escaped = value.startsWith("\\");
    }



    @Override
    public void describe(StringBuilder builder, DescriptionContext context, RegexFlavour flavour) {
        builder.append(value);
    }

    @Override
    public Leaf cloneTree() {
        Constant clone = new Constant(value);
        return clone;
    }





    @Override
    public boolean isCharacterClass() {
        return charClass;
    }

    @Override
    public boolean isEscaped() {
        return escaped;
    }


}
