package net.sourceforge.jesktopimpl.core;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Paul Hammant
 * @version $Revision: 1.1 $
 */
public class DefaultDocumentBuilderFactory extends DocumentBuilderFactory {

    DocumentBuilderFactory delegate;

    public DefaultDocumentBuilderFactory() {
        delegate = DocumentBuilderFactory.newInstance();
    }

    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return delegate.newDocumentBuilder();
    }

    public Object getAttribute(String name) throws IllegalArgumentException {
        return delegate.getAttribute(name);
    }

    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        delegate.setAttribute(name, value);
    }
}
