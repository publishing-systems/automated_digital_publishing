/* Copyright (C) 2013-2015  Stephan Kreutzer
 *
 * This file is part of html2epub2.
 *
 * html2epub2. is free software: you can redistribute it and/or modify
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
 * @file $/XHTMLProcessor.java
 * @brief Processor for generating the XHTML5 files.
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
import java.io.UnsupportedEncodingException;



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
                        body == false &&
                        tagName.equalsIgnoreCase("head") == true)
                    {
                        head = true;
                        continue;
                    }

                    if (body == false &&
                        head == false &&
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
                                                System.out.print("html2epub2: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                                System.exit(-69);
                                            }

                                            if (cssFile.isFile() != true)
                                            {
                                                System.out.print("html2epub2: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                                System.exit(-70);
                                            }

                                            if (cssFile.canRead() != true)
                                            {
                                                System.out.print("html2epub2: '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                                System.exit(-71);
                                            }

                                            referencedFiles.AddCSSFile(cssFile);
                                        }
                                    }
                                    else
                                    {
                                        System.out.print("html2epub2: As for the CSS file '" + href + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
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
                                        System.out.print("html2epub2: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                        System.exit(-49);
                                    }

                                    if (srcFile.isFile() != true)
                                    {
                                        System.out.print("html2epub2: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                        System.exit(-50);
                                    }

                                    if (srcFile.canRead() != true)
                                    {
                                        System.out.print("html2epub2: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                        System.exit(-51);
                                    }

                                    referencedFiles.AddImageFile(srcFile);
                                }
                            }
                            else
                            {
                                System.out.print("html2epub2: As for the image '" + src + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                                System.exit(-52);
                            }
                        }
                        else if (tagName.equalsIgnoreCase("a") == true)
                        {
                            Attribute href = event.asStartElement().getAttributeByName(new QName("href"));

                            if (href != null)
                            {
                                if (href.getValue().startsWith("#") == true || href.getValue().startsWith("mailto:") == true)
                                {
                                    // Referencing an anchor or ID within the same file, or a mail to address
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
                                            System.out.print("html2epub2: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                            System.exit(-53);
                                        }

                                        if (hrefFile.isFile() != true)
                                        {
                                            System.out.print("html2epub2: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                            System.exit(-54);
                                        }

                                        if (hrefFile.canRead() != true)
                                        {
                                            System.out.print("html2epub2: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
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
                    String tagName = event.asEndElement().getName().getLocalPart();
                
                    if (tagName.equalsIgnoreCase("head") == true)
                    {
                        if (head == true)
                        {
                            head = false;
                            continue;
                        }
                        else
                        {
                            System.out.println("html2epub2: Misplaced </head> found.");
                            System.exit(-1);
                        }
                    }
                    else if (tagName.equalsIgnoreCase("body") == true)
                    {
                        if (body == true)
                        {
                            body = false;
                            continue;
                        }
                        else
                        {
                            System.out.println("html2epub2: Misplaced </body> found.");
                            System.exit(-1);
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


    public boolean generateNavTOC(ArrayList<File> xhtmlInFiles,
                                  ArrayList<String> xhtmlInFileTitles,
                                  File xhtmlOutFile,
                                  String title)
    {
        /**
         * @todo Maybe check besides exception handler, if in/out files
         *     are existing, readable, etc.?
         */

        // Ampersand needs to be the first, otherwise it would double-encode
        // other entities.
        title = title.replaceAll("&", "&amp;");
        title = title.replaceAll("\"", "&quot;");
        title = title.replaceAll("'", "&apos;");
        title = title.replaceAll("<", "&lt;");
        title = title.replaceAll(">", "&gt;");

        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(xhtmlOutFile),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE html>\n");
            writer.write("<!-- This file was generated by html2epub2, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\">\n");
            writer.write("  <head>\n");
            writer.write("    <title>" + title + "</title>\n");
            writer.write("    <meta charset=\"utf-8\"/>\n");
            writer.write("  </head>\n");
            writer.write("  <body>\n");
            writer.write("    <nav epub:type=\"toc\" id=\"toc\">\n");
            writer.write("      <ol>\n");

            for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
            {
                String inFileTitle = xhtmlInFileTitles.get(currentXHTMLFile-1);
                
                // Ampersand needs to be the first, otherwise it would double-encode
                // other entities.
                inFileTitle = inFileTitle.replaceAll("&", "&amp;");
                inFileTitle = inFileTitle.replaceAll("\"", "&quot;");
                inFileTitle = inFileTitle.replaceAll("'", "&apos;");
                inFileTitle = inFileTitle.replaceAll("<", "&lt;");
                inFileTitle = inFileTitle.replaceAll(">", "&gt;");
                
                writer.write("        <li><a href=\"page_" + currentXHTMLFile + ".xhtml\">" + inFileTitle + "</a></li>\n");
            }
            
            writer.write("        </ol>\n");
            writer.write("      </nav>\n");
            writer.write("    </body>\n");
            writer.write("</html>\n");

            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-61);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-63);
        }
    
        return true;
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

        // Ampersand needs to be the first, otherwise it would double-encode
        // other entities.
        title = title.replaceAll("&", "&amp;");
        title = title.replaceAll("\"", "&quot;");
        title = title.replaceAll("'", "&apos;");
        title = title.replaceAll("<", "&lt;");
        title = title.replaceAll(">", "&gt;");

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
                                    new FileOutputStream(xhtmlOutFile),
                                    "UTF8"));

            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE html>\n");
            writer.write("<!-- This file was generated by html2epub2, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            writer.write("  <head>\n");
            writer.write("    <title>" + title + "</title>\n");
            writer.write("    <link rel=\"schema.DC\" href=\"http://purl.org/dc/elements/1.1/\"/>\n");
            writer.write("    <meta name=\"DC.generator\" content=\"html2epub2\"/>\n");
            writer.write("    <meta name=\"DC.type\" content=\"Text\"/>\n");
            writer.write("    <meta name=\"DC.format\" content=\"application/xhtml+xml\"/>\n");
            writer.write("    <meta charset=\"utf-8\"/>\n");


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
                        body == false &&
                        tagName.equalsIgnoreCase("head") == true)
                    {
                        head = true;
                        continue;
                    }

                    if (body == false &&
                        head == false &&
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

                                String attributeValue = attribute.getValue();
                                
                                // Ampersand needs to be the first, otherwise it would double-encode
                                // other entities.
                                attributeValue = attributeValue.replaceAll("&", "&amp;");
                                attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                attributeValue = attributeValue.replaceAll("'", "&apos;");
                                attributeValue = attributeValue.replaceAll("<", "&lt;");
                                attributeValue = attributeValue.replaceAll(">", "&gt;");

                                writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
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
                                            cssFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + File.separator + href);
                                        }

                                        boolean found = false;
                                        String cssFilePath = null;

                                        try
                                        {
                                            cssFilePath = cssFile.getCanonicalPath();
                                        }
                                        catch (IOException ex)
                                        {
                                            ex.printStackTrace();
                                            System.exit(-1);
                                        }
                                        
                                        if (cssFilePath == null)
                                        {
                                            System.out.println("html2epub2: Can't get canonical path of referenced CSS file '" + cssFile.getAbsolutePath() + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "'.");
                                            System.exit(-1);
                                        }

                                        for (int referencedCSSFile = 1; referencedCSSFile <= referencedCSSFiles.size(); referencedCSSFile++)
                                        {
                                            File currentCSSFile = referencedCSSFiles.get(referencedCSSFile-1);
                                            String currentCSSFilePath = null;
                                            
                                            try
                                            {
                                                currentCSSFilePath = currentCSSFile.getCanonicalPath();
                                            }
                                            catch (IOException ex)
                                            {
                                                ex.printStackTrace();
                                                System.exit(-1);
                                            }
                                            
                                            if (currentCSSFilePath == null)
                                            {
                                                System.out.println("html2epub2: Can't get canonical path of CSS file '" + currentCSSFile.getAbsolutePath() + "' of the prepared EPUB2 files.");
                                                System.exit(-1);
                                            }
                                        
                                            if (currentCSSFilePath.equals(cssFilePath) == true)
                                            {
                                                writer.write("    <link rel=\"stylesheet\" type=\"text/css\" href=\"style_" + referencedCSSFile + ".css\"/>");
                                                
                                                found = true;
                                                break;
                                            }
                                        }
                                        
                                        if (found == false)
                                        {
                                            System.out.print("html2epub2: In '" + xhtmlInFile.getAbsolutePath() + "', there was the CSS file '" + cssFile.getAbsolutePath() + "' referenced, which couldn't be found in the prepared EPUB2 files. Comparison is case sensitive.\n");
                                            System.exit(-78);
                                        }
                                    }
                                    else
                                    {
                                        System.out.print("html2epub2: As for the CSS file '" + href + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
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
                                    srcFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + File.separator + src);
                                }

                                boolean found = false;
                                String srcFilePath = null;
                                
                                try
                                {
                                    srcFilePath = srcFile.getCanonicalPath();
                                }
                                catch (IOException ex)
                                {
                                    ex.printStackTrace();
                                    System.exit(-1);
                                }

                                if (srcFilePath == null)
                                {
                                    System.out.println("html2epub2: Can't get canonical path of referenced image file '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "'.");
                                    System.exit(-1);
                                }

                                for (int referencedImageFile = 1; referencedImageFile <= referencedImageFiles.size(); referencedImageFile++)
                                {
                                    File currentImageFile = referencedImageFiles.get(referencedImageFile-1);
                                    String currentImageFilePath = null;
                                    
                                    try
                                    {
                                        currentImageFilePath = currentImageFile.getCanonicalPath();
                                    }
                                    catch (IOException ex)
                                    {
                                        ex.printStackTrace();
                                        System.exit(-1);
                                    }

                                    if (currentImageFilePath == null)
                                    {
                                        System.out.println("html2epub2: Can't get canonical path of image file '" + currentImageFile.getAbsolutePath() + "' of the prepared EPUB2 files.");
                                        System.exit(-1);
                                    }
                                
                                    if (currentImageFilePath.equals(srcFilePath) == true)
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

                                                String attributeValue = attribute.getValue();
                                                
                                                // Ampersand needs to be the first, otherwise it would double-encode
                                                // other entities.
                                                attributeValue = attributeValue.replaceAll("&", "&amp;");
                                                attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                                attributeValue = attributeValue.replaceAll("'", "&apos;");
                                                attributeValue = attributeValue.replaceAll("<", "&lt;");
                                                attributeValue = attributeValue.replaceAll(">", "&gt;");

                                                writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
                                            }
                                        }
                                        
                                        writer.write(">");

                                        found = true;
                                        break;
                                    }
                                }
                                
                                if (found == false)
                                {
                                    System.out.print("html2epub2: In '" + xhtmlInFile.getAbsolutePath() + "', there was the image '" + srcFile.getAbsolutePath() + "' referenced, which couldn't be found in the prepared EPUB2 files. Comparison is case sensitive.\n");
                                    System.exit(-58);
                                }
                            }
                            else
                            {
                                System.out.print("html2epub2: As for the image '" + src + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
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

                                    String attributeValue = attribute.getValue();
                                    
                                    // Ampersand needs to be the first, otherwise it would double-encode
                                    // other entities.
                                    attributeValue = attributeValue.replaceAll("&", "&amp;");
                                    attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                    attributeValue = attributeValue.replaceAll("'", "&apos;");
                                    attributeValue = attributeValue.replaceAll("<", "&lt;");
                                    attributeValue = attributeValue.replaceAll(">", "&gt;");

                                    writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
                                }

                                writer.write(">");
                                
                                continue;
                            }
                        
                            String href = attributeHref.getValue();

                            if (href.startsWith("file://") == true)
                            {
                                href = href.substring(new String("file://").length());
                            }

                            if (href.startsWith("#") == true || href.startsWith("mailto:") == true)
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

                                    String attributeValue = attribute.getValue();
                                    
                                    // Ampersand needs to be the first, otherwise it would double-encode
                                    // other entities.
                                    attributeValue = attributeValue.replaceAll("&", "&amp;");
                                    attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                    attributeValue = attributeValue.replaceAll("'", "&apos;");
                                    attributeValue = attributeValue.replaceAll("<", "&lt;");
                                    attributeValue = attributeValue.replaceAll(">", "&gt;");

                                    writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
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
                                    hrefFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + File.separator + hrefFilePart);
                                }
                                
                                boolean found = false;
                                String hrefFilePath = null;
                                
                                try
                                {
                                    hrefFilePath = hrefFile.getCanonicalPath();
                                }
                                catch (IOException ex)
                                {
                                    ex.printStackTrace();
                                    System.exit(-1);
                                }
                                
                                if (hrefFilePath == null)
                                {
                                    System.out.println("html2epub2: Can't get canonical path of linked file '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "'.");
                                    System.exit(-1);
                                }
                                
                                for (int referencedXHTMLFile = 1; referencedXHTMLFile <= xhtmlInFiles.size(); referencedXHTMLFile++)
                                {
                                    File currentXHTMLFile = xhtmlInFiles.get(referencedXHTMLFile-1);
                                    String currentXHTMLFilePath = null;
                                    
                                    try
                                    {
                                        currentXHTMLFilePath = currentXHTMLFile.getCanonicalPath();
                                    }
                                    catch (IOException ex)
                                    {
                                        ex.printStackTrace();
                                        System.exit(-1);
                                    }
                                    
                                    if (currentXHTMLFilePath == null)
                                    {
                                        System.out.println("html2epub2: Can't get canonical path of linked file '" + currentXHTMLFile.getAbsolutePath() + "' of the prepared EPUB2 files.");
                                        System.exit(-1);
                                    }
 
                                    if (currentXHTMLFilePath.equals(hrefFilePath) == true)
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

                                                String attributeValue = attribute.getValue();
                                                
                                                // Ampersand needs to be the first, otherwise it would double-encode
                                                // other entities.
                                                attributeValue = attributeValue.replaceAll("&", "&amp;");
                                                attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                                attributeValue = attributeValue.replaceAll("'", "&apos;");
                                                attributeValue = attributeValue.replaceAll("<", "&lt;");
                                                attributeValue = attributeValue.replaceAll(">", "&gt;");

                                                writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
                                            }
                                        }

                                        writer.write(">");

                                        found = true;
                                        break;
                                    }
                                }
                                
                                if (found == false)
                                {
                                    System.out.print("html2epub2: In '" + xhtmlInFile.getAbsolutePath() + "', there was a link found to '" + hrefFile.getAbsolutePath() + "', but there is no corresponding local XHTML file in the prepared EPUB2 files. Comparison is case sensitive.\n");
                                    System.exit(-60);
                                }
                            }
                            else
                            {
                                href = href.replaceAll("&", "&amp;");
                            
                                writer.write("<a href=\"" + href + "\"");
                                
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

                                        String attributeValue = attribute.getValue();
                                        
                                        // Ampersand needs to be the first, otherwise it would double-encode
                                        // other entities.
                                        attributeValue = attributeValue.replaceAll("&", "&amp;");
                                        attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                        attributeValue = attributeValue.replaceAll("'", "&apos;");
                                        attributeValue = attributeValue.replaceAll("<", "&lt;");
                                        attributeValue = attributeValue.replaceAll(">", "&gt;");

                                        writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
                                    }
                                }

                                writer.write(">");
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

                                String attributeValue = attribute.getValue();
                                
                                // Ampersand needs to be the first, otherwise it would double-encode
                                // other entities.
                                attributeValue = attributeValue.replaceAll("&", "&amp;");
                                attributeValue = attributeValue.replaceAll("\"", "&quot;");
                                attributeValue = attributeValue.replaceAll("'", "&apos;");
                                attributeValue = attributeValue.replaceAll("<", "&lt;");
                                attributeValue = attributeValue.replaceAll(">", "&gt;");

                                writer.write(attributeName.getLocalPart() + "=\"" + attributeValue + "\"");
                            }
                            
                            writer.write(">");
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    String tagName = event.asEndElement().getName().getLocalPart();
                
                    if (tagName.equalsIgnoreCase("head") == true)
                    {
                        if (head == true)
                        {
                            head = false;
                            writer.write("\n</head>\n");
                            continue;
                        }
                        else
                        {
                            System.out.println("html2epub2: Misplaced </head> found.");
                            System.exit(-1);
                        }
                    }  
                    else if (tagName.equalsIgnoreCase("style") == true)
                    {
                        if (head == true)
                        {
                            if (style == true)
                            {
                                style = false;
                                writer.write("</" + tagName + ">");
                                continue;
                            }
                            else
                            {
                                System.out.println("html2epub2: Misplaced </style> found.");
                                System.exit(-1);
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("body") == true)
                    {
                        if (body == true)
                        {
                            body = false;
                            continue;
                        }
                        else
                        {
                            System.out.println("html2epub2: Misplaced </body> found.");
                            System.exit(-1);
                        }
                    }
                    else
                    {
                        if (body == true)
                        {
                            writer.write("</" + tagName + ">");
                        }
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
