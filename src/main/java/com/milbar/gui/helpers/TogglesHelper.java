package com.milbar.gui.helpers;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.abstracts.Algorithm;
import com.milbar.logic.abstracts.Mode;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TogglesHelper {
    
    private final static Logger log = LoggerFactory.getLogger(TogglesHelper.class);
    
    public static <K, V> Map<V, K> swapKeyValueMap(Map<K, V> map) {
        Stream<Map.Entry<K, V>> stream = map.entrySet().stream();
        return stream.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
    
    public static Map<Algorithm, Toggle> prepareAlgorithmToggleMap(ArrayList<RadioButton> radioButtons) {
        log.log(Level.INFO, "Creating algorithm type map.");
        return new HashMap<>() {{
            put(Algorithm.AES, radioButtons.get(0));
            put(Algorithm.DES, radioButtons.get(1));
            put(Algorithm.DESeee, radioButtons.get(2));
            put(Algorithm.DESede2, radioButtons.get(3));
            put(Algorithm.DESede3, radioButtons.get(4));
            put(Algorithm.Blowfish, radioButtons.get(5));
        }};
    }
    
    public static Map<Mode, Toggle> prepareModeToggleMap(ArrayList<RadioButton> radioButtons) {
        log.log(Level.INFO, "Creating block type map.");
        return new HashMap<>() {{
            put(Mode.ECB, radioButtons.get(0));
            put(Mode.CBC, radioButtons.get(1));
            put(Mode.CFB, radioButtons.get(2));
            put(Mode.OFB, radioButtons.get(3));
        }};
    }
}
