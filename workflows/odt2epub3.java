/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of odt2epub3 workflow.
 *
 * odt2epub3 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2epub3 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2epub3 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2epub3.java
 * @brief Workflow to automatically process one or more semantic ODT input
 *     file(s) based on template1 of odt2html to an EPUB2.
 * @author Stephan Kreutzer
 * @since 2014-12-04
 */



import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;



public class odt2epub3
{
    public static void main(String args[])
    {
        System.out.print("odt2epub3 workflow Copyright (C) 2014-2015 Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");
    
        String programPath = odt2epub3.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\todt2epub3 config-file epub-out-file\n\n");
            System.exit(1);
        }


        File configFile = new File(args[0]);
        
        if (configFile.exists() != true)
        {
            System.out.print("odt2epub3 workflow: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (configFile.isFile() != true)
        {
            System.out.print("odt2epub3 workflow: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (configFile.canRead() != true)
        {
            System.out.print("odt2epub3 workflow: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        List<File> inputFiles = new ArrayList<File>();
        File html2epub1ConfigFile = null;

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(configFile);
            document.getDocumentElement().normalize();


            {
                NodeList inputFilesNodeList = document.getElementsByTagName("input-file");
                int inputFilesNodeListLength = inputFilesNodeList.getLength();

                if (inputFilesNodeListLength > 0)
                {
                    for (int i = 0; i < inputFilesNodeListLength; i++)
                    {
                        Node inputFileNode = inputFilesNodeList.item(i);

                        NamedNodeMap inputFileNodeAttributes = inputFileNode.getAttributes();
                        
                        if (inputFileNodeAttributes == null)
                        {
                            System.out.print("odt2epub3 workflow: Input file entry #" + (i + 1) + " in '" + configFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = inputFileNodeAttributes.getNamedItem("path");
                        
                        if (pathAttribute == null)
                        {
                            System.out.print("odt2epub3 workflow: Input file entry #" + (i + 1) + " in '" + configFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        File inputFile = new File(pathAttribute.getTextContent());
                        
                        if (inputFile.isAbsolute() != true)
                        {
                            inputFile = new File(configFile.getAbsoluteFile().getParent() + File.separator + pathAttribute.getTextContent());
                        }
                           
                        if (inputFile.exists() != true)
                        {
                            System.out.print("odt2epub3 workflow: Input file '" + inputFile.getAbsolutePath() + "' doesn't exist.\n");
                            System.exit(-1);
                        }

                        if (inputFile.isFile() != true)
                        {
                            System.out.print("odt2epub3 workflow: Input path '" + inputFile.getAbsolutePath() + "' isn't a file.\n");
                            System.exit(-1);
                        }

                        if (inputFile.canRead() != true)
                        {
                            System.out.print("odt2epub3 workflow: Input file '" + inputFile.getAbsolutePath() + "' isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        inputFiles.add(inputFile);
                    }
                }
                else
                {
                    System.out.print("odt2epub3 workflow: No input files configured.\n");
                    System.exit(-1);
                }
            }

            {
                NodeList html2epub1ConfigFileNodeList = document.getElementsByTagName("html2epub1-config-file");
                int html2epub1ConfigFileNodeListLength = html2epub1ConfigFileNodeList.getLength();

                if (html2epub1ConfigFileNodeListLength == 1)
                {
                    for (int i = 0; i < html2epub1ConfigFileNodeListLength; i++)
                    {
                        Node html2epub1ConfigFileNode = html2epub1ConfigFileNodeList.item(i);

                        NamedNodeMap html2epub1ConfigFileNodeAttributes = html2epub1ConfigFileNode.getAttributes();
                        
                        if (html2epub1ConfigFileNodeAttributes == null)
                        {
                            System.out.print("odt2epub3 workflow: html2epub1 config file entry in '" + configFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = html2epub1ConfigFileNodeAttributes.getNamedItem("path");
                        
                        if (pathAttribute == null)
                        {
                            System.out.print("odt2epub3 workflow: html2epub1 config file entry in '" + configFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        html2epub1ConfigFile = new File(pathAttribute.getTextContent());
                        
                        if (html2epub1ConfigFile.isAbsolute() != true)
                        {
                            html2epub1ConfigFile = new File(configFile.getAbsoluteFile().getParent() + File.separator + pathAttribute.getTextContent());
                        }
                    }
                }
                else if (html2epub1ConfigFileNodeListLength < 1)
                {
                    System.out.print("odt2epub3 workflow: No html2epub1 config file configured in '" + configFile.getAbsolutePath() + "'.\n");
                    System.exit(-1);
                }
                else
                {
                    System.out.print("odt2epub3 workflow: More than one html2epub1 config file configured in '" + configFile.getAbsolutePath() + "'.\n");
                    System.exit(-1);
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

        if (html2epub1ConfigFile == null)
        {
            System.out.println("odt2epub3 workflow: html2epub1 config file isn't set.");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.exists() != true)
        {
            System.out.print("odt2epub3 workflow: Input file '" + html2epub1ConfigFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.isFile() != true)
        {
            System.out.print("odt2epub3 workflow: Input path '" + html2epub1ConfigFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.canRead() != true)
        {
            System.out.print("odt2epub3 workflow: Input file '" + html2epub1ConfigFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        ProcessBuilder builder = null;
        File tempDirectory = new File(programPath + "temp");

        for (int i = 0; i < inputFiles.size(); i++)
        {
            File inputFile = inputFiles.get(i);

            builder = new ProcessBuilder("java", "odt2html1", inputFile.getAbsolutePath());
            builder.directory(new File(programPath));
            builder.redirectErrorStream(true);

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

            if (tempDirectory.exists() != true)
            {
                System.out.print("odt2epub3 workflow: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist, but should exist by now.\n");
                System.exit(-1);
            }
            else
            {
                if (tempDirectory.isDirectory() != true)
                {
                    System.out.print("odt2epub3 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                    System.exit(-1);
                }
            }

            {
                File from = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html");
                File to = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "html_part_" + (i + 1) + ".html");


                if (from.exists() != true)
                {
                    System.out.print("odt2epub3 workflow: File '" + from.getAbsolutePath() + "' doesn't exist, but should by now.\n");
                    System.exit(-1);
                }

                if (from.isFile() != true)
                {
                    System.out.print("odt2epub3 workflow: Path '" + from.getAbsolutePath() + "' isn't a file.\n");
                    System.exit(-1);
                }

                if (from.canRead() != true)
                {
                    System.out.print("odt2epub3 workflow: File '" + from.getAbsolutePath() + "' isn't readable.\n");
                    System.exit(-1);
                }

                if (odt2epub3.CopyFile(from, to) != 0)
                {
                    System.exit(-1);
                }
            }
        }

        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + File.separator + "html_concatenate1_jobfile.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by odt2epub3 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<html-concatenate1-jobfile>\n");

            for (int i = 0; i < inputFiles.size(); i++)
            {
                File inputFile = inputFiles.get(i);
            
                if (i == 0)
                {
                    writer.write("  <head-file path=\"./output_1/html_part_" + (i + 1) + ".html\"/>\n");
                }
                
                writer.write("  <input-file path=\"./output_1/html_part_" + (i + 1) + ".html\"/>\n");
            }

            writer.write("  <output-file path=\"./output_1/html_concatenated.html\"/>\n");
            writer.write("</html-concatenate1-jobfile>\n");

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


        if (tempDirectory.exists() != true)
        {
            System.out.print("odt2epub3 workflow: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist, but should by now.\n");
            System.exit(-6);
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2epub3 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
        }


        builder = new ProcessBuilder("java", "html_concatenate1", tempDirectory.getAbsolutePath() + File.separator + "html_concatenate1_jobfile.xml");
        builder.directory(new File(programPath + "../html_concatenate/html_concatenate1"));
        builder.redirectErrorStream(true);

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


        File outputDirectory = new File(tempDirectory + File.separator + "epub");
        
        if (outputDirectory.exists() == true)
        {  
            if (odt2epub3.DeleteFileRecursively(outputDirectory) != 0)
            {
                System.out.println("odt2epub3 workflow: Can't clean '" + outputDirectory.getAbsolutePath() + "'.");
                System.exit(-13);
            }
        }
        
        if (outputDirectory.mkdirs() != true)
        {
            System.out.print("odt2epub3 workflow: Can't create output directory '" + outputDirectory.getAbsolutePath() + "'.\n");
            System.exit(-14);
        }
        
        {
            File outputInDirectory = new File(outputDirectory + File.separator + "in");

            if (outputInDirectory.mkdirs() != true)
            {
                System.out.print("odt2epub3 workflow: Can't create output in directory '" + outputInDirectory.getAbsolutePath() + "'.\n");
                System.exit(-14);
            }
        }
        
        {
            File from = new File(programPath + "../html_split/html_split1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../html_split/html_split1/entities/config.xml");
            
            if (odt2epub3.CopyFile(from, to) != 0)
            {
                System.exit(-15);
            }
        }


        builder = new ProcessBuilder("java", "html2epub1_config_create_new", tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml");
        builder.directory(new File(programPath + "../html2epub/html2epub1/gui/html2epub1_config_create_new"));
        builder.redirectErrorStream(true);

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

        builder = new ProcessBuilder("java", "html2epub1_config_merge1", tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml", html2epub1ConfigFile.getAbsolutePath(), tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml");
        builder.directory(new File(programPath + "../html2epub/html2epub1/workflows"));
        builder.redirectErrorStream(true);

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


        builder = new ProcessBuilder("java", "html2epub1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "html_concatenated.html", tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml");
        builder.directory(new File(programPath));
        builder.redirectErrorStream(true);

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
            File to = new File(args[1]);

            if (odt2epub3.CopyFileBinary(from, to) != 0)
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
            System.out.println("odt2epub3 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2epub3 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2epub3 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
            System.out.println("odt2epub3 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2epub3 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2epub3 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
    
    public static int DeleteFileRecursively(File file)
    {
        if (file.isDirectory() == true)
        {
            for (File child : file.listFiles())
            {
                if (odt2epub3.DeleteFileRecursively(child) != 0)
                {
                    return -1;
                }
            }
        }
        
        if (file.delete() != true)
        {
            System.out.println("odt2epub3 workflow: Can't delete '" + file.getAbsolutePath() + "'.");
            return -1;
        }
    
        return 0;
    }
}
