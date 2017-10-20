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
 * Recurring Task Board helps you keep track of your regularly occuring tasks
 * such as laundry, mowing the lawn, backing up your files, etc.
 * 
 *
 * Could implement
 *     - sort items by date
 * 
 */

package recurringtaskboard;

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

public class RTB extends JFrame {

    // init variables
    public static final Font BOLD_FONT = new Font("Verdana", Font.BOLD, 16);
    public static final Font BASE_FONT = new Font("Verdana", Font.PLAIN, 16);
    public static final String DB_PATH = "java-recurringtask-db.txt";
    public static final String PACK_NAME = RTB.class.getPackage().getName();
    public static final String PRGM_NAME = "Recurring Task Board";
    public static final String USERNAME = System.getProperty("user.name");
    public static final String[] HEADERS = {"Task", "Days until", "Date", "Days", "", ""};
    public static final String CLOUD_PATH = "C:/Users/" +  USERNAME + "/Google Drive";
    public static final int COLUMN_COUNT = HEADERS.length;
    public static final int MAX_GAP = 20;
    
    public int TASK_COUNT;
    
    // initialize JThings
    public JLabel[] headerLabels;
    public JLabel[] dayLabels;
    public JLabel[] taskLabels;
    public JLabel[] dates;
    public JTextArea[] TAs;
    public JButton[] submitButtons;
    public JButton[] setDefaultButtons;
    public JButton updateButton;
    
    // initialize classes
    public GridLayout mainLayout;
    public String string;
    
    public String[][] db;
    public List<String> lines = new ArrayList<String>();
    
