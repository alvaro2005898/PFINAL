package Model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import App.RepositoryException;
import notion.api.v1.NotionClient;
import notion.api.v1.http.OkHttp5Client;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageParent;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;
import notion.api.v1.model.pages.PageProperty.RichText.Text;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import notion.api.v1.request.pages.CreatePageRequest;
import notion.api.v1.request.pages.UpdatePageRequest;


public class NotionRepository implements IRepository{
    private String apiKey;
    private String databaseId;
    private NotionClient client;
    private String columnaClave="ID";

    //151a0416414880a9b6fff839b5d67362

    public NotionRepository(String apiKey,String databaseId){
        this.apiKey=apiKey;
        this.databaseId=databaseId;
        this.client=new NotionClient(apiKey);
        client.setHttpClient(new OkHttp5Client(6000, 60000, 6000));
        client.setLogger(new Slf4jLogger());
        System.setProperty("notion.api.v1.logging.StdoutLogger", "debug");

    }

    public Task addTask(Task task) throws RepositoryException {
        System.out.println("Creando nueva pagina");
        Map<String, PageProperty> properties = Map.of(
                "ID", createTitleProperty(String.valueOf(task.getIdentifier())),
                "TASK TITULO", createRichTextProperty(task.getTitle()),
                "FECHA", createDateProperty(task.getDate()),
                "CONTENIDO", createRichTextProperty(task.getContent()),
                "PRIORIDAD", createNumberProperty(task.getPriority()),
                "DURACION", createNumberProperty(task.getEstimatedDuration()),
                "COMPLETADO", createCheckboxProperty(task.isCompleted())
        );

        PageParent parent = PageParent.database(databaseId);
        // Crear la solicitud a la API de Notion
        CreatePageRequest request = new CreatePageRequest(parent, properties);
        // Ejecutar la solicitud (necesita de conexión a internet)
        Page response = client.createPage(request);

        System.out.println("Página creada con ID (interno Notion)" + response.getId());
        String notionPageId=response.getId();
        Task taskNew=new Task(task.getIdentifier(), task.getTitle(), task.getDate(), task.getContent(), task.getPriority(),
        task.getEstimatedDuration(), task.isCompleted());
        return taskNew;
        
    }

