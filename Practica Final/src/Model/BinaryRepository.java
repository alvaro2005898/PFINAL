package Model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;


import App.RepositoryException;

public class BinaryRepository implements IRepository{
    private ArrayList<Task> listaTareas;

    public BinaryRepository(){
        listaTareas=new ArrayList<>();
    }

    
    public Task addTask(Task task) throws RepositoryException{
        for(Task t:listaTareas){
            if(t.getIdentifier()==task.getIdentifier()){
                throw new RepositoryException("Hay una tarea con el mismo id introducido");
            }
        }
        listaTareas.add(task);
        return task;
    }

    public void remove(Task task) throws RepositoryException{
        listaTareas.remove(task);
    }

    public void loadData() throws RepositoryException{
        Path path=Path.of(System.getProperty("user.home"),"tasks.bin");
        if (path.toFile().exists()) {
            try {
                FileInputStream fis=new FileInputStream(path.toFile());
                BufferedInputStream bis=new BufferedInputStream(fis);
                ObjectInputStream ois=new ObjectInputStream(bis);
                listaTareas=(ArrayList<Task>)ois.readObject();

                ois.close();
                bis.close();
                fis.close();
                System.out.println("Datos cargados de: "+path.toAbsolutePath());
            }catch (IOException e) {
                RepositoryException exc=new RepositoryException("Error al cargar datos",e);
                throw exc;
            }catch(ClassNotFoundException ex){
                RepositoryException exc=new RepositoryException("Error al cargar datos",ex);
                throw exc;
            }
        }
    }

    public void saveData() throws RepositoryException{
        Path path=Path.of(System.getProperty("user.home"),"tasks.bin");
        try {
            FileOutputStream fos=new FileOutputStream(path.toFile());
            BufferedOutputStream bos=new BufferedOutputStream(fos);
            ObjectOutputStream oos=new ObjectOutputStream(bos);

            oos.writeObject(listaTareas);

            oos.close();
            bos.close();
            fos.close();
            System.out.println("Datos guardados en: "+path.toAbsolutePath());
        } catch (Exception e) {
            throw new RepositoryException("Error al guardar los datos", e);
        }
    }

    public void modifyTask(Task tareaModificar){
        boolean encontrado=false;
        ArrayList<Task> listaNueva=new ArrayList<>();
        for(Task t:listaTareas){
            if(t.getIdentifier()==tareaModificar.getIdentifier()){
                listaNueva.add(tareaModificar);
                encontrado=true;
            }else{
                listaNueva.add(t);
            }
        }
    }

    public ArrayList<Task> getAllTask() throws RepositoryException{
        return listaTareas;
    }
}
