/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2epub2 workflow.
 *
 * odt2epub2 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2epub2 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2epub2 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2epub2.java
 * @brief Workflow to automatically process a semantic ODT input file based on
 *     template1 of odt2html to an EPUB2.
 * @author Stephan Kreutzer
 * @since 2014-11-09
 */



import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;



public class odt2epub2
{
    public static void main(String args[])
    {
        System.out.print("odt2epub2 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");
    
        String programPath = odt2epub2.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\todt2epub2 odt-in-file html2epub1-config-file epub-out-file\n\n");
            System.exit(1);
        }

        ProcessBuilder builder = new ProcessBuilder("java", "odt2html1", args[0]);
        builder.directory(new File(programPath));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            System.out.print("odt2epub2 workflow: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-6);
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2epub2 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
        }


        File outputDirectory = new File(tempDirectory + File.separator + "epub");
        
        if (outputDirectory.exists() == true)
        {  
            if (odt2epub2.DeleteFileRecursively(outputDirectory) != 0)
            {
                System.out.println("odt2epub2 workflow: Can't clean '" + outputDirectory.getAbsolutePath() + "'.");
                System.exit(-13);
            }
        }
        
        if (outputDirectory.mkdirs() != true)
        {
            System.out.print("odt2epub2 workflow: Can't create output directory '" + outputDirectory.getAbsolutePath() + "'.\n");
            System.exit(-14);
        }
        
        {
            File outputInDirectory = new File(outputDirectory + File.separator + "in");

            if (outputInDirectory.mkdirs() != true)
            {
                System.out.print("odt2epub2 workflow: Can't create output in directory '" + outputInDirectory.getAbsolutePath() + "'.\n");
                System.exit(-14);
            }
        }

        {
            File extractionInfoFile = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "info.xml");

