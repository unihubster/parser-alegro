package org.example.parsing.alegro.parser;

import org.example.parsing.alegro.model.Product;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductsCollector {
    private static final Logger LOGGER = Logger.getLogger(ProductsCollector.class.getName());
    private static final String SELECT_ITEMS_SECTION = "div[data-box-name=\"items-v3\"]";
    // collect <a> tag from items divs which have child div with class "mp0t_ji mpof_vs _9c44d_1VS-Y _9c44d_3_DDQ mpof_vs _9c44d_2MDwk"
    private static final String SELECT_PRODUCT_LINK_ELEMENTS =
            "div > article:has(div.mp0t_ji.mpof_vs._9c44d_1VS-Y._9c44d_3_DDQ.mpof_vs._9c44d_2MDwk" +
                    " span.mpof_uk.mqu1_ae._9c44d_18kEF.m9qz_yp._9c44d_2BSa0._9c44d_KrRuv) h2:has(a) a";
    private static final String PAGE_PARAM_REGEXP = "([?|&]p=)(\\d+)";
    private static final String PAGE_PARAM_ADD = "&p=";
    private static final String PAGE_PARAM_NEW = "?p=";

    /**
     * @param link     link to web page with items (first page of a category)
     * @param limit    return products no more than the limit
     * @param lastPage what page have to be checked last after page on the provided link
     * @return Set of links to sales products of the provided category
     * @throws IOException because of http connection
     */
    @SuppressWarnings("unchecked")
    public Set<String> getSalesProductsLinks(String link, int limit, int lastPage) throws IOException {
        Set<String> productsLinks = new LinkedHashSet<>();

        int page = 1;
        do {
            LOGGER.log(Level.INFO, "Searching products on link... {0}", link);
            Document doc = ConnectionHandler.getDocument(link);
            productsLinks.addAll(getSalesProductsLinksOnPage(doc));
            //todo set Level.FINE
            LOGGER.log(Level.INFO, "productsLinks.size={0} on page={1}", new Object[]{productsLinks.size(), page});
            link = nextPageLink(link);
            page++;
        } while (productsLinks.size() < limit + 1 || page < lastPage + 1);

        return (Set<String>) cutTailAfterLimit(productsLinks, limit);
    }

    /**
     * @param link     link to web page with items (first page of a category)
     * @param limit    return products no more than the limit
     * @param lastPage what page have to be checked last after page on the provided link
     * @return Set of sales products of the provided category
     * @throws IOException because of http connection
     */
    @SuppressWarnings("unchecked")
    public Set<Product> getSalesProducts(String link, int limit, int lastPage) throws IOException {
        Set<Product> productsLinks = new LinkedHashSet<>();

        int page = 1;
        do {
            LOGGER.log(Level.INFO, "Searching products on link... {0}", link);
            Document doc = ConnectionHandler.getDocument(link);
            productsLinks.addAll(getSalesProductsOnPage(doc));
            //todo set Level.FINE
            LOGGER.log(Level.INFO, "productsLinks.size()={0} on page={1}", new Object[]{productsLinks.size(), page});
            link = nextPageLink(link);
            page++;
        } while (productsLinks.size() < limit + 1 && page < lastPage + 1);

        return (Set<Product>) cutTailAfterLimit(productsLinks, limit);
    }

    /**
     * Finds Set of links to sales products pages on the provided web page
     *
     * @return LinkedHashSet ordered by items order on the web page
     */
    public Set<String> getSalesProductsLinksOnPage(Document doc) {
        return Stream.of(doc)
                     .map(this::extractItemsSection)
                     .map(this::extractProductLinkElements)
                     .flatMap(Elements::stream)
                     .map(this::extractLink)
                     .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Finds Set of sales products on the provided web page
     * Product pojos are filled with link to its page and product name
     *
     * @return LinkedHashSet ordered by items order on the web page
     */
    public Set<Product> getSalesProductsOnPage(Document doc) {
        return Stream.of(doc)
                     .map(this::extractItemsSection)
                     .map(this::extractProductLinkElements)
                     .flatMap(Elements::stream)
                     .map(this::extractProduct)
                     .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Element extractItemsSection(Document doc) {
        return doc.selectFirst(SELECT_ITEMS_SECTION);
    }

    private Elements extractProductLinkElements(Element section) {
        return section.select(SELECT_PRODUCT_LINK_ELEMENTS);
    }

    /**
     * @param element Product element like
     *                &lt;a href="https://allegro.pl/oferta/spodnie-dresowe-meskie-joggery-dresy-tomy-jakosc-9551156401"
     *                &gt;SPODNIE DRESOWE MĘSKIE JOGGERY DRESY TOMY JAKOŚĆ&lt;/a&gt;
     * @return Product pojo with link and product name
     */
    private Product extractProduct(Element element) {
        return new Product(element.ownText(), element.attr("href"));
    }

    /**
     * @param element Product element like
     *                &lt;a href="https://allegro.pl/oferta/spodnie-dresowe-meskie-joggery-dresy-tomy-jakosc-9551156401"
     *                &gt;SPODNIE DRESOWE MĘSKIE JOGGERY DRESY TOMY JAKOŚĆ&lt;/a&gt;
     * @return Product pojo with link and product name
     */
    private String extractLink(Element element) {
        return element.attr("href");
    }

    protected String nextPageLink(String link) {
        if (!link.contains("?")) {
            return link.concat(PAGE_PARAM_NEW).concat("2");
        }

        if (link.contains(PAGE_PARAM_NEW) || link.contains(PAGE_PARAM_ADD)) {
            link = replacePageNumber(link);
        } else {
            link = link.concat(PAGE_PARAM_ADD).concat("2");
        }

        return link;
    }

    private String replacePageNumber(String link) {
        Matcher matcher = Pattern.compile(PAGE_PARAM_REGEXP).matcher(link);

        if (matcher.find()) {
            int pageNum = Integer.parseInt(matcher.group(2)) + 1;
            link = link.replaceFirst(PAGE_PARAM_REGEXP, matcher.group(1) + pageNum);
        }

        return link;
    }

    private Set<?> cutTailAfterLimit(Set<?> set, int limit) {
        if (set.size() > limit) {
            set = set.stream()
                     .limit(limit)
                     .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return set;
    }

    private void cutTailAfterLimit(List<?> list, int limit) {
        if (list.size() > limit) {
            list.subList(limit, list.size()).clear();
        }
    }
}
