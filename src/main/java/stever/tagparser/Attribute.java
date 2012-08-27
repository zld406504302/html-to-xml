package stever.tagparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attribute {

    private static final Logger log = LoggerFactory.getLogger(Attribute.class);

    private String name;
    private String value;

    public Attribute(String name) {
        this.name = name;
        value = null;
    }

    public Attribute(String name, String value) {
        this.name = name;
        if (value != null && !value.toLowerCase().equals("true")) {
            this.value = value;
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null) {
            if (this.value != null) {
                if (log.isWarnEnabled()) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Overwriting previous attribute value. ");
                    msg.append("Attribute name is \"").append(name).append("\". ");
                    msg.append("Old value is \"").append(this.value).append("\". ");
                    msg.append("New value is \"").append(value).append("\".");
                    log.warn(msg.toString());
                }
            }
            this.value = value;
        }
    }
}
