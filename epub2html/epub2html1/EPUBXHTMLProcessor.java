/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of epub2html1.
 *
 * epub2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epub2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epub2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/EPUBXHTMLProcessor.java
 * @brief Processor for XHTML files.
 * @author Stephan Kreutzer
 * @since 2014-07-12
 */



import java.io.File;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.stream.XMLResolver;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.events.Namespace;



class EPUBXHTMLProcessor
{
    public EPUBXHTMLProcessor(File inFile)
    {
        this.inFile = inFile;
    }

    public ReferencedFiles GetReferencedFiles(File xhtmlFile)
    {
        /**
         * @todo Maybe check besides exception handler, if in file
         *     is existing, readable, etc.?
         */

        String programPath = EPUBXHTMLProcessor.class.getProtectionDomain().getCodeSource().getLocation().getFile();

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
                System.out.print("epub2html1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("epub2html1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }

        ReferencedFiles referencedFiles = new ReferencedFiles();

        try
        {
            EntityResolverLocal localResolver = new EntityResolverLocal(entitiesDirectory);
        
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(localResolver);
            InputStream in = new FileInputStream(this.inFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            boolean head = false;
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

                    if (head == false &&
                        body == false &&
                        fullElementName.equalsIgnoreCase("head") == true)
                    {
                        head = true;
                        continue;
                    }

                    if (body == false &&
                        head == false &&
                        fullElementName.equalsIgnoreCase("body") == true)
                    {
                        body = true;
                        continue;
                    }
                    
                    if (body == false &&
                        head == false)
                    {
                        continue;
                    }

                    if (head == true)
                    {
                        if (fullElementName.equalsIgnoreCase("link") == true)
                        { 
                            String rel = null;
                            String type = null;
                            String href = null;

                            {
                                Attribute attributeRel = event.asStartElement().getAttributeByName(new QName("rel"));
                                
                                if (attributeRel != null)
                                {
                                    rel = attributeRel.getValue();
                                }
                            }

                            {
                                Attribute attributeType = event.asStartElement().getAttributeByName(new QName("type"));
                                
                                if (attributeType != null)
                                {
                                    type = attributeType.getValue();
                                }
                            }
                            
                            {
                                Attribute attributeHref = event.asStartElement().getAttributeByName(new QName("href"));
                                
                                if (attributeHref != null)
                                {
                                    href = attributeHref.getValue();
                                }
                            }
                            
                            if (rel != null &&
                                type != null &&
                                href != null)
                            {
                                if (rel.equalsIgnoreCase("stylesheet") == true &&
                                    type.equalsIgnoreCase("text/css") == true)
                                {
                                    if (href.startsWith("file://") == true)
                                    {
                                        System.out.print("epub2html1: The CSS file reference '" + href + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', uses the 'file' protocol.\n");
                                        System.exit(-1);
                                    }

                                    if (href.contains("://") == false)
                                    {
                                        File cssFile = new File(href);
                                        
                                        if (cssFile.isAbsolute() == true)
                                        {
                                            System.out.println("epub2html1: The CSS file reference '" + href + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', is an absolute path.");
                                            System.exit(-1);
                                        }
                                        
                                        cssFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + href);

                                        if (referencedFiles.ContainsCSSFile(cssFile.getAbsolutePath()) != true)
                                        {
                                            if (cssFile.exists() != true)
                                            {
                                                System.out.print("epub2html1: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                                System.exit(-69);
                                            }

                                            if (cssFile.isFile() != true)
                                            {
                                                System.out.print("epub2html1: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                                System.exit(-70);
                                            }

                                            if (cssFile.canRead() != true)
                                            {
                                                System.out.print("epub2html1: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                                System.exit(-71);
                                            }

                                            referencedFiles.AddCSSFile(cssFile);
                                        }
                                    }
                                    else
                                    {
                                        System.out.print("epub2html1: As for the CSS file '" + href + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                        System.exit(-1);
                                    } 
                                }
                            }
                        }
                    }
                    else if (body == true)
                    {
                        if (fullElementName.equalsIgnoreCase("img") == true)
                        {
                            String src = event.asStartElement().getAttributeByName(new QName("src")).getValue();

                            if (src.startsWith("file://") == true)
                            {
                                System.out.print("epub2html1: The image file reference '" + src + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', uses the 'file' protocol.\n");
                                System.exit(-1);
                            }

                            if (src.contains("://") == false)
                            {
                                File srcFile = new File(src);
                                
                                if (srcFile.isAbsolute() == true)
                                {
                                    System.out.println("epub2html1: The image file reference '" + src + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', is an absolute path.");
                                    System.exit(-1);
                                }
                                
                                srcFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + src);

                                if (referencedFiles.ContainsImageFile(srcFile.getAbsolutePath()) != true)
                                {
                                    if (srcFile.exists() != true)
                                    {
                                        System.out.print("epub2html1: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                        System.exit(-49);
                                    }

                                    if (srcFile.isFile() != true)
                                    {
                                        System.out.print("epub2html1: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                        System.exit(-50);
                                    }

                                    if (srcFile.canRead() != true)
                                    {
                                        System.out.print("epub2html1: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                        System.exit(-51);
                                    }

                                    referencedFiles.AddImageFile(srcFile);
                                }
                            }
                            else
                            {
                                System.out.print("epub2html1: As for the image '" + src + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                System.exit(-52);
                            }
                        }
                        else if (fullElementName.equalsIgnoreCase("a") == true)
                        {
                            Attribute href = event.asStartElement().getAttributeByName(new QName("href"));

                            if (href != null)
                            {
                                if (href.getValue().startsWith("#") == true)
                                {
                                    // Referencing an anchor or ID within the same file,
                                    // just ignore.
                                }
                                else if (href.getValue().contains("://") == false)
                                {
                                    String hrefFilePart = href.getValue();
                                    //String hrefAnchor = new String();
                                    
                                    if (hrefFilePart.contains("?") == true)
                                    {
                                        hrefFilePart = hrefFilePart.substring(0, hrefFilePart.indexOf("?"));
                                    }
                                    
                                    if (hrefFilePart.contains("#") == true)
                                    {
                                        //hrefAnchor = hrefFilePart.substring(hrefFilePart.indexOf("#"));
                                        hrefFilePart = hrefFilePart.substring(0, hrefFilePart.indexOf("#"));
                                    }

                                    File hrefFile = new File(hrefFilePart);
                                    
                                    if (hrefFile.isAbsolute() == true)
                                    {
                                        System.out.println("epub2html1: The link reference '" + href.getValue() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', is an absolute path.");
                                        System.exit(-1);
                                    }
                                    
                                    hrefFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + hrefFilePart);
                                    
                                    if (referencedFiles.ContainsXHTMLFile(hrefFile.getAbsolutePath()) != true)
                                    {
                                        if (hrefFile.exists() != true)
                                        {
                                            System.out.print("epub2html1: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                            System.exit(-53);
                                        }

                                        if (hrefFile.isFile() != true)
                                        {
                                            System.out.print("epub2html1: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                            System.exit(-54);
                                        }

                                        if (hrefFile.canRead() != true)
                                        {
                                            System.out.print("epub2html1: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                            System.exit(-55);
                                        }

                                        referencedFiles.AddXHTMLFile(hrefFile);
                                    }
                                }
                                else
                                {
                                    if (href.getValue().startsWith("file://") == true)
                                    {
                                        System.out.print("epub2html1: The link reference '" + href.getValue() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', uses the 'file' protocol.\n");
                                        System.exit(-1);
                                    }
                                    else
                                    {
                                        // Ordinary hyperlink.
                                    }
                                }
                            }
                        }
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
                
                    if (fullElementName.equalsIgnoreCase("head") == true)
                    {
                        if (head == true)
                        {
                            head = false;
                            continue;
                        }
                        else
                        {
                            System.out.println("epub2html1: Misplaced </head> found.");
                            System.exit(-1);
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("body") == true)
                    {
                        if (body == true)
                        {
                            body = false;
                            continue;
                        }
                        else
                        {
                            System.out.println("epub2html1: Misplaced </body> found.");
                            System.exit(-1);
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

        return referencedFiles;
    }

    public boolean ProcessFile(File xhtmlInFile,
                               File xhtmlOutFile,
                               ArrayList<File> xhtmlInFiles,
                               ArrayList<File> referencedImageFiles,
                               ArrayList<File> referencedCSSFiles)
    {
        /**
         * @todo Maybe check besides exception handler, if in/out files
         *     are existing, readable, etc.?
         */

        String programPath = EPUBXHTMLProcessor.class.getProtectionDomain().getCodeSource().getLocation().getFile();

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
                System.out.print("epub2html1: Can't create entities directory '" + entitiesDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (entitiesDirectory.isDirectory() != true)
            {
                System.out.print("epub2html1: Entities path '" + entitiesDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }
        
        String doctypeDeclaration = new String("<!DOCTYPE");
        int doctypePosMatching = 0;
        String doctype = new String();
    
        try
        {
            FileInputStream in = new FileInputStream(xhtmlInFile);
            
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


        try
        {
            EntityResolverLocal localResolver = new EntityResolverLocal(entitiesDirectory);

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(localResolver);
            InputStream in = new FileInputStream(xhtmlInFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(xhtmlOutFile),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write(doctype + "\n");
            writer.write("<!-- This file was generated by epub2html1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");

            boolean head = false;
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

                    if (head == false &&
                        body == false &&
                        fullElementName.equalsIgnoreCase("head") == true)
                    {
                        head = true;
                    }
                    else if (body == false &&
                             head == false &&
                             fullElementName.equalsIgnoreCase("body") == true)
                    {
                        body = true;
                    }

                    writer.write("<" + fullElementName);
                    
                    boolean handled = false;

                    if (head == true)
                    {
                        if (fullElementName.equalsIgnoreCase("link") == true)
                        {
                            String rel = null;
                            String type = null;
                            String href = null;

                            {
                                Attribute attributeRel = event.asStartElement().getAttributeByName(new QName("rel"));
                                
                                if (attributeRel != null)
                                {
                                    rel = attributeRel.getValue();
                                }
                            }

                            {
                                Attribute attributeType = event.asStartElement().getAttributeByName(new QName("type"));
                                
                                if (attributeType != null)
                                {
                                    type = attributeType.getValue();
                                }
                            }
                            
                            {
                                Attribute attributeHref = event.asStartElement().getAttributeByName(new QName("href"));
                                
                                if (attributeHref != null)
                                {
                                    href = attributeHref.getValue();
                                }
                            }
                            
                            if (rel != null &&
                                type != null &&
                                href != null)
                            {
                                if (rel.equalsIgnoreCase("stylesheet") == true &&
                                    type.equalsIgnoreCase("text/css") == true)
                                {
                                    if (href.startsWith("file://") == true)
                                    {
                                        System.out.print("epub2html1: The CSS file reference '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', uses the 'file' protocol.\n");
                                        System.exit(-1);
                                    }

                                    if (href.contains("://") == false)
                                    {
                                        File cssFile = new File(href);
                                        
                                        if (cssFile.isAbsolute() == true)
                                        {
                                            System.out.println("epub2html1: The CSS file reference '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', is an absolute path.");
                                            System.exit(-1);
                                        }
                                        
                                        cssFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + href);

                                        boolean found = false;
                                        
                                        for (int referencedCSSFile = 1; referencedCSSFile <= referencedCSSFiles.size(); referencedCSSFile++)
                                        {
                                            File currentCSSFile = referencedCSSFiles.get(referencedCSSFile-1);
                                        
                                            if (currentCSSFile.getAbsolutePath().equalsIgnoreCase(cssFile.getAbsolutePath()) == true)
                                            {
                                                writer.write(" rel=\"stylesheet\" type=\"text/css\" href=\"style_" + referencedCSSFile + ".css\"");
                                                
                                                found = true;
                                                handled = true;
                                                break;
                                            }
                                        }
                                        
                                        if (found == false)
                                        {
                                            System.out.print("epub2html1: In '" + xhtmlInFile.getAbsolutePath() + "', there was the CSS file '" + cssFile.getAbsolutePath() + "' referenced, which couldn't be found in the extracted EPUB2 files.\n");
                                            System.exit(-78);
                                        }
                                    }
                                    else
                                    {
                                        System.out.print("epub2html1: As for the CSS file '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                        System.exit(-79);
                                    }
                                }
                            }
                        }
                    }
                    else if (body == true)
                    {
                        if (fullElementName.equalsIgnoreCase("img") == true)
                        {
                            String src = event.asStartElement().getAttributeByName(new QName("src")).getValue();

                            if (src.startsWith("file://") == true)
                            {
                                System.out.print("epub2html1: The image file reference '" + src + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', uses the 'file' protocol.\n");
                                System.exit(-1);
                            }

                            if (src.contains("://") == false)
                            {
                                File srcFile = new File(src);
                                
                                if (srcFile.isAbsolute() == true)
                                {
                                    System.out.println("epub2html1: The image file reference '" + src + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', is an absolute path.");
                                    System.exit(-1);
                                }
                                
                                srcFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + src);


                                boolean found = false;
                                
                                for (int referencedImageFile = 1; referencedImageFile <= referencedImageFiles.size(); referencedImageFile++)
                                {
                                    File currentImageFile = referencedImageFiles.get(referencedImageFile-1);
                                
                                    if (currentImageFile.getAbsolutePath().equalsIgnoreCase(srcFile.getAbsolutePath()) == true)
                                    {
                                        String extension = currentImageFile.getName().toLowerCase().substring(currentImageFile.getName().toLowerCase().lastIndexOf('.'));

                                        writer.write(" src=\"image_" + referencedImageFile + extension + "\"");

                                        
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

                                            if (fullAttributeName.equalsIgnoreCase("src") != true)
                                            {
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

                                        found = true;
                                        handled = true;
                                        break;
                                    }
                                }
                                
                                if (found == false)
                                {
                                    System.out.print("epub2html1: In '" + xhtmlInFile.getAbsolutePath() + "', there was the image '" + srcFile.getAbsolutePath() + "' referenced, which couldn't be found in the extracted EPUB2 files.\n");
                                    System.exit(-58);
                                }
                            }
                            else
                            {
                                System.out.print("epub2html1: As for the image '" + src + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                System.exit(-59);
                            }
                        }
                        else if (fullElementName.equalsIgnoreCase("a") == true)
                        {
                            Attribute attributeHref = event.asStartElement().getAttributeByName(new QName("href"));
                                
                            if (attributeHref != null)
                            {
                                String href = attributeHref.getValue();

                                if (href.startsWith("file://") == true)
                                {
                                    System.out.print("epub2html1: The link reference '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', uses the 'file' protocol.\n");
                                    System.exit(-1);
                                }

                                if (href.startsWith("#") != true)
                                {
                                    if (href.contains("://") == false)
                                    {
                                        String hrefFilePart = href;
                                        String hrefAnchor = new String();
                                        
                                        if (hrefFilePart.contains("?") == true)
                                        {
                                            hrefFilePart = hrefFilePart.substring(0, hrefFilePart.indexOf("?"));
                                        }
                                        
                                        if (hrefFilePart.contains("#") == true)
                                        {
                                            hrefAnchor = hrefFilePart.substring(hrefFilePart.indexOf("#"));
                                            hrefFilePart = hrefFilePart.substring(0, hrefFilePart.indexOf("#"));
                                        }


                                        File hrefFile = new File(hrefFilePart);
                                        
                                        if (hrefFile.isAbsolute() == true)
                                        {
                                            System.out.println("epub2html1: The link reference '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', is an absolute path.");
                                            System.exit(-1);
                                        }

                                        hrefFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + hrefFilePart);
                                        
                                        boolean found = false;
                                        
                                        for (int referencedXHTMLFile = 1; referencedXHTMLFile <= xhtmlInFiles.size(); referencedXHTMLFile++)
                                        {
                                            File currentXHTMLFile = xhtmlInFiles.get(referencedXHTMLFile-1);
                                        
                                            if (currentXHTMLFile.getAbsolutePath().equalsIgnoreCase(hrefFile.getAbsolutePath()) == true)
                                            {
                                                writer.write(" href=\"page_" + referencedXHTMLFile + ".xhtml" + hrefAnchor + "\"");
                                                
                                                
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

                                                    if (fullAttributeName.equalsIgnoreCase("href") != true)
                                                    {
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

                                                found = true;
                                                handled = true;
                                                break;
                                            }
                                        }
                                        
                                        if (found == false)
                                        {
                                            System.out.print("epub2html1: In '" + xhtmlInFile.getAbsolutePath() + "', there was a link found to '" + hrefFile.getAbsolutePath() + "', but there is no corresponding local XHTML file in the extracted EPUB2 files.\n");
                                            System.exit(-60);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (handled != true)
                    {
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
                
                    if (fullElementName.equalsIgnoreCase("head") == true)
                    {
                        if (head == true)
                        {
                            head = false;
                        }
                        else
                        {
                            System.out.println("epub2html1: Misplaced </head> found.");
                            System.exit(-1);
                        }
                    }  
                    else if (fullElementName.equalsIgnoreCase("body") == true)
                    {
                        if (body == true)
                        {
                            body = false;
                        }
                        else
                        {
                            System.out.println("epub2html1: Misplaced </body> found.");
                            System.exit(-1);
                        }
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
    
        return true;
    }

    private File inFile;
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
                            System.out.print("epub2html1: '" + this.configFile.getAbsolutePath() + "' contains a resolve entry with empty identifier.\n");
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
                            System.out.print("epub2html1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.isFile() != true)
                        {
                            System.out.print("epub2html1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
                            System.exit(-1);
                        }
                        
                        if (referencedFile.canRead() != true)
                        {
                            System.out.print("epub2html1: '" + referencedFile.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        if (this.localEntities.containsKey(identifier) != true)
                        {
                            this.localEntities.put(identifier, referencedFile);
                        }
                        else
                        {
                            System.out.print("epub2html1: Identifier '" + identifier + "' configured twice in '" + this.configFile.getAbsolutePath() + "'.\n");
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
            System.out.print("epub2html1: Can't resolve entity, no local entities directory.\n");
            System.exit(-1);
        }
        
        if (this.configFile == null)
        {
            System.out.print("epub2html1: Can't resolve entity, no entities configured.\n");
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
            System.out.print("epub2html1: Can't resolve entity with public ID '" + publicID + "', system ID '" + systemID + "', no local copy configured in '" + this.configFile.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
    
        if (localEntity.exists() != true)
        {
            System.out.print("epub2html1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', doesn't exist.\n");
            System.exit(-1);
        }
        
        if (localEntity.isFile() != true)
        {
            System.out.print("epub2html1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't a file.\n");
            System.exit(-1);
        }
        
        if (localEntity.canRead() != true)
        {
            System.out.print("epub2html1: '" + localEntity.getAbsolutePath() + "', referenced in '" + this.configFile.getAbsolutePath() + "', isn't readable.\n");
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

    protected File entitiesDirectory;
    protected File configFile;
    protected Map<String, File> localEntities;
}


class ReferencedFiles
{
    public ReferencedFiles()
    {
        this.xhtmlFiles = new ArrayList<File>();
        this.imageFiles = new ArrayList<File>();
        this.cssFiles = new ArrayList<File>();
    }
    
    public boolean AddXHTMLFile(File xhtmlFile)
    {
        return this.xhtmlFiles.add(xhtmlFile);
    }
    
    public boolean AddImageFile(File imageFile)
    {
        return this.imageFiles.add(imageFile);
    }
    
    public boolean AddCSSFile(File cssFile)
    {
        return this.cssFiles.add(cssFile);
    }
    
    public ArrayList<File> GetXHTMLFiles()
    {
        return this.xhtmlFiles;
    }
    
    public ArrayList<File> GetImageFiles()
    {
        return this.imageFiles;
    }
    
    public ArrayList<File> GetCSSFiles()
    {
        return this.cssFiles;
    }
    
    public boolean ContainsXHTMLFile(String absolutePath)
    {
        for (int i = 0; i < this.xhtmlFiles.size(); i++)
        {
            if (this.xhtmlFiles.get(i).getAbsolutePath().equalsIgnoreCase(absolutePath) == true)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean ContainsImageFile(String absolutePath)
    {
        for (int i = 0; i < this.imageFiles.size(); i++)
        {
            if (this.imageFiles.get(i).getAbsolutePath().equalsIgnoreCase(absolutePath) == true)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean ContainsCSSFile(String absolutePath)
    {
        for (int i = 0; i < this.cssFiles.size(); i++)
        {
            if (this.cssFiles.get(i).getAbsolutePath().equalsIgnoreCase(absolutePath) == true)
            {
                return true;
            }
        }
        
        return false;
    }

    private ArrayList<File> xhtmlFiles;
    private ArrayList<File> imageFiles;
    private ArrayList<File> cssFiles;
}

