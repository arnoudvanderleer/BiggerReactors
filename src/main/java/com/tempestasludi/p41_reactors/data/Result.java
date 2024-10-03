package com.tempestasludi.p41_reactors.data;

public record Result(long gen, double efficiency) {
    @Override
    public final String toString() {
        return gen + ", " + efficiency;
    }
}