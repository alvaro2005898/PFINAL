package View;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.coti.tools.Esdia;

import App.RepositoryException;
import Model.Task;

public class InteractiveView extends BaseView{
    private boolean exporterEstablecido=false;
    private String exporter;

    public void init() {
        int opc;
        do{
            System.out.println("MENU CRUD");
            System.out.println("1) Añadir tarea ");
            System.out.println("2) Submenu Listados ");
            System.out.println("3) Marcar tareas como completas o incompletas ");
            System.out.println("4) Modificar tarea ");
            System.out.println("5) Eliminar tarea ");
            System.out.println("6) Submenu exportacion e importacion");
            System.out.println("7) Salir del menu ");
            opc=Esdia.readInt("Introduzca la opcion que desea elegir: ");

            switch (opc) {
                case 1:
                    addTask();
                    break;
                case 2:
                    int opcion;
                    do{
                        System.out.println("1) Listado de las tareas sin completar y ordenadas por prioridad(de mayor a menor)");
                        System.out.println("2) Listado del historial completo de tareas");
                        System.out.println("3) Volver al menu principal");
                        opcion=Esdia.readInt("Elija una opcion: ");
                        switch (opcion) {
                            case 1:
                                tareasSinCompletarPorPrioridad();
                                break;
                            case 2:
                                listadoHistorialTareas();
                                break;
                            case 3:
                                System.out.println("Menu principal ");
                                break;
                            default:
                                System.out.println("Opcion incorrecta");
                                break;
                        }
                    }while(opcion!=3);
                    break;
                case 3:
                    marcarTareas();
                    break;
                    
                case 4:
                    modifyTask();
                    break; 
                    
                case 5:
                    removeTask();
                    break;

                case 6:
                    int op=0;
                    do{
                        System.out.println("1) Elegir exporter ");
                        System.out.println("2) Exportar ");
                        System.out.println("3) Importar ");
                        System.out.println("4) Volver al menu principal");
                        op=Esdia.readInt("Elija una opcion: ");
                        switch (op) {
                            case 1:
                                selectExporter();
                                break;
                            case 2:
                                exportTasks();
                                break;
                            case 3:      
                                importTasks(); 
                                break;
                            case 4:
                                System.out.println("Menú principal");
                                System.out.println("");
                                break;
                            default:
                                System.out.println("Opcion incorrecta");
                                break;
                        }
                    }while(op!=4);
                    break;
                case 7:
                    System.out.println("Fin del programa");
                    break;
            
                default:
                    System.out.println("Opcion incorrecta ");
                    break;
            }

        }while(opc!=7);
    }


