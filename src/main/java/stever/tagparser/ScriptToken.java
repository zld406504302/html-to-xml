package stever.tagparser;

public class ScriptToken extends ParseToken {

    private String script;

    public ScriptToken(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Script: ").append(script);
        return result.toString();
    }

    public String render() {
        return script;
    }
}
