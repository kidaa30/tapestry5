// Copyright 2006, 2007, 2008, 2009, 2010, 2014 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.dom;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;

import java.io.PrintWriter;
import java.util.Collections;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> refs/remotes/apache/5.0
import java.util.Map;

/**
 * The root node of a DOM.
 */
public final class Document extends Node
{
    /**
     * XML Namespace URI. May be bound to the "xml" but must not be bound to any other prefix.
     */
    public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";

    /**
     * Namespace used exclusively for defining namespaces.
     */
    public static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";

    private Element rootElement;

    private DTD dtd;

    private final MarkupModel model;

    private final String encoding;
    
    private final String mimeType;

    /**
     * Non-element content that comes between the DOCTYPE and the root element.
     */
    private List<Node> preamble;

    /**
     * Same as Document(model, null).
     * @param model a {@link MarkupModel}.
     */
    public Document(final MarkupModel model)
    {
        this(model, null);
    }

    /**
     * Same as Document(model, encoding, null).
     * @param model a {@link MarkupModel}.
     */
    public Document(final MarkupModel model, final String encoding) {
        this(model, encoding, null);
    }

    /**
     * Creates a document instance with a given markup model, encoding and MIME type.
     * @param model a {@link MarkupModel}.
     * @param encoding the encoding.
     * @param mimeType the MIME type.
     * @since 5.4
     */
    public Document(final MarkupModel model, final String encoding, final String mimeType)
    {
        super(null);

        assert model != null;

        this.model = model;
        this.encoding = encoding;
        this.mimeType = mimeType;
    }

    @Override
    public Document getDocument()
    {
        return this;
    }

    /**
     * Finds an element based on a path of element names.
     *
     * @param path slash separated series of element names
     * @return the matching element, or null if not found
     * @see Element#find(String)
     */
    public Element find(String path)
    {
        assert InternalUtils.isNonBlank(path);

        if (rootElement == null)
            return null;

        int slashx = path.indexOf("/");

        String rootElementName = slashx < 0 ? path : path.substring(0, slashx);

        if (!rootElement.getName().equals(rootElementName))
            return null;

        return slashx < 0 ? rootElement : rootElement.find(path.substring(slashx + 1));
    }

    /**
     * Builds with an instance of {@link DefaultMarkupModel}.
     */
    public Document()
    {
        this(new DefaultMarkupModel());
    }

    public MarkupModel getMarkupModel()
    {
        return model;
    }

    /**
     * Creates the root element for this document, replacing any previous root element.
     */
    public Element newRootElement(String name)
    {
        rootElement = new Element(this, null, name);

        return rootElement;
    }

    /**
     * Creates a new root element within a namespace.
     *
     * @param namespace URI of namespace containing the element
     * @param name      name of element with namespace
     * @return the root element
     */
    public Element newRootElement(String namespace, String name)
    {
        rootElement = new Element(this, namespace, name);

        return rootElement;
    }

    @Override
    public void toMarkup(Document document, PrintWriter writer, Map<String, String> namespaceURIToPrefix)
    {
        if (model.isXML())
        {
            writer.print("<?xml version=\"1.0\"");

            if (encoding != null)
                writer.printf(" encoding=\"%s\"", encoding);

            writer.print("?>\n");
        }
        if (dtd != null)
        {
            dtd.toMarkup(writer);
        }

<<<<<<< HEAD
        if (preamble != null)
        {
            for (Node n : preamble)
                n.toMarkup(this, writer, namespaceURIToPrefix);
        }
=======
        Map<String, String> initialNamespaceMap = Collections.emptyMap();

        rootElement.toMarkup(document, writer, initialNamespaceMap);
    }
>>>>>>> refs/remotes/apache/5.0

        if (rootElement == null)
            return;

        Map<String, String> initialNamespaceMap = CollectionFactory.newMap();

        initialNamespaceMap.put("xml", "http://www.w3.org/XML/1998/namespace");
        initialNamespaceMap.put("xmlns", "http://www.w3.org/2000/xmlns/");

        rootElement.toMarkup(document, writer, initialNamespaceMap);
    }

    public Element getRootElement()
    {
        return rootElement;
    }

    /**
     * Tries to find an element in this document whose id is specified.
     *
     * @param id the value of the id attribute of the element being looked for
     * @return the element if found. null if not found.
     */
    public Element getElementById(String id)
    {
        return rootElement.getElementById(id);
    }

    /**
     * Sets the DTD for the document, overriding any prior DTD.
     *
     * @param name     non-blank name of document type (i.e., "html")
     * @param publicId optional
     * @param systemId optional
     */
    public void dtd(String name, String publicId, String systemId)
    {
        dtd = new DTD(name, publicId, systemId);
    }

<<<<<<< HEAD
    /**
     * Returns true if the document has an explicit DTD (set via {@link #dtd(String, String, String)}).
     *
     * @since 5.3
     */
    public boolean hasDTD()
    {
        return dtd != null;
    }

    @Override
    protected Map<String, String> getNamespaceURIToPrefix()
    {
        if (rootElement == null)
        {
            return Collections.emptyMap();
        }

        return rootElement.getNamespaceURIToPrefix();
    }

    /**
     * Visits the root element of the document.
     *
     * @param visitor callback
     * @since 5.1.0.0
     */
    void visit(Visitor visitor)
    {
        rootElement.visit(visitor);
    }

    private <T extends Node> T newChild(T child)
    {
        if (preamble == null)
            preamble = CollectionFactory.newList();

        preamble.add(child);

        return child;
    }

    /**
     * Adds the comment and returns this document for further construction.
     *
     * @since 5.1.0.0
     */
    public Document comment(String text)
    {
        newChild(new Comment(null, text));

        return this;
    }

    /**
     * Adds the raw text and returns this document for further construction.
     *
     * @since 5.1.0.0
     */
    public Document raw(String text)
    {
        newChild(new Raw(null, text));

        return this;
    }

    /**
     * Adds and returns a new text node (the text node is returned so that {@link Text#write(String)} or [@link
     * {@link Text#writef(String, Object[])} may be invoked.
     *
     * @param text initial text for the node
     * @return the new Text node
     */
    public Text text(String text)
    {
        return newChild(new Text(null, text));
    }

    /**
     * Adds and returns a new CDATA node.
     *
     * @param content the content to be rendered by the node
     * @return the newly created node
     */
    public CData cdata(String content)
    {
        return newChild(new CData(null, content));
    }

    /**
     * Returns the MIME type of this document.
     * @return the MIME type.
     * @since 5.4
     */
    public String getMimeType()
    {
        return mimeType;
    }
    
=======
    @Override
    protected Map<String, String> getNamespaceURIToPrefix()
    {
        if (rootElement == null) return Collections.emptyMap();

        return rootElement.getNamespaceURIToPrefix();
    }
>>>>>>> refs/remotes/apache/5.0
}
