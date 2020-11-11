package org.example.parsing.alegro;

import org.example.parsing.alegro.csv.CSVUtils;
import org.example.parsing.alegro.model.Product;
import org.example.parsing.alegro.model.ProductsContainer;
import org.example.parsing.alegro.parser.ProductInfoCollector;
import org.example.parsing.alegro.parser.ProductsCollector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalesProductsFinder {
    private static final Logger LOGGER = Logger.getLogger(SalesProductsFinder.class.getName());
    private static final int DEFAULT_LIMIT = 100;
    private static final int DEFAULT_LAST_PAGE = 100;
    private final ProductsCollector productsCollector;
    private final ProductInfoCollector productInfoCollector;

    public SalesProductsFinder() {
        productsCollector = new ProductsCollector();
        productInfoCollector = new ProductInfoCollector();
    }

    public SalesProductsFinder(ProductsCollector productsCollector, ProductInfoCollector productInfoCollector) {
        this.productsCollector = productsCollector;
        this.productInfoCollector = productInfoCollector;
    }

    public void getSalesProductsToCSV(Map<String, Path> tasks) throws IOException {
        for (Map.Entry<String, Path> entry : tasks.entrySet()) {
            ProductsContainer productsContainer = findSalesProducts(entry.getKey());
            writeProductsContainerToCSV(productsContainer, entry.getValue());
        }
    }

    public ProductsContainer findSalesProducts(String link) throws IOException {
        Set<Product> products = productsCollector.getSalesProducts(link, DEFAULT_LIMIT, DEFAULT_LAST_PAGE);

        for (Product product : products) {
            productInfoCollector.fillUpProductInfo(product);
        }

        ProductsContainer productsContainer = new ProductsContainer();
        productsContainer.getProducts().addAll(products);
        productsContainer.collectHeaders();

        return productsContainer;
    }

    public void writeProductsContainerToCSV(ProductsContainer productsContainer, Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            CSVUtils.writeLine(writer, new ArrayList<>(productsContainer.getHeaders()));
            productsContainer.getProductFieldsStream()
                             .forEachOrdered(list -> {
                                 try {
                                     CSVUtils.writeLine(writer, list);
                                 } catch (IOException e) {
                                     LOGGER.log(Level.SEVERE, e, e::getMessage);
                                 }
                             });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, e::getMessage);
        }
    }
}
