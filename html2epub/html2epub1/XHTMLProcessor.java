/* Copyright (C) 2013-2014  Stephan Kreutzer
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
 * @file $/XHTMLProcessor.java
 * @brief Processor for XHTML files.
 * @author Stephan Kreutzer
 * @since 2013-12-13
 */



import java.io.File;
import java.util.ArrayList;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.io.IOException;



class XHTMLProcessor
{
    public XHTMLProcessor()
    {
    
    }

    public ReferencedFiles getReferencedFiles(File xhtmlFile,
                                              boolean xhtmlReaderDTDValidation,
                                              boolean xhtmlReaderNamespaceProcessing,
                                              boolean xhtmlReaderCoalesceAdjacentCharacterData,
                                              boolean xhtmlReaderReplaceEntityReferences,
                                              boolean xhtmlReaderResolveExternalParsedEntities,
                                              boolean xhtmlReaderUseDTDNotDTDFallback)
    {
        /**
         * @todo Maybe check besides exception handler, if in file
         *     is existing, readable, etc.?
         */
    
        ReferencedFiles referencedFiles = new ReferencedFiles();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            inputFactory.setProperty("javax.xml.stream.isValidating", xhtmlReaderDTDValidation);
            inputFactory.setProperty("javax.xml.stream.isNamespaceAware", xhtmlReaderNamespaceProcessing);
            inputFactory.setProperty("javax.xml.stream.isCoalescing", xhtmlReaderCoalesceAdjacentCharacterData);
            inputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", xhtmlReaderReplaceEntityReferences);
            inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", xhtmlReaderResolveExternalParsedEntities);
            inputFactory.setProperty("javax.xml.stream.supportDTD", xhtmlReaderUseDTDNotDTDFallback);

            InputStream in = new FileInputStream(xhtmlFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            boolean head = false;
            boolean body = false;


            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (head == false &&
                        tagName.equalsIgnoreCase("head") == true)
                    {
                        head = true;
                        continue;
                    }

                    if (body == false &&
                        tagName.equalsIgnoreCase("body") == true)
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
                        if (tagName.equalsIgnoreCase("link") == true)
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
                                        href = href.substring(new String("file://").length());
                                    }

                                    if (href.contains("://") == false)
                                    {
                                        File cssFile = new File(href);
                                        
                                        if (cssFile.isAbsolute() != true)
                                        {
                                            cssFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + href);
                                        }

                                        if (referencedFiles.ContainsCSSFile(cssFile.getAbsolutePath()) != true)
                                        {
                                            if (cssFile.exists() != true)
                                            {
                                                System.out.print("html2epub1: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                                System.exit(-69);
                                            }

                                            if (cssFile.isFile() != true)
                                            {
                                                System.out.print("html2epub1: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                                System.exit(-70);
                                            }

                                            if (cssFile.canRead() != true)
                                            {
                                                System.out.print("html2epub1: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                                System.exit(-71);
                                            }

                                            referencedFiles.AddCSSFile(cssFile);
                                        }
                                    }
                                    else
                                    {
                                        System.out.print("html2epub1: As for the CSS file '" + href + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                        System.exit(-72);
                                    } 
                                }
                            }
                        }
                    }
                    else if (body == true)
                    {
                        if (tagName.equalsIgnoreCase("img") == true)
                        {
                            String src = event.asStartElement().getAttributeByName(new QName("src")).getValue();

                            if (src.startsWith("file://") == true)
                            {
                                src = src.substring(new String("file://").length());
                            }

                            if (src.contains("://") == false)
                            {
                                File srcFile = new File(src);
                                
                                if (srcFile.isAbsolute() != true)
                                {
                                    srcFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + src);
                                }

                                if (referencedFiles.ContainsImageFile(srcFile.getAbsolutePath()) != true)
                                {
                                    if (srcFile.exists() != true)
                                    {
                                        System.out.print("html2epub1: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                        System.exit(-49);
                                    }

                                    if (srcFile.isFile() != true)
                                    {
                                        System.out.print("html2epub1: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                        System.exit(-50);
                                    }

                                    if (srcFile.canRead() != true)
                                    {
                                        System.out.print("html2epub1: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                        System.exit(-51);
                                    }

                                    referencedFiles.AddImageFile(srcFile);
                                }
                            }
                            else
                            {
                                System.out.print("html2epub1: As for the image '" + src + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                System.exit(-52);
                            }
                        }
                        else if (tagName.equalsIgnoreCase("a") == true)
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
                                    
                                    if (hrefFile.isAbsolute() != true)
                                    {
                                        hrefFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + hrefFilePart);
                                    }
                                    
                                    if (referencedFiles.ContainsXHTMLFile(hrefFile.getAbsolutePath()) != true)
                                    {
                                        if (hrefFile.exists() != true)
                                        {
                                            System.out.print("html2epub1: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                            System.exit(-53);
                                        }

                                        if (hrefFile.isFile() != true)
                                        {
                                            System.out.print("html2epub1: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                            System.exit(-54);
                                        }

                                        if (hrefFile.canRead() != true)
                                        {
                                            System.out.print("html2epub1: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                            System.exit(-55);
                                        }

                                        referencedFiles.AddXHTMLFile(hrefFile);
                                    }
                                }
                                else
                                {
                                    if (href.getValue().startsWith("file://") == true)
                                    {
                                    
                                    }
                                }
                            }
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    if (head == true)
                    {
                        String tagName = event.asEndElement().getName().getLocalPart();

                        if (tagName.equalsIgnoreCase("head") == true)
                        {
                            head = false;
                            continue;
                        }
                    }
                    else if (body == true)
                    {
                        String tagName = event.asEndElement().getName().getLocalPart();

                        if (tagName.equalsIgnoreCase("body") == true)
                        {
                            body = false;
                            continue;
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-56);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-57);
        }

        return referencedFiles;
    }

    public boolean processFile(File xhtmlInFile,
                               File xhtmlOutFile,
                               String title,
                               ArrayList<File> xhtmlInFiles,
                               ArrayList<File> referencedImageFiles,
                               ArrayList<File> referencedCSSFiles,
                               boolean xhtmlReaderDTDValidation,
                               boolean xhtmlReaderNamespaceProcessing,
                               boolean xhtmlReaderCoalesceAdjacentCharacterData,
                               boolean xhtmlReaderReplaceEntityReferences,
                               boolean xhtmlReaderResolveExternalParsedEntities,
                               boolean xhtmlReaderUseDTDNotDTDFallback)
    {
        /**
         * @todo Maybe check besides exception handler, if in/out files
         *     are existing, readable, etc.?
         */
    
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            inputFactory.setProperty("javax.xml.stream.isValidating", xhtmlReaderDTDValidation);
            inputFactory.setProperty("javax.xml.stream.isNamespaceAware", xhtmlReaderNamespaceProcessing);
            inputFactory.setProperty("javax.xml.stream.isCoalescing", xhtmlReaderCoalesceAdjacentCharacterData);
            inputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", xhtmlReaderReplaceEntityReferences);
            inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", xhtmlReaderResolveExternalParsedEntities);
            inputFactory.setProperty("javax.xml.stream.supportDTD", xhtmlReaderUseDTDNotDTDFallback);

            InputStream in = new FileInputStream(xhtmlInFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(xhtmlOutFile.getAbsolutePath()),
                                    "UTF8"));

            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
            writer.write("<!-- This file was created by html2epub1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            writer.write("  <head profile=\"http://dublincore.org/documents/dcq-html/\">\n");
            writer.write("    <title>" + title + "</title>\n");
            writer.write("    <link rel=\"schema.DC\" href=\"http://purl.org/dc/elements/1.1/\"/>\n");
            writer.write("    <meta name=\"DC.generator\" content=\"html2epub1\"/>\n");
            writer.write("    <meta name=\"DC.type\" content=\"Text\"/>\n");
            writer.write("    <meta name=\"DC.format\" content=\"application/xhtml+xml\"/>\n");
            writer.write("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n");
            writer.write("    <meta http-equiv=\"content-style-type\" content=\"text/css\"/>\n");


            boolean head = false;
            boolean style = false;
            boolean body = false;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (head == false &&
                        tagName.equalsIgnoreCase("head") == true)
                    {
                        head = true;
                        continue;
                    }

                    if (body == false &&
                        tagName.equalsIgnoreCase("body") == true)
                    {
                        body = true;
                        writer.write("\n<body>\n");
                        continue;
                    }
                    
                    if (body == false &&
                        head == false)
                    {
                        continue;
                    }

                    if (head == true)
                    {
                        if (tagName.equalsIgnoreCase("style") == true)
                        {
                            style = true;

                            writer.write("<" + tagName);
                            
                            // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                            @SuppressWarnings("unchecked")
                            Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                            
                            while (attributes.hasNext() == true)
                            {
                                Attribute attribute = attributes.next();
                                QName attributeName = attribute.getName();
                                
                                writer.write(" ");
                                
                                if (attributeName.getPrefix().length() > 0)
                                {
                                    writer.write(attributeName.getPrefix() + ":");
                                }

                                writer.write(attributeName.getLocalPart() + "=\"" + attribute.getValue() + "\"");
                            }
                            
                            writer.write(">");
                        }
                        else if (tagName.equalsIgnoreCase("link") == true)
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
                                        href = href.substring(new String("file://").length());
                                    }

                                    if (href.contains("://") == false)
                                    {
                                        File cssFile = new File(href);
                                        
                                        if (cssFile.isAbsolute() != true)
                                        {
                                            cssFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + href);
                                        }

                                        boolean found = false;
                                        
                                        for (int referencedCSSFile = 1; referencedCSSFile <= referencedCSSFiles.size(); referencedCSSFile++)
                                        {
                                            File currentCSSFile = referencedCSSFiles.get(referencedCSSFile-1);
                                        
                                            if (currentCSSFile.getAbsolutePath().equalsIgnoreCase(cssFile.getAbsolutePath()) == true)
                                            {
                                                writer.write("    <link rel=\"stylesheet\" type=\"text/css\" href=\"style_" + referencedCSSFile + ".css\"/>");
                                                
                                                found = true;
                                                break;
                                            }
                                        }
                                        
                                        if (found == false)
                                        {
                                            System.out.print("html2epub1: In '" + xhtmlInFile.getAbsolutePath() + "', there was the CSS file '" + cssFile.getAbsolutePath() + "' referenced, which couldn't be found in the prepared EPUB2 files.\n");
                                            System.exit(-78);
                                        }
                                    }
                                    else
                                    {
                                        System.out.print("html2epub1: As for the CSS file '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                        System.exit(-79);
                                    }
                                }
                            }
                        }
                    }
                    else if (body == true)
                    {
                        if (tagName.equalsIgnoreCase("img") == true)
                        {
                            String src = event.asStartElement().getAttributeByName(new QName("src")).getValue();

                            if (src.startsWith("file://") == true)
                            {
                                src = src.substring(new String("file://").length());
                            }

                            if (src.contains("://") == false)
                            {
                                File srcFile = new File(src);
                                
                                if (srcFile.isAbsolute() != true)
                                {
                                    srcFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + src);
                                }

                                boolean found = false;
                                
                                for (int referencedImageFile = 1; referencedImageFile <= referencedImageFiles.size(); referencedImageFile++)
                                {
                                    File currentImageFile = referencedImageFiles.get(referencedImageFile-1);
                                
                                    if (currentImageFile.getAbsolutePath().equalsIgnoreCase(srcFile.getAbsolutePath()) == true)
                                    {
                                        String extension = currentImageFile.getName().toLowerCase().substring(currentImageFile.getName().toLowerCase().lastIndexOf('.'));
                                    
                                        writer.write("<img src=\"image_" + referencedImageFile + extension + "\"");
                                        
                                        // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                                        @SuppressWarnings("unchecked")
                                        Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                                        
                                        while (attributes.hasNext() == true)
                                        {  
                                            Attribute attribute = attributes.next();
                                            QName attributeName = attribute.getName();
                                            
                                            if (attributeName.getLocalPart().equalsIgnoreCase("src") != true)
                                            {
                                                writer.write(" ");
                                                
                                                if (attributeName.getPrefix().length() > 0)
                                                {
                                                    writer.write(attributeName.getPrefix() + ":");
                                                }

                                                writer.write(attributeName.getLocalPart() + "=\"" + attribute.getValue() + "\"");
                                            }
                                        }
                                        
                                        writer.write(">");

                                        found = true;
                                        break;
                                    }
                                }
                                
                                if (found == false)
                                {
                                    System.out.print("html2epub1: In '" + xhtmlInFile.getAbsolutePath() + "', there was the image '" + srcFile.getAbsolutePath() + "' referenced, which couldn't be found in the prepared EPUB2 files.\n");
                                    System.exit(-58);
                                }
                            }
                            else
                            {
                                System.out.print("html2epub1: As for the image '" + src + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                System.exit(-59);
                            }
                        }
                        else if (tagName.equalsIgnoreCase("a") == true)
                        {
                            Attribute attributeHref = event.asStartElement().getAttributeByName(new QName("href"));
                                
                            if (attributeHref == null)
                            {
                                writer.write("<a");
                                
                                // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                                @SuppressWarnings("unchecked")
                                Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                                
                                while (attributes.hasNext() == true)
                                {  
                                    Attribute attribute = attributes.next();
                                    QName attributeName = attribute.getName();
                                    
                                    writer.write(" ");
                                    
                                    if (attributeName.getPrefix().length() > 0)
                                    {
                                        writer.write(attributeName.getPrefix() + ":");
                                    }

                                    writer.write(attributeName.getLocalPart() + "=\"" + attribute.getValue() + "\"");
                                }

                                writer.write(">");
                                
                                continue;
                            }
                        
                            String href = attributeHref.getValue();

                            if (href.startsWith("file://") == true)
                            {
                                href = href.substring(new String("file://").length());
                            }

                            if (href.startsWith("#") == true)
                            {
                                // Referencing an anchor or ID within the same file.
                                
                                writer.write("<a");
                                
                                // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                                @SuppressWarnings("unchecked")
                                Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                                
                                while (attributes.hasNext() == true)
                                {  
                                    Attribute attribute = attributes.next();
                                    QName attributeName = attribute.getName();
                                    
                                    writer.write(" ");
                                    
                                    if (attributeName.getPrefix().length() > 0)
                                    {
                                        writer.write(attributeName.getPrefix() + ":");
                                    }

                                    writer.write(attributeName.getLocalPart() + "=\"" + attribute.getValue() + "\"");
                                }

                                writer.write(">");
                            }
                            else if (href.contains("://") == false)
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
                                
                                if (hrefFile.isAbsolute() != true)
                                {
                                    hrefFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + hrefFilePart);
                                }
                                
                                boolean found = false;
                                
                                for (int referencedXHTMLFile = 1; referencedXHTMLFile <= xhtmlInFiles.size(); referencedXHTMLFile++)
                                {
                                    File currentXHTMLFile = xhtmlInFiles.get(referencedXHTMLFile-1);
                                
                                    if (currentXHTMLFile.getAbsolutePath().equalsIgnoreCase(hrefFile.getAbsolutePath()) == true)
                                    {
                                        writer.write("<a href=\"page_" + referencedXHTMLFile + ".xhtml" + hrefAnchor + "\"");
                                        
                                        // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                                        @SuppressWarnings("unchecked")
                                        Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                                        
                                        while (attributes.hasNext() == true)
                                        {  
                                            Attribute attribute = attributes.next();
                                            QName attributeName = attribute.getName();
                                            
                                            if (attributeName.getLocalPart().equalsIgnoreCase("href") != true)
                                            {
                                                writer.write(" ");
                                                
                                                if (attributeName.getPrefix().length() > 0)
                                                {
                                                    writer.write(attributeName.getPrefix() + ":");
                                                }

                                                writer.write(attributeName.getLocalPart() + "=\"" + attribute.getValue() + "\"");
                                            }
                                        }

                                        writer.write(">");

                                        found = true;
                                        break;
                                    }
                                }
                                
                                if (found == false)
                                {
                                    System.out.print("html2epub1: In '" + xhtmlInFile.getAbsolutePath() + "', there was a link found to '" + hrefFile.getAbsolutePath() + "', but there is no corresponding local XHTML file in the prepared EPUB2 files.\n");
                                    System.exit(-60);
                                }
                            }
                            else
                            {
                                writer.write("<a href=\"" + href + "\">");
                            }
                        }
                        else
                        {
                            writer.write("<" + tagName);
                            
                            // http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-12/msg00090.html
                            @SuppressWarnings("unchecked")
                            Iterator<Attribute> attributes = (Iterator<Attribute>)event.asStartElement().getAttributes();
                            
                            while (attributes.hasNext() == true)
                            {  
                                Attribute attribute = attributes.next();
                                QName attributeName = attribute.getName();
                                
                                writer.write(" ");
                                
                                if (attributeName.getPrefix().length() > 0)
                                {
                                    writer.write(attributeName.getPrefix() + ":");
                                }

                                writer.write(attributeName.getLocalPart() + "=\"" + attribute.getValue() + "\"");
                            }
                            
                            writer.write(">");
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    if (head == true)
                    {
                        String tagName = event.asEndElement().getName().getLocalPart();

                        if (tagName.equalsIgnoreCase("head") == true)
                        {
                            head = false;
                            writer.write("\n</head>\n");
                            continue;
                        }
                        
                        if (tagName.equalsIgnoreCase("style") == true)
                        {
                            style = false;
                            writer.write("</" + tagName + ">");
                        }
                    }
                    else if (body == true)
                    {
                        String tagName = event.asEndElement().getName().getLocalPart();

                        if (tagName.equalsIgnoreCase("body") == true)
                        {
                            body = false;
                            continue;
                        }
                        
                        writer.write("</" + tagName + ">");
                    }
                }
                else if (event.isCharacters() == true)
                {
                    if (head == true)
                    {
                        if (style == true)
                        {
                            event.writeAsEncodedUnicode(writer);
                        }
                    }
                    else if (body == true)
                    {
                        event.writeAsEncodedUnicode(writer);
                    }
                }
            }
            
            writer.write("\n");
            writer.write("  </body>\n");
            writer.write("</html>\n");
            
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
            {System.out.println(absolutePath + "x2!");
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
