/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of html2epub1.
 *
 * html2epub1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/XHTMLValidator.java
 * @brief Validator for XHTML files.
 * @author Stephan Kreutzer
 * @since 2014-02-01
 */



import org.xml.sax.ErrorHandler;
import java.io.File;
import org.xml.sax.SAXParseException;
import org.xml.sax.EntityResolver;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
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
import java.io.BufferedReader;



class XHTMLValidator implements ErrorHandler
{
    public XHTMLValidator()
    {
    
    }
    
    public int validate(File xhtmlFile)
    {
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
            System.exit(-19);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-20);
        }
        
        
        String programPath = XHTMLValidator.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        if (doctype.contains("\"-//W3C//DTD XHTML 1.0 Strict//EN\"") == true)
        {
            File schemaFile = new File(programPath + "xhtml1-strict.xsd");

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
            
            if (schemaFile != null)
            {
                try
                {
                    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    // Disable validating by DTD.
                    parserFactory.setValidating(false); 
                    parserFactory.setNamespaceAware(true);

                    EntityResolverLocal localResolver = new EntityResolverLocal(programPath);

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
                    System.exit(-2);
                }
                catch (ParserConfigurationException ex)
                {
                    ex.printStackTrace();
                    System.exit(-3);
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-24);
                }
            }
            else
            {
                System.out.print("html2epub1: Can't validate XHTML 1.0 file - schema 'xhtml1-strict.xsd' is missing.\n");
                System.exit(-21);
            }
        }
        else if (doctype.contains("\"-//W3C//DTD XHTML 1.1//EN\"") == true)
        {
            System.out.print("html2epub1: Can't validate XHTML 1.1 file - XHTML 1.1 validation not supported yet.\n");
            System.exit(-22);
        }
        else
        {
            System.out.print("html2epub1: Unknown XHTML version, can't validate.\n");
            System.exit(-23);
        }

        return 0;
    }

    public void warning(SAXParseException ex)
    {
        ex.printStackTrace();
        System.exit(-80);
    }

    public void error(SAXParseException ex)
    {
        ex.printStackTrace();
        System.exit(-81);
    }

    public void fatalError(SAXParseException ex)
    {
        ex.printStackTrace();
        System.exit(-82);
    }
}

class EntityResolverLocal implements EntityResolver, LSResourceResolver
{
    public EntityResolverLocal(String localPath)
    {
        this.localPath = localPath;
    }

    public LSInput resolveResource(String type,
                                   String namespaceURI,
                                   String publicId,
                                   String systemId,
                                   String baseURI)
    {
        if (systemId.equalsIgnoreCase("http://www.w3.org/2001/xml.xsd") == true)
        {
            File xml = new File(this.localPath + "xml.xsd");
            
            if (xml.exists() == true)
            {
                return new LocalEntityInput(xml, publicId, true);
            }
            else
            {
                System.out.print("html2epub1: Can't validate - schema 'xml.xsd' is missing.\n");
                System.exit(-83);
                return null;
            }
        }
    
        System.out.print("html2epub1: Can't validate - unknown resource with public ID '" + publicId + "', system ID '" + systemId + "', type '" + type + "', namespace URI '" + namespaceURI + "' and base URI '" + baseURI + "'.\n");
        System.exit(-84);
        return null;
    }

    public InputSource resolveEntity(String publicId, String systemId)
    {
        if (publicId.equalsIgnoreCase("-//W3C//DTD XHTML 1.0 Strict//EN") == true ||
            systemId.equalsIgnoreCase("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd") == true)
        {
            File dtd = new File(this.localPath + "xhtml1-strict.dtd");
            
            if (dtd.exists() == true)
            {
                try
                {
                    InputSource inputSource = new InputSource(new BufferedReader(new FileReader(dtd)));
                    return inputSource;
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    System.exit(-85);
                }
            }
            else
            {
                System.out.print("html2epub1: Can't validate - 'xhtml1-strict.dtd' is missing, which is required by the Schema mechanism in order to be able to read XHTMLs.\n");
                System.exit(-86);
                return null;
            }
        }
        else if (publicId.equalsIgnoreCase("-//W3C//ENTITIES Latin 1 for XHTML//EN") == true)
        {
            File latin1Entities = new File(this.localPath + "xhtml-lat1.ent");
            
            if (latin1Entities.exists() == true)
            {
                try
                {
                    InputSource inputSource = new InputSource(new BufferedReader(new FileReader(latin1Entities)));
                    return inputSource;
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    System.exit(-87);
                }
            }
            else
            {
                System.out.print("html2epub1: Can't validate - 'xhtml-lat1.ent' is missing, which is required by the Schema mechanism in order to be able to read XHTMLs.\n");
                System.exit(-88);
                return null;
            }
        }
        else if (publicId.equalsIgnoreCase("-//W3C//ENTITIES Symbols for XHTML//EN") == true)
        {
            File symbolEntities = new File(this.localPath + "xhtml-symbol.ent");
            
            if (symbolEntities.exists() == true)
            {
                try
                {
                    InputSource inputSource = new InputSource(new BufferedReader(new FileReader(symbolEntities)));
                    return inputSource;
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    System.exit(-89);
                }
            }
            else
            {
                System.out.print("html2epub1: Can't validate - 'xhtml-symbol.ent' is missing, which is required by the Schema mechanism in order to be able to read XHTMLs.\n");
                System.exit(-90);
                return null;
            }
        }
        else if (publicId.equalsIgnoreCase("-//W3C//ENTITIES Special for XHTML//EN") == true)
        {
            File specialEntities = new File(this.localPath + "xhtml-special.ent");
            
            if (specialEntities.exists() == true)
            {
                try
                {
                    InputSource inputSource = new InputSource(new BufferedReader(new FileReader(specialEntities)));
                    return inputSource;
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    System.exit(-91);
                }
            }
            else
            {
                System.out.print("html2epub1: Can't validate - 'xhtml-special.ent' is missing, which is required by the Schema mechanism in order to be able to read XHTMLs.\n");
                System.exit(-92);
                return null;
            }
        }
        
        System.out.print("html2epub1: Can't validate - unknown resource with public ID '" + publicId + "' and system ID '" + systemId + "'.\n");
        System.exit(-93);
        return null;
    }
    
    protected String localPath;
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
        System.out.println("html2epub1: Calling LocalEntityInput.setByteStream() isn't allowed.");
        System.exit(-94);
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
            System.exit(-95);
        }
        
        return reader;
    }
    
    public void setCharacterStream(Reader characterStream)
    {
        System.out.println("html2epub1: Calling LocalEntityInput.setCharacterStream() isn't allowed.");
        System.exit(-96);
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
        System.out.println("html2epub1: Calling LocalEntityInput.setPublicId() isn't allowed.");
        System.exit(-97);
    }
    
    public String getStringData()
    {
        return null;
    }
    
    public void setStringData(String stringData)
    {
        System.out.println("html2epub1: Calling LocalEntityInput.setStringData() isn't allowed.");
        System.exit(-98);
    }
    
    public String getSystemId()
    {
        return this.localFile.getAbsolutePath();
    }
    
    public void setSystemId(String systemId)
    {
        System.out.println("html2epub1: Calling LocalEntityInput.setSystemId() isn't allowed.");
        System.exit(-99);
    }
    

    protected File localFile;
    protected String publicId;
    protected boolean certifiedText;
}

