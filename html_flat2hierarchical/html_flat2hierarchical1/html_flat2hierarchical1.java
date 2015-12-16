/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of html_flat2hierarchical1.
 *
 * html_flat2hierarchical1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html_flat2hierarchical1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html_flat2hierarchical1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html_flat2hierarchical/html_flat2hierarchical1.java
 * @brief Transforms a flat, sequencial HTML file to a hierarchical nested one.
 * @author Stephan Kreutzer
 * @since 2014-04-26
 */



import org.xml.sax.EntityResolver;
import java.io.File;
import org.xml.sax.InputSource;
import java.util.Map;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.xml.stream.XMLResolver;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import java.util.Stack;
import java.util.Iterator;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class html_flat2hierarchical1
{
    public static void main(String args[])
    {
        System.out.print("html_flat2hierarchical1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\thtml_flat2hierarchical1 in-file configuration-file out-file\n\n");

            System.exit(1);
        }


        String programPath = html_flat2hierarchical1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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
                System.out.print("html_flat2hierarchical1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("html_flat2hierarchical1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-2);
            }
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-3);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-4);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-5);
        }
        
        File configFile = new File(args[1]);

        if (configFile.exists() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-6);
        }

        if (configFile.isFile() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-7);
        }

        if (configFile.canRead() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-8);
        }
        
        File outFile = new File(args[2]);


        Map<Integer, HierarchyDefinition> hierarchyDefinitions = new HashMap<Integer, HierarchyDefinition>();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;


            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();
                
                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String elementNameString = elementName.getLocalPart();

                    if (elementNameString.equalsIgnoreCase("hierarchy") == true)
                    {
                        Attribute attributeLevel = event.asStartElement().getAttributeByName(new QName("level"));
                        Attribute attributeElement = event.asStartElement().getAttributeByName(new QName("element"));
                        Attribute attributeAttribute = event.asStartElement().getAttributeByName(new QName("attribute"));
                        Attribute attributeValue = event.asStartElement().getAttributeByName(new QName("value"));
                                
                        if (attributeLevel == null)
                        {
                            System.out.println("html_flat2hierarchical1: Hierarchy definition is missing the 'level' attribute in '" + configFile.getAbsolutePath() + "'.");                            
                            continue;
                        }
                        
                        if (attributeElement == null)
                        {
                            System.out.println("html_flat2hierarchical1: Hierarchy definition is missing the 'element' attribute in '" + configFile.getAbsolutePath() + "'.");                            
                            continue;
                        }
                        
                        Integer level = null;
                        String element = attributeElement.getValue();
                        String attribute = null;
                        String value = null;
                        
                        try
                        {
                            level = Integer.parseInt(attributeLevel.getValue());
                        }
                        catch (NumberFormatException ex)
                        {
                            System.out.println("html_flat2hierarchical: Hierarchy definition has invalid value '" + attributeLevel.getValue() + "' in the 'level' attribute in '" + configFile.getAbsolutePath() + "'.");
                            continue;
                        }
                        
                        if (attributeAttribute != null)
                        {
                            if (attributeValue == null)
                            {
                                System.out.println("html_flat2hierarchical1: Hierarchy definition has 'attribute' attribute but is missing the 'value' attribute in '" + configFile.getAbsolutePath() + "'.");                            
                                continue;
                            }
                        
                            attribute = attributeAttribute.getValue();
                            value = attributeValue.getValue();
                        }

                        HierarchyDefinition hierarchyDefinition = new HierarchyDefinition(level, element, attribute, value);
                        
                        if (hierarchyDefinitions.containsKey(level) != true)
                        {
                            hierarchyDefinitions.put(level, hierarchyDefinition);
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-9);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-11);
        }

        {
            Integer counter = 1;
            
            while (true)
            {
                if (hierarchyDefinitions.containsKey(counter) != true)
                {
                    break;
                }
                
                counter++;
            }
            
            if (counter < hierarchyDefinitions.size())
            {
                System.out.println("html_flat2hierarchical1: Hierarchy definition ends with level '" + counter + "', expected level '" + (counter + 1) + "' is missing in '" + configFile.getAbsolutePath() + "'.");
                System.exit(-12);
            }
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
                                    new FileOutputStream(outFile.getAbsolutePath()),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write(doctype + "\n");
            writer.write("<!-- This file was generated by html_flat2hierarchical1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");


            Stack<StructureStackElement> structureStack = new Stack<StructureStackElement>();

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
                    
                    Iterator iterDefinitions = hierarchyDefinitions.entrySet().iterator();
                    Integer currentLevel = 0;
                    
                    while (iterDefinitions.hasNext() == true && currentLevel == 0)
                    {
                        Map.Entry pair = (Map.Entry)iterDefinitions.next();
                        
                        HierarchyDefinition hierarchyDefinition = (HierarchyDefinition)pair.getValue();
                        
                        if (hierarchyDefinition.GetElement().equalsIgnoreCase(fullElementName) == true)
                        {
                            if (hierarchyDefinition.GetAttribute() == null)
                            {
                                /**
                                 * @todo: Check for hierarchy definitions with exactly the same element
                                 *     name but different level number.
                                 */
                            
                                currentLevel = hierarchyDefinition.GetLevel();
                            }
                            else
                            {
                                String attribute = hierarchyDefinition.GetAttribute();
                                
                                // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                                @SuppressWarnings("unchecked")
                                Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                                
                                while (attributes.hasNext() == true)
                                {  
                                    Attribute attributeFound = attributes.next();
                                    QName attributeFoundName = attributeFound.getName();
                                    String fullAttributeFoundName = attributeFoundName.getLocalPart();
                                    
                                    if (attributeFoundName.getPrefix().length() > 0)
                                    {
                                        fullAttributeFoundName = attributeFoundName.getPrefix() + ":" + fullAttributeFoundName;
                                    }
             
                                    if (fullAttributeFoundName.equalsIgnoreCase(attribute) == true &&
                                        attributeFound.getValue().equalsIgnoreCase(hierarchyDefinition.GetValue()) == true)
                                    {
                                        /**
                                         * @todo: Check for hierarchy definitions with exactly the same element
                                         *     name but different level number.
                                         */

                                        currentLevel = hierarchyDefinition.GetLevel();

                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    if (currentLevel <= 0)
                    {
                        structureStack.push(new StructureStackElement(null));
                    }
                    else
                    {
                        // Start of new hierarchy level found, which might of
                        // lower or higher order than the current one, and in
                        // case of the latter, all previous lower hierarchy
                        // elements need to be ended.
                    
                        while (structureStack.empty() != true)
                        {
                            StructureStackElement stackElement = structureStack.peek();

                            if (stackElement.GetLevel() == null)
                            {
                                break;
                            }
                            else if (stackElement.GetLevel() >= currentLevel)
                            {
                                HierarchyDefinition hierarchyDefinition = hierarchyDefinitions.get(stackElement.GetLevel());
  
                                writer.write("</" + hierarchyDefinition.GetElement() + ">");
                                
                                structureStack.pop();
                            }
                            else
                            {
                                break;
                            }
                        }
                        
                        structureStack.push(new StructureStackElement(currentLevel));
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
                        
                        // Ampersand needs to be the first, otherwise it would double-encode
                        // other entities.
                        attributeValue = attributeValue.replaceAll("&", "&amp;");
                        attributeValue = attributeValue.replaceAll("\"", "&quot;");
                        attributeValue = attributeValue.replaceAll("'", "&apos;");
                        attributeValue = attributeValue.replaceAll("<", "&lt;");
                        attributeValue = attributeValue.replaceAll(">", "&gt;");

                        writer.write(" " + fullAttributeName + "=\"" + attributeValue + "\"");
                    }
                    
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

                    {
                        // If the end of the document would be reached, all remaining
                        // hierarchical levels need to be ended.
                    
                        XMLEvent nextElement = eventReader.peek();

                        if (nextElement.isEndDocument() == true)
                        {
                            while (structureStack.empty() == false)
                            {
                                StructureStackElement currentStackElement = structureStack.peek();

                                if (currentStackElement.GetLevel() != null)
                                {
                                    HierarchyDefinition hierarchyDefinition = hierarchyDefinitions.get(currentStackElement.GetLevel());

                                    writer.write("</" + hierarchyDefinition.GetElement() + ">");

                                    structureStack.pop();
                                }
                                else
                                {
                                    // The remaining currentStackElement.GetLevel() == null
                                    // should be the last, if it wasn't itself part of the
                                    // elements that define the structure.
                                    break;
                                }
                            }
                        }
                    }
                
                    if (structureStack.empty() != true)
                    {
                        StructureStackElement currentStackElement = structureStack.peek();
                        
                        if (currentStackElement.GetLevel() == null)
                        {
                            writer.write("</" + fullElementName + ">");
                            structureStack.pop();
                        }
                        else
                        {
                            // Don't write out the ending of an element which is
                            // defined as a hierarchical level marker, so it will
                            // hierarchically span over all subsequent elements till
                            // another hierarchical level element of higher order is
                            // started and therefore ends the current one, or the end
                            // of the document is reached.
                        }
                    }
                    else
                    {
                        // Supposed to be the end of the root element.
                    }
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
            System.exit(-15);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-17);
        }

        System.exit(0);
    }
}

class StructureStackElement
{
    public StructureStackElement(Integer level)
    {
        this.level = level;
    }
    
    public Integer GetLevel()
    {
        return this.level;
    }
    
    protected Integer level;
}

class HierarchyDefinition
{
    public HierarchyDefinition(Integer level,
                               String element,
                               String attribute,
                               String value)
    {
        this.level = level;
        this.element = element;
        this.attribute = attribute;
        this.value = value;
    }                           

    public Integer GetLevel()
    {
        return this.level;
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

    protected Integer level;
    protected String element;
    protected String attribute;
    protected String value;
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
                            System.out.print("html_flat2hierarchical1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
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
                            System.out.print("html_flat2hierarchical1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-22);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html_flat2hierarchical1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-23);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html_flat2hierarchical1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-24);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html_flat2hierarchical1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
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
            System.out.print("html_flat2hierarchical1: Can't resolve entity, no local entities directory.\n");
            System.exit(-26);
        }
        
        if (this.configFile == null)
        {
            System.out.print("html_flat2hierarchical1: Can't resolve entity, no entities configured.\n");
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
            System.out.print("html_flat2hierarchical1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-28);                  
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-29);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-30);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html_flat2hierarchical1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
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
