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
package it.units.inginf.male.strategy.impl;

import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import it.units.inginf.male.configuration.Configuration;
import it.units.inginf.male.configuration.EvolutionParameters;
import it.units.inginf.male.evaluators.TreeEvaluationException;
import it.units.inginf.male.generations.Generation;
import it.units.inginf.male.generations.InitialPopulationBuilder;
import it.units.inginf.male.generations.Ramped;
import it.units.inginf.male.inputs.Context;
import it.units.inginf.male.objective.Objective;
import it.units.inginf.male.objective.Ranking;
import it.units.inginf.male.selections.Selection;
import it.units.inginf.male.selections.Tournament;
import it.units.inginf.male.strategy.ExecutionListener;
import it.units.inginf.male.strategy.RunStrategy;
import it.units.inginf.male.tree.Node;
import it.units.inginf.male.variations.Variation;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Implements the default evolution strategy, termination criteria can be enabled thru parameters.
 * Optional accepted parameters:
 * "terminationCriteria", Boolean, then True the termination criteria is enabled when false is disabled, Default value: false
 * "terminationCriteriaGenerations", Integer, number of generations for the termination criteria.Default value: 200
 *
 * @author MaleLabTs
 */
public class DefaultStrategy implements RunStrategy {

    protected Context context;
    protected int maxDepth;
    protected final FastList<Node> population = new FastList();
    protected final TreeSet<Ranking> rankings = new TreeSet(RankingComparator);
    protected Selection selection;
    protected Objective objective;
    protected Variation variation;
    protected EvolutionParameters param;
    protected ExecutionListener listener;
    protected boolean terminationCriteria = false; //Termination criteria enables/disables the premature termination of thread when best regex/individual doens't change for
    //a speciefied amount of generations (terminationCriteriaGenerations)

    @Deprecated
    protected int terminationCriteriaGenerations = 200;
    @Deprecated
    protected int maxCrossoverTries = 20;


    @Override
    public void setup(Configuration configuration, ExecutionListener listener) {
        this.param = configuration.getEvolutionParameters();

        this.readParameters(configuration);

        this.context = new Context(Context.EvaluationPhases.TRAINING, configuration);
        this.maxDepth = param.getCreationMaxDepth();
        //cloning the objective 
        this.objective = configuration.getObjective();
        this.selection = new Tournament(this.context);
        this.variation = new Variation(this.context);
        this.listener = listener;

        this.objective.setup(context);
    }

    protected void readParameters(Configuration configuration) {
        Map<String, String> parameters = configuration.getStrategyParameters();
        if (parameters != null) {
            //add parameters if needed
            if (parameters.containsKey("terminationCriteriaGenerations")) {
                terminationCriteriaGenerations = Integer.valueOf(parameters.get("terminationCriteriaGenerations"));
            }
            if (parameters.containsKey("terminationCriteria")) {
                terminationCriteria = Boolean.valueOf(parameters.get("terminationCriteria"));
            }
        }
    }

    @Override
    public Void call() throws TreeEvaluationException {
        try {
            listener.evolutionStarted(this);

            Context ctx = this.context;

            InitialPopulationBuilder populationBuilder = ctx.getConfiguration().getPopulationBuilder();

            populationBuilder.init(population);
            Generation ramped = new Ramped(this.maxDepth, this.context);
            int popSize = param.getPopulationSize();
            population.addAll(ramped.generate(popSize - this.population.size()));

            //IntObjectHashMap<Ranking> remaining = new IntObjectHashMap(newPopulation.size());
            //Set<Ranking> remaining = new UnifiedSet(population.size());
            //this.rankings = new TreeSet(RankingComparator);
            eachRankings(population, objective, (n, f) -> {
                rankings.add(new Ranking(n, f));
                //population.add(n);
            });

//        rankings.clear();
//
//        final int popSize = population.size();
//        while (!remaining.isEmpty() /*|| rankings.size() < popSize*/) {
//            Utils.getFirstParetoFront(remaining);
//            sortByFirst(rankings);
//        }
//
//
//        population.clear();
//
//        Obtain an ordinated (as Rankings are) population
//        rankings.forEach(rr -> population.add(rr.getTree()));

            //Variables for termination criteria
            String oldGenerationBestValue = null;
            int terminationCriteriaGenerationsCounter = 0;
            int doneGenerations = 0;

            for (int generation = 0; generation < param.getGenerations(); generation++) {
                ctx.setStripedPhase(ctx.getDataSetContainer().isDataSetStriped() && ((generation % ctx.getDataSetContainer().getProposedNormalDatasetInterval()) != 0));

                evolve();

                Ranking best = rankings.first();
                doneGenerations = generation + 1;
                if (listener != null) {
                    listener.logGeneration(this, doneGenerations, best.getNode(), best.getFitness(), rankings);
                }
                boolean allPerfect = true;
                for (double fitness : best.getFitness()) {
                    if (Math.round(fitness * 10000) != 0) {
                        allPerfect = false;
                        break;
                    }
                }
                if (allPerfect) {
                    break;
                }

                if (terminationCriteria) {
                    String newBestValue = best.getDescription();
                    if (newBestValue.equals(oldGenerationBestValue)) {
                        terminationCriteriaGenerationsCounter++;
                    } else {
                        terminationCriteriaGenerationsCounter = 0;
                    }
                    if (terminationCriteriaGenerationsCounter >= this.terminationCriteriaGenerations) {
                        break;
                    }
                    oldGenerationBestValue = newBestValue;
                }

                if (Thread.interrupted()) {
                    break;
                }

            }

            //                //We have to evaluate the new solutions on the testing dataset
            TreeSet<Ranking> tmp = new TreeSet(RankingComparator);
            sortRankings(population, objective, tmp);


            listener.evolutionComplete(this, doneGenerations - 1, tmp);


//            //now generation value is already last generation + 1, no reason to add +1
//            if (listener != null) {
//                listener.evolutionComplete(this, doneGenerations, rankings);
//            }
            return null;
        } catch (Throwable x) {
            throw new TreeEvaluationException("Error during evaluation of a tree", x, this);
        }
    }

