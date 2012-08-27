package stever.tagparser;

public class TagToken extends ParseToken {

    private Tag tag;

    public TagToken(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tag: ").append(tag);
        return result.toString();
    }

    public String render() {
        return tag.toString();
    }
}
