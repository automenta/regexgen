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
package it.units.inginf.male.generations;

import it.units.inginf.male.inputs.Context;
import it.units.inginf.male.tree.Leaf;
import it.units.inginf.male.tree.Node;
import it.units.inginf.male.tree.ParentNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author MaleLabTs
 */
public class Growth implements Generation {

    final int maxDepth;
    final Context context;
    final Random rng;

    public Growth(int maxDepth, Context context) {
        this.maxDepth = maxDepth;
        this.context = context;
        this.rng = context.getRandom();
    }

     /**
     * This method return a new population of the desired size. The population
     * is generated by Growth algorithm which creates individual with a depth
     * from 1 to a specified max depth,
     * @param popSize the desired population size
     * @return a List of Node of size popSize
     */
    @Override
    public List<Node> generate(int popSize) {
        List<Node> population = new ArrayList<>(popSize);

        for (int i = 0; i < popSize;) {
            Node candidate = grow(1);
            if (candidate.isValid()) {
                population.add(candidate);
                i++;
            }
        }

        return population;
    }

    private Node grow(int depth) {
        Node _tree = randomFunction();
        if (_tree.getMaxChildrenCount() > 0) {
            ParentNode tree = (ParentNode) _tree;

            if (depth >= this.maxDepth - 1) {

                for (int i = tree.getMaxChildrenCount() - tree.getMinChildrenCount(); i < tree.getMaxChildrenCount(); i++) {
                    Leaf leaf = randomLeaf();
                    add(tree, leaf);
                }

            } else {

                for (int i = tree.getMaxChildrenCount() - tree.getMinChildrenCount(); i < tree.getMaxChildrenCount(); i++) {

                    if (rng.nextBoolean()) {
                        Node node = grow(depth + 1);
                        add(tree, node);
                    } else {
                        Leaf leaf = randomLeaf();
                        add(tree, leaf);
                    }
                }
            }
        }
        return _tree;
    }

    static void add(ParentNode tree, Node n) {
        n.setParent(tree);
        tree.add(n);
    }

    private Node randomFunction() {

        List<Node> functionSet = context.getConfiguration().getNodeFactory().getFunctionSet();
        return functionSet.get(rng.nextInt(functionSet.size())).cloneTree();
    }

    private Leaf randomLeaf() {
        List<Leaf> terminalSet = context.getConfiguration().getNodeFactory().getTerminalSet();
        return terminalSet.get(rng.nextInt(terminalSet.size())).cloneTree();
    }
}
