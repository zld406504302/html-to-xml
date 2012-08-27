package stever.tagparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

/** This class is used to store instances of tags found within a parsed document. */
public class Tag {

    private static final Logger log = LoggerFactory.getLogger(Tag.class);

    private String name;
    private boolean caseSensitive;

    protected Hashtable<String,Attribute> attributes = new Hashtable<String,Attribute>();

    /**
     * This is a constructor for the class: Tag
     * @param name Name of this tag. This is an end-tag if the name begins '/'.
     * @param caseSensitive True if tag and attribute names are case-sensitive.
     */
    public Tag(String name, boolean caseSensitive) {
        if (!caseSensitive) name = name.toLowerCase();
        this.caseSensitive = caseSensitive;
        this.name = name;
    }

    /**
     * Validate the attribute name.
     * @param name Attribute or tag name to validate.
     * @return True if the name is valid.
     */
    private static boolean isValidName(String name) {

        /*
        Valid tag and attribute names.
        http://www.w3.org/TR/REC-xml/#NT-NameStartChar

        NameStartChar  ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
        NameChar       ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
        */

        // First char can be a letter or '_' or ':'.
        // Following chars can include numbers, '-', '.' and some others that might be unicode.
        // See: http://www.w3.org/TR/2000/REC-xml-20001006#NT-Name

        // Check the first character here.
        if (!TagParser.isNameFirstChar(name.charAt(0))) {
            log.error("Attribute ignored due to invalid name char: " + name.charAt(0));
            return false;
        }

        // Check the other characters in the name.
        for (int i = 1; i < name.length(); i++) {
            if (!TagParser.isNameFirstChar(name.charAt(i))) {
                log.error("Attribute ignored due to invalid name char: " + name.charAt(i));
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns the name of the tag.
     * @return Name of the tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for case-sensitivity option property.
     * @return True if case-sensitive option on.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * This method checks if this an end tag.
     * @return True if this is an end tag.
     */
    public boolean isEndTag() {
        return name.charAt(0) == '/';
    }

    /**
     * This method adds an attribute to this tag.
     * If the attribute already exists then set this value for the attribute.
     * @param name Name of the attribute.
     * @param value Optional value associated the attribute.
     */
    public void addAttribute(String name, String value) {
        if (!isValidName(name)) return;
        if (!caseSensitive) name = name.toLowerCase();

        // Make sure the attribute value doesn't contain invalid characters.
        if (value != null) {
            value = value.replaceAll("&amp;", "&");
            value = value.replaceAll("&quot;", "\"");
            value = value.replaceAll("&lt;", "<");
            value = value.replaceAll("&gt;", ">");
            value = value.replaceAll("&", "&amp;");
            value = value.replaceAll("\"", "&quot;");
            value = value.replaceAll("<", "&lt;");
            value = value.replaceAll(">", "&gt;");
        }

        Attribute attrib = attributes.get(name);
        if (attrib == null) {
            attrib = new Attribute(name, value);
            attributes.put(name, attrib);
        } else {
            attrib.setValue(value);
        }
    }

    /**
     * This method adds a attribute to this tag.
     * @param name Name of the attribute.
     */
    public void addAttribute(String name) {
        addAttribute(name, null);
    }

    /**
     * This method returns the value of a named attribute.
     * @param name The attribute name.
     * @return The attribute class instance.
     */
    public Attribute getAttribute(String name) {
        if (!caseSensitive) name = name.toLowerCase();
        return attributes.get(name);
    }

    /**
     * This method returns the value of a named attribute.
     * @param name The attribute name.
     * @return String value of the attribute.
     */
    public String getAttributeValue(String name) {
        if (!caseSensitive) name = name.toLowerCase();
        Attribute attrib = attributes.get(name);
        if (attrib == null) return null;
        else return attrib.getValue();
    }

    /**
     * This method returns a list of the attributes.
     * @return List of attributes.
     */
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> result = new ArrayList<Attribute>();
        for (Attribute attrib : attributes.values()) result.add(attrib);
        return result;
    }

    /**
     * This method writes the tag to a string, also supporting the toString()
     * methods of the extending classes EmptyElement and DummyElement.
     * @param isEmptyElement True if this start-tag is to be closed.
     * @param comment An optional comment to be enclosed in the element.
     * @return String representation.
     */
    protected String toString(boolean isEmptyElement, String comment) {

        // Check arguments.
        if (!isEmptyElement && comment != null) {
            throw new IllegalArgumentException("Non-element can't have comment.");
        }

        // Result string builder.
        StringBuilder result = new StringBuilder();

        // Open start-tag.
        result.append('<');

        // Tag or element name.
        result.append(name);

        // Attributes
        if (attributes != null) {
            for (Enumeration e = attributes.keys(); e.hasMoreElements();) {
                String attributeName = (String) e.nextElement();
                Attribute attrib = getAttribute(attributeName);
                if (attrib != null) {
                    result.append(' ');

                    // Attribute name.
                    result.append(attributeName);

                    // Attribute value.
                    if (attrib.getValue() == null) {
                        result.append("=\"\"");
                    } else {
                        result.append('=');
                        result.append('"');
                        result.append(escapeAttribute(attrib.getValue()));
                        result.append('"');
                    }
                }
            }
        }

        // Close start-tag or element.
        if (!isEmptyElement) {
            result.append('>');
        } else {
            if (comment == null) {
                //TODO: Check if the empty element syntax is allowed.
                result.append("/>");
            } else {
                result.append("><!--").append(comment).append("-->");
                result.append("</").append(name).append('>');
            }
        }

        // Return result as string.
        return result.toString();
    }

    /**
     * This method is used to ensure that '&' are not misused in attribute values.
     * @param value Attribute value to process.
     * @return Attribute value with escaped '&' as may be required.
     */
    private String escapeAttribute(String value) {
        if (value != null) {
            value = value.replaceAll("&", "&amp;");
            value = value.replaceAll("&amp;amp;", "&amp;");
        } else {
            value = "";
        }
        return value;
    }

    /**
     * This method returns this tag as a string.
     * @return Tag string.
     */
    public String toString() {
        return toString(false, null);
    }
}
