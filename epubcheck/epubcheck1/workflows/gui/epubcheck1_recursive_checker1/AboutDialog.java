/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of epubcheck1_recursive_checker1 GUI.
 *
 * epubcheck1_recursive_checker1 GUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epubcheck1_recursive_checker1 GUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epubcheck1_recursive_checker1 GUI. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/epubcheck/epubcheck1/workflows/gui/epubcheck1_recursive_checker1/AboutDialog.java
 * @author Stephan Kreutzer
 * @since 2015-07-01
 */



import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;



class AboutDialog extends JDialog
{
    public AboutDialog(JFrame parent)
    {
        generateGUI(parent);
    }

    public final void generateGUI(JFrame parent)
    {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 10)));

        String programPath = AboutDialog.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        ImageIcon icon = new ImageIcon(programPath + "publishing_systems_logo.png");
        JLabel label = new JLabel(icon);
        label.setAlignmentX(0.5f);
        add(label);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel notice = new JLabel("<html><head><title>" + getI10nString("noticeHTMLTitleAbout") + " epubcheck1_recursive_checker1</title></head><body>" +
                                   "epubcheck1_recursive_checker1 GUI  Copyright (C) 2015  Stephan Kreutzer<br/><br/>" +
                                   "This program comes with ABSOLUTELY NO WARRANTY.<br/>" +
                                   "This is free software, and you are welcome to redistribute it<br/>" +
                                   "under certain conditions. See the GNU Affero General Public<br/>" +
                                   "License 3 or any later version for details. Also, see the source code<br/>" +
                                   "repository https://github.com/publishing-systems/automated_digital_publishing/<br/>" +
                                   "and the project website http://www.publishing-systems.org.<br/>" +
                                   "</body></html>");
                                 
        notice.setFont(notice.getFont().deriveFont(notice.getFont().getStyle() & ~Font.BOLD));
        notice.setAlignmentX(0.5f);
        add(notice);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton closeButton = new JButton(getI10nString("buttonClose"));

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        closeButton.setAlignmentX(0.5f);
        add(closeButton);

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle(getI10nString("dialogCaption"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setSize(520, 435);
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
            this.i10nGUI = ResourceBundle.getBundle("i10n.i10nAboutDialogGUI", getLocale());
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
}

