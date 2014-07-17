/* Copyright (C) 2014  Stephan Kreutzer
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
        System.out.print("odt2html1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");

        if (args.length != 2)
        {
            System.out.print("Usage:\n" +
                             "\todt2html1 odt-in-file html-out-file\n\n");

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
        
        File outFile = new File(args[1]);


        ZipProcessor zipProcessor = new ZipProcessor();
        Map<String, File> fileList = zipProcessor.Run(inFile);

        ODTProcessor odtProcessor = new ODTProcessor(fileList);

        int result = odtProcessor.Run(outFile);

        if (result != 0)
        {
            zipProcessor.Clean();
            System.exit(-15);
        }


        zipProcessor.Clean();

        System.exit(0);
    }
}
