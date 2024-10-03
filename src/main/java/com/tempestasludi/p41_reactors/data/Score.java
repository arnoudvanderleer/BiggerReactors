package com.tempestasludi.p41_reactors.data;

public record Score(long yields, double fuelUsage) {
    public double efficiency() {
        return yields / fuelUsage;
    }

    @Override
    public final String toString() {
        return yields + ", " + fuelUsage + ", " + efficiency();
    }
}