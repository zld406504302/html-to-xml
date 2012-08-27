package stever.tagparser;

public class DummyElement extends Tag {

    // Example: <BR><!-- inserted missing start-tag --></BR>

    private String comment;

    /**
     * Constructor for the DummyElement class.
     * @param endTag This must be an end-tag (tag name beginning '/').
     */
    public DummyElement(Tag endTag) {
        super(endTag.getName().substring(1), endTag.isCaseSensitive());

        // Ensure provided tag is an end-tag.        
        if (!endTag.isEndTag()) {
            throw new IllegalArgumentException("End-tag must be provided to DummyElement class constructor!");
        }

        // Copy attributes.
        for (Attribute attrib : endTag.getAttributes()) {
            attributes.put(attrib.getName(), attrib);
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * This method returns this dummy element as a string.
     * @return Tag string.
     */
    public String toString() {
        return super.toString(true, comment);
    }
}
