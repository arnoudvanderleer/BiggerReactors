package com.tempestasludi.p41_reactors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.tempestasludi.p41_reactors.data.Score;

public class Algorithm {
    private static final int startSeed = 1; // The random seed to start with. Control over the random seed is important for reproducibility. After every run (after every #generations generations), the seed is incremented by one
    private static final int generations = 500; // The number of generations to run the algorithm for, before starting over with the next seed
    private static final int poolSize = 500; // The number of specimens in the pool
    private static final float mutationRate = 2.0f; // The expected number of mutations per specimen
    private static final float preservationFraction = 1f / 10; // The fraction of "best specimens" that are always preserved
    private static final float crossoverRate = .2f; // The probability that reproduction of a specimen also uses crossover with another specimen

    // The dimensions of the reactors (their inside dimensions) in the east-west, up-down, north-south directions
    public static final int dx = 11;
    public static final int dy = 7;
    public static final int dz = 11;

    // Whether symmetry of the reactors is enforced along the x, z, and diagonal (x+z) axis
    // Note that enabling xz-symmetry gives errors if dx != dz
    public static final boolean xSymmetry = true;
    public static final boolean zSymmetry = true;
    public static final boolean xzSymmetry = true;

    // Compares the scores of two specimens. In this case, we are looking for the specimens with the highest fuel efficiency
    public static int compare(Score r1, Score r2) { 
        return Double.compare(r1.efficiency(), r2.efficiency());
    }

    public static void main(String[] args) {
        Specimen best = null;
        for (int seed = startSeed; true; seed++) {
            Random random = new Random(seed);

            List<Specimen> pool = new ArrayList<>(poolSize);

            Specimen localBest = null;

            // Initialize the pool
            for (int i = 0; i < poolSize; i++) {
                pool.add(new Specimen(random));
            }

            for (int generation = 0; generation < generations; generation++) {
                pool.sort(Comparator.naturalOrder()); // Evaluate the specimens and sort the pool from best to worst

                // If there is a new best specimen, print it
                if (best == null || best.compareTo(pool.get(0)) > 0) {
                    localBest = best = pool.get(0);

                    System.out.println();
                    System.out.print("Best: " + best);
                } else if (localBest == null || localBest.compareTo(pool.get(0)) > 0) {
                    localBest = pool.get(0);

                    System.out.println();
                    System.out.print("Local best: " + localBest);
                    System.out.print("Best: " + best);
                }

                System.out.println("Seed: " + seed + ", Generation: " + generation);
                System.out.print("\u001b[1A");

                List<Specimen> newPool = new ArrayList<>(poolSize);
                for (int i = 0; i < poolSize; i++) {
                    // At least preserve the best couple of specimens, to prevent regression
                    if (i < poolSize * preservationFraction) {
                        newPool.add(pool.get(i).clone());
                        continue;
                    }

                    // For the rest of the new pool, pick (with replacement) from the old pool, weighted towards the best specimens
                    int index = (int)Math.floor(Math.pow(random.nextFloat(), 2) * poolSize);
                    Specimen candidate = pool.get(index).clone();

                    // In some cases, also pick a second candidate, to perform crossover
                    if (random.nextDouble() < crossoverRate) {
                        int index2 = (int)Math.floor(Math.pow(random.nextFloat(), 2) * poolSize);
                        candidate = candidate.crossover(pool.get(index2), random);
                    }

                    // Mutate every candidate
                    candidate.mutate(mutationRate, random);

                    newPool.add(candidate);
                }

                pool = newPool;
            }
        }
    }
}
