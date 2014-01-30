package com.geowarin;

import org.festassertgoodies.XmlEqual;
import org.intellij.lang.annotations.Language;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Date: 30/01/2014
 * Time: 22:03
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
public class XPathFilterTest {

    @Language("XML")
    public static final String XML_INPUT = "" +
            "<persons>" +
            "   <person name='Joe' age='26'>" +
            "       <project name='dom4j' language='java'/>" +
            "       <project name='underscore' language='javascript'/>" +
            "       <project name='amber' language='javascript'/>" +
            "   </person>" +
            "   <person name='Jane' age='23'>" +
            "       <project name='dom4j' language='java'/>" +
            "   </person>" +
            "   <person name='Kevin' age='15'>" +
            "       <project name='myMMORPG' language='php'/>" +
            "       <project name='struts2' language='java'/>" +
            "   </person>" +
            "</persons>";

    @Language("XML")
    public static final String SIMPLE_EXPECTED_XML = "" +
            "<persons>" +
            "   <person name='Joe' age='26'>" +
            "       <project name='dom4j' language='java'/>" +
            "       <project name='underscore' language='javascript'/>" +
            "       <project name='amber' language='javascript'/>" +
            "   </person>" +
            "</persons>";

    @Test
    public void should_handle_simple_xpath() throws Exception {
        String result = new XPathFilter(XML_INPUT).filter("/persons/person[@name = 'Joe']");
        assertThat(new XmlEqual(result)).isXmlEqual(SIMPLE_EXPECTED_XML);
    }

    @Language("XML")
    public static final String NESTED_EXPECTED_XML = "" +
            "<persons>" +
            "   <person name='Joe' age='26'>" +
            "       <project name='dom4j' language='java'/>" +
            "   </person>" +
            "   <person name='Jane' age='23'>" +
            "       <project name='dom4j' language='java'/>" +
            "   </person>" +
            "</persons>";

    @Test
    public void should_handle_nested_results() {
        String result = new XPathFilter(XML_INPUT).filter("/persons/person[@age > 18]/project[@language = 'java']");
        assertThat(new XmlEqual(result)).isXmlEqual(NESTED_EXPECTED_XML);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_when_xpath_does_not_find_result() {
        new XPathFilter(XML_INPUT).filter("/persons/person[@name = 'unexisting']");
    }
}
