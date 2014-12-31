/* Copyright (C) 2014  Stephan Kreutzer
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
 * @file $/EPUBProcessor.java
 * @brief Processor to operate on the unpacked EPUB file.
 * @author Stephan Kreutzer
 * @since 2014-07-12
 */



import java.io.File;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;



class EPUBProcessor
{
    public EPUBProcessor(Map<String, File> epubFiles, File epubDirectory)
    {
        this.epubFiles = epubFiles;
        this.epubDirectory = epubDirectory;
    }

    public int Run(File outDirectory)
    {
        /**
         * @todo epubFiles != null, epubDirectory != null.
         */
         
         System.out.println("epub2html1: Reading extracted EPUB files.");
    
        {
            File mimetypeFile = this.epubFiles.get("mimetype");
        
            if (mimetypeFile == null)
            {
                System.out.println("epub2html1: 'mimetype' file of the EPUB file is missing.");
                return -1;
            }
            
            String mimetypeString = null;
            
            try
            {
                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(
                                        new FileInputStream(mimetypeFile),
                                        "UTF8"));
            
                mimetypeString = reader.readLine();
            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
                return -2;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                return -3;
            }
            
            if (mimetypeString != null)
            {
                String requiredMimetypeString = new String("application/epub+zip");
            
                if (mimetypeString.equals(requiredMimetypeString) != true)
                {
                    System.out.println("epub2html1: File isn't an EPUB file - mimetype '" + requiredMimetypeString + "' expected, but mimetype '" + mimetypeString + "' found.");
                    return -4;
                }
            }
            else
            {
                System.out.println("epub2html1: 'mimetype file of the EPUB file doesn't contain a line.");
                return -5;
            }
        }
        
        String opfPath = null;
        
        {
            File containerFile = this.epubFiles.get("META-INF/container.xml");
        
            if (containerFile == null)
            {
                System.out.println("epub2html1: 'META-INF/container.xml' file of the EPUB file is missing.");
                return -6;
            }
            
            EPUBContainerProcessor containerProcessor = new EPUBContainerProcessor(containerFile);

            int result = containerProcessor.Run();
            
            if (result != 0)
            {
                return -7;
            }
            
            Map<String, String> containerInfo = containerProcessor.GetContainerInfo();
            
            if (containerInfo.containsKey("opfPath") != true)
            {
                System.out.println("epub2html1: No OPF path found in 'META-INF/container.xml'.");
                return -9;
            }
            
            opfPath = containerInfo.get("opfPath");
        }

