package platform;

import platform.predicates.Recommendation;
import platform.predicates.Recommendations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class UserGUI extends JPanel {
    private JList items_list;
    private JTable recommendations_table;
    private JTextField value_text;
    private JTextField user_text;
    private JTextField trust_text;

    private User user;
    private int user_id;

    public UserGUI(User user, int user_id) {
        this.user = user;
        this.user_id = user_id;
    }

    public void loadGUI() {
        // Configure frame
        JFrame frame = new JFrame();
        frame.setTitle("User");
        frame.setSize(800, 550);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Items List
        items_list = new JList();
        DefaultListModel model = new DefaultListModel();
        items_list.setModel(model);
        items_list.setBorder(BorderFactory.createTitledBorder("Item ID"));
        items_list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroller = new JScrollPane(items_list);
        listScroller.setPreferredSize(new Dimension(300, 400));

        // Recommendations List
        String[] columns = {"Position", "Item ID", "Prediction"};
        String[][] data = {};
        DefaultTableModel dtm = new DefaultTableModel(0, 0);
        recommendations_table = new JTable();
        dtm.setColumnIdentifiers(columns);
        recommendations_table.setModel(dtm);
        JScrollPane tableScroller = new JScrollPane(recommendations_table);

        // Value TextBox
        value_text = new JTextField(10);

        // Rate Button
        JButton rate_btn = new JButton("Rate");
        rate_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rate_btn_handler();
            }
        });

        //User Text
        user_text = new JTextField(10);

        //Trust Text
        trust_text = new JTextField(10);

        // Change Trust Button
        JButton change_trust_btn = new JButton("Change Trust");
        change_trust_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                change_trust_handler();
            }
        });

        // Recommend Button
        JButton recommend_btn = new JButton("Get Recommendations");
        recommend_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recommend_btn_handler();
            }
        });


        // Update Model Button
        JButton update_model_btn = new JButton("Update Model");
        update_model_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update_model_handler();
            }
        });

        // Panel
        this.add(listScroller);
        this.add(tableScroller);
        this.add(value_text);
        this.add(rate_btn);
        this.add(recommend_btn);
        this.add(user_text);
        this.add(trust_text);
        this.add(change_trust_btn);
        this.add(update_model_btn);

        Container contentPane = frame.getContentPane();
        contentPane.add(this);
        frame.setVisible(true);

        //Load Items
        user.requestItems(user_id);
    }

    public void rate_btn_handler() {

        // Check if value typed is an double
        try {
            double rating = Double.parseDouble(value_text.getText());
            if (rating < 1 || rating > 4) {
                JOptionPane.showMessageDialog(null, "Rating must be between 1 and 4");
                return;
            }
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Type a double rating");
            return;
        }

        int index = items_list.getSelectedIndex();
        if (index != -1) {
            int id = (int) (long) items_list.getSelectedValue();
            user.rateItem(user_id, id, value_text.getText());

            DefaultListModel model = (DefaultListModel) items_list.getModel();
            model.remove(index);
        }
    }

    public void recommend_btn_handler() {
        System.out.println("Recommend");
        user.requestRecommendation(user_id);
    }

    public void change_trust_handler() {
        try {
            int user_id2 = Integer.parseInt(user_text.getText());
            int trust = Integer.parseInt(trust_text.getText());
            if (trust != 0 && trust != 1) {
                JOptionPane.showMessageDialog(null, "Trust must be 0 or 1");
                return;
            }
            user.changeTrust(this.user_id, user_id2, trust_text.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "User id and trust must be integers");
        }
    }

    public void update_model_handler() {
        System.out.println("Update Model");
        user.updateModel();
    }

    public void load_items(List<Integer> items) {
        DefaultListModel dm = (DefaultListModel) items_list.getModel();
        dm.removeAllElements();
        for (int i = 0; i < items.size(); i++)
            dm.addElement(items.get(i));
        items_list.updateUI();
    }

    public void load_recommendations(Recommendations recommendations) {
        List<Recommendation> recommendationList = recommendations.getRecommendations();
        DefaultTableModel dtm = (DefaultTableModel) recommendations_table.getModel();

        for (int i = 0; i < recommendationList.size(); i++) {
            dtm.addRow(new Object[]{i, recommendationList.get(i).getItem_id(), recommendationList.get(i).getRating()});
        }
        recommendations_table.updateUI();
    }


}