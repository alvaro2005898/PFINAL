package Model;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import App.RepositoryException;

public class JSONExporter implements IExporter{
    public void exportTasks(ArrayList<Task> tasks) throws RepositoryException {
        Path ruta=Path.of(System.getProperty("user.home"),"output.json");
        try{
            if(tasks.isEmpty()){
                throw new RepositoryException("No hay tareas para exportar");
            }
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                })
                .setPrettyPrinting() //FORMATEAR
                .create();
            String json=gson.toJson(tasks);
            Files.writeString(ruta, json, StandardCharsets.UTF_8);
            System.out.println("Tareas exportadas con exito a: "+ruta.toAbsolutePath());
        }catch (Exception e) {
            throw new RepositoryException("Error al exportar las tareas al JSON", e);
        }
    }

    public ArrayList<Task> importTasks() throws RepositoryException {
        Path ruta=Path.of(System.getProperty("user.home"),"output.json");
        try {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                })
                .create();

            String json=new String(Files.readAllBytes(ruta), StandardCharsets.UTF_8);
            Type tipoLista=new TypeToken<ArrayList<Task>>() {}.getType();
            ArrayList<Task> tasks=gson.fromJson(json, tipoLista);
            if(tasks.isEmpty()){
                System.out.println("No hay tareas en la lista para importar");
            }else{
                System.out.println("Tareas importadas con exito desde: "+ruta.toAbsolutePath());
            }
            return tasks;
        } catch (Exception e) {
            throw new RepositoryException("Error al realizar la importacion JSON", e);
        }
    }
}
