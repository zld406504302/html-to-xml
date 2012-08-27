HTML to XML
===========

Informal conversion of HTML to XML. Useful for screen-scraping with XPath.

Build
-----

To build this project you'll need OpenJDK or similar, and [Maven](http://maven.apache.org/).

Clone this repository and run the following commands at the root of the new folder.

Build and package the library Jar file:

    mvn clean package

Usage
-----

Use the static toXml method of the XmlExtractor class to convert an HTML string to an XML string.

    String xml = stever.tagparser.XmlExtractor.toXml("<html><body>Hello world</body></html>");
