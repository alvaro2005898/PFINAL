package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import App.RepositoryException;
public class Model {
    private IRepository repository;
    private IExporter exporter;
    private ArrayList<Task> listaTareas=new ArrayList<>();

    public Model(IRepository repository){
        this.repository=repository;
    }

    public void setExporter(IExporter exporter){
        this.exporter=exporter;
    }

    public void addTask(Task task) throws RepositoryException{
        repository.addTask(task);
    }

    public void removeTask(Task task) throws RepositoryException{
        repository.remove(task);
    }

    public ArrayList<Task> getAllTask() throws RepositoryException{
        return repository.getAllTask();
    }

    public void modifyTask(Task tareaModificar) throws RepositoryException{
        repository.modifyTask(tareaModificar);
    }

    public void exportTasks(ArrayList<Task> tasks) throws RepositoryException{
        exporter.exportTasks(tasks);
    }

    public ArrayList<Task> importTasks() throws RepositoryException{
        listaTareas=repository.getAllTask();
        ArrayList<Task> importadas = exporter.importTasks(); 
        for(Task t:importadas){
            listaTareas.add(t);
        }
        return listaTareas;
    }

    public void marcarTareas(Task task) throws RepositoryException{
        boolean encontrado=false;
        ArrayList<Task> listaNueva=new ArrayList<>();
        for(Task t:listaTareas){
            if(t.getIdentifier()==task.getIdentifier()){
                listaNueva.add(task);
                encontrado=true;
            }else{
                listaNueva.add(t);
            }
        }
    }

    public ArrayList<Task> tareasSinCompletarPorPrioridad() throws RepositoryException{
        ArrayList<Task> listaTareas=repository.getAllTask();
        ArrayList<Task> listaPrioridad=new ArrayList<>();
        for(Task t:listaTareas){
            if(!t.isCompleted()){
                listaPrioridad.add(t);
            }
        }
        Collections.sort(listaPrioridad,new Comparator<Task>() {
            public int compare(Task t1,Task t2){
                return Integer.compare(t2.getPriority(),t1.getPriority());
            }
        });
        return listaPrioridad;
    }

    public void loadData() throws RepositoryException{
        repository.loadData();
    }

    public void saveData() throws RepositoryException{
        repository.saveData();
    }
}