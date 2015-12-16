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
 * @file $/ZipProcessor.java
 * @brief Processor to unpack (unzip) the ODT file.
 * @author Stephan Kreutzer
 * @since 2014-04-19
 */



import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipInputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



class ZipProcessor
{
    public ZipProcessor()
    {
        this.fileList = new HashMap<String, File>();

        String programPath = ZipProcessor.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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

        this.tempDirectory = new File(programPath + "temp" + File.separator);

        if (this.tempDirectory.exists() == true)
        {
            if (this.tempDirectory.isDirectory() == true)
            {
                if (this.tempDirectory.canWrite() != true)
                {
                    System.out.println("odt2html1: Can't write to temporary ODT extraction directory '" + this.tempDirectory.getAbsolutePath() + "'.");
                    System.exit(-4);
                }
            }
            else
            {
                System.out.println("odt2html1: Temporary ODT extraction path '" + this.tempDirectory.getAbsolutePath() + "' isn't a directory.");
                System.exit(-5);
            }
        }
        else
        {
            if (this.tempDirectory.mkdir() != true)
            {
                System.out.println("odt2html1: Can't create temporary ODT extraction directory '" + this.tempDirectory.getAbsolutePath() + "'.");
                System.exit(-6);
            }
        }
    }

    public Map<String, File> Run(File inODTFile)
    {
        Clean();
        
        try
        {
            FileInputStream inStream = new FileInputStream(inODTFile);
            ZipInputStream zipStream = new ZipInputStream(inStream);
            
            byte[] buffer = new byte[1024];
            int length = 0;
            
            ZipEntry zipEntry = zipStream.getNextEntry();
            
            while (zipEntry != null)
            {
                String fileName = zipEntry.getName();
                File file = new File(tempDirectory.getAbsolutePath() + File.separator + fileName);
                
                if (zipEntry.isDirectory() == true)
                {
                    CreateTempDirectories(fileName);
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
                            CreateTempDirectories(parentDirectoryName);
                        }
                    }

                    
                    if (parentDirectory.exists() != true)
                    {
                        System.out.println("odt2html1: Temporary ODT extraction parent directory '" + parentDirectory.getAbsolutePath() + "' doesn't exist, but should be already created.");
                        Clean();
                        System.exit(-7);
                    }
                    else
                    {
                        if (parentDirectory.isDirectory() != true)
                        {
                            System.out.println("odt2html1: Temporary ODT extraction path '" + parentDirectory.getAbsolutePath() + "' is supposed to specify a parent directory, but a file was found.");
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
                        System.out.println("odt2html1: Duplicate ODT entry '" + fileName + "'.");
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
    
    private void CreateTempDirectories(String fileName)
    {
        String directoryName = "";
        String directoryPath = this.tempDirectory.getAbsolutePath();
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
                        System.out.println("odt2html1: Duplicate ODT entry '" + directoryPath + "'.");
                        Clean();
                        System.exit(-12);
                    }
                }
                else
                {
                    System.out.println("odt2html1: Can't create temporary ODT extraction directory '" + directory.getAbsolutePath() + "'.");
                    Clean();
                    System.exit(-13);
                }
            }
            else
            {
                if (directory.isDirectory() != true)
                {
                    System.out.println("odt2html1: Temporary ODT extraction path '" + directory.getAbsolutePath() + "' is supposed to specify a directory, but an already existing file was found.");
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
        ArrayList<String> directoryList = new ArrayList<String>();
    
        for (Map.Entry<String, File> mapEntry : this.fileList.entrySet())
        {
            File file = mapEntry.getValue();

            if (file.isDirectory() != true)
            {
                if (file.delete() != true)
                {
                    System.out.println("odt2html1: Can't delete temporarily extracted ODT file '" + file.getAbsolutePath() + "'.");
                }
            }
            else
            {
                directoryList.add(mapEntry.getKey());
            }
        }

        Collections.sort(directoryList);

        for (int i = directoryList.size() - 1; i >= 0; i--)
        {
            File directory = this.fileList.get(directoryList.get(i));
            
            if (directory.delete() != true)
            {
                System.out.println("odt2html1: Can't delete temporarily extracted ODT directory '" + directory.getAbsolutePath() + "'.");
            }
        }

        this.fileList.clear();
    }
    
    private Map<String, File> fileList;
    private File tempDirectory;
}
