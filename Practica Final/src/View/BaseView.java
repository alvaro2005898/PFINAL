package View;
import Controller.Controller;

public abstract class BaseView {
    protected Controller controller;
    
    public void setController(Controller controller){
        this.controller=controller;
    }

    public abstract void init();
    public abstract void showMessage(String mensaje);
    public abstract void showErrorMessage(String mensaje);
    public abstract void end();
}
