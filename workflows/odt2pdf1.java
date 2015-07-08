/* Copyright (C) 2014-2015  Stephan Kreutzer
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
        System.out.print("odt2pdf1 workflow  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");
    
        String programPath = odt2pdf1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        ProcessBuilder builder = null;
        
        if (args.length > 0)
        {
            builder = new ProcessBuilder("java", "odt2html1", args[0]);
        }
        else
        {
            builder = new ProcessBuilder("java", "odt2html1");
        }
        
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


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            System.out.print("odt2pdf1 workflow: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-9);
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2pdf1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-10);
            }
        }


        builder = new ProcessBuilder("java", "xsltransformator1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", programPath + "../odt2html/templates/template1/prepare4html2latex1_layout1.xsl", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_5.html");
        builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));
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
            System.exit(-15);
        }


        builder = new ProcessBuilder("java", "html2pdf1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_5.html");
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
            System.exit(-7);
        }

        return;
    }
}
