package net.roguelogix.biggerreactors.registries;

import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.phosphophyllite.config.ConfigValue;
import net.roguelogix.phosphophyllite.data.DatapackLoader;
import net.roguelogix.phosphophyllite.networking.SimplePhosChannel;
import net.roguelogix.phosphophyllite.registry.OnModLoad;
import net.roguelogix.phosphophyllite.robn.ROBNObject;
import net.roguelogix.phosphophyllite.serialization.PhosphophylliteCompound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


public class ReactorModeratorRegistry {
    
    public interface IModeratorProperties extends ROBNObject {
        double absorption();
        
        double heatEfficiency();
        
        double moderation();
        
        double heatConductivity();
        
        @Override
        default Map<String, Object> toROBNMap() {
            final Map<String, Object> map = new HashMap<>();
            map.put("absorption", absorption());
            map.put("heatEfficiency", heatEfficiency());
            map.put("moderation", moderation());
            map.put("heatConductivity", heatConductivity());
            return map;
        }
        
        @Override
        default void fromROBNMap(Map<String, Object> map) {
            throw new IllegalArgumentException("");
        }
    }
    
    public static class ModeratorProperties implements IModeratorProperties, ROBNObject {
        
        public static final ModeratorProperties EMPTY_MODERATOR = new ModeratorProperties(0, 0, 1, 0);
        
        public final double absorption;
        public final double heatEfficiency;
        public final double moderation;
        public final double heatConductivity;
        
        public ModeratorProperties(double absorption, double heatEfficiency, double moderation, double heatConductivity) {
            this.absorption = absorption;
            this.heatEfficiency = heatEfficiency;
            this.moderation = moderation;
            this.heatConductivity = heatConductivity;
        }
        
        public ModeratorProperties(IModeratorProperties properties) {
            this(properties.absorption(), properties.heatEfficiency(), properties.moderation(), properties.heatConductivity());
        }
        
        @Override
        public double absorption() {
            return absorption;
        }
        
        @Override
        public double heatEfficiency() {
            return heatEfficiency;
        }
        
        @Override
        public double moderation() {
            return moderation;
        }
        
        @Override
        public double heatConductivity() {
            return heatConductivity;
        }
        
        
    }
    
    // TODO: unify these names across all registries
    private enum RegistryType {
        tag,
        registry,
        fluidtag,
        fluid
    }
    
    public enum Color {
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        public final String v;

        private Color(String v) {
            this.v = v;
        }

        @Override
        public String toString() {
            return v;
        }
    }

    // The moderator properties that the values in Specimen.moderators refer to.
    // When you add or remove an entry here, you should also add or remove an entry in the colors array below.
    public static ReactorModeratorRegistry.IModeratorProperties[] registry = {
        new ModeratorProperties(0.1,  0.25, 1.1, 0.05), // 0: air
        new ModeratorProperties(0.66, 0.9,  3.5, 3.5 ), // 1: allthemodium
        new ModeratorProperties(0.15, 0.75, 8,   4   ), // 2: vibranium
        new ModeratorProperties(0.95, 0.82, 2,   5   ), // 3: unobtanium
        // new ModeratorProperties(0.1,  0.5,  2,   2   ), // 4: graphite
        // new ModeratorProperties(0.55, 0.85, 1.5, 3   ), // 5: diamond
        // new ModeratorProperties(0.55, 0.85, 1.5, 2.5 ), // 6: emerald
    };

    // The colors that are used to print the different materials in the terminal
    public static final Color[] colors = new Color[]{
        Color.WHITE,    // 0: air
        Color.YELLOW,   // 1: allthemodium
        Color.BLUE,     // 2: vibranium
        Color.PURPLE,   // 3: unobtanium
        // Color.BLACK,    // 4: graphite
        // Color.CYAN,     // 5: diamond
        // Color.GREEN,    // 6: emerald
    };
}
