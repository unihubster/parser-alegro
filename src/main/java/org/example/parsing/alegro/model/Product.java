package org.example.parsing.alegro.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Product {
    private final String name;
    private final String link;
    private final Map<String, String> parameters;
    private BigDecimal price;
    private BigDecimal fullPrice; // before sales
    private int salesPercent;

    public Product(String name, String link) {
        this.name = name;
        this.link = link;
        parameters = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public int getSalesPercent() {
        return salesPercent;
    }

    public void setSalesPercent(int salesPercent) {
        this.salesPercent = salesPercent;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        Product product = (Product) o;
        return name.equals(product.name) &&
                link.equals(product.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }

    @Override
    public String toString() {
        return "Product{" +
                "link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", price=" + fullPrice +
                ", salePercent='" + salesPercent + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
