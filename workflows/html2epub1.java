/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of html2epub1 workflow.
 *
 * html2epub1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/html2epub1.java
 * @brief Workflow to automatically process a semantic HTML input file based on
 *     template1 of odt2html to an EPUB2.
 * @author Stephan Kreutzer
 * @since 2014-06-09
 */



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;



public class html2epub1
{
    public static void main(String args[])
    {
        System.out.print("html2epub1 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");
    
        String programPath = html2epub1.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        if (args.length != 2)
        {
            System.out.print("Usage:\n" +
                             "\thtml2epub1 input-html-file input-config-file\n\n");

            System.exit(1);
        }

        File inputHTMLFile = new File(args[0]);

        if (inputHTMLFile.exists() != true)
        {
            System.out.print("html2epub1 workflow: '" + inputHTMLFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inputHTMLFile.isFile() != true)
        {
            System.out.print("html2epub1 workflow: '" + inputHTMLFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (inputHTMLFile.canRead() != true)
        {
            System.out.print("html2epub1 workflow: '" + inputHTMLFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }

        File inputConfigFile = new File(args[1]);

        if (inputConfigFile.exists() != true)
        {
            System.out.print("html2epub1 workflow: '" + inputConfigFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (inputConfigFile.isFile() != true)
        {
            System.out.print("html2epub1 workflow: '" + inputConfigFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (inputConfigFile.canRead() != true)
        {
            System.out.print("html2epub1 workflow: '" + inputConfigFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("html2epub1 workflow: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-7);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("html2epub1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-8);
            }
        }


        File outputDirectory = new File(tempDirectory + File.separator + "epub");
        
        if (outputDirectory.exists() == true)
        {  
            if (html2epub1.DeleteFileRecursively(outputDirectory) != 0)
            {
                System.out.println("html2epub1 workflow: Can't clean '" + outputDirectory.getAbsolutePath() + "'.");
                System.exit(-9);
            }
        }
        
        if (outputDirectory.mkdirs() != true)
        {
            System.out.print("html2epub1 workflow: Can't create output directory '" + outputDirectory.getAbsolutePath() + "'.\n");
            System.exit(-10);
        }
        

        {
            File from = new File(programPath + "../html_split/html_split1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../html_split/html_split1/entities/config.xml");
            
            if (html2epub1.CopyFile(from, to) != 0)
            {
                System.exit(-11);
            }
        }
        

        List<File> splittedParts = new ArrayList<File>();

        ProcessBuilder builder = new ProcessBuilder("java", "html_split1", inputHTMLFile.getAbsolutePath(), programPath + "../odt2html/templates/template1/html_split1_config_part.xml", outputDirectory.getAbsolutePath() + File.separator + "in");
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
            System.exit(-12);
        }
        
        
        List<File> splittedChapters = new ArrayList<File>();
        
        if (splittedParts.size() > 0)
        {
            for (int i = 1; i <= splittedParts.size(); i++)
            {
                if (splittedParts.get(i-1).exists() != true)
                {
                    System.out.println("html2epub1 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                    continue;
                }
                
                if (splittedParts.get(i-1).isFile() != true)
                {
                    System.out.println("html2epub1 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should be a file, but isn't.");
                    continue;
                }
                
                if (splittedParts.get(i-1).canRead() != true)
                {
                    System.out.println("html2epub1 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' isn't readable.");
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
                    System.exit(-13);
                }
            }
            
            for (int i = 1; i <= splittedParts.size(); i++)
            {
                if (splittedParts.get(i-1).exists() != true)
                {
                    //System.out.println("html2epub1 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                    continue;
                }
                
                if (splittedParts.get(i-1).isFile() != true)
                {
                    //System.out.println("html2epub1 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' should be a file, but isn't.");
                    continue;
                }
                
                if (splittedParts.get(i-1).canRead() != true)
                {
                    //System.out.println("html2epub1 workflow: '" + splittedParts.get(i-1).getAbsolutePath() + "' isn't readable.");
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
                            System.out.println("html2epub1 workflow: '" + splittedChapters.get(j-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                            continue;
                        }
                        
                        if (splittedChapters.get(j-1).isFile() != true)
                        {
                            System.out.println("html2epub1 workflow: '" + splittedChapters.get(j-1).getAbsolutePath() + "' should be a file, but isn't.");
                            continue;
                        }
                        
                        if (splittedChapters.get(j-1).canRead() != true)
                        {
                            System.out.println("html2epub1 workflow: '" + splittedChapters.get(j-1).getAbsolutePath() + "' isn't readable.");
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
                            System.exit(-14);
                        }
                    }
                    
                    splittedChapters.clear();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-15);
                }
            }
		    }
		    else
		    {
            builder = new ProcessBuilder("java", "html_split1", inputHTMLFile.getAbsolutePath(), programPath + "../odt2html/templates/template1/html_split1_config_chapter.xml", outputDirectory.getAbsolutePath() + File.separator + "in");
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
                System.exit(-16);
            }
            
            for (int i = 1; i <= splittedChapters.size(); i++)
            {
                if (splittedChapters.get(i-1).exists() != true)
                {
                    System.out.println("html2epub1 workflow: '" + splittedChapters.get(i-1).getAbsolutePath() + "' should have been created, but doesn't exist.");
                    continue;
                }
                
                if (splittedChapters.get(i-1).isFile() != true)
                {
                    System.out.println("html2epub1 workflow: '" + splittedChapters.get(i-1).getAbsolutePath() + "' should be a file, but isn't.");
                    continue;
                }
                
                if (splittedChapters.get(i-1).canRead() != true)
                {
                    System.out.println("html2epub1 workflow: '" + splittedChapters.get(i-1).getAbsolutePath() + "' isn't readable.");
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
                    System.exit(-17);
                }
            }
		    }


        builder = new ProcessBuilder("java", "xsltransformator1", inputHTMLFile.getAbsolutePath(), programPath + "../odt2html/templates/template1/html2epub1_html_title.xsl", outputDirectory.getAbsolutePath() + File.separator + "in" + File.separator + "title.html");
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
            System.exit(-18);
        }


        // Generate html2epub1 configuration file which will contain the
        // list of HTML input files. Will be merged with metadata later.

        builder = new ProcessBuilder("java", "xsltransformator1", inputHTMLFile.getAbsolutePath(), programPath + "../odt2html/templates/template1/html2epub1_config.xsl", outputDirectory.getAbsolutePath() + File.separator + "config.xml");
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
            System.exit(-19);
        }
        

        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + File.separator + "html2epub1_config_replacement_dictionary.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write("<!-- This file was created by html2epub1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
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
            System.exit(-20);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-21);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-22);
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
            System.exit(-23);
        }


        // Merge html2epub1 configuration file, which already contains the
        // list of HTML input files, with the metadata configuration file.

        builder = new ProcessBuilder("java", "html2epub1_config_merge1", outputDirectory.getAbsolutePath() + File.separator + "config.xml", inputConfigFile.getAbsolutePath(), outputDirectory.getAbsolutePath() + File.separator + "config.xml");
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
            System.exit(-24);
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
            System.exit(-25);
        }

        System.exit(0);
    }
    
    public static int CopyFile (File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("html2epub1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("html2epub1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("html2epub1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
            System.exit(-27);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-28);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-29);
        }
    
        return 0;
    }
    
    public static int DeleteFileRecursively(File file)
    {
        if (file.isDirectory() == true)
        {
            for (File child : file.listFiles())
            {
                if (html2epub1.DeleteFileRecursively(child) != 0)
                {
                    return -1;
                }
            }
        }
        
        if (file.delete() != true)
        {
            System.out.println("html2epub1 workflow: Can't delete '" + file.getAbsolutePath() + "'.");
            return -1;
        }
    
        return 0;
    }
}
