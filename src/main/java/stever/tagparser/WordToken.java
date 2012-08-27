package stever.tagparser;

public class WordToken extends ParseToken {

    private String word;

    public WordToken(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Word: ");
        result.append(withQuotes(word));
        return result.toString();
    }

    private String withQuotes(String str) {
        StringBuilder result = new StringBuilder();
        if (str.length() == 1) result.append('\'');
        else result.append('"');
        result.append(str);
        if (str.length() == 1) result.append('\'');
        else result.append('"');
        return result.toString();
    }

    public String render() {
        return word;
    }
}
