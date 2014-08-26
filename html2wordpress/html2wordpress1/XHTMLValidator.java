/* Copyright (C) 2014  Stephan Kreutzer
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
 * @file $/XHTMLValidator.java
 * @brief Validator for XHTML files.
 * @author Stephan Kreutzer
 * @since 2014-08-19
 */



import org.xml.sax.ErrorHandler;
import java.io.File;
import org.xml.sax.SAXParseException;
import org.xml.sax.EntityResolver;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.SAXParser;
import org.xml.sax.XMLReader;
import java.io.FileReader;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.BufferedReader;



class XHTMLValidator implements ErrorHandler
{
    public XHTMLValidator()
    {
    
    }
    
    public int Validate(File xhtmlFile)
    {
        String programPath = XHTMLValidator.class.getProtectionDomain().getCodeSource().getLocation().getFile();

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

        File schemataDirectory = new File(programPath + "schemata");

        if (schemataDirectory.exists() != true)
        {
            if (schemataDirectory.mkdir() != true)
            {
                System.out.print("html2wordpress1: Can't create schemata directory '" + schemataDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (schemataDirectory.isDirectory() != true)
            {
                System.out.print("html2wordpress1: Schemata path '" + schemataDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }

        String doctypeDeclaration = new String("<!DOCTYPE");
        int doctypePosMatching = 0;
        String doctype = new String();
    
        try
        {
            FileInputStream in = new FileInputStream(xhtmlFile);
            
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
        

        if (doctype.contains("\"-//W3C//DTD XHTML 1.0 Strict//EN\"") == true)
        {
            File schemaFile = new File(schemataDirectory.getAbsolutePath() + "/xhtml1-strict.xsd");

            if (schemaFile.exists() != true)
            {
                schemaFile = null;
            }

            if (schemaFile != null)
            {
                if (schemaFile.isFile() != true)
                {
                    schemaFile = null;
                }
            }

            if (schemaFile != null)
            {
                if (schemaFile.canRead() != true)
                {
                    schemaFile = null;
                }
            }
            
            if (schemaFile == null)
            {
                System.out.print("html2wordpress1: Can't validate XHTML 1.0 Strict file - schema 'xhtml1-strict.xsd' is missing.\n");
                System.exit(-1);
            }
            
            File entitiesConfigFile = new File(entitiesDirectory.getAbsolutePath() + "/config_xhtml1-strict.xml");
            
            if (entitiesConfigFile.exists() != true)
            {
                entitiesConfigFile = null;
            }

            if (entitiesConfigFile != null)
            {
                if (entitiesConfigFile.isFile() != true)
                {
                    entitiesConfigFile = null;
                }
            }

            if (entitiesConfigFile != null)
            {
                if (entitiesConfigFile.canRead() != true)
                {
                    entitiesConfigFile = null;
                }
            }
            
            if (entitiesConfigFile == null)
            {
                System.out.print("html2wordpress1: Can't validate XHTML 1.0 Strict file - entity resolver configuration file is missing.\n");
                System.exit(-1);
            }
            
            File schemataConfigFile = new File(schemataDirectory.getAbsolutePath() + "/config_xhtml1-strict.xml");
            
            if (schemataConfigFile.exists() != true)
            {
                schemataConfigFile = null;
            }

            if (schemataConfigFile != null)
            {
                if (schemataConfigFile.isFile() != true)
                {
                    schemataConfigFile = null;
                }
            }

            if (schemataConfigFile != null)
            {
                if (schemataConfigFile.canRead() != true)
                {
                    schemataConfigFile = null;
                }
            }
            
            if (schemataConfigFile == null)
            {
                System.out.print("html2wordpress1: Can't validate XHTML 1.0 Strict file - schema resolver configuration file is missing.\n");
                System.exit(-1);
            }


            try
            {
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                // Disable validating by DTD.
                parserFactory.setValidating(false); 
                parserFactory.setNamespaceAware(true);

                SchemaEntityResolverLocal localResolver = new SchemaEntityResolverLocal(programPath, entitiesConfigFile, entitiesDirectory, schemataConfigFile, schemataDirectory);

                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schemaFactory.setResourceResolver(localResolver);
                
                Source schemaSource = new StreamSource(schemaFile);
                parserFactory.setSchema(schemaFactory.newSchema(new Source[] { schemaSource }));
                
                SAXParser parser = parserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setErrorHandler(this);
                reader.setEntityResolver(localResolver);

                // Do XML Schema validation.
                reader.parse(new InputSource(new FileReader(xhtmlFile)));
            }
            catch (SAXException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
            catch (ParserConfigurationException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
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
        }
        else if (doctype.contains("\"-//W3C//DTD XHTML 1.1//EN\"") == true)
        {
            /*
            File schemaFile = new File(programPath + "xhtml11.xsd");

            if (schemaFile.exists() != true)
            {
                schemaFile = null;
            }

            if (schemaFile != null)
            {
                if (schemaFile.isFile() != true)
                {
                    schemaFile = null;
                }
            }

            if (schemaFile != null)
            {
                if (schemaFile.canRead() != true)
                {
                    schemaFile = null;
                }
            }
            
            if (schemaFile == null)
            {
                System.out.print("html2wordpress1: Can't validate XHTML 1.1 file - schema 'xhtml11.xsd' is missing.\n");
                System.exit(-1);
            }
            
            File configFile = new File(entitiesDirectory.getAbsolutePath() + "/config_xhtml1_1.xml");
            
            if (configFile.exists() != true)
            {
                configFile = null;
            }

            if (configFile != null)
            {
                if (configFile.isFile() != true)
                {
                    configFile = null;
                }
            }

            if (configFile != null)
            {
                if (configFile.canRead() != true)
                {
                    configFile = null;
                }
            }
            
            if (configFile == null)
            {
                System.out.print("html2wordpress1: Can't validate XHTML 1.1 file - entity resolver configuration file is missing.\n");
                System.exit(-1);
            }
            
            try
            {
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                // Disable validating by DTD.
                parserFactory.setValidating(false); 
                parserFactory.setNamespaceAware(true);

                SchemaEntityResolverLocal localResolver = new SchemaEntityResolverLocal(programPath, configFile, entitiesDirectory);

                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schemaFactory.setResourceResolver(localResolver);
                
                Source schemaSource = new StreamSource(schemaFile);
                parserFactory.setSchema(schemaFactory.newSchema(new Source[] { schemaSource }));
                
                SAXParser parser = parserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setErrorHandler(this);
                reader.setEntityResolver(localResolver);

                // Do XML Schema validation.
                reader.parse(new InputSource(new FileReader(xhtmlFile)));
            }
            catch (SAXException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
            catch (ParserConfigurationException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
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
            */

            System.out.print("html2epub1: Can't validate XHTML 1.1 file - XHTML 1.1 validation not supported yet.\n");
            System.exit(-1);
        }
        else
        {
            System.out.print("html2wordpress1: Unknown XHTML version, can't validate.\n");
            System.exit(-1);
        }

        return 0;
    }

    public void warning(SAXParseException ex)
    {
        ex.printStackTrace();
        System.exit(-1);
    }

    public void error(SAXParseException ex)
    {
        ex.printStackTrace();
        System.exit(-1);
    }

    public void fatalError(SAXParseException ex)
    {
        ex.printStackTrace();
        System.exit(-1);
    }
}

class SchemaEntityResolverLocal implements EntityResolver, LSResourceResolver
{
    public SchemaEntityResolverLocal(String localPath, File entitiesConfigFile, File entitiesDirectory, File schemataConfigFile, File schemataDirectory)
    {
        this.localPath = localPath;
        this.entitiesConfigFile = entitiesConfigFile;
        this.entitiesDirectory = entitiesDirectory;
        this.schemataConfigFile = schemataConfigFile;
        this.schemataDirectory = schemataDirectory;
        this.localEntities = new HashMap<String, File>();
        this.localSchemata = new HashMap<String, File>();
        
        boolean success = true;

        if (success == true)
        {
            success = this.entitiesConfigFile.exists();
        }

        if (success == true)
        {
            success = this.entitiesConfigFile.isFile();
        }

        if (success == true)
        {
            success = this.entitiesConfigFile.canRead();
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
            success = this.schemataDirectory.exists();
        }

        if (success == true)
        {
            success = this.schemataDirectory.isDirectory();
        }

        if (success != true)
        {
            this.schemataDirectory = null;
        }

        if (success == true)
        {
            Document document = null;

            try
            {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(this.entitiesConfigFile);
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
                            System.out.print("html2wordpress1: '" + this.entitiesConfigFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
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
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.entitiesConfigFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.entitiesConfigFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.entitiesConfigFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html2wordpress1: Identifier '" + identifier + "' configured twice in '" + this.entitiesConfigFile.getAbsolutePath() + "'.\n");
                            System.exit(-1);
                        }
                    }
                }
            }


            document = null;
        
            try
            {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(this.schemataConfigFile);
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


            NodeList schemaNodeList = document.getElementsByTagName("schema");
            int schemaNodeListCount = schemaNodeList.getLength();

            for (int i = 0; i < schemaNodeListCount; i++)
            {
                Node schemaNode = schemaNodeList.item(i);
                NodeList schemaChildNodeList = schemaNode.getChildNodes();
                int schemaChildNodeListCount = schemaChildNodeList.getLength();

                for (int j = 0; j < schemaChildNodeListCount; j++)
                {
                    Node schemaChildNode = schemaChildNodeList.item(j);

                    if (schemaChildNode.getNodeName().equalsIgnoreCase("resolve") == true)
                    {
                        Element element = (Element) schemaChildNode;
                        String identifier = element.getAttribute("identifier");
                        String reference = element.getAttribute("reference");
                        File referencedFile = new File(reference);

                        if (identifier.length() <= 0)
                        {
                            System.out.print("html2wordpress1: '" + this.schemataConfigFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isAbsolute() != true)
                        {
                            String relativePath = this.schemataDirectory.getAbsolutePath();
                            
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
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.schemataConfigFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.schemataConfigFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("html2wordpress1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.schemataConfigFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localSchemata.containsKey(identifier) != true)
                        {
                            this.localSchemata.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("html2wordpress1: Identifier '" + identifier + "' configured twice in '" + this.schemataConfigFile.getAbsolutePath() + "'.\n");
                            System.exit(-1);
                        }
                    }
                }
            }
        }
    }

    public LSInput resolveResource(String type,
                                   String namespaceURI,
                                   String publicId,
                                   String systemId,
                                   String baseURI)
    {
        if (this.schemataDirectory == null)
        {
            System.out.print("html2wordpress1: Can't resolve schema, no local schemata directory.\n");
            System.exit(-1);
        }

        if (this.schemataConfigFile == null)
        {
            System.out.print("html2wordpress1: Can't resolve schema, no schemata configured.\n");
            System.exit(-1);
        }

        File localSchema = null;
    
        if (localSchema == null)
        {
            if (this.localSchemata.containsKey(publicId) == true)
            {
                localSchema = this.localSchemata.get(publicId);
            }
        }
        
        if (localSchema == null)
        {
            if (this.localSchemata.containsKey(systemId) == true)
            {
                localSchema = this.localSchemata.get(systemId);
            }
        }
        
        if (localSchema == null)
        {
            System.out.print("html2wordpress1: Can't resolve schema with type '" + type + "', namespace URI '" + namespaceURI + "', public ID '" + publicId + "', system ID '" + systemId + "', base URI '" + baseURI + "', no local copy configured in '" + this.schemataConfigFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localSchema.exists() != true)
        {
            System.out.print("html2wordpress1: '" + localSchema.getAbsolutePath() + "', referenced in '" + this.schemataConfigFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localSchema.isFile() != true)
        {
            System.out.print("html2wordpress1: '" + localSchema.getAbsolutePath() + "', referenced in '" + this.schemataConfigFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localSchema.canRead() != true)
        {
            System.out.print("html2wordpress1: '" + localSchema.getAbsolutePath() + "', referenced in '" + this.schemataConfigFile.getAbsolutePath() + "', isn't readable.\n");
            System.exit(-1);
        }
        
        if (localSchema.exists() == true)
        {
            return new LocalSchemaInput(localSchema, publicId, true);
        }
        else
        {
            System.out.print("html2wordpress1: Can't validate - unknown resource with public ID '" + publicId + "', system ID '" + systemId + "', type '" + type + "', namespace URI '" + namespaceURI + "' and base URI '" + baseURI + "'.\n");
            System.exit(-1);
            return null;
        }
        
    
        /*
        // For XHTML 1.0 Strict (should go into a schema directory with its own schema config file).
    
        if (systemId.equalsIgnoreCase("http://www.w3.org/2001/xml.xsd") == true)
        {
            File xml = new File(this.localPath + "xml.xsd");
            
            if (xml.exists() == true)
            {
                return new LocalEntityInput(xml, publicId, true);
            }
            else
            {
                System.out.print("html2wordpress1: Can't validate - schema 'xml.xsd' is missing.\n");
                System.exit(-1);
                return null;
            }
        }
        */
        /*
        
        XHTML 1.1 (should go into a schema directory with its own schema config file).
        
        else if (systemId.equalsIgnoreCase("xhtml11-model-1.xsd") == true)
        {
            File xhtml11Model = new File(this.localPath + "xhtml11-model-1.xsd");
            
            if (xhtml11Model.exists() == true)
            {
                return new LocalEntityInput(xhtml11Model, publicId, true);
            }
            else
            {
                System.out.print("html2wordpress1: Can't validate - schema 'xhtml11-model-1.xsd' is missing.\n");
                System.exit(-1);
                return null;
            }
        }
        else if (systemId.equalsIgnoreCase("http://www.w3.org/MarkUp/SCHEMA/xhtml-datatypes-1.xsd") == true)
        {
            File xhtmlDatatypes = new File(this.localPath + "xhtml-datatypes-1.xsd");
            
            if (xhtmlDatatypes.exists() == true)
            {
                return new LocalEntityInput(xhtmlDatatypes, publicId, true);
            }
            else
            {
                System.out.print("html2wordpress1: Can't validate - schema 'xhtml-datatypes-1.xsd' is missing.\n");
                System.exit(-1);
                return null;
            }
        }
        else if (systemId.equalsIgnoreCase("xhtml11-modules-1.xsd") == true)
        {
            File xhtml11Modules = new File(this.localPath + "xhtml11-modules-1.xsd");
            
            if (xhtml11Modules.exists() == true)
            {
                return new LocalEntityInput(xhtml11Modules, publicId, true);
            }
            else
            {
                System.out.print("html2wordpress1: Can't validate - schema 'xhtml11-modules-1.xsd' is missing.\n");
                System.exit(-1);
                return null;
            }
        }
        */
    }

    public InputSource resolveEntity(String publicId, String systemId)
    {
        if (this.entitiesDirectory == null)
        {
            System.out.print("html2wordpress1: Can't resolve entity, no local entities directory.\n");
            System.exit(-1);
        }
        
        if (this.entitiesConfigFile == null)
        {
            System.out.print("html2wordpress1: Can't resolve entity, no entities configured.\n");
            System.exit(-1);
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
            System.out.print("html2wordpress1: Can't resolve entity with public ID '" + publicId + "', system ID '" + systemId + "', no local copy configured in '" + this.entitiesConfigFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("html2wordpress1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.entitiesConfigFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("html2wordpress1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.entitiesConfigFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("html2wordpress1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.entitiesConfigFile.getAbsolutePath() + "', isn't readable.\n");
            System.exit(-1);
        }
        
        try
        {
            InputSource inputSource = new InputSource(new BufferedReader(new FileReader(localEntity)));
            return inputSource;
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        return null;
    }

    protected String localPath;
    protected File entitiesConfigFile;
    protected File entitiesDirectory;
    protected File schemataConfigFile;
    protected File schemataDirectory;
    protected Map<String, File> localEntities;
    protected Map<String, File> localSchemata;
}

// This implementation provides access to files which are already
// locally resolved, so setters are either ignored because the
// corresponding getters are irrelevant or their call causes an
// error, if it would change the reference to the locally resolved file.
class LocalEntityInput implements LSInput
{
    public LocalEntityInput(File localFile, String publicId, boolean certifiedText)
    {
        this.localFile = localFile;
        this.publicId = publicId;
        this.certifiedText = certifiedText;
    }

    public String getBaseURI()
    {
        return null;
    }
    
    public void setBaseURI(String baseURI)
    {
    
    }
    
    public InputStream getByteStream()
    {
        return null;
    }
    
    public void setByteStream(InputStream byteStream)
    {
        System.out.println("html2wordpress1: Calling LocalEntityInput.setByteStream() isn't allowed.");
        System.exit(-1);
    }

    public boolean getCertifiedText()
    {
        return this.certifiedText;
    }
    
    public void setCertifiedText(boolean certifiedText)
    {
        this.certifiedText = certifiedText;
    }
    
    public Reader getCharacterStream()
    {
        Reader reader = null;
    
        try
        {
            reader = new FileReader(this.localFile);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        return reader;
    }
    
    public void setCharacterStream(Reader characterStream)
    {
        System.out.println("html2wordpress1: Calling LocalEntityInput.setCharacterStream() isn't allowed.");
        System.exit(-1);
    }
    
    public String getEncoding()
    {
        return null;
    }
    
    public void setEncoding(String encoding)
    {
    
    }
    
    public String getPublicId()
    {
        return this.publicId;
    }
    
    public void setPublicId(String publicId)
    {
        System.out.println("html2wordpress1: Calling LocalEntityInput.setPublicId() isn't allowed.");
        System.exit(-1);
    }
    
    public String getStringData()
    {
        return null;
    }
    
    public void setStringData(String stringData)
    {
        System.out.println("html2wordpress11: Calling LocalEntityInput.setStringData() isn't allowed.");
        System.exit(-1);
    }
    
    public String getSystemId()
    {
        return this.localFile.getAbsolutePath();
    }
    
    public void setSystemId(String systemId)
    {
        System.out.println("html2wordpress1: Calling LocalEntityInput.setSystemId() isn't allowed.");
        System.exit(-1);
    }

    protected File localFile;
    protected String publicId;
    protected boolean certifiedText;
}

// This implementation provides access to files which are already
// locally resolved, so setters are either ignored because the
// corresponding getters are irrelevant or their call causes an
// error, if it would change the reference to the locally resolved file.
class LocalSchemaInput implements LSInput
{
    public LocalSchemaInput(File localFile, String publicId, boolean certifiedText)
    {
        this.localFile = localFile;
        this.publicId = publicId;
        this.certifiedText = certifiedText;
    }

    public String getBaseURI()
    {
        return null;
    }
    
    public void setBaseURI(String baseURI)
    {
    
    }
    
    public InputStream getByteStream()
    {
        return null;
    }
    
    public void setByteStream(InputStream byteStream)
    {
        System.out.println("html2wordpress1: Calling LocalSchemaInput.setByteStream() isn't allowed.");
        System.exit(-1);
    }

    public boolean getCertifiedText()
    {
        return this.certifiedText;
    }
    
    public void setCertifiedText(boolean certifiedText)
    {
        this.certifiedText = certifiedText;
    }
    
    public Reader getCharacterStream()
    {
        Reader reader = null;
    
        try
        {
            reader = new FileReader(this.localFile);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        return reader;
    }
    
    public void setCharacterStream(Reader characterStream)
    {
        System.out.println("html2wordpress1: Calling LocalSchemaInput.setCharacterStream() isn't allowed.");
        System.exit(-1);
    }
    
    public String getEncoding()
    {
        return null;
    }
    
    public void setEncoding(String encoding)
    {
    
    }
    
    public String getPublicId()
    {
        return this.publicId;
    }
    
    public void setPublicId(String publicId)
    {
        System.out.println("html2wordpress1: Calling LocalSchemaInput.setPublicId() isn't allowed.");
        System.exit(-1);
    }
    
    public String getStringData()
    {
        return null;
    }
    
    public void setStringData(String stringData)
    {
        System.out.println("html2wordpress11: Calling LocalSchemaInput.setStringData() isn't allowed.");
        System.exit(-1);
    }
    
    public String getSystemId()
    {
        return this.localFile.getAbsolutePath();
    }
    
    public void setSystemId(String systemId)
    {
        System.out.println("html2wordpress1: Calling LocalSchemaInput.setSystemId() isn't allowed.");
        System.exit(-1);
    }

    protected File localFile;
    protected String publicId;
    protected boolean certifiedText;
}

