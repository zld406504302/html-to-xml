package stever.tagparser;

public class NewlineToken extends ParseToken {

    public String toString() {
        return "Newline";
    }

    public String render() {
        return "\n";
    }
}
