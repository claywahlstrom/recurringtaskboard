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
 * Recurring Task Board can help you keep track of your regularly occurring tasks
 * such as laundry, mowing the lawn, backing up your files, etc.
 *
 * TODO
 * Could implement
 *     - sort items by date
 *
 */

package recurringtaskboard;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
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

    // initialize variables
    public static final Font BASE_FONT = new Font("Verdana", Font.PLAIN, 16);
    public static final Font BOLD_FONT = new Font("Verdana", Font.BOLD, 16);
    public static final String DB_PATH = "recurringtasks-db.txt";
    public static final String DB_EXAMPLE_PATH = "recurringtasks-db-example.txt";
    public static final String LINE_ENDING = "\r\n";
    public static final String PACK_NAME = RTB.class.getPackage().getName();
    public static final String PRGM_NAME = "Recurring Task Board";
    public static final String USERNAME = System.getProperty("user.name");
    public static final String[] HEADERS = {"Task", "Days until", "Date", "Days", "Days delta", "", ""};
    public static final String CLOUD_PATH = "C:/Users/" +  USERNAME + "/Google Drive";
    public static final int DB_COLUMNS = 3;
    public static final int MAX_BACKUP = 15;
    public static final int MAX_GAP = 20;

    // initialize JThings
    private JLabel[] headerLabels;
    private JLabel[] taskLabels;
    private JLabel[] daysUntilLabels;
    private JLabel[] dateLabels;
    private JLabel[] daysLabels;
    private JTextArea[] deltaTextAreas;
    private JButton[] addDaysButtons;
    private JButton[] setDaysButtons;
    private JButton updateButton;

    private String[][] db;
    private List<String> lines = new ArrayList<String>();

    // main method: instantiates and creates a thread for the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File backups = new File(PACK_NAME + "/backups");
                if (!backups.exists()) {
                    backups.mkdir();
                }
                RTB rtbController = new RTB();
                rtbController.loadFile(new File(PACK_NAME + "/" + DB_PATH));
                rtbController.initializeVars();
                rtbController.render();
            }
        });
    }

    // creates a new task manager
    public RTB() {
        super(PRGM_NAME);
    }

    // @override of super-class, accepts Container object
    public void addComponentsToPane(final Container pane) {

        GridLayout mainLayout = new GridLayout(db.length + 1, HEADERS.length); // +1 for header row

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(mainLayout);

        // Set up components preferred size
        Dimension buttonSize = new JButton("Just a fake button").getPreferredSize();
        Dimension preferredSize = new Dimension((int)(buttonSize.getWidth() * HEADERS.length * 0.9) + MAX_GAP * 2,
                                                (int)(buttonSize.getHeight() * (db.length + 1) * 1.5 + MAX_GAP * 2));
        mainPanel.setPreferredSize(preferredSize);

        for (int i = 0; i < HEADERS.length; i++) {
            headerLabels[i] = new JLabel(HEADERS[i]);
            headerLabels[i].setFont(BOLD_FONT);
            headerLabels[i].setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(headerLabels[i]);
        }

        // Add submitButtons to mainLayout with Grid Layout
        for (int i = 0; i < db.length; i++) {

            taskLabels[i] = new JLabel(db[i][0]);
            taskLabels[i].setFont(BASE_FONT);

            daysUntilLabels[i] = new JLabel();
            daysUntilLabels[i].setFont(BASE_FONT);
            daysUntilLabels[i].setHorizontalAlignment(JLabel.CENTER);

            dateLabels[i] = new JLabel(db[i][1]);
            dateLabels[i].setFont(BASE_FONT);
            dateLabels[i].setHorizontalAlignment(JLabel.CENTER);

            daysLabels[i] = new JLabel(db[i][2]);
            daysLabels[i].setFont(BASE_FONT);
            daysLabels[i].setHorizontalAlignment(JLabel.CENTER);

            deltaTextAreas[i] = new JTextArea(db[i][2]);
            deltaTextAreas[i].setFont(BASE_FONT);

            addDaysButtons[i] = new JButton("Add delta");
            addDaysButtons[i].setFont(BASE_FONT);
            addDaysButtons[i].addActionListener(new ButtonHandler());

            setDaysButtons[i] = new JButton("Set days");
            setDaysButtons[i].setFont(BASE_FONT);
            setDaysButtons[i].addActionListener(new ButtonHandler());

            // add components
            mainPanel.add(taskLabels[i]);
            mainPanel.add(daysUntilLabels[i]);
            mainPanel.add(dateLabels[i]);
            mainPanel.add(daysLabels[i]);
            mainPanel.add(deltaTextAreas[i]);
            mainPanel.add(addDaysButtons[i]);
            mainPanel.add(setDaysButtons[i]);
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

    // backs up the db
    public void backup() {
        saveTasksToFile(PACK_NAME + "/backups/backup-" + System.currentTimeMillis() + ".txt");
    }

    // returns whether cloud storage exists
    public boolean checkCloudExists() {
        return new File(CLOUD_PATH).exists();
    }

    public void deleteOld() {
        while (new File(PACK_NAME + "/backups").list().length > MAX_BACKUP) {
            new File(PACK_NAME + "/backups").listFiles()[0].delete();
        }
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

    // initializes JPanel variables, checks if cloud service exists
    public void initializeVars() {

        headerLabels    = new JLabel[HEADERS.length];
        daysUntilLabels = new JLabel[db.length];
        taskLabels      = new JLabel[db.length];
        dateLabels      = new JLabel[db.length];
        daysLabels      = new JLabel[db.length];
        deltaTextAreas  = new JTextArea[db.length];
        addDaysButtons  = new JButton[db.length];
        setDaysButtons  = new JButton[db.length];

        System.out.print("Cloud service 'Google Drive' ");
        if (checkCloudExists()){
            System.out.println("exists");
        } else {
            System.out.println("doesn't exist");
        }
        System.out.println();
    }

    public void loadFile(File file) {
        try {
            System.out.println("pwd = " + new File(".").getAbsoluteFile());
            if (!file.isFile()) {
                // Create db from example if not exists
                Path examplePath = new File(PACK_NAME + "/" + DB_EXAMPLE_PATH).getAbsoluteFile().toPath();
                Path dbPath = new File(PACK_NAME + "/" + DB_PATH).getAbsoluteFile().toPath();
                Files.copy(examplePath, dbPath, StandardCopyOption.REPLACE_EXISTING);
            }
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(LINE_ENDING);
            while (scanner.hasNext()) {
                lines.add(scanner.next());
            }
            scanner.close();
            db = new String[lines.size()][DB_COLUMNS];
            for (int i = 0; i < lines.size(); i++) {
                db[i] = lines.get(i).split(", ");
            }
            backup();
            deleteOld();

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    // renders and packs the GUI
    public void render() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentsToPane(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }

    // writes the database in CSV format to the disk
    public void saveTasksToFile(String path) {
        String string = "";
        for (int i = 0; i < db.length; i++) {
            string += String.join(", ", db[i]) + LINE_ENDING;
        }
        writeTextToFile(path, string.trim());
    }

    // adds the number of days in the text box to the given index,
    // updates text area and date.
    public boolean setDays(int i) {
        boolean success = false;
        try {
            // set up parser
            int days = Integer.parseInt(deltaTextAreas[i].getText().trim());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, days);
            SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
            String next = parser.format(cal.getTime());

            // change date
            db[i][1] = next;
            dateLabels[i].setText(db[i][1]);
            System.out.println("Modifying the date for \"" + db[i][0] + "\" (index " + i + ")"); // only prints if successful
            success = true;
        } catch (Exception e) {
            System.err.println("Cannot change the date of " + e.getMessage());
        } finally {
            // rollback transaction
            deltaTextAreas[i].setText(db[i][2]);
        }
        return success;
    }

    // set the default days interval for the given index.
    // returns true if successful, otherwise false
    public boolean setDefaultDays(int i) {
        boolean isError = false;
        String text = deltaTextAreas[i].getText();
        if (!text.equals("")) {
            System.out.println("Setting the default days for \"" + db[i][0]
                + "\" (index " + i + ") from " + db[i][2] + " to " + text);
            // update the database
            db[i][2] = text;
            // update the UI
            daysLabels[i].setText(text);
        } else {
            System.err.println("Cannot set default days to a blank string.");
            isError = true;
        }
        deltaTextAreas[i].setText(db[i][2]);
        return !isError;
    }

    // updates all of the days column for all of the tasks
    public void updateDaysUntil() {
        for (int i = 0; i < db.length; i++) {
            updateDaysUntilAt(i);
        }
    }

    // updates the "days until" field of the given index
    public void updateDaysUntilAt(int i) {
        int daysuntil = getDaysFromToday(db[i][1]);
        daysUntilLabels[i].setText(Integer.toString(daysuntil));
        if (daysuntil > 0) {
            daysUntilLabels[i].setForeground(Color.BLACK);
        } else if (daysuntil == 0) {
            daysUntilLabels[i].setForeground(Color.GREEN);
        /* } else if (0 > daysuntil && daysuntil > -(Integer.parseInt(db[i][2])/2.0)) {
            dayLabels[i].setForeground(Color.ORANGE); */
        } else if (daysuntil < 0) {
            daysUntilLabels[i].setForeground(Color.RED);
        }
    }

    // writes the given text to the file path
    public void writeTextToFile(String path, String text) {
        try {
            System.out.print("Writing to " + path + "... ");
            File file = new File(path);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.close();
            System.out.println("Done");
        } catch (IOException ioe) {
            System.out.println("Failed");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    // handles button events
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            boolean flushOutput = false;
            for (int i = 0; i < db.length; i++) {
                boolean found = false;
                if (ae.getSource() == addDaysButtons[i]) {
                    // a submit button was pressed
                    found = setDays(i);
                } else if (ae.getSource() == setDaysButtons[i]) {
                    // a set default button was pressed
                    found = setDefaultDays(i);
                }

                if (found) {
                    saveTasksToFile(PACK_NAME + "/" + DB_PATH);
                    backup();
                    deleteOld();
                    if (checkCloudExists()) {
                        saveTasksToFile(CLOUD_PATH + "/" + DB_PATH);
                    }
                    flushOutput = true;
                }
            }

            if (flushOutput) {
                System.out.println();
            }
            updateDaysUntil(); // behavior when any button is pressed
        }
    }

}
