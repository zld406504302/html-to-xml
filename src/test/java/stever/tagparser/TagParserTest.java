package stever.tagparser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TagParserTest {

    private static final Logger log = LoggerFactory.getLogger(TagParserTest.class);

    @Test
    public void parseString() throws IOException, MaxErrorsException {
        ParseReader reader = new ParseReader("<html><head><title>Test</title></head></html>");
        TagParser parser = new TagParser(reader);
        ParseToken token = parser.getNextToken();
        while (!(token instanceof EOFToken)) {
            log.debug("Token: {}", token);
            token = parser.getNextToken();
        }
    }
}
