package net.sourceforge.jesktopimpl.core;

import org.jesktop.ObjectRepository;

import java.util.HashMap;

public class DefaultObjectRepository implements ObjectRepository {

    HashMap hashMap;

    public DefaultObjectRepository() {
        //todo deserialize
        this.hashMap = new HashMap();
    }

    public void put(String key, Object data) {
        hashMap.put(key, data);
        // todo serialize
    }

    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    public Object get(String key) {
        return hashMap.get(key);
    }

    public Object get(String key, ClassLoader classLoader) {
        return hashMap.get(key); //TODO classloader support.
    }

}
