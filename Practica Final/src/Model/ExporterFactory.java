package Model;

public class ExporterFactory {
    public static IExporter obtenerExporter(String tipoExporter){
        if(tipoExporter.equals("csv")){
            return new CSVExporter();
        }else{
            return new JSONExporter();
        }
    }
}