            if (extractionInfoFile.exists() != true)
            {
                System.out.print("odt2epub2 workflow: '" + extractionInfoFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (extractionInfoFile.isFile() != true)
            {
                System.out.print("odt2epub2 workflow: '" + extractionInfoFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (extractionInfoFile.canRead() != true)
            {
                System.out.print("odt2epub2 workflow: '" + extractionInfoFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }

            try
            {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(extractionInfoFile.getAbsolutePath());
                document.getDocumentElement().normalize();


                NodeList extractedFilesNodeList = document.getElementsByTagName("extracted-file");
                int extractedFilesNodeListLength = extractedFilesNodeList.getLength();

                for (int i = 0; i < extractedFilesNodeListLength; i++)
                {
                    Node extractedFileNode = extractedFilesNodeList.item(i);

                    NamedNodeMap extractedFileNodeAttributes = extractedFileNode.getAttributes();
                    
                    if (extractedFileNodeAttributes == null)
                    {
                        System.out.print("odt2epub2 workflow: Entry #" + (i + 1) + " in '" + extractionInfoFile.getAbsolutePath() + "' has no attributes.\n");
                        System.exit(-1);
                    }

                    Node typeAttribute = extractedFileNodeAttributes.getNamedItem("type");
                    
                    if (typeAttribute == null)
                    {
                        System.out.print("odt2epub2 workflow: Entry #" + (i + 1) + " in '" + extractionInfoFile.getAbsolutePath() + "' is missing the 'type' attribute.\n");
                        System.exit(-1);
                    }
                    
                    if (typeAttribute.getTextContent().equalsIgnoreCase("image") != true)
                    {
                        continue;
                    }

                    Node pathAttribute = extractedFileNodeAttributes.getNamedItem("path");
                    
                    if (pathAttribute == null)
                    {
                        System.out.print("odt2epub2 workflow: Entry #" + (i + 1) + " in '" + extractionInfoFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                        System.exit(-1);
                    }

                    File imageFile = new File(extractionInfoFile.getAbsoluteFile().getParent() + File.separator + pathAttribute.getTextContent());

                    if (imageFile.exists() != true)
                    {
                        System.out.print("odt2epub2 workflow: Image file '" + imageFile.getAbsolutePath() + "', referenced in '" + extractionInfoFile.getAbsolutePath() + "', doesn't exist.\n");
                        System.exit(-1);
                    }

                    if (imageFile.isFile() != true)
                    {
                        System.out.print("odt2epub2 workflow: Image path '" + imageFile.getAbsolutePath() + "', referenced in '" + extractionInfoFile.getAbsolutePath() + "', isn't a file.\n");
                        System.exit(-1);
                    }

                    if (imageFile.canRead() != true)
                    {
                        System.out.print("odt2epub2 workflow: Image file '" + imageFile.getAbsolutePath() + "', referenced in '" + extractionInfoFile.getAbsolutePath() + "', isn't readable.\n");
                        System.exit(-1);
                    }

                    if (extractionInfoFile.getAbsoluteFile().getParent().equalsIgnoreCase(imageFile.getAbsoluteFile().getParent()) != true)
                    {
                        System.out.print("odt2epub2 workflow: Image file, referenced by entry #" + (i + 1) + " in '" + extractionInfoFile.getAbsolutePath() + "' must be located in the same directory as '" + extractionInfoFile.getAbsolutePath() + "'.\n");
                        System.exit(-1);
                    }

                    File to = new File(outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + imageFile.getName());
                    
                    if (odt2epub2.CopyFileBinary(imageFile, to) != 0)
                    {
                        System.exit(-15);
                    }
                }
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
        }


        {
            File from = new File(programPath + "../html_split/html_split1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../html_split/html_split1/entities/config.xml");
            
            if (odt2epub2.CopyFile(from, to) != 0)
            {
                System.exit(-15);
            }
        }
        

        List<File> splittedParts = new ArrayList<File>();

        builder = new ProcessBuilder("java", "html_split1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", programPath + "../odt2html/templates/template1/html_split1_config_part.xml", outputDirectory.getAbsolutePath() + File.separator + "in");
        builder.directory(new File(programPath + "../html_split/html_split1"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                String line = scanner.next();
                
                System.out.println(line);
                
                if (line.contains("Splitting to '") == true)
                {
                    StringTokenizer tokenizer = new StringTokenizer(line, "'");
                    
                    if (tokenizer.countTokens() >= 2)
                    {
                        tokenizer.nextToken();
                        splittedParts.add(new File(tokenizer.nextToken()));
                    }
                }
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }
        
        
        List<File> splittedChapters = new ArrayList<File>();
        
        if (splittedParts.size() > 0)
        {
            for (int i = 1; i <= splittedParts.size(); i++)
            {
                if (splittedParts.get(i-1).exists() != true)
                {
                    System.out.println("odt2epub2 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                    continue;
                }
                
                if (splittedParts.get(i-1).isFile() != true)
                {
                    System.out.println("odt2epub2 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should be a file, but isn't.");
                    continue;
                }
                
                if (splittedParts.get(i-1).canRead() != true)
                {
                    System.out.println("odt2epub2 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' isn't readable.");
                    continue;
                }

            
                builder = new ProcessBuilder("java", "xsltransformator1", splittedParts.get(i-1).getAbsolutePath(), programPath + "../odt2html/templates/template1/html2epub1_html_part.xsl", outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + i + ".html");
                builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));

                try
                {
                    Process process = builder.start();
                    Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                    
                    while (scanner.hasNext() == true)
                    {
                        System.out.println(scanner.next());
                    }
                    
                    scanner.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-17);
                }
            }
            
            for (int i = 1; i <= splittedParts.size(); i++)
            {
                if (splittedParts.get(i-1).exists() != true)
                {
                    //System.out.println("odt2epub2 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                    continue;
                }
                
                if (splittedParts.get(i-1).isFile() != true)
                {
                    //System.out.println("odt2epub2 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should be a file, but isn't.");
                    continue;
                }
                
                if (splittedParts.get(i-1).canRead() != true)
                {
                    //System.out.println("odt2epub2 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' isn't readable.");
                    continue;
                }
            

                builder = new ProcessBuilder("java", "html_split1", splittedParts.get(i-1).getAbsolutePath(), programPath + "../odt2html/templates/template1/html_split1_config_chapter.xml", outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + i);
                builder.directory(new File(programPath + "../html_split/html_split1"));

                try
                {
                    Process process = builder.start();
                    Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                    
                    while (scanner.hasNext() == true)
                    {
                        String line = scanner.next();
                        
                        System.out.println(line);
                        
                        if (line.contains("Splitting to '") == true)
                        {
                            StringTokenizer tokenizer = new StringTokenizer(line, "'");
                            
                            if (tokenizer.countTokens() >= 2)
                            {
                                tokenizer.nextToken();
                                splittedChapters.add(new File(tokenizer.nextToken()));
                            }
                        }
                    }
                    
                    scanner.close();
                    
                    for (int j = 1; j <= splittedChapters.size(); j++)
                    {
                        if (splittedChapters.get(j-1).exists() != true)
                        {
                            System.out.println("odt2epub2 workflow: '" + splittedChapters.get(j-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                            continue;
                        }
                        
                        if (splittedChapters.get(j-1).isFile() != true)
                        {
                            System.out.println("odt2epub2 workflow: '" + splittedChapters.get(j-1).getAbsolutePath() + "' should be a file, but isn't.");
                            continue;
                        }
                        
                        if (splittedChapters.get(j-1).canRead() != true)
                        {
                            System.out.println("odt2epub2 workflow: '" + splittedChapters.get(j-1).getAbsolutePath() + "' isn't readable.");
                            continue;
                        }
                    
                        builder = new ProcessBuilder("java", "xsltransformator1", splittedChapters.get(j-1).getAbsolutePath(), programPath + "../odt2html/templates/template1/html2epub1_html_chapter.xsl", outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + i + File.separator + j + ".html");
                        builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));

                        try
                        {
                            process = builder.start();
                            scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                            
                            while (scanner.hasNext() == true)
                            {
                                System.out.println(scanner.next());
                            }
                            
                            scanner.close();
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            System.exit(-18);
                        } 
                    }
                    
                    splittedChapters.clear();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-19);
                }
            }
	    }
	    else
	    {
            builder = new ProcessBuilder("java", "html_split1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", programPath + "../odt2html/templates/template1/html_split1_config_chapter.xml", outputDirectory.getAbsolutePath() + File.separator + "in");
            builder.directory(new File(programPath + "../html_split/html_split1"));

            try
            {
                Process process = builder.start();
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                
                while (scanner.hasNext() == true)
                {
                    String line = scanner.next();
                    
                    System.out.println(line);
                    
                    if (line.contains("Splitting to '") == true)
                    {
                        StringTokenizer tokenizer = new StringTokenizer(line, "'");
                        
                        if (tokenizer.countTokens() >= 2)
                        {
                            tokenizer.nextToken();
                            splittedChapters.add(new File(tokenizer.nextToken()));
                        }
                    }
                }
                
                scanner.close(); 
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-20);
            }
            
            for (int i = 1; i <= splittedChapters.size(); i++)
            {
                if (splittedChapters.get(i-1).exists() != true)
                {
                    System.out.println("odt2epub2 workflow: '" + splittedChapters.get(i-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                    continue;
                }
                
                if (splittedChapters.get(i-1).isFile() != true)
                {
                    System.out.println("odt2epub2 workflow: '" + splittedChapters.get(i-1).getAbsolutePath() + "' should be a file, but isn't.");
                    continue;
                }
                
                if (splittedChapters.get(i-1).canRead() != true)
                {
                    System.out.println("odt2epub2 workflow: '" + splittedChapters.get(i-1).getAbsolutePath() + "' isn't readable.");
                    continue;
                }
            
                builder = new ProcessBuilder("java", "xsltransformator1", splittedChapters.get(i-1).getAbsolutePath(), programPath + "../odt2html/templates/template1/html2epub1_html_chapter.xsl", outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + i + ".html");
                builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));

                try
                {
                    Process process = builder.start();
                    Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                    
                    while (scanner.hasNext() == true)
                    {
                        System.out.println(scanner.next());
                    }
                    
                    scanner.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-21);
                }
            }
		}


        builder = new ProcessBuilder("java", "xsltransformator1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", programPath + "../odt2html/templates/template1/html2epub1_html_title.xsl", outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + "title.html");
        builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-34);
        }


