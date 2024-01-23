package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ResourceManager {
    private static HashMap<Class<? extends Resource>, OneResourceManager<? extends Resource>> managers = new HashMap<>();
    
    public static <T extends Resource> void registerType(Class<T> resourceClass, String resourceLocation, ResourceSupplier<T> supplier) {
        managers.put(resourceClass, new OneResourceManager<>(resourceLocation, supplier));
    }
    
    public static <T extends Resource> T getFromName(String name, Class<T> resourceClass) throws IOException {
        assert managers.containsKey(resourceClass);
        
        @SuppressWarnings("unchecked")
        OneResourceManager<T> om = (OneResourceManager<T>) managers.get(resourceClass);
        
        return om.getFromName(name);
    }
    
    public static void clear() {
        managers.forEach((key, value) -> {
            value.resources.clear();
        });
    }
    
    private static class OneResourceManager<T extends Resource> {
        public HashMap<String, T> resources = new HashMap<>();
        public String resourceLocations;
        public ResourceSupplier<T> supplier;
        
        public OneResourceManager(String resourceLocation, ResourceSupplier<T> supplier) {
            this.resourceLocations = resourceLocation;
            this.supplier = supplier;
        }
        
        public T getFromName(String name) {
            if(resources.containsKey(name)) return resources.get(name);
            
            File jsonFile = new File(resourceLocations + name + ".json");
            
            try {
                T newResource = supplier.get(jsonFile);
                resources.put(name, newResource);
                return newResource;
            } catch (IOException e) {
                System.err.println("Error: failed to load resource");
                e.printStackTrace();
            }
            
            return null;
        }
    }
    
    @FunctionalInterface
    public static interface ResourceSupplier<T> {
        public T get(File jsonFile) throws IOException;
    }
    
}
