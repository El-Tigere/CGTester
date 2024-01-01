package cgtester.scene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

// TODO
public class ResourceManager {
    private static HashMap<Class<?>, HashMap<String, Object>> resources = new HashMap<>();
    private static HashMap<Class<?>, String> resourceLocations = new HashMap<>();
    
    static {
        resourceLocations.put(Material.class, "src/cgtester/resources/materials/");
        resourceLocations.put(Mesh.class, "src/cgtester/resources/meshes/");
        resourceLocations.put(Scene.class, "src/cgtester/resources/scenes/");
        resourceLocations.put(ShaderProgram.class, "src/cgtester/resources/shaders/");
        resourceLocations.put(Texture.class, "src/cgtester/resources/textures/");
    }
    
    public static <T> T getFromName(String name, T... junk) throws IOException {
        Class<T> resourceClass = (Class<T>) junk.getClass().getComponentType();
        if(!resources.containsKey(resourceClass)) {
            resources.put(resourceClass, new HashMap<>());
        }
        HashMap<String, Object> hm = resources.get(resourceClass);
        
        if(!hm.containsKey(name)) {
            Object o = null;
            File jsonFile = new File(resourceLocations.get(resourceClass) + name + ".json");
            switch(resourceClass.getName()) {
                case "cgtester.scene.Material":
                    o = Material.fromJsonFile(jsonFile);
                    break;
                case "cgtester.scene.Mesh":
                    o = Mesh.fromJsonFile(jsonFile);
                    break;
                case "cgtester.scene.Scene":
                    o = Scene.fromJsonFile(jsonFile);
                    break;
                case "cgtester.scene.ShaderProgram":
                    o = ShaderProgram.fromJsonFile(jsonFile);
                    break;
                case "cgtester.scene.Texture":
                    o = Texture.fromJsonFile(jsonFile);
                    break;
                default:
                    System.err.println("Invalid Resource Type: " + resourceClass.getName());
            }
            hm.put(name, o);
        }
        
        return (T) hm.get(name);
    }
    
}
