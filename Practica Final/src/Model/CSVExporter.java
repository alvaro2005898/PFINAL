package Model;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import App.RepositoryException;

public class CSVExporter implements IExporter{
    private ArrayList<Task> listaTareas;

    public CSVExporter(){
        listaTareas=new ArrayList<>();
    }

    public void exportTasks(ArrayList<Task> tasks) throws RepositoryException {
        if(tasks.isEmpty()){
            System.out.println("No hay tareas para exportar");
        }
        Path ruta=Paths.get(System.getProperty("user.home"),"output.csv");
        ArrayList<String> lineas = new ArrayList<>();
        for(Task t:tasks){
            lineas.add(t.toDelimitedString(","));
        }
        try {
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
            System.out.println("Tareas exportadas con exito a: "+ruta.toAbsolutePath());
        } catch (Exception e) {
            throw new RepositoryException("Error al exportar a CSV", e);
        }
    }

    public ArrayList<Task> importTasks() throws RepositoryException {
        Path ruta=Path.of(System.getProperty("user.home"),"output.csv");
        if(!Files.exists(ruta)){
            System.out.println("No existe el fichero indicado");
            return null;
        }
        try {
            List<String> lineas=Files.readAllLines(ruta);
            for(String l:lineas){
                Task task=Task.factory(l, ",");
                if(task!=null){
                    boolean idRepetido=false;
                    for(Task t:listaTareas){
                        if(t.getIdentifier()==task.getIdentifier()){
                            idRepetido=true;
                            break;
                        }
                    }
                    if(!idRepetido){
                        listaTareas.add(task);
                    }else{
                        System.out.println("Se ha encontrado un id repetido");
                    }
                } 
            }
            System.out.println("Tareas importadas con exito desde: "+ruta.toAbsolutePath());
            return listaTareas;
        } catch (Exception e) {
            throw new RepositoryException("Error al hacer la importacion de CSV", e);
        }
    }
}
