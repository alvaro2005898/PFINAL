package Controller;

import java.util.ArrayList;

import View.BaseView;
import App.RepositoryException;
import Model.ExporterFactory;
import Model.IExporter;
import Model.Model;
import Model.Task;


public class Controller {
    private Model model;
    private BaseView view;
    public Controller(Model model,BaseView view){
        this.model=model;
        this.view=view;
    }

    public void start(){
        try {
            model.loadData();
            view.init();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void end(){
        try {
            model.saveData();
            view.end();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void addTask(Task task) throws RepositoryException{
        model.addTask(task);
    }

    public void removeTask(Task task) throws RepositoryException{
        model.removeTask(task);
    }

    public ArrayList<Task> getAllTask() throws RepositoryException{
        return model.getAllTask();
    }

    public ArrayList<Task> tareasSinCompletarPorPrioridad() throws RepositoryException{
        return model.tareasSinCompletarPorPrioridad();
    }

    public void obtenerExporter(String tipoExporter){
        IExporter exporter=ExporterFactory.obtenerExporter(tipoExporter);
        model.setExporter(exporter);
    }

    public void modifyTask(Task tareaModificar)throws RepositoryException{
        model.modifyTask(tareaModificar);
    }

    public void exportTasks(ArrayList<Task> tasks)throws RepositoryException{
        model.exportTasks(tasks);
    }

    public ArrayList<Task> importTasks() throws RepositoryException{
        return model.importTasks();
    }

    public void marcarTareas(Task task) throws RepositoryException{
        model.marcarTareas(task);
    }

}
