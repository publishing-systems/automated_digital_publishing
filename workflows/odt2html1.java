/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2html1 workflow.
 *
 * odt2html1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2html1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2html1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2html1.java
 * @brief Workflow to automatically process a semantic ODT input file based on
 *     template1 of odt2html to structural HTML.
 * @author Stephan Kreutzer
 * @since 2014-06-04
 */



import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;



public class odt2html1
{
    public static void main(String args[])
    {
        System.out.print("odt2html1 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        String programPath = odt2html1.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        File inputFileODT = null;

        if (args.length > 0)
        {
            inputFileODT = new File(args[0]);
        
            if (inputFileODT.exists() != true)
            {
                System.out.print("odt2html1 workflow: '" + inputFileODT.getAbsolutePath() + "' doesn't exist.\n");
                inputFileODT = null;
            }

            if (inputFileODT != null)
            {
                if (inputFileODT.isFile() != true)
                {
                    System.out.print("odt2html1 workflow: '" + inputFileODT.getAbsolutePath() + "' isn't a file.\n");
                    inputFileODT = null;
                }
            }

            if (inputFileODT != null)
            {
                if (inputFileODT.canRead() != true)
                {
                    System.out.print("odt2html1 workflow: '" + inputFileODT.getAbsolutePath() + "' isn't readable.\n");
                    inputFileODT = null;
                }
            }
        }
        else
        {
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(new File(programPath + "../gui/file_picker/file_picker1/config.xml")),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was created by odt2html1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                writer.write("<file-picker1-config>\n");
                writer.write("  <extension extension=\"odt\">ODF Text Document (.odt)</extension>\n");
                writer.write("</file-picker1-config>\n");
                
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
                System.exit(-2);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-3);
            }


            ProcessBuilder builder = new ProcessBuilder("java", "file_picker1");
            builder.directory(new File(programPath + "../gui/file_picker/file_picker1"));

            try
            {
                Process process = builder.start();
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                
                while (scanner.hasNext() == true)
                {
                    String line = scanner.next();
                    
                    System.out.println(line);
                    
                    if (line.contains("' selected.") == true)
                    {
                        StringTokenizer tokenizer = new StringTokenizer(line, "'");
                        
                        if (tokenizer.countTokens() >= 2)
                        {
                            tokenizer.nextToken();
                            inputFileODT = new File(tokenizer.nextToken());
                        }
                    }
                }
                
                scanner.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-4);
            }
        }

        if (inputFileODT == null)
        {
            System.out.println("odt2html1 workflow: No input ODT file.");
            System.exit(-5);
        }


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("odt2html1 workflow: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-6);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2html1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
        }


        File outDirectory = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1");
        
        if (outDirectory.exists() != true)
        {
            if (outDirectory.mkdir() != true)
            {
                System.out.print("odt2html1 workflow: Can't create output directory '" + outDirectory.getAbsolutePath() + "'.\n");
                System.exit(-6);
            }
        }
        else
        {
            if (outDirectory.isDirectory() != true)
            {
                System.out.print("odt2html1 workflow: Output directory path '" + outDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
            else
            {
                if (outDirectory.canWrite() != true)
                {
                    System.out.println("odt2html1 workflow: Can't write to output directory '" + outDirectory.getAbsolutePath() + "'.");
                    System.exit(-4);
                }
            }
        }


        ProcessBuilder builder = new ProcessBuilder("java", "odt2html1", inputFileODT.getAbsolutePath(), outDirectory.getAbsolutePath(), "output_1.html");
        builder.directory(new File(programPath + "../odt2html/odt2html1"));

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
            System.exit(-8);
        }

        
        {
            File from = new File(programPath + "../xsltransformator/xsltransformator1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../xsltransformator/xsltransformator1/entities/config.xml");
            
            if (odt2html1.CopyFile(from, to) != 0)
            {
                System.exit(-9);
            }
        }


        builder = new ProcessBuilder("java", "xsltransformator1", outDirectory.getAbsolutePath() + File.separator + "output_1.html", programPath + "../odt2html/templates/template1/prepare4hierarchical.xsl", outDirectory.getAbsolutePath() + File.separator + "output_2.html");
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
            System.exit(-10);
        }


        {
            File from = new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/config.xml");
            
            if (odt2html1.CopyFile(from, to) != 0)
            {
                System.exit(-11);
            }
        }


        builder = new ProcessBuilder("java", "html_flat2hierarchical1", outDirectory.getAbsolutePath() + File.separator + "output_2.html", programPath + "../odt2html/templates/template1/html_flat2hierarchical1_config.xml", outDirectory.getAbsolutePath() + File.separator + "output_3.html");
        builder.directory(new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1"));

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
            System.exit(-12);
        }


        builder = new ProcessBuilder("java", "xsltransformator1", outDirectory.getAbsolutePath() + File.separator + "output_3.html", programPath + "../odt2html/templates/template1/html_clean.xsl", outDirectory.getAbsolutePath() + File.separator + "output_4.html");
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
            System.exit(-33);
        }

        return;
    }
    
    public static int CopyFile(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("odt2html1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2html1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2html1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
}
