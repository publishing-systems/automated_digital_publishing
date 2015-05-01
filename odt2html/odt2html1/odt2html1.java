/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of odt2html1.
 *
 * odt2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/odt2html1.java
 * @brief The main module that delegates to the specific processors.
 * @attention Program isn't thread-safe! Don't run two instances of it at
 *     the same time, access to the temporary extraction directory would conflict.
 * @author Stephan Kreutzer
 * @since 2014-04-18
 */



import java.io.File;
import java.util.Map;



public class odt2html1
{
    public static void main(String args[])
    {
        System.out.print("odt2html1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\todt2html1 odt-in-file html-out-directory [html-out-name]\n\n" +
                             "Please note that odt2html1 will overwrite existing files in\n" +
                             "html-out-directory.\n\n");

            System.exit(1);
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("odt2html1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("odt2html1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("odt2html1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }


        File outDirectory = new File(args[1]);

        if (outDirectory.exists() == true)
        {
            if (outDirectory.isDirectory() == true)
            {
                if (outDirectory.canWrite() != true)
                {
                    System.out.println("odt2html1: Can't write to directory '" + outDirectory.getAbsolutePath() + "'.");
                    System.exit(-4);
                }
            }
            else
            {
                System.out.println("odt2html1: Out path '" + outDirectory.getAbsolutePath() + "' isn't a directory.");
                System.exit(-5);
            }
        }
        else
        {
            if (outDirectory.mkdirs() != true)
            {
                System.out.println("odt2html1: Can't create out directory '" + outDirectory.getAbsolutePath() + "'.");
                System.exit(-6);
            }
        }


        ZipProcessor zipProcessor = new ZipProcessor();
        Map<String, File> fileList = zipProcessor.Run(inFile);

        ODTProcessor odtProcessor = new ODTProcessor(fileList);


        String outFileName = null;
        
        if (args.length >= 3)
        {
            if (args[2].equalsIgnoreCase("info.xml") == true)
            {
                System.out.println("odt2html1: The output file name 'info.xml' is reserved.");
                System.exit(-1);
            }
        
            outFileName = args[2];
            
            File outFile = new File(outDirectory.getAbsolutePath() + File.separator + args[2]);

            if (outFile.getAbsoluteFile().getParent().equalsIgnoreCase(outDirectory.getAbsolutePath()) != true)
            {
                System.out.println("odt2html1: The output file name '" + args[2] + "' would lead to the path '" + outFile.getAbsolutePath() + "', which isn't located in the output directory '" + outDirectory.getAbsolutePath() + "'.");
                System.exit(-1);
            }
        }
        else
        {
            outFileName = inFile.getName();
        
            int extensionPosition = outFileName.lastIndexOf(".");
            
            if (extensionPosition >= 0)
            {
                outFileName = outFileName.substring(0, extensionPosition); 
            }
            
            outFileName += ".html";
        }


        int result = odtProcessor.Run(outFileName, outDirectory);

        if (result != 0)
        {
            zipProcessor.Clean();
            System.exit(-15);
        }


        zipProcessor.Clean();

        System.exit(0);
    }
}