    public void remove(Task task) throws RepositoryException {
       try {
            String pageId = findPageIdByIdentifier(String.valueOf(task.getIdentifier()),columnaClave);
            if (pageId == null) {
                System.out.println("No se encontró una tarea con el Identifier: " + task.getIdentifier());
                return;
            }
            // Archivar la página
            UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, Collections.emptyMap(), true);
            client.updatePage(updateRequest);
            System.out.println("Tarea archivada con ID (interno Notion)" + pageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> getAllTask() throws RepositoryException{
        ArrayList<Task> listaTareas = new ArrayList<>();
        try {
            // Crear la solicitud para consultar la base de datos
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);

            // Ejecutar la consulta
            QueryResults queryResults = client.queryDatabase(queryRequest);

            // Procesar los resultados
            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                Task task = mapPageToTask(page.getId(), properties);
                if (task != null) {
                    listaTareas.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTareas;
    }

    public void loadData() throws RepositoryException {
    }

    public void saveData() throws RepositoryException {
    }

    public void modifyTask(Task tareaModificar) throws RepositoryException {
        try {
            String pageId = findPageIdByIdentifier(String.valueOf(tareaModificar.getIdentifier()), columnaClave);
            if (pageId == null) {
                System.out.println("No se encontró un registro con el Identifier: " + tareaModificar.getIdentifier());
                return;
            }

            // Crear las propiedades actualizadas
            Map<String, PageProperty> updatedProperties = Map.of(
                "ID", createTitleProperty(String.valueOf(tareaModificar.getIdentifier())),
                "TASK TITULO", createRichTextProperty(tareaModificar.getTitle()),
                "FECHA", createDateProperty(tareaModificar.getDate()),
                "CONTENIDO", createRichTextProperty(tareaModificar.getContent()),
                "PRIORIDAD", createNumberProperty(tareaModificar.getPriority()),
                "DURACION", createNumberProperty(tareaModificar.getEstimatedDuration()),
                "COMPLETADO", createCheckboxProperty(tareaModificar.isCompleted())
            );

            // Crear la solicitud de actualización
            UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, updatedProperties);
            client.updatePage(updateRequest);

            System.out.println("Tarea actualizada con ID (interno Notion)" + pageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String findPageIdByIdentifier(String identifier, String columnaClave) {
        try {
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);
            QueryResults queryResults = client.queryDatabase(queryRequest);

            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                if (properties.containsKey(columnaClave) &&
                        properties.get(columnaClave).getTitle().get(0).getText().getContent().equals(identifier)) {
                    return page.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PageProperty createTitleProperty(String title) {
        RichText idText = new RichText();
        idText.setText(new Text(title));
        PageProperty idProperty = new PageProperty();
        idProperty.setTitle(Collections.singletonList(idText));
        return idProperty;
    }


    private PageProperty createRichTextProperty(String text) {
        RichText richText = new RichText();
        richText.setText(new Text(text));
        PageProperty property = new PageProperty();
        property.setRichText(Collections.singletonList(richText));
        return property;
    }

    private PageProperty createNumberProperty(Integer number) {
        PageProperty property = new PageProperty();
        property.setNumber(number);
        return property;
    }

    private PageProperty createDateProperty(LocalDate date) {
        DateTimeFormatter formato=DateTimeFormatter.ISO_LOCAL_DATE;
        String formatoDate = date.format(formato); 
        PageProperty property = new PageProperty();
        PageProperty.Date dateProperty = new PageProperty.Date();
        dateProperty.setStart(formatoDate); 
        property.setDate(dateProperty);

        return property;
    }

    private PageProperty createCheckboxProperty(boolean checked) {
        PageProperty property = new PageProperty();
        property.setCheckbox(checked);
        return property;
    }

    private Task mapPageToTask(String pageId, Map<String, PageProperty> properties) {
    try {
        Task task = new Task();

        // Obtener el ID
        PageProperty idProperty = properties.get("ID");
        if (idProperty != null && idProperty.getTitle().size() > 0) {
            String id = idProperty.getTitle().get(0).getText().getContent();
            task.setIdentifier(Integer.parseInt(id));  
        }

        // Obtener el Titulo
        PageProperty titleProperty = properties.get("TASK TITULO");
        if (titleProperty != null && titleProperty.getRichText().size() > 0) {
            task.setTitle(titleProperty.getRichText().get(0).getText().getContent());
        }

        // Obtener la Fecha
        PageProperty dateProperty = properties.get("FECHA");
        if (dateProperty != null && dateProperty.getDate() !=null) {
            String startDate = dateProperty.getDate().getStart(); 
            if (startDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; 
                LocalDate date = LocalDate.parse(startDate, formatter);
                task.setDate(date);
            }
        }

        // Obtener el Contenido
        PageProperty contentProperty = properties.get("CONTENIDO");
        if (contentProperty != null && contentProperty.getRichText().size() > 0) {
            task.setContent(contentProperty.getRichText().get(0).getText().getContent());
        }

        // Obtener la Prioridad
        PageProperty priorityProperty = properties.get("PRIORIDAD");
        if (priorityProperty != null) {
            task.setPriority(priorityProperty.getNumber().intValue());
        }

        // Obtener la Duración
        PageProperty durationProperty = properties.get("DURACION");
        if (durationProperty != null) {
            task.setEstimatedDuration(durationProperty.getNumber().intValue());
        }

        // Obtener el estado de Completado
        PageProperty completedProperty = properties.get("COMPLETADO");
        if (completedProperty != null) {
            task.setCompleted(completedProperty.getCheckbox());
        }

        return task;
    } catch (Exception e) {
        System.err.println("Error al mapear la página a Task: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

}
