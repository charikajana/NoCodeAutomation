package com.automation.browser.healing;

import com.automation.utils.LoggerUtil;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persistently stores and manages element fingerprints for self-healing.
 */
public class HealingRegistry {
    private static final LoggerUtil logger = LoggerUtil.getLogger(HealingRegistry.class);
    private static HealingRegistry instance;
    private static final String STORAGE_PATH = "config/healing_registry.dat";

    private Map<String, ElementFingerprint> fingerprints = new ConcurrentHashMap<>();

    private HealingRegistry() {
        load();
    }

    public static synchronized HealingRegistry getInstance() {
        if (instance == null) {
            instance = new HealingRegistry();
        }
        return instance;
    }

    public void store(String stepKey, ElementFingerprint fingerprint) {
        fingerprints.put(stepKey.toLowerCase(), fingerprint);
        logger.debug("Stored fingerprint for: {}", stepKey);
        save();
    }

    public ElementFingerprint get(String stepKey) {
        return fingerprints.get(stepKey.toLowerCase());
    }

    private synchronized void save() {
        try {
            File file = new File(STORAGE_PATH);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(fingerprints);
            }
        } catch (IOException e) {
            logger.error("Failed to save Healing Registry: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(STORAGE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            fingerprints = (Map<String, ElementFingerprint>) ois.readObject();
            logger.info("Loaded {} fingerprints for self-healing", fingerprints.size());
        } catch (Exception e) {
            logger.warning("Could not load Healing Registry: {}", e.getMessage());
        }
    }
}
