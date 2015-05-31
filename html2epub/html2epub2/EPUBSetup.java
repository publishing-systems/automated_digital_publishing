/* Copyright (C) 2013-2015  Stephan Kreutzer
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
 * @file $/EPUBSetup.java
 * @brief Sets up the files required to compile an EPUB3 file.
 * @author Stephan Kreutzer
 * @since 2013-12-11
 */



import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ListIterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;



class EPUBSetup
{
    public EPUBSetup()
    {
        this.allReferencedImageFiles = new ArrayList<File>();
        this.allReferencedCSSFiles = new ArrayList<File>();
        this.imageOutFiles = new ArrayList<File>();
        this.cssOutFiles = new ArrayList<File>();
    }

    public int run(File outDirectory,
                   ArrayList<File> xhtmlInFiles,
                   ArrayList<String> xhtmlInFileTitles,
                   Map<String, String> metaData,
                   boolean xhtmlReaderDTDValidation,
                   boolean xhtmlReaderNamespaceProcessing,
                   boolean xhtmlReaderCoalesceAdjacentCharacterData,
                   boolean xhtmlReaderReplaceEntityReferences,
                   boolean xhtmlReaderResolveExternalParsedEntities,
                   boolean xhtmlReaderUseDTDNotDTDFallback)
    {
        this.allReferencedImageFiles.clear();
        this.allReferencedCSSFiles.clear();
    
        String identifier = "epublication";

        if (metaData.containsKey("identifier") == true)
        {
            identifier = metaData.get("identifier");
        }
        else
        {
            SecureRandom random = new SecureRandom();
            identifier += "_" + new BigInteger(130, random).toString(32);
        }

        String now = "2014-04-13T20:32:16Z";

        {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(timeZone);
            now = dateFormat.format(new Date());
        }

        identifier += "_" + now;

        String title = "epublication";

        if (metaData.containsKey("title") == true)
        {
            title = metaData.get("title");
            
            title = title.replaceAll("&", "&amp;");
            title = title.replaceAll("\"", "&quot;");
            title = title.replaceAll("'", "&apos;");
            title = title.replaceAll("<", "&lt;");
            title = title.replaceAll(">", "&gt;");
        }
        else
        {
            System.out.print("html2epub2: Title is missing in the metadata.\n");
            System.exit(-1);
        }

        String languageCode = "en";

        if (metaData.containsKey("language") == true)
        {
            languageCode = metaData.get("language");
        }
        else
        {
            System.out.println("html2epub2: Language is missing in the metadata.\n");
            System.exit(-1);
        }

        {
            File mimetypeFile = new File(outDirectory.getAbsolutePath() + File.separator + "mimetype");
        
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(mimetypeFile.getAbsolutePath()),
                                        "UTF8"));

