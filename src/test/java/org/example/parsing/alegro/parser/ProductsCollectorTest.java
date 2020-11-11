package org.example.parsing.alegro.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductsCollectorTest {
    private static final ProductsCollector PRODUCTS_COLLECTOR = new ProductsCollector();

    @Test
    void shouldAddPageParamIfAbsent() {
        String link = "https://allegro.pl/kategoria/fotografia?bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-3-0717";
        String expected = "https://allegro.pl/kategoria/fotografia?bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-3-0717&p=2";

        String actual = PRODUCTS_COLLECTOR.nextPageLink(link);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReplacePageNumberByAddPlusOneIfPageParamIsFirst() {
        String link = "https://allegro.pl/kategoria/fotografia?p=3&bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-3-0717";
        String expected = "https://allegro.pl/kategoria/fotografia?p=4&bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-3-0717";

        String actual = PRODUCTS_COLLECTOR.nextPageLink(link);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReplacePageNumberByAddPlusOneIfPageParamIsLast() {
        String link = "https://allegro.pl/kategoria/odziez-meska-1455?bmatch=baseline-product-eyesa2-engag-dict45-fas-1-3-0717&p=3";
        String expected = "https://allegro.pl/kategoria/odziez-meska-1455?bmatch=baseline-product-eyesa2-engag-dict45-fas-1-3-0717&p=4";

        String actual = PRODUCTS_COLLECTOR.nextPageLink(link);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReplacePageNumberByAddPlusOneIfPageParamIsInMiddle() {
        String link = "https://allegro.pl/kategoria/fotografia?bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-3-0717&p=3&param=30";
        String expected = "https://allegro.pl/kategoria/fotografia?bmatch=baseline-product-cl-eyesa2-engag-dict45-ele-1-3-0717&p=4&param=30";

        String actual = PRODUCTS_COLLECTOR.nextPageLink(link);

        assertEquals(expected, actual);
    }
}