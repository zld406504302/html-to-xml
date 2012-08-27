package stever.tagparser;

public class DoctypeToken extends ParseToken {

    private String name;
    private String data;

    public DoctypeToken(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Doctype: ").append(name).append(' ').append(data);
        return result.toString();
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("<!").append(name).append(' ').append(data).append(">");
        return result.toString();
    }
}
