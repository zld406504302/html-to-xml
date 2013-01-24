package stever.tagparser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class XmlExtractorTest {

    private static final Logger log = LoggerFactory.getLogger(XmlExtractorTest.class);

    @Test
    public void parseString() throws IOException, MaxErrorsException {
        String html = "<html><body>Hello world</body></html>";
        String xml = XmlExtractor.toXml(html);
        log.info("XML:\n{}", xml);
    }
}
