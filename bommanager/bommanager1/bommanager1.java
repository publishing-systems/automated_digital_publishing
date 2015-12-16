/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of bommanager1.
 *
 * bommanager1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * bommanager1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with bommanager1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/bommanager1.java
 * @brief Is able to handle XML BOMs.
 * @details UTF-8 support only.
 * @author Stephan Kreutzer
 * @since 2015-02-12
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class bommanager1
{
    public static void main(String args[])
    {
        System.out.print("bommanager1  Copyright (C) 2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\tbommanager1 in-file operation out-file\n" +
                             "\n" +
                             "operation needs to be 'add' or 'remove'.\n\n");

            System.exit(1);
        }


        String programPath = bommanager1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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

        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.println("bommanager1: Input file '" + inFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.println("bommanager1: Input path '" + inFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (inFile.canRead() != true)
        {
            System.out.println("bommanager1: Input file '" + inFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        File tempDirectory = new File(programPath + "temp");

        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.println("bommanager1: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.");
                System.exit(-1);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.println("bommanager1: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.");
                System.exit(-1);
            }
        }

        File tempCopyFile = new File(tempDirectory.getAbsolutePath() + File.separator + "in.file");

        // Needed to allow both input and output path to be the same.
        if (CopyFileBinary(inFile, tempCopyFile) != 0)
        {
            System.out.println("bommanager1: Can't copy the input file '" + inFile.getAbsolutePath() + "' to the temporary path '" + tempCopyFile.getAbsolutePath() + File.separator + "in.file'.");
            System.exit(-1);
        }

        byte[] bomUTF8 = { (byte)0xEF, (byte)0xBB, (byte)0xBF };

        if (args[1].equalsIgnoreCase("add") == true)
        {
            AddBOM(tempCopyFile, bomUTF8, new File(args[2]));
        }
        else if (args[1].equalsIgnoreCase("remove") == true)
        {
            RemoveBOM(tempCopyFile, bomUTF8, new File(args[2]));
        }
        else
        {
            System.out.println("bommanager1: Operation '" + args[1] + "' isn't supported.");
            System.exit(-1);
        }

        System.exit(0);
    }

    public static int AddBOM(File in, byte[] bom, File out)
    {
        byte[] buffer = new byte[1024];

        try
        {
            out.createNewFile();

            FileInputStream reader = new FileInputStream(in);
            FileOutputStream writer = new FileOutputStream(out);

            int bytesRead = reader.read(buffer, 0, buffer.length);
            boolean first = true;

            while (bytesRead > 0)
            {
                if (first == true)
                {
                    first = false;

                    if (bytesRead < bom.length)
                    {
                        writer.write(bom, 0, bom.length);
                        writer.write(buffer, 0, bytesRead);
                        bytesRead = reader.read(buffer, 0, buffer.length);

                        System.out.println("bommanager1: BOM added.");
                    }
                    else
                    {
                        boolean bomPresent = true;
                    
                        for (int i = 0; i < bom.length; i++)
                        {
                            if (buffer[i] != bom[i])
                            {
                                bomPresent = false;
                                break;
                            }
                        }
                        
                        if (bomPresent == true)
                        {
                            writer.write(buffer, 0, bytesRead);
                            bytesRead = reader.read(buffer, 0, buffer.length);
                            
                            System.out.println("bommanager1: BOM is already present.");
                        }
                        else
                        {
                            writer.write(bom, 0, bom.length);
                            writer.write(buffer, 0, bytesRead);
                            bytesRead = reader.read(buffer, 0, buffer.length);

                            System.out.println("bommanager1: BOM added.");
                        }
                    }
                }
                else
                {
                    writer.write(buffer, 0, bytesRead);
                    bytesRead = reader.read(buffer, 0, buffer.length);
                }
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
    
    public static int RemoveBOM(File in, byte[] bom, File out)
    {
        byte[] buffer = new byte[1024];

        try
        {
            out.createNewFile();

            FileInputStream reader = new FileInputStream(in);
            FileOutputStream writer = new FileOutputStream(out);

            int bytesRead = reader.read(buffer, 0, buffer.length);
            boolean first = true;

            while (bytesRead > 0)
            {
                if (first == true)
                {
                    first = false;

                    if (bytesRead < bom.length)
                    {
                        writer.write(buffer, 0, bytesRead);
                        bytesRead = reader.read(buffer, 0, buffer.length);

                        System.out.println("bommanager1: No BOM was present to remove.");
                    }
                    else
                    {
                        boolean bomPresent = true;
                    
                        for (int i = 0; i < bom.length; i++)
                        {
                            if (buffer[i] != bom[i])
                            {
                                bomPresent = false;
                                break;
                            }
                        }
                        
                        if (bomPresent == true)
                        {
                            writer.write(buffer, bom.length, bytesRead - bom.length);
                            bytesRead = reader.read(buffer, 0, buffer.length);
                            
                            System.out.println("bommanager1: BOM removed.");
                        }
                        else
                        {
                            writer.write(buffer, 0, bytesRead);
                            bytesRead = reader.read(buffer, 0, buffer.length);

                            System.out.println("bommanager1: No BOM was present to remove.");
                        }
                    }
                }
                else
                {
                    writer.write(buffer, 0, bytesRead);
                    bytesRead = reader.read(buffer, 0, buffer.length);
                }
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

    public static int CopyFileBinary(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("bommanager1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("bommanager1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("bommanager1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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

