/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of html_attributeanalyzer1.
 *
 * html_attributeanalyzer1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html_attributeanalyzer1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html_attributeanalyzer1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html_attributeanalyzer/html_attributeanalyzer1.java
 * @brief Generates a list of the attributes used in a XML file, recognizing
 *     parent element, element, attribute name and attribute value.
 * @author Stephan Kreutzer
 * @since 2014-10-25
 */



import java.io.File;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.util.Stack;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLResolver;
import java.io.IOException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;



public class html_attributeanalyzer1
{
    public static void main(String args[])
    {
        System.out.print("html_attributeanalyzer1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\thtml_attributeanalyzer1 in-file out-file\n\n");

            System.exit(1);
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("html_attributeanalyzer1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-3);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("html_attributeanalyzer1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-4);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("html_attributeanalyzer1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-5);
        }

        String programPath = html_attributeanalyzer1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
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
                System.out.print("html_attributeanalyzer1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("html_attributeanalyzer1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }

        File outFile = new File(args[1]);


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
            
            if (resolverConfigFile.exists() != true)
            {
                System.out.print("html_attributeanalyzer1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.isFile() != true)
            {
                System.out.print("html_attributeanalyzer1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.canRead() != true)
            {
                System.out.print("html_attributeanalyzer1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }
        else if (doctype.contains("\"-//W3C//DTD XHTML 1.1//EN\"") == true)
        {
            resolverConfigFile = new File(entitiesDirectory.getAbsolutePath() + "/config_xhtml1_1.xml");
            
            if (resolverConfigFile.exists() != true)
            {
                System.out.print("html_attributeanalyzer1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.isFile() != true)
            {
                System.out.print("html_attributeanalyzer1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.canRead() != true)
            {
                System.out.print("html_attributeanalyzer1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }
        else
        {
            System.out.print("html_attributeanalyzer1: Unknown XHTML version.\n");
            System.exit(-1);
        }

        if (resolverConfigFile == null)
        {
            System.exit(-1);
        }


        Map<String, List<AttributeInformation>> attributeInformationMap = new HashMap<String, List<AttributeInformation>>();
        Stack<String> structureStack = new Stack<String>();

        try
        {
            EntityResolverLocal localResolver = new EntityResolverLocal(resolverConfigFile, entitiesDirectory);
        
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(localResolver);
            InputStream in = new FileInputStream(inFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            boolean body = false;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String parentElement = new String();
                
                    if (structureStack.empty() != true)
                    {
                        parentElement = structureStack.peek();
                    }
                
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
                    
                    if (body == false)
                    {
                        continue;
                    }
                
                    structureStack.push(fullElementName);
                
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

                        if (attributeInformationMap.containsKey(fullAttributeName) != true)
                        {
                            attributeInformationMap.put(fullAttributeName, new ArrayList<AttributeInformation>());
                        }

                        Iterator<AttributeInformation> iterAttributeInformation = attributeInformationMap.get(fullAttributeName).iterator();
                        boolean found = false;
                         
                        while (iterAttributeInformation.hasNext() == true)
                        {
                            AttributeInformation attributeInformation = iterAttributeInformation.next();
                            
                            if (attributeInformation.GetParent().equals(parentElement) &&
                                attributeInformation.GetElement().equals(fullElementName) &&
                                attributeInformation.GetAttribute().equals(fullAttributeName) &&
                                attributeInformation.GetValue().equals(attribute.getValue()))
                            {
                                found = true;
                                break;
                            }
                        }
                        
                        if (found == false)
                        {
                            AttributeInformation newEntry = new AttributeInformation(parentElement,
                                                                                     fullElementName,
                                                                                     fullAttributeName,
                                                                                     attribute.getValue());
                            attributeInformationMap.get(fullAttributeName).add(newEntry);
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    if (body == true)
                    {
                        structureStack.pop();

                        QName elementName = event.asEndElement().getName();
                        String fullElementName = elementName.getLocalPart();

                        if (elementName.getPrefix().isEmpty() != true)
                        {
                            fullElementName = elementName.getPrefix() + ":" + fullElementName;
                        }
                    
                        if (structureStack.empty() == true &&
                            fullElementName.equalsIgnoreCase("body") == true)
                        {
                            body = false;
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


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(outFile),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by html_attributeanalyzer1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<attribute-list>\n");

            Iterator iterMap = attributeInformationMap.entrySet().iterator();

            while (iterMap.hasNext() == true)
            {
                Map.Entry pair = (Map.Entry)iterMap.next();
                
                // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                @SuppressWarnings("unchecked")
                List<AttributeInformation> attributeInformationList = (List<AttributeInformation>)pair.getValue();

                Iterator<AttributeInformation> iterAttributeInformation = attributeInformationList.iterator();

                while (iterAttributeInformation.hasNext() == true)
                {
                    AttributeInformation attributeInformation = iterAttributeInformation.next();
                    
                    String attributeValue = attributeInformation.GetValue();

                    // Ampersand needs to be the first, otherwise it would double-encode
                    // other entities.
                    attributeValue = attributeValue.replaceAll("&", "&amp;");
                    attributeValue = attributeValue.replaceAll("\"", "&quot;");
                    attributeValue = attributeValue.replaceAll("'", "&apos;");
                    attributeValue = attributeValue.replaceAll("<", "&lt;");
                    attributeValue = attributeValue.replaceAll(">", "&gt;");
                    
                    writer.write("  <attribute ");
                    writer.write("element=\"" + attributeInformation.GetElement() + "\" ");
                    writer.write("attribute=\"" + attributeInformation.GetAttribute() + "\" ");
                    writer.write("value=\"" + attributeValue + "\" ");
                    writer.write("parent=\"" + attributeInformation.GetParent() + "\"/>\n");
                }
            }
            
            writer.write("</attribute-list>\n");

            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
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
                            System.out.print("html_attributeanalyzer1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
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
                            System.out.print("html_attributeanalyzer1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html_attributeanalyzer1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html_attributeanalyzer1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html_attributeanalyzer1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
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
            System.out.print("html_attributeanalyzer1: Can't resolve entity, no local entities directory.\n");
            System.exit(-1);
        }
        
        if (this.configFile == null)
        {
            System.out.print("html_attributeanalyzer1: Can't resolve entity, no entities configured.\n");
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
            System.out.print("html_attributeanalyzer1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html_attributeanalyzer1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html_attributeanalyzer1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html_attributeanalyzer1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
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

class AttributeInformation
{
    public AttributeInformation(String parent,
                                String element,
                                String attribute,
                                String value)
    {
        this.parent = parent;
        this.element = element;
        this.attribute = attribute;
        this.value = value;
    }                           

    public String GetParent()
    {
        return this.parent;
    }
    
    public String GetElement()
    {
        return this.element;
    }
    
    public String GetAttribute()
    {
        return this.attribute;
    }
    
    public String GetValue()
    {
        return this.value;
    }

    protected String parent;
    protected String element;
    protected String attribute;
    protected String value;
}

