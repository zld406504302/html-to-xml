package stever.tagparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public abstract class Parser {

    private static final Logger log = LoggerFactory.getLogger(Parser.class);

    private Enum state;

    protected ParseReader stream; // Character input stream.
    protected int maxErrors;
    protected int numErrors;
    protected int numWarnings;
    protected int numRecoveries;

    /**
     * This is the constructor for the abstract parser class.
     * @param stream Character input stream.
     * @param initialState Initial state of the parser.
     */
    protected Parser(ParseReader stream, Enum initialState) {
        state = initialState;
        init(stream);
    }

    /**
     * This method (re)initialises the parser.
     * @param stream Character input stream.
     */
    private void init(ParseReader stream) {
        this.stream = stream;
        maxErrors = 1000;
        numErrors = 0;
        numWarnings = 0;
        numRecoveries = 0;
    }

    /**
     * Abstract method to provide the next token parsed from the input stream.
     * @return Next token from the input stream.
     * @throws IOException Occurs if failed to read from input.
     * @throws MaxErrorsException Maximum number of errors reached.
     */
    public abstract ParseToken getNextToken()
            throws IOException, MaxErrorsException;

    /**
     * Sets the parser machine state.
     * @param state Parser machine state.
     */
    protected void setState(Enum state) {
        if (log.isInfoEnabled()) {
            if (this.state != state) {
                if (this.state == null) {
                    log.info("Initial state is {}.", state);
                } else {
                    log.info("Changing state {} to {} state.", this.state, state);
                }
            } else if (log.isWarnEnabled()) {
                log.warn("Changing to same state! ({})", state);
            }
        }
        this.state = state;
    }

    /**
     * Gets the parser machine state.
     * @return Parser machine state.
     */
    protected Enum getState() {
        return state;
    }

    /**
     * Gets the number of characters read.
     * @return The count of characters read on the stream.
     */
    public int getCharCount() {
        return stream.getCharCount();
    }

    /**
     * Gets the checksum for the characters read.
     * @return The current stream checksum.
     */
    public char getChecksum() {
        return stream.getChecksum();
    }

    /**
     * This produces a warning message when a character is accepted by default.
     * @param c The character accepted by default.
     * @param state The parser state.
     * @return The warning message.
     */
    protected String getCharDefaultAcceptWarningMessage(char c, Enum state) {
        return (new StringBuilder())
                .append("Character ").append(toNameString(c))
                .append(" defaulted from the ")
                .append(state.toString())
                .append(" state (")
                .append(getCharacterPosition())
                .append(')')
                .toString();
    }

    /**
     * Produces the error message for invalid name character.
     * @param c The invalid character.
     * @param state The parser state.
     * @return The error message.
     */
    protected String getInvalidCharErrorMessage(char c, Enum state) {
        return (new StringBuilder())
                .append("Character ")
                .append(toNameString(c))
                .append(" cannot be accepted from the ")
                .append(state.toString())
                .append(" state (")
                .append(getCharacterPosition())
                .append(')')
                .toString();
    }

    /**
     * This method reports that there does not exist an edge from the current
     * state which matches the next character from input.
     * @param edge  Next input character.
     * @param state Current parse state.
     * @return Error message for when invalid character found.
     */
    protected String getEdgeUnknownErrorMessage(char edge, Enum state) {
        return (new StringBuilder())
                .append("No edge labelled ")
                .append(toNameString(edge))
                .append(" from the ")
                .append(state.toString())
                .append(" state (")
                .append(getCharacterPosition())
                .append(')')
                .toString();
    }

    /**
     * Produces the line number and column number to be added to error and warning messages.
     * @return Character position prefix.
     */
    public String getCharacterPosition() {
        return (new StringBuilder())
            .append(stream.getLineNumber())
            .append(':').append(stream.getColumnNumber())
            .toString();
    }

    /**
     * This method is used to report the final status of the parser.
     * @return The completion report as a string.
     */
    public String getCompletionReport() {
        StringBuilder report = new StringBuilder();
        report.append("Parsed ");
        report.append(stream.getLineNumber());
        report.append(" line");
        if (stream.getLineNumber() > 1) report.append('s');
        report.append(" containing ");
        report.append(stream.getCharCount());
        report.append(" characters.");
        if ((numErrors + numRecoveries) > 0 || numWarnings > 0) {
            report.append(" Reported ");
            if ((numErrors + numRecoveries) > 0) {
                report.append(numErrors + numRecoveries);
                report.append(" error");
                if (numErrors + numRecoveries != 1) {
                    report.append('s');
                }
                if (numRecoveries > 0) {
                    report.append(" (recovered ");
                    report.append(numRecoveries).append(')');
                }
                if (numWarnings == 0) {
                    report.append('.');
                } else {
                    report.append(" and ");
                }
            }
            if (numWarnings > 0) {
                report.append(numWarnings);
                report.append(" warnings.");
            }
        }
        return report.toString();
    }

    /**
     * This method returns the string representation of a character.
     * @param c Character value to represent.
     * @return Label used to represent all characters in text logging.
     */
    public static String toNameString(char c) {
        switch (c) {
            case 0x00: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <NUL>";
            case 0x01: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <SOH>";
            case 0x02: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <STX>";
            case 0x03: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <ETX>";
            case 0x04: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <EOT>";
            case 0x05: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <ENQ>";
            case 0x06: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <ACK>";
            case 0x07: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <BEL>";
            case 0x08: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <BS>";
            case 0x09: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <HT>";
            case 0x0A: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <LF>";
            case 0x0B: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <VT>";
            case 0x0C: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <FF>";
            case 0x0D: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <CR>";
            case 0x0E: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <SO>";
            case 0x0F: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <SI>";
            case 0x10: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <DLE>";
            case 0x11: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <DC1>";
            case 0x12: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <DC2>";
            case 0x13: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <DC3>";
            case 0x14: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <DC4>";
            case 0x15: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <NAK>";
            case 0x16: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <SYN>";
            case 0x17: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <ETB>";
            case 0x18: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <CAN>";
            case 0x19: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <EM>";
            case 0x1A: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <SUB>";
            case 0x1B: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <ESC>";
            case 0x1C: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <FS>";
            case 0x1D: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <GS>";
            case 0x1E: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <RS>";
            case 0x1F: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <US>";
            case 0x20: return "0x" + Integer.toHexString((int) c).toUpperCase() + " <SP>";
            case 0x21:
            case 0x22:
            case 0x23:
            case 0x24:
            case 0x25:
            case 0x26:
            case 0x27:
            case 0x28:
            case 0x29:
            case 0x2A:
            case 0x2B:
            case 0x2C:
            case 0x2D:
            case 0x2E:
            case 0x2F:
            case 0x30:
            case 0x31:
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39:
            case 0x3A:
            case 0x3B:
            case 0x3C:
            case 0x3D:
            case 0x3E:
            case 0x3F:
            case 0x40:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x46:
            case 0x47:
            case 0x48:
            case 0x49:
            case 0x4A:
            case 0x4B:
            case 0x4C:
            case 0x4D:
            case 0x4E:
            case 0x4F:
            case 0x50:
            case 0x51:
            case 0x52:
            case 0x53:
            case 0x54:
            case 0x55:
            case 0x56:
            case 0x57:
            case 0x58:
            case 0x59:
            case 0x5A:
            case 0x5B:
            case 0x5C:
            case 0x5D:
            case 0x5E:
            case 0x5F:
            case 0x60:
            case 0x61:
            case 0x62:
            case 0x63:
            case 0x64:
            case 0x65:
            case 0x66:
            case 0x67:
            case 0x68:
            case 0x69:
            case 0x6A:
            case 0x6B:
            case 0x6C:
            case 0x6D:
            case 0x6E:
            case 0x6F:
            case 0x70:
            case 0x71:
            case 0x72:
            case 0x73:
            case 0x74:
            case 0x75:
            case 0x76:
            case 0x77:
            case 0x78:
            case 0x79:
            case 0x7A:
            case 0x7B:
            case 0x7C:
            case 0x7D:
            case 0x7E: {
                StringBuilder result = new StringBuilder();
                result.append(c);
                return result.toString();
            }
            case 0x7F:
                return "0x" + Integer.toHexString((int) c).toUpperCase() + " <DEL>";
            default: {
                StringBuilder result = new StringBuilder();
                result.append("0x");
                result.append(Integer.toHexString((int) c).toUpperCase());
                return result.toString();
            }
        }
    }
}
