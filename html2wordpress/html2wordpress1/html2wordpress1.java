/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of html2wordpress1.
 *
 * html2wordpress1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2wordpress1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2wordpress1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html2wordpress1.java
 * @brief Publishes HTML to a running WordPress server via XMLRPC API.
 * @author Stephan Kreutzer
 * @since 2014-08-09
 */



import javax.xml.stream.XMLResolver;
import java.io.File;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import java.util.Iterator;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.io.OutputStream;
import java.util.HashMap;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;



public class html2wordpress1
{
    public static void main(String[] args)
    {
        System.out.print("html2wordpress1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        if (args.length != 1)
        {
            System.out.print("Usage:\n" +
                             "\thtml2wordpress1 job-file\n\n");

            System.exit(1);
        }
        
        File jobFile = new File(args[0]);

        if (jobFile.exists() != true)
        {
            System.out.print("html2wordpress1: Job file '" + jobFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (jobFile.isFile() != true)
        {
            System.out.print("html2wordpress1: Job file '" + jobFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (jobFile.canRead() != true)
        {
            System.out.print("html2wordpress1: Job file '" + jobFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        JobFileProcessor jobFileProcessor = new JobFileProcessor(jobFile);
        jobFileProcessor.Run();

        Map<String, String> jobSettings = jobFileProcessor.GetJobSettings();

        if (jobSettings.containsKey("input-html-file") != true)
        {
            System.out.println("html2wordpress1: No input file specified.");
            System.exit(-1);
        }
        
        if (jobSettings.containsKey("wordpress-xmlrpc-url") != true)
        {
            System.out.println("html2wordpress1: No WordPress XML-RPC URL specified.");
            System.exit(-1);
        }
        
        if (jobSettings.containsKey("wordpress-user-public-key") != true)
        {
            System.out.println("html2wordpress1: No WordPress authentication public key specified.");
            System.exit(-1);
        }
        
        if (jobSettings.containsKey("wordpress-user-private-key") != true)
        {
            System.out.println("html2wordpress1: No WordPress authentication private key specified.");
            System.exit(-1);
        }

        File inFile = new File(jobSettings.get("input-html-file"));

        if (inFile.exists() != true)
        {
            System.out.print("html2wordpress1: Input file '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("html2wordpress1: Input file '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("html2wordpress1: Input file '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        XHTMLValidator validator = new XHTMLValidator();
        validator.Validate(inFile);

        String xmlrpcURL = jobSettings.get("wordpress-xmlrpc-url");
        
        if (xmlrpcURL.startsWith("http://") == true)
        {
            xmlrpcURL = xmlrpcURL.substring(new String("http://").length());
        }
        else if (xmlrpcURL.startsWith("https://") == true)
        {
            xmlrpcURL = xmlrpcURL.substring(new String("https://").length());
        }
        
        int slashPosition = Math.max(xmlrpcURL.indexOf('/'), xmlrpcURL.indexOf('\\'));
        
        if (slashPosition < 1)
        {
            System.out.println("html2wordpress1: WordPress XML-RPC URL '" + xmlrpcURL + "' doesn't contain a host/file path.");
            System.exit(-1);
        }

        String host = xmlrpcURL.substring(0, slashPosition);
        String xmlrpcPath = xmlrpcURL.substring(slashPosition);
        
        if (host.length() <= 0)
        {
            System.out.println("html2wordpress1: WordPress XML-RPC URL '" + xmlrpcURL + "' is missing the host.");
            System.exit(-1);
        }

        if (xmlrpcPath.length() <= 0)
        {
            System.out.println("html2wordpress1: WordPress XML-RPC URL '" + xmlrpcURL + "' is missing the XML-RPC file path.");
            System.exit(-1);
        }
        
        if (xmlrpcPath.indexOf('/') != 0 &&
            xmlrpcPath.indexOf('\\') != 0)
        {
            System.out.println("html2wordpress: WordPress XML-RPC URL '" + xmlrpcURL + "' doesn't start with a leading slash for file path specification.");
            System.exit(-1);
        }


        String xmlrpc = "<?xml version=\"1.0\"?>" +
                        "<methodCall>" +
                          "<methodName>wp.newPost</methodName>" +
                          "<params>" +
                            "<param>" +
                              "<value>" +
                                "<array>" +
                                  "<data>" +
                                    "<value><int></int></value>" +
                                    "<value><string></string></value>" +
                                    "<value><string></string></value>" +
                                    "<value>" +
                                      "<struct>";

        /**
         * @todo Every setting needs proper XML special characters encoding.
         */

        if (jobSettings.containsKey("wordpress-post-type") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_type</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-type") + "</string></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-status") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_status</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-status") + "</string></value>" +
                                        "</member>";
        }
        
        if (jobSettings.containsKey("wordpress-post-title") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_title</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-title") + "</string></value>" +
                                        "</member>";
        }
        
        if (jobSettings.containsKey("wordpress-post-user-id") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_author</name>" +
                                          "<value><int>" + jobSettings.get("wordpress-post-user-id") + "</int></value>" +
                                        "</member>";
        }
        
        if (jobSettings.containsKey("wordpress-post-excerpt") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_excerpt</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-excerpt") + "</string></value>" +
                                        "</member>";
        }
        
        xmlrpc +=                       "<member>" +
                                          "<name>post_content</name>" +
                                          "<value>" +
                                            "<string>";

        String programPath = html2wordpress1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

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
                System.out.print("html2wordpress1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("html2wordpress1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
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
                System.out.print("html2wordpress1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.isFile() != true)
            {
                System.out.print("html2wordpress1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.canRead() != true)
            {
                System.out.print("html2wordpress1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }
        else if (doctype.contains("\"-//W3C//DTD XHTML 1.1//EN\"") == true)
        {
            resolverConfigFile = new File(entitiesDirectory.getAbsolutePath() + "/config_xhtml1_1.xml");
            
            if (resolverConfigFile.exists() != true)
            {
                System.out.print("html2wordpress1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.isFile() != true)
            {
                System.out.print("html2wordpress1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (resolverConfigFile.canRead() != true)
            {
                System.out.print("html2wordpress1: Resolver configuration file '" + resolverConfigFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }
        else
        {
            System.out.print("html2wordpress1: Unknown XHTML version.\n");
            System.exit(-1);
        }

        if (resolverConfigFile == null)
        {
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
                    else if (body == true)
                    {
                        xmlrpc += "&lt;" + fullElementName;
                    
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
                            attributeValue = attributeValue.replaceAll("&", "&amp;amp;");
                            attributeValue = attributeValue.replaceAll("\"", "&amp;quot;");
                            attributeValue = attributeValue.replaceAll("'", "&amp;apos;");
                            attributeValue = attributeValue.replaceAll("<", "&amp;lt;");
                            attributeValue = attributeValue.replaceAll(">", "&amp;gt;");

                            xmlrpc += " " + fullAttributeName + "=&quot;" + attributeValue + "&quot;";
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
                                xmlrpc += " xmlns=&quot;" + namespace.getNamespaceURI() + "&quot;";
                            }
                            else
                            {
                                xmlrpc += " xmlns:" + namespace.getPrefix() + "=&quot;" + namespace.getNamespaceURI() + "&quot;";
                            }
                        }
                        
                        xmlrpc += "&gt;";
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
                        if (body == true)
                        {
                            body = false;
                        }
                        else
                        {
                            System.out.println("html2wordpress1: Misplaced </body> found.");
                            System.exit(-1);
                        }
                    }
                    else if (body == true)
                    {
                        xmlrpc += "&lt;/" + fullElementName + "&gt;";
                    }
                }
                else if (event.isCharacters() == true)
                {
                    if (body == true)
                    {
                        String characters = event.asCharacters().getData();
                        
                        // Ampersand needs to be the first, otherwise it would double-encode
                        // other entities.
                        characters = characters.replaceAll("&", "&amp;amp;");
                        characters = characters.replaceAll("\"", "&amp;quot;");
                        characters = characters.replaceAll("'", "&amp;apos;");
                        characters = characters.replaceAll("<", "&amp;lt;");
                        characters = characters.replaceAll(">", "&amp;gt;");
                    
                        xmlrpc += characters;
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-61);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-62);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-63);
        }


        xmlrpc +=                           "</string>" +
                                          "</value>" +
                                        "</member>";

        if (jobSettings.containsKey("wordpress-post-date") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_date</name>" +
                                          "<value><dateTime.iso8601>" + jobSettings.get("wordpress-post-date") + "</dateTime.iso8601></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-format") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_format</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-format") + "</string></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-name-slug") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_name</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-name-slug") + "</string></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-comment-default-status") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>comment_status</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-comment-default-status") + "</string></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-ping-default-status") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>ping_status</name>" +
                                          "<value><string>" + jobSettings.get("wordpress-post-ping-default-status") + "</string></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-sticky") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>sticky</name>" +
                                          "<value><int>" + jobSettings.get("wordpress-post-sticky") + "</int></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-thumbnail-id") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_thumbnail</name>" +
                                          "<value><int>" + jobSettings.get("wordpress-post-thumbnail-id") + "</int></value>" +
                                        "</member>";
        }

        if (jobSettings.containsKey("wordpress-post-parent-id") == true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>post_parent</name>" +
                                          "<value><int>" + jobSettings.get("wordpress-post-parent-id") + "</int></value>" +
                                        "</member>";
        }

        Map<String, String> customFields = jobFileProcessor.GetCustomFields();
        
        if (customFields.isEmpty() != true)
        {
            xmlrpc +=                   "<member>" +
                                          "<name>custom_fields</name>" +
                                          "<value>" +
                                            "<array>" +
                                              "<data>";

            for (Map.Entry<String, String> entry : customFields.entrySet())
            {
                xmlrpc +=                       "<value>" +
                                                  "<struct>" +
                                                    "<member>" +
                                                      "<name>key</name>";

                String key = entry.getKey();

                // Ampersand needs to be the first, otherwise it would double-encode
                // other entities.
                key = key.replaceAll("&", "&amp;");
                key = key.replaceAll("\"", "&quot;");
                key = key.replaceAll("'", "&apos;");
                key = key.replaceAll("<", "&lt;");
                key = key.replaceAll(">", "&gt;");

                xmlrpc +=                             "<value><string>" + key + "</string></value>" +
                                                    "</member>" +
                                                    "<member>" +
                                                      "<name>value</name>";

                String value = entry.getValue();

                // Ampersand needs to be the first, otherwise it would double-encode
                // other entities.
                value = value.replaceAll("&", "&amp;");
                value = value.replaceAll("\"", "&quot;");
                value = value.replaceAll("'", "&apos;");
                value = value.replaceAll("<", "&lt;");
                value = value.replaceAll(">", "&gt;");

                xmlrpc +=                             "<value><string>" + value + "</string></value>" +
                                                    "</member>" +
                                                  "</struct>" +
                                                "</value>";
            }

            xmlrpc +=                         "</data>" +
                                            "</array>" +
                                          "</value>" +
                                        "</member>";
        }
          
        Map<String, ArrayList<String>> taxonomyHierarchy = jobFileProcessor.GetTaxonomyHierarchy();
        
        if (taxonomyHierarchy.isEmpty() != true)
        {                         
            xmlrpc +=                   "<member>" +
                                          "<name>terms</name>" +
                                          "<value>" +
                                            "<struct>";
                                            
            for (Map.Entry<String, ArrayList<String>> taxonomy : taxonomyHierarchy.entrySet())
            {
                xmlrpc +=                     "<member>";
                
                String name = taxonomy.getKey();

                // Ampersand needs to be the first, otherwise it would double-encode
                // other entities.
                name = name.replaceAll("&", "&amp;");
                name = name.replaceAll("\"", "&quot;");
                name = name.replaceAll("'", "&apos;");
                name = name.replaceAll("<", "&lt;");
                name = name.replaceAll(">", "&gt;");
                
                xmlrpc +=                       "<name>" + name + "</name>" +
                                                "<value>" +
                                                  "<array>" +
                                                    "<data>";

                for (String term : taxonomy.getValue())
                {
                    // Ampersand needs to be the first, otherwise it would double-encode
                    // other entities.
                    term = term.replaceAll("&", "&amp;");
                    term = term.replaceAll("\"", "&quot;");
                    term = term.replaceAll("'", "&apos;");
                    term = term.replaceAll("<", "&lt;");
                    term = term.replaceAll(">", "&gt;");
                
                    xmlrpc +=                         "<value><int>" + term + "</int></value>";
                }
                
                xmlrpc +=                           "</data>" +
                                                  "</array>" +
                                                "</value>" +
                                              "</member>";
            }
            
            xmlrpc +=                       "</struct>" +
                                          "</value>" +
                                        "</member>";
        }

        Map<String, ArrayList<String>> taxonomyTags = jobFileProcessor.GetTaxonomyTags();
        
        if (taxonomyTags.isEmpty() != true)
        {                         
            xmlrpc +=                   "<member>" +
                                          "<name>terms_names</name>" +
                                          "<value>" +
                                            "<struct>";
                                            
            for (Map.Entry<String, ArrayList<String>> taxonomy : taxonomyTags.entrySet())
            {
                xmlrpc +=                     "<member>";

                String name = taxonomy.getKey();

                // Ampersand needs to be the first, otherwise it would double-encode
                // other entities.
                name = name.replaceAll("&", "&amp;");
                name = name.replaceAll("\"", "&quot;");
                name = name.replaceAll("'", "&apos;");
                name = name.replaceAll("<", "&lt;");
                name = name.replaceAll(">", "&gt;");

                xmlrpc +=                       "<name>" + name + "</name>" +
                                                "<value>" +
                                                  "<array>" +
                                                    "<data>";

                for (String term : taxonomy.getValue())
                {
                    // Ampersand needs to be the first, otherwise it would double-encode
                    // other entities.
                    term = term.replaceAll("&", "&amp;");
                    term = term.replaceAll("\"", "&quot;");
                    term = term.replaceAll("'", "&apos;");
                    term = term.replaceAll("<", "&lt;");
                    term = term.replaceAll(">", "&gt;");

                    xmlrpc +=                         "<value><string>" + term + "</string></value>";
                }
                
                xmlrpc +=                           "</data>" +
                                                  "</array>" +
                                                "</value>" +
                                              "</member>";
            }
            
            xmlrpc +=                       "</struct>" +
                                          "</value>" +
                                        "</member>";
        }                          

        /*
        xmlrpc +=                       "<member>" +
                                          "<name>enclosure</name>" +
                                          "<value>" +
                                            "<array>" +
                                              "<data></data>" +
                                            "</array>" +
                                          "</value>" +
                                        "</member>";
        */

        xmlrpc +=                     "</struct>" +
                                    "</value>" +
                                  "</data>" +
                                "</array>" +
                              "</value>" +
                            "</param>" +
                          "</params>" +
                        "</methodCall>";


        String http = "POST " + xmlrpcPath + " HTTP/1.1\r\n" +
                      "Host: " + host + "\r\n" +
                      "Content-Type: text/xml\r\n" +
                      "User-Agent: html2wordpress1 (publishing-systems.org)\r\n";

        MessageDigest md = null;

        try
        {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        String hashBase = jobSettings.get("wordpress-user-private-key") + xmlrpc;

        try
        {
            md.update(hashBase.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        byte[] digest = md.digest();

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < digest.length; i++)
        {
            String hex = Integer.toHexString(0xFF & digest[i]);

            if (hex.length() == 1)
            {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        // Second time.

        hashBase = jobSettings.get("wordpress-user-private-key") + hexString;

        try
        {
            md.update(hashBase.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        digest = md.digest();

        hexString = new StringBuffer();

        for (int i = 0; i < digest.length; i++)
        {
            String hex = Integer.toHexString(0xFF & digest[i]);

            if (hex.length() == 1)
            {
                hexString.append('0');
            }

            hexString.append(hex);
        }


        String authorization = new String(jobSettings.get("wordpress-user-public-key") + "||" + hexString);
        http += "Authorization: " + authorization + "\r\n";

        try
        {
            // Bytes, not characters (might differ because of UTF-8 encoding).
            http += "Content-Length: " + xmlrpc.getBytes("UTF-8").length + "\r\n\r\n" + xmlrpc;
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }


        try
        {
            Socket socket = new Socket(host, 80);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(http.getBytes("UTF-8"));

            int length = 0;
            
            byte[] buffer = new byte[4096];
            
            while ((length = in.read(buffer)) != -1)
            {
                System.out.write(buffer, 0, length);
            }
            
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
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
                            System.out.print("html2wordpress1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
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
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html2wordpress1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
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
            System.out.print("html2wordpress1: Can't resolve entity, no local entities directory.\n");
            System.exit(-1);
        }
        
        if (this.configFile == null)
        {
            System.out.print("html2wordpress1: Can't resolve entity, no entities configured.\n");
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
            System.out.print("html2wordpress1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html2wordpress1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html2wordpress1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html2wordpress1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
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

