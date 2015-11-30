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

import it.units.inginf.male.tree.Node;
import it.units.inginf.male.tree.ParentNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MaleLabTs
 */
public abstract class TernaryOperator extends ParentNode {
    private Node parent;

    public TernaryOperator() {
        super(new ArrayList(3));
    }

//    @Override
//    public Node cloneTree() {
//        TernaryOperator top = buildCopy();
//        List<Node> topChilds = top.children();
//        for(Node child:this.children()){
//            Node newChild = child.cloneTree();
//            newChild.setParent(top);
//            topChilds.add(newChild);
//        }
//        return top;
//    }

    @Override
    public Node cloneTree() {
        TernaryOperator bop = buildCopy();
        List<Node> ch = this.children();
        if (ch.size() >= 3) {
            List<Node> bopChilds = bop.children();
            cloneChild(ch.get(0), bop);
            cloneChild(ch.get(1), bop);
            cloneChild(ch.get(2), bop);
        }
        bop.unhash();
        return bop;
    }

    @Override
    public final int getMinChildrenCount() {
        return 3;
    }

    @Override
    public final int getMaxChildrenCount() {
        return 3;
    }
    
    public final Node getFirst() {
        return children().get(0);
    }

    public final Node getSecond() {
        return children().get(1);
    } 
    
    public final Node getThird() {
        return children().get(2);
    }
    
    protected abstract  TernaryOperator buildCopy();
    
}
