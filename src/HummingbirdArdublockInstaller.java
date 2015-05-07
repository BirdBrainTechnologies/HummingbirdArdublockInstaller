import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.util.Map;
import java.util.Hashtable;


public class HummingbirdArdublockInstaller extends JFrame {
    private JButton goButton;
    private JPanel MainPanel;
    private JCheckBox AdvancedArdublock;
    private JLabel helpText;
    private JLabel installHelpText;
    private JFileChooser fc;

    public HummingbirdArdublockInstaller(){
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File prefs;
                if(SystemUtils.IS_OS_LINUX) {
                    prefs = new File(System.getProperty("user.home") + "/.arduino/preferences.txt");
                }
                else if(SystemUtils.IS_OS_WINDOWS) {
                    prefs= new File(System.getenv("APPDATA")+"/Arduino/preferences.txt");
                    if(!prefs.exists()) {
                        prefs = new File(com.sun.jna.platform.win32.Shell32Util.getFolderPath(0x001a) + "/Arduino/preferences.txt");
                    }
                } else {
                    prefs= new File(System.getProperty("user.home")+"/Library/Arduino/preferences.txt");
                }
                Hashtable table = new Hashtable();
                try {
                    String sketchbook = null;
                    String message;
                    if(prefs.exists()) {
                        load(new FileInputStream(prefs), table);
                        sketchbook = (String) table.get("sketchbook.path");
                        System.out.println("Preferences file exists");
                    }
                    else {

                        System.out.println("Preferences file does not exist, ask user to locate");
                        fc = new JFileChooser();
                        System.out.println("Got here");
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        System.out.println("Directories only");
                        JOptionPane.showMessageDialog(null,"Couldn't automatically find the Arduino Sketchbook directory, press OK to manually select it.\n If unsure of the location, open the Arduino IDE, select File->Preferences and note the \"Sketchbook\" location.");

                        System.out.println("Show dialog");
                        int returnVal = fc.showDialog(HummingbirdArdublockInstaller.this, "Select");
                        System.out.println("Show file chooser");
                        if(returnVal == JFileChooser.APPROVE_OPTION) {
                            sketchbook = fc.getSelectedFile().toString();
                            installHelpText.setText("Files will be installed to " + sketchbook);
                        }
                    }

                    if(sketchbook != null) {

                        FileUtils.copyDirectory(new File("files/Hummingbird"), new File(sketchbook + "/libraries/Hummingbird"));
                        FileUtils.copyDirectory(new File("files/hardware/hbduo"), new File(sketchbook + "/hardware/hbduo"));
                        if (AdvancedArdublock.isSelected()) {
                            FileUtils.copyFileToDirectory(new File("files/Ardublock_advanced/ardublock-all.jar"), new File(sketchbook + "/tools/ArduBlockTool/tool"));
                        } else {
                            FileUtils.copyFileToDirectory(new File("files/Ardublock_normal/ardublock-all.jar"), new File(sketchbook + "/tools/ArduBlockTool/tool"));
                        }

                        FileUtils.copyDirectory(new File("files/ArdublockDuoExamples"), new File(sketchbook + "/ArdublockDuoExamples"));

                        if (SystemUtils.IS_OS_WINDOWS) {
                            Process p = Runtime.getRuntime().exec("files\\WindowsDrivers\\runDriverInstall.bat");
                            message = "Done copying files, now installing Duo driver";
                        } else {
                            message = "Done! Close me and restart the Arduino IDE!";
                        }
                    }
                    else {
                        message = "No Arduino Sketchbook directory found, please install the Arduino environment and run it once, then try this installer again.";
                    }
                    JOptionPane.showMessageDialog(null,message);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null,"An error occurred. Please try again or use the manual way to install described in the tutorial.");
                    e.printStackTrace();
                }
            }
        });
        setContentPane(MainPanel);
        setTitle("Hummingbird Arduino Library Installer");
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    public static void main(String[] args){
        try {
            if(SystemUtils.IS_OS_LINUX) //Set Linux L&F to GTK+
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            else // Set System L&F
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        HummingbirdArdublockInstaller window = new HummingbirdArdublockInstaller();
    }

    static public void load(InputStream input, Map table) throws IOException {
        String[] lines = loadStrings(input); // Reads as UTF-8
        for (String line : lines) {
            if ((line.length() == 0) ||
                    (line.charAt(0) == '#')) continue;

            // this won't properly handle = signs being in the text
            int equals = line.indexOf('=');
            if (equals != -1) {
                String key = line.substring(0, equals).trim();
                String value = line.substring(equals + 1).trim();
                table.put(key, value);
            }
        }
    }

    static public String[] loadStrings(InputStream input) {
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(input, "UTF-8"));

            String lines[] = new String[100];
            int lineCount = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (lineCount == lines.length) {
                    String temp[] = new String[lineCount << 1];
                    System.arraycopy(lines, 0, temp, 0, lineCount);
                    lines = temp;
                }
                lines[lineCount++] = line;
            }
            reader.close();

            if (lineCount == lines.length) {
                return lines;
            }

            // resize array to appropriate amount for these lines
            String output[] = new String[lineCount];
            System.arraycopy(lines, 0, output, 0, lineCount);
            return output;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
