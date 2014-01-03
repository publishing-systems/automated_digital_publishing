/* Copyright (C) 2013-2014  Stephan Kreutzer
 *
 * This file is part of html2epub.
 *
 * html2epub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub. If not, see <http://www.gnu.org/licenses/>.
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


            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (tagName.equalsIgnoreCase("img") == true)
                    {
                        String src = event.asStartElement().getAttributeByName(new QName("src")).getValue();

                        if (src.startsWith("file://") == true)
                        {
                            src = src.substring(new String("file://").length());
                        }

                        if (src.contains("://") == false)
                        {
                            /**
                             * @todo There's no OS independent mechanism in Java present to
                             *     check if a path is absolute. So this code is only capable
                             *     of relative file references.
                             */

                            File srcFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + src);

                            if (referencedFiles.ContainsImageFile(srcFile.getAbsolutePath()) != true)
                            {
                                if (srcFile.exists() != true)
                                {
                                    System.out.print("html2epub: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                    System.exit(-49);
                                }

                                if (srcFile.isFile() != true)
                                {
                                    System.out.print("html2epub: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                    System.exit(-50);
                                }

                                if (srcFile.canRead() != true)
                                {
                                    System.out.print("html2epub: '" + srcFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
                                    System.exit(-51);
                                }

                                referencedFiles.AddImageFile(srcFile);
                            }
                        }
                        else
                        {
                            System.out.print("html2epub: As for the image '" + src + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                            System.exit(-52);
                        }
                    }
                    else if (tagName.equalsIgnoreCase("a") == true)
                    {
                        Attribute href = event.asStartElement().getAttributeByName(new QName("href"));

                        if (href != null)
                        {
                            if (href.getValue().contains("://") == false)
                            {
                                /**
                                 * @todo There's no OS independent mechanism in Java present to
                                 *     check if a path is absolute. So this code is only capable
                                 *     of handling relative file references.
                                 */

                                File hrefFile = new File(xhtmlFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + href.getValue());
                                
                                if (referencedFiles.ContainsXHTMLFile(hrefFile.getAbsolutePath()) != true)
                                {
                                    if (hrefFile.exists() != true)
                                    {
                                        System.out.print("html2epub: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', doesn't exist.\n");
                                        System.exit(-53);
                                    }

                                    if (hrefFile.isFile() != true)
                                    {
                                        System.out.print("html2epub: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't a file.\n");
                                        System.exit(-54);
                                    }

                                    if (hrefFile.canRead() != true)
                                    {
                                        System.out.print("html2epub: '" + hrefFile.getAbsolutePath() + "', referenced in '" + xhtmlFile.getAbsolutePath() + "', isn't readable.\n");
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
            writer.write("<!-- This file was created by html2epub, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/text_processing/). -->\n");
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            writer.write("  <head profile=\"http://dublincore.org/documents/dcq-html/\">\n");
            writer.write("    <title>" + title + "</title>\n");
            writer.write("    <link rel=\"schema.DC\" href=\"http://purl.org/dc/elements/1.1/\"/>\n");
            writer.write("    <meta name=\"DC.generator\" content=\"html2epub\"/>\n");
            writer.write("    <meta name=\"DC.type\" content=\"Text\"/>\n");
            writer.write("    <meta name=\"DC.format\" content=\"application/xhtml+xml\"/>\n");
            writer.write("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n");
            writer.write("    <meta http-equiv=\"content-style-type\" content=\"text/css\"/>\n");
            writer.write("  </head>\n");
            writer.write("  <body>\n");

            
            boolean body = false;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (body == false &&
                        tagName.equalsIgnoreCase("body") == true)
                    {
                        body = true;
                        continue;
                    }
                    
                    if (body == false)
                    {
                        continue;
                    }

                    if (tagName.equalsIgnoreCase("img") == true)
                    {
                        String src = event.asStartElement().getAttributeByName(new QName("src")).getValue();

                        if (src.startsWith("file://") == true)
                        {
                            src = src.substring(new String("file://").length());
                        }

                        if (src.contains("://") == false)
                        {
                            /**
                             * @todo There's no OS independent mechanism in Java present to
                             *     check if a path is absolute. So this code is only capable
                             *     of relative file references.
                             */

                            File srcFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + src);

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
                                        
                                        if (attribute.getName().getLocalPart().equalsIgnoreCase("src") != true)
                                        {
                                            writer.write(" " + attribute.getName() + "=\"" + attribute.getValue() + "\"");
                                        }
                                    }
                                    
                                    writer.write(">");

                                    found = true;
                                    break;
                                }
                            }
                            
                            if (found == false)
                            {
                                System.out.print("html2epub: In '" + xhtmlInFile.getAbsolutePath() + "', there was the image '" + srcFile.getAbsolutePath() + "' referenced, which couldn't be found in the prepared EPUB2 files.\n");
                                System.exit(-58);
                            }
                        }
                        else
                        {
                            System.out.print("html2epub: As for the image '" + src + "', referenced in '" + xhtmlInFile.getAbsolutePath() + "', remote resources are not allowed in an EPUB2 file.\n");
                            System.exit(-59);
                        }
                    }
                    else if (tagName.equalsIgnoreCase("a") == true)
                    {
                        String href = event.asStartElement().getAttributeByName(new QName("href")).getValue();

                        if (href.startsWith("file://") == true)
                        {
                            href = href.substring(new String("file://").length());
                        }

                        if (href.contains("://") == false)
                        {
                            /**
                             * @todo There's no OS independent mechanism in Java present to
                             *     check if a path is absolute. So this code is only capable
                             *     of handling relative file references.
                             */

                            File hrefFile = new File(xhtmlInFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + href);
                            
                            boolean found = false;
                            
                            for (int referencedXHTMLFile = 1; referencedXHTMLFile <= xhtmlInFiles.size(); referencedXHTMLFile++)
                            {
                                File currentXHTMLFile = xhtmlInFiles.get(referencedXHTMLFile-1);
                            
                                if (currentXHTMLFile.getAbsolutePath().equalsIgnoreCase(hrefFile.getAbsolutePath()) == true)
                                {
                                    writer.write("<a href=\"page_" + referencedXHTMLFile + ".xhtml\">");

                                    found = true;
                                    break;
                                }
                            }
                            
                            if (found == false)
                            {
                                System.out.print("html2epub: In '" + xhtmlInFile.getAbsolutePath() + "', there was a link found to '" + hrefFile.getAbsolutePath() + "', but there is no corresponding local XHTML file in the prepared EPUB2 files.\n");
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

                            writer.write(" " + attribute.getName() + "=\"" + attribute.getValue() + "\"");
                        }
                        
                        writer.write(">");
                    }
                }
                else if (event.isEndElement() == true)
                {
                    String tagName = event.asEndElement().getName().getLocalPart();

                    if (tagName.equalsIgnoreCase("body") == true)
                    {
                        body = false;
                        continue;
                    }
                    
                    if (body == true)
                    {
                        writer.write("</" + tagName + ">");
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
    }
    
    public boolean AddXHTMLFile(File xhtmlFile)
    {
        return this.xhtmlFiles.add(xhtmlFile);
    }
    
    public boolean AddImageFile(File imageFile)
    {
        return this.imageFiles.add(imageFile);
    }
    
    public ArrayList<File> GetXHTMLFiles()
    {
        return this.xhtmlFiles;
    }
    
    public ArrayList<File> GetImageFiles()
    {
        return this.imageFiles;
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

    private ArrayList<File> xhtmlFiles;
    private ArrayList<File> imageFiles;
}
