package stever.tagparser;

public abstract class ParseToken {

    protected ParseToken() {
        // Subclass! No direct instantiation.
    }

    /**
     * This method returns a descriptive string for the token.
     * @return Descriptive string used for debug logging.
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("ClassName=\"").append(getClass().getName()).append('"');
        return str.toString();
    }

    /**
     * This method should render to a string equivalent to a parsed source.
     * @return String representation of the token value.
     */
    public abstract String render();
}
