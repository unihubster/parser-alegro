package org.example.parsing.alegro.parser;

import org.example.parsing.alegro.model.Product;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductInfoCollector {
    public static final int DEFAULT_VALUE_IF_ERROR = -100000;
    private static final Logger LOGGER = Logger.getLogger(ProductInfoCollector.class.getName());
    private static final String PRICE_PREFIX = "cena ";
    private static final String PRICE_POSTFIX = " groszy";
    private static final String PRICE_SEPARATOR = " zł ";
    private static final String PRICE_CURRENCY = " zł";
    private static final String PARAMETERS_SELECT = "div[data-box-name='Parameters'] > ul div._1qltd._f8818_3-1jj"; // div._1qltd._f8818_3-1jj // li._f8818_2jDsV

    public void fillUpProductInfo(Product product) throws IOException {
        LOGGER.log(Level.INFO, "Collecting info for product link {0}", product.getLink());
        parseSalesProductInfo(ConnectionHandler.getDocument(product.getLink()), product);
    }

    public Product fillUpProductInfo(String link) throws IOException {
        LOGGER.log(Level.INFO, "Collecting info for product link {0}", link);
        return parseSalesProductInfo(ConnectionHandler.getDocument(link), link);
    }

    public void parseSalesProductInfo(Document doc, Product product) {
        product.setPrice(parsePrice(doc));
        product.setFullPrice(parseFullPrice(doc));
        int salesPercent = parseSalesPercent(doc);
        if (salesPercent != 0) {
            product.setSalesPercent(salesPercent);
        } else {
            product.setSalesPercent(calculateSalesPercent(product.getPrice(), product.getFullPrice()));
        }
        product.getParameters().putAll(parseParameters(doc));
    }

    public Product parseSalesProductInfo(Document doc, String link) {
        Product product = new Product(parseName(doc), link);

        parseSalesProductInfo(doc, product);

        return product;
    }

    public String parseName(Document doc) {
        return doc.select("h1._9a071_1Ux3M._9a071_3nB--._9a071_1R3g4._9a071_1S3No")
                  .text();
    }

    public BigDecimal parsePrice(Document doc) {
        // "_1svub _lf05o _9a071_2MEB_"
        Element element = doc.selectFirst("._1svub._lf05o._9a071_2MEB_");
        if (element == null) {
            return new BigDecimal(DEFAULT_VALUE_IF_ERROR);
        }
        String priceString = element.attr("aria-label");

        return parsePrice(priceString);
    }

    public BigDecimal parsePrice(String priceString) {
        if (priceString.isEmpty()) {
            return new BigDecimal(DEFAULT_VALUE_IF_ERROR);
        }

        StringBuilder stringBuilder = new StringBuilder(priceString);
        stringBuilder.delete(stringBuilder.indexOf(PRICE_PREFIX), PRICE_PREFIX.length())
                     .delete(stringBuilder.indexOf(PRICE_POSTFIX), stringBuilder.length());

        int priceSeparatorIndex = stringBuilder.indexOf(PRICE_SEPARATOR);
        stringBuilder.replace(priceSeparatorIndex, priceSeparatorIndex + PRICE_SEPARATOR.length(), ".");

        priceString = stringBuilder.toString().replace(" ", "");

        return new BigDecimal(priceString);
    }

    public BigDecimal parseFullPrice(Document doc) {
        // "_9a071_3GWg- _9a071_3Xr_c"
        Element element = doc.selectFirst("._9a071_3GWg-._9a071_3Xr_c");

        if (element == null) {
            return new BigDecimal(DEFAULT_VALUE_IF_ERROR);
        }
        String fullPriceString = element.ownText();

        return parseFullPrice(fullPriceString);
    }

    public BigDecimal parseFullPrice(String fullPriceString) {
        StringBuilder stringBuilder = new StringBuilder(fullPriceString);
        stringBuilder.delete(stringBuilder.indexOf(PRICE_CURRENCY), stringBuilder.length());

        int index = stringBuilder.indexOf(",");
        stringBuilder.replace(index, index + 1, ".");

        fullPriceString = stringBuilder.toString().replace(" ", "");

        return new BigDecimal(fullPriceString);
    }

    public int parseSalesPercent(Document doc) {
        // "_9a071_ttMyU _9a071_SnWoN _9a071_1fw40 _9a071_91d5z _9a071_2IH40 _9a071_1dAl5"
        Element element = doc
                .selectFirst("._9a071_ttMyU._9a071_SnWoN._9a071_1fw40._9a071_91d5z._9a071_2IH40._9a071_1dAl5");
        if (element == null) {
            return DEFAULT_VALUE_IF_ERROR;
        }
        String percentString = element.ownText();

        return parseSalesPercent(percentString);
    }

    public int parseSalesPercent(String percentString) {
        Matcher matcher = Pattern.compile("\\d+").matcher(percentString);
        if (matcher.find()) {
            return Math.abs(Integer.parseInt(matcher.group()));
        }
        return 0;
    }

    public int calculateSalesPercent(BigDecimal price, BigDecimal fullPrice) {
        return fullPrice.subtract(price)
                        .divide(fullPrice, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .intValue();
    }

    public Map<String, String> parseParameters(Document doc) {
        Map<String, String> parameters = new LinkedHashMap<>();

        doc.select(PARAMETERS_SELECT)
           .forEach(el -> parameters.put(
                   el.child(0).ownText().replace(":", ""),
                   el.child(1).text())
           );

        return parameters;
    }
}
