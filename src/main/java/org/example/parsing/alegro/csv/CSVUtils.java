package org.example.parsing.alegro.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

// https://mkyong.com/java/how-to-export-data-to-csv-file-java/
// https://www.baeldung.com/java-csv
public class CSVUtils {
    private static final char DEFAULT_SEPARATOR = ',';

    private CSVUtils() {
    }

    public static void writeLine(Writer writer, List<String> values) throws IOException {
        writeLine(writer, values, DEFAULT_SEPARATOR);
    }

    public static void writeLine(Writer writer, List<String> values, char separator) throws IOException {
        writer.append(convertToCSVLine(values, separator))
              .append(System.lineSeparator());
    }

    public static String convertToCSVLine(List<String> data, char separator) {
        return data.stream()
                   .map(CSVUtils::escapeSpecialCharacters)
                   .collect(Collectors.joining(String.valueOf(separator)));
    }

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
