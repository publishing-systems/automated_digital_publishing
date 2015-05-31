/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of html2epub2.
 *
 * html2epub2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub2. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/XMLSchemaValidator.java
 * @brief XML schema validator.
 * @author Stephan Kreutzer
 * @since 2015-02-02
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.BufferedReader;



class XMLSchemaValidator implements ErrorHandler
{
    public XMLSchemaValidator()
    {
    
    }

    public int Validate(File inFile, File entitiesResolverConfigFile, File schemaFile, File schemataResolverConfigFile)
    {
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            // Disable validating by DTD.
            parserFactory.setValidating(false); 
            parserFactory.setNamespaceAware(true);

            SchemaEntityResolverLocal localResolver = new SchemaEntityResolverLocal(entitiesResolverConfigFile, schemataResolverConfigFile);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(localResolver);

            Source schemaSource = new StreamSource(schemaFile);
            parserFactory.setSchema(schemaFactory.newSchema(new Source[] { schemaSource }));
            
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(this);
            reader.setEntityResolver(localResolver);

            // Do XML Schema validation.
            reader.parse(new InputSource(new FileReader(inFile)));
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
    public SchemaEntityResolverLocal(File entitiesResolverConfigFile, File schemataResolverConfigFile)
    {
        this.entitiesResolverConfigFile = entitiesResolverConfigFile;
        this.schemataResolverConfigFile = schemataResolverConfigFile;
        this.localEntities = new HashMap<String, File>();
        this.localSchemata = new HashMap<String, File>();
        
        if (this.entitiesResolverConfigFile == null)
        {
            System.out.println("html2epub2: No entities resolver configuration file specified.");
            System.exit(-1);
        }

        if (this.entitiesResolverConfigFile.exists() != true)
        {
            System.out.println("html2epub2: Entities resolver configuration file '" + this.entitiesResolverConfigFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (this.entitiesResolverConfigFile.isFile() != true)
        {
            System.out.println("html2epub2: Entities resolver configuration path '" + this.entitiesResolverConfigFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (this.entitiesResolverConfigFile.canRead() != true)
        {
            System.out.print("html2epub2: Entities resolver configuration file '" + this.entitiesResolverConfigFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        {
            Document document = null;

            try
            {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(this.entitiesResolverConfigFile);
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
                            System.out.println("html2epub2: '" + this.entitiesResolverConfigFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isAbsolute() != true)
                        {
                            referencedFile = new File(this.entitiesResolverConfigFile.getAbsoluteFile().getParent() + File.separator + reference);
                        }
                        
                        if (referencedFile.exists() != true)
                        {
                            System.out.println("html2epub2: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "', doesn't exist.");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.println("html2epub2: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "', isn't a file.");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.println("html2epub2: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "', isn't readable.");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.println("html2epub2: Identifier '" + identifier + "' configured twice in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "'.");
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
                document = documentBuilder.parse(this.schemataResolverConfigFile);
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
                            System.out.println("html2epub2: '" + this.schemataResolverConfigFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isAbsolute() != true)
                        {
                            referencedFile = new File(this.schemataResolverConfigFile.getAbsoluteFile().getParent() + File.separator + reference);
                        }
                        
                        if (referencedFile.exists() != true)
                        {
                            System.out.println("html2epub2: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.schemataResolverConfigFile.getAbsolutePath() + "', doesn't exist.");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.println("html2epub2: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.schemataResolverConfigFile.getAbsolutePath() + "', isn't a file.");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.println("html2epub2: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.schemataResolverConfigFile.getAbsolutePath() + "', isn't readable.");
                            System.exit(-1);
                        }
                        
                        if (this.localSchemata.containsKey(identifier) != true)
                        {
                            this.localSchemata.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.println("html2epub2: Identifier '" + identifier + "' configured twice in '" + this.schemataResolverConfigFile.getAbsolutePath() + "'.");
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
            System.out.println("html2epub2: Can't resolve schema with type '" + type + "', namespace URI '" + namespaceURI + "', public ID '" + publicId + "', system ID '" + systemId + "', base URI '" + baseURI + "', no local copy configured in '" + this.schemataResolverConfigFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
    
        if (localSchema.exists() != true)
        {
            System.out.println("html2epub2: '" + localSchema.getAbsolutePath() + "', referenced in '" + this.schemataResolverConfigFile.getAbsolutePath() + "', doesn't exist.");
            System.exit(-1);
        }
        
        if (localSchema.isFile() != true)
        {
            System.out.println("html2epub2: '" + localSchema.getAbsolutePath() + "', referenced in '" + this.schemataResolverConfigFile.getAbsolutePath() + "', isn't a file.");
            System.exit(-1);
        }
        
        if (localSchema.canRead() != true)
        {
            System.out.println("html2epub2: '" + localSchema.getAbsolutePath() + "', referenced in '" + this.schemataResolverConfigFile.getAbsolutePath() + "', isn't readable.");
            System.exit(-1);
        }

        return new LocalSchemaInput(localSchema, publicId, true);
    }

    public InputSource resolveEntity(String publicId, String systemId)
    {
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
            System.out.println("html2epub2: Can't resolve entity with public ID '" + publicId + "', system ID '" + systemId + "', no local copy configured in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.println("html2epub2: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "', doesn't exist.");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.println("html2epub2: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "', isn't a file.");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.println("html2epub2: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.entitiesResolverConfigFile.getAbsolutePath() + "', isn't readable.");
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

    protected File entitiesResolverConfigFile;
    protected File schemataResolverConfigFile;
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
        System.out.println("html2epub2: Calling LocalEntityInput.setByteStream() isn't allowed.");
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
        System.out.println("html2epub2: Calling LocalEntityInput.setCharacterStream() isn't allowed.");
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
        System.out.println("html2epub2: Calling LocalEntityInput.setPublicId() isn't allowed.");
        System.exit(-1);
    }
    
    public String getStringData()
    {
        return null;
    }
    
    public void setStringData(String stringData)
    {
        System.out.println("html2epub2: Calling LocalEntityInput.setStringData() isn't allowed.");
        System.exit(-1);
    }
    
    public String getSystemId()
    {
        return this.localFile.getAbsolutePath();
    }
    
    public void setSystemId(String systemId)
    {
        System.out.println("html2epub2: Calling LocalEntityInput.setSystemId() isn't allowed.");
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
        System.out.println("html2epub2: Calling LocalSchemaInput.setByteStream() isn't allowed.");
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
        System.out.println("html2epub2: Calling LocalSchemaInput.setCharacterStream() isn't allowed.");
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
        System.out.println("html2epub2: Calling LocalSchemaInput.setPublicId() isn't allowed.");
        System.exit(-1);
    }
    
    public String getStringData()
    {
        return null;
    }
    
    public void setStringData(String stringData)
    {
        System.out.println("html2epub2: Calling LocalSchemaInput.setStringData() isn't allowed.");
        System.exit(-1);
    }
    
    public String getSystemId()
    {
        return this.localFile.getAbsolutePath();
    }
    
    public void setSystemId(String systemId)
    {
        System.out.println("html2epub2: Calling LocalSchemaInput.setSystemId() isn't allowed.");
        System.exit(-1);
    }

    protected File localFile;
    protected String publicId;
    protected boolean certifiedText;
}

