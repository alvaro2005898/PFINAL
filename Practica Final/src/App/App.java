package App;
import View.BaseView;
import View.InteractiveView;
import Controller.Controller;
import Model.BinaryRepository;
import Model.IRepository;
import Model.Model;
import Model.NotionRepository;

public class App {
    public static void main(String[] args) throws Exception {
        IRepository repository;
        if (args.length == 4 && args[0].equals("--repository") && args[1].equals("notion")) {
            String apiKey = args[2];
            String databaseId = args[3];
            repository = new NotionRepository(apiKey, databaseId);
        } else if (args.length == 2 && args[0].equals("--repository") && args[1].equals("bin")) {
            repository = new BinaryRepository();
        } else {
            System.out.println("No se pasaron argumentos válidos. Usando BinaryRepository por defecto.");
            repository = new BinaryRepository();
        }
        Model model=new Model(repository);
        BaseView view=new InteractiveView();
        Controller controller=new Controller(model,view);
        view.setController(controller);
        controller.start();
        controller.end();
    }
}
