package org.example.parsing.alegro;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        SalesProductsFinder finder = new SalesProductsFinder();

        Map<String, Path> tasks = new HashMap<>();
        tasks.put("https://allegro.pl/kategoria/gitary-i-akcesoria-122641?bmatch=baseline-product-eyesa2-engag-dict45-cul-1-1-1106",
                Paths.get("gitary-i-akcesoria.csv"));
        tasks.put("https://allegro.pl/kategoria/laptopy-491?bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-1-1106",
                Paths.get("laptopy.csv"));
        tasks.put("https://allegro.pl/kategoria/odziez-meska-1455?bmatch=baseline-product-eyesa2-engag-dict45-fas-1-1-1106",
                Paths.get("odziez-meska.csv"));
        finder.getSalesProductsToCSV(tasks);
    }
}
