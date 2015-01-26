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
 * @file $/ZipProcessor.java
 * @brief Processor to unpack (unzip) a Zip compressed archive.
 * @author Stephan Kreutzer
 * @since 2014-07-01
 */



import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.io.FileInputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



class ZipProcessor
{
    public ZipProcessor()
    {
        this.fileList = new HashMap<String, File>();
    }

    public Map<String, File> Run(File inFile, File outDirectory)
    {
        Clean();
        
        try
        {
            FileInputStream inStream = new FileInputStream(inFile);
            ZipInputStream zipStream = new ZipInputStream(inStream);
            
            byte[] buffer = new byte[1024];
            int length = 0;
            
            ZipEntry zipEntry = zipStream.getNextEntry();
            
            while (zipEntry != null)
            {
                String fileName = zipEntry.getName();
                File file = new File(outDirectory.getAbsolutePath() + File.separator + fileName);
                
                if (zipEntry.isDirectory() == true)
                {
                    CreateTempDirectories(fileName, outDirectory);
                }
                else
                {
                    File parentDirectory = new File(file.getParent());
                    
                    if (parentDirectory.exists() != true)
                    {
                        int separatorPosition = fileName.lastIndexOf("/");
                        
                        if (separatorPosition < 0)
                        {
                            separatorPosition = fileName.lastIndexOf("\\");
                        }
                    
                        if (separatorPosition > 0)
                        {
                            String parentDirectoryName = fileName.substring(0, separatorPosition + new String("/").length());
                            CreateTempDirectories(parentDirectoryName, outDirectory);
                        }
                    }

                    
                    if (parentDirectory.exists() != true)
                    {
                        System.out.println("unzip1: Extraction parent directory '" + parentDirectory.getAbsolutePath() + "' doesn't exist, but should be already created.");
                        Clean();
                        System.exit(-7);
                    }
                    else
                    {
                        if (parentDirectory.isDirectory() != true)
                        {
                            System.out.println("unzip1: Extraction path '" + parentDirectory.getAbsolutePath() + "' is supposed to specify a parent directory, but a file was found.");
                            Clean();
                            System.exit(-8);
                        }
                    }


                    if (this.fileList.containsKey(fileName) != true)
                    {
                        this.fileList.put(fileName, file);
                    }
                    else
                    {
                        System.out.println("unzip1: Duplicate Zip entry '" + fileName + "'.");
                        Clean();
                        System.exit(-9);
                    }
                    
                
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                   
                    // Retrieving the ZipEntry has set the zipStream to
                    // the corresponding position.
                    while ((length = zipStream.read(buffer)) > 0)
                    {
                        fileOutputStream.write(buffer, 0, length);
                    }
                    
                    fileOutputStream.close();
                }

                zipEntry = zipStream.getNextEntry();
            }
            
            zipStream.closeEntry();
            zipStream.close();  
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-10);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-11);
        }
        
        return this.fileList;
    }
    
    private void CreateTempDirectories(String fileName, File outDirectory)
    {
        String directoryName = "";
        String directoryPath = outDirectory.getAbsolutePath();
        int start = 0;
        int end = fileName.indexOf("/");
        
        if (end < 0)
        {
            end = fileName.indexOf("\\");
        }
        
        while (end > 0)
        {   
            String directoryPathPart = fileName.substring(start, end);
            directoryName += directoryPathPart + File.separator;
            directoryPath += File.separator + directoryPathPart;
            File directory = new File(directoryPath);
            
            if (directory.exists() != true)
            {
                if (directory.mkdirs() == true)
                {
                    if (this.fileList.containsKey(directoryName) != true)
                    {
                        this.fileList.put(directoryName, directory);
                    }
                    else
                    {
                        System.out.println("unzip1: Duplicate Zip entry '" + directoryPath + "'.");
                        Clean();
                        System.exit(-12);
                    }
                }
                else
                {
                    System.out.println("unzip1: Can't create extraction directory '" + directory.getAbsolutePath() + "'.");
                    Clean();
                    System.exit(-13);
                }
            }
            else
            {
                if (directory.isDirectory() != true)
                {
                    System.out.println("unzip1: Extraction path '" + directory.getAbsolutePath() + "' is supposed to specify a directory, but an already existing file was found.");
                    Clean();
                    System.exit(-14);
                }
            }
            
            start = end + File.separator.length();
            
            {
                int separatorPositionSlash = fileName.indexOf('/', start);
                int separatorPositionBackslash = fileName.indexOf('\\', end);
                
                if (separatorPositionSlash > 0 &&
                    separatorPositionBackslash > 0)
                {
                    end = Math.min(separatorPositionSlash, separatorPositionBackslash);
                }
                else
                {
                    if (separatorPositionSlash <= 0 &&
                        separatorPositionBackslash <= 0)
                    {
                        end = separatorPositionSlash;
                    }
                    else
                    {
                        if (separatorPositionSlash > 0)
                        {
                            end = separatorPositionSlash;
                        }
                        else
                        {
                            end = separatorPositionBackslash;
                        }
                    }
                }
            }


            if (end <= 0 &&
                start < (fileName.length() - 1))
            {
                end = fileName.length();
            }
        }
    }
    
    public void Clean()
    {
        this.fileList.clear();
    }
    
    private Map<String, File> fileList;
}