    // main method: instantiates and creates the GUI
    public static void main(String[] args) {
        // create a thread for creating the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RTB rtbController = new RTB();
                rtbController.loadFile(new File(PACK_NAME + "/" + DB_PATH));
                rtbController.initializeVars();
                rtbController.setTitle(PRGM_NAME);
                rtbController.build();
            }
        });
    }

    // instantiates
    public RTB() {
        super();
    }
    
    // builds and packs the GUI
    public void build() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentsToPane(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }
        
    // initalizes JPanel variables, checks for cloud service
    public void initializeVars() {

        headerLabels      = new JLabel[COLUMN_COUNT];
        taskLabels        = new JLabel[TASK_COUNT];
        dayLabels         = new JLabel[TASK_COUNT];
        dates             = new JLabel[TASK_COUNT];
        TAs               = new JTextArea[TASK_COUNT];
        submitButtons     = new JButton[TASK_COUNT];
        setDefaultButtons = new JButton[TASK_COUNT];
        
        if (cloudExists()){
            System.out.println("Cloud service 'Google Drive' exists");
        } else {
            System.out.println("Cloud service 'Google Drive' doesn't exist");
        }
    }
    
    // handles button events
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            boolean found;
            for (int i = 0; i < TASK_COUNT; i++) {
                found = false;
                if (ae.getSource() == submitButtons[i]) {
                    // one of the submit buttons was pressed
                    System.out.println("Adding to index " + i + "'s date");
                    setDaysFromTA(i);
                    updateDaysUntil();
                    found = true;
                } else if (ae.getSource() == setDefaultButtons[i]) {
                    // one of the set default buttons was pressed
                    setDefaultDays(i);
                    found = true;
                }
                
                if (found) {
                    saveTasksToFile(PACK_NAME + "/" + DB_PATH);
                    if (cloudExists()) {
                        saveTasksToFile(CLOUD_PATH + "/" + DB_PATH);
                    }
                    System.out.println();
                }
            }
            
            if (ae.getSource() == updateButton) {
                updateDaysUntil();
            }
        }
    }
    
    // @override of super-class, accepts Container object
    public void addComponentsToPane(final Container pane) {
        
        mainLayout = new GridLayout(TASK_COUNT + 1, COLUMN_COUNT); // +1 for header row
        
        final JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(mainLayout);

        // Set up components preferred size
        Dimension buttonSize = new JButton("Just a fake button").getPreferredSize();
        Dimension preferredSize = new Dimension((int)(buttonSize.getWidth() * COLUMN_COUNT) + MAX_GAP * 2,
                                                (int)(buttonSize.getHeight() * (TASK_COUNT + 1) * 1.5 + MAX_GAP * 2));
        mainPanel.setPreferredSize(preferredSize);

        for (int i = 0; i < HEADERS.length; i++) {
            headerLabels[i] = new JLabel(HEADERS[i]);
            headerLabels[i].setFont(BOLD_FONT);
            headerLabels[i].setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(headerLabels[i]);
        }

        // Add submitButtons to mainLayout with Grid Layout
        for (int i = 0; i < TASK_COUNT; i++) {
            
            taskLabels[i] = new JLabel(db[i][0]);
            taskLabels[i].setFont(BASE_FONT);
            
            dayLabels[i] = new JLabel();
            dayLabels[i].setFont(BASE_FONT);
            dayLabels[i].setHorizontalAlignment(JLabel.CENTER);
            updateDaysUntil(i);
            
            dates[i] = new JLabel(db[i][1]);
            dates[i].setFont(BASE_FONT);
            dates[i].setHorizontalAlignment(JLabel.CENTER);
            
            TAs[i] = new JTextArea(db[i][2]);
            TAs[i].setFont(BASE_FONT);
            
            submitButtons[i] = new JButton("Submit");
            submitButtons[i].setFont(BASE_FONT);
            submitButtons[i].addActionListener(new ButtonHandler());
            
            setDefaultButtons[i] = new JButton("Set Default");
            setDefaultButtons[i].setFont(BASE_FONT);
            setDefaultButtons[i].addActionListener(new ButtonHandler());
            
            // add components
            mainPanel.add(taskLabels[i]);
            mainPanel.add(dayLabels[i]);
            mainPanel.add(dates[i]);
            mainPanel.add(TAs[i]);
            mainPanel.add(submitButtons[i]);
            mainPanel.add(setDefaultButtons[i]);
        }
        
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new FlowLayout());
        
        updateButton = new JButton();
        updateButton.setFont(BASE_FONT);
        updateButton.addActionListener(new ButtonHandler());
        updateButton.setText("Update days until");
        
        updatePanel.add(updateButton);
        
        mainLayout.setHgap(5);
        mainLayout.setVgap(5);
        mainLayout.layoutContainer(mainPanel);
        
        pane.add(mainPanel, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(updatePanel, BorderLayout.SOUTH);
        
        updateDaysUntil();
    }
    
    // returns the number of days from today, + or -
    public int getDaysFromToday(String nextDate) {
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
    
    public void loadFile(File file) {
        try {
            System.out.println("pwd = " + new File(".").getAbsoluteFile());
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                lines.add(scanner.next());
            }
            int linesize = lines.size();
            db = new String[linesize][3];
            for (int i = 0; i < linesize; i++) {
                scanner = new Scanner(lines.get(i));
                for (int j = 0; j < 3; j++) {
                    db[i][j] = scanner.next();
                }
            }
            TASK_COUNT = db.length;
            
            saveTasksToFile(PACK_NAME + "/backups/" + "backup-" + System.currentTimeMillis() + ".txt");
            
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        }
    }
    
    // returns whether cloud storage exists
    public static boolean cloudExists() {
        return new File(CLOUD_PATH).exists();
    }
    
    /* saves the formatted datebase (db) to a file
     * assumes db exists and is filled
     * writes "string" to file using the writeText method
     */
    public void saveTasksToFile(String path) {
        string = new String();
        for (int i = 0; i < TASK_COUNT; i++) {
            string += db[i][0] + " " + db[i][1] + " " + db[i][2];
            if (i < TASK_COUNT - 1) {
                string += "\r\n";
            }
        }
        writeText(path, string);
    }
    
    // set the default days left for the item clicked
    public void setDefaultDays(int i) {
        System.out.println("Setting the default days for index " + i + 
                           " from " + db[i][2] + " to " + TAs[i].getText());
        db[i][2] = TAs[i].getText();
        TAs[i].setText(db[i][2]);
    }
    
    // basic filewriter using filename and text to write
    public void writeText(String path, String text) {
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
    
    // updates the days column of all tasks
    public void updateDaysUntil() {
        for (int i = 0; i < TASK_COUNT; i++) {
            updateDaysUntil(i);
        }
    }
    
    // updates the "days until" field of index "i"
    public void updateDaysUntil(int i) {
        int daysuntil = getDaysFromToday(db[i][1]);
        dayLabels[i].setText(Integer.toString(daysuntil));
        if (daysuntil > 0) {
            dayLabels[i].setForeground(Color.BLACK);
        } else if (daysuntil == 0) {
            dayLabels[i].setForeground(Color.GREEN);
        /* } else if (0 > daysuntil && daysuntil > -(Integer.parseInt(db[i][2])/2.0)) {
            dayLabels[i].setForeground(Color.ORANGE); */
        } else if (daysuntil < 0) {
            dayLabels[i].setForeground(Color.RED);
        }
    }
    
    // adds date entered into JTextArea to the respective index "i" in db
    // updates textarea and date
    public void setDaysFromTA(int i) {
        // parse, add, and reformat
        try {
            // set up parser
            int days = Integer.parseInt(TAs[i].getText());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, days);
            SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
            String next = parser.format(cal.getTime());
            
            // change date
            db[i][1] = next;
            dates[i].setText(db[i][1]);
            
        } catch (Exception e) {
            System.err.println("Cannot make a date " + e.getMessage().toLowerCase());
        } finally {
            // reset textarea back to default days
            TAs[i].setText(db[i][2]);
        }
    }
}