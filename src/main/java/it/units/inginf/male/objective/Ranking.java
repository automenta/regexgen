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
package it.units.inginf.male.objective;

import it.units.inginf.male.tree.IDFactory;
import it.units.inginf.male.tree.Node;

import java.util.Arrays;

/**
 *
 * @author MaleLabTs
 */
final public class Ranking {

    private final Node tree;
    private final double[] fitness;
    private final int id;

    public Ranking(Node tree, double[] fitness) {
        this.tree = tree;
        this.fitness = fitness;
        this.id = (int)IDFactory.nextID();
    }

    @Override
    public String toString() {
        return "{" +
                tree.getDescription() +
                "=" + Arrays.toString(fitness) +
                '}';
    }


    public double[] getFitness() {
        return fitness;
    }


    public Node getNode() {
        return tree;
    }    
    
    public String getDescription(){
        StringBuilder sb = new StringBuilder();
        this.tree.describe(sb);
        return sb.toString();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;

        //return tree.equals( ((Ranking)obj ).tree );
//
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Ranking other = (Ranking) obj;
//        Node t = this.tree;
//        Node ot = other.tree;
//        return Objects.equals(t, ot);
////        if (t != ot && (t == null || !t.equals(ot))) {
////            return false;
////        }
////        return true;
    }

    @Override
    public final int hashCode() {
        return id;
        //return tree.hashCode(); //tree.hashCode();
//        int hash = 5;
//        hash = 97 * hash + (this.tree != null ? this.tree.hashCode() : 0);
//        return hash;
    }       
    
}
