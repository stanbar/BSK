package com.milbar.logic.login.loaders;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.exceptions.ReadingSerializedFileException;
import com.milbar.logic.exceptions.WritingSerializedFileException;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerializedDataReader <K, V> {
    
    private final static Logger logger = LoggerFactory.getLogger(SerializedDataReader.class);
    
    private Map<K, V> serializedCollection;
    private Path pathToSerializedData;
    private boolean isCollectionUpToDate = false;
    
    public SerializedDataReader(Path path) {
         this.pathToSerializedData = path;
     }
    
    public void readFromFile() throws ReadingSerializedFileException {
        isCollectionUpToDate = true;
        try (FileInputStream fileStream = new FileInputStream(pathToSerializedData.toFile());
             ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
            
            serializedCollection = (Map<K, V>) objectStream.readObject();
            
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new ReadingSerializedFileException("Failed to load serialized collection.");
        }
    }
    
    public void saveToFile() throws WritingSerializedFileException {
        File file = pathToSerializedData.toFile();
        file.getParentFile().mkdirs();
        try (FileOutputStream fileStream = new FileOutputStream(pathToSerializedData.toFile());
             ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
            
            objectStream.writeObject(serializedCollection);
            isCollectionUpToDate = false;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new WritingSerializedFileException("Failed to write to file collection");
        }
    }
    
    public List<K> getKeysList() {
        return new ArrayList<>(getCollection().keySet());
    }
    
    public Map<K, V> getCollection() {
        if (!isCollectionUpToDate) {
            try {
                readFromFile();
            } catch (ReadingSerializedFileException e) {
                e.printStackTrace();
            }
        }
        return serializedCollection;
    }
    
    public boolean updateCollection(K key, V value) throws WritingSerializedFileException {
        if (!serializedCollection.containsKey(key)) {
            serializedCollection.put(key, value);
            saveToFile();
            return true;
        }
        else
            return false;
    }
    
    public void updateCollection(Map<K, V> newCollection) throws WritingSerializedFileException {
         serializedCollection = newCollection;
         saveToFile();
    }
    
    public V getValue(K key) {
         return serializedCollection.get(key);
    }
    
    public boolean keyExists(K key) {
         return serializedCollection.containsKey(key);
    }
    
    public boolean removeItem(K key) throws WritingSerializedFileException {
        if (serializedCollection.remove(key) != null) {
            saveToFile();
            return true;
        }
        else
            return false;
    }

}
