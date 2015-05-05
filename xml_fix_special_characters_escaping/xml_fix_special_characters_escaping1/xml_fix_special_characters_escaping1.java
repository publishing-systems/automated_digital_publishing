/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of xml_fix_special_characters_escaping1.
 *
 * xml_fix_special_characters_escaping1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * xml_fix_special_characters_escaping1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with xml_fix_special_characters_escaping1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/xml_fix_special_characters_escaping1.java
 * @brief Fix XML special characters which aren't properly escaped
 *     as XML entity.
 * @details Currently, only ampersands gets fixed.
 * @author Stephan Kreutzer
 * @since 2015-04-23
 */



import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;



public class xml_fix_special_characters_escaping1
{
    public static void main(String args[])
    {
        System.out.print("xml_fix_special_characters_escaping1 Copyright (C) 2015 Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        String programPath = xml_fix_special_characters_escaping1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\txml_fix_special_characters_escaping1 in-file out-file\n\n");

            System.exit(1);
        }


        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("xml_fix_special_characters_escaping1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("xml_fix_special_characters_escaping1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("xml_fix_special_characters_escaping1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }

        File tempDirectory = new File(programPath + "temp");

        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("xml_fix_special_characters_escaping1: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("xml_fix_special_characters_escaping1: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }

        File sourceFile = new File(tempDirectory.getAbsolutePath() + File.separator + "in.xml");
        File destinationFile = new File(tempDirectory.getAbsolutePath() + File.separator + "out.xml");

        if (xml_fix_special_characters_escaping1.CopyFileBinary(inFile, sourceFile) != 0)
        {
            System.exit(-1);
        }


        char[] buffer = new char[1024];
        char[] matchBuffer = new char[1024];

        boolean matching = false;
        int matchCount = 0;

        try
        {
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(sourceFile),
                                    "UTF8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(destinationFile),
                                    "UTF8"));
            int charactersRead = reader.read(buffer, 0, buffer.length);

            while (charactersRead > 0)
            {
                for (int i = 0; i < charactersRead; i++)
                {
                    if (matching == false)
                    {
                        if (buffer[i] == '&')
                        {
                            matching = true;
                        }
                        else
                        {
                            writer.write(buffer[i]);
                        }
                    }
                    else
                    {
                        if (matchCount >= matchBuffer.length)
                        {
                            System.out.println("xml_fix_special_characters_escaping1: Maximum size of " + matchBuffer.length + " characters for a possible XML entity exceeded.");
                            System.exit(-1);
                        }

                        if (Character.isLetter(buffer[i]) == true)
                        {
                            matchBuffer[matchCount] = buffer[i];
                            matchCount++;
                        }
                        else if (Character.isDigit(buffer[i]) == true)
                        {
                            if (matchCount == 0)
                            {
                                writer.write("&amp;");
                                writer.write(buffer[i]);

                                matching = false;
                                matchCount = 0;
                            }
                            else
                            {
                                matchBuffer[matchCount] = buffer[i];
                                matchCount++;
                            }
                        }
                        else if (buffer[i] == ';')
                        {
                            if (matchCount == 0)
                            {
                                writer.write("&amp;");
                                writer.write(buffer[i]);

                                matching = false;
                                matchCount = 0;
                            }
                            else
                            {
                                // A valid XML entity.

                                writer.write("&");
                                writer.write(matchBuffer, 0, matchCount);
                                writer.write(buffer[i]);

                                matching = false;
                                matchCount = 0;
                            }
                        }
                        else if (Character.isSpaceChar(buffer[i]) == true)
                        {
                            writer.write("&amp;");
                            writer.write(matchBuffer, 0, matchCount);
                            writer.write(buffer[i]);

                            matching = false;
                            matchCount = 0;
                        }
                        else if (buffer[i] == '_' ||
                                 buffer[i] == '-' ||
                                 buffer[i] == '.')
                        {
                            matchBuffer[matchCount] = buffer[i];
                            matchCount++;
                        }
                        else
                        {
                            writer.write("&amp;");
                            writer.write(matchBuffer, 0, matchCount);
                            // buffer[i] needs to be checked in the next iteration
                            // (for "&&" for instance).
                            --i;

                            matching = false;
                            matchCount = 0;
                        }
                    }
                }

                charactersRead = reader.read(buffer, 0, buffer.length);
            }

            if (matchCount > 0)
            {
                writer.write("&amp;");
                writer.write(matchBuffer, 0, matchCount);
            }

            writer.close();
            reader.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
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


        if (xml_fix_special_characters_escaping1.CopyFileBinary(destinationFile, new File(args[1])) != 0)
        {
            System.exit(-1);
        }

        if (sourceFile.delete() != true)
        {
            System.out.println("xml_fix_special_characters_escaping1: Can't delete temporary file '" + sourceFile.getAbsolutePath() + "'.");
        }

        if (destinationFile.delete() != true)
        {
            System.out.println("xml_fix_special_characters_escaping1: Can't delete temporary file '" + destinationFile.getAbsolutePath() + "'.");
        }

        System.exit(0);
    }

    public static int CopyFileBinary(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("xml_fix_special_characters_escaping1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("xml_fix_special_characters_escaping1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("xml_fix_special_characters_escaping1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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

