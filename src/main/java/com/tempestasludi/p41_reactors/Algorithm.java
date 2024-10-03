package com.tempestasludi.p41_reactors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.tempestasludi.p41_reactors.data.Score;

public class Algorithm {
    private static final int seed = 1; // The random seed, for reproducibility
    private static final int poolSize = 500; // The number of specimens in the pool
    private static final float mutationRate = 5.0f; // The expected number of mutations per specimen
    private static final float preservationFraction = 1f / 10; // The fraction of "best specimens" that are always preserved
    private static final float crossoverRate = .3f; // The probability that reproduction of a specimen also uses crossover with another specimen

    // The dimensions of the reactors (their inside dimensions) in the east-west, up-down, north-south directions
    public static final int dx = 11;
    public static final int dy = 7;
    public static final int dz = 11;

    // Whether symmetry of the reactors is enforced along the x, z, and diagonal (x+z) axis
    public static final boolean xSymmetry = true;
    public static final boolean zSymmetry = true;
    public static final boolean xzSymmetry = true;

    // Compares the scores of two specimens. In this case, we are looking for the specimens with the highest fuel efficiency
    public static int compare(Score r1, Score r2) { 
        return Double.compare(r1.efficiency(), r2.efficiency());
    }

    public static void main(String[] args) {
        Random random = new Random(seed);

        List<Specimen> pool = new ArrayList<>(poolSize);

        // Initialize the pool
        for (int i = 0; i < poolSize; i++) {
            pool.add(new Specimen(random));
        }

        Specimen best = null;

        for (int iteration = 0; true; iteration++) {
            pool.sort(Comparator.naturalOrder()); // Evaluate the specimens and sort the pool from best to worst

            // If there is a new best specimen, print it
            if (best == null || best.compareTo(pool.get(0)) > 0) {
                // An attempt to overwrite the old print.
                // Does not work very well for large reactors, unfortunately, because the terminal cannot scroll that far back
                // if (best != null) {
                //     System.out.println("\u001b[" + ((dz + 1) * dy + 4) + "A");
                // }
                best = pool.get(0);

                System.out.println("Generation: " + iteration);
                System.out.print("Score: " + best);
            }

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
