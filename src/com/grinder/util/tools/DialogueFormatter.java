package com.grinder.util.tools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dexter Morgan
 * <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class DialogueFormatter extends JFrame implements ActionListener {

    private static ArrayList<Dialogue> dialogues = new ArrayList<>();

    private JTextField input;
    private JButton print;

    private JButton add;
    private JButton clear;

    private JComboBox dialogueType;

    private JComboBox expression;


    private JTextField npcId;
    private JTextField nextDialoueId;

    private JLabel information;

    private int nextDialogueId;

    public static HashMap<Integer, ArrayList<String>> splitText(String s, int size) {
        HashMap<Integer, ArrayList<String>> list = new HashMap<>();
        String string = "";
        int length = 0;
        int dialogueId = 0;
        for (String str : s.split(" ")) {
            string += str + " ";
            length += str.length();
            list.computeIfAbsent(dialogueId, k -> new ArrayList<>());
            if (list.get(dialogueId).size() == 4) {
                dialogueId++;
            }
            list.computeIfAbsent(dialogueId, k -> new ArrayList<>());
            if (length >= size) {
                list.get(dialogueId).add(string);
                string = "";
                length = 0;
            }
        }
        if (string.length() > 1) {
            list.get(dialogueId).add(string);
        }
        return list;
    }

    public void finish() {
        System.out.println("Printing dialouges.. " + dialogues.size() + " total.");

        String split = System.getProperty("line.separator");

        String data = "";

        for (Dialogue dialogue : dialogues) {

            data += ("{" + split);
            data += ("\"id\": " + dialogue.id + " ," + split);
            data += ("\"type\": \"" + dialogue.type + "\"," + split);
            data += ("\"anim\": \""+dialogue.expression+"\"," + split);
            data += ("\"lines\": " + dialogue.dialogues.size() + "," + split);

            int line = 1;

            for (String s : dialogue.dialogues) {
                data += ("\"line" + line + "\": \"" + s + "\"," + split);
                line++;
            }

            data += ("\"next\": " + dialogue.nextDialogue + "," + split);
            data += ("\"npcId\": " + dialogue.npcId + "" + split);
            data += ("}," + split);
        }

        try {
            Files.write(Paths.get("./data/definitions/quest/df.cfg"), data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }

    private void add() {

        HashMap<Integer, ArrayList<String>> list = splitText(input.getText(), 30);

        int nextDialogue = Integer.parseInt(nextDialoueId.getText());

        if (nextDialogue > nextDialogueId) {
            nextDialogueId = nextDialogue;
        }

        for (Map.Entry<Integer, ArrayList<String>> e : list.entrySet()) {
            ArrayList<String> d = e.getValue();

            Dialogue dialogue = new Dialogue();

            dialogue.id = nextDialogueId;
            dialogue.nextDialogue = nextDialogueId + 1;
            dialogue.type = (String) dialogueType.getSelectedItem();
            dialogue.dialogues.addAll(d);
            dialogue.npcId = Integer.parseInt(npcId.getText());
            dialogue.expression = (String) expression.getSelectedItem();

            dialogues.add(dialogue);

            nextDialogueId++;

            String info = "Added dialogue. Total dialogues: " + dialogues.size();

            System.out.println(info);

            information.setText(info);
        }

        input.setText("");
    }

    private static class Dialogue {
        private int id;
        private String type;
        private ArrayList<String> dialogues = new ArrayList<>();
        private int nextDialogue;
        private int npcId;

        private String expression;
    }

    public DialogueFormatter() {

        setLayout(null);
        input = new JTextField(5);
        input.setBounds(0, 0, 100, 20);
        add(input);

        print = new JButton("Print");
        print.setBounds(120, 200, 100, 20);
        print.addActionListener(this);
        add(print);

        clear = new JButton("Clear");
        clear.setBounds(120, 240, 100, 20);
        clear.addActionListener(this);
        add(clear);

        add = new JButton("Add");
        add.setBounds(0, 100, 100, 20);
        add.addActionListener(this);
        add(add);

        npcId = new JTextField(5);
        npcId.setBounds(0, 60, 100, 20);
        add(npcId);

        nextDialoueId = new JTextField(5);
        nextDialoueId.setBounds(0, 80, 100, 20);
        add(nextDialoueId);

        JLabel textInput = new JLabel("Input dialogue text");
        textInput.setBounds(100, 0, 150, 20);
        add(textInput);

        JLabel npcIdInput = new JLabel("Input NPC Id");
        npcIdInput.setBounds(100, 60, 150, 20);
        add(npcIdInput);

        JLabel dialogueIdInput = new JLabel("Input Next Dialogue Id");
        dialogueIdInput.setBounds(100, 80, 150, 20);
        add(dialogueIdInput);

        information = new JLabel("Enter input on the above");
        information.setBounds(20, 150, 350, 20);
        add(information);

        String s1[] = {"NPC_STATEMENT", "PLAYER_STATEMENT", "QUEST_STAGE", "STATEMENT", "OPTION"};

        dialogueType = new JComboBox(s1);

        dialogueType.setBounds(0, 20, 100, 20);
        dialogueType.addActionListener(this);
        add(dialogueType);

        String[] expressions = {
                        "HAPPY", "LAUGHING", "CURIOUS", "SURPRISED",
                "THINKING", "CALM", "EVIL", "EVIL_DELIGHTED",
                "ANNOYED", "DISTRESSED", "CRYING_ALMOST",
                "SAD", "SLEEPY", "ANGRY",
        };

        expression = new JComboBox(expressions);

        expression.setBounds(0, 40, 100, 20);
        expression.addActionListener(this);
        add(expression);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clear) {
            dialogues.clear();
            nextDialogueId = 0;
            information.setText("Dialogues cleared.");
        } else if (e.getSource() == add) {
            add();
        } else if (e.getSource() == print) {
            finish();
            input.setText("");
        }
    }

    public static void main(String[] args) {
        DialogueFormatter me = new DialogueFormatter();
        me.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        me.setVisible(true);
        me.setSize(300, 300);
        me.setTitle("Dialogue Formatter");
    }
}
