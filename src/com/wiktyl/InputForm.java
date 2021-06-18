package com.wiktyl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.wiktyl.model.Data;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import org.python.util.PythonInterpreter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Array;
import java.text.DecimalFormat;

public class InputForm {
    public JPanel rootPanel;
    private JTextField koszt_tf;
    private JTextField trudnosc_tf;
    private JButton submit_btn;
    private JLabel wynik_label;
    private JTextField dlugosc_tf;
    private JButton zPlikuButton;
    private JTextField zysk_tf;
    private JFileChooser fileChooser;

    //Load fcl file
    String fileName = "./ocena.fcl";
    FIS fis = FIS.load(fileName, true);

    //dobry, sredni, zly
    Double[] score_arr = new Double[3];

    // Pokazuje reguly
    FunctionBlock functionBlock = fis.getFunctionBlock(null);
//		JFuzzyChart.get().chart(functionBlock);

    public InputForm() {
        submit_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DecimalFormat df = new DecimalFormat("0.00");
                fis.setVariable("koszty", Double.parseDouble(koszt_tf.getText()));
                fis.setVariable("trudnosc", Double.parseDouble(trudnosc_tf.getText()));
                fis.setVariable("dlugosc", Double.parseDouble(dlugosc_tf.getText()));
                fis.setVariable("zysk", Double.parseDouble(zysk_tf.getText()));

                System.out.println(koszt_tf.getText());
                System.out.println(trudnosc_tf.getText());
                System.out.println(dlugosc_tf.getText());
                System.out.println(zysk_tf.getText());

                fis.evaluate();

                score_arr[0] = fis.getVariable("projekt").getMembership("dobry");
                score_arr[1] = fis.getVariable("projekt").getMembership("sredni");
                score_arr[2] = fis.getVariable("projekt").getMembership("slaby");

                int final_score = get_the_biggest_from_array(score_arr);

                wynik_label.setText("Ocena: " + df.format(fis.getVariable("projekt").getValue()) + " (" + get_name(final_score) + " projekt)");
            }
        });
        zPlikuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                int return_value = fc.showOpenDialog(rootPanel);
                String final_path = "";
                if (return_value == JFileChooser.APPROVE_OPTION) {
                    File selected_file = fc.getSelectedFile();
                    System.out.println(selected_file.getAbsolutePath());
                    Process p = null;
                    try {
                        //idk how to repair it, bug still exists
                        if (selected_file.getAbsolutePath().contains(" ")) {
                            File new_file = new File(selected_file.getAbsolutePath().replace(" ", "_"));
                            if (new_file.exists()) {
                                throw new IOException("file exists");
                            }
                            boolean success = selected_file.renameTo(new_file);
                            System.out.println(selected_file.getAbsolutePath());
                            System.out.println(new_file.getAbsolutePath());
                            final_path = new_file.getAbsolutePath();
                            p = Runtime.getRuntime().exec("python3 src/com/wiktyl/main.py" + " " + new_file.getAbsolutePath());
                        } else {
                            p = Runtime.getRuntime().exec("python3 src/com/wiktyl/main.py" + " " + selected_file.getAbsolutePath());
                            final_path = selected_file.getAbsolutePath();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String ret = null;
                    try {
                        ret = in.readLine();
                        System.out.println(ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame, "Podany plik jest błędny.");
                    }
                    if (ret == null || !ret.equals("OK")) {
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame, "Podany plik jest błędny.");
                    } else {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        JsonReader reader = null;
                        try {
                            reader = new JsonReader(new FileReader(final_path + ".json"));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Gson gson = builder.create();
                        Data data_x = gson.fromJson(reader, Data.class);
                        koszt_tf.setText(String.valueOf(data_x.getKoszt()));
                        trudnosc_tf.setText(String.valueOf(data_x.getTrudnosc()));
                        dlugosc_tf.setText(data_x.getCzas_trwania());
                        zysk_tf.setText(String.valueOf(data_x.getZysk()));

                        DecimalFormat df = new DecimalFormat("0.00");
                        fis.setVariable("koszty", Double.parseDouble(koszt_tf.getText()));
                        fis.setVariable("trudnosc", Double.parseDouble(trudnosc_tf.getText()));
                        fis.setVariable("dlugosc", Double.parseDouble(dlugosc_tf.getText()));
                        fis.setVariable("zysk", Double.parseDouble(zysk_tf.getText()));

                        fis.evaluate();

                        score_arr[0] = fis.getVariable("projekt").getMembership("dobry");
                        score_arr[1] = fis.getVariable("projekt").getMembership("sredni");
                        score_arr[2] = fis.getVariable("projekt").getMembership("slaby");

                        int final_score = get_the_biggest_from_array(score_arr);

                        wynik_label.setText("Ocena: " + df.format(fis.getVariable("projekt").getValue()) + " (" + get_name(final_score) + " projekt)");
                    }
                }
            }
        });
    }

    public int get_the_biggest_from_array(Double array[]) {
        int maxAt = 0;

        for (int i = 0; i < array.length; i++) {
            maxAt = array[i] > array[maxAt] ? i : maxAt;
        }
        return maxAt;
    }

    public String get_name(int index) {
        if (index == 0) {
            return "Dobry";
        } else if (index == 1) {
            return "Średni";
        } else {
            return "Słaby";
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(11, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Wprowadź koszt projektu (0 - ...)");
        rootPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        koszt_tf = new JTextField();
        rootPanel.add(koszt_tf, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Wprowadź trudność projektu (0 - 10)");
        rootPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wynik_label = new JLabel();
        wynik_label.setText("Kliknij potwierdź aby uzyskać ocenę projektu albo z piku");
        rootPanel.add(wynik_label, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        trudnosc_tf = new JTextField();
        trudnosc_tf.setMargin(new Insets(2, 6, 2, 6));
        rootPanel.add(trudnosc_tf, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Wprowadź długość projektu w miesiącach (0 - ...)");
        rootPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dlugosc_tf = new JTextField();
        rootPanel.add(dlugosc_tf, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        submit_btn = new JButton();
        submit_btn.setMargin(new Insets(0, 0, 0, 0));
        submit_btn.setText("Potwierdź");
        rootPanel.add(submit_btn, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(405, 30), null, 0, false));
        zPlikuButton = new JButton();
        zPlikuButton.setText("Z pliku");
        rootPanel.add(zPlikuButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(405, 30), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Wprowadź zysk z projektu (0-...)");
        rootPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zysk_tf = new JTextField();
        rootPanel.add(zysk_tf, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
