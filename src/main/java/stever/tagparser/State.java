package stever.tagparser;

enum State {

    /** Recovery after Tag Error */
    RECOVER {
        public String toDescription() {
            return "error recovery";
        }
    },

    /** Element Content */
    INITIAL {
        public String toDescription() {
            return "text content";
        }
    },

    NUMBER {
        public String toDescription() {
            return "number text content";
        }
    },

    OPENTAG {
        public String toDescription() {
            return "tag markup entry";
        }
    },

    /** Start Tag */
    TAGNAME {
        public String toDescription() {
            return "start-tag name";
        }
    },

    /** End Tag */
    ENDTAG1 {
        public String toDescription() {
            return "end-tag begin";
        }
    },

    ENDTAG2 {
        public String toDescription() {
            return "end-tag name";
        }
    },

    SGML {
        public String toDescription() {
            return "SGML tag begin";
        }
    },

    /** Document Type Declaration */
    DTD1 {
        public String toDescription() {
            return "DTD identifier";
        }
    },
    DTD2 {
        public String toDescription() {
            return "DTD white-space";
        }
    },
    DTD3 {
        public String toDescription() {
            return "DTD unparsed data";
        }
    },

    /** Comment */
    COMMENT1 {
        public String toDescription() {
            return "comment entry-sequence";
        }
    },
    COMMENT2 {
        public String toDescription() {
            return "comment content";
        }
    },
    COMMENT3 {
        public String toDescription() {
            return "comment exit-sequence A";
        }
    },
    COMMENT4 {
        public String toDescription() {
            return "comment exit-sequence B";
        }
    },

    /** Processing Instruction - Target */
    PITARGET {
        public String toDescription() {
            return "PI target identifier";
        }
    },

    /** Processing Instruction - Data */
    PIDATA {
        public String toDescription() {
            return "PI data";
        }
    },

    ENDPI {
        public String toDescription() {
            return "PI end";
        }
    },

    ENTITY {
        public String toDescription() {
            return "entity markup begin";
        }
    },

    /** Entity Reference */
    REF {
        public String toDescription() {
            return "entity reference";
        }
    },

    CHAR {
        public String toDescription() {
            return "character reference begin";
        }
    },

    /** Character Reference (HEX) */
    HEX {
        public String toDescription() {
            return "hexadecimal character";
        }
    },

    /** Character Reference (DEC) */
    DECIMAL {
        public String toDescription() {
            return "decimal character";
        }
    },

    SPACES {
        public String toDescription() {
            return "white-space content";
        }
    },

    TAG {
        public String toDescription() {
            return "tag attribute parse begin";
        }
    },

    /** Empty Element */
    EMPTY_ELEMENT1 {
        public String toDescription() {
            return "empty-element exception 1";
        }
    },
    EMPTY_ELEMENT2 {
        public String toDescription() {
            return "empty-element exception 2";
        }
    },

    /** Element Attribute */
    NAME1 {
        public String toDescription() {
            return "attribute name A";
        }
    },
    NAME2 {
        public String toDescription() {
            return "attribute name B";
        }
    },

    /** Attribute Value */
    VALUE1 {
        public String toDescription() {
            return "attribute value begin";
        }
    },
    VALUE2 {
        public String toDescription() {
            return "attribute value end";
        }
    },

    LABEL {
        public String toDescription() {
            return "literal value";
        }
    },

    QUOTED {
        public String toDescription() {
            return "quoted value";
        }
    },

    COMMA {
        public String toDescription() {
            return "inverted-comma delimited value";
        }
    },

    /** CData Section */
    CDATA1 {
        public String toDescription() {
            return "CData entry-sequence A";
        }
    },
    CDATA2 {
        public String toDescription() {
            return "CData entry-sequence B";
        }
    },
    CDATA3 {
        public String toDescription() {
            return "CData identifier";
        }
    },
    CDATA4 {
        public String toDescription() {
            return "CData exit-sequence A";
        }
    },
    CDATA5 {
        public String toDescription() {
            return "CData exit-sequence B";
        }
    },

    /** New state for collecting content of the SCRIPT element. */
    SCRIPT1 {
        public String toDescription() {
            return "Script element content";
        }
    },
    SCRIPT2 {
        public String toDescription() {
            return "Script escape step 1 ('<')";
        }
    },
    SCRIPT3 {
        public String toDescription() {
            return "Script escape step 2 ('/')";
        }
    },
    SCRIPT4 {
        public String toDescription() {
            return "Script escape step 3 ('s')";
        }
    },
    SCRIPT5 {
        public String toDescription() {
            return "Script escape step 4 ('c')";
        }
    },
    SCRIPT6 {
        public String toDescription() {
            return "Script escape step 5 ('r')";
        }
    },
    SCRIPT7 {
        public String toDescription() {
            return "Script escape step 6 ('i')";
        }
    },
    SCRIPT8 {
        public String toDescription() {
            return "Script escape step 7 ('p')";
        }
    },
    SCRIPT9 {
        public String toDescription() {
            return "Script escape step 8 ('t')";
        }
    },
}
