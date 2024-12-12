package Model;

import java.util.ArrayList;

import App.RepositoryException;

public interface IExporter {
    public void exportTasks(ArrayList<Task> tasks) throws RepositoryException;
    public ArrayList<Task> importTasks() throws RepositoryException;
}