package stever.tagparser;

public class EmptyElement extends Tag {

    // Example: <BR/>

    public EmptyElement(Tag tag) {
        super(tag.getName(), tag.isCaseSensitive());

        // Ensure provided tag is not an end-tag.
        if (super.isEndTag()) {
            throw new IllegalArgumentException("End-tag cannot be provided to EmptyElement class constructor!");
        }

        // Copy attributes.
        for (Attribute attrib : tag.getAttributes()) {
            attributes.put(attrib.getName(), attrib);
        }
    }

    /**
     * This method returns this dummy element as a string.
     * @return Tag string.
     */
    public String toString() {
        return super.toString(true, null);
    }
}