        {
            if (this.epubFiles.containsKey(opfPath) != true)
            {
                System.out.println("epub2html1: No OPF file found.");
                return -10;
            }
            
            File opfFile = this.epubFiles.get(opfPath);

            if (opfFile.exists() != true)
            {
                System.out.println("epub2html1: OPF file '" + opfFile.getAbsolutePath() + "' doesn't exist.");
                return -11;
            }
            
            if (opfFile.isFile() != true)
            {
                System.out.println("epub2html1: OPF path '" + opfFile.getAbsolutePath() + "' isn't a file.");
                return -12;
            }
            
            if (opfFile.canRead() != true)
            {
                System.out.println("epub2html1: OPF file '" + opfFile.getAbsolutePath() + "' isn't readable.");
                return -13;
            }

            EPUBOPFProcessor opfProcessor = new EPUBOPFProcessor(opfFile, epubDirectory);
            
            if (opfProcessor.Run() != 0)
            {
                return -1;
            }
            
            Map<String, OPFManifestEntry> manifest = opfProcessor.GetManifest();
            ArrayList<String> order = opfProcessor.GetOrder();
            ReferencedFiles referencedFiles = new ReferencedFiles();

            for (int i = 0; i < order.size(); i++)
            {
                OPFManifestEntry entry = manifest.get(order.get(i));
                
                if (entry == null)
                {
                    System.out.println("epub2html1: ID not found in manifest.");
                    return -1;
                }
                
                if (entry.GetMediaType().equalsIgnoreCase("application/xhtml+xml") != true)
                {
                    System.out.println("epub2html1: Unexpected media type '" + entry.GetMediaType() + "'.");
                    return -1;
                }
 

                File xhtmlFile = entry.GetHRef();
                
                try
                {
                    if (xhtmlFile.getCanonicalPath().substring(0, this.epubDirectory.getCanonicalPath().length()).equalsIgnoreCase(this.epubDirectory.getCanonicalPath()) != true)
                    {
                        System.out.println("epub2html1: XHTML file reference '" + xhtmlFile.getAbsolutePath() + " isn't relative to EPUB extraction directory '" + this.epubDirectory.getAbsolutePath() + "'.");
                        return -1;
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                
                if (xhtmlFile.exists() != true)
                {
                    System.out.println("epub2html1: '" + xhtmlFile.getAbsolutePath() + "', referenced in OPF '" + opfFile.getAbsolutePath() + "', doesn't exist.");
                    return -1;
                }

                if (xhtmlFile.isFile() != true)
                {
                    System.out.println("epub2html1: '" + xhtmlFile.getAbsolutePath() + "', referenced in OPF '" + opfFile.getAbsolutePath() + "', isn't a file.");
                    return -1;
                }

                if (xhtmlFile.canRead() != true)
                {
                    System.out.println("epub2html1: '" + xhtmlFile.getAbsolutePath() + "', referenced in OPF '" + opfFile.getAbsolutePath() + "', isn't readable.");
                    return -1;
                }

                System.out.println("epub2html1: Analyzing '" + xhtmlFile.getAbsolutePath() + "'.");

                EPUBXHTMLProcessor xhtmlProcessor = new EPUBXHTMLProcessor(xhtmlFile);

                ReferencedFiles referencedFilesXHTML = xhtmlProcessor.GetReferencedFiles(xhtmlFile);
                
                if (referencedFilesXHTML == null)
                {
                    System.out.println("epub2html1: No referenced files information for '" + xhtmlFile.getAbsolutePath() + "'.");
                    return -1;
                }

                ArrayList<File> xhtmlFileList = referencedFilesXHTML.GetXHTMLFiles();
                ArrayList<File> imageFileList = referencedFilesXHTML.GetImageFiles();
                ArrayList<File> cssFileList = referencedFilesXHTML.GetCSSFiles();

                for (File reference : xhtmlFileList)
                {
                    if (referencedFiles.ContainsXHTMLFile(reference.getAbsolutePath()) != true)
                    {
                        referencedFiles.AddXHTMLFile(reference);
                    }
                }

                for (File reference : imageFileList)
                {
                    if (referencedFiles.ContainsImageFile(reference.getAbsolutePath()) != true)
                    {
                        referencedFiles.AddImageFile(reference);
                    }
                }
                
                for (File reference : cssFileList)
                {
                    if (referencedFiles.ContainsCSSFile(reference.getAbsolutePath()) != true)
                    {
                        referencedFiles.AddCSSFile(reference);
                    }
                }
            }

            {
                ArrayList<File> xhtmlFileList = referencedFiles.GetXHTMLFiles();
                ArrayList<File> imageFileList = referencedFiles.GetImageFiles();
                ArrayList<File> cssFileList = referencedFiles.GetCSSFiles();

                for (File reference : imageFileList)
                {
                    boolean found = false;
                
                    for (Map.Entry<String, OPFManifestEntry> entry : manifest.entrySet())
                    { 
                        OPFManifestEntry manifestEntry = entry.getValue();

                        try
                        {
                            if (reference.getCanonicalPath().equalsIgnoreCase(manifestEntry.GetHRef().getCanonicalPath()) == true)
                            {
                                found = true;
                                break;
                            }
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                    }
                    
                    if (found == false)
                    {
                        System.out.println("epub2html1: '" + reference.getAbsolutePath() + "' is referenced in a XHTML file, but has no entry in OPF '" + opfFile.getAbsolutePath() + "'.");
                        return -1;
                    }
                }
            }

            {
                ArrayList<File> outXHTMLFiles = new ArrayList<File>();
            
                for (int i = 0; i < order.size(); i++)
                {
                    OPFManifestEntry entry = manifest.get(order.get(i));
                    
                    if (entry == null)
                    {
                        System.out.println("epub2html1: ID not found in manifest.");
                        return -14;
                    }

                    if (entry.GetMediaType().equalsIgnoreCase("application/xhtml+xml") != true)
                    {
                        System.out.println("epub2html1: Unexpected media type '" + entry.GetMediaType() + "'.");
                        return -15;
                    }

                    File xhtmlFile = entry.GetHRef();
                    
                    try
                    {
                        if (xhtmlFile.getCanonicalPath().substring(0, this.epubDirectory.getCanonicalPath().length()).equalsIgnoreCase(this.epubDirectory.getCanonicalPath()) != true)
                        {
                            System.out.println("epub2html1: XHTML file reference '" + xhtmlFile.getAbsolutePath() + " isn't relative to EPUB extraction directory '" + this.epubDirectory.getAbsolutePath() + "'.");
                            return -1;
                        }
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                        System.exit(-1);
                    }
                    
                    if (xhtmlFile.exists() != true)
                    {
                        System.out.println("epub2html1: '" + xhtmlFile.getAbsolutePath() + "', referenced in OPF '" + opfFile.getAbsolutePath() + "', doesn't exist.");
                        return -17;
                    }

                    if (xhtmlFile.isFile() != true)
                    {
                        System.out.println("epub2html1: '" + xhtmlFile.getAbsolutePath() + "', referenced in OPF '" + opfFile.getAbsolutePath() + "', isn't a file.");
                        return -18;
                    }

                    if (xhtmlFile.canRead() != true)
                    {
                        System.out.println("epub2html1: '" + xhtmlFile.getAbsolutePath() + "', referenced in OPF '" + opfFile.getAbsolutePath() + "', isn't readable.");
                        return -19;
                    }

                    File outFile = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "page_" + (i + 1) + ".html");

                    System.out.println("epub2html1: Processing '" + xhtmlFile.getAbsolutePath() + "'.");

                    EPUBXHTMLProcessor xhtmlProcessor = new EPUBXHTMLProcessor(xhtmlFile);

                    if (xhtmlProcessor.ProcessFile(xhtmlFile,
                                                   outFile,
                                                   referencedFiles.GetXHTMLFiles(),
                                                   referencedFiles.GetImageFiles(),
                                                   referencedFiles.GetCSSFiles()) != true)
                    {
                        return -1;
                    }
                    else
                    {
                        if (outFile.exists() != true)
                        {
                            System.out.println("epub2html1: XHTML processing result file '" + outFile.getAbsolutePath() + "' doesn't exist.");
                            return -1;
                        }

                        if (outFile.isFile() != true)
                        {
                            System.out.println("epub2html1: XHTML processing result path '" + outFile.getAbsolutePath() + "' isn't a file.");
                            return -1;
                        }

                        if (outFile.canRead() != true)
                        {
                            System.out.println("epub2html1: XHTML processing result file '" + outFile.getAbsolutePath() + "' isn't readable.");
                            return -1;
                        }

                        outXHTMLFiles.add(outFile);
                    }
                }
                
                try
                {
                    BufferedWriter writer = new BufferedWriter(
                                            new OutputStreamWriter(
                                            new FileOutputStream(new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "index.xml")),
                                            "UTF8"));

                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    writer.write("<!-- This file was generated by epub2html1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                    writer.write("<epub2html1-output-index>\n");

                    for (int i = 1; i <= outXHTMLFiles.size(); i++)
                    {
                        File outXHTMLFile = outXHTMLFiles.get(i - 1);
                        String name = outXHTMLFile.getName();

                        writer.write("  <file>" + name + "</file>\n");
                    }
                    
                    writer.write("</epub2html1-output-index>\n");
                    
                    writer.close();
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                catch (UnsupportedEncodingException ex)
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


            System.out.println("epub2html1: Copying files to output directory '" + outDirectory.getAbsolutePath() + "'.");
            
            for (int i = 1; i <= referencedFiles.GetImageFiles().size(); i++)
            {
                File imageFile = referencedFiles.GetImageFiles().get(i - 1);
                String extension = imageFile.getName().toLowerCase().substring(imageFile.getName().toLowerCase().lastIndexOf('.'));

                if (EPUBProcessor.CopyFile(imageFile, new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "image_" + i + extension)) != 0)
                {
                    System.exit(-1);
                }
            }
            
            for (int i = 1; i <= referencedFiles.GetCSSFiles().size(); i++)
            {
                if (EPUBProcessor.CopyFile(referencedFiles.GetCSSFiles().get(i - 1), new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "style_" + i + ".css")) != 0)
                {
                    System.exit(-1);
                }
            }
        }

        return 0;
    }

    public static int CopyFile(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("epub2html1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("epub2html1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("epub2html1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
            return -3;
        }
    
    
        byte[] buffer = new byte[1024];

        try
        {
            to.createNewFile();

            FileInputStream reader = new FileInputStream(from);
            FileOutputStream writer = new FileOutputStream(to);
            
            int bytesRead = reader.read(buffer, 0, buffer.length);
            
            while (bytesRead > 0)
            {
                writer.write(buffer, 0, bytesRead);
                bytesRead = reader.read(buffer, 0, buffer.length);
            }
            
            writer.close();
            reader.close();
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
    
    private Map<String, File> epubFiles;
    private File epubDirectory;
}
