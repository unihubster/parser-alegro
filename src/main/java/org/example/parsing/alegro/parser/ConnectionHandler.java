package org.example.parsing.alegro.parser;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler {
    public static final String USER_AGENT = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
    private static final int TIMEOUT = 450;
    private static final int REPEAT_TIMES = 100;
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "en-us";

    private ConnectionHandler() {
    }

    /**
     * Returns Document object of provided link to web page
     * Contains retry to
     *
     * @param link path to a web page
     * @return Document object of provided link to a web page
     * @throws IOException -
     */
    @SuppressWarnings("java:S2259")
    public static Document getDocument(String link) throws IOException {
        Map<String, String> cookies = new LinkedHashMap<>(); // if status 429 from alegro.pl it helps to avoid long waiting
        Connection.Response response = null;

        for (int repeat = 0; repeat < REPEAT_TIMES; repeat++) {
            response = getResponse(link, cookies);

            int status = response.statusCode();
            LOGGER.log(Level.SEVERE, "HTTP status code is {0} for link {1}", new Object[]{status, link});

            if (status >= 200 && status < 400) {
                break;
            }

            if (status != 429 || repeat == REPEAT_TIMES - 1) {
                throw new HttpStatusException("HTTP error fetching URL", status, link);
            }

            cookies = response.cookies();
            ConnectionHandler.doTimeout(); //  because alegro.pl ddos and bot protection
        }

        return response.parse();
    }

    /**
     * Returns JSoup Connection.Response.<br>
     * Http status errors are ignored
     */
    public static Connection.Response getResponse(String link, Map<String, String> cookies) throws IOException {
        return Jsoup.connect(link)
                    .userAgent(USER_AGENT)
                    .maxBodySize(0)
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .cookies(cookies)
                    .ignoreHttpErrors(true)
                    .execute();
    }

    /**
     * Use it when ddos and bot protection is active
     */
    public static void doTimeout() {
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "", e);
            Thread.currentThread().interrupt();
        }
    }
}
