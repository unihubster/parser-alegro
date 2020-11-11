package org.example.parsing.alegro.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to contain and manipulate set of Products
 */
public class ProductsContainer {
    private static final String DEFAULT_PARAM = "-";
    private final Set<Product> products;
    private final Set<String> headers;
    private final Set<String> headersMain;

    public ProductsContainer() {
        products = new LinkedHashSet<>();
        headers = new LinkedHashSet<>();
        headersMain = getMainHeaders();
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    /**
     * Call this method after you added some new Products to the ProductsContainer
     */
    public void collectHeaders() {
        headers.addAll(headersMain);
        headers.addAll(getOtherHeaders());
    }

    /**
     * Supplies Stream of Lists which contain values converted to String of every Product fields
     * in the ProductsContainer according to current Set of headers
     *
     * @return Stream of List&lt;String&gt;
     */
    public Stream<List<String>> getProductFieldsStream() {
        validateHeaders();
        return products.stream()
                       .sequential()
                       .map(this::convertProductFieldsToList);
    }

    private void validateHeaders() {
        if (headers.isEmpty()) {
            collectHeaders();
        }
    }

    /**
     * Converts all fields of product to List&lt;String&gt; ordered according to Set&lt;String&gt; headers
     *
     * @param product Product
     * @return List of converted to String all field of the product
     */
    private List<String> convertProductFieldsToList(Product product) {
        List<String> list = new ArrayList<>(getMainParameters(product)); // add main fields first of all
        headers.stream()
               .skip(headersMain.size()) // skip already added main parameters
               .forEachOrdered(key -> list.add(product.getParameters()
                                                      .getOrDefault(key, DEFAULT_PARAM)));
        return list;
    }

    private List<String> getMainParameters(Product product) {
        List<String> list = new ArrayList<>();

        list.add(product.getName());
        list.add(product.getPrice().toString());
        list.add(product.getFullPrice().toString());
        list.add(product.getSalesPercent() + "");
        list.add(product.getLink());

        return list;
    }

    /**
     * @return Set of default headers for fields which are present in every product
     */
    private Set<String> getMainHeaders() {
        Set<String> mainHeaders = new LinkedHashSet<>();
        mainHeaders.add("Name");
        mainHeaders.add("Price");
        mainHeaders.add("Full Price");
        mainHeaders.add("Sales percent");
        mainHeaders.add("Link");
        return mainHeaders;
    }

    private Set<String> getOtherHeaders() {
        return products.stream()
                       .flatMap(product -> product.getParameters().keySet().stream())
//                       .sorted() //todo should it sort in alphabetic order or new Headers have to be just added to the end?
                       .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
