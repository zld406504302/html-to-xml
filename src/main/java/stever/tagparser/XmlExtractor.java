package stever.tagparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

public class XmlExtractor {

    private static final Logger log = LoggerFactory.getLogger(XmlExtractor.class);

    /** Mapping entity names to decimal values, to be used in converting entities not supported in XML. */
    private static final Hashtable<String, Integer> entityMappings;
    static {
        entityMappings = new Hashtable<String, Integer>();
        mapHtmlEntities();
    }

    /** This method maps all the known HTML entities with decimal code, for conversion purposes. */
    private static void mapHtmlEntities() {

        // Latin-1 Entities
        // http://htmlhelp.com/reference/html40/entities/latin1.html
        mapHtmlEntity("nbsp", 160);     // no-break space = non-breaking space
        mapHtmlEntity("iexcl", 161);    // inverted exclamation mark
        mapHtmlEntity("cent", 162);     // cent sign
        mapHtmlEntity("pound", 163);    // pound sign
        mapHtmlEntity("curren", 164);   // currency sign
        mapHtmlEntity("yen", 165);      // yen sign = yuan sign
        mapHtmlEntity("brvbar", 166);   // broken bar = broken vertical bar
        mapHtmlEntity("sect", 167);     // section sign
        mapHtmlEntity("uml", 168);      // diaeresis = spacing diaeresis
        mapHtmlEntity("copy", 169);     // copyright sign
        mapHtmlEntity("ordf", 170);     // feminine ordinal indicator
        mapHtmlEntity("laquo", 171);    // left-pointing double angle quotation mark = left pointing guillemet
        mapHtmlEntity("not", 172);      // not sign
        mapHtmlEntity("shy", 173);      // soft hyphen = discretionary hyphen
        mapHtmlEntity("reg", 174);      // registered sign = registered trade mark sign
        mapHtmlEntity("macr", 175);     // macron = spacing macron = overline = APL overbar
        mapHtmlEntity("deg", 176);      // degree sign
        mapHtmlEntity("plusmn", 177);   // plus-minus sign = plus-or-minus sign
        mapHtmlEntity("sup2", 178);     // superscript two = superscript digit two = squared
        mapHtmlEntity("sup3", 179);     // superscript three = superscript digit three = cubed
        mapHtmlEntity("acute", 180);    // acute accent = spacing acute
        mapHtmlEntity("micro", 181);    // micro sign
        mapHtmlEntity("para", 182);     // pilcrow sign = paragraph sign
        mapHtmlEntity("middot", 183);   // middle dot = Georgian comma = Greek middle dot
        mapHtmlEntity("cedil", 184);    // cedilla = spacing cedilla
        mapHtmlEntity("sup1", 185);     // superscript one = superscript digit one
        mapHtmlEntity("ordm", 186);     // masculine ordinal indicator
        mapHtmlEntity("raquo", 187);    // right-pointing double angle quotation mark = right pointing guillemet
        mapHtmlEntity("frac14", 188);   // vulgar fraction one quarter = fraction one quarter
        mapHtmlEntity("frac12", 189);   // vulgar fraction one half = fraction one half
        mapHtmlEntity("frac34", 190);   // vulgar fraction three quarters = fraction three quarters
        mapHtmlEntity("iquest", 191);   // inverted question mark = turned question mark
        mapHtmlEntity("Agrave", 192);   // Latin capital letter A with grave = Latin capital letter A grave
        mapHtmlEntity("Aacute", 193);   // Latin capital letter A with acute
        mapHtmlEntity("Acirc", 194);    // Latin capital letter A with circumflex
        mapHtmlEntity("Atilde", 195);   // Latin capital letter A with tilde
        mapHtmlEntity("Auml", 196);     // Latin capital letter A with diaeresis
        mapHtmlEntity("Aring", 197);    // Latin capital letter A with ring above = Latin capital letter A ring
        mapHtmlEntity("AElig", 198);    // Latin capital letter AE = Latin capital ligature AE
        mapHtmlEntity("Ccedil", 199);   // Latin capital letter C with cedilla
        mapHtmlEntity("Egrave", 200);   // Latin capital letter E with grave
        mapHtmlEntity("Eacute", 201);   // Latin capital letter E with acute
        mapHtmlEntity("Ecirc", 202);    // Latin capital letter E with circumflex
        mapHtmlEntity("Euml", 203);     // Latin capital letter E with diaeresis
        mapHtmlEntity("Igrave", 204);   // Latin capital letter I with grave
        mapHtmlEntity("Iacute", 205);   // Latin capital letter I with acute
        mapHtmlEntity("Icirc", 206);    // Latin capital letter I with circumflex
        mapHtmlEntity("Iuml", 207);     // Latin capital letter I with diaeresis
        mapHtmlEntity("ETH", 208);      // Latin capital letter ETH
        mapHtmlEntity("Ntilde", 209);   // Latin capital letter N with tilde
        mapHtmlEntity("Ograve", 210);   // Latin capital letter O with grave
        mapHtmlEntity("Oacute", 211);   // Latin capital letter O with acute
        mapHtmlEntity("Ocirc", 212);    // Latin capital letter O with circumflex
        mapHtmlEntity("Otilde", 213);   // Latin capital letter O with tilde
        mapHtmlEntity("Ouml", 214);     // Latin capital letter O with diaeresis
        mapHtmlEntity("times", 215);    // multiplication sign
        mapHtmlEntity("Oslash", 216);   // Latin capital letter O with stroke = Latin capital letter O slash
        mapHtmlEntity("Ugrave", 217);   // Latin capital letter U with grave
        mapHtmlEntity("Uacute", 218);   // Latin capital letter U with acute
        mapHtmlEntity("Ucirc", 219);    // Latin capital letter U with circumflex
        mapHtmlEntity("Uuml", 220);     // Latin capital letter U with diaeresis
        mapHtmlEntity("Yacute", 221);   // Latin capital letter Y with acute
        mapHtmlEntity("THORN", 222);    // Latin capital letter THORN
        mapHtmlEntity("szlig", 223);    // Latin small letter sharp s = ess-zed
        mapHtmlEntity("agrave", 224);   // Latin small letter a with grave = Latin small letter a grave
        mapHtmlEntity("aacute", 225);   // Latin small letter a with acute
        mapHtmlEntity("acirc", 226);    // Latin small letter a with circumflex
        mapHtmlEntity("atilde", 227);   // Latin small letter a with tilde
        mapHtmlEntity("auml", 228);     // Latin small letter a with diaeresis
        mapHtmlEntity("aring", 229);    // Latin small letter a with ring above = Latin small letter a ring
        mapHtmlEntity("aelig", 230);    // Latin small letter ae = Latin small ligature ae
        mapHtmlEntity("ccedil", 231);   // Latin small letter c with cedilla
        mapHtmlEntity("egrave", 232);   // Latin small letter e with grave
        mapHtmlEntity("eacute", 233);   // Latin small letter e with acute
        mapHtmlEntity("ecirc", 234);    // Latin small letter e with circumflex
        mapHtmlEntity("euml", 235);     // Latin small letter e with diaeresis
        mapHtmlEntity("igrave", 236);   // Latin small letter i with grave
        mapHtmlEntity("iacute", 237);   // Latin small letter i with acute
        mapHtmlEntity("icirc", 238);    // Latin small letter i with circumflex
        mapHtmlEntity("iuml", 239);     // Latin small letter i with diaeresis
        mapHtmlEntity("eth", 240);      // Latin small letter eth
        mapHtmlEntity("ntilde", 241);   // Latin small letter n with tilde
        mapHtmlEntity("ograve", 242);   // Latin small letter o with grave
        mapHtmlEntity("oacute", 243);   // Latin small letter o with acute
        mapHtmlEntity("ocirc", 244);    // Latin small letter o with circumflex
        mapHtmlEntity("otilde", 245);   // Latin small letter o with tilde
        mapHtmlEntity("ouml", 246);     // Latin small letter o with diaeresis
        mapHtmlEntity("divide", 247);   // division sign
        mapHtmlEntity("oslash", 248);   // Latin small letter o with stroke = Latin small letter o slash
        mapHtmlEntity("ugrave", 249);   // Latin small letter u with grave
        mapHtmlEntity("uacute", 250);   // Latin small letter u with acute
        mapHtmlEntity("ucirc", 251);    // Latin small letter u with circumflex
        mapHtmlEntity("uuml", 252);     // Latin small letter u with diaeresis
        mapHtmlEntity("yacute", 253);   // Latin small letter y with acute
        mapHtmlEntity("thorn", 254);    // Latin small letter thorn
        mapHtmlEntity("yuml", 255);     // Latin small letter y with diaeresis

        // Entities for Symbols and Greek Letters.
        // http://htmlhelp.com/reference/html40/entities/symbols.html
        mapHtmlEntity("fnof", 402);     // Latin small f with hook = function = florin
        mapHtmlEntity("Alpha", 913);    // Greek capital letter alpha
        mapHtmlEntity("Beta", 914);     // Greek capital letter beta
        mapHtmlEntity("Gamma", 915);    // Greek capital letter gamma
        mapHtmlEntity("Delta", 916);    // Greek capital letter delta
        mapHtmlEntity("Epsilon", 917);  // Greek capital letter epsilon
        mapHtmlEntity("Zeta", 918);     // Greek capital letter zeta
        mapHtmlEntity("Eta", 919);      // Greek capital letter eta
        mapHtmlEntity("Theta", 920);    // Greek capital letter theta
        mapHtmlEntity("Iota", 921);     // Greek capital letter iota
        mapHtmlEntity("Kappa", 922);    // Greek capital letter kappa
        mapHtmlEntity("Lambda", 923);   // Greek capital letter lambda
        mapHtmlEntity("Mu", 924);       // Greek capital letter mu
        mapHtmlEntity("Nu", 925);       // Greek capital letter nu
        mapHtmlEntity("Xi", 926);       // Greek capital letter xi
        mapHtmlEntity("Omicron", 927);  // Greek capital letter omicron
        mapHtmlEntity("Pi", 928);       // Greek capital letter pi
        mapHtmlEntity("Rho", 929);      // Greek capital letter rho
        mapHtmlEntity("Sigma", 931);    // Greek capital letter sigma
        mapHtmlEntity("Tau", 932);      // Greek capital letter tau
        mapHtmlEntity("Upsilon", 933);  // Greek capital letter upsilon
        mapHtmlEntity("Phi", 934);      // Greek capital letter phi
        mapHtmlEntity("Chi", 935);      // Greek capital letter chi
        mapHtmlEntity("Psi", 936);      // Greek capital letter psi
        mapHtmlEntity("Omega", 937);    // Greek capital letter omega
        mapHtmlEntity("alpha", 945);    // Greek small letter alpha
        mapHtmlEntity("beta", 946);     // Greek small letter beta
        mapHtmlEntity("gamma", 947);    // Greek small letter gamma
        mapHtmlEntity("delta", 948);    // Greek small letter delta
        mapHtmlEntity("epsilon", 949);  // Greek small letter epsilon
        mapHtmlEntity("zeta", 950);     // Greek small letter zeta
        mapHtmlEntity("eta", 951);      // Greek small letter eta
        mapHtmlEntity("theta", 952);    // Greek small letter theta
        mapHtmlEntity("iota", 953);     // Greek small letter iota
        mapHtmlEntity("kappa", 954);    // Greek small letter kappa
        mapHtmlEntity("lambda", 955);   // Greek small letter lambda
        mapHtmlEntity("mu", 956);       // Greek small letter mu
        mapHtmlEntity("nu", 957);       // Greek small letter nu
        mapHtmlEntity("xi", 958);       // Greek small letter xi
        mapHtmlEntity("omicron", 959);  // Greek small letter omicron
        mapHtmlEntity("pi", 960);       // Greek small letter pi
        mapHtmlEntity("rho", 961);      // Greek small letter rho
        mapHtmlEntity("sigmaf", 962);   // Greek small letter final sigma
        mapHtmlEntity("sigma", 963);    // Greek small letter sigma
        mapHtmlEntity("tau", 964);      // Greek small letter tau
        mapHtmlEntity("upsilon", 965);  // Greek small letter upsilon
        mapHtmlEntity("phi", 966);      // Greek small letter phi
        mapHtmlEntity("chi", 967);      // Greek small letter chi
        mapHtmlEntity("psi", 968);      // Greek small letter psi
        mapHtmlEntity("omega", 969);    // Greek small letter omega
        mapHtmlEntity("thetasym", 977); // Greek small letter theta symbol
        mapHtmlEntity("upsih", 978);    // Greek upsilon with hook symbol
        mapHtmlEntity("piv", 982);      // Greek pi symbol
        mapHtmlEntity("bull", 8226);    // bullet = black small circle
        mapHtmlEntity("hellip", 8230);  // horizontal ellipsis = three dot leader
        mapHtmlEntity("prime", 8242);   // prime = minutes = feet
        mapHtmlEntity("Prime", 8243);   // double prime = seconds = inches
        mapHtmlEntity("oline", 8254);   // overline = spacing overscore
        mapHtmlEntity("weierp", 8472);  // script capital P = power set = Weierstrass p
        mapHtmlEntity("image", 8465);   // blackletter capital I = imaginary part
        mapHtmlEntity("real", 8476);    // blackletter capital R = real part symbol
        mapHtmlEntity("trade", 8482);   // trade mark sign
        mapHtmlEntity("alefsym", 8501); // alef symbol = first transfinite cardinal
        mapHtmlEntity("larr", 8592);    // leftwards arrow
        mapHtmlEntity("uarr", 8593);    // upwards arrow
        mapHtmlEntity("rarr", 8594);    // rightwards arrow
        mapHtmlEntity("darr", 8595);    // downwards arrow
        mapHtmlEntity("harr", 8596);    // left right arrow
        mapHtmlEntity("crarr", 8629);   // downwards arrow with corner leftwards = carriage return
        mapHtmlEntity("lArr", 8656);    // leftwards double arrow
        mapHtmlEntity("uArr", 8657);    // upwards double arrow
        mapHtmlEntity("rArr", 8658);    // rightwards double arrow
        mapHtmlEntity("dArr", 8659);    // downwards double arrow
        mapHtmlEntity("hArr", 8660);    // left right double arrow
        mapHtmlEntity("forall", 8704);  // for all
        mapHtmlEntity("part", 8706);    // partial differential
        mapHtmlEntity("exist", 8707);   // there exists
        mapHtmlEntity("empty", 8709);   // empty set = null set = diameter
        mapHtmlEntity("nabla", 8711);   // nabla = backward difference
        mapHtmlEntity("isin", 8712);    // element of
        mapHtmlEntity("notin", 8713);   // not an element of
        mapHtmlEntity("ni", 8715);      // contains as member
        mapHtmlEntity("prod", 8719);    // n-ary product = product sign
        mapHtmlEntity("sum", 8721);     // n-ary sumation
        mapHtmlEntity("minus", 8722);   // minus sign
        mapHtmlEntity("lowast", 8727);  // asterisk operator
        mapHtmlEntity("radic", 8730);   // square root = radical sign
        mapHtmlEntity("prop", 8733);    // proportional to
        mapHtmlEntity("infin", 8734);   // infinity
        mapHtmlEntity("ang", 8736);     // angle
        mapHtmlEntity("and", 8743);     // logical and = wedge
        mapHtmlEntity("or", 8744);      // logical or = vee
        mapHtmlEntity("cap", 8745);     // intersection = cap
        mapHtmlEntity("cup", 8746);     // union = cup
        mapHtmlEntity("int", 8747);     // integral
        mapHtmlEntity("there4", 8756);  // therefore
        mapHtmlEntity("sim", 8764);     // tilde operator = varies with = similar to
        mapHtmlEntity("cong", 8773);    // approximately equal to
        mapHtmlEntity("asymp", 8776);   // almost equal to = asymptotic to
        mapHtmlEntity("ne", 8800);      // not equal to
        mapHtmlEntity("equiv", 8801);   // identical to
        mapHtmlEntity("le", 8804);      // less-than or equal to
        mapHtmlEntity("ge", 8805);      // greater-than or equal to
        mapHtmlEntity("sub", 8834);     // subset of
        mapHtmlEntity("sup", 8835);     // superset of
        mapHtmlEntity("nsub", 8836);    // not a subset of
        mapHtmlEntity("sube", 8838);    // subset of or equal to
        mapHtmlEntity("supe", 8839);    // superset of or equal to
        mapHtmlEntity("oplus", 8853);   // circled plus = direct sum
        mapHtmlEntity("otimes", 8855);  // circled times = vector product
        mapHtmlEntity("perp", 8869);    // up tack = orthogonal to = perpendicular
        mapHtmlEntity("sdot", 8901);    // dot operator
        mapHtmlEntity("lceil", 8968);   // left ceiling = APL upstile
        mapHtmlEntity("rceil", 8969);   // right ceiling
        mapHtmlEntity("lfloor", 8970);  // left floor = APL downstile
        mapHtmlEntity("rfloor", 8971);  // right floor
        mapHtmlEntity("lang", 9001);    // left-pointing angle bracket = bra
        mapHtmlEntity("rang", 9002);    // right-pointing angle bracket = ket
        mapHtmlEntity("loz", 9674);     // lozenge
        mapHtmlEntity("spades", 9824);  // black spade suit
        mapHtmlEntity("clubs", 9827);   // black club suit = shamrock
        mapHtmlEntity("hearts", 9829);  // black heart suit = valentine
        mapHtmlEntity("diams", 9830);   // black diamond suit

        // Entities for other special characters.
        // http://htmlhelp.com/reference/html40/entities/special.html
        mapHtmlEntity("quot", 34);  	// quotation mark = APL quote
        mapHtmlEntity("amp", 38); 	    // ampersand
        mapHtmlEntity("lt", 60); 	    // less-than sign
        mapHtmlEntity("gt", 62); 	    // greater-than sign
        mapHtmlEntity("OElig", 338); 	// Latin capital ligature OE
        mapHtmlEntity("oelig", 339); 	// Latin small ligature oe
        mapHtmlEntity("Scaron", 352); 	// Latin capital letter S with caron
        mapHtmlEntity("scaron", 353); 	// Latin small letter s with caron
        mapHtmlEntity("Yuml", 376); 	// Latin capital letter Y with diaeresis
        mapHtmlEntity("circ", 710);     // modifier letter circumflex accent
        mapHtmlEntity("tilde", 732);    // small tilde
        mapHtmlEntity("ensp", 8194);    // en space
        mapHtmlEntity("emsp", 8195);    // em space
        mapHtmlEntity("thinsp", 8201);  // thin space
        mapHtmlEntity("zwnj", 8204);    // zero width non-joiner
        mapHtmlEntity("zwj", 8205);     // zero width joiner
        mapHtmlEntity("lrm", 8206);     // left-to-right mark
        mapHtmlEntity("rlm", 8207);     // right-to-left mark
        mapHtmlEntity("ndash", 8211);   // en dash
        mapHtmlEntity("mdash", 8212);   // em dash
        mapHtmlEntity("lsquo", 8216);   // left single quotation mark
        mapHtmlEntity("rsquo", 8217);   // right single quotation mark
        mapHtmlEntity("sbquo", 8218);   // single low-9 quotation mark
        mapHtmlEntity("ldquo", 8220);   // left double quotation mark
        mapHtmlEntity("rdquo", 8221);   // right double quotation mark
        mapHtmlEntity("bdquo", 8222);   // double low-9 quotation mark
        mapHtmlEntity("dagger", 8224);  // dagger
        mapHtmlEntity("Dagger", 8225);  // double dagger
        mapHtmlEntity("permil", 8240);  // per mille sign
        mapHtmlEntity("lsaquo", 8249);  // single left-pointing angle quotation mark
        mapHtmlEntity("rsaquo", 8250);  // single right-pointing angle quotation mark
        mapHtmlEntity("euro", 8364);    // euro sign
    }

