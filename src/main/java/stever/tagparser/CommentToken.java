package stever.tagparser;

public class CommentToken extends ParseToken {

    private String comment;

    public CommentToken(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Comment: ").append(comment);
        return result.toString();
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("<!--").append(comment).append("-->");
        return result.toString();
    }
}
