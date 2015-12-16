/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of html2xelatex1.
 *
 * html2xelatex1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2xelatex1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2xelatex1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html2latex/html2xelatex1/html2xelatex1.java
 * @brief Prepare a semantic HTML input file based on template1 of odt2html
 *     to be processed by xelatex.
 * @author Stephan Kreutzer
 * @since 2015-04-05
 */



import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class html2xelatex1
{
    public static void main(String args[])
    {
        System.out.print("html2xelatex1  Copyright (C) 2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\thtml2xelatex1 in-file out-file\n\n");
            System.exit(1);
        }


        String programPath = html2xelatex1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
            programPath = URLDecoder.decode(programPath, "UTF-8");
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

        File inputHTMLFile = new File(args[0]);

        if (inputHTMLFile.exists() != true)
        {
            System.out.print("html2xelatex1: '" + inputHTMLFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inputHTMLFile.isFile() != true)
        {
            System.out.print("html2xelatex1: '" + inputHTMLFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (inputHTMLFile.canRead() != true)
        {
            System.out.print("html2xelatex1: '" + inputHTMLFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }


        File tempDirectory = new File(programPath + "temp");

        if (tempDirectory.exists() == true)
        {
            if (tempDirectory.isDirectory() == true)
            {
                if (tempDirectory.canWrite() != true)
                {
                    System.out.println("html2xelatex1: Can't write to temp directory '" + tempDirectory.getAbsolutePath() + "'.");
                    System.exit(-1);
                }
            }
            else
            {
                System.out.println("html2xelatex1: Temp path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.");
                System.exit(-1);
            }
        }
        else
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.println("html2xelatex1: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.");
                System.exit(-1);
            }
        }
        
        if (tempDirectory.exists() != true)
        {
            System.out.print("html2xelatex1: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-9);
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("html2xelatex1: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-10);
            }
        }


        File outputHTMLFile = new File(tempDirectory.getAbsolutePath() + File.separator + "output.tex");

        ProcessBuilder builder = new ProcessBuilder("java", "xsltransformator1", inputHTMLFile.getAbsolutePath(), programPath + "html2xelatex1.xsl", outputHTMLFile.getAbsolutePath());
        builder.directory(new File(programPath + ".." + File.separator + ".." + File.separator + "xsltransformator" + File.separator + "xsltransformator1"));
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

        if (html2xelatex1.CopyFileBinary(outputHTMLFile, new File(args[1])) != 0)
        {
            System.out.println("html2xelatex1: Can't copy the result file '" + outputHTMLFile.getAbsolutePath() + "' to output path '" + args[1] + "'.");
            System.exit(-1);
        }

        return;
    }

    public static int CopyFileBinary(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("html2xelatex1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("html2xelatex1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("html2xelatex1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
}
