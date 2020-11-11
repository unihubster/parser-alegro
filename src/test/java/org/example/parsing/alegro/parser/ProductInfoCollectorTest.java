package org.example.parsing.alegro.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductInfoCollectorTest {
    private static final ProductInfoCollector infoCollector = new ProductInfoCollector();
    private static Document doc;

    @BeforeAll
    static void init() throws IOException {
        String link = "https://allegro.pl/oferta/presety-lightroom-15-kolekcji-ponad-100-filtrow-9373140840";
        doc = getDocument(link);
    }

    private static Document getDocument(String link) throws IOException {
        return Jsoup.connect(link).maxBodySize(0).get();
    }

    @Test
    void parseName() {
        String expected = "Presety lightroom 15 kolekcji ponad 100 filtrów";

        String actual = infoCollector.parseName(doc);

        assertEquals(expected, actual);
    }

    @Test
    void parsePrice() {
        BigDecimal expected = new BigDecimal("29.00");

        BigDecimal actual = infoCollector.parsePrice(doc);

        assertEquals(expected, actual);
    }

    @Test
    void parseFullPrice() {
        BigDecimal expected = new BigDecimal("47.00");

        BigDecimal actual = infoCollector.parseFullPrice(doc);

        assertEquals(expected, actual);
    }

    @Test
    void parseSalesPercent() {
        int expected = 38;

        int actual = infoCollector.parseSalesPercent(doc);

        assertEquals(expected, actual);
    }

    @Test
    void parseParameters() {
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("Stan", "Nowy");
        expected.put("Faktura", "Wystawiam fakturę bez VAT");
        expected.put("Model", "Lightroom Classic / Mobile / 5 / 6 / 7");
        expected.put("Producent", "Adobe");
        expected.put("Kod producenta", "Adobe");

        Map<String, String> actual = infoCollector.parseParameters(doc);

        assertEquals(expected, actual);
    }
}