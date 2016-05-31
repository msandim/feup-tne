package platform;

import platform.predicates.Recommendation;
import platform.predicates.Recommendations;

import javax.swing.*;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class UserGUI extends JPanel {
    private JFrame frame;
    private JList items_list;
    private JTable recommendations_table;
    private JButton rate_btn;
    private JButton recommend_btn;
    private JTextField value_text;

    private User user;
    private int user_id;

    public UserGUI(User user, int user_id) {
        this.user = user;
        this.user_id = user_id;
    }

    public void loadGUI() {
        // Configure frame
        frame = new JFrame();
        frame.setTitle("User");
        frame.setSize(800, 500);
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
        recommendations_table = new JTable(data, columns);
        JScrollPane tableScroller = new JScrollPane(recommendations_table);

        // Value TextBox
        value_text = new JTextField(10);

        // Rate Button
        rate_btn = new JButton("Rate");
        rate_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rate_btn_handler();
            }
        });

        // Recommend Button
        recommend_btn = new JButton("Get Recommendations");
        recommend_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recommend_btn_handler();
            }
        });

        // Panel
        this.add(listScroller);
        this.add(tableScroller);

        this.add(rate_btn);
        this.add(value_text);
        this.add(recommend_btn);

        Container contentPane = frame.getContentPane();
        contentPane.add(this);
        frame.setVisible(true);

        //Load Items
        user.requestItems(user_id);

        //Load Recommendations
        //user.requestRecommendation(user_id);
    }

    public void rate_btn_handler() {

        // Check if value typed is an double
        try {
            Double.parseDouble(value_text.getText());
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

    public void load_items(List<Integer> items) {
        DefaultListModel dm = (DefaultListModel) items_list.getModel();
        dm.removeAllElements();
        for (int i = 0; i < items.size(); i++)
            dm.addElement(items.get(i));
        items_list.updateUI();
    }

    public void load_recommendations(Recommendations recommendations) {
        List<Recommendation> recommendationList = recommendations.getRecommendations();
        for (int i = 0; i < recommendationList.size(); i++) {
            recommendations_table.getModel().setValueAt(i+1, i, 0);
            recommendations_table.getModel().setValueAt(recommendationList.get(i).getItem_id(), i, 1);
            recommendations_table.getModel().setValueAt(recommendationList.get(i).getRating(), i, 2);
        }
    }


}