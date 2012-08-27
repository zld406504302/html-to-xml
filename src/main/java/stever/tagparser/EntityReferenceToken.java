package stever.tagparser;

public class EntityReferenceToken extends ParseToken {

    private String name;

    public EntityReferenceToken(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Entity Reference: ").append(name);
        return result.toString();
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("&").append(name).append(";");
        return result.toString();
    }
}