    @Override
    public void showMessage(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public void showErrorMessage(String mensaje) {
        System.err.println(mensaje);
    }

    @Override
    public void end(){
        showMessage("Programa finalizado con exito");
    }

    public void listadoHistorialTareas(){
        try {
            ArrayList<Task> historialTareas=controller.getAllTask();
            System.out.printf("%-5s%-20s%-30s%-40s%-10s%-10s%-12s\n","ID","TITULO","FECHA","CONTENIDO","PRIORIDAD","DURACION","COMPLETADA");
            if(historialTareas.isEmpty()){
                showErrorMessage("No hay tareas para mostrar");
            }else{
                for(Task t:historialTareas){
                    System.out.println(t.asTableRow());
                }
            }
        } catch (RepositoryException e) {
            showErrorMessage("Error al mostrar el historial de tareas");
        }
    }

    public void tareasSinCompletarPorPrioridad(){
        try {
            ArrayList<Task> tareasPrioridad=controller.tareasSinCompletarPorPrioridad();
            if(tareasPrioridad.isEmpty()){
                System.out.println("Lista de tareas vacia");
            }else{
                for(Task t:tareasPrioridad){
                    System.out.println(t.asTableRow());
                }
            }
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    public void addTask(){
        try { 
        int id=Esdia.readInt("Introduzca el id de la tarea: ");
        String titulo=Esdia.readString("Introduzca el titulo de la tarea: ");
        boolean fechaValida=false;
        LocalDate date = null;
        DateTimeFormatter dateFormato = DateTimeFormatter.ofPattern("d/M/yyyy");
        while (!fechaValida) {
            String fecha = Esdia.readString("Introduzca la fecha de la tarea (dd/MM/yyyy): ");
            try {
                date = LocalDate.parse(fecha, dateFormato);
                fechaValida = true;
            } catch (Exception e) {
                showErrorMessage("Formato de fecha introducido incorrecto");
            }
        }

        String contenido=Esdia.readString("Introduzca el contenido de la tarea: ");
        int prioridad=0;
        do{
            prioridad=Esdia.readInt("Introduzca el numero de prioridad de la tarea (entre 1-5 siendo 5 la mas prioritaria): ");
        }while(prioridad<1 || prioridad>5);
        int duracion=Esdia.readInt("Introduzca la duracion de la tarea (minutos): ");
        boolean completo=Esdia.yesOrNo("Introduzca si la tarea esta completa o no: ");

        Task task=new Task(id, titulo, date, contenido, prioridad, duracion, completo);
        controller.addTask(task);
        showMessage("Tarea añadida con exito");
        }catch(RepositoryException ex){
            showErrorMessage(ex.getMessage());
        }
    }

    public void removeTask(){
        try{
            ArrayList<Task> listaTareas=controller.getAllTask();
            if(listaTareas.isEmpty()){
                showErrorMessage("No se pueden eliminar tareas");
            }else{
                for(Task t:listaTareas){
                    System.out.printf("ID- %d Titulo- %s \n",t.getIdentifier(),t.getTitle());
                }
                int id=Esdia.readInt("Introduzca el id de la tarea que desea eliminar: ");
                Task tareaBorrar=null;
                for(Task t:listaTareas){
                    if(t.getIdentifier()==id){
                        tareaBorrar=t;
                    }
                }
                if(tareaBorrar!=null){
                    controller.removeTask(tareaBorrar);
                    showMessage("Tarea elimanada con exito");
                }else{
                    showErrorMessage("No se encontro la tarea indicada");
                }
            }
        }catch(RepositoryException e){
            showErrorMessage("Error al eliminar la tarea");
        }
    }

    public void selectExporter(){
        System.out.println("Tipos de exporters");
        System.out.println("1) CSV");
        System.out.println("2) JSON");
        int opcion=Esdia.readInt("Introduzca una opcion: ");
        switch (opcion) {
            case 1:
                exporter="csv";
                break;
            case 2:
                exporter="json";
                break;
            default:
                System.out.println("Opcion invalida");
                break;
        }
        exporterEstablecido=true;
        System.out.println("Exporter elegido: "+exporter);
        controller.obtenerExporter(exporter);
    }

    public void modifyTask(){
        try{
            ArrayList<Task> listaTareas=controller.getAllTask();
            if(listaTareas.isEmpty()){
                showErrorMessage("No se pueden modificar tareas");
            }else{
                for(Task t:listaTareas){
                    System.out.printf("ID- %d Titulo- %s \n",t.getIdentifier(),t.getTitle());
                }
                int id=Esdia.readInt("Introduzca el id de la tarea que desea modificar: ");
                Task tareaModificar=null;
                for(Task t:listaTareas){
                    if(t.getIdentifier()==id){
                        tareaModificar=t;
                        break;
                    }
                }
                if(tareaModificar==null){
                    showErrorMessage("No se pudo encontrar la tarea que deseaba modificar");
                    return;
                }
                String tituloNuevo=Esdia.readString("Introduzca el nuevo titulo de la tarea: ");
                String fechaNueva=Esdia.readString("Introduzca la nueva fecha de la tarea (dd/mm/aaaa): ");
                DateTimeFormatter dateFormato = DateTimeFormatter.ofPattern("d/M/yyyy"); 
                LocalDate dateNuevo = null;
                try {
                    dateNuevo = LocalDate.parse(fechaNueva, dateFormato);
                } catch (Exception e) {
                    showErrorMessage("Error con la fecha introducida");
                return; 
                }
                String contenidoNuevo=Esdia.readString("Introduzca el nuevo contenido de la tarea: ");
                int prioridadNueva=0;
                do{
                    prioridadNueva=Esdia.readInt("Introduzca el numero de prioridad de la tarea a modificar (entre 1-5 siendo 5 la mas prioritaria): ");
                }while(prioridadNueva<1 || prioridadNueva>5);
                int duracionNueva=Esdia.readInt("Introduzca la duracion de la tarea a modificar (minutos): ");
                boolean completoNuevo=Esdia.yesOrNo("Introduzca si la tarea que va a modificar esta completa o no: ");

                tareaModificar.setTitle(tituloNuevo);
                tareaModificar.setDate(dateNuevo);
                tareaModificar.setContent(contenidoNuevo);
                tareaModificar.setPriority(prioridadNueva);
                tareaModificar.setEstimatedDuration(duracionNueva);
                tareaModificar.setCompleted(completoNuevo);
                controller.modifyTask(tareaModificar);
                showMessage("Tarea modificada con exito");
            }
        }catch(RepositoryException e){
            showErrorMessage("Error al modificar la tarea");
        }
    }

    public void exportTasks(){
        try {
            if(!exporterEstablecido){
                showErrorMessage("No se ha establecido ningun exporter");
            }else{
                ArrayList<Task> listaTareas=controller.getAllTask();
                if(listaTareas.isEmpty()){
                    showErrorMessage("La lista esta vacia, no se puede realizar la exportacion");
                }else{
                    controller.exportTasks(listaTareas);
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error al exportar las tareas");
        }
    }

    public void importTasks(){
        try {
            if(!exporterEstablecido){
                showErrorMessage("No se ha establecido ningun exporter");
            }else{
                ArrayList<Task> tareasImportadas=controller.importTasks();
                if(tareasImportadas.isEmpty()){
                    showErrorMessage("No se han importado tareas ya que la lista esta vacia");
                }else{
                    showMessage("Tareas importadas con exito");
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error al intentar importar las tareas");
        }
    }

    public void marcarTareas(){
        try{
            ArrayList<Task> listaTareas=controller.getAllTask();
            if(listaTareas.isEmpty()){
                showErrorMessage("No se pueden modificar tareas, la lista esta vacia");
            }else{
                for(Task t:listaTareas){
                    System.out.printf("ID- %d Titulo- %s Completada- %s%n \n",t.getIdentifier(),t.getTitle(),t.isCompleted() ? "SI" : "NO");
                }
                int id=Esdia.readInt("Introduzca el id de la tarea que desea marcar como incompleta o completa: ");
                Task task=null;
                for(Task t:listaTareas){
                    if(t.getIdentifier()==id){
                        task=t;
                        break;
                        }
                    }
                    if(task==null){
                        showErrorMessage("No se pudo encontrar la tarea que deseaba modificar");
                    }
                    task.setCompleted(!task.isCompleted());
                    controller.marcarTareas(task);
                    showMessage("Tarea modificada con exito");
            }
        }catch(RepositoryException e){
            showErrorMessage("Error al modificar la tarea");
        }
    }
}

