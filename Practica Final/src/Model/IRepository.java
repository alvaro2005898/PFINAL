package Model;


import java.util.ArrayList;

import App.RepositoryException;

public interface IRepository {
    public Task addTask(Task task) throws RepositoryException;
    public void remove(Task task) throws RepositoryException;
    public ArrayList<Task> getAllTask() throws RepositoryException;
    public void loadData() throws RepositoryException;
    public void saveData() throws RepositoryException;
    public void modifyTask(Task tareaModificar) throws RepositoryException;
}
