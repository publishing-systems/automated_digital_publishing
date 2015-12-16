/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of epub2html1.
 *
 * epub2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epub2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epub2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/epub2html1.java
 * @brief The main module that delegates to the specific processors.
 * @author Stephan Kreutzer
 * @since 2014-07-08
 */



import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class epub2html1
{
    public static void main(String args[])
    {
        System.out.print("epub2html1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");

        if (args.length < 1)
        {
            System.out.print("Usage:\n" +
                             "\tepub2html1 config-file\n\n");

            System.exit(1);
        }

        System.out.print("epub2html1: Reading configuration.\n");

        ConfigProcessor configuration = new ConfigProcessor(args[0]);
        configuration.run();

        File inFile = configuration.GetInFile();
        String inFileType = configuration.GetInFileType();
        File outDirectory = configuration.GetOutDirectory();

        if (inFile == null)
        {
            System.out.println("epub2html1: No input file specified.");
            System.exit(-1);
        }

        if (outDirectory == null)
        {
            System.out.println("epub2html1: No output directory specified.");
            System.exit(-1);
        }

        String programPath = epub2html1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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

        File tempDirectory = new File(programPath + "temp" + File.separator);
        File epubDirectory = null;
        Map<String, File> epubFiles = null;

        if (inFileType.equalsIgnoreCase("epub") == true)
        {
            if (tempDirectory.exists() == true)
            {
                if (tempDirectory.isDirectory() == true)
                {
                    if (tempDirectory.canWrite() != true)
                    {
                        System.out.println("epub2html1: Can't write to temporary directory '" + tempDirectory.getAbsolutePath() + "'.");
                        System.exit(-1);
                    }
                }
                else
                {
                    System.out.println("epub2html1: Temporary path '" + tempDirectory.getAbsolutePath() + "' isn't a directory.");
                    System.exit(-1);
                }
            }
            else
            {
                if (tempDirectory.mkdir() != true)
                {
                    System.out.println("epub2html1: Can't create temporary directory '" + tempDirectory.getAbsolutePath() + "'.");
                    System.exit(-1);
                }
            }

            epubDirectory = new File(tempDirectory.getAbsolutePath() + "/epub_extraction/");

            if (epubDirectory.exists() == true)
            {
                if (epubDirectory.isDirectory() == true)
                {
                    if (epubDirectory.canWrite() != true)
                    {
                        System.out.println("epub2html1: Can't write to temporary EPUB extraction directory '" + epubDirectory.getAbsolutePath() + "'.");
                        System.exit(-1);
                    }
                }
                else
                {
                    System.out.println("epub2html1: Temporary EPUB extraction path '" + epubDirectory.getAbsolutePath() + "' isn't a directory.");
                    System.exit(-1);
                }
            }
            else
            {
                if (epubDirectory.mkdir() != true)
                {
                    System.out.println("epub2html1: Can't create temporary EPUB extraction directory '" + epubDirectory.getAbsolutePath() + "'.");
                    System.exit(-1);
                }
            }

            System.out.println("epub2html1: Extracting EPUB file '" + inFile.getAbsolutePath() + "'.");

            ZipProcessor zipProcessor = new ZipProcessor();
            epubFiles = zipProcessor.Run(inFile, epubDirectory);
        }
        
        if (epubFiles == null)
        {
            System.out.println("epub2html1: No EPUB file list for '" + inFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        
        if (epubFiles.isEmpty())
        {
            System.out.println("epub2html1: EPUB file list for '" + inFile.getAbsolutePath() + "' is empty.");
            System.exit(-1);
        }
        
        if (epubDirectory == null)
        {
            System.out.println("epub2html1: No EPUB directory.");
            System.exit(-1);
        }
        
        EPUBProcessor processor = new EPUBProcessor(epubFiles, epubDirectory);
        processor.Run(outDirectory);

        System.exit(0);
    }
}
