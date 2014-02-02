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



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;



class XHTMLValidator
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
                    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                    documentFactory.setNamespaceAware(true);
                    DocumentBuilder builder = documentFactory.newDocumentBuilder();

                    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Source schemaSource = new StreamSource(schemaFile);
                    Schema schema = schemaFactory.newSchema(schemaSource);
                    Validator validator = schema.newValidator();

                    Document document = builder.parse(xhtmlFile);
                    validator.validate(new DOMSource(document));
                }
                catch (ParserConfigurationException ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                catch (SAXException ex)
                {
                    ex.printStackTrace();
                    System.exit(-2);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-3);
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
}

