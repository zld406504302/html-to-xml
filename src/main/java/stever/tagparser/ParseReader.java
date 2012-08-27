package stever.tagparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

/**
 * This class provides the character input stream to the Parser class.
 * It supports a pushback queue to assist the Parser class deal with unexpected input.
 */
public class ParseReader {

    private static final Logger log = LoggerFactory.getLogger(ParseReader.class);

    /** The value from the character stream which represents the end-of-file. */
    public static final char EOF = (char) -1;

    /** Optional filename or URL to identify the stream. */
    private String filename = null;

    /** Input stream reader. */
    private Reader stream = null;

    /** Pushback queue is used to push characters back onto input stream to be re-parsed. */
    private Stack<Character> pushbackQueue = null;

    /** Checksum on the raw data from the input stream. */
    private char checksum = (char) 0;

    /** The character count is simply used for keeping a note of the size of the document. */
    private int charCount = 0;

    /** The column number is used in reporting errors. */
    private int columnNumber = 0;

    /** The line number is used in reporting errors. */
    private int lineNumber = 1;

    /**
     * Constructor using a content string.
     * @param text Content string.
     */
    public ParseReader(String text) {
        stream = new StringReader(text);
        pushbackQueue = new Stack<Character>();
    }

    /**
     * Constructor for the ParseReader class.
     * @param reader The character input stream.
     */
    public ParseReader(Reader reader) {
        stream = reader;
        pushbackQueue = new Stack<Character>();
    }

    /**
     * Constructor for the ParseReader class.
     * @param reader The character input stream.
     * @param filename Optional filename or URL to identify the stream.
     */
    public ParseReader(Reader reader, String filename) {
        stream = reader;
        this.filename = filename;
        pushbackQueue = new Stack<Character>();
    }

    /**
     * Filename or URL string to identify the string.
     * @return Filename property.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Push character back into the stream.
     * @param c Character to push back into the stream.
     */
    public void pushback(char c) {
        log.info("Pushback Char: '{}'", c);
        pushbackQueue.push(c);
    }

    /**
     * Push whole string back into the stream.
     * @param str String to push back into the stream.
     */
    public void pushback(String str) {
        log.info("Pushback String: \"{}\"", str);
        for (int i = str.length() - 1; i > -1; i--) {
            pushbackQueue.push(str.charAt(i));
        }
    }

    /** @return The current line number. */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return The current column position. */
    public int getColumnNumber() {
        return columnNumber;
    }

    /** @return The character read count. */
    public int getCharCount() {
        return charCount;
    }

    /** @return The current stream checksum. */
    public char getChecksum() {
        return checksum;
    }

    /**
     * This method reads a single character from the raw input stream.
     * @return Next character from input.
     * @throws IOException Thrown by character stream Reader.
     */
    private char rawRead() throws IOException {
        char c = (char) stream.read();
        if (c != EOF) {

            // Count new lines and track column position.
            if (c == '\n') {
                lineNumber += 1;
                columnNumber = 0;
            } else {
                columnNumber++;
            }

            // Count characters read.
            charCount++;

            // Update the checkum.
            updateChecksum(c);
        }
        return c;
    }

    /**
     * This method reads a single character from the input buffer.
     * @return Next character from input.
     * @throws IOException Thrown by character stream Reader.
     */
    public char read() throws IOException {
        char nextChar;
        do {

            // Pop last character on the queue if there are items pushed-back.
            if (pushbackQueue.size() > 0) {
                nextChar = pushbackQueue.pop();
            } else {
                nextChar = rawRead();
            }

        } while (nextChar == '\r'); // Ignore linefeed.

        log.info("Char: {}", Parser.toNameString(nextChar));
        return nextChar;
    }

    /**
     * This method is used to update the checksum for the stream.
     * @param nextChar The next character on the input stream.
     */
    private void updateChecksum(char nextChar) {
        // http://atlas.csd.net/~cgadd/knowbase/CRC0013.HTM
        // xor char with the checksum
        if (nextChar != (char) -1) {
            checksum ^= nextChar;
        }
    }
}
