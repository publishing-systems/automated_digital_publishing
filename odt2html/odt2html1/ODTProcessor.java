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
 * @file $/ODTProcessor.java
 * @brief Processor to operate on the unpacked ODT file.
 * @author Stephan Kreutzer
 * @since 2014-04-19
 */



import java.io.File;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



class ODTProcessor
{
    public ODTProcessor(Map<String, File> odtFiles)
    {
        this.odtFiles = odtFiles;
    }

    public int Run(String outFileName, File outDirectory)
    {
        {
            File mimetypeFile = this.odtFiles.get("mimetype");
        
            if (mimetypeFile == null)
            {
                System.out.println("odt2html1: 'mimetype' file of the ODT file is missing.");
                return -1;
            }
            
            String mimetypeString = null;
            
            try
            {
                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(
                                        new FileInputStream(mimetypeFile),
                                        "UTF8"));
            
                mimetypeString = reader.readLine();
            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
                return -2;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                return -3;
            }
            
            if (mimetypeString != null)
            {
                String requiredMimetypeString = new String("application/vnd.oasis.opendocument.text");
            
                if (mimetypeString.equals(requiredMimetypeString) != true)
                {
                    System.out.println("odt2html1: File isn't an ODT file - mimetype '" + requiredMimetypeString + "' expected, but mimetype '" + mimetypeString + "' found.");
                    return -4;
                }
            }
            else
            {
                System.out.println("odt2html1: 'mimetype file of the ODT file doesn't contain a line.");
                return -5;
            }
        }
        
        {
            File manifestFile = this.odtFiles.get("META-INF/manifest.xml");
        
            if (manifestFile == null)
            {
                System.out.println("odt2html1: 'META-INF/manifest.xml' file of the ODT file is missing.");
                return -6;
            }
            
            ODTManifestProcessor manifestProcessor = new ODTManifestProcessor(manifestFile);
            
            int result = manifestProcessor.Run();
            
            if (result != 0)
            {
                return -7;
            }
            
            Map<String, String> manifestInfo = manifestProcessor.GetManifestInfo();
            
            if (manifestInfo.containsKey("manifestVersion") == true)
            {
                String manifestVersion = manifestInfo.get("manifestVersion");
                
                if (manifestVersion.equalsIgnoreCase("1.2") != true)
                {
                    System.out.println("odt2html1: Found unsupported manifest version '" + manifestVersion + "' in 'META-INF/manifest.xml'.");
                    return -8;
                }
            }
            else
            {
                System.out.println("odt2html1: No manifest version found in 'META-INF/manifest.xml'.");
                return -9;
            }
        }
        
        Map<String, String> styleMappings = null;
        
        {
            File stylesFile = this.odtFiles.get("styles.xml");
            
            if (stylesFile == null)
            {
                System.out.println("odt2html1: 'styles.xml' file of the ODT file is missing.");
                return -15;
            }
            
            ODTStylesProcessor stylesProcessor = new ODTStylesProcessor(stylesFile);
            
            int result = stylesProcessor.Analyze();
            
            if (result != 0)
            {
                return -16;
            }
            
            Map<String, String> stylesInfo = stylesProcessor.GetStylesInfo();
            
            if (stylesInfo.containsKey("stylesVersion") == true)
            {
                String stylesVersion = stylesInfo.get("stylesVersion");
                
                if (stylesVersion.equalsIgnoreCase("1.2") != true)
                {
                    System.out.println("odt2html1: Found unsupported styles version '" + stylesVersion + "' in 'styles.xml'.");
                    return -17;
                }
            }
            else
            {
                System.out.println("odt2html1: No styles version found in 'styles.xml'.");
                return -18;
            }
            
            styleMappings = stylesProcessor.GetStyleMappings();
        }
        
        if (styleMappings == null)
        {
            System.out.println("odt2html1: Style mapping wasn't constructed.");
            return -19;
        }
        
        {
            File contentFile = this.odtFiles.get("content.xml");
        
            if (contentFile == null)
            {
                System.out.println("odt2html1: 'content.xml' file of the ODT file is missing.");
                return -10;
            }
            
            ODTContentProcessor contentProcessor = new ODTContentProcessor(contentFile, styleMappings);
            
            int result = contentProcessor.Analyze();
            
            if (result != 0)
            {
                return -11;
            }
            
            Map<String, String> contentInfo = contentProcessor.GetContentInfo();
            
            if (contentInfo.containsKey("contentVersion") == true)
            {
                String contentVersion = contentInfo.get("contentVersion");
                
                if (contentVersion.equalsIgnoreCase("1.2") != true)
                {
                    System.out.println("odt2html1: Found unsupported content version '" + contentVersion + "' in 'content.xml'.");
                    return -12;
                }
            }
            else
            {
                System.out.println("odt2html1: No content version found in 'content.xml'.");
                return -13;
            }


            result = contentProcessor.Run(outFileName, outDirectory);
            
            if (result != 0)
            {
                return -14;
            }
        }

        return 0;
    }
    
    private Map<String, File> odtFiles;
}
