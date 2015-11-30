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
package it.units.inginf.male.tree.operator;

import it.units.inginf.male.tree.DescriptionContext;
import it.units.inginf.male.tree.Leaf;

/**
 * https://docs.oracle.com/javase/tutorial/essential/regex/groups.html#backref
 * @author MaleLabTs
 */
public class Backreference extends Leaf<Integer> {

    public Backreference(){
        super(1 /* TODO random? */);
    }

    public Backreference(int value){
        super(value);
    }

    public Backreference(Backreference value){
        super(value);
    }

    @Override
    public void describe(StringBuilder builder, DescriptionContext context, RegexFlavour flavour) {
        builder.append('\\');
        int v = value.intValue();
        switch (flavour) {
            case JAVA:
                builder.append(v);
                break;
            default:
                builder.append(v + context.getExpansionGroups());
        }
    }

    @Override
    public Leaf cloneTree() {
        return new Backreference(this);
    }



    
}
