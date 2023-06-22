import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonScraper {
    public static void main(String[] args) {
        try {
            // Paso 2: Obtener los datos de la página web de Amazon
            String url = "https://www.amazon.com/s?k=juegos";
            Document document = Jsoup.connect(url).get();

            // Paso 3: Analizar el HTML y extraer los datos relevantes
            Elements productElements = document.select("div[data-component-type='s-search-result']");

            // Paso 4: Guardar los datos en un archivo CSV
            CSVFormat csvFormat = CSVFormat.DEFAULT;
            try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter("productos.csv"), csvFormat)) {
                // Itera sobre los datos de los productos y escribe cada uno en una línea del
                // archivo CSV
                for (Element productElement : productElements) {
                    String title = productElement.select("h2 a span").text();
                    String priceText = productElement.select("span.a-offscreen").text();

                    // Extraer el precio en euros utilizando una expresión regular
                    String price = extractPriceInEuros(priceText);

                    csvPrinter.printRecord(title, price);
                }
            }

            System.out.println("Se ha completado el proceso de scraping y se ha generado el archivo productos.csv.");
        } catch (IOException e) {
            System.err.println("Ocurrió un error durante el proceso de scraping: " + e.getMessage());
        }
    }

    private static String extractPriceInEuros(String priceText) {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(priceText);

        if (matcher.find()) {
            double price = Double.parseDouble(matcher.group());
            return String.format("%.2f €", price);
        }

        return "";
    }
}
