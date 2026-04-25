//imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

//declarations
public class StudentGradeSystem extends JFrame {
    private JTextField txtStudentName, txtSubjectName, txtScore, txtSearch;
    private DefaultTableModel tableModel;
    private JTable dataTable;
    private JLabel lblOverallStats, lblStudentAvg, lblStudentGrade, lblStudentStatus;
    private DefaultListModel<String> subjectListModel;
    private JList<String> subjectList;

    //store subject + score
    private ArrayList<Object[]> pendingScores = new ArrayList<>();

    //colours
    private Color BG_DARK = new Color(30, 34, 42); //mainly for the bg of the running tool
    private Color BG_PANEL = new Color(40, 44, 55);
    private Color BG_INPUT = new Color(52, 57, 70);
    private Color ACCENT = new Color(72, 152, 241);
    private Color TEXT_PRIMARY = new Color(230, 233, 240);
    private Color TEXT_SECONDARY = new Color(160, 165, 180);
    private Color GREEN = new Color(80, 200, 120);
    private Color RED = new Color(240, 90, 90);

    //the run tool window
    public StudentGradeSystem() {
        setTitle("Student Grading System");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(BG_PANEL);

        //title on the form
        JLabel title = new JLabel("Student Grading System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(BG_PANEL);

        //search bar placeholder text
        txtSearch = new PlaceholderTextField(22);
        ((PlaceholderTextField) txtSearch).setPlaceholder("Search student name...");
        styleTextField(txtSearch);

        JButton btnSearch = createButton("Search", ACCENT, e -> searchStudent());

        right.add(txtSearch);
        right.add(btnSearch);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(15, 0));
        main.setBackground(BG_DARK);
        main.setBorder(BorderFactory.createEmptyBorder(15, 25, 5, 25));

        main.add(buildFormPanel(), BorderLayout.WEST);
        main.add(buildTablePanel(), BorderLayout.CENTER);
        return main;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBorder(createRoundedBorder("New Student Entry"));

        panel.add(createLabel("Student Name"));
        txtStudentName = createTextField(28);
        panel.add(txtStudentName);

        panel.add(Box.createVerticalStrut(10));

        panel.add(createLabel("Subject"));
        txtSubjectName = createTextField(28);
        panel.add(txtSubjectName);

        panel.add(createLabel("Score"));
        txtScore = createTextField(28);
        panel.add(txtScore);

        JButton addBtn = createButton("+ Add Subject", GREEN, e -> addSubjectToList());
        panel.add(addBtn);

        subjectListModel = new DefaultListModel<>();
        subjectList = new JList<>(subjectListModel);
        panel.add(new JScrollPane(subjectList));

        JButton removeBtn = createButton("Remove", RED, e -> removeSubject());
        panel.add(removeBtn);

        panel.add(Box.createVerticalStrut(10));

        lblStudentAvg = new JLabel("Average: —");
        lblStudentGrade = new JLabel("Grade: —");
        lblStudentStatus = new JLabel("Status: —");

        lblStudentAvg.setForeground(TEXT_SECONDARY);
        lblStudentGrade.setForeground(ACCENT);
        lblStudentStatus.setForeground(TEXT_PRIMARY);

        panel.add(lblStudentAvg);
        panel.add(lblStudentGrade);
        panel.add(lblStudentStatus);

        JButton submitBtn = createButton("Submit", ACCENT, e -> submitStudent());
        panel.add(submitBtn);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createRoundedBorder("Records"));
        panel.setBackground(BG_PANEL);

        tableModel = new DefaultTableModel(
                new String[]{"Student", "Subject", "Score", "Grade", "Status"}, 0
        );

        dataTable = new JTable(tableModel);
        panel.add(new JScrollPane(dataTable), BorderLayout.CENTER);

        JButton deleteBtn = createButton("Delete", RED, e -> deleteSelected());
        JButton clearBtn = createButton("Clear All", Color.GRAY, e -> clearAll());

