/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of downloader1.
 *
 * downloader1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * downloader1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; replacementout even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along replacement downloader1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/downloader1.java
 * @author Stephan Kreutzer
 * @since 2015-04-20
 */



import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;



public class downloader1
{
    public static void main(String args[])
    {
        System.out.print("downloader1  Copyright (C) 2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\tdownloader1 in-url out-file\n\n");
            System.exit(1);
        }

        String programPath = downloader1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        // http://stackoverflow.com/questions/11650375/downloading-files-using-java-randomly-freezes
        // https://community.oracle.com/thread/2311363
        System.setProperty("java.net.preferIPv4Stack", "true");


        BufferedInputStream in = null;
        FileOutputStream out = null;

        try
        {
            in = new BufferedInputStream(new URI(args[0]).toURL().openStream());
            out = new FileOutputStream(args[1]);

            byte data[] = new byte[1024];
            int count = in.read(data, 0, 1024);
 
            while (count != -1)
            {
                out.write(data, 0, count);
                count = in.read(data, 0, 1024);
            }
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }

                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
        }

        return;
    }
}
