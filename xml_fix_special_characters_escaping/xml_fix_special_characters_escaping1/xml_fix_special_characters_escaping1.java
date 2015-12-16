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
 * @details Currently, only ampersands and single/double quotation marks
 *     within attributes get fixed.
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
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.net.URLDecoder;



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

        xml_fix_special_characters_escaping1 fixer = new xml_fix_special_characters_escaping1();
        fixer.fix(args);
    }

    public int fix(String args[])
    {
        if (args.length < 2)
        {
            System.out.print(getI10nString("messageArgumentsMissingUsage") + "\n" +
                             "\txml_fix_special_characters_escaping1 " + getI10nString("messageParameterList") + "\n\n");
            System.exit(1);
        }

        String programPath = xml_fix_special_characters_escaping1.class.getProtectionDomain().getCodeSource().getLocation().getPath();

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
            Object[] messageArguments = { inFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageInputFileDoesntExist"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

            System.exit(-4);
        }

        if (inFile.isFile() != true)
        {
            Object[] messageArguments = { inFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageInputPathIsntAFile"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

            System.exit(-5);
        }

        if (inFile.canRead() != true)
        {
            Object[] messageArguments = { inFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageInputFileIsntReadable"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

            System.exit(-6);
        }

        File tempDirectory = new File(programPath + "temp");

        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                Object[] messageArguments = { tempDirectory.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageCantCreateTempDirectory"));
                System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                System.exit(-1);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                Object[] messageArguments = { tempDirectory.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageTempPathExistsButIsntADirectory"));
                System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                System.exit(-1);
            }
        }


        this.ampersandsEscapedCount = 0;
        this.singleQuotationMarksEscapedCount = 0;
        this.doubleQuotationMarksEscapedCount = 0;

        // Fix ampersands.

        /**
         * @todo Doesn't recognize comments, CDATA etc.!
         */

        final int FA_STAGE_NONE = 0;
        final int FA_STAGE_AMPERSAND = 1;
        final int FA_STAGE_CHARACTER_REFERENCE = 2;
        final int FA_STAGE_CHARACTER_REFERENCE_HEX = 3;

        File sourceFile = new File(tempDirectory.getAbsolutePath() + File.separator + "in.xml");
        File destinationFile = new File(tempDirectory.getAbsolutePath() + File.separator + "out.xml");

        if (CopyFileBinary(inFile, sourceFile) != 0)
        {
            System.exit(-1);
        }


        char[] buffer = new char[2048];
        char[] matchBuffer = new char[2048];

        int stage = FA_STAGE_NONE;
        int matchCount = 0;

        try
        {
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(sourceFile),
                                    "UTF-8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(destinationFile),
                                    "UTF-8"));

            try
            {
                int charactersRead = reader.read(buffer, 0, buffer.length);

                while (charactersRead > 0)
                {
                    for (int i = 0; i < charactersRead; i++)
                    {
                        if (stage == FA_STAGE_NONE)
                        {
                            if (buffer[i] == '&')
                            {
                                stage = FA_STAGE_AMPERSAND;
                            }
                            else
                            {
                                writer.write(buffer[i]);
                            }
                        }
                        else if (stage == FA_STAGE_AMPERSAND)
                        {
                            // Valid XML entity names are defined as
                            // [4]  NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
                            // [4a] NameChar      ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
                            // This UTF-8-only program doesn't support Unicode supplementary characters, characters > 0xFFFF can't be
                            // represented in Java's char, and several ranges are considered as undefined by UTF-8 because one bit in
                            // them is used to signal that there is a second char for UTF-16.
                            if (buffer[i] == ':' ||
                                (buffer[i] >= 0x41 &&
                                 buffer[i] <= 0x5A) ||
                                buffer[i] == '_' ||
                                (buffer[i] >= 0x61 &&
                                 buffer[i] <= 0x7A) ||
                                (buffer[i] >= 0xC0 &&
                                 buffer[i] <= 0xD6) ||
                                (buffer[i] >= 0xD8 &&
                                 buffer[i] <= 0xF6) ||
                                (buffer[i] >= 0xF8 &&
                                 buffer[i] <= 0x2FF) ||
                                (buffer[i] >= 0x370 &&
                                 buffer[i] <= 0x37D) ||
                                (buffer[i] >= 0x37F &&
                                 buffer[i] <= 0x1FFF) ||
                                (buffer[i] >= 0x200C &&
                                 buffer[i] <= 0x200D) ||
                                (buffer[i] >= 0x2070 &&
                                 buffer[i] <= 0x218F) ||
                                (buffer[i] >= 0x2C00 &&
                                 buffer[i] <= 0x2FEF) ||
                                (buffer[i] >= 0x3001 &&
                                 buffer[i] <= 0xD7FF) ||
                                (buffer[i] >= 0xF900 &&
                                 buffer[i] <= 0xFDCF) ||
                                (buffer[i] >= 0xFDF0 &&
                                 buffer[i] <= 0xFFFD))
                            {
                                if (matchCount >= matchBuffer.length)
                                {
                                    Object[] messageArguments = { new Integer(matchBuffer.length) };
                                    MessageFormat formatter = new MessageFormat("");
                                    formatter.setLocale(getLocale());

                                    formatter.applyPattern(getI10nString("messageEscapeAmpersandMaximumAmountOfCharactersForXMLEntityExceeded"));
                                    System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                    System.exit(-1);
                                }

                                matchBuffer[matchCount] = buffer[i];
                                matchCount++; 
                            }
                            else if (buffer[i] == '-' ||
                                     buffer[i] == '.' ||
                                     (buffer[i] >= 0x30 &&
                                      buffer[i] <= 0x39) ||
                                     buffer[i] == 0xB7 |
                                     (buffer[i] >= 0x300 &&
                                      buffer[i] <= 0x36F) ||
                                     (buffer[i] >= 0x203F &&
                                      buffer[i] <= 0x2040))
                            {
                                if (matchCount <= 0)
                                {
                                    writer.write("&amp;");
                                    this.ampersandsEscapedCount++;
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                                else
                                {
                                    if (matchCount >= matchBuffer.length)
                                    {
                                        Object[] messageArguments = { new Integer(matchBuffer.length) };
                                        MessageFormat formatter = new MessageFormat("");
                                        formatter.setLocale(getLocale());

                                        formatter.applyPattern(getI10nString("messageEscapeAmpersandMaximumAmountOfCharactersForXMLEntityExceeded"));
                                        System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                        System.exit(-1);
                                    }

                                    matchBuffer[matchCount] = buffer[i];
                                    matchCount++;
                                }
                            }
                            else if (buffer[i] == '#')
                            {
                                if (matchCount <= 0)
                                {
                                    matchBuffer[matchCount] = buffer[i];
                                    matchCount++;
                                    stage = FA_STAGE_CHARACTER_REFERENCE;
                                }
                                else
                                {
                                    writer.write("&amp;");
                                    this.ampersandsEscapedCount++;
                                    writer.write(matchBuffer, 0, matchCount);
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                            }
                            else if (buffer[i] == ';')
                            {
                                if (matchCount <= 0)
                                {
                                    writer.write("&amp;");
                                    this.ampersandsEscapedCount++;
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                                else
                                {
                                    // A valid XML entity.

                                    writer.write("&");
                                    writer.write(matchBuffer, 0, matchCount);
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                            }
                            else
                            {
                                writer.write("&amp;");
                                this.ampersandsEscapedCount++;
                                writer.write(matchBuffer, 0, matchCount);
                                // buffer[i] needs to be considered in its own,
                                // new, restarted iteration (for "&&" for instance).
                                --i;

                                stage = FA_STAGE_NONE;
                                matchCount = 0;
                            }
                        }
                        else if (stage == FA_STAGE_CHARACTER_REFERENCE)
                        {
                            if (buffer[i] == 'x')
                            {
                                if (matchCount <= 1)
                                {
                                    if (matchCount >= matchBuffer.length)
                                    {
                                        Object[] messageArguments = { new Integer(matchBuffer.length) };
                                        MessageFormat formatter = new MessageFormat("");
                                        formatter.setLocale(getLocale());

                                        formatter.applyPattern(getI10nString("messageEscapeAmpersandMaximumAmountOfCharactersForXMLEntityExceeded"));
                                        System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                        System.exit(-1);
                                    }

                                    matchBuffer[matchCount] = buffer[i];
                                    matchCount++;
                                    stage = FA_STAGE_CHARACTER_REFERENCE_HEX;
                                }
                                else
                                {
                                    writer.write("&amp;");
                                    this.ampersandsEscapedCount++;
                                    writer.write(matchBuffer, 0, matchCount);
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                            }
                            else if (buffer[i] >= 0x30 &&
                                     buffer[i] <= 0x39)
                            {
                                if (matchCount >= matchBuffer.length)
                                {
                                    Object[] messageArguments = { new Integer(matchBuffer.length) };
                                    MessageFormat formatter = new MessageFormat("");
                                    formatter.setLocale(getLocale());

                                    formatter.applyPattern(getI10nString("messageEscapeAmpersandMaximumAmountOfCharactersForXMLEntityExceeded"));
                                    System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                    System.exit(-1);
                                }

                                matchBuffer[matchCount] = buffer[i];
                                matchCount++;
                            }
                            else if (buffer[i] == ';')
                            {
                                if (matchCount <= 1)
                                {
                                    writer.write("&amp;");
                                    writer.write(matchBuffer, 0, matchCount);
                                    this.ampersandsEscapedCount++;
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                                else
                                {
                                    int characterReference = -1;
                                    String characterReferenceString = new String(matchBuffer, 1, matchCount - 1);

                                    try
                                    {
                                        characterReference = Integer.parseInt(characterReferenceString, 10);
                                    }
                                    catch (NumberFormatException ex)
                                    {
                                        characterReference = -1;
                                    }

                                    if (characterReference == 0x9 ||
                                        characterReference == 0xA ||
                                        characterReference == 0xD ||
                                        (characterReference >= 0x20 &&
                                         characterReference <= 0xD7FF) ||
                                        (characterReference >= 0xE000 &&
                                         characterReference <= 0xFFFD))
                                    {
                                        // A valid XML character reference.

                                        writer.write("&");
                                        writer.write(matchBuffer, 0, matchCount);
                                        writer.write(buffer[i]);

                                        stage = FA_STAGE_NONE;
                                        matchCount = 0;
                                    }
                                    else
                                    {
                                        writer.write("&amp;");
                                        this.ampersandsEscapedCount++;
                                        writer.write(matchBuffer, 0, matchCount);
                                        writer.write(buffer[i]);

                                        stage = FA_STAGE_NONE;
                                        matchCount = 0;
                                    }
                                }
                            }
                            else
                            {
                                writer.write("&amp;");
                                this.ampersandsEscapedCount++;
                                writer.write(matchBuffer, 0, matchCount);
                                // buffer[i] needs to be considered in its own,
                                // new, restarted iteration (for "&#&" for instance).
                                --i;

                                stage = FA_STAGE_NONE;
                                matchCount = 0;
                            }
                        }
                        else if (stage == FA_STAGE_CHARACTER_REFERENCE_HEX)
                        {
                            if ((buffer[i] >= 0x30 &&
                                 buffer[i] <= 0x39) ||
                                buffer[i] == 'A' || buffer[i] == 'a' ||
                                buffer[i] == 'B' || buffer[i] == 'b' ||
                                buffer[i] == 'C' || buffer[i] == 'c' ||
                                buffer[i] == 'D' || buffer[i] == 'd' ||
                                buffer[i] == 'E' || buffer[i] == 'e' ||
                                buffer[i] == 'F' || buffer[i] == 'f')
                            {
                                if (matchCount >= matchBuffer.length)
                                {
                                    Object[] messageArguments = { new Integer(matchBuffer.length) };
                                    MessageFormat formatter = new MessageFormat("");
                                    formatter.setLocale(getLocale());

                                    formatter.applyPattern(getI10nString("messageEscapeAmpersandMaximumAmountOfCharactersForXMLEntityExceeded"));
                                    System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                    System.exit(-1);
                                }

                                matchBuffer[matchCount] = buffer[i];
                                matchCount++;
                            }
                            else if (buffer[i] == ';')
                            {
                                if (matchCount <= 2)
                                {
                                    writer.write("&amp;");
                                    writer.write(matchBuffer, 0, matchCount);
                                    this.ampersandsEscapedCount++;
                                    writer.write(buffer[i]);

                                    stage = FA_STAGE_NONE;
                                    matchCount = 0;
                                }
                                else
                                {
                                    int characterReference = -1;
                                    String characterReferenceString = new String(matchBuffer, 2, matchCount - 2);

                                    try
                                    {
                                        characterReference = Integer.parseInt(characterReferenceString, 16);
                                    }
                                    catch (NumberFormatException ex)
                                    {
                                        characterReference = -1;
                                    }

                                    if (characterReference == 0x9 ||
                                        characterReference == 0xA ||
                                        characterReference == 0xD ||
                                        (characterReference >= 0x20 &&
                                         characterReference <= 0xD7FF) ||
                                        (characterReference >= 0xE000 &&
                                         characterReference <= 0xFFFD))
                                    {
                                        // A valid XML character reference.

                                        writer.write("&");
                                        writer.write(matchBuffer, 0, matchCount);
                                        writer.write(buffer[i]);

                                        stage = FA_STAGE_NONE;
                                        matchCount = 0;
                                    }
                                    else
                                    {
                                        writer.write("&amp;");
                                        this.ampersandsEscapedCount++;
                                        writer.write(matchBuffer, 0, matchCount);
                                        writer.write(buffer[i]);

                                        stage = FA_STAGE_NONE;
                                        matchCount = 0;
                                    }
                                }
                            }
                            else
                            {
                                writer.write("&amp;");
                                this.ampersandsEscapedCount++;
                                writer.write(matchBuffer, 0, matchCount);
                                // buffer[i] needs to be considered in its own,
                                // new, restarted iteration (for "&#x&" for instance).
                                --i;

                                stage = FA_STAGE_NONE;
                                matchCount = 0;
                            }
                        }
                        else
                        {
                            Object[] messageArguments = { stage };
                            MessageFormat formatter = new MessageFormat("");
                            formatter.setLocale(getLocale());

                            formatter.applyPattern(getI10nString("messageEscapeAmpersandUnknownStage"));
                            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));
                        }
                    }

                    charactersRead = reader.read(buffer, 0, buffer.length);
                }

                if (matchCount > 0)
                {
                    writer.write("&amp;");
                    this.ampersandsEscapedCount++;
                    writer.write(matchBuffer, 0, matchCount);
                }
            }
            finally
            {
                writer.close();
                reader.close();
            }
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


        /** @todo Fix unescaped < and >. */


        // Fix quotation marks (FQM) within attributes.

        if (CopyFileBinary(destinationFile, sourceFile) != 0)
        {
            System.exit(-1);
        }

        boolean matching = false;
        boolean skipping = false;
        matchCount = 0;

        char[] tagBuffer = new char[2048];
        int tagBufferCount = 0;


        // If you set this to true, you have to adjust EscapeTagSequenceForQuotationMarks().
        boolean fixQuotationMarksIncludeStartEndSequences = false;
        boolean fixQuotationMarksDebugOutput = false;

        Sequence sequenceTagStart = new Sequence(new char[] { '<' }, false);
        Sequence sequenceTagEnd = new Sequence(new char[] { '>' }, false);
        Sequence sequenceTagEmptyStart = new Sequence(new char[] { '<' }, false);
        Sequence sequenceTagEmptyEnd = new Sequence(new char[] { '/', '>'}, false);
        Sequence sequenceProcessingInstructionStart = new Sequence(new char[] { '<', '?' }, true);
        Sequence sequenceProcessingInstructionEnd = new Sequence(new char[] { '?', '>' }, true);

        // Can't rely solely on markup declaration open delimiter ("<!") and markup declaration close delimiter (">"),
        // because CDATA or a comment might contain XML-unescaped ">". Either each possible markup declaration
        // needs to be defined as skipping sequence here, or markup declarations shouldn't be skipped but be
        // recognized, so a special handler could skip them properly.

        Sequence sequenceCommentStart = new Sequence(new char[] { '<', '!', '-', '-' }, true);
        Sequence sequenceCommentEnd = new Sequence(new char[] { '-', '-', '>' }, true);
        Sequence sequenceCDATAStart = new Sequence(new char[] { '<', '!', '[', 'C', 'D', 'A', 'T', 'A', '[' }, true);
        Sequence sequenceCDATAEnd = new Sequence(new char[] { ']', ']', '>' }, true);

        int START_SEQUENCE = 0;
        int END_SEQUENCE = 1;

        List<List<Sequence>> sequences = new ArrayList<List<Sequence>>();
        sequences.add(new ArrayList<Sequence>());
        sequences.get(sequences.size() - 1).add(sequenceTagStart);
        sequences.get(sequences.size() - 1).add(sequenceTagEnd);
        sequences.add(new ArrayList<Sequence>());
        sequences.get(sequences.size() - 1).add(sequenceTagEmptyStart);
        sequences.get(sequences.size() - 1).add(sequenceTagEmptyEnd);
        sequences.add(new ArrayList<Sequence>());
        sequences.get(sequences.size() - 1).add(sequenceProcessingInstructionStart);
        sequences.get(sequences.size() - 1).add(sequenceProcessingInstructionEnd);
        sequences.add(new ArrayList<Sequence>());
        sequences.get(sequences.size() - 1).add(sequenceCommentStart);
        sequences.get(sequences.size() - 1).add(sequenceCommentEnd);
        sequences.add(new ArrayList<Sequence>());
        sequences.get(sequences.size() - 1).add(sequenceCDATAStart);
        sequences.get(sequences.size() - 1).add(sequenceCDATAEnd);

        try
        {
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(sourceFile),
                                    "UTF-8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(destinationFile),
                                    "UTF-8"));

            try
            {
                int charactersRead = reader.read(buffer, 0, buffer.length);

                while (charactersRead > 0)
                {
                    for (int i = 0; i < charactersRead; i++)
                    {
                        matching = false;
                        skipping = false;

                        // Analyze the result of the previous iteration and clean it up, so
                        // that the current iteration doesn't have to deal with a sudden match
                        // situation while checking buffer[i].

                        if ((sequenceTagStart.GetMatched() == true &&
                             sequenceTagEnd.GetMatched() == true) ||
                            (sequenceTagEmptyStart.GetMatched() == true &&
                             sequenceTagEmptyEnd.GetMatched() == true))
                        {
                            if (fixQuotationMarksDebugOutput == true)
                            {
                                System.out.println("FQM: " + i + ": ETSFQM");
                            }

                            EscapeTagSequenceForQuotationMarks(tagBuffer,
                                                               tagBufferCount,
                                                               writer,
                                                               fixQuotationMarksDebugOutput);

                            if (matchCount > 0)
                            {
                                if (fixQuotationMarksIncludeStartEndSequences == false)
                                {
                                    if (fixQuotationMarksDebugOutput == true)
                                    {
                                        System.out.println("FQM: " + i + ": flushing " + matchCount);
                                    }

                                    writer.write(matchBuffer, 0, matchCount);
                                }

                                matchCount = 0;
                            }

                            tagBufferCount = 0;

                            // This should be data driven (from the sequence definitions)!
                            sequenceTagStart.Reset();
                            sequenceTagEnd.Reset();
                            sequenceTagEmptyStart.Reset();
                            sequenceTagEmptyEnd.Reset();
                        }


                        for (int indexSequencePair = 0; indexSequencePair < sequences.size(); indexSequencePair++)
                        {
                            if (sequences.get(indexSequencePair).get(START_SEQUENCE).GetMatched() == true)
                            {
                                if (sequences.get(indexSequencePair).get(END_SEQUENCE).GetMatched() != true)
                                {
                                    if (sequences.get(indexSequencePair).get(START_SEQUENCE).GetSkip() == true)
                                    {
                                        // Prevent other start sequences from beginning to match.
                                        skipping = true;

                                        break;
                                    }
                                }
                                else
                                {
                                    sequences.get(indexSequencePair).get(START_SEQUENCE).Reset();
                                    sequences.get(indexSequencePair).get(END_SEQUENCE).Reset();

                                    if (sequences.get(indexSequencePair).get(START_SEQUENCE).GetSkip() == true)
                                    {
                                        // If this sequence was skipping, then it prevented every
                                        // other start sequence from beginning to match.
                                        skipping = false;

                                        break;
                                    }
                                }
                            }
                        }

                        // Check buffer[i] for the current iteration.

                        for (int indexSequencePair = 0; indexSequencePair < sequences.size(); indexSequencePair++)
                        {
                            if (sequences.get(indexSequencePair).get(START_SEQUENCE).GetMatched() != true)
                            {
                                if (skipping == false)
                                {
                                    int checkResult = sequences.get(indexSequencePair).get(START_SEQUENCE).CheckChar(buffer[i]);

                                    if (checkResult == 1)
                                    {
                                        matching = true;
                                    }
                                    else if (checkResult == 2)
                                    {
                                        matching = true;

                                        if (sequences.get(indexSequencePair).get(START_SEQUENCE).GetSkip() == true)
                                        {
                                            // Abort all other matching sequences.
                                            for (int j = 0; j < sequences.size(); j++)
                                            {
                                                if (sequences.get(j).get(START_SEQUENCE) != sequences.get(indexSequencePair).get(START_SEQUENCE))
                                                {
                                                    sequences.get(j).get(START_SEQUENCE).Reset();
                                                    sequences.get(j).get(END_SEQUENCE).Reset();
                                                }
                                            }

                                            break;
                                        }
                                    }
                                }
                                else
                                {
                                    // Another start sequence has matched and is currently skipping
                                    // characters. This sequence here isn't allowed to begin to match
                                    // at the same time. Instead of another start, the end of the
                                    // currently skipping sequence is expected next.
                                }
                            }
                            else
                            {
                                if (sequences.get(indexSequencePair).get(END_SEQUENCE).GetMatched() != true)
                                {
                                    int checkResult = sequences.get(indexSequencePair).get(END_SEQUENCE).CheckChar(buffer[i]);

                                    if (checkResult == 1)
                                    {
                                        matching = true;
                                        skipping = false;
                                    }
                                    else if (checkResult == 2)
                                    {
                                        matching = true;
                                        skipping = false;
                                    }
                                }
                            }
                        }


                        if (matching == true)
                        {
                            if (fixQuotationMarksDebugOutput == true)
                            {
                                System.out.println("FQM: " + i + ": matching " + buffer[i]);
                            }

                            if (matchCount + 1 >= matchBuffer.length)
                            {
                                Object[] messageArguments = { new Integer(matchBuffer.length) };
                                MessageFormat formatter = new MessageFormat("");
                                formatter.setLocale(getLocale());

                                formatter.applyPattern(getI10nString("messageEscapeQuotationMarksMaximumAmountOfMatchingCharactersExceeded"));
                                System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                System.exit(-1);
                            }

                            matchBuffer[matchCount] = buffer[i];
                            matchCount++;
                        }
                        else
                        {
                            if (matchCount > 0)
                            {
                                if (skipping == false &&
                                    (sequenceTagStart.GetMatched() == true ||
                                     sequenceTagEmptyStart.GetMatched() == true))
                                {
                                    if (fixQuotationMarksIncludeStartEndSequences == false)
                                    {
                                        if (tagBufferCount > 0)
                                        {
                                            if (tagBufferCount + matchCount > tagBuffer.length)
                                            {
                                                Object[] messageArguments = { new Integer(tagBufferCount),
                                                                              new Integer(matchCount),
                                                                              new Integer(tagBuffer.length),
                                                                              new Integer((tagBufferCount + matchCount) - tagBuffer.length) };
                                                MessageFormat formatter = new MessageFormat("");
                                                formatter.setLocale(getLocale());

                                                formatter.applyPattern(getI10nString("messageEscapeQuotationMarksAppendingMatchBufferToTagBufferWouldExceedTagBuffer"));
                                                System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                                System.exit(-1);
                                            }

                                            System.arraycopy(matchBuffer, 0, tagBuffer, tagBufferCount, matchCount);
                                            tagBufferCount += matchCount;
                                        }
                                        else
                                        {
                                            if (fixQuotationMarksDebugOutput == true)
                                            {
                                                System.out.println("FQM: " + i + ": flushing " + matchCount);
                                            }

                                            writer.write(matchBuffer, 0, matchCount);
                                        }
                                    }
                                    else
                                    {
                                        if (matchCount > tagBuffer.length)
                                        {
                                            Object[] messageArguments = { new Integer(0),
                                                                          new Integer(matchCount),
                                                                          new Integer(tagBuffer.length),
                                                                          new Integer(matchCount - tagBuffer.length) };
                                            MessageFormat formatter = new MessageFormat("");
                                            formatter.setLocale(getLocale());

                                            formatter.applyPattern(getI10nString("messageEscapeQuotationMarksAppendingMatchBufferToTagBufferWouldExceedTagBuffer"));
                                            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                            System.exit(-1);
                                        }

                                        System.arraycopy(matchBuffer, 0, tagBuffer, 0, matchCount);
                                        tagBufferCount = matchCount;
                                    }
                                }
                                else
                                {
                                    if (fixQuotationMarksDebugOutput == true)
                                    {
                                        System.out.println("FQM: " + i + ": flushing " + matchCount);
                                    }

                                    writer.write(matchBuffer, 0, matchCount);
                                }

                                matchCount = 0;
                            }

                            if (skipping == true)
                            {
                                if (fixQuotationMarksDebugOutput == true)
                                {
                                    System.out.println("FQM: " + i + ": skipping " + buffer[i]);
                                }

                                writer.write(buffer[i]);
                                matchCount = 0;
                            }
                        }


                        // Raw content.

                        if (sequenceTagStart.GetMatched() == true ||
                            sequenceTagEmptyStart.GetMatched() == true)
                        {
                            if (matching == false &&
                                skipping == false)
                            {
                                if (sequenceTagEnd.GetMatched() != true &&
                                    sequenceTagEmptyEnd.GetMatched() != true)
                                {
                                    if (matchCount > 0)
                                    {
                                        if (fixQuotationMarksIncludeStartEndSequences == false)
                                        {
                                            if (fixQuotationMarksDebugOutput == true)
                                            {
                                                System.out.println("FQM: " + i + ": flushing " + matchCount);
                                            }

                                            writer.write(matchBuffer, 0 , matchCount);
                                        }
                                        else
                                        {
                                            if (0 + matchCount > tagBuffer.length)
                                            {
                                                Object[] messageArguments = { new Integer(0),
                                                                              new Integer(matchCount),
                                                                              new Integer(tagBuffer.length),
                                                                              new Integer((0 + matchCount) - tagBuffer.length) };
                                                MessageFormat formatter = new MessageFormat("");
                                                formatter.setLocale(getLocale());

                                                formatter.applyPattern(getI10nString("messageEscapeQuotationMarksAppendingMatchBufferToTagBufferWouldExceedTagBuffer"));
                                                System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                                System.exit(-1);
                                            }

                                            System.arraycopy(matchBuffer, 0, tagBuffer, 0, matchCount);
                                            tagBufferCount = matchCount;
                                        }

                                        matchCount = 0;
                                    }

                                    if (tagBufferCount + 1 > tagBuffer.length)
                                    {
                                        Object[] messageArguments = { new Integer(tagBuffer.length) };
                                        MessageFormat formatter = new MessageFormat("");
                                        formatter.setLocale(getLocale());

                                        formatter.applyPattern(getI10nString("messageEscapeQuotationMarksMaximumAmountOfMatchingCharactersExceeded"));
                                        System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                        System.exit(-1);
                                    }

                                    tagBuffer[tagBufferCount] = buffer[i];
                                    tagBufferCount++;

                                    if (fixQuotationMarksDebugOutput == true)
                                    {
                                        System.out.println("FQM: " + i + ": recognizing " + buffer[i]);
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (matching == false &&
                                skipping == false)
                            {
                                // Not within any known sequence.

                                if (fixQuotationMarksDebugOutput == true)
                                {
                                    System.out.println("FQM: " + i + ": ignoring " + buffer[i]);
                                }

                                writer.write(buffer[i]);
                                matchCount = 0;
                            }
                        }
                    }

                    charactersRead = reader.read(buffer, 0, buffer.length);
                }


                if ((sequenceTagStart.GetMatched() == true &&
                     sequenceTagEnd.GetMatched() == true) ||
                    (sequenceTagEmptyStart.GetMatched() == true &&
                     sequenceTagEmptyEnd.GetMatched() == true))
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("FQM: end: ETSFQM");
                    }

                    EscapeTagSequenceForQuotationMarks(tagBuffer,
                                                       tagBufferCount,
                                                       writer,
                                                       fixQuotationMarksDebugOutput);

                    tagBufferCount = 0;
                }

                if (tagBufferCount > 0)
                {
                      if (fixQuotationMarksDebugOutput == true)
                      {
                          System.out.println("FQM: end: flushing " + tagBufferCount);
                      }

                      writer.write(tagBuffer, 0, tagBufferCount);
                }

                if (matchCount > 0)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("FQM: end: flushing " + matchCount);
                    }

                    writer.write(matchBuffer, 0, matchCount);
                }
            }
            finally
            {
                writer.close();
                reader.close();
            }
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

        if (CopyFileBinary(destinationFile, new File(args[1])) != 0)
        {
            System.exit(-1);
        }

        /**
         * @todo Add singular and plural to this messages.
         */

        if (this.ampersandsEscapedCount > 0)
        {
            Object[] messageArguments = { new Integer(this.ampersandsEscapedCount) };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageAmpersandsEscapedCount"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));
        }

        if (this.singleQuotationMarksEscapedCount > 0)
        {
            Object[] messageArguments = { new Integer(this.singleQuotationMarksEscapedCount) };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageSingleQuotationMarksEscapedCount"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));
        }

        if (this.doubleQuotationMarksEscapedCount > 0)
        {
            Object[] messageArguments = { new Integer(this.doubleQuotationMarksEscapedCount) };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageDoubleQuotationMarksEscapedCount"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));
        }

        if (this.ampersandsEscapedCount <= 0 &&
            this.singleQuotationMarksEscapedCount <= 0 &&
            this.doubleQuotationMarksEscapedCount <= 0)
        {
            System.out.println("xml_fix_special_characters_escaping1: " + getI10nString("messageNothingToResult"));
        }

        if (sourceFile.delete() != true)
        {
            Object[] messageArguments = { sourceFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCantDeleteTempFile"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));
        }


        if (destinationFile.delete() != true)
        {
            Object[] messageArguments = { destinationFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCantDeleteTempFile"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));
        }

        return 0;
    }

    public int EscapeTagSequenceForQuotationMarks(char[] tagBuffer,
                                                  int tagBufferCount,
                                                  BufferedWriter writer,
                                                  boolean fixQuotationMarksDebugOutput) throws IOException
    {
        if (tagBufferCount < 0)
        {
            System.out.println("xml_fix_special_characters_escaping1: " + getI10nString("messageEscapeTagSequenceForQuotationMarksTagBufferCountIsNegative"));
            System.exit(-1);
        }

        if (tagBufferCount > tagBuffer.length)
        {
            Object[] messageArguments = { tagBufferCount, tagBuffer.length };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageEscapeTagSequenceForQuotationMarksTagBufferCountLargerThanTagBuffer"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

            System.exit(-1);
        }

        if (tagBufferCount < 1)
        {
            if (fixQuotationMarksDebugOutput == true)
            {
                System.out.println("ETSFQM: flushing " + tagBufferCount);
            }

            writer.write(tagBuffer, 0, tagBufferCount);
        }

        if (tagBuffer[0] == '/')
        {
            if (fixQuotationMarksDebugOutput == true)
            {
                System.out.println("ETSFQM: flushing " + tagBufferCount);
            }

            writer.write(tagBuffer, 0, tagBufferCount);
            return 0;
        }

        final int ETSFQM_STAGE_NONE = 0;
        final int ETSFQM_STAGE_FIRST_ATTRIBUTE_EQUAL_SIGN = 1;
        final int ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER = 2;
        final int ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER = 3;
        final int ETSFQM_STAGE_ATTRIBUTE_NAME = 4;
        final int ETSFQM_STAGE_WHITESPACE_AFTER_ATTRIBUTE_NAME = 5;
        final int ETSFQM_STAGE_EQUAL_SIGN_AFTER_ATTRIBUTE_NAME = 6;

        int stage = ETSFQM_STAGE_NONE;
        int startPos = 0;
        int endPos = 0;
        char delimiterCharacter = '\0';

        for (int i = 0; i < tagBufferCount; i++)
        {
            if (stage == ETSFQM_STAGE_NONE)
            {
                if (fixQuotationMarksDebugOutput == true)
                {
                    System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + tagBuffer[i]);
                }

                writer.write(tagBuffer[i]);

                if (tagBuffer[i] == '=')
                {
                    stage = ETSFQM_STAGE_FIRST_ATTRIBUTE_EQUAL_SIGN;

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " triggers stage " + stage);
                    }
                }
            }
            else if (stage == ETSFQM_STAGE_FIRST_ATTRIBUTE_EQUAL_SIGN)
            {
                if (fixQuotationMarksDebugOutput == true)
                {
                    System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + tagBuffer[i]);
                }

                writer.write(tagBuffer[i]);

                if (tagBuffer[i] == '\'' ||
                    tagBuffer[i] == '"')
                {
                    stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                    delimiterCharacter = tagBuffer[i];
                    startPos = i;

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " triggers stage " + stage);
                    }
                }
            }
            else if (stage == ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER)
            {
                if (tagBuffer[i] == delimiterCharacter)
                {
                    stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER;
                    endPos = i;

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " triggers stage " + stage);
                    }
                }
                else
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": matching " + tagBuffer[i]);
                    }

                    continue;
                }
            }
            else if (stage == ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER)
            {
                if (Character.isWhitespace(tagBuffer[i]) == true)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": matching " + tagBuffer[i]);
                    }

                    continue;
                }
                else
                {
                    if (i == endPos + 1)
                    {
                        if (tagBuffer[i] == delimiterCharacter)
                        {
                            stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER;
                            endPos = i;
                        }
                        else
                        {
                            stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                            endPos = 0;
                        }

                        if (fixQuotationMarksDebugOutput == true)
                        {
                            System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " no whitespace after potential attribute value end delimiter triggers stage " + stage);
                        }
                    }
                    else
                    {
                        /**
                         * @todo Check for real valid attribute names according to XML
                         *     specification (see "Fix Ampersand" part).
                         */
                        if (Character.isLetter(tagBuffer[i]) == true)
                        {
                            stage = ETSFQM_STAGE_ATTRIBUTE_NAME;
                        }
                        else
                        {
                            if (tagBuffer[i] == delimiterCharacter)
                            {
                                stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER;
                                endPos = i;
                            }
                            else
                            {
                                stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                                endPos = 0;
                            }
                        }

                        if (fixQuotationMarksDebugOutput == true)
                        {
                            System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " triggers stage " + stage);
                        }
                    }
                }
            }
            else if (stage == ETSFQM_STAGE_ATTRIBUTE_NAME)
            {
                /**
                 * @todo Check for real valid attribute names according to XML
                 *     specification (see "Fix Ampersand" part).
                 */
                if (Character.isLetter(tagBuffer[i]) == true ||
                    Character.isDigit(tagBuffer[i]) == true)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": matching " + tagBuffer[i]);
                    }

                    continue;
                }
                else if (tagBuffer[i] == ':')
                {
                    /**
                     * @todo Check for more than 1 colon?
                     */

                    if (i == startPos + 1)
                    {
                        stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                        endPos = 0;

                        if (fixQuotationMarksDebugOutput == true)
                        {
                            System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " at the start of a potential attribute name triggers stage " + stage);
                        }
                    }
                    else
                    {
                        if (fixQuotationMarksDebugOutput == true)
                        {
                            System.out.println("ETSFQM: " + i + "," + stage + ": matching " + tagBuffer[i]);
                        }

                        continue;
                    }
                }
                else if (Character.isWhitespace(tagBuffer[i]) == true)
                {
                    stage = ETSFQM_STAGE_WHITESPACE_AFTER_ATTRIBUTE_NAME;
                }
                else if (tagBuffer[i] == '=')
                {
                    stage = ETSFQM_STAGE_EQUAL_SIGN_AFTER_ATTRIBUTE_NAME;
                }
                else
                {
                    if (tagBuffer[i] == delimiterCharacter)
                    {
                        stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER;
                        endPos = i;
                    }
                    else
                    {
                        stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                        endPos = 0;
                    }
                }

                if (fixQuotationMarksDebugOutput == true)
                {
                    System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " triggers stage " + stage);
                }
            }
            else if (stage == ETSFQM_STAGE_WHITESPACE_AFTER_ATTRIBUTE_NAME)
            {
                if (Character.isWhitespace(tagBuffer[i]) == true)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": matching " + tagBuffer[i]);
                    }

                    continue;
                }
                else if (tagBuffer[i] == '=')
                {
                    stage = ETSFQM_STAGE_EQUAL_SIGN_AFTER_ATTRIBUTE_NAME;
                }
                else
                {
                    if (tagBuffer[i] == delimiterCharacter)
                    {
                        stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER;
                        endPos = i;
                    }
                    else
                    {
                        stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                        endPos = 0;
                    }
                }

                if (fixQuotationMarksDebugOutput == true)
                {
                    System.out.println("ETSFQM: " + i + ": " + tagBuffer[i] + " triggers stage " + stage);
                }
            }
            else if (stage == ETSFQM_STAGE_EQUAL_SIGN_AFTER_ATTRIBUTE_NAME)
            {
                if (Character.isWhitespace(tagBuffer[i]) == true)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": matching " + tagBuffer[i]);
                    }

                    continue;
                }
                else if (tagBuffer[i] == '\'' ||
                         tagBuffer[i] == '"')
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": start escaping");
                    }

                    for (int j = startPos + 1; j < endPos; j++)
                    {
                        if (tagBuffer[j] == delimiterCharacter)
                        {
                            if (fixQuotationMarksDebugOutput == true)
                            {
                                System.out.println("ETSFQM: " + i + "," + stage + ": escaping " + tagBuffer[j]);
                            }

                            if (delimiterCharacter == '\'')
                            {
                                writer.write("&apos;");
                                this.singleQuotationMarksEscapedCount++;
                            }
                            else if (delimiterCharacter == '"')
                            {
                                writer.write("&quot;");
                                this.doubleQuotationMarksEscapedCount++;
                            }
                            else
                            {
                                Object[] messageArguments = { delimiterCharacter };
                                MessageFormat formatter = new MessageFormat("");
                                formatter.setLocale(getLocale());

                                formatter.applyPattern(getI10nString("messageEscapeTagSequenceForQuotationMarksUnexpectedDelimiterCharacter"));
                                System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                                System.exit(-1);
                            }
                        }
                        else
                        {
                            if (fixQuotationMarksDebugOutput == true)
                            {
                                System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + tagBuffer[j]);
                            }

                            writer.write(tagBuffer[j]);
                        }
                    }

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + delimiterCharacter);
                    }

                    writer.write(delimiterCharacter);

                    for (int j = endPos + 1; j < i; j++)
                    {
                        if (fixQuotationMarksDebugOutput == true)
                        {
                            System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + tagBuffer[j]);
                        }

                        writer.write(tagBuffer[j]);
                    }

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + tagBuffer[i]);
                    }

                    writer.write(tagBuffer[i]);

                    stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                    startPos = i;
                    delimiterCharacter = tagBuffer[i];
                    endPos = 0;

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + ": stop escaping triggers stage " + stage);
                    }
                }
                else
                {
                    stage = ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER;
                    endPos = 0;

                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: " + i + "," + stage + ": flushing 1: " + tagBuffer[i]);
                    }
                }
            }
        }


        if (stage == ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER ||
            stage == ETSFQM_STAGE_ATTRIBUTE_NAME ||
            stage == ETSFQM_STAGE_WHITESPACE_AFTER_ATTRIBUTE_NAME ||
            stage == ETSFQM_STAGE_EQUAL_SIGN_AFTER_ATTRIBUTE_NAME)
        {
            for (int i = startPos + 1; i < tagBufferCount; i++)
            {
                if (tagBuffer[i] == delimiterCharacter)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: end," + stage + ": escaping " + tagBuffer[i]);
                    }

                    if (delimiterCharacter == '\'')
                    {
                        writer.write("&apos;");
                        this.singleQuotationMarksEscapedCount++;
                    }
                    else if (delimiterCharacter == '"')
                    {
                        writer.write("&quot;");
                        this.doubleQuotationMarksEscapedCount++;
                    }
                    else
                    {
                        Object[] messageArguments = { delimiterCharacter };
                        MessageFormat formatter = new MessageFormat("");
                        formatter.setLocale(getLocale());

                        formatter.applyPattern(getI10nString("messageEscapeTagSequenceForQuotationMarksUnexpectedDelimiterCharacter"));
                        System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                        System.exit(-1);
                    }
                }
                else
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: end," + stage + ": flushing 1: " + tagBuffer[i]);
                    }

                    writer.write(tagBuffer[i]);
                }
            }

            if (fixQuotationMarksDebugOutput == true)
            {
                System.out.println("ETSFQM: end," + stage + ": flushing 1: " + delimiterCharacter);
            }

            if (stage == ETSFQM_STAGE_ATTRIBUTE_VALUE_START_DELIMITER)
            {
                System.out.println("xml_fix_special_characters_escaping1: " + getI10nString("messageEscapeTagSequenceForQuotationMarksEndDelimiterWasMissing"));
            }

            writer.write(delimiterCharacter);
        }
        else if (stage == ETSFQM_STAGE_ATTRIBUTE_VALUE_END_DELIMITER)
        {
            for (int i = startPos + 1; i < endPos; i++)
            {
                if (tagBuffer[i] == delimiterCharacter)
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: end," + stage + ": escaping " + tagBuffer[i]);
                    }

                    if (delimiterCharacter == '\'')
                    {
                        writer.write("&apos;");
                        this.singleQuotationMarksEscapedCount++;
                    }
                    else if (delimiterCharacter == '"')
                    {
                        writer.write("&quot;");
                        this.doubleQuotationMarksEscapedCount++;
                    }
                    else
                    {
                        Object[] messageArguments = { delimiterCharacter };
                        MessageFormat formatter = new MessageFormat("");
                        formatter.setLocale(getLocale());

                        formatter.applyPattern(getI10nString("messageEscapeTagSequenceForQuotationMarksUnexpectedDelimiterCharacter"));
                        System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

                        System.exit(-1);
                    }
                }
                else
                {
                    if (fixQuotationMarksDebugOutput == true)
                    {
                        System.out.println("ETSFQM: end," + stage + ": flushing 1: " + tagBuffer[i]);
                    }

                    writer.write(tagBuffer[i]);
                }
            }

            if (fixQuotationMarksDebugOutput == true)
            {
                System.out.println("ETSFQM: end," + stage + ": flushing 1: " + delimiterCharacter);
            }

            writer.write(delimiterCharacter);

            if (fixQuotationMarksDebugOutput == true)
            {
                System.out.println("ETSFQM: end," + stage + ": flushing " + (tagBufferCount - (endPos + Character.toString(delimiterCharacter).length())));
            }

            writer.write(tagBuffer, endPos + Character.toString(delimiterCharacter).length(), tagBufferCount - (endPos + Character.toString(delimiterCharacter).length()));
        }

        return 0;
    }

    public int CopyFileBinary(File from, File to)
    {
        if (from.exists() != true)
        {
            Object[] messageArguments = { from.getAbsolutePath(),
                                          to.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCantCopyBinaryBecauseFromDoesntExist"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

            return -1;
        }

        if (from.isFile() != true)
        {
            Object[] messageArguments = { from.getAbsolutePath(),
                                          to.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCantCopyBinaryBecauseFromIsntAFile"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

            return -2;
        }

        if (from.canRead() != true)
        {
            Object[] messageArguments = { from.getAbsolutePath(),
                                          to.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCantCopyBinaryBecauseFromIsntReadable"));
            System.out.println("xml_fix_special_characters_escaping1: " + formatter.format(messageArguments));

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

    public Locale getLocale()
    {
        return Locale.getDefault();
    }

    /**
     * @brief This method interprets i10n strings from a .properties file as encoded in UTF-8.
     */
    public String getI10nString(String key)
    {
        if (this.i10nConsole == null)
        {
            this.i10nConsole = ResourceBundle.getBundle("i10n.i10nConsole", getLocale());
        }

        try
        {
            return new String(this.i10nConsole.getString(key).getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return this.i10nConsole.getString(key);
        }
    }

    private ResourceBundle i10nConsole;
    private int ampersandsEscapedCount;
    private int singleQuotationMarksEscapedCount;
    private int doubleQuotationMarksEscapedCount;
}
