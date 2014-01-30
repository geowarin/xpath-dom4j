package com.geowarin;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Date: 30/01/2014
 * Time: 22:03
 *
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
public class XPathFilter {
    private final Document xmlDocument;

    public XPathFilter(String xml) {
        xmlDocument = readXml(xml);
    }

    public String filter(String xPath) {
        Element root = xmlDocument.getRootElement();
        List<Element> resultNodes = root.selectNodes(xPath);

        if (resultNodes.isEmpty()) {
            throw new IllegalStateException("No result found for xpath " + xPath);
        }

        deleteNonResultNodes(resultNodes);
        return write(root);
    }

    private void deleteNonResultNodes(List<Element> resultNodes) {
        Set<Element> nodesToKeep = new HashSet<>(resultNodes);
        Set<Element> parents;
        do {
            parents = getParentNodes(nodesToKeep);

            for (Element parent : parents) {
                List<Element> children = parent.elements();
                for (Element child : children) {
                    if (!nodesToKeep.contains(child)) {
                        parent.remove(child);
                    }
                }
            }
            nodesToKeep = new HashSet<>(parents);
        }
        while (!parents.isEmpty());
    }

    private Set<Element> getParentNodes(Collection<Element> nodes) {
        Set<Element> parents = new HashSet<>();
        for (Element node : nodes) {
            Element parent = node.getParent();
            if (parent != null) {
                parents.add(parent);
            }
        }
        return parents;
    }

    private Document readXml(String xml) {
        Document document;
        try (StringReader reader = new StringReader(xml)) {
            DocumentFactory factory = new DocumentFactory();
            SAXReader saxReader = new SAXReader();
            saxReader.setDocumentFactory(factory);
            document = saxReader.read(reader);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e);
        }
        return document;
    }

    private String write(Element rootElement) {
        Document documentOut = DocumentHelper.createDocument();
        documentOut.add((Element) rootElement.clone());

        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());

        try {
            xmlWriter.write(documentOut);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                xmlWriter.close();
            } catch (IOException ignored) {
            }
        }

        return writer.toString();
    }
}
