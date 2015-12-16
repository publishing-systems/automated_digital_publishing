/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of html_prepare4latex1.
 *
 * html_prepare4latex1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html_prepare4latex1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html_prepare4latex1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/latex/html_prepare4latex1.java
 * @brief Prepares the text of a HTML file for consumption by LaTeX (escaping of
 *     special characters).
 * @author Stephan Kreutzer
 * @since 2014-06-14
 */



import javax.xml.stream.XMLResolver;
import java.io.File;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import javax.xml.namespace.QName;
import java.util.Iterator;
import javax.xml.stream.events.Attribute;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.HashMap;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.stream.events.Namespace;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class html_prepare4latex1
{
    public static void main(String args[])
    {
        System.out.print("html_prepare4latex1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\thtml_prepare4latex1 in-html-file out-html-file\n\n");

            System.exit(1);
        }


        String programPath = html_prepare4latex1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
            programPath = URLDecoder.decode(programPath, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        File entitiesDirectory = new File(programPath + "entities");

        if (entitiesDirectory.exists() != true)
        {
            if (entitiesDirectory.mkdir() != true)
            {
                System.out.print("html_prepare4latex1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("html_prepare4latex1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-2);
            }
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("html_prepare4latex1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-3);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("html_prepare4latex1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-4);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("html_prepare4latex1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-5);
        }
        
        File outFile = new File(args[1]);

        try
        {
            if (outFile.getCanonicalPath().equals(inFile.getCanonicalPath()) == true)
            {
                System.out.print("html_prepare4latex1: Input and output file are the same, can't read and write to '" + inFile.getAbsolutePath() + "' at the same time.\n");
                System.exit(-1);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }


        String doctypeDeclaration = new String("<!DOCTYPE");
        int doctypePosMatching = 0;
        String doctype = new String();
    
        try
        {
            FileInputStream in = new FileInputStream(inFile);
            
            int currentByte = 0;
 
            do
            {
                currentByte = in.read();
                
                if (currentByte < 0 ||
                    currentByte > 255)
                {
                    break;
                }
                

                char currentByteCharacter = (char) currentByte;
                
                if (doctypePosMatching < doctypeDeclaration.length())
                {
                    if (currentByteCharacter == doctypeDeclaration.charAt(doctypePosMatching))
                    {
                        doctypePosMatching++;
                        doctype += currentByteCharacter;
                    }
                    else
                    {
                        doctypePosMatching = 0;
                        doctype = new String();
                    }
                }
                else
                {
                    doctype += currentByteCharacter;
                
                    if (currentByteCharacter == '>')
                    {
                        break;
                    }
                }
            
            } while (true);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-13);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-14);
        }


        try
        {
            EntityResolverLocal localResolver = new EntityResolverLocal(entitiesDirectory);
        
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(localResolver);
            InputStream in = new FileInputStream(inFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;


            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(outFile),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write(doctype + "\n");
            writer.write("<!-- This file was generated by html_prepare4latex1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getLocalPart();

                    if (elementName.getPrefix().isEmpty() != true)
                    {
                        fullElementName = elementName.getPrefix() + ":" + fullElementName;
                    }

                    writer.write("<" + fullElementName);
                    
                    // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                    @SuppressWarnings("unchecked")
                    Iterator<Namespace> namespaces = (Iterator<Namespace>)event.asStartElement().getNamespaces();
                    
                    if (namespaces.hasNext() == true)
                    {
                        Namespace namespace = namespaces.next();
                        
                        if (namespace.isDefaultNamespaceDeclaration() == true &&
                            namespace.getPrefix().length() <= 0)
                        {
                            writer.write(" xmlns=\"" + namespace.getNamespaceURI() + "\"");
                        }
                        else
                        {
                            writer.write(" xmlns:" + namespace.getPrefix() + "=\"" + namespace.getNamespaceURI() + "\"");
                        }
                    }
                    
                    // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                    @SuppressWarnings("unchecked")
                    Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                    
                    while (attributes.hasNext() == true)
                    {  
                        Attribute attribute = attributes.next();
                        QName attributeName = attribute.getName();
                        String fullAttributeName = attributeName.getLocalPart();

                        if (attributeName.getPrefix().length() > 0)
                        {
                            fullAttributeName = attributeName.getPrefix() + ":" + fullAttributeName;
                        }

                        String attributeValue = attribute.getValue();

                        // Ampersand needs to be the first, otherwise it would double-encode
                        // other entities.
                        attributeValue = attributeValue.replaceAll("&", "&amp;");
                        attributeValue = attributeValue.replaceAll("\"", "&quot;");
                        attributeValue = attributeValue.replaceAll("'", "&apos;");
                        attributeValue = attributeValue.replaceAll("<", "&lt;");
                        attributeValue = attributeValue.replaceAll(">", "&gt;");

                        writer.write(" " + fullAttributeName + "=\"" + attributeValue + "\"");
                    }

                    writer.write(">");
                }
                else if (event.isEndElement() == true)
                {
                    QName elementName = event.asEndElement().getName();
                    String fullElementName = elementName.getLocalPart();
                    
                    if (elementName.getPrefix().isEmpty() != true)
                    {
                        fullElementName = elementName.getPrefix() + ":" + fullElementName;
                    }
                    
                    writer.write("</" + fullElementName + ">");
                }
                else if (event.isCharacters() == true)
                {
                    String text = event.asCharacters().getData();
                    
                    int textLength = text.length();
                    
                    for (int i = 0; i < textLength; i++)
                    {
                        char character = text.charAt(i);
                        
                        switch (character)
                        {
                        case '#':
                            writer.write("\\#");
                            break;
                        case '$':
                            writer.write("\\$");
                            break;
                        case '%':
                            writer.write("\\%");
                            break;
                        case '<':
                            System.out.println("html_prepare4latex1: Less-than character ('<') detected, which got written as '&lt;' for well-formed XML/HTML output and needs to be replaced by '<' for LaTeX manually.");
                            writer.write("&lt;");
                            break;
                        case '>':
                            System.out.println("html_prepare4latex1: Greater-than character ('>') detected, which got written as '&gt;' for well-formed XML/HTML output and needs to be replaced by '>' for LaTeX manually.");
                            writer.write("&gt;");
                            break;
                        case '&':
                            System.out.println("html_prepare4latex1: Ampersand ('&') detected, which needs to be escaped manually by '\\&' for LaTeX because '\\&' in the output wouldn't be well-formed XML/HTML.");
                            writer.write("&amp;");
                            break;
                        case '\\':
                            writer.write("\\textbackslash{}");
                            break;
                        case '^':
                            writer.write("\\textasciicircum{}");
                            break;
                        case '_':
                            writer.write("\\_");
                            break;
                        case '{':
                            writer.write("\\{");
                            break;
                        case '}':
                            writer.write("\\}");
                            break;
                        case '~':
                            writer.write("\\textasciitilde{}");
                            break;
                        /**
                         * @todo A command line flag should allow to enable/disable this feature.
                         */
                        /*
                        // The ngerman package will try to replace any '"' with leading or followed
                        // by a space character with the corresponding typographic quotation marks.
                        // If this feature would preserve non-typographic quotation marks, users could
                        // be invited to use them in the source files. Therefore, this feature is
                        // disabled in order to let the ngerman package do its magic or to let the
                        // processing fail for those cases which are handled here, where non-typographic
                        // quotation marks are needed explicitly.
                        case '"':
                            // Needed for ngerman package (any '"' followed by a character instead
                            // of a space would cause an error).
                            writer.write("\\textquotedbl{}");
                            break;
                        */
                        default:
                            writer.write(character);
                            break;
                        }
                    }
                }
            }

            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-14);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }

        System.exit(0);
    }
}

