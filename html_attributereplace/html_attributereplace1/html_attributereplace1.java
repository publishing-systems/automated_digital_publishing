/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of html_attributereplace1.
 *
 * html_attributereplace1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html_attributereplace1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html_attributereplace1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html_attributereplace/html_attributereplace1.java
 * @brief Replaces the value of certain attributes of certain elements.
 * @author Stephan Kreutzer
 * @since 2014-11-01
 */



import java.io.File;
import java.util.Map;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLResolver;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;
import java.io.UnsupportedEncodingException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.net.URLDecoder;



public class html_attributereplace1
{
    public static void main(String args[])
    {
        System.out.print("html_attributereplace1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\thtml_attributereplace1 in-file mapping-file out-file\n\n");

            System.exit(1);
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("html_attributereplace1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("html_attributereplace1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("html_attributereplace1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        File mappingFile = new File(args[1]);

        if (mappingFile.exists() != true)
        {
            System.out.print("html_attributereplace1: '" + mappingFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (mappingFile.isFile() != true)
        {
            System.out.print("html_attributereplace1: '" + mappingFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (mappingFile.canRead() != true)
        {
            System.out.print("html_attributereplace1: '" + mappingFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        String programPath = html_attributereplace1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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
                System.out.print("html_attributereplace1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("html_attributereplace1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }

        File outFile = new File(args[2]);


        Map<String, String> mapping = new HashMap<String, String>();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(mappingFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;


            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();
                
                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String elementNameString = elementName.getLocalPart();

                    if (elementNameString.equalsIgnoreCase("mapping") == true)
                    {
                        Attribute attributeElement = event.asStartElement().getAttributeByName(new QName("element"));
                        Attribute attributeAttribute = event.asStartElement().getAttributeByName(new QName("attribute"));
                        Attribute attributeOldValue = event.asStartElement().getAttributeByName(new QName("old-value"));
                        Attribute attributeNewValue = event.asStartElement().getAttributeByName(new QName("new-value"));
                        
                        if (attributeElement == null)
                        {
                            System.out.println("html_attributereplace1: Mapping is missing the 'element' attribute in " + mappingFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        if (attributeAttribute == null)
                        {
                            System.out.println("html_attributereplace1: Mapping is missing the 'attribute' attribute in " + mappingFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        if (attributeOldValue == null)
                        {
                            System.out.println("html_attributereplace1: Mapping is missing the 'old-value' attribute in " + mappingFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        if (attributeNewValue == null)
                        {
                            System.out.println("html_attributereplace1: Mapping is missing the 'new-value' attribute in " + mappingFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        String element = attributeElement.getValue();
                        String attribute = attributeAttribute.getValue();
                        String oldValue = attributeOldValue.getValue();
                        String newValue = attributeNewValue.getValue();

                        if (mapping.containsKey(element + "/" + attribute + "/" + oldValue) != true)
                        {
                            mapping.put(element + "/" + attribute + "/" + oldValue, newValue);
                        }
                        else
                        {
                            String existingNewValue = mapping.get(element + "/" + attribute + "/" + oldValue);
                            
                            if (existingNewValue.equals(newValue) == true)
                            {
                                System.out.println("html_attributereplace1: New value '" + newValue + "' in mapping for element '" + element + "', attribute '" + attribute + "', old value '" + oldValue + "' is configured more than once.");
                            }
                            else
                            {
                                System.out.println("html_attributereplace1: Mapping for element '" + element + "', attribute '" + attribute + "', old value '" + oldValue + "' is configured more than once, new value '" + newValue + "' differs from '" + existingNewValue + "'.");
                                System.exit(-1);
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (XMLStreamException ex)
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
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }


        File resolverConfigFile = null;

        if (doctype.contains("\"-//W3C//DTD XHTML 1.0 Strict//EN\"") == true)
        {
            resolverConfigFile = new File(entitiesDirectory.getAbsolutePath() + "/config_xhtml1-strict.xml");
        }
        else if (doctype.contains("\"-//W3C//DTD XHTML 1.1//EN\"") == true)
        {
            resolverConfigFile = new File(entitiesDirectory.getAbsolutePath() + "/config_xhtml1_1.xml");
        }
        else
        {
            System.out.print("html_attributereplace1: Unknown XHTML version.\n");
            System.exit(-1);
        }
        
        if (resolverConfigFile.exists() != true)
        {
            System.out.print("html_attributereplace1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (resolverConfigFile.isFile() != true)
        {
            System.out.print("html_attributereplace1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (resolverConfigFile.canRead() != true)
        {
            System.out.print("html_attributereplace1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        try
        {
            EntityResolverLocal localResolver = new EntityResolverLocal(resolverConfigFile, entitiesDirectory);
        
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(localResolver);
            InputStream in = new FileInputStream(inFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            boolean body = false;

            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(outFile.getAbsolutePath()),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by html_attributereplace1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write(doctype + "\n");

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

                    if (body == false &&
                        fullElementName.equalsIgnoreCase("body") == true)
                    {
                        body = true;
                    }

                    writer.write("<" + fullElementName);

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
                        String mappingKey = fullElementName + "/" + fullAttributeName + "/" + attributeValue;
                        
                        if (mapping.containsKey(mappingKey) == true &&
                            body == true)
                        {
                            attributeValue = mapping.get(mappingKey);
                        }

                        // Ampersand needs to be the first, otherwise it would double-encode
                        // other entities.
                        attributeValue = attributeValue.replaceAll("&", "&amp;");
                        attributeValue = attributeValue.replaceAll("\"", "&quot;");
                        attributeValue = attributeValue.replaceAll("'", "&apos;");
                        attributeValue = attributeValue.replaceAll("<", "&lt;");
                        attributeValue = attributeValue.replaceAll(">", "&gt;");

                        writer.write(" " + fullAttributeName + "=\"" + attributeValue + "\"");
                        
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

                    if (fullElementName.equalsIgnoreCase("body") == true)
                    {
                        if (body == false)
                        {
                            System.out.println("html_attributereplace1: 'body' end element found more than once.");
                            System.exit(-1);
                        }

                        body = false;
                    }

                    writer.write("</" + fullElementName + ">");
                }
                else if (event.isCharacters() == true)
                {
                    event.writeAsEncodedUnicode(writer);
                }
            }

            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
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

        System.exit(0);
    }
}

class EntityResolverLocal implements XMLResolver
{
    public EntityResolverLocal(File configFile, File entitiesDirectory)
    {
        this.configFile = configFile;
        this.entitiesDirectory = entitiesDirectory;
        this.localEntities = new HashMap<String, File>();
        
        boolean success = true;

        if (success == true)
        {
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
                System.exit(-1);
            }
            catch (SAXException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
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
                            System.out.print("html_attributereplace1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
                            System.exit(-1);
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
                            System.out.print("html_attributereplace1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html_attributereplace1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html_attributereplace1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html_attributereplace1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
                            System.exit(-1);
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
            System.out.print("html_attributereplace1: Can't resolve entity, no local entities directory.\n");
            System.exit(-1);
        }
        
        if (this.configFile == null)
        {
            System.out.print("html_attributereplace1: Can't resolve entity, no entities configured.\n");
            System.exit(-1);
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
            System.out.print("html_attributereplace1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html_attributereplace1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html_attributereplace1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html_attributereplace1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
            System.exit(-1);
        }
        
        FileInputStream fileInputStream = null;
        
        try
        {
            fileInputStream = new FileInputStream(localEntity);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        return fileInputStream;
    }

    protected File configFile;
    protected File entitiesDirectory;
    protected Map<String, File> localEntities;
}

