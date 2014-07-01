/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of unzip1.
 *
 * unzip1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * unzip1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with unzip1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/unzip1.java
 * @brief The main module that delegates to the specific processors.
 * @author Stephan Kreutzer
 * @since 2014-07-01
 */



import java.io.File;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;



public class unzip1
{
    public static void main(String args[])
    {
        System.out.print("unzip1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");

        if (args.length != 2)
        {
            System.out.print("Usage:\n" +
                             "\tunzip1 zip-file out-directory\n\n");

            System.exit(1);
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("unzip1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("unzip1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("unzip1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }
        
        File outDirectory = new File(args[1]);

        if (outDirectory.exists() == true)
        {
            if (outDirectory.isDirectory() == true)
            {
                if (outDirectory.canWrite() != true)
                {
                    System.out.println("unzip1: Can't write to directory '" + outDirectory.getAbsolutePath() + "'.");
                    System.exit(-4);
                }
            }
            else
            {
                System.out.println("unzip11: '" + outDirectory.getAbsolutePath() + "' isn't a directory.");
                System.exit(-5);
            }
        }
        else
        {
            if (outDirectory.mkdir() != true)
            {
                System.out.println("unzip1: Can't create out directory '" + outDirectory.getAbsolutePath() + "'.");
                System.exit(-6);
            }
        }


        ZipProcessor zipProcessor = new ZipProcessor();
        Map<String, File> fileList = zipProcessor.Run(inFile, outDirectory);

        
        String programPath = unzip1.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File outLog = new File(programPath + "extraction.xml");
        
        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(outLog.getAbsolutePath()),
                                    "UTF8"));


            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by unzip1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
            writer.write("<unzip1-extraction>\n");
            
            for (Map.Entry<String, File> entry : fileList.entrySet())
            {
                String key = entry.getKey();
                File value = entry.getValue();
                
                writer.write("  <file path=\"" + value.getAbsolutePath() + "\">" + key + "</file>\n");
                
                System.out.println("unzip1: File '" + value.getAbsolutePath() + "' extracted.");
            }

            writer.write("</unzip1-extraction>\n");

            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-18);
        }
        

        zipProcessor.Clean();

        System.exit(0);
    }
}