                writer.write("application/epub+zip");
                writer.flush();
                writer.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-43);
            }

            if (mimetypeFile.exists() != true)
            {
                System.out.print("html2epub2: '" + mimetypeFile.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-44);
            }

            if (mimetypeFile.isFile() != true)
            {
                System.out.print("html2epub2: '" + mimetypeFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                System.exit(-45);
            }
        }

        {
            File metaInfDirectory = new File(outDirectory.getAbsolutePath() + File.separator + "META-INF");
            metaInfDirectory.mkdir();

            if (metaInfDirectory.exists() != true)
            {
                System.out.print("html2epub2: '" + metaInfDirectory.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-46);
            }

            if (metaInfDirectory.isDirectory() != true)
            {
                System.out.print("html2epub2: '" + metaInfDirectory.getAbsolutePath() + "' was created, but isn't a directory.\n");
                System.exit(-47);
            }
            
            if (metaInfDirectory.canWrite() != true)
            {
                System.out.print("html2epub2: '" + metaInfDirectory.getAbsolutePath() + "' was created, but isn't writable.\n");
                System.exit(-48);
            }

            {
                File containerFile = new File(metaInfDirectory.getAbsolutePath() + File.separator + "container.xml");
            
                try
                {
                    BufferedWriter writer = new BufferedWriter(
                                            new OutputStreamWriter(
                                            new FileOutputStream(containerFile.getAbsolutePath()),
                                            "UTF8"));
                    
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    writer.write("<!-- This file was generated by html2epub2, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                    writer.write("<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" version=\"1.0\">\n");
                    writer.write("  <rootfiles>\n");
                    writer.write("    <rootfile full-path=\"OPS/package.opf\" media-type=\"application/oebps-package+xml\"/>\n");
                    writer.write("  </rootfiles>\n");
                    writer.write("</container>\n");

                    writer.flush();
                    writer.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-25);
                }

                if (containerFile.exists() != true)
                {
                    System.out.print("html2epub2: '" + containerFile.getAbsolutePath() + "' wasn't created.\n");
                    System.exit(-26);
                }

                if (containerFile.isFile() != true)
                {
                    System.out.print("html2epub2: '" + containerFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                    System.exit(-27);
                }
            }
        }

        File opsDirectory = new File(outDirectory.getAbsolutePath() + File.separator + "OPS");
        opsDirectory.mkdir();

        if (opsDirectory.exists() != true)
        {
            System.out.print("html2epub2: '" + opsDirectory.getAbsolutePath() + "' wasn't created.\n");
            System.exit(-46);
        }

        if (opsDirectory.isDirectory() != true)
        {
            System.out.print("html2epub2: '" + opsDirectory.getAbsolutePath() + "' was created, but isn't a directory.\n");
            System.exit(-47);
        }
        
        if (opsDirectory.canWrite() != true)
        {
            System.out.print("html2epub2: '" + opsDirectory.getAbsolutePath() + "' was created, but isn't writable.\n");
            System.exit(-48);
        }

        {
            File opfFile = new File(opsDirectory.getAbsolutePath() + File.separator + "package.opf");
        
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(opfFile.getAbsolutePath()),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was generated by html2epub2, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                writer.write("<package xmlns=\"http://www.idpf.org/2007/opf\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"3.0\" xml:lang=\"" + languageCode + "\" unique-identifier=\"BookId\">\n");
                writer.write("  <metadata>\n");
                writer.write("    <dc:title>" + title + "</dc:title>\n");

                if (metaData.containsKey("creator") == true)
                {
                    writer.write("    <dc:creator>" + metaData.get("creator") + "</dc:creator>\n");
                }

                if (metaData.containsKey("description") == true)
                {
                    writer.write("    <dc:description>" + metaData.get("description") + "</dc:description>\n");
                }

                if (metaData.containsKey("publisher") == true)
                {
                    writer.write("    <dc:publisher>" + metaData.get("publisher") + "</dc:publisher>\n");
                }

                if (metaData.containsKey("subject") == true)
                {
                    writer.write("    <dc:subject>" + metaData.get("subject") + "</dc:subject>\n");
                }

                if (metaData.containsKey("contributor") == true)
                {
                    String contributor = metaData.get("contributor");

                    if (contributor.indexOf("<separator/>") > 0)
                    {
                        String[] contributors = contributor.split("<separator/>");

                        for (int i = 0; i < contributors.length; i++)
                        {
                            writer.write("    <dc:contributor>" + contributors[i] + "</dc:contributor>\n");
                        }
                    }
                    else
                    {
                        writer.write("    <dc:contributor>" + contributor + "</dc:contributor>\n");
                    }
                }

                /**
                 * @todo This date may come from the configuration file to specify
                 *     a particular version of the text, while <meta property="dcterms:modified"/>
                 *     is always the current time stamp.
                 */
                writer.write("    <dc:date>" + now + "</dc:date>\n");
                writer.write("    <dc:identifier id=\"BookId\">" + identifier + "</dc:identifier>\n");
                
                if (metaData.containsKey("source") == true)
                {
                    writer.write("    <dc:source>" + metaData.get("source") + "</dc:source>\n");
                }
                
                writer.write("    <dc:language>" + languageCode + "</dc:language>\n");
                
                if (metaData.containsKey("coverage") == true)
                {
                    writer.write("    <dc:coverage>" + metaData.get("coverage") + "</dc:coverage>\n");
                }
                
                if (metaData.containsKey("rights") == true)
                {
                    writer.write("    <dc:rights>" + metaData.get("rights") + "</dc:rights>\n");
                }

                writer.write("    <meta property=\"dcterms:modified\">" + now + "</meta>\n");
                writer.write("  </metadata>\n");
                writer.write("  <manifest>\n");
                writer.write("    <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>\n");
                writer.write("    <item id=\"toc\" href=\"navtoc.xhtml\" properties=\"nav\" media-type=\"application/xhtml+xml\"/>\n");

                for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
                {
                    writer.write("    <item id=\"id_page_" + currentXHTMLFile + "\" href=\"page_" + currentXHTMLFile + ".xhtml\" media-type=\"application/xhtml+xml\"/>\n");

                    File inFile = xhtmlInFiles.get(currentXHTMLFile-1);

                    XHTMLProcessor xhtmlProcessor = new XHTMLProcessor();
                    ReferencedFiles referencedFiles = xhtmlProcessor.getReferencedFiles(inFile,
                                                                                        xhtmlReaderDTDValidation,
                                                                                        xhtmlReaderNamespaceProcessing,
                                                                                        xhtmlReaderCoalesceAdjacentCharacterData,
                                                                                        xhtmlReaderReplaceEntityReferences,
                                                                                        xhtmlReaderResolveExternalParsedEntities,
                                                                                        xhtmlReaderUseDTDNotDTDFallback);

                    ArrayList<File> referencedXHTMLFiles = referencedFiles.GetXHTMLFiles();
                    ArrayList<File> referencedImageFiles = referencedFiles.GetImageFiles();
                    ArrayList<File> referencedCSSFiles = referencedFiles.GetCSSFiles();

                    ListIterator<File> iter = referencedXHTMLFiles.listIterator();
                            
                    while (iter.hasNext())
                    {
                        boolean found = false;
                        File referencedXHTMLFile = iter.next();
                        String referencedXHTMLFilePath = null;

                        try
                        {
                            referencedXHTMLFilePath = referencedXHTMLFile.getCanonicalPath();
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                        
                        if (referencedXHTMLFilePath == null)
                        {
                            System.out.println("html2epub2: Can't get canonical path of referenced XHTML file '" + referencedXHTMLFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        for (int j = 0; j < xhtmlInFiles.size(); j++)
                        {
                            String xhtmlInFilePath = null;

                            try
                            {
                                xhtmlInFilePath = xhtmlInFiles.get(j).getCanonicalPath();
                            }
                            catch (IOException ex)
                            {
                                ex.printStackTrace();
                                System.exit(-1);
                            }
                            
                            if (xhtmlInFilePath == null)
                            {
                                System.out.println("html2epub2: Can't get canonical path of XHTML input file '" + xhtmlInFiles.get(j).getAbsolutePath() + "'.");
                                System.exit(-1);
                            }

                            if (referencedXHTMLFilePath.equals(xhtmlInFilePath) == true)
                            {
                                found = true;
                                break;
                            }
                        }
                        
                        if (found != true)
                        {
                            System.out.print("html2epub2: '" + referencedXHTMLFile.getAbsolutePath() + "' referenced in '" + inFile.getAbsolutePath() + "', but not configured as input file. Comparison is case sensitive.\n");
                            System.exit(-28);
                        }
                    }

                    iter = referencedImageFiles.listIterator();

                    while (iter.hasNext())
                    {
                        boolean alreadyKnown = false;
                        File referencedImageFile = iter.next();
                        String referencedImageFilePath = null;
                        
                        try
                        {
                            referencedImageFilePath = referencedImageFile.getCanonicalPath();
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            System.exit(-1);
                        }

                        if (referencedImageFilePath == null)
                        {
                            System.out.println("html2epub2: Can't get canonical path of referenced image file '" + referencedImageFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        for (int currentAllReferencedImageFile = 0; currentAllReferencedImageFile < this.allReferencedImageFiles.size(); currentAllReferencedImageFile++)
                        {
                            String currentAllReferencedImageFilePath = null;

                            try
                            {
                                currentAllReferencedImageFilePath = this.allReferencedImageFiles.get(currentAllReferencedImageFile).getCanonicalPath();
                            }
                            catch (IOException ex)
                            {
                                ex.printStackTrace();
                                System.exit(-1);
                            }
                        
                            if (currentAllReferencedImageFilePath.equals(referencedImageFilePath) == true)
                            {
                                alreadyKnown = true;
                            }
                        }
                        
                        if (alreadyKnown != true)
                        {
                            this.allReferencedImageFiles.add(referencedImageFile);
                        }
                    }
                    
                    iter = referencedCSSFiles.listIterator();

                    while (iter.hasNext())
                    {
                        boolean alreadyKnown = false;
                        File referencedCSSFile = iter.next();
                        String referencedCSSFilePath = null;
                        
                        try
                        {
                            referencedCSSFilePath = referencedCSSFile.getCanonicalPath();
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                        
                        if (referencedCSSFilePath == null)
                        {
                            System.out.println("html2epub2: Can't get canonical path of referenced CSS file '" + referencedCSSFile.getAbsolutePath() + "'.");
                            System.exit(-1);
                        }

                        for (int currentAllReferencedCSSFile = 0; currentAllReferencedCSSFile < this.allReferencedCSSFiles.size(); currentAllReferencedCSSFile++)
                        {
                            String currentAllReferencedCSSFilePath = null;
                            
                            try
                            {
                                currentAllReferencedCSSFilePath = this.allReferencedCSSFiles.get(currentAllReferencedCSSFile).getCanonicalPath();
                            }
                            catch (IOException ex)
                            {
                                ex.printStackTrace();
                                System.exit(-1);
                            }
                        
                            if (currentAllReferencedCSSFilePath.equals(referencedCSSFilePath) == true)
                            {
                                alreadyKnown = true;
                            }
                        }
                        
                        if (alreadyKnown != true)
                        {
                            this.allReferencedCSSFiles.add(referencedCSSFile);
                        }
                    }
                }

                for (int currentImageFile = 1; currentImageFile <= this.allReferencedImageFiles.size(); currentImageFile++)
                {
                    File inFile = this.allReferencedImageFiles.get(currentImageFile-1);
                    String fileName = inFile.getName().toLowerCase();
                    String fileExtension = new String();

                    {
                        int dotPosition = fileName.lastIndexOf('.');
                       
                        if (dotPosition > 0)
                        {
                            fileExtension = fileName.substring(dotPosition).toLowerCase();
                        }
                        else
                        {
                            System.out.print("html2epub2: No file extension in the name of image file '" + inFile.getAbsolutePath() + "'.\n");
                            System.exit(-68);
                        }
                    }

                    if (fileExtension.equals(".png") == true)
                    {
                        writer.write("    <item id=\"id_image_" + currentImageFile + "\" href=\"image_" + currentImageFile + ".png\" media-type=\"image/png\"/>\n");
                    }
                    else if (fileExtension.equals(".svg") == true)
                    {
                        writer.write("    <item id=\"id_image_" + currentImageFile + "\" href=\"image_" + currentImageFile + ".svg\" media-type=\"image/svg+xml\"/>\n");
                    }
                    else if (fileExtension.equals(".jpg") == true)
                    {
                        writer.write("    <item id=\"id_image_" + currentImageFile + "\" href=\"image_" + currentImageFile + ".jpg\" media-type=\"image/jpeg\"/>\n");
                    }
                    else if (fileExtension.equals(".jpeg") == true)
                    {
                        writer.write("    <item id=\"id_image_" + currentImageFile + "\" href=\"image_" + currentImageFile + ".jpeg\" media-type=\"image/jpeg\"/>\n");
                    }
                    else
                    {
                        System.out.print("html2epub2: Unsupported file type for image '" + inFile.getAbsolutePath() + "'.\n");
                        System.exit(-29);
                    }


                    File outFile = new File(opsDirectory.getAbsolutePath() + File.separator + "image_" + currentImageFile + fileExtension);

                    try
                    {
                        InputStream inStream = new FileInputStream(inFile);
                        OutputStream outStream = new FileOutputStream(outFile);

                        byte[] buffer = new byte[1024];
                        int length = 0;

                        while ((length = inStream.read(buffer)) > 0)
                        {
                            outStream.write(buffer, 0, length);
                        }

                        inStream.close();
                        outStream.close();
                    }
                    catch(FileNotFoundException ex)
                    {
                        ex.printStackTrace();
                        System.exit(-30);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                        System.exit(-31);
                    }
                    
                    if (outFile.exists() != true)
                    {
                        System.out.print("html2epub2: '" + outFile.getAbsolutePath() + "' wasn't copied.\n");
                        System.exit(-32);
                    }

                    if (outFile.isFile() != true)
                    {
                        System.out.print("html2epub2: '" + outFile.getAbsolutePath() + "' was copied, but isn't a file.\n");
                        System.exit(-33);
                    }

                    if (outFile.canRead() != true)
                    {
                        System.out.print("html2epub2: '" + outFile.getAbsolutePath() + "' was copied, but isn't readable.\n");
                        System.exit(-34);
                    }
                    
                    this.imageOutFiles.add(outFile);
                }

                for (int currentCSSFile = 1; currentCSSFile <= this.allReferencedCSSFiles.size(); currentCSSFile++)
                {
                    File inFile = this.allReferencedCSSFiles.get(currentCSSFile-1);

                    writer.write("    <item id=\"id_style_" + currentCSSFile + "\" href=\"style_" + currentCSSFile + ".css\" media-type=\"text/css\"/>\n");

                    File outFile = new File(opsDirectory.getAbsolutePath() + File.separator + "style_" + currentCSSFile + ".css");

                    try
                    {
                        InputStream inStream = new FileInputStream(inFile);
                        OutputStream outStream = new FileOutputStream(outFile);

                        byte[] buffer = new byte[1024];
                        int length = 0;

                        while ((length = inStream.read(buffer)) > 0)
                        {
                            outStream.write(buffer, 0, length);
                        }

                        inStream.close();
                        outStream.close();
                    }
                    catch(FileNotFoundException ex)
                    {
                        ex.printStackTrace();
                        System.exit(-73);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                        System.exit(-74);
                    }
                    
                    if (outFile.exists() != true)
                    {
                        System.out.print("html2epub2: '" + outFile.getAbsolutePath() + "' wasn't copied.\n");
                        System.exit(-75);
                    }

                    if (outFile.isFile() != true)
                    {
                        System.out.print("html2epub2: '" + outFile.getAbsolutePath() + "' was copied, but isn't a file.\n");
                        System.exit(-76);
                    }

                    if (outFile.canRead() != true)
                    {
                        System.out.print("html2epub2: '" + outFile.getAbsolutePath() + "' was copied, but isn't readable.\n");
                        System.exit(-77);
                    }
                    
                    this.cssOutFiles.add(outFile);
                }

                writer.write("  </manifest>\n");
                writer.write("  <spine toc=\"ncx\">\n");

                for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
                {
                    writer.write("    <itemref idref=\"id_page_" + currentXHTMLFile + "\" linear=\"yes\"/>\n");
                }

                writer.write("  </spine>\n");
                writer.write("</package>\n");

                writer.flush();
                writer.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-35);
            }

            if (opfFile.exists() != true)
            {
                System.out.print("html2epub2: '" + opfFile.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-36);
            }

            if (opfFile.isFile() != true)
            {
                System.out.print("html2epub2: '" + opfFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                System.exit(-37);
            }
        }

        {
            File ncxFile = new File(opsDirectory.getAbsolutePath() + File.separator + "toc.ncx");
        
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(ncxFile.getAbsolutePath()),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was generated by html2epub2, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                writer.write("<ncx:ncx xmlns:ncx=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\" xml:lang=\"" + languageCode + "\">\n");
                writer.write("  <ncx:head>\n");
                writer.write("    <ncx:meta name=\"dc:Title\" content=\"" + title + "\"/>\n");
                writer.write("    <ncx:meta name=\"dtb:uid\" content=\"" + identifier + "\"/>\n");
                writer.write("  </ncx:head>\n");
                writer.write("  <ncx:docTitle>\n");
                writer.write("    <ncx:text>" + title + "</ncx:text>\n");
                writer.write("  </ncx:docTitle>\n");
                writer.write("  <ncx:navMap>\n");
                
                for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
                {
                    String navPointTitle = xhtmlInFileTitles.get(currentXHTMLFile-1);

                    // Ampersand needs to be the first, otherwise it would double-encode
                    // other entities.
                    navPointTitle = navPointTitle.replaceAll("&", "&amp;");
                    navPointTitle = navPointTitle.replaceAll("\"", "&quot;");
                    navPointTitle = navPointTitle.replaceAll("'", "&apos;");
                    navPointTitle = navPointTitle.replaceAll("<", "&lt;");
                    navPointTitle = navPointTitle.replaceAll(">", "&gt;");

                    writer.write("    <ncx:navPoint xmlns:ncx=\"http://www.daisy.org/z3986/2005/ncx/\" id=\"id_page_" + currentXHTMLFile + "\" playOrder=\"" + currentXHTMLFile + "\">\n");
                    writer.write("      <ncx:navLabel>\n");
                    writer.write("        <ncx:text>" + navPointTitle + "</ncx:text>\n");
                    writer.write("      </ncx:navLabel>\n");
                    writer.write("      <ncx:content src=\"page_" + currentXHTMLFile + ".xhtml\"/>\n");
                    writer.write("    </ncx:navPoint>\n");
                }

                writer.write("  </ncx:navMap>\n");
                writer.write("</ncx:ncx>\n");

                writer.flush();
                writer.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-40);
            }

            if (ncxFile.exists() != true)
            {
                System.out.print("html2epub2: '" + ncxFile.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-41);
            }

            if (ncxFile.isFile() != true)
            {
                System.out.print("html2epub2: '" + ncxFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                System.exit(-42);
            }
        }
    
        return 0;
    }
    
    public ArrayList<File> GetReferencedImageFiles()
    {
        return this.allReferencedImageFiles;
    }
    
    public ArrayList<File> GetReferencedCSSFiles()
    {
        return this.allReferencedCSSFiles;
    }
    
    public ArrayList<File> GetImageOutFiles()
    {
        return this.imageOutFiles;
    }
    
    public ArrayList<File> GetCSSOutFiles()
    {
        return this.cssOutFiles;
    }

    private ArrayList<File> allReferencedImageFiles;
    private ArrayList<File> allReferencedCSSFiles;
    private ArrayList<File> imageOutFiles;
    private ArrayList<File> cssOutFiles;
}
