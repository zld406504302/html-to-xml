package stever.tagparser;

public class SpacesToken extends ParseToken {

    private String spaces;

    public SpacesToken(String spaces) {
        this.spaces = spaces;
    }

    public String getSpaces() {
        return spaces;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Spaces: ");

        if (spaces.length() == 1) result.append('\'');
        else result.append('"');

        for (int i = 0; i < spaces.length(); i++) {
            switch (spaces.charAt(i)) {
                case ' ': {
                    result.append(' ');
                    break;
                }
                case '\t': {
                    result.append("\\t");
                    break;
                }
                case '\n': {
                    result.append("\\n");
                    break;
                }
                case '\r': {
                    result.append("\\r");
                    break;
                }
                default: {
                    result.append('?');
                }
            }
        }

        if (spaces.length() == 1) result.append('\'');
        else result.append('"');

        return result.toString();
    }

    public String render() {
        return spaces;
    }
}
