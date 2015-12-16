/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of xsltransformator1.
 *
 * xsltransformator1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * xsltransformator1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with xsltransformator1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/xsltransformator1.java
 * @brief Java command line tool for using a Java XSLT processor.
 * @author Stephan Kreutzer
 * @since 2014-03-29
 */



import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.util.Map;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class xsltransformator1
{
    public static void main(String args[])
    {
        System.out.print("xsltransformator1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\txsltransformator1 in-file stylesheet-file out-file\n\n");

            System.exit(1);
        }


        String programPath = xsltransformator1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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
                System.out.print("xsltransformator1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-9);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("xsltransformator1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-10);
            }
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("xsltransformator1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("xsltransformator1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("xsltransformator1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }
        
        File stylesheetFile = new File(args[1]);

        if (stylesheetFile.exists() != true)
        {
            System.out.print("xsltransformator1: '" + stylesheetFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (stylesheetFile.isFile() != true)
        {
            System.out.print("xsltransformator1: '" + stylesheetFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (stylesheetFile.canRead() != true)
        {
            System.out.print("xsltransformator1: '" + stylesheetFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }
        
        File outFile = new File(args[2]);


        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(false); 
            parserFactory.setNamespaceAware(true);

            EntityResolverLocal localResolver = new EntityResolverLocal(entitiesDirectory);

            SAXParser parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            //reader.setErrorHandler(this);
            reader.setEntityResolver(localResolver);
            
            SAXSource inSource = new SAXSource(reader, new InputSource(inFile.getAbsolutePath()));
            Source stylesheetSource = new StreamSource(stylesheetFile);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(stylesheetSource);
            /** @todo transformer.setOutputProperty(): http://docs.oracle.com/javase/7/docs/api/javax/xml/transform/Transformer.html#setOutputProperty%28java.lang.String,%20java.lang.String%29 */
            
            transformer.transform(inSource, new StreamResult(outFile));
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-11);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-12);
        }
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-7);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-8);
        }

        System.exit(0);
    }
}

class EntityResolverLocal implements EntityResolver
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
            this.configFile = new File(this.entitiesDirectory.getAbsolutePath() + File.separator + "config.xml");
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
                System.exit(-13);
            }
            catch (SAXException ex)
            {
                ex.printStackTrace();
                System.exit(-14);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-15);
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
                            System.out.print("xsltransformator1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
                            System.exit(-16);
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
                            System.out.print("xsltransformator1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-17);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("xsltransformator1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-18);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("xsltransformator1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-19);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("xsltransformator1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
                            System.exit(-20);
                        }
                    }
                }
            }
        }
        else
        {
            this.configFile = null;
        }
    }

    public InputSource resolveEntity(String publicId, String systemId)
    {
        if (this.entitiesDirectory == null)
        {
            System.out.print("xsltransformator1: Can't resolve entity, no local entities directory.\n");
            System.exit(-21);
        }
        
        if (this.configFile == null)
        {
            System.out.print("xsltransformator1: Can't resolve entity, no entities configured.\n");
            System.exit(-22);
        }
    
        File localEntity = null;
    
        if (localEntity == null)
        {
            if (this.localEntities.containsKey(publicId) == true)
            {
                localEntity = this.localEntities.get(publicId);
            }
        }
        
        if (localEntity == null)
        {
            if (this.localEntities.containsKey(systemId) == true)
            {
                localEntity = this.localEntities.get(systemId);
            }
        }
        
        if (localEntity == null)
        {
            System.out.print("xsltransformator1: Can't resolve entity with public ID '" + publicId + "', system ID '" + systemId + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-23);                  
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("xsltransformator1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-24);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("xsltransformator1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-25);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("xsltransformator1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
            System.exit(-26);
        }
        
        InputSource inputSource = null;
        
        try
        {
            inputSource = new InputSource(new BufferedReader(new FileReader(localEntity)));
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-27);
        }
        
        return inputSource;
    }
    
    protected File entitiesDirectory;
    protected File configFile;
    protected Map<String, File> localEntities;
}