class EntityResolverLocal implements XMLResolver
{
    public EntityResolverLocal(File entitiesDirectory)
    {
        this.entitiesDirectory = entitiesDirectory;
        this.configFile = null;
        this.localEntities = new HashMap<String, File>();
        
        boolean success = true;
        
        if (success == true)
        {
            success = this.entitiesDirectory.exists();
        }
        
        if (success == true)
        {
            success = this.entitiesDirectory.isDirectory();
        }
        
        if (success != true)
        {
            this.entitiesDirectory = null;
        }
        
        if (success == true)
        {
            this.configFile = new File(this.entitiesDirectory.getAbsolutePath() + "/config.xml");
            success = this.configFile.exists();
        }
        
        if (success == true)
        {
            success = this.configFile.isFile();
        }
        
        if (success == true)
        {
            success = this.configFile.canRead();
        }

        if (success == true)
        {
            Document document = null;
        
            try
            {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(this.configFile);
                document.getDocumentElement().normalize();
            }
            catch (ParserConfigurationException ex)
            {
                ex.printStackTrace();
                System.exit(-18);
            }
            catch (SAXException ex)
            {
                ex.printStackTrace();
                System.exit(-19);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-20);
            }


            NodeList entityNodeList = document.getElementsByTagName("entity");
            int entityNodeListCount = entityNodeList.getLength();

            for (int i = 0; i < entityNodeListCount; i++)
            {
                Node entityNode = entityNodeList.item(i);
                NodeList entityChildNodeList = entityNode.getChildNodes();
                int entityChildNodeListCount = entityChildNodeList.getLength();

                for (int j = 0; j < entityChildNodeListCount; j++)
                {
                    Node entityChildNode = entityChildNodeList.item(j);

                    if (entityChildNode.getNodeName().equalsIgnoreCase("resolve") == true)
                    {
                        Element element = (Element) entityChildNode;
                        String identifier = element.getAttribute("identifier");
                        String reference = element.getAttribute("reference");
                        File referencedFile = new File(reference);
                        
                        if (identifier.length() <= 0)
                        {
                            System.out.print("html_prepare4latex1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
                            System.exit(-21);
                        }
                        
                        if (referencedFile.isAbsolute() != true)
                        {
                            String relativePath = this.entitiesDirectory.getAbsolutePath();
                            
                            if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                            {
                                // Remove dot that references the local, current directory.
                                relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                            }
                            
                            if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                            {
                                relativePath += File.separator;
                            }
                            
                            relativePath += reference;
                            referencedFile = new File(relativePath);
                        }
                        
                        if (referencedFile.exists() != true)
                        {
                            System.out.print("html_prepare4latex1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-22);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html_prepare4latex1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-23);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html_prepare4latex1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-24);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html_prepare4latex1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
                            System.exit(-25);
                        }
                    }
                }
            }
        }
    }

    public Object resolveEntity(String publicID,
                                String systemID,
                                String baseURI,
                                String namespace)
    {
        if (this.entitiesDirectory == null)
        {
            System.out.print("html_prepare4latex1: Can't resolve entity, no local entities directory.\n");
            System.exit(-26);
        }
        
        if (this.configFile == null)
        {
            System.out.print("html_prepare4latex1: Can't resolve entity, no entities configured.\n");
            System.exit(-27);
        }
    
        File localEntity = null;
    
        if (localEntity == null)
        {
            if (this.localEntities.containsKey(publicID) == true)
            {
                localEntity = this.localEntities.get(publicID);
            }
        }
        
        if (localEntity == null)
        {
            if (this.localEntities.containsKey(systemID) == true)
            {
                localEntity = this.localEntities.get(systemID);
            }
        }
        
        if (localEntity == null)
        {
            System.out.print("html_prepare4latex1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-28);                  
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html_prepare4latex1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-29);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html_prepare4latex1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-30);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html_prepare4latex1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
            System.exit(-31);
        }
        
        FileInputStream fileInputStream = null;
        
        try
        {
            fileInputStream = new FileInputStream(localEntity);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-32);
        }
        
        return fileInputStream;
    }

    protected File entitiesDirectory;
    protected File configFile;
    protected Map<String, File> localEntities;
}