        JPanel bottom = new JPanel();
        bottom.add(deleteBtn);
        bottom.add(clearBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);

        lblOverallStats = new JLabel("Overall: —");
        panel.add(lblOverallStats, BorderLayout.WEST);

        return panel;
    }

    // ================= LOGIC =================

    private void addSubjectToList() {
        String sub = txtSubjectName.getText().trim();
        String scoreStr = txtScore.getText().trim();

        if (sub.isEmpty() || scoreStr.isEmpty()) return;

        try {
            double score = Double.parseDouble(scoreStr);
            pendingScores.add(new Object[]{sub, score});
            subjectListModel.addElement(sub + " - " + score);

            txtSubjectName.setText("");
            txtScore.setText("");
            updatePreview();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid score");
        }
    }

    private void removeSubject() {
        int i = subjectList.getSelectedIndex();
        if (i != -1) {
            subjectListModel.remove(i);
            pendingScores.remove(i);
            updatePreview();
        }
    }

    private void updatePreview() {
        if (pendingScores.isEmpty()) {
            lblStudentAvg.setText("Average: —");
            return;
        }

        double total = 0;
        for (Object[] o : pendingScores) total += (double) o[1];

        double avg = total / pendingScores.size();
        lblStudentAvg.setText("Average: " + avg);
        lblStudentGrade.setText("Grade: " + calculateGrade(avg));
        lblStudentStatus.setText(avg >= 60 ? "PASS" : "FAIL");
    }

    private void submitStudent() {
        String name = txtStudentName.getText();

        for (Object[] o : pendingScores) {
            String sub = (String) o[0];
            double score = (double) o[1];

            tableModel.addRow(new Object[]{
                    name, sub, score,
                    calculateGrade(score),
                    score >= 60 ? "PASS" : "FAIL"
            });
        }

        pendingScores.clear();
        subjectListModel.clear();
        txtStudentName.setText("");
        updateOverall();
    }

    private void updateOverall() {
        if (tableModel.getRowCount() == 0) return;

        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 2);
        }

        //average calculation
        double avg = total / tableModel.getRowCount();
        lblOverallStats.setText("Overall Avg: " + avg);
    }

    //check what's in the input box and reads it lowercase, or capitalised
    private void searchStudent() {
        String target = txtSearch.getText().toLowerCase();

        //clear the area with all results
        dataTable.clearSelection();
        for (int i = 0; i < dataTable.getRowCount(); i++) {
            String name = tableModel.getValueAt(i, 0).toString().toLowerCase();
            if (name.contains(target)) {
                dataTable.addRowSelectionInterval(i, i);
            }
        }
    }

    //deletion of selected items
    private void deleteSelected() {
        int row = dataTable.getSelectedRow();
        if (row != -1) tableModel.removeRow(row);
    }

    //clear all function
    private void clearAll() {
        tableModel.setRowCount(0);
    }

    //grading criteria
    private String calculateGrade(double s) {
        if (s >= 90) return "A";
        if (s >= 80) return "B";
        if (s >= 70) return "C";
        if (s >= 60) return "D";
        return "F";
    }

    // ================= UI HELPERS =================

    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    private JButton createButton(String text, Color color, ActionListener a) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.addActionListener(a);
        return b;
    }

    private Border createRoundedBorder(String title) {
        return BorderFactory.createTitledBorder(title);
    }

    // ================= PLACEHOLDER =================

    static class PlaceholderTextField extends JTextField {
        private String placeholder;

        public PlaceholderTextField(int cols) {
            super(cols);
        }

        public void setPlaceholder(String text) {
            this.placeholder = text;
        }

        //message for no data in the input box
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && placeholder != null) {
                g.setColor(Color.GRAY);
                g.drawString(placeholder, 5, 15);
            }
        }
    }

    //the running function
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeSystem().setVisible(true));
    }
}