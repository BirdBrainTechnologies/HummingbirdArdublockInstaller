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
                }
                else{
                    prefs= new File(System.getProperty("user.home")+"/Library/Arduino/preferences.txt");
                }
                Hashtable table = new Hashtable();
                try {
                    load(new FileInputStream(prefs),table);
                    String sketchbook= (String) table.get("sketchbook.path");
                    FileUtils.copyDirectory(new File("files/Hummingbird"),new File(sketchbook+"/libraries/Hummingbird"));
                    FileUtils.copyFileToDirectory(new File("files/ardublock-all.jar"),new File(sketchbook+"/tools/ArduBlockTool/tool"));
                    JOptionPane.showMessageDialog(null,"Done!");
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null,"An error occurred. Please try again.");
                    e.printStackTrace();
                }
            }
        });
        setContentPane(MainPanel);
        setTitle("Hummingbird + Ardublock Installer");
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
}
