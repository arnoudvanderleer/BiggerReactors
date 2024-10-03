package com.tempestasludi.p41_reactors.data;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector3i;

import com.tempestasludi.p41_reactors.Simulation;

public record Symmetry(int sx, int sz, int sxz) {

    public Vector3i apply(Vector3i p) {
        int x0 = Simulation.dx / 2 - (Simulation.dx / 2 - p.x) * sx;
        int z0 = Simulation.dz / 2 - (Simulation.dz / 2 - p.z) * sz;
        int x = x0 * sxz + z0 * (1 - sxz);
        int z = z0 * sxz + x0 * (1 - sxz);

        return new Vector3i(x, p.y, z);
    }

    public static final Symmetry[] symmetries;

    static {
        List<Symmetry> result = new ArrayList<Symmetry>();

        for (int sx = (Simulation.xSymmetry ? -1 : 1); sx <= 1; sx += 2) {
            for (int sz = (Simulation.zSymmetry ? -1 : 1); sz <= 1; sz += 2) {
                for (int sxz = (Simulation.xzSymmetry ? 0 : 1); sxz <= 1; sxz += 1) {
                    result.add(new Symmetry(sx, sz, sxz));
                }
            }
        }

        symmetries = result.toArray(new Symmetry[result.size()]);
    }

}
