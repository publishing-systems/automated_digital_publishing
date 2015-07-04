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
 * @file $/epubcheck/epubcheck1/workflows/gui/epubcheck1_recursive_checker1/AddInputDecideRecursiveDialog.java
 * @brief Dialog asks if the added directory should be checked recursively or not.
 * @author Stephan Kreutzer
 * @since 2015-06-28
 */



import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.*;
import java.io.UnsupportedEncodingException;



class AddInputDecideRecursiveDialog extends JDialog
  implements ActionListener
{
    public AddInputDecideRecursiveDialog(JFrame parent)
    {
        this.recursive = false;
        generateGUI(parent);
    }

    public final void generateGUI(JFrame parent)
    {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel notice = new JLabel("<html><head><title>" +
                                   getI10nString("dialogCaption") +
                                   "</title></head><body>" +
                                   getI10nString("labelDescription") +
                                   "</body></html>");
                                 
        notice.setFont(notice.getFont().deriveFont(notice.getFont().getStyle() & ~Font.BOLD));
        notice.setAlignmentX(0.5f);
        add(notice);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel panelButtons = new JPanel();

        buttonYes = new JButton(getI10nString("buttonYesCaption"));
        buttonNo = new JButton(getI10nString("buttonNoCaption"));

        buttonYes.addActionListener(this);
        buttonNo.addActionListener(this);

        panelButtons.add(buttonYes);
        panelButtons.add(buttonNo);
        
        add(panelButtons);

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle(getI10nString("dialogCaption"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setSize(200, 125);
    }
    
    public void actionPerformed(ActionEvent event)
    {
        Object source = event.getSource();
        
        if (source == this.buttonYes)
        {
            this.recursive = true;
            this.dispose();
        }
        else if (source == this.buttonNo)
        {
            this.recursive = false;
            this.dispose();
        }
    }

    public boolean getRecursive()
    {
        return this.recursive;
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
            this.i10nGUI = ResourceBundle.getBundle("i10n.i10nAddInputDecideRecursiveDialogGUI", getLocale());
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
    private boolean recursive;
    private JButton buttonYes;
    private JButton buttonNo;
}

