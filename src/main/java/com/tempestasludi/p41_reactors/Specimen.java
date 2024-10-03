package com.tempestasludi.p41_reactors;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector3i;

import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorSimulation;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.SimulationDescription;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.SimulationConfiguration;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.cpu.TimeSlicedReactorSimulation;
import net.roguelogix.biggerreactors.registries.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.Config;

import com.tempestasludi.p41_reactors.data.Result;
import com.tempestasludi.p41_reactors.data.Symmetry;

public class Specimen implements Comparable<Specimen> {

    // The map that relates positions to moderators (listed in ReactorModeratorRegistry.registry)
    // -1 counts as a fuel rod
    private Map<Vector3i, Integer> moderators;

    // The degree to which the fuel rods are inserted / extended / activated
    private int insertion;

    // The random seed that the TimeSlicedReactorSimulation uses for selecting the order of the fuel rods
    private int seed;

    // A cache for the evaluation value of this specimen
    private Result value = null;

    // Initializes the specimen, but does not fill the moderators map.
    public Specimen(int seed, int insertion) {
        moderators = new HashMap<Vector3i, Integer>();
        this.seed = seed;
        this.insertion = insertion;
    }

    // Initializes the specimen, and fills the moderators in some way. In this case, setting everything to unobtainium
    public Specimen(Random random) {
        this(random.nextInt(), random.nextInt(100));
        for (Vector3i p : positions) {
            if (p.x == Simulation.dx / 2 && p.z == Simulation.dz / 2) {
                set(p, -1);
                continue;
            }
            set(p, ReactorModeratorRegistry.registry.length - 1);
            // set(p, random.nextInt(ReactorModeratorRegistry.registry.length));
        }
    }

    // Randomly changes a couple of blocks, but never the fuel rod
    public void mutate(float rate, Random random) {
        while (random.nextFloat() < rate / (1 + rate)) {
            Vector3i p = positions[random.nextInt(positions.length)];
            if (p.x == Simulation.dx / 2 && p.z == Simulation.dz / 2) {
                continue;
            }
            this.set(p, random.nextInt(ReactorModeratorRegistry.registry.length));
        }
        if (random.nextFloat() < rate / (5 + rate)) {
            insertion = random.nextInt(100);
        }
    }

    // Performs crossover between two specimens
    public Specimen crossover(Specimen other, Random random) {
        float t = random.nextFloat();
        Specimen result = new Specimen(
            random.nextInt(), 
            (int) (t * this.insertion + (1 - t) * other.insertion)
        );
        result.moderators = new HashMap<Vector3i, Integer>();

        for (Vector3i p : positions) {
            if (random.nextFloat() < .5) {
                result.set(p, other.get(p));
            } else {
                result.set(p, this.get(p));
            }
        }

        return result;
    }

    // Runs the simulation for 30 seconds, and reports the highest output, and the fuel efficiency (output / consumption) at that moment
    Result evaluate() {
        if (this.value != null) {
            return this.value;
        }

        final int fuelStep = 1000; // The amount of insterted fuel in millibuckets. 1000 is realistic, 1 would be "optimal"

        SimulationDescription description = new SimulationDescription(new Vector3i(Simulation.dx, Simulation.dy, Simulation.dz), moderators);
        SimulationConfiguration config = new SimulationConfiguration(Config.CONFIG.Reactor, 293.15, true);
        IReactorSimulation simulation = new TimeSlicedReactorSimulation(description, config, seed);

        // If you enable this line, the system will use the insertion of the control rods, and therefore try to optimize it
        // simulation.controlRodAt(Simulation.dx / 2, Simulation.dz / 2).setInsertion(insertion);

        long gen = 0;
        double efficiency = 0;

        int tick = 0;

        while (tick <= 600) {
            simulation.fuelTank().extractWaste(Math.round(simulation.fuelTank().waste() / fuelStep) * fuelStep, false);
            simulation.fuelTank().insertFuel(((simulation.fuelTank().capacity() - simulation.fuelTank().fuel()) / fuelStep) * fuelStep, false);

            simulation.tick(true);
            tick++;

            long g = simulation.battery().generatedLastTick();
            double b = simulation.fuelTank().burnedLastTick();
            if (g > gen) {
                gen = g;
                efficiency = g / b;
            }
        }
        return this.value = new Result((long)gen, efficiency);
    }

    @Override
    public int compareTo(Specimen other) {
        return -Simulation.compare(this.evaluate(), other.evaluate());
    }

    // Sets the block at position p to a value.
    // If symmetries are enabled in the configuration of Simulation, uses those symmetries to set some other blocks to the same value
    private void set(Vector3i p, int value) {
        this.value = null;

        for (Symmetry s : Symmetry.symmetries) {
            Vector3i position = s.apply(p);
            moderators.put(position, value);
        }
    }

    public int get(Vector3i p) {
        return moderators.getOrDefault(p, 0);
    }


    // Utility function for positions().
    // Determines whether the given position is worth optimizing, since radiation only happens from the bottom of a fuel rod,
    // to a distance of 4 blocks (from the outside of the fuel rod block)
    private static boolean insideBounds(Vector3i p) {
        return (Math.pow(p.x - Simulation.dx / 2, 2) + Math.pow(p.y, 2) + Math.pow(p.z - Simulation.dz / 2, 2) <= 26)
            || (Math.abs(p.x - Simulation.dx / 2) + Math.abs(p.z - Simulation.dz / 2) == 1);
    }

    // Gives the array of block positions that we work with
    private static final Vector3i[] positions;

    static {
        List<Vector3i> result = new ArrayList<Vector3i>();
        for (int x = 0; x < Simulation.dx; x++) {
            for (int y = 0; y < Simulation.dy; y++) {
                for (int z = 0; z < Simulation.dz; z++) {
                    Vector3i p = new Vector3i(x, y, z);
                    if (insideBounds(p)) {
                        result.add(p);
                    }
                }
            }
        }
        positions = result.toArray(new Vector3i[result.size()]);
    }

    @Override
    protected Specimen clone() {
        Specimen result = new Specimen(seed, insertion);
        result.moderators = new HashMap<Vector3i, Integer>(this.moderators);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(evaluate());
        builder.append("\n");

        builder.append("Insertion: ");
        builder.append(insertion);
        builder.append("\n");

        for (int y = 0; y < Simulation.dy; y++) {
            for (int z = 0; z < Simulation.dz; z++) {
                for (int x = 0; x < Simulation.dx; x++) {
                    int v = get(new Vector3i(x, y, z));
                    if (v == -1) {
                        builder.append(ReactorModeratorRegistry.Color.RESET);
                    } else {
                        builder.append(ReactorModeratorRegistry.colors[v]);
                    }
                    builder.append(String.format("%2s", v));
                }
                builder.append("\n");
            }
            builder.append("\n");
        }

        builder.append(ReactorModeratorRegistry.Color.RESET);

        return builder.toString();
    }
}
