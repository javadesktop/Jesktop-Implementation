package net.sourceforge.jesktopimpl.core;

import org.jesktop.ObjectRepository;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.EOFException;

public class DefaultObjectRepository implements ObjectRepository {

    final File repoDir;

    public DefaultObjectRepository(File root) {
        repoDir = new File(root, "object-repository");
        repoDir.mkdirs();
    }

    public synchronized void put(String key, Object data) {
        ObjectOutputStream oos = null;
        try {
            File file = new File(repoDir, key);
            file.getParentFile().mkdirs();
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(data);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException("TODO - IO error during write : " + e.getMessage());
        } finally{
            try {
                oos.close();
            } catch (IOException e) {
            }
        }
    }

    public synchronized boolean containsKey(String key) {
        File file = new File(repoDir, key);
        file.getParentFile().mkdirs();
        System.out.println(file.getAbsolutePath());
        return file.exists();
    }

    public synchronized Object get(String key) {
        ObjectInputStream ois = null;
        try {
            File file = new File(repoDir, key);
            file.getParentFile().mkdirs();
            ois = new ObjectInputStream(new FileInputStream(file));
            return ois.readObject();
        } catch (EOFException eofe) {
            return null;
        } catch (FileNotFoundException fnfe) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("TODO - IO error during read");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("TODO - class not found exception");
        } finally{
            try {
                ois.close();
            } catch (IOException e) {
            }
        }
    }

    public synchronized Object get(String key, ClassLoader classLoader) {
        return get(key);
        // TODO should do classloader stuff.
    }

}
