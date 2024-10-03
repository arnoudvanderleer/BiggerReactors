package com.tempestasludi.p41_reactors;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Random random = new Random();

        Specimen s = new Specimen(random);

        System.out.println(s.evaluate());
        // System.out.println(s);
    }
}