    protected void evolve() {
        throw new RuntimeException("share the impl from DiversityElitismStrategy");

//        List<Node> newPopulation = new ArrayList<>(population.size());
//        int popSize = population.size();
//        int oldPopSize = (int) (popSize * 0.9);
//
//        boolean allPerfect = true;
//        for (double fitness : rankings.get(0).getFitness()) {
//            if (Math.round(fitness * 10000) != 0) {
//                allPerfect = false;
//                break;
//            }
//        }
//        if (allPerfect) {
//            return;
//        }
//
//        for (int index = 0; index < param.getElitarism(); index++) {
//            Node elite = rankings.remove(0).getTree();
//            newPopulation.add(elite);
//        }
//
//        while (newPopulation.size() < oldPopSize) {
//
//            double random = context.getRandom().nextDouble();
//
//            if (random <= param.getCrossoverProbability() && oldPopSize - newPopulation.size() >= 2) {
//                Node selectedA = selection.select(rankings);
//                Node selectedB = selection.select(rankings);
//
//                Twin<Node> newIndividuals = variation.crossover(selectedA, selectedB, maxCrossoverTries);
//                if (newIndividuals != null) {
//                    newPopulation.add(newIndividuals.getOne());
//                    newPopulation.add(newIndividuals.getTwo());
//                }
//            } else if (random <= param.getCrossoverProbability() + param.getMutationPobability()) {
//                Node mutant = selection.select(this.rankings);
//                mutant = variation.mutate(mutant);
//                newPopulation.add(mutant);
//
//            } else {
//                Node duplicated = selection.select(rankings);
//                newPopulation.add(duplicated);
//            }
//        }
//
//        Generation ramped = new Ramped(maxDepth, context);
//        List<Node> generated = ramped.generate(popSize - oldPopSize);
//        newPopulation.addAll(generated);
//
//        population = newPopulation;
//        List<Ranking> tmp = buildRankings(population, objective);
//        rankings.clear();
//        while (tmp.size() > 0) {
//            List<Ranking> t = Utils.getFirstParetoFront(tmp);
//            tmp.removeAll(t);
//            sortByFirst(t);
//            rankings.addAll(t);
//        }
    }

    protected static MutableMap<Node, double[]> buildRankings(List<Node> population, Objective objective) {
        MutableMap<Node, double[]> each = new UnifiedMap();
        eachRankings(population, objective, each);
        return each;
    }

    protected static void eachRankings(List<Node> population, Objective objective, BiConsumer<Node, double[]> each) {
        for (Node tree : population) {
            each.accept(tree, objective.fitness(tree));
        }
    }

    protected static void eachRankings(List<Node> population, Objective objective, Map<Node, double[]> each) {
        eachRankings(population, objective, (n, f) -> each.put(n, f));
    }
    protected static void sortRankings(List<Node> population, Objective objective, TreeSet<Ranking> each) {
        eachRankings(population, objective, (n, f) -> each.add(new Ranking(n, f)));
    }

    @Override
    public Configuration getConfiguration() {
        return context.getConfiguration();
    }

    @Override
    public ExecutionListener getExecutionListener() {
        return listener;
    }

    protected void sortByFirst(List<Ranking> front) {
        Collections.sort(front, RankingComparator);
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    final static Comparator<Ranking> RankingComparator = new Comparator<Ranking>() {

        @Override
        public int compare(Ranking o1, Ranking o2) {
            if (o1 == o2) return 0;

            double[] f1 = o1.getFitness();
            double[] f2 = o2.getFitness();
            final int n = f1.length;

            double balance = 0;

            for (int i = 0; i < n; i++) {
                double v1 = f1[i];
                double v2 = f2[i];
                if (v1==v2) continue;

                //int result = Double.compare(v1, v2);
                balance += (v1/(v1+v2) - 0.5);
            }

            //TODO weight of fitness objectives

            if (balance > 0) return 1;
            if (balance < 0) return -1;

//            if (result == 0) {
//                return o1.getDescription().
//                        compareTo(o2.getDescription());
//            }

            //??
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    };
}