    private static void mapHtmlEntity(String entity, Integer code) {
        entityMappings.put(entity.toUpperCase(), code);
    }

    /** These elements are always empty elements. End-tag is redundant for these. */
    private static final List<String> emptyElements;
    static {
        emptyElements = Arrays.asList("br", "meta", "link", "base", "input", "img", "area");
    }

    /** These elements appear only once in an HTML document. */
    private static final List<String> singleElements;
    static {
        singleElements = Arrays.asList("html", "head", "body");
    }

    /** These entities are the only entities supported by default in XML. */
    private static final List<String> xmlEntities;
    static {
        xmlEntities = Arrays.asList("amp", "lt", "gt", "quot", "apos");
    }

    /** Returns null when nesting stack will be empty. */
    private static String peekNextNested(Stack<String> nestingStack) {
        if (nestingStack.isEmpty()) return null;
        return nestingStack.peek();
    }

    public static String toXml(String html) {
        StringBuilder result = new StringBuilder();

        // Standard XML file header, including entities that are likely to be used.
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        final ParseReader reader = new ParseReader(html);
        final TagParser parser = new TagParser(reader);
        final Stack<String> nestingStack = new Stack<String>();

        try {
            ParseToken token = parser.getNextToken();

            // Ignore leading white-space.
            while (token instanceof SpacesToken || token instanceof NewlineToken || token instanceof DoctypeToken)
                token = parser.getNextToken();

            while (!(token instanceof EOFToken)) {
                log.debug("Token = {}", token.toString());
                if (token instanceof TagToken) {
                    TagToken t = (TagToken) token;
                    if (!t.getTag().isEndTag()) {

                        // Deal with start-tag. Typically this will be new element nesting.
                        Tag startTag = t.getTag();
                        if (startTag instanceof EmptyElement) {
                            result.append(startTag.toString());
                        } else {

                            // Tags that are always empty elements are converted to empty elements here.
                            // Element names are pushed onto the stack to balance elements with missing end-tag.
                            String startTagName = startTag.getName().toLowerCase();
                            log.debug("startTagName = {}", startTagName);
                            if (emptyElements.contains(startTagName)) {
                                result.append(new EmptyElement(startTag));
                            } else {
                                result.append(startTag.toString());
                                nestingStack.push(startTagName);
                            }
                        }
                    } else {

                        // Deal with end-tag.
                        Tag endTag = t.getTag();

                        // Remove the '/' from beginning of the tag-name for comparison.
                        String endTagName = endTag.getName().substring(1).toLowerCase();
                        log.debug("endTagName = {}", endTagName);

                        // Ignore some end-tags for empty elements that are handled with or without empty element syntax.
                        if (emptyElements.contains(endTagName)) {
                            log.info("Ignoring redundant end-tag: {}", endTagName);
                        } else {

                            // Keep element tags matched appropriately.
                            String peek = peekNextNested(nestingStack);
                            if (peek == null) {
                                log.warn("Ignoring extra content at end of document! </{}> ({})", endTagName, parser.getCharacterPosition());
                            } else {
                                if (peek.equals(endTagName)) {
                                    nestingStack.pop();
                                } else {

                                    // Pair all the previous unmatched tags for these important structural elements.
                                    // These elements appear only once, so should never be automatically closed.
                                    if (singleElements.contains(endTagName)) {

                                        while (peek != endTagName) {
                                            StringBuilder endtag = (new StringBuilder()).append("</").append(peek).append('>');
                                            log.warn("Adding a missing end-tag! {} ({})", endtag, parser.getCharacterPosition());
                                            result.append(endtag);
                                            nestingStack.pop();
                                            peek = peekNextNested(nestingStack);
                                        }

                                        // Remove the current item from the stack, as it has been paired now.
                                        nestingStack.pop();

                                    } else {

                                        // Insert a matching start-tag before the unbalanced end-tag found.
                                        StringBuilder startTag = (new StringBuilder()).append("<").append(endTagName).append('>');
                                        log.warn("Adding a missing start-tag! {} ({})", startTag, parser.getCharacterPosition());
                                        result.append(startTag);
                                    }
                                }

                                // Write the current element end-tag.
                                result.append("</").append(endTagName).append('>');
                            }
                        }
                    }
                } else if (token instanceof WordToken) {
                    WordToken t = (WordToken) token;
                    result.append(t.getWord());
                } else if (token instanceof SpacesToken) {
                    SpacesToken t = (SpacesToken) token;
                    result.append(t.getSpaces());
                } else if (token instanceof NumberToken) {
                    NumberToken t = (NumberToken) token;
                    result.append(t.getNumber());
                } else if (token instanceof EntityReferenceToken) {
                    EntityReferenceToken t = (EntityReferenceToken) token;
                    result.append(xmlEntity(t.getName()));
                } else if (token instanceof PunctuationToken) {
                    PunctuationToken t = (PunctuationToken) token;
                    result.append(t.getCharacter());
                } else if (token instanceof CharacterEntityToken) {
                    CharacterEntityToken t = (CharacterEntityToken) token;
                    result.append(t.getCharacter());
                } else if (token instanceof NewlineToken) {
                    result.append('\n');
                } else if (token instanceof ScriptToken) {
                    ScriptToken t = (ScriptToken) token;
                    if (t.getScript().length() > 0) { // Script element contents are often empty.
                        // NOTE: Removing any prior use of CDATA section in script, to avoid conflict.
                        String script = t.getScript().replaceAll("<\\!\\[CDATA\\[", "").replaceAll("\\]\\]>", "");
                        result.append("/*<![CDATA[*/").append(script).append("/*]]>*/");
                    }
                } else if (token instanceof CDataToken) {
                    CDataToken t = (CDataToken) token;
                    result.append("<![CDATA[").append(t.getData()).append("]]>");
                } else if (token instanceof CommentToken) {
                    CommentToken t = (CommentToken) token;
                    result.append("<!--").append(t.getComment()).append("-->");
                } else if (token instanceof DoctypeToken) {
                    // Ignore.
                } else if (token instanceof ProcessingInstructionToken) {
                    // Ignore.
                } else {
                    log.warn("Unexpected token! {}", token.toString());
                }
                token = parser.getNextToken();
            }

            log.info(parser.getCompletionReport());
        } catch (Exception ex) {
            log.error("EXCEPTION", ex);
            result = null;
        }

        if (result == null) return null;
        return result.toString();
    }

    /** Convert entity reference for valid XML text. */
    private static String xmlEntity(String entityName) throws Exception {
        String entityLowerCase = entityName.toLowerCase();
        if (xmlEntities.contains(entityLowerCase)) {
            return (new StringBuilder())
                .append('&').append(entityLowerCase).append(';')
                .toString();
        } else {
            String entityUpperCase = entityName.toUpperCase();
            if (!entityMappings.containsKey(entityUpperCase)) {
                throw new Exception("Unsupported entity name: " + entityUpperCase);
            }
            return (new StringBuilder())
                .append("&#").append(entityMappings.get(entityUpperCase)).append(';')
                .toString();
        }
    }
}
