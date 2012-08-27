package stever.tagparser;

public class ProcessingInstructionToken extends ParseToken {

    private String target;
    private String data;

    public ProcessingInstructionToken(String target, String data) {
        this.target = target;
        this.data = data;
    }

    public String getTarget() {
        return target;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("PI: ").append(target).append(' ').append(data);
        return result.toString();
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("<?").append(target).append(' ').append(data).append("?>");
        return result.toString();
    }
}
