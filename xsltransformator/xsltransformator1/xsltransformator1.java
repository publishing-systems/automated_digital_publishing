/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of xsltransformator1.
 *
 * xsltransformator1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * xsltransformator1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with xsltransformator1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/xsltransformator1.java
 * @brief Java command line tool for using a Java XSLT processor.
 * @author Stephan Kreutzer
 * @since 2014-03-29
 */



import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;



public class xsltransformator1
{
    public static void main(String args[])
    {
        System.out.print("xsltransformator1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\txsltransformator1 in-file stylesheet-file out-file\n\n");

            System.exit(1);
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("xsltransformator1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("xsltransformator1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("xsltransformator1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }
        
        File stylesheetFile = new File(args[1]);

        if (stylesheetFile.exists() != true)
        {
            System.out.print("xsltransformator1: '" + stylesheetFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (stylesheetFile.isFile() != true)
        {
            System.out.print("xsltransformator1: '" + stylesheetFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (stylesheetFile.canRead() != true)
        {
            System.out.print("xsltransformator1: '" + stylesheetFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }
        
        File outFile = new File(args[2]);


        Source inSource = new StreamSource(inFile);
        Source stylesheetSource = new StreamSource(stylesheetFile);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try
        {
            Transformer transformer = transformerFactory.newTransformer(stylesheetSource);
            transformer.transform(inSource, new StreamResult(outFile));
        }
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-7);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-8);
        }

        System.exit(0);
    }
}
