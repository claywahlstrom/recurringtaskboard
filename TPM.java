
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this db of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this db of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
/* Code formatting and snippets taken from Oracle's documentation
 * 
 * Could implement
 *     - items sorted by date
 */

package tpm;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TPM extends JFrame {

    // init static variables
    private static final long serialVersionUID = 7379608572441765481L;
    public static final String GAP_LIST[] = {"0", "10", "15", "20"};
    public static final int MAX_GAP = 20;
    public static final String FILENAME = "java-tpm-db.txt";
    public static final int WIDTH = 5;
    public static int COUNT;
    
    // init static arrays
    static JLabel[] headerLabels;
    static JLabel[] dayLabels;
    static JLabel[] labels;
    static JLabel[] dates;
    static JTextArea[] tas;
    static JButton[] buttons;
    
    // init static classes
    static GridLayout experimentLayout;
    static String finalString;
    
    String[][] db;
    List<String> lines = new ArrayList<String>();
    
    public TPM(String name) {
        super(name);
    }
    
    // handles button events
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            for (int i = 0; i < COUNT; i++) {
                if (ae.getSource() == buttons[i]) {
                    System.out.println("Adding date to index " + i);
                    addDate(i);
                    saveToFile("tpm/" + FILENAME);
                    saveToFile("C:/Users/Clayton/Google Drive/" + FILENAME);
                    updateDaysUntil(i);
                }
            }
        }
    }
    
    // parses db file, reserves space for the JPanel
    public void initialize() {
        try {
            System.out.println(new File(".").getAbsoluteFile());
            Scanner scr = new Scanner(new File("tpm/" + FILENAME));
            scr.useDelimiter("\n");
            while (scr.hasNext()) {
                lines.add(scr.next());
            }
            db = new String[lines.size()][3];
            Scanner scrn;
            int num = 0;
            for (String str : lines) {
                scrn = new Scanner(str);
                db[num][0] = scrn.next();
                db[num][1] = scrn.next();
                db[num][2] = scrn.next();
                num++;
            }
            COUNT = db.length;
            
            saveToFile("tpm/backup.txt");
            
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        }

        experimentLayout = new GridLayout(COUNT + 1, WIDTH); // +1 for header row
        
        // initialize J stuff based on number of items
        headerLabels = new JLabel[WIDTH];
        labels = new JLabel[COUNT];
        dayLabels = new JLabel[COUNT];
        dates = new JLabel[COUNT];
        tas = new JTextArea[COUNT];
        buttons = new JButton[COUNT];
    }

    private static void createAndShowGUI() {
        
        //Create and set up the window.
        TPM frame = new TPM("TPM");
        frame.initialize();
        frame.setTitle("tpm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void addComponentsToPane(final Container pane) {
        final JPanel compsToExperiment = new JPanel();
        compsToExperiment.setBorder(new EmptyBorder(10, 10, 10, 10));
        compsToExperiment.setLayout(experimentLayout);
        Font font = new Font("Verdana", Font.PLAIN, 16);

        //Set up components preferred size
        JButton b = new JButton("Just fake button");
        Dimension buttonSize = b.getPreferredSize();
        compsToExperiment.setPreferredSize(new Dimension((int)(buttonSize.getWidth() * WIDTH)+MAX_GAP, (int)(buttonSize.getHeight() * COUNT*1.5+MAX_GAP  * 2)));
        // compsToExperiment.setPreferredSize(new Dimension(500, 300));
                
        String[] headers = {"Task", "Days until", "Date", "Days", ""};
        for (int i = 0; i < headers.length; i++) {
            headerLabels[i] = new JLabel(headers[i]);
            headerLabels[i].setFont(new Font("Verdana", Font.BOLD, 16));
            headerLabels[i].setHorizontalAlignment(JLabel.CENTER);
            compsToExperiment.add(headerLabels[i]);
        }
        // Add buttons to experimentLayout with Grid Layout

        for (int i = 0; i < COUNT; i++) {
            
            labels[i] = new JLabel(db[i][0]);
            labels[i].setFont(font);
            
            dayLabels[i] = new JLabel();
            updateDaysUntil(i);
            dayLabels[i].setFont(font);
            dayLabels[i].setHorizontalAlignment(JLabel.CENTER);
            
            dates[i] = new JLabel(db[i][1]);
            dates[i].setFont(font);
            dates[i].setHorizontalAlignment(JLabel.CENTER);
            
            tas[i] = new JTextArea(db[i][2], 3, 10);
            tas[i].setFont(font);
            
            buttons[i] = new JButton("Submit");
            buttons[i].setFont(font);
            buttons[i].addActionListener(new ButtonHandler());
            
            // add components
            compsToExperiment.add(labels[i]);
            compsToExperiment.add(dayLabels[i]);
            compsToExperiment.add(dates[i]);
            compsToExperiment.add(tas[i]);
            compsToExperiment.add(buttons[i]);
        }
        
        experimentLayout.setHgap(5);
        //Set up the vertical gap value
        experimentLayout.setVgap(5);
        //Set up the layout of the buttons
        experimentLayout.layoutContainer(compsToExperiment);
        
        pane.add(compsToExperiment, BorderLayout.NORTH);

    }
    
    // adds date entered into JTextArea to the respective index "i" in db
    // updates textarea and date
    public void addDate(int i) {
        // set up parser
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        
        // parse, add, and reformat
        Date date = new Date();
        int days = Integer.parseInt(tas[i].getText());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        String nexxt = parser.format(cal.getTime());
        
        // change date
        db[i][1] = nexxt;
        dates[i].setText(db[i][1]);
        
        // reset textarea back to default days
        tas[i].setText(db[i][2]);
    }
    
    // saves the formatted datebase (db) to a file
    // assumes db exists and is filled
    // writes finalString to file using writeFile
    public void saveToFile(String name) {
        finalString = new String();
        for (int i = 0; i < COUNT; i++) {
            finalString += db[i][0] + " " + db[i][1] + " " + db[i][2];
            if (i < COUNT - 1) {
                finalString += "\r\n";
            }
        }
        writeFile(name, finalString);
    }
    
    // basic filewriter using filename and text to write
    public void writeFile(String name, String text) {
        try {
            File file = new File(name);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.close();
            System.out.println(name + " written");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        } finally {
        }
    }
    
    // return number of days from today
    // includes positive and negative differences
    public int daysFromToday(String startDate) {
        System.out.println("startDate = " + startDate);
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        
        // parse, add, and reformat
        int daysBetween = 0;
        try {
            Date datedate = parser.parse(startDate);
            Calendar date = Calendar.getInstance();
            date.setTime(datedate);
            Calendar today = Calendar.getInstance();

            if (date.after(today)) {
                while (date.after(today)) {
                    date.add(Calendar.DATE, -1);
                    daysBetween++;
                }
            } else if (date.before(today)) {
                daysBetween = 1; // offset for correct calculations
                while (date.before(today)) {
                    
                    date.add(Calendar.DATE, 1);
                    daysBetween--;
                }
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return daysBetween;
    }
    
    // updates the "days until" field of index "i"
    public void updateDaysUntil(int i) {
        dayLabels[i].setText(Integer.toString(daysFromToday(db[i][1])));
    }
    
    // main, duh...
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
