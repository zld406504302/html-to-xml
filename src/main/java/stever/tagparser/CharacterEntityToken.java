package stever.tagparser;

public class CharacterEntityToken extends ParseToken {

    private char character;

    public CharacterEntityToken(String hex) {
        character = (char) Integer.parseInt(hex, 16);
    }

    public CharacterEntityToken(int value) {
        character = (char) value;
    }

    public CharacterEntityToken(char c) {
        character = c;
    }

    public char getCharacter() {
        return character;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Char Entity: ").append(Parser.toNameString(character));
        return result.toString();
    }

    public String render() {
        StringBuilder result = new StringBuilder();
        result.append("&#").append((int) character).append(";");
        return result.toString();
    }
}
