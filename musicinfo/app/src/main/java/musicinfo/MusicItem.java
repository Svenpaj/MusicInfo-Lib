package musicinfo;


import java.util.*;
import java.io.*;

public abstract class MusicItem implements Serializable {
    public static Map<Class<? extends MusicItem>, List<MusicItem>> registry = new HashMap<>();

    public String name;
    public String info = "";



    public MusicItem(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return this.name; }

    public static List<MusicItem> getRegistryOf(Class<? extends MusicItem> type) {
        if (!registry.containsKey(type)) registry.put(type, new ArrayList<MusicItem>());
        return registry.get(type);
    }

    public static MusicItem getFromRegistry(Class<? extends MusicItem> type, int i) {
        return getRegistryOf(type).get(i);
    }

    public static void register(Class<? extends MusicItem> type, MusicItem item) {
        getRegistryOf(type).add(item);
    }

    public static MusicItem unregister(Class<? extends MusicItem> type, int i) {
        return getRegistryOf(type).remove(i);
    }

    public static void serialize(String fileName) {
        try {
            var fos = new FileOutputStream(fileName + ".ser");
            var oos = new ObjectOutputStream(fos);
            oos.writeObject(registry);
            oos.close();
            fos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void deserialize(String fileName) {
        try {
            var fis = new FileInputStream(fileName + ".ser");
            var ois = new ObjectInputStream(fis);
            registry = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void enumerate(List li) {
        for (int i = 0; i < li.size(); i++) {
            System.out.println("(" + (i + 1) + ") " + li.get(i));
        }
    }







}
