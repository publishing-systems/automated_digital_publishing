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
 * @file $/EPUBSetup.java
 * @brief Sets up the files required to compile an EPUB2 file.
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
        this.imageOutFiles = new ArrayList<File>();
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
    
        String identifier = "ebook";
        
        {
            SecureRandom random = new SecureRandom();
            identifier += "_" + new BigInteger(130, random).toString(32);
        }

        if (metaData.containsKey("identifier") == true)
        {
            identifier = metaData.get("identifier");
        }
        
        String now = "2014-01-03T12:34:56Z";

        {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(timeZone);
            now = dateFormat.format(new Date());
        }
        
        identifier += "_" + now;


        {
            File mimetypeFile = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "mimetype");
        
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
                System.out.print("html2epub: '" + mimetypeFile.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-44);
            }

            if (mimetypeFile.isFile() != true)
            {
                System.out.print("html2epub: '" + mimetypeFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                System.exit(-45);
            }
        }

        {
            File metaInfDirectory = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "META-INF");
            metaInfDirectory.mkdir();

            if (metaInfDirectory.exists() != true)
            {
                System.out.print("html2epub: '" + metaInfDirectory.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-46);
            }

            if (metaInfDirectory.isDirectory() != true)
            {
                System.out.print("html2epub: '" + metaInfDirectory.getAbsolutePath() + "' was created, but isn't a directory.\n");
                System.exit(-47);
            }
            
            if (metaInfDirectory.canWrite() != true)
            {
                System.out.print("html2epub: '" + metaInfDirectory.getAbsolutePath() + "' was created, but isn't writable.\n");
                System.exit(-48);
            }

            {
                File containerFile = new File(metaInfDirectory.getAbsolutePath() + System.getProperty("file.separator") + "container.xml");
            
                try
                {
                    BufferedWriter writer = new BufferedWriter(
                                            new OutputStreamWriter(
                                            new FileOutputStream(containerFile.getAbsolutePath()),
                                            "UTF8"));
                    
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    writer.write("<!-- This file was created by html2epub, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/text_processing/). -->\n");
                    writer.write("<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" version=\"1.0\">\n");
                    writer.write("  <rootfiles>\n");
                    writer.write("    <rootfile full-path=\"content.opf\" media-type=\"application/oebps-package+xml\"/>\n");
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
                    System.out.print("html2epub: '" + containerFile.getAbsolutePath() + "' wasn't created.\n");
                    System.exit(-26);
                }

                if (containerFile.isFile() != true)
                {
                    System.out.print("html2epub: '" + containerFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                    System.exit(-27);
                }
            }
        }
        
        {
            File opfFile = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "content.opf");
        
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(opfFile.getAbsolutePath()),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was created by html2epub, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/text_processing/). -->\n");
                writer.write("<package xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:opf=\"http://www.idpf.org/2007/opf\" xmlns=\"http://www.idpf.org/2007/opf\" version=\"2.0\" unique-identifier=\"BookId\">\n");
                writer.write("  <metadata>\n");
                
                if (metaData.containsKey("title") == true)
                {
                    writer.write("    <dc:title>" + metaData.get("title") + "</dc:title>\n");
                }
                
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

                writer.write("    <dc:date>" + now + "</dc:date>\n");
                writer.write("    <dc:identifier id=\"BookId\">" + identifier + "</dc:identifier>\n");
                
                if (metaData.containsKey("source") == true)
                {
                    writer.write("    <dc:source>" + metaData.get("source") + "</dc:source>\n");
                }
                
                if (metaData.containsKey("language") == true)
                {
                    writer.write("    <dc:language xsi:type=\"dcterms:RFC3066\">" + metaData.get("language") + "</dc:language>\n");
                }
                
                if (metaData.containsKey("coverage") == true)
                {
                    writer.write("    <dc:coverage>" + metaData.get("coverage") + "</dc:coverage>\n");
                }
                
                if (metaData.containsKey("rights") == true)
                {
                    writer.write("    <dc:rights>" + metaData.get("rights") + "</dc:rights>\n");
                }

                writer.write("  </metadata>\n");
                writer.write("  <manifest>\n");
                writer.write("    <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>\n");


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

                    ListIterator<File> iter = referencedXHTMLFiles.listIterator();
                            
                    while (iter.hasNext())
                    {
                        File referencedXHTMLFile = iter.next();
                        boolean found = false;

                        for (int j = 0; j < xhtmlInFiles.size(); j++)
                        {
                            if (referencedXHTMLFile.getAbsolutePath().equalsIgnoreCase(xhtmlInFiles.get(j).getAbsolutePath()) == true)
                            {
                                found = true;
                                break;
                            }
                        }
                        
                        if (found != true)
                        {
                            System.out.print("html2epub: '" + referencedXHTMLFile.getAbsolutePath() + "' referenced in '" + inFile.getAbsolutePath() + "', but not configured as input file.\n");
                            System.exit(-28);
                        }
                    }

                    iter = referencedImageFiles.listIterator();

                    while (iter.hasNext())
                    {
                        File referencedImageFile = iter.next();
                        boolean alreadyKnown = false;

                        for (int currentAllReferencedImageFile = 0; currentAllReferencedImageFile < this.allReferencedImageFiles.size(); currentAllReferencedImageFile++)
                        {
                            if (this.allReferencedImageFiles.get(currentAllReferencedImageFile).getAbsolutePath().equalsIgnoreCase(referencedImageFile.getAbsolutePath()) == true)
                            {
                                alreadyKnown = true;
                            }
                        }
                        
                        if (alreadyKnown != true)
                        {
                            this.allReferencedImageFiles.add(referencedImageFile);
                        }
                    }
                }

                for (int currentImageFile = 1; currentImageFile <= this.allReferencedImageFiles.size(); currentImageFile++)
                {
                    File inFile = this.allReferencedImageFiles.get(currentImageFile-1);
                    String fileName = inFile.getName().toLowerCase();
                    
                    if (fileName.endsWith(".png") == true)
                    {
                        writer.write("    <item id=\"id_image_" + currentImageFile + "\" href=\"image_" + currentImageFile + ".png\" media-type=\"image/png\"/>\n");
                    }
                    else
                    {
                        System.out.print("html2epub: Unsupported file type for image '" + inFile.getAbsolutePath() + "'.\n");
                        System.exit(-29);
                    }

                    
                    File outFile = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "image_" + currentImageFile + ".png");

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
                        System.out.print("html2epub: '" + outFile.getAbsolutePath() + "' wasn't copied.\n");
                        System.exit(-32);
                    }

                    if (outFile.isFile() != true)
                    {
                        System.out.print("html2epub: '" + outFile.getAbsolutePath() + "' was copied, but isn't a file.\n");
                        System.exit(-33);
                    }

                    if (outFile.canRead() != true)
                    {
                        System.out.print("html2epub: '" + outFile.getAbsolutePath() + "' was copied, but isn't readable.\n");
                        System.exit(-34);
                    }
                    
                    this.imageOutFiles.add(outFile);
                }

                writer.write("  </manifest>\n");
                writer.write("  <spine toc=\"ncx\">\n");

                for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
                {
                    writer.write("    <itemref idref=\"id_page_" + currentXHTMLFile + "\"/>\n");
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
                System.out.print("html2epub: '" + opfFile.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-36);
            }

            if (opfFile.isFile() != true)
            {
                System.out.print("html2epub: '" + opfFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                System.exit(-37);
            }
        }


        {
            File ncxFile = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "toc.ncx");
        
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(ncxFile.getAbsolutePath()),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">\n");
                writer.write("<!-- This file was created by html2epub, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/text_processing/). -->\n");
                writer.write("<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\" xml:lang=\"de-DE\">\n");
                writer.write("  <head>\n");
                
                if (metaData.containsKey("title") == true)
                {
                    writer.write("    <meta name=\"dc:Title\" content=\"" + metaData.get("title") + "\"/>\n");
                }
                else
                {
                    System.out.print("html2epub: Title is missing in the metadata.\n");
                    System.exit(-38);
                }
                
                writer.write("    <meta name=\"dtb:uid\" content=\"" + identifier + "\"/>\n");
                writer.write("  </head>\n");

                if (metaData.containsKey("title") == true)
                {
                    writer.write("  <docTitle>\n");
                    writer.write("    <text>" + metaData.get("title") + "</text>\n");
                    writer.write("  </docTitle>\n");
                }
                else
                {
                    System.out.print("html2epub: Title is missing in the metadata.\n");
                    System.exit(-39);
                }
                
                writer.write("  <navMap>\n");
                
                for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
                {
                    String title = xhtmlInFileTitles.get(currentXHTMLFile-1);

                    writer.write("    <navPoint id=\"id_page_" + currentXHTMLFile + "\" playOrder=\"" + currentXHTMLFile + "\">\n");
                    writer.write("      <navLabel>\n");
                    writer.write("        <text>" + title + "</text>\n");
                    writer.write("      </navLabel>\n");
                    writer.write("      <content src=\"page_" + currentXHTMLFile + ".xhtml\"/>\n");
                    writer.write("    </navPoint>\n");
                }

                writer.write("  </navMap>\n");
                writer.write("</ncx>\n");

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
                System.out.print("html2epub: '" + ncxFile.getAbsolutePath() + "' wasn't created.\n");
                System.exit(-41);
            }

            if (ncxFile.isFile() != true)
            {
                System.out.print("html2epub: '" + ncxFile.getAbsolutePath() + "' was created, but isn't a file.\n");
                System.exit(-42);
            }
        }
    
        return 0;
    }
    
    public ArrayList<File> GetReferencedImageFiles()
    {
        return this.allReferencedImageFiles;
    }
    
    public ArrayList<File> GetImageOutFiles()
    {
        return this.imageOutFiles;
    }

    private ArrayList<File> allReferencedImageFiles;
    private ArrayList<File> imageOutFiles;
}
