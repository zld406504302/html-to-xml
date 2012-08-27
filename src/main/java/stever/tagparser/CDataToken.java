package stever.tagparser;

public class CDataToken extends ParseToken {

    private String data;

    public CDataToken(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("CData: ").append(data);
        return result.toString();
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("<![CData[").append(data).append("]]>");
        return result.toString();
    }
}
