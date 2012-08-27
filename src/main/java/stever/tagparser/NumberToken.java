package stever.tagparser;

public class NumberToken extends ParseToken {

    private long number;

    public NumberToken(long number) {
        this.number = number;
    }

    public long getNumber() {
        return number;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Number: ").append(number);
        return result.toString();
    }

    public String render() {
        return new Long(number).toString();
    }
}
