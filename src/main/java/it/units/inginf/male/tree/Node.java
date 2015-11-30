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

import it.units.inginf.male.utils.Utils;

import java.util.List;

/**
 *
 * @author MaleLabTs
 */
public interface Node {
    
    Node cloneTree();

    Node getParent();
    void setParent(Node parent);
    
    int getMinChildrenCount();
    int getMaxChildrenCount();
    List<Node> getChildrens();
    long getId();
    
    void describe(StringBuilder builder);
    void describe(StringBuilder builder, DescriptionContext context, RegexFlavour flavour);
    boolean isValid();

    default int toNonNegativeInteger(int ifMissing) {
        String s = toString();
        if (!s.isEmpty()) {

            char cc = s.charAt(0);
            if ((cc >= '0' && cc <= '9')) {
                try {
                    return Utils.i(s);
                } catch (NumberFormatException ex) {
                }
            }
        }
        return ifMissing;
    }

    public enum RegexFlavour {
        JAVA,
        CSHARP,
        JS
    }
    public boolean isCharacterClass();
    public boolean isEscaped();
}
