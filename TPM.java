/* 
 * Copyright (c) 2017, Clayton Wahlstrom
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
/* 
 * Need to do!
 *     - make setters and getters
 * Could implement
 *     - sort items by date
 * 
 */


package tpmboard;

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

    // init variables
    private final Font BOLD_FONT = new Font("Verdana", Font.BOLD, 16);
    private final Font DEF_FONT = new Font("Verdana", Font.PLAIN, 16);
    private final String FILENAME = "java-tpm-db.txt";
    private final String GAP_LIST[] = {"0", "10", "15", "20"};
    private final String PACK_NAME = this.getClass().getPackage().getName();
    private final String PRGM_NAME = "Total Productive Maintenance";
    private final String USERNAME = System.getProperty("user.name");
    private final String[] HEADERS = {"Task", "Days until", "Date", "Days", "", "Set Default"};
    private final int MAX_GAP = 20;
    private final int WIDTH = HEADERS.length;
    
    private int COUNT;
    
    // init JThings
    private JLabel[] headerLabels;
    private JLabel[] dayLabels;
    private JLabel[] labels;
    private JLabel[] dates;
    private JTextArea[] tas;
    private JButton[] submitButtons;
    private JButton[] setDefaultButtons;
    private JButton updateButton;
    
    
    // cloudPath
    private boolean cloudExists;
    private String cloudPath = "C:/Users/" +  USERNAME + "/Google Drive/";
    
    // init classes
    private GridLayout experimentLayout;
    private String finalString;
    
    private String[][] db;
    private List<String> lines = new ArrayList<String>();
    
    public TPM() {
        super();
    }
    
    // handles button events
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            for (int i = 0; i < COUNT; i++) {
                if (ae.getSource() == submitButtons[i]) {
                    System.out.println("Adding date to index " + i);
                    addDate(i);
                    saveToFile(PACK_NAME + "/" + FILENAME);
    
                    // just a checker to make sure the file is there
                    if (cloudExists) {
                        saveToFile(cloudPath + FILENAME);
                    }
                    updateDaysUntil();
                } else if (ae.getSource() == setDefaultButtons[i]) {
                    //System.out.println("Adding date to index " + i);
                    //addDate(i);
                    setDefaultDays(i);
                    saveToFile(PACK_NAME + "/" + FILENAME);
                    // just a checker to make sure the file is there
                    if (cloudExists) {
                        saveToFile(cloudPath + FILENAME);
                    }
                }
                
            }
            
            if (ae.getSource() == updateButton) {
                updateDaysUntil();
            }
            
        }
    }
    
    public void addComponentsToPane(final Container pane) {
        
        experimentLayout = new GridLayout(COUNT + 1, WIDTH); // +1 for header row
        
        final JPanel compsToExperiment = new JPanel();
        compsToExperiment.setBorder(new EmptyBorder(10, 10, 10, 10));
        compsToExperiment.setLayout(experimentLayout);

        // Set up components preferred size
        Dimension buttonSize = new JButton("Just a fake button").getPreferredSize();
        Dimension preferredSize = new Dimension((int)(buttonSize.getWidth() * WIDTH) + MAX_GAP * 2,
                                                (int)(buttonSize.getHeight() * COUNT * 1.5 + MAX_GAP * 2));
        compsToExperiment.setPreferredSize(preferredSize);

        for (int i = 0; i < HEADERS.length; i++) {
            headerLabels[i] = new JLabel(HEADERS[i]);
            headerLabels[i].setFont(BOLD_FONT);
            headerLabels[i].setHorizontalAlignment(JLabel.CENTER);
            compsToExperiment.add(headerLabels[i]);
        }

        // Add submitButtons to experimentLayout with Grid Layout
        for (int i = 0; i < COUNT; i++) {
            
            labels[i] = new JLabel(db[i][0]);
            labels[i].setFont(DEF_FONT);
            
            dayLabels[i] = new JLabel();
            updateDaysUntil(i);
            dayLabels[i].setFont(DEF_FONT);
            dayLabels[i].setHorizontalAlignment(JLabel.CENTER);
            
            dates[i] = new JLabel(db[i][1]);
            dates[i].setFont(DEF_FONT);
            dates[i].setHorizontalAlignment(JLabel.CENTER);
            
            tas[i] = new JTextArea(db[i][2]);
            tas[i].setFont(DEF_FONT);
            
            submitButtons[i] = new JButton("Submit");
            submitButtons[i].setFont(DEF_FONT);
            submitButtons[i].addActionListener(new ButtonHandler());
            
            setDefaultButtons[i] = new JButton("Set Default");
            setDefaultButtons[i].setFont(DEF_FONT);
            setDefaultButtons[i].addActionListener(new ButtonHandler());
            
            // add components
            compsToExperiment.add(labels[i]);
            compsToExperiment.add(dayLabels[i]);
            compsToExperiment.add(dates[i]);
            compsToExperiment.add(tas[i]);
            compsToExperiment.add(submitButtons[i]);
            compsToExperiment.add(setDefaultButtons[i]);
        }
        
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new FlowLayout());
        
        updateButton = new JButton();
        updateButton.setFont(DEF_FONT);
        updateButton.addActionListener(new ButtonHandler());
        updateButton.setText("Update days until");
        
        updatePanel.add(updateButton);
        
        experimentLayout.setHgap(5);
        //Set up the vertical gap value
        experimentLayout.setVgap(5);
        //Set up the layout of the submitButtons
        experimentLayout.layoutContainer(compsToExperiment);
        
        pane.add(compsToExperiment, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(updatePanel, BorderLayout.SOUTH);

    }
    
    // adds date entered into JTextArea to the respective index "i" in db
    // updates textarea and date
    public void addDate(int i) {
        // set up parser
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        
        // parse, add, and reformat
        int days = Integer.parseInt(tas[i].getText());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        String next = parser.format(cal.getTime());
        
        // change date
        db[i][1] = next;
        dates[i].setText(db[i][1]);
        
        // reset textarea back to default days
        tas[i].setText(db[i][2]);
    }
    
    private void createAndShowGUI() {
        this.initialize();
        this.setTitle(PRGM_NAME);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        this.addComponentsToPane(this.getContentPane());
        // Display the window.
        this.pack();
        this.setVisible(true);
    }
    
    // return number of days from today
    // includes positive and negative differences
    public int daysFromToday(String nextDate) {
        System.out.println("nextDate = " + nextDate);
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        
        // parse, add, and reformat
        int daysBetween = 0;
        try {
            Date parsedDate = parser.parse(nextDate);
            Calendar date = Calendar.getInstance();
            date.setTime(parsedDate);
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
            // else, daysBetween stays 0
            
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return daysBetween;
    }
    
    // parses db file, reserves space for the JPanel
    public void initialize() {
        try {
            System.out.println("pwd = " + new File(".").getAbsoluteFile());
            Scanner scr = new Scanner(new File(PACK_NAME + "/" + FILENAME));
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
            
            saveToFile(PACK_NAME + "/backup.txt");
            
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        }

        // initialize J stuff based on number of items
        headerLabels = new JLabel[WIDTH];
        labels = new JLabel[COUNT];
        dayLabels = new JLabel[COUNT];
        dates = new JLabel[COUNT];
        tas = new JTextArea[COUNT];
        submitButtons = new JButton[COUNT];
        setDefaultButtons = new JButton[COUNT];
        
        if(!new File(cloudPath).exists()){
            System.out.println("Cloud service 'Google Drive' doesn't exist");
            cloudExists = false;
        } else {
            System.out.println("Cloud service 'Google Drive' exists");
            cloudExists = true;
        }
    }
    
    // saves the formatted datebase (db) to a file
    // assumes db exists and is filled
    // writes finalString to file using writeFile
    public void saveToFile(String path) {
        finalString = new String();
        for (int i = 0; i < COUNT; i++) {
            finalString += db[i][0] + " " + db[i][1] + " " + db[i][2];
            if (i < COUNT - 1) {
                finalString += "\r\n";
            }
        }
        writeFile(path, finalString);
    }
    
    // set the default days left for the item clicked
    public void setDefaultDays(int i) {
        System.out.println("Setting the default days for index " + i + " to " + tas[i].getText());
        db[i][2] = tas[i].getText();
        tas[i].setText(db[i][2]);
    }
    
    // basic filewriter using filename and text to write
    public void writeFile(String path, String text) {
        try {
            File file = new File(path);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.close();
            System.out.println(path + " written");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }
    
    // updates the "days until" field of index "i"
    public void updateDaysUntil(int i) {
        dayLabels[i].setText(Integer.toString(daysFromToday(db[i][1])));
    }
    
    public void updateDaysUntil() {
        for (int i = 0; i < COUNT; i++) {
            dayLabels[i].setText(Integer.toString(daysFromToday(db[i][1])));
        }
    }
    
    // main, duh...
    public static void main(String[] args) {
        
        // create a thread for creating the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TPM tpmController = new TPM();
                tpmController.createAndShowGUI();
            }
        });
    }
}