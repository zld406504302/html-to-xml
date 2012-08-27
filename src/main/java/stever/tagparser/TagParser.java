package stever.tagparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Hashtable;

public class TagParser extends Parser {

    private static final Logger log = LoggerFactory.getLogger(TagParser.class);

    /** Table of known entities and their text equivalents. */
    private Hashtable<String,String> entities = null;

    /** Flag for tag and attribute name case-sensitivity. */
    private boolean caseSensitive = false;

    /**
     * Constructor for TagParser.
     * @param stream Character stream reader.
     */
    public TagParser(ParseReader stream) {
        super(stream, State.INITIAL);
        entities = new Hashtable<String,String>();
        entities.put("amp", "&");
        entities.put("nbsp", " ");
        entities.put("quot", "\"");
        log.debug("Constructed TagParser");
    }

    /**
     * Getter for case-sensitivity option property.
     * @return True if case-sensitive option on.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Setter for case-sensitivity option property.
     * @param caseSensitive Option to regulate case sensitivity.
     */
    public void isCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Gets the parser machine state.
     * @return Parser machine state.
     */
    protected State getState() {
        return (State) super.getState();
    }

    /**
     * Returns the next token from the input stream.
     * @return Next token from the input stream.
     * @throws IOException Occurs if failed to read from input.
     * @throws MaxErrorsException Maximum number of errors reached.
     */
    public ParseToken getNextToken() throws IOException, MaxErrorsException {
        if (log.isDebugEnabled()) log.debug("getNextToken()");

        // Buffer containing text in current context.
        StringBuilder buffer = new StringBuilder();
        String name = null;
        
        // Read nextToken character from the input stream.
        char nextChar; // Current character from the input stream.
        while ((nextChar = stream.read()) != ParseReader.EOF) {
            switch (getState()) {
                
                case RECOVER: {
                    if (nextChar == '>') {
                        setState(State.INITIAL);
                    }
                    break;
                }

                case INITIAL: {
                    switch (nextChar) {
                        case '<': {
                            setState(State.OPENTAG);
                            if (buffer.length() == 0) break; // No token yet.
                            else return new WordToken(buffer.toString());
                        }

                        case '&': {
                            setState(State.ENTITY);
                            if (buffer.length() == 0) break; // No token yet.
                            else return new WordToken(buffer.toString());
                        }

                        case ' ':
                        case '\t':
                        case '\r': {
                            stream.pushback(nextChar);
                            setState(State.SPACES);
                            if (buffer.length() == 0) break; // No token yet.
                            else return new WordToken(buffer.toString());
                        }

                        case '\n': {
                            if (buffer.length() == 0) {
                                return new NewlineToken();
                            } else {
                                // Push newline back and return new token.
                                stream.pushback(nextChar);
                                return new WordToken(buffer.toString());
                            }
                        }

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            // Pushback number character, return text in buffer.
                            stream.pushback(nextChar);
                            setState(State.NUMBER);
                            if (buffer.length() == 0) break;
                            else return new WordToken(buffer.toString());
                        }

                        case '\'':
                        case '`':
                        case '!':
                        case '"':
                        case '^':
                        case '*':
                        case '(':
                        case ')':
                        case '-':
                        case '_':
                        case '+':
                        case '=':
                        case '|':
                        case '[':
                        case ']':
                        case '{':
                        case '}':
                        case ':':
                        case ';':
                        case '@':
                        case '~':
                        case '#':
                        case ',':
                        case '.':
                        case '?':
                        case '/':
                        case '\\': {
                            // If there's any characters in the buffer, pushback
                            // the punctuation character and return text buffer.
                            if (buffer.length() > 0) {
                                stream.pushback(nextChar);
                                return new WordToken(buffer.toString());
                            } else {
                                return new PunctuationToken(nextChar);
                            }
                        }

                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case NUMBER: {
                    switch (nextChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            buffer.append(nextChar);
                            break;
                        }

                        default: {
                            stream.pushback(nextChar);
                            setState(State.INITIAL);
                            ParseToken token;
                            try {
                                token = new NumberToken(new Long(buffer.toString()));
                            } catch (NumberFormatException ex) {
                                token = new WordToken(buffer.toString());
                            }
                            return token;
                        }
                    }
                    break;
                }

                case SPACES: {
                    switch (nextChar) {
                        case ' ':
                        case '\t':
                        case '\r': {
                            buffer.append(nextChar);
                            break;
                        }

                        default: {
                            stream.pushback(nextChar);
                            setState(State.INITIAL);
                            return new SpacesToken(buffer.toString());
                        }
                    }
                    break;
                }

                case OPENTAG: {
                    switch (nextChar) {
                        case '!': {
                            setState(State.SGML);
                            break;
                        }

                        case '?': {
                            setState(State.PITARGET);
                            break;
                        }

                        case '/': {
                            buffer.append(nextChar);
                            setState(State.ENDTAG1);
                            break;
                        }

                        default: {
                            if (isNameFirstChar(nextChar)) {
                                buffer.append(nextChar);
                                setState(State.TAGNAME);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case TAGNAME: {
                    switch (nextChar) {
                        case '>': {
                            String tagname = buffer.toString();
                            Tag tag = new Tag(tagname, caseSensitive);
                            setState(tagname.equalsIgnoreCase("script") ? State.SCRIPT1 : State.INITIAL);
                            return new TagToken(tag);
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            String tagname = buffer.toString();
                            Tag tag = getTag(tagname);
                            setState(tagname.equalsIgnoreCase("script") ? State.SCRIPT1 : State.INITIAL);
                            return new TagToken(tag);
                        }

                        case '/': {
                            setState(State.EMPTY_ELEMENT1);
                            break;
                        }

                        default: {
                            if (isNameChar(nextChar)) {
                                buffer.append(nextChar);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case EMPTY_ELEMENT1: {
                    switch (nextChar) {
                        case '>': {
                            String tagname = buffer.toString();
                            Tag tag = new Tag(tagname, caseSensitive);
                            setState(tagname.equalsIgnoreCase("script") ? State.SCRIPT1 : State.INITIAL);
                            return new TagToken(tag);
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            // Ignore
                            break;
                        }

                        default: {
                            // Report unknown transition path as an error.
                            log.error(getEdgeUnknownErrorMessage(nextChar, getState()));
                            setState(State.RECOVER);
                            if (++numErrors >= maxErrors) {
                                throw new MaxErrorsException();
                            }
                        }
                    }
                    break;
                }

                case ENTITY: {
                    switch (nextChar) {
                        case '#': {
                            setState(State.CHAR);
                            break;
                        }

                        default: {
                            if (isNameFirstChar(nextChar)) {
                                stream.pushback(nextChar);
                                setState(State.REF);
                            } else {

                                // Common error to have '&' in hand-written HTML not "&nbsp;" as it should be!
                                // In this case a warning will be logged and the HTML will be corrected.
                                log.debug(getInvalidCharErrorMessage(nextChar, getState()));
                                log.warn("Recovery assumed that '&' is not intended as an entity reference ({})", getCharacterPosition());
                                if (log.isDebugEnabled()) {
                                    log.debug("nextChar='" + nextChar + '\'');
                                    if (stream.getFilename() != null) {
                                        log.debug("filename=\"" + stream.getFilename() + '"');
                                    }
                                }
                                stream.pushback(nextChar);
                                stream.pushback("&amp;");
                                numRecoveries++;
                                setState(State.INITIAL);
                            }
                        }
                    }
                    break;
                }

                case REF: {
                    switch (nextChar) {
                        case ';': {
                            setState(State.INITIAL);
                            return new EntityReferenceToken(buffer.toString());
                        }

                        default: {

                            // Normally only accept valid name characters.
                            if (isNameChar(nextChar)) {
                                buffer.append(nextChar);

                            } else {

                                // A common error in hand-written HTML is to omit ';' at end of an entity reference.
                                // HTML correction depends on whether or not the entity is recognised.
                                // In any case the error will be handled and a warning message will be logged.
                                log.debug(getInvalidCharErrorMessage(nextChar, getState()));

                                // Check known entities to decide if the ';' was omitted
                                String str = buffer.toString();
                                if (isKnownEntity(str)) {

                                    // This is a known entity, so assume that the ';' is missing.
                                    // Pushback last character and resume parsing from initial state.
                                    log.warn("Recovery assumed that ; should have ended this entity reference ({})", getCharacterPosition());
                                    stream.pushback(nextChar);
                                    numRecoveries++;
                                    setState(State.INITIAL);
                                    return new EntityReferenceToken(str);

                                } else {
                                    
                                    // This is not a known entity, so we don't assume that the ';' is missing.
                                    // Push the whole string from buffer back onto stream and resume from initial state.
                                    log.warn("Recovery assumed that text was not intended as an entity reference ({})", getCharacterPosition());
                                    buffer.append(nextChar);
                                    stream.pushback(buffer.toString());
                                    buffer = new StringBuilder();
                                    numRecoveries++;
                                    setState(State.INITIAL);
                                }
                            }
                        }
                    }
                    break;
                }

                case CHAR: {
                    switch (nextChar) {
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '0': {
                            buffer.append(nextChar);
                            setState(State.DECIMAL);
                            break;
                        }

                        case 'X':
                        case 'x': {
                            setState(State.HEX);
                            break;
                        }

                        default: {
                            // Unknown transition path from this state
                            log.error(getEdgeUnknownErrorMessage(nextChar, getState()));
                            setState(State.RECOVER);
                            if (++numErrors >= maxErrors) {
                                throw new MaxErrorsException();
                            }
                        }
                    }
                    break;
                }

                case HEX: {
                    switch (nextChar) {
                        case ';': {
                            setState(State.INITIAL);
                            return new CharacterEntityToken(buffer.toString());
                        }

                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '0':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F': {
                            buffer.append(nextChar);
                            break;
                        }

                        default: {

                            // A common error in hand-written HTML is to omit ';' at end of an entity reference.
                            // Pushback last character and resume parsing from initial state.
                            log.debug(getEdgeUnknownErrorMessage(nextChar, getState()));
                            String str = buffer.toString();
                            log.warn("Recovery assumed that ; should have ended this character entity: {} ({})", str, getCharacterPosition());
                            stream.pushback(nextChar);
                            numRecoveries++;
                            setState(State.INITIAL);
                            return new CharacterEntityToken(str);
                        }
                    }
                    break;
                }

                case DECIMAL: {
                    switch (nextChar) {
                        case ';': {
                            setState(State.INITIAL);
                            return new CharacterEntityToken(new Integer(buffer.toString()));
                        }

                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '0': {
                            buffer.append(nextChar);
                            break;
                        }

                        default: {

                            // A common error in hand-written HTML is to omit ';' at end of an entity reference.
                            // Pushback last character and resume parsing from initial state.
                            log.debug(getEdgeUnknownErrorMessage(nextChar, getState()));
                            log.warn("Recovery assumed that ; should have ended this character entity ({})", getCharacterPosition());
                            stream.pushback(nextChar);
                            numRecoveries++;
                            setState(State.INITIAL);
                            return new CharacterEntityToken(new Integer(buffer.toString()));
                        }
                    }
                    break;
                }

                case SGML: {
                    switch (nextChar) {
                        case '-': {
                            setState(State.COMMENT1);
                            break;
                        }

                        case '[': {
                            setState(State.CDATA1);
                            break;
                        }

                        default: {
                            if (isNameFirstChar(nextChar)) {
                                buffer.append(nextChar);
                                setState(State.DTD1);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                }
                break;

                case COMMENT1: {
                    switch (nextChar) {
                        case '-': {
                            setState(State.COMMENT2);
                            break;
                        }

                        default: {
                            // Unknown transition path from this state
                            log.error(getEdgeUnknownErrorMessage(nextChar, getState()));
                            setState(State.RECOVER);
                            if (++numErrors >= maxErrors) {
                                throw new MaxErrorsException();
                            }
                        }
                    }
                    break;
                }

                case COMMENT2: {
                    switch (nextChar) {
                        case '-': {
                            buffer.append(nextChar);
                            setState(State.COMMENT3);
                            break;
                        }

                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case COMMENT3: {
                    buffer.append(nextChar);
                    switch (nextChar) {
                        case '-': {
                            setState(State.COMMENT4);
                            break;
                        }

                        default: {
                            setState(State.COMMENT2);
                        }
                    }
                    break;
                }

                case COMMENT4: {
                    switch (nextChar) {
                        case '>': {
                            setState(State.INITIAL);
                            String data = buffer.toString().substring(0, buffer.length() - 2);
                            return new CommentToken(data);
                        }

                        default: {
                            buffer.append(nextChar);
                            setState(State.COMMENT2);
                        }
                    }
                    break;
                }

                case ENDTAG1: {
                    switch (nextChar) {
                        case '>': {
                            String tagname = buffer.toString();
                            Tag tag = new Tag(tagname, caseSensitive);
                            setState(tagname.equalsIgnoreCase("script") ? State.SCRIPT1 : State.INITIAL);
                            return new TagToken(tag);
                        }

                        default: {
                            if (isNameChar(nextChar)) {
                                buffer.append(nextChar);
                                setState(State.ENDTAG2);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case ENDTAG2: {
                    switch (nextChar) {
                        case '>': {
                            setState(State.INITIAL);
                            Tag tag = new Tag(buffer.toString(), caseSensitive);
                            return new TagToken(tag);
                        }

                        default: {
                            if (isNameChar(nextChar)) {
                                buffer.append(nextChar);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case CDATA1: {
                    if (isNameChar(nextChar)) {
                        buffer.append(nextChar);
                        setState(State.CDATA2);
                    } else {
                        log.error(getInvalidCharErrorMessage(nextChar, getState()));
                        stream.pushback(nextChar);
                        setState(State.RECOVER);
                        if (++numErrors >= maxErrors) {
                            throw new MaxErrorsException();
                        }
                    }
                    break;
                }

                case CDATA2: {
                    switch (nextChar) {
                        case '[': {
                            if (buffer.toString().toUpperCase().equals("CData")) {
                                buffer = new StringBuilder();
                                setState(State.CDATA3);
                            } else {
                                log.error("CData declaration expected");
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                            break;
                        }

                        default: {
                            if (isNameChar(nextChar)) {
                                buffer.append(nextChar);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case CDATA3: {
                    switch (nextChar) {
                        case ']': {
                            setState(State.CDATA4);
                            break;
                        }

                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case CDATA4: {
                    switch (nextChar) {
                        case ']': {
                            setState(State.CDATA5);
                            break;
                        }

                        default: {
                            setState(State.CDATA3);
                        }
                    }
                    break;
                }

                case CDATA5: {
                    switch (nextChar) {
                        case '>': {
                            setState(State.INITIAL);
                            return new CDataToken(buffer.toString());
                        }

                        default: {
                            setState(State.CDATA3);
                        }
                    }
                    break;
                }

                case DTD1: {
                    switch (nextChar) {
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {

                            // Checking that the DTD name string is recognised.
                            name = buffer.toString();
                            buffer = new StringBuilder();
                            if ((name.toUpperCase().equals("DOCTYPE")) ||
                                (name.toUpperCase().equals("ELEMENT")) ||
                                (name.toUpperCase().equals("ATTLIST")) ||
                                (name.toUpperCase().equals("ENTITY")) ||
                                (name.toUpperCase().equals("NOTATION"))) {

                                setState(State.DTD2);

                            } else {
                                log.error("Unrecognised DTD part \"{}\"", name);
                                stream.pushback(buffer.toString());
                                buffer = new StringBuilder();
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                            break;
                        }

                        default: {
                            buffer.append(nextChar);
                            if (!isNameChar(nextChar)) {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(buffer.toString());
                                buffer = new StringBuilder();
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case DTD2: {
                    switch (nextChar) {
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            // Ignore
                            break;
                        }

                        default: {
                            buffer.append(nextChar);
                            setState(State.DTD3);
                        }
                    }
                    break;
                }

                case DTD3: {
                    switch (nextChar) {
                        case '>': {
                            setState(State.INITIAL);
                            return new DoctypeToken(name, buffer.toString());
                        }

                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case PITARGET: {
                    switch (nextChar) {
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            name = buffer.toString();
                            buffer = new StringBuilder();
                            setState(State.PIDATA);
                        }
                        break;

                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case PIDATA: {
                    switch (nextChar) {
                        case '?': {
                            setState(State.ENDPI);
                            break;
                        }

                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case ENDPI: {
                    switch (nextChar) {
                        case '>': {
                            setState(State.INITIAL);
                            return new ProcessingInstructionToken(name, buffer.toString());
                        }

                        default: {
                            // Unknown transition path from this state.
                            log.error(getEdgeUnknownErrorMessage(nextChar, getState()));
                            stream.pushback(nextChar);
                            setState(State.RECOVER);
                            if (++numErrors >= maxErrors) {
                                throw new MaxErrorsException();
                            }
                        }
                    }
                    break;
                }

                case SCRIPT1: {
                    log.debug("nextChar = {}", nextChar);
                    switch (nextChar) {
                        case '<': {
                            setState(State.SCRIPT2);
                            break;
                        }
                        
                        default: {
                            buffer.append(nextChar);
                        }
                    }
                    break;
                }

                case SCRIPT2: {
                    switch (nextChar) {
                        case '/': {
                            setState(State.SCRIPT3);
                            break;
                        }

                        default: {
                            buffer.append('<');
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT3: {
                    switch (nextChar) {
                        case 's': {
                            setState(State.SCRIPT4);
                            break;
                        }

                        default: {
                            buffer.append("</");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT4: {
                    switch (nextChar) {
                        case 'c': {
                            setState(State.SCRIPT5);
                            break;
                        }

                        default: {
                            buffer.append("</s");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT5: {
                    switch (nextChar) {
                        case 'r': {
                            setState(State.SCRIPT6);
                            break;
                        }

                        default: {
                            buffer.append("</sc");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT6: {
                    switch (nextChar) {
                        case 'i': {
                            setState(State.SCRIPT7);
                            break;
                        }

                        default: {
                            buffer.append("</scr");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT7: {
                    switch (nextChar) {
                        case 'p': {
                            setState(State.SCRIPT8);
                            break;
                        }

                        default: {
                            buffer.append("</scri");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT8: {
                    switch (nextChar) {
                        case 't': {
                            setState(State.SCRIPT9);
                            break;
                        }

                        default: {
                            buffer.append("</scrip");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }

                case SCRIPT9: {
                    switch (nextChar) {
                        case '>': {
                            stream.pushback("</script>");
                            setState(State.INITIAL);
                            return new ScriptToken(buffer.toString());
                        }

                        default: {
                            buffer.append("</script");
                            buffer.append(nextChar);
                            setState(State.SCRIPT1);
                        }
                    }
                    break;
                }
            }
        }

        // Warning if unprocessed content in buffer, or unexpected end-of-file.
        String leftovers = buffer.toString();
        switch (getState()) {
            case INITIAL: {
                if (leftovers.length() == 0) break;
                else return new WordToken(leftovers);
            }

            case NUMBER: {
                if (leftovers.length() == 0) break;
                else return new NumberToken(new Long(leftovers));
            }

            case SPACES: {
                if (leftovers.length() == 0) break;
                else return new SpacesToken(leftovers);
            }

            default: {
                numWarnings++;
                log.warn("Unexpected EOF");
            }
        }

        // EOF
        return new EOFToken();
    }

    /**
     * This method returns a complete Tag instance.
     * Parsing continues from the point that this method was called until the
     * end of the tag is found, or on error.
     *
     * @param name Name of the tag instance.
     * @return Tag instance.
     * @throws IOException        Occurs if failed to read from input.
     * @throws MaxErrorsException Maximum number of errors reached.
     */
    protected Tag getTag(String name) throws IOException, MaxErrorsException {
        if (log.isDebugEnabled()) log.debug("Entering getTag()");

        Tag tag = new Tag(name, caseSensitive);
        StringBuilder attribute = new StringBuilder();
        StringBuilder value = new StringBuilder();

        setState(State.TAG);
        
        char nextChar;
        while ((nextChar = stream.read()) > 0) {
            /*
            if (getState() == State.RECOVER) {
                log.info("getTag() method returning due to error recovery mode");
                return null;
            }
            */

            switch (getState()) {
                case RECOVER: {
                    switch (nextChar) {
                        case '>':
                            return tag;
                    }
                    break;
                }
                case TAG: {
                    switch (nextChar) {
                        case '>':
                            return tag;

                        case '/': {
                            setState(State.EMPTY_ELEMENT2);
                            break;
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            // Ignore
                            break;
                        }

                        default: {
                            attribute.append(nextChar);
                            if (isNameChar(nextChar)) {
                                setState(State.NAME1);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(attribute.toString());
                                attribute = new StringBuilder();
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case EMPTY_ELEMENT2: {
                    switch (nextChar) {
                        case '>': {
                            return new EmptyElement(tag);
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            // Ignore
                            break;
                        }

                        default: {
                            // Unknown transition path from this state
                            log.error(getEdgeUnknownErrorMessage(nextChar, getState()));
                            stream.pushback(nextChar);
                            setState(State.RECOVER);
                            if (++numErrors >= maxErrors) {
                                throw new MaxErrorsException();
                            }
                        }
                    }
                    break;
                }

                case NAME1: {
                    switch (nextChar) {
                        case '>': {
                            tag.addAttribute(attribute.toString());
                            return tag;
                        }

                        case '/': {
                            setState(State.EMPTY_ELEMENT2);
                            break;
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            setState(State.NAME2);
                            break;
                        }

                        case '=': {
                            setState(State.VALUE1);
                            break;
                        }

                        default: {
                            attribute.append(nextChar);
                            if (!isNameChar(nextChar)) {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(attribute.toString());
                                attribute = new StringBuilder();
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case NAME2: {
                    switch (nextChar) {
                        case '>': {
                            tag.addAttribute(attribute.toString());
                            return tag;
                        }

                        case '/': {
                            tag.addAttribute(attribute.toString());
                            setState(State.EMPTY_ELEMENT2);
                            break;
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            // Ignore
                            break;
                        }

                        case '=': {
                            setState(State.VALUE1);
                            break;
                        }

                        default: {
                            tag.addAttribute(attribute.toString());
                            if (isNameChar(nextChar)) {
                                attribute = new StringBuilder();
                                attribute.append(nextChar); // New attribute.
                                setState(State.NAME1);
                                
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(nextChar);
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case VALUE1: {
                    switch (nextChar) {
                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            // Ignore
                            break;
                        }

                        case '"': {
                            setState(State.QUOTED);
                            break;
                        }

                        case '\'': {
                            setState(State.COMMA);
                            break;
                        }

                        default: {
                            value.append(nextChar);
                            if (isNameChar(nextChar)) {
                                setState(State.LABEL);
                            } else {
                                log.error(getInvalidCharErrorMessage(nextChar, getState()));
                                stream.pushback(value.toString());
                                value = new StringBuilder();
                                setState(State.RECOVER);
                                if (++numErrors >= maxErrors) {
                                    throw new MaxErrorsException();
                                }
                            }
                        }
                    }
                    break;
                }

                case VALUE2: {
                    switch (nextChar) {
                        case '>': {
                            return tag;
                        }

                        case '/': {
                            setState(State.EMPTY_ELEMENT2);
                            break;
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            setState(State.TAG);
                            break;
                        }

                        default: {
                            stream.pushback(nextChar);
                            setState(State.TAG);
                        }
                    }
                    break;
                }

                case LABEL: {
                    switch (nextChar) {
                        case '>': {
                            tag.addAttribute(attribute.toString(), value.toString());
                            return tag;
                        }

                        case ' ':
                        case '\t':
                        case '\n':
                        case '\r': {
                            tag.addAttribute(attribute.toString(), value.toString());
                            attribute = new StringBuilder();
                            value = new StringBuilder();
                            setState(State.TAG);
                            break;
                        }

                        default: {
                            value.append(nextChar);
                        }
                    }
                    break;
                }

                case QUOTED: {
                    switch (nextChar) {
                        case '"': {
                            tag.addAttribute(attribute.toString(), value.toString());
                            attribute = new StringBuilder();
                            value = new StringBuilder();
                            setState(State.VALUE2);
                            break;
                        }

                        default: {
                            value.append(nextChar);
                        }
                    }
                    break;
                }

                case COMMA: {
                    switch (nextChar) {
                        case '\'': {
                            tag.addAttribute(attribute.toString(), value.toString());
                            attribute = new StringBuilder();
                            value = new StringBuilder();
                            setState(State.VALUE2);
                            break;
                        }

                        default: {
                            value.append(nextChar);
                        }
                    }
                    break;
                }
            }
        }

        numWarnings++;
        log.warn("Unexpected EOF");
        return null;
    }

    /**
     * Decodes known entity.
     * @param entity Entity name.
     * @return Decoded entity, or null if entity unknown.
     */
    private String decodeEntity(String entity) {
        if (entities.containsKey(entity)) {
            return entities.get(entity);
        } else {
            return null;
        }
    }

    /**
     * This checks if a the entity name is recognised.
     * @param entity Name of entity.
     * @return True if the entity name is recognised.
     */
    private boolean isKnownEntity(String entity) {
        return decodeEntity(entity) != null;
    }

    /**
     * Character allowed as part of attribute or element name.
     * @param c Character to test.
     * @return True if the character is allowed for names.
     */
    static boolean isNameFirstChar(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_' ||
                c == ':'  ;
    }

    /**
     * Character allowed as part of attribute or element name.
     * @param c Character to test.
     * @return True if the character is allowed for names.
     */
    static boolean isNameChar(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               (c >= '0' && c <= '9') ||
                c == '-' ||
                c == '_' ||
                c == '.' ||
                c == ':'  ;
    }
}
