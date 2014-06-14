/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2pdf1 workflow.
 *
 * odt2pdf1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2pdf1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2pdf1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2pdf1.java
 * @brief Workflow to automatically process a semantic ODT input file based on
 *     template1 of odt2html to a PDF.
 * @author Stephan Kreutzer
 * @since 2014-05-20
 */



import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;



public class odt2pdf1
{
    public static void main(String args[])
    {
        System.out.print("odt2pdf1 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");
    
        String programPath = odt2pdf1.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        ProcessBuilder builder = null;
        
        if (args.length > 0)
        {
            builder = new ProcessBuilder("java", "odt2html1", args[0]);
        }
        else
        {
            builder = new ProcessBuilder("java", "odt2html1");
        }
        
        builder.directory(new File("."));

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
            System.out.print("odt2pdf1 workflow: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-6);
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2pdf1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
        }


        File outputDirectory = new File(tempDirectory + File.separator + "pdf");
        
        if (outputDirectory.exists() == true)
        {  
            if (odt2pdf1.DeleteFileRecursively(outputDirectory) != 0)
            {
                System.out.println("odt2pdf1 workflow: Can't clean '" + outputDirectory.getAbsolutePath() + "'.");
                System.exit(-13);
            }
        }
        
        if (outputDirectory.mkdirs() != true)
        {
            System.out.print("odt2pdf1 workflow: Can't create output directory '" + outputDirectory.getAbsolutePath() + "'.\n");
            System.exit(-14);
        }


        builder = new ProcessBuilder("java", "html_prepare4latex1", tempDirectory.getAbsolutePath() + File.separator + "output_4.html", outputDirectory.getAbsolutePath() + File.separator + "input.html");
        builder.directory(new File(programPath + "../html2latex/html_prepare4latex1"));

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
            System.exit(-2);
        }


        builder = new ProcessBuilder("java", "xsltransformator1", outputDirectory.getAbsolutePath() + File.separator + "input.html", programPath + "../html2latex/html2latex1/layout/layout1.xsl", outputDirectory.getAbsolutePath() + File.separator + "output.tex");
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
            System.exit(-15);
        }


        for (int i = 0; i < 4; i++)
        {
            builder = new ProcessBuilder("pdflatex", "-halt-on-error", outputDirectory.getAbsolutePath() + File.separator + "output.tex");
            builder.directory(new File(outputDirectory.getAbsolutePath()));

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
                System.exit(-16);
            }
        }

        return;
    }

    public static int DeleteFileRecursively(File file)
    {
        if (file.isDirectory() == true)
        {
            for (File child : file.listFiles())
            {
                if (odt2pdf1.DeleteFileRecursively(child) != 0)
                {
                    return -1;
                }
            }
        }
        
        if (file.delete() != true)
        {
            System.out.println("odt2pdf1 workflow: Can't delete '" + file.getAbsolutePath() + "'.");
            return -1;
        }
    
        return 0;
    }
}
