package Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable{
    private int identifier;
    private String title;
    private LocalDate date;
    private String content;
    private int priority;
    private int estimatedDuration;
    private boolean completed;

    public Task(int identifier, String title, LocalDate date, String content, int priority, int estimatedDuration,boolean completed) {
        this.identifier = identifier;
        this.title = title;
        this.date = date;
        this.content = content;
        this.priority = priority;
        this.estimatedDuration = estimatedDuration;
        this.completed = completed;
    }

    public Task(){}

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String asTableRow(){
        String tarea=String.format("%-5d%-20s%-30s%-40s%-10d%-10d%-12s",identifier,title,date.toString(),content,priority,estimatedDuration,completed ? "Si" : "No");
        return tarea;
    }

    public String toDelimitedString(String delimiter){
        return identifier+delimiter+title+delimiter+date+delimiter+content+delimiter+priority+delimiter+estimatedDuration+delimiter+completed;
    }

    public static Task factory(String cadena,String delimiter){
        String[] campos=cadena.split(delimiter);
        if(campos.length!=7){
            return null;
        }

        int id=Integer.parseInt(campos[0]);
        String titulo=campos[1];
        String fecha=campos[2];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = null;
        try {
            date = LocalDate.parse(fecha, formatter); 
        } catch (Exception e) {
            System.out.println("Error al convertir la fecha: " + fecha);
            e.printStackTrace();
        }
        String content=campos[3];
        int priority=Integer.parseInt(campos[4]);
        int estimatedDuration=Integer.parseInt(campos[5]);
        boolean completed=Boolean.parseBoolean(campos[6]);
        Task task=new Task(id, titulo, date, content, priority, estimatedDuration, completed);
        return task;
    }

}
