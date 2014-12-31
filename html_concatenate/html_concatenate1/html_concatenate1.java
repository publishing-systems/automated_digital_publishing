/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of html_concatenate1.
 *
 * html_concatenate1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html_concatenate1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html_concatenate1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html_concatenate/html_concatenate1.java
 * @brief Concatenates several HTML input files.
 * @details It is also possible to use html_concatenate1 for replacing the
 *     header part of a HTML input file with the header part of another HTML
 *     file.
 * @author Stephan Kreutzer
 * @since 2014-11-01
 */



import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.stream.XMLResolver;
import java.util.Map;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.XMLStreamException;
import java.io.UnsupportedEncodingException;



public class html_concatenate1
{
    public static void main(String args[])
    {
        System.out.print("html_concatenate1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 1)
        {
            System.out.print("Usage:\n" +
                             "\thtml_concatenate1 input-config\n\n");

            System.exit(1);
        }

        File jobFile = new File(args[0]);
        
        if (jobFile.exists() != true)
        {
            System.out.print("html_concatenate1: '" + jobFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (jobFile.isFile() != true)
        {
            System.out.print("html_concatenate1: '" + jobFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (jobFile.canRead() != true)
        {
            System.out.print("html_concatenate1: '" + jobFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        String programPath = html_concatenate1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        File entitiesDirectory = new File(programPath + "entities");
        
        if (entitiesDirectory.exists() != true)
        {
            if (entitiesDirectory.mkdir() != true)
            {
                System.out.print("html_concatenate1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("html_concatenate1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }


        File headFile = null;
        List<File> inputFiles = new ArrayList<File>();
        File outputFile = null;

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(jobFile);
            document.getDocumentElement().normalize();


            {
                NodeList inputFilesNodeList = document.getElementsByTagName("input-file");
                int inputFilesNodeListLength = inputFilesNodeList.getLength();

                if (inputFilesNodeListLength > 0)
                {
                    for (int i = 0; i < inputFilesNodeListLength; i++)
                    {
                        Node inputFileNode = inputFilesNodeList.item(i);

                        NamedNodeMap inputFileNodeAttributes = inputFileNode.getAttributes();
                        
                        if (inputFileNodeAttributes == null)
                        {
                            System.out.print("html_concatenate1: Input file entry #" + (i + 1) + " in '" + jobFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = inputFileNodeAttributes.getNamedItem("path");
                        
                        if (pathAttribute == null)
                        {
                            System.out.print("html_concatenate1: Input file entry #" + (i + 1) + " in '" + jobFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        File inputFile = new File(pathAttribute.getTextContent());
                        
                        if (inputFile.isAbsolute() != true)
                        {
                            inputFile = new File(jobFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + pathAttribute.getTextContent());
                        }
                           
                        if (inputFile.exists() != true)
                        {
                            System.out.print("html_concatenate1: Input file '" + inputFile.getAbsolutePath() + "' doesn't exist.\n");
                            System.exit(-1);
                        }

                        if (inputFile.isFile() != true)
                        {
                            System.out.print("html_concatenate1: Input path '" + inputFile.getAbsolutePath() + "' isn't a file.\n");
                            System.exit(-1);
                        }

                        if (inputFile.canRead() != true)
                        {
                            System.out.print("html_concatenate1: Input file '" + inputFile.getAbsolutePath() + "' isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        inputFiles.add(inputFile);
                    }
                }
                else
                {
                    System.out.print("html_concatenate1: No input files configured.\n");
                    System.exit(-1);
                }
            }

            {
                NodeList outputFileNodeList = document.getElementsByTagName("output-file");
                int outputFileNodeListLength = outputFileNodeList.getLength();

                if (outputFileNodeListLength == 1)
                {
                    for (int i = 0; i < outputFileNodeListLength; i++)
                    {
                        Node outputFileNode = outputFileNodeList.item(i);

                        NamedNodeMap outputFileNodeAttributes = outputFileNode.getAttributes();
                        
                        if (outputFileNodeAttributes == null)
                        {
                            System.out.print("html_concatenate1: Output file entry in '" + jobFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = outputFileNodeAttributes.getNamedItem("path");
                        
                        if (pathAttribute == null)
                        {
                            System.out.print("html_concatenate1: Output file entry in '" + jobFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        outputFile = new File(pathAttribute.getTextContent());
                        
                        if (outputFile.isAbsolute() != true)
                        {
                            outputFile = new File(jobFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + pathAttribute.getTextContent());
                        }
                    }
                }
                else if (outputFileNodeListLength < 1)
                {
                    System.out.print("html_concatenate1: No output file configured.\n");
                    System.exit(-1);
                }
                else
                {
                    System.out.print("html_concatenate1: More than one output file configured.\n");
                    System.exit(-1);
                }
            }

            {
                NodeList headFileNodeList = document.getElementsByTagName("head-file");
                int headFileNodeListLength = headFileNodeList.getLength();

                if (headFileNodeListLength == 1)
                {
                    for (int i = 0; i < headFileNodeListLength; i++)
                    {
                        Node headFileNode = headFileNodeList.item(i);

                        NamedNodeMap headFileNodeAttributes = headFileNode.getAttributes();
                        
                        if (headFileNodeAttributes == null)
                        {
                            System.out.print("html_concatenate1: Head file entry in '" + jobFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = headFileNodeAttributes.getNamedItem("path");
                        
                        if (pathAttribute == null)
                        {
                            System.out.print("html_concatenate1: Head file entry in '" + jobFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        headFile = new File(pathAttribute.getTextContent());
                        
                        if (headFile.isAbsolute() != true)
                        {
                            headFile = new File(jobFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + pathAttribute.getTextContent());
                        }
                           
                        if (headFile.exists() != true)
                        {
                            System.out.print("html_concatenate1: Head file '" + headFile.getAbsolutePath() + "' doesn't exist.\n");
                            System.exit(-1);
                        }

                        if (headFile.isFile() != true)
                        {
                            System.out.print("html_concatenate1: Head file path '" + headFile.getAbsolutePath() + "' isn't a file.\n");
                            System.exit(-1);
                        }

                        if (headFile.canRead() != true)
                        {
                            System.out.print("html_concatenate1: Head file '" + headFile.getAbsolutePath() + "' isn't readable.\n");
                            System.exit(-1);
                        }
                    }
                }
                else if (headFileNodeListLength < 1)
                {
                    headFile = inputFiles.get(0);
                }
                else
                {
                    System.out.print("html_concatenate1: More than one head file configured.\n");
                    System.exit(-1);
                }
            }
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


        String doctypeDeclaration = new String("<!DOCTYPE");
        int doctypePosMatching = 0;
        String doctype = new String();
    
        try
        {
            FileInputStream in = new FileInputStream(headFile);
            
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
            System.out.print("html_concatenate1: Unknown XHTML version.\n");
            System.exit(-1);
        }
        
        if (resolverConfigFile.exists() != true)
        {
            System.out.print("html_concatenate1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (resolverConfigFile.isFile() != true)
        {
            System.out.print("html_concatenate1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (resolverConfigFile.canRead() != true)
        {
            System.out.print("html_concatenate1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        EntityResolverLocal localResolver = new EntityResolverLocal(resolverConfigFile, entitiesDirectory);
        StringBuilder afterBodyStringBuilder = new StringBuilder("");


        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(localResolver);
            InputStream in = new FileInputStream(headFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            boolean body = false;
            boolean afterBody = false;

            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(outputFile.getAbsolutePath()),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by html_concatenate1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write(doctype + "\n");

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    if (body == true)
                    {
                        continue;
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

                    if (afterBody == false)
                    {
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
                    else
                    {
                        afterBodyStringBuilder.append("<" + fullElementName);

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

                            afterBodyStringBuilder.append(" " + fullAttributeName + "=\"" + attributeValue + "\"");
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
                                afterBodyStringBuilder.append(" xmlns=\"" + namespace.getNamespaceURI() + "\"");
                            }
                            else
                            {
                                afterBodyStringBuilder.append(" xmlns:" + namespace.getPrefix() + "=\"" + namespace.getNamespaceURI() + "\"");
                            }
                        }

                        afterBodyStringBuilder.append(">");
                    }
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
                            System.out.println("html_concatenate1: 'body' end element found more than once in head file '" + headFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        body = false;
                        afterBody = true;
                    }

                    if (body == false)
                    {
                        if (afterBody == false)
                        {
                            writer.write("</" + fullElementName + ">");
                        }
                        else
                        {
                            afterBodyStringBuilder.append("</" + fullElementName + ">");
                        }
                    }
                }
                else if (event.isCharacters() == true)
                {
                    if (body == false)
                    {
                        if (afterBody == false)
                        {
                            event.writeAsEncodedUnicode(writer);
                        }
                        else
                        {
                            afterBodyStringBuilder.append(event.asCharacters());
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


        for (int i = 0; i < inputFiles.size(); i++)
        {
            File inputFile = inputFiles.get(i);
            
            doctypePosMatching = 0;
            String currentDoctype = new String();
        
            try
            {
                FileInputStream in = new FileInputStream(inputFile);
                
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
                            currentDoctype += currentByteCharacter;
                        }
                        else
                        {
                            doctypePosMatching = 0;
                            currentDoctype = new String();
                        }
                    }
                    else
                    {
                        currentDoctype += currentByteCharacter;
                    
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
            
            if (currentDoctype.equals(doctype) != true)
            {
                System.out.println("html_concatenate1: Doctype '" + currentDoctype + "' of input file #" + (i + 1) + ", '" + inputFile.getAbsolutePath() + "' differs from head file XHTML doctype '" + doctype + "'.");
                System.exit(-1);
            }
            

            try
            {
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                inputFactory.setXMLResolver(localResolver);
                InputStream in = new FileInputStream(inputFile);
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

                XMLEvent event = null;

                boolean body = false;

                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(outputFile.getAbsolutePath(), true),
                                        "UTF8"));

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
                            continue;
                        }

                        if (body == true)
                        {
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
                                System.out.println("html_concatenate1: 'body' end element found more than once in input file #" + (i + 1) + ", '" + inputFile.getAbsolutePath() + "'.");
                                System.exit(-1);
                            }

                            body = false;
                            continue;
                        }

                        if (body == true)
                        {
                            writer.write("</" + fullElementName + ">");
                        }
                    }
                    else if (event.isCharacters() == true)
                    {
                        if (body == true)
                        {
                            event.writeAsEncodedUnicode(writer);
                        }
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
        }
    

        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(outputFile.getAbsolutePath(), true),
                                    "UTF8"));

            writer.write(afterBodyStringBuilder.toString());

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

        return;
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
                            System.out.print("html_concatenate1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
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
                            System.out.print("html_concatenate1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html_concatenate1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html_concatenate1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html_concatenate1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
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
            System.out.print("html_concatenate1: Can't resolve entity, no local entities directory.\n");
            System.exit(-1);
        }
        
        if (this.configFile == null)
        {
            System.out.print("html_concatenate1: Can't resolve entity, no entities configured.\n");
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
            System.out.print("html_concatenate1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html_concatenate1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html_concatenate1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html_concatenate1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
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

