package platform;

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
        List<Integer> v = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17});
        items_list = new JList();
        DefaultListModel model = new DefaultListModel();
        items_list.setModel(model);
        for (Integer element : v)
            model.addElement(element);
        items_list.setBorder(BorderFactory.createTitledBorder("Item ID"));
        items_list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroller = new JScrollPane(items_list);
        listScroller.setPreferredSize(new Dimension(300, 400));

        // Recommendations List
        String[] columns = {"Position", "Item ID", "Prediction"};
        String[][] data = {{"1", "2", "3"}, {"1", "2", "3"}, {"1", "2", "3"}, {"1", "2", "3"}};
        recommendations_table = new JTable(data, columns);
        JScrollPane tableScroller = new JScrollPane(recommendations_table);

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

        this.add(recommend_btn);

        Container contentPane = frame.getContentPane();
        contentPane.add(this);
        frame.setVisible(true);
    }

    public void rate_btn_handler() {
        int id = (Integer) items_list.getSelectedValue();
        int index = items_list.getSelectedIndex();
        System.out.println("Rate: index=" + index + " id=" + id);

        DefaultListModel model = (DefaultListModel) items_list.getModel();
        if (index != -1) {
            // Rate
            model.remove(index);
        }

    }

    public void recommend_btn_handler() {
        System.out.println("Recommend");
        String[][] data = {{"3", "2", "1"}, {"3", "2", "1"}, {"3", "2", "1"}, {"3", "2", "1"}};
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                recommendations_table.getModel().setValueAt(data[i][j], i, j);
    }

}