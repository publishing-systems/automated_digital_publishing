/* Copyright (C) 2014-2015  Stephan Kreutzer
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



public class odt2epub2
{
    public static void main(String args[])
    {
        System.out.print("odt2epub2 workflow Copyright (C) 2014-2015 Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\todt2epub2 odt-in-file html2epub1-config-file epub-out-file\n\n");
            System.exit(1);
        }

        String programPath = odt2epub2.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        ProcessBuilder builder = new ProcessBuilder("java", "odt2html1", args[0]);
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

        builder = new ProcessBuilder("java", "html2epub1_config_merge1", tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml", programPath + File.separator + args[1], tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml");
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


        builder = new ProcessBuilder("java", "html2epub1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", tempDirectory.getAbsolutePath() + File.separator + "html2epub1_metadata_config.xml");
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
