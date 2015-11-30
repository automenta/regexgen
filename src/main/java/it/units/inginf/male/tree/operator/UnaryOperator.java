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
public abstract class UnaryOperator extends ParentNode {

    private Node parent;

    public UnaryOperator() {
        super(new ArrayList(1));
    }

    public UnaryOperator(Node child) {
        super(Lists.mutable.of(child));
    }

    @Override
    public final int getMinChildrenCount() {
        return 1;
    }

    @Override
    public final int getMaxChildrenCount() {
        return 1;
    }       

//    @Override
//    public Node cloneTree() {
//        UnaryOperator clone = buildCopy();
//        List<Node> ch = children();
//        if (!ch.isEmpty()) {
//            Node child = ch.get(0).cloneTree();
//            child.setParent(clone);
//            clone.children().add(child);
//        }
//        return clone;
//    }
    @Override
    public Node cloneTree() {
        UnaryOperator bop = buildCopy();

        List<Node> ch = this.children();
        if (!ch.isEmpty()) {
            List<Node> bopChilds = bop.children();
            cloneChild(ch.get(0), bop);
        }
        bop.unhash();
        return bop;
    }


   
    protected abstract UnaryOperator buildCopy();  

}
