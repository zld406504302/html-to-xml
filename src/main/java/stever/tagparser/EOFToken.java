package stever.tagparser;

public class EOFToken extends ParseToken {

    public String toString() {
        return "EOF";
    }

    public String render() {
        char[] str = { (char) -1 };
        return str.toString();
    }
}