        builder = new ProcessBuilder("java", "xsltransformator1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", programPath + "../odt2html/templates/template1/html2epub1_config.xsl", outputDirectory.getAbsolutePath() + File.separator + "config.xml");
        builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-22);
        }
        
        
        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + File.separator + "html2epub1_config_replacement_dictionary.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write("<!-- This file was created by odt2epub2 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<txtreplace1-replacement-dictionary>\n");
            writer.write("  <replace>\n");
            writer.write("    <pattern>./</pattern>\n");
            writer.write("    <replacement>./in/</replacement>\n");
            writer.write("  </replace>\n");
            writer.write("</txtreplace1-replacement-dictionary>\n");
            
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-23);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-24);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-25);
        }
        

        builder = new ProcessBuilder("java", "txtreplace1", outputDirectory.getAbsolutePath() + File.separator + "config.xml", tempDirectory.getAbsolutePath() + File.separator + "html2epub1_config_replacement_dictionary.xml", outputDirectory.getAbsolutePath() + File.separator + "config.xml");
        builder.directory(new File(programPath + "../txtreplace/txtreplace1"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-26);
        }


        builder = new ProcessBuilder("java", "html2epub1_config_merge1", outputDirectory.getAbsolutePath() + File.separator + "config.xml", programPath + File.separator + args[1], outputDirectory.getAbsolutePath() + File.separator + "config.xml");
        builder.directory(new File(programPath + "../html2epub/html2epub1/workflows"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-27);
        }


        builder = new ProcessBuilder("java", "html2epub1", outputDirectory.getAbsolutePath() + File.separator + "config.xml");
        builder.directory(new File(programPath + "../html2epub/html2epub1"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-29);
        }

        {
            File from = new File(outputDirectory.getAbsolutePath() + File.separator + "out.epub");
            File to = new File(programPath + File.separator + args[2]);
            
            if (odt2epub2.CopyFileBinary(from, to) != 0)
            {
                System.exit(-15);
            }
        }

        return;
    }
    
    public static int CopyFile(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
            return -3;
        }
    
    
        char[] buffer = new char[1024];

        try
        {
            to.createNewFile();
        
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(from),
                                    "UTF8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(to),
                                    "UTF8"));
            int charactersRead = reader.read(buffer, 0, buffer.length);

            while (charactersRead > 0)
            {
                writer.write(buffer, 0, charactersRead);
                charactersRead = reader.read(buffer, 0, buffer.length);
            }
            
            writer.close();
            reader.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-30);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-31);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-32);
        }
    
        return 0;
    }

    public static int CopyFileBinary(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
            return -3;
        }
        /*
        if (to.canWrite() != true)
        {
            System.out.println("odt2epub2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + to.getAbsolutePath() + "' isn't writable.");
            return -4;
        }
        */
    
    
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
    
    public static int DeleteFileRecursively(File file)
    {
        if (file.isDirectory() == true)
        {
            for (File child : file.listFiles())
            {
                if (odt2epub2.DeleteFileRecursively(child) != 0)
                {
                    return -1;
                }
            }
        }
        
        if (file.delete() != true)
        {
            System.out.println("odt2epub2 workflow: Can't delete '" + file.getAbsolutePath() + "'.");
            return -1;
        }
    
        return 0;
    }
}
