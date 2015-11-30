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

import com.gs.collections.impl.factory.Lists;
import it.units.inginf.male.tree.Node;
import it.units.inginf.male.tree.ParentNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MaleLabTs
 */
public abstract class BinaryOperator extends ParentNode {



    public BinaryOperator() {
        super(new ArrayList(2));
    }

    public BinaryOperator(Node a, Node b) {
        super(Lists.mutable.of(a,b));
    }

    @Override
    public int getMinChildrenCount() {
        return 2;
    }

    @Override
    public int getMaxChildrenCount() {
        return 2;
    }

    public final Node getLeft() {
        return children().get(0);
    }

    public final Node getRight() {
        return children().get(1);
    }    

    @Override
    public Node cloneTree() {
        BinaryOperator bop = buildCopy();

        List<Node> ch = this.children();
        if (ch.size() >= 2) {
            List<Node> bopChilds = bop.children();
            cloneChild(ch.get(0), bop);
            cloneChild(ch.get(1), bop);
        }
        bop.unhash();
        return bop;
    }



    
    protected abstract  BinaryOperator buildCopy();

}