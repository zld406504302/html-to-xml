package stever.tagparser;

public class PunctuationToken extends ParseToken {

    private char character;

    public PunctuationToken(char c) {
        character = c;
    }

    public char getCharacter() {
        return character;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Punctuation: ").append(character);
        return result.toString();
    }

    public String render() {
        return new Character(character).toString();
    }
}
