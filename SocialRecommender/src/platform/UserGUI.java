package platform;

import platform.predicates.Recommendation;
import platform.predicates.Recommendations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class UserGUI extends JPanel {
    private JList items_list;
    private JTable recommendations_table;
    private JTextField size_text;
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
        frame.setSize(800, 600);
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
        listScroller.setPreferredSize(new Dimension(300, 300));

        // Recommendations List
        String[] columns = {"Position", "Item ID", "Prediction"};
        String[][] data = {};
        DefaultTableModel dtm = new DefaultTableModel(0, 0);
        recommendations_table = new JTable();
        dtm.setColumnIdentifiers(columns);
        recommendations_table.setModel(dtm);
        JScrollPane tableScroller = new JScrollPane(recommendations_table);
        tableScroller.setPreferredSize(new Dimension(300, 300));

        JLabel value_label = new JLabel("Rating:");
        // Value TextBox
        value_text = new JTextField(10);

        // Rate Button
        JButton rate_btn = new JButton("Rate");
        rate_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rate_btn_handler();
            }
        });

        JLabel user_label = new JLabel("User ID:");
        //User Text
        user_text = new JTextField(10);

        JLabel trust_label = new JLabel("Trust:");
        //Trust Text
        trust_text = new JTextField(10);

        // Get Items Not Rated Button
        JButton get_items_btn = new JButton("Get Items Not Rated");
        get_items_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                get_items_handler();
            }
        });

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

        JLabel size_label = new JLabel("Size:");
        this.size_text = new JTextField(10);

        this.setLayout(new BorderLayout());


        JPanel top = new JPanel();
        this.add(top, BorderLayout.PAGE_START);
        JPanel top_left = new JPanel();
        top.add(top_left, BorderLayout.WEST);
        top_left.setBorder(BorderFactory.createTitledBorder("Items not Rated"));
        top_left.add(listScroller);
        JPanel top_right = new JPanel();
        top.add(top_right, BorderLayout.EAST);
        top_right.setBorder(BorderFactory.createTitledBorder("Recommendations"));
        top_right.add(tableScroller);


        JPanel center = new JPanel();
        JPanel left = new JPanel();
        center.add(left, BorderLayout.WEST);
        left.setBorder(BorderFactory.createTitledBorder("Rate Item"));
        left.add(value_label);
        left.add(value_text);
        left.add(rate_btn);
        left.add(Box.createHorizontalStrut(135));
        JPanel right = new JPanel();
        center.add(right, BorderLayout.EAST);
        right.setBorder(BorderFactory.createTitledBorder("Get Recommendations"));
        right.add(size_label);
        right.add(size_text);
        right.add(recommend_btn);
        this.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        this.add(bottom, BorderLayout.PAGE_END);
        JPanel bottom_left = new JPanel();
        center.add(bottom_left, BorderLayout.WEST);
        bottom_left.setBorder(BorderFactory.createTitledBorder("Change Trust"));
        bottom_left.add(user_label);
        bottom_left.add(user_text);
        bottom_left.add(trust_label);
        bottom_left.add(trust_text);
        bottom_left.add(change_trust_btn);
        bottom_left.add(Box.createHorizontalStrut(65));
        JPanel bottom_right = new JPanel();
        center.add(bottom_right, BorderLayout.EAST);
        bottom_right.setBorder(BorderFactory.createTitledBorder("Get Items & Update Model"));
        bottom_right.add(get_items_btn);
        bottom_right.add(update_model_btn);

        Container contentPane = frame.getContentPane();
        contentPane.add(this);
        frame.setVisible(true);
    }

    public void get_items_handler() {
        System.out.println("Get Items");
        user.requestItems(user_id);
    }

    public void rate_btn_handler() {

        // Check if value typed is an double
        try {
            double rating = Double.parseDouble(value_text.getText());
            if (rating < 0.5 || rating > 4) {
                JOptionPane.showMessageDialog(null, "Rating must be between 0.5 and 4.0");
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
        int size = 20;
        try {
            size = Integer.parseInt(size_text.getText());
            if (size < 1 || size > 50) {
                JOptionPane.showMessageDialog(null, "Size of recommendation must be between 1 and 50");
                return;
            }
            user.requestRecommendation(user_id, size);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Size must be and integer");
            return;
        }
    }

    public void change_trust_handler() {
        try {
            int user_id2 = Integer.parseInt(user_text.getText());
            int trust = Integer.parseInt(trust_text.getText());
            if (trust != 0 && trust != 1) {
                JOptionPane.showMessageDialog(null, "Trust must be 0 or 1");
                return;
            }
            user.changeTrust(this.user_id, user_id2, trust_text.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "User id and trust must be integers");
        }
    }

    public void update_model_handler() {
        System.out.println("Update Model");
        user.updateModel();
    }

    public void load_items(List<Integer> items) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DefaultListModel dm = (DefaultListModel) items_list.getModel();
                dm.removeAllElements();
                for (int i = 0; i < items.size(); i++)
                    dm.addElement(items.get(i));
            }
        });
    }

    public void load_recommendations(Recommendations recommendations) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                List<Recommendation> recommendationList = recommendations.getRecommendations();
                DefaultTableModel dtm = (DefaultTableModel) recommendations_table.getModel();

                // Remove all elements
                int rowCount = dtm.getRowCount();
                for (int i = rowCount - 1; i >= 0; i--) {
                    dtm.removeRow(i);
                }

                // Add elements
                for (int i = 0; i < recommendationList.size(); i++) {
                    dtm.addRow(new Object[]{i+1, recommendationList.get(i).getItem_id(), recommendationList.get(i).getRating()});
                }
            }
        });
    }

    public void action_done() {
        JOptionPane.showMessageDialog(null, "Action finished!");
    }

    public void action_failed() {
        JOptionPane.showMessageDialog(null, "Action failed, try to update model");
    }

}