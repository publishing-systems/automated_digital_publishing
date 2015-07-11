/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of epubcheck1_recursive_checker1 workflow.
 *
 * epubcheck1_recursive_checker1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epubcheck1_recursive_checker1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epubcheck1_recursive_checker1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/epubcheck/epubcheck1/workflows/epubcheck1_recursive_checker1.java
 * @brief Workflow calls epubcheck on EPUB files (directly referenced or found in
 *     directories, even recursively).
 * @author Stephan Kreutzer
 * @since 2015-07-03
 */



import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;



public class epubcheck1_recursive_checker1
{
    public static void main(String args[])
    {
        System.out.print("epubcheck1_recursive_checker1 workflow Copyright (C) 2015 Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        epubcheck1_recursive_checker1 checker = new epubcheck1_recursive_checker1();
        checker.check(args);
    }
    
    public int check(String args[])
    {
        if (args.length < 2)
        {
            System.out.print(getI10nString("messageArgumentsMissingUsage") + "\n" +
                             "\tepubcheck1_recursive_checker1 " + getI10nString("messageParameterList") + "\n\n");
            System.exit(1);
        }

        String programPath = epubcheck1_recursive_checker1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        File jobFile = new File(args[0]);

        if (jobFile.exists() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageJobfileFileDoesntExist"));
            System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

            System.exit(-1);
        }

        if (jobFile.isFile() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageJobfileIsntAFile"));
            System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

            System.exit(-1);
        }

        if (jobFile.canRead() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageJobfileCantReadFile"));
            System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

            System.exit(-1);
        }
        
        File outDirectory = new File(args[1]);
        
        if (outDirectory.exists() == true)
        {
            if (outDirectory.isDirectory() != true)
            {
                Object[] messageArguments = { outDirectory.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageOutDirectoryIsntDirectory"));
                System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

                System.exit(-1);
            }
        }


        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(jobFile);
            document.getDocumentElement().normalize();

            NodeList inputNodeList = document.getElementsByTagName("input");

            if (inputNodeList.getLength() <= 0)
            {
                Object[] messageArguments = { new String("input"),
                                              jobFile.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageJobfileNoInputEntries"));
                System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

                return 1;
            }

            this.epubFiles = new ArrayList<File>();
            this.checkResults = new HashMap<Integer, String>();

            for (int i = 0; i < inputNodeList.getLength(); i++)
            {
                Node inputNode = inputNodeList.item(i);
                NamedNodeMap attributes = inputNode.getAttributes();

                if (attributes == null)
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageJobfileInputEntryMissingAttributes"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));
                    
                    continue;
                }
                
                Node attributePath = attributes.getNamedItem("path");
                
                if (attributePath == null)
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  new String("path"),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageJobfileInputEntryMissingPathAttribute"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

                    continue;
                }

                Node attributeRecursive = attributes.getNamedItem("recursive");

                if (attributeRecursive == null)
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  new String("recursive"),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageJobfileInputEntryMissingRecursiveAttribute"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

                    continue;
                }

                String recursiveAttribute = attributeRecursive.getTextContent();
                boolean recursive = false;

                if (recursiveAttribute.equals("true") == true)
                {
                    recursive = true;
                }
                else if (recursiveAttribute.equals("false") == true)
                {
                    recursive = false;
                }
                else
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  recursive,
                                                  new String("recursive"),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageJobfileInputEntryRecursiveAttributeInvalidValue"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

                    continue;
                }


                File inputFile = new File(attributePath.getTextContent());

                if (inputFile.isAbsolute() != true)
                {
                    String relativePath = jobFile.getAbsoluteFile().getParent();

                    if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                    {
                        // Remove dot that references the local, current directory.
                        relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                    }

                    if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                    {
                        relativePath += File.separator;
                    }

                    relativePath += attributePath.getTextContent();
                    inputFile = new File(relativePath);
                }

                if (inputFile.exists() != true)
                {
                    Object[] messageArguments = { inputFile.getAbsolutePath(),
                                                  new String("input"),
                                                  new Integer(i + 1),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageJobfileInputEntryReferencedFileDoesntExist"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));

                    continue;
                }
                
                if (inputFile.isFile() != true &&
                    inputFile.isDirectory() != true)
                {
                    Object[] messageArguments = { inputFile.getAbsolutePath(),
                                                  new String("input"),
                                                  new Integer(i + 1),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageJobfileInputEntryReferencedFileNeitherFileNorDirectory"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));
                    
                    continue;
                }
                
                AddInputFile(inputFile, recursive);
            }
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        

        File resultInfoFile = new File(outDirectory.getAbsolutePath() + File.separator + "result_info.xml");
        
        if (resultInfoFile.exists() == true)
        {
            if (resultInfoFile.isFile() != true)
            {
                Object[] messageArguments = { resultInfoFile.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageResultInfoFileIsntAFile"));
                System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));
            
                return -1;
            }
        }


        int epubFilesSize = this.epubFiles.size();

        for (int i = 0; i < epubFilesSize; i++)
        {
            // Only newer builds recognize i10n. For older builds, $/i10n/i10nGUI_*.properties
            // epubcheckNoErrorsNorWarningsMessage and epubcheckNoErrorsButWarnings need to
            // be changed to the English version, otherwise the result status won't get recognized
            // properly.
            ProcessBuilder builder = new ProcessBuilder("java", "-Duser.language=" + getLocale().getLanguage(), "-Duser.country=" + getLocale().getCountry(), "-jar", programPath + ".." + File.separator + "epubcheck.jar", this.epubFiles.get(i).getAbsolutePath());
            builder.directory(new File(programPath + ".."));
            builder.redirectErrorStream(true);

            try
            {
                File resultFile = new File(outDirectory.getAbsolutePath() + File.separator + "result_" + (i + 1) + ".log");
                String status = "error";
                
                if (resultFile.exists() == true)
                {
                    if (resultFile.isFile() != true)
                    {
                        Object[] messageArguments = { resultFile.getAbsolutePath() };
                        MessageFormat formatter = new MessageFormat("");
                        formatter.setLocale(getLocale());

                        formatter.applyPattern(getI10nString("messageResultLogIsntAFile"));
                        System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));
                        
                        return -1;
                    }
                }

                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(resultFile.getAbsolutePath()),
                                        "UTF8"));

                Process process = builder.start();
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");

                while (scanner.hasNext() == true)
                {
                    String line = scanner.next();
                    writer.write(line + "\n");
                    System.out.println(line);
                    
                    if (line.equals(getI10nString("epubcheckNoErrorsNorWarningsMessage")) == true)
                    {
                        status = "valid";
                    }
                    else if (line.equals(getI10nString("epubcheckNoErrorsButWarnings")) == true)
                    {
                        status = "warning";
                    }
                }

                scanner.close();

                writer.flush();
                writer.close();

                if (this.checkResults.containsKey(i) != true)
                {
                    this.checkResults.put(i, status);
                }
                else
                {
                
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
        }

        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(resultInfoFile.getAbsolutePath()),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by epubcheck1_recursive_checker1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<epubcheck1-recursive-checker1-result-info>\n");

            for (int i = 0; i < epubFilesSize; i++)
            {
                String epubFilePath = this.epubFiles.get(i).getCanonicalPath();

                epubFilePath = epubFilePath.replaceAll("&", "&amp;");

                writer.write("  <check-result nr=\"" + (i + 1) + "\" input=\"" + epubFilePath + "\" result=\"result_" + (i + 1) + ".log\"");

                if (this.checkResults.containsKey(i) == true)
                {
                    String status = this.checkResults.get(i);
                    
                    writer.write(" status=\"" + status + "\"");
                }
                else
                {
                    Object[] messageArguments = { i };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageResultStatusMissing"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments)); 
                }

                writer.write("/>\n"); 
            }

            writer.write("</epubcheck1-recursive-checker1-result-info>\n");

            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return 0;
    }
    
    /**
     * @param[in] inputFile Needs to be an absolute path.
     */
    public void AddInputFile(File inputFile, boolean recursive)
    {
        if (inputFile.isFile() == true)
        {
            if (inputFile.getName().endsWith(".epub") == true)
            {
                this.epubFiles.add(inputFile);
            }
        }
        else if (inputFile.isDirectory() == true)
        {
            String directoryPath = inputFile.getAbsolutePath() + File.separator;

            String entries[];
            entries = inputFile.list();

            for (int i = 0; i < entries.length; i++)
            {
                File childFile = new File(directoryPath + entries[i]);

                if (childFile.exists() != true)
                {
                    Object[] messageArguments = { entries[i],
                                                  directoryPath };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageFindInputFilesChildDoesntExist"));
                    System.out.println("epubcheck1_recursive_checker1 workflow: " + formatter.format(messageArguments));
                    
                    continue;
                }

                if (childFile.isFile() == true)
                {
                    AddInputFile(childFile, false);
                }
                else if (childFile.isDirectory() == true &&
                         recursive == true)
                {
                    AddInputFile(childFile, recursive);
                }
            }
        }
    }

    public Locale getLocale()
    {
        return Locale.getDefault();
    }

    /**
     * @brief This method interprets i10n strings from a .properties file as encoded in UTF-8.
     */
    public String getI10nString(String key)
    {
        if (this.i10nGUI == null)
        {
            this.i10nGUI = ResourceBundle.getBundle("i10n.i10nGUI", getLocale());
        }
    
        try
        {
            return new String(this.i10nGUI.getString(key).getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return this.i10nGUI.getString(key);
        }
    }

    private ResourceBundle i10nGUI;
    protected List<File> epubFiles;
    protected Map<Integer, String> checkResults;
}
