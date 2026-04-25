import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GradeVaultUI extends JFrame {

    private final Color BACKGROUND = new Color(18, 18, 18);
    private final Color CARD_BG = new Color(30, 30, 30);
    private final Color ACCENT_GREEN = new Color(0, 200, 150);
    private final Color TEXT_GRAY = new Color(180, 180, 180);

    private Map<String, List<School>> countryData = new TreeMap<>(); // TreeMap keeps countries alphabetized

    private JTextField subjectField, scoreField;
    private JComboBox<String> countryBox;
    private JComboBox<School> schoolBox;
    private JTextField nameField, idField;
    private JLabel avgLabel, gradeLabel, gpaLabel, statusLabel;
    private DefaultTableModel tableModel;
    private JTable table;

    public GradeVaultUI() {
        initData();
        setupFrame();

        add(createHeader(), BorderLayout.NORTH);

        JScrollPane sidebarScroll = new JScrollPane(createSidebar());
        sidebarScroll.setBorder(null);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(sidebarScroll, BorderLayout.WEST);

        add(createMainTableArea(), BorderLayout.CENTER);
    }

    private void initData() {

        GradingScale caribScale = new GradingScale(Arrays.asList(
                new GradeThreshold("A", 80, 4.0),
                new GradeThreshold("B+", 75, 3.5),
                new GradeThreshold("B", 65, 3.0),
                new GradeThreshold("C+", 60, 2.5),
                new GradeThreshold("C", 50, 2.0),
                new GradeThreshold("F", 0, 0.0)
        ));

        // US Standard Style
        GradingScale usScale = new GradingScale(Arrays.asList(
                new GradeThreshold("A", 90, 4.0),
                new GradeThreshold("B", 80, 3.0),
                new GradeThreshold("C", 70, 2.0),
                new GradeThreshold("D", 60, 1.0),
                new GradeThreshold("F", 0, 0.0)
        ));

        // UK Honours Style
        GradingScale ukScale = new GradingScale(Arrays.asList(
                new GradeThreshold("1st", 70, 4.0),
                new GradeThreshold("2:1", 60, 3.3),
                new GradeThreshold("2:2", 50, 2.7),
                new GradeThreshold("3rd", 40, 2.0),
                new GradeThreshold("Fail", 0, 0.0)
        ));

        // --- 2. Map Schools to Countries ---

        // GRENADA
        countryData.put("Grenada", Arrays.asList(
                new School("T.A.M.C.C", caribScale, 50),
                new School("St. George's University", usScale, 65)
        ));

        // USA
        countryData.put("United States", Arrays.asList(
                new School("Harvard University", usScale, 60),
                new School("Stanford University", usScale, 60),
                new School("MIT", usScale, 60)
        ));

        // UNITED KINGDOM
        countryData.put("United Kingdom", Arrays.asList(
                new School("University of Oxford", ukScale, 40),
                new School("Cambridge", ukScale, 40)
        ));

        // CANADA
        countryData.put("Canada", Arrays.asList(
                new School("University of Toronto", usScale, 50),
                new School("UBC", usScale, 50)
        ));
    }

    private void setupFrame() {
        setTitle("Student Grading System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 800);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logo = new JLabel("GRADEVAULT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton deleteBtn = new JButton("Delete Selected");
        JButton clearAllBtn = new JButton("Clear All");

        deleteBtn.addActionListener(e -> deleteSelectedRow());
        clearAllBtn.addActionListener(e -> clearForm());

        actions.add(deleteBtn);
        actions.add(clearAllBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BACKGROUND);
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBorder(new EmptyBorder(0, 20, 20, 10));

        // Student Info
        JPanel studentForm = new JPanel(new GridLayout(0, 1, 5, 6));
        studentForm.setBackground(CARD_BG);
        studentForm.setBorder(new EmptyBorder(15, 15, 15, 15));
        studentForm.setMaximumSize(new Dimension(300, 280));

        countryBox = new JComboBox<>(countryData.keySet().toArray(new String[0]));
        schoolBox = new JComboBox<>();
        nameField = new JTextField();
        idField = new JTextField();

        countryBox.addActionListener(e -> updateSchoolList());
        updateSchoolList(); // Initial trigger

        studentForm.add(createLabel("COUNTRY"));
        studentForm.add(countryBox);
        studentForm.add(createLabel("SCHOOL"));
        studentForm.add(schoolBox);
        studentForm.add(createLabel("STUDENT NAME"));
        studentForm.add(nameField);
        studentForm.add(createLabel("STUDENT ID"));
        studentForm.add(idField);

        // Subject Section
        JPanel subjectSection = new JPanel(new GridLayout(0, 1, 5, 6));
        subjectSection.setBackground(CARD_BG);
        subjectSection.setBorder(new EmptyBorder(15, 15, 15, 15));
        subjectSection.setMaximumSize(new Dimension(300, 150));

        subjectField = new JTextField();
        scoreField = new JTextField();
        scoreField.addCaretListener(e -> calculateRealTime());

        subjectSection.add(createLabel("SUBJECT"));
        subjectSection.add(subjectField);
        subjectSection.add(createLabel("SCORE (0-100)"));
        subjectSection.add(scoreField);

        // Stats Section
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        statsGrid.setBackground(BACKGROUND);
        statsGrid.setMaximumSize(new Dimension(300, 120));
        avgLabel = createStatBox(statsGrid, "FINAL SCORE");
        gradeLabel = createStatBox(statsGrid, "GRADE");
        gpaLabel = createStatBox(statsGrid, "GPA");
        statusLabel = createStatBox(statsGrid, "STATUS");

        // Submit Button
        JButton submitBtn = new JButton("SUBMIT RECORD");
        submitBtn.setBackground(ACCENT_GREEN);
        submitBtn.setForeground(Color.BLACK);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(300, 45));
        submitBtn.addActionListener(e -> submitRecord());

        sidebar.add(studentForm);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(subjectSection);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(statsGrid);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(submitBtn);

        return sidebar;
    }

    private JPanel createMainTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(0, 0, 20, 20));

        String[] columns = {"NAME", "ID", "COUNTRY", "SCHOOL", "SUBJECT", "SCORE", "GRADE", "GPA", "STATUS"};
        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setBackground(CARD_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(45, 45, 45));
        table.setSelectionBackground(new Color(50, 50, 50));
        table.getTableHeader().setBackground(BACKGROUND);
        table.getTableHeader().setForeground(TEXT_GRAY);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void updateSchoolList() {
        schoolBox.removeAllItems();
        String country = (String) countryBox.getSelectedItem();
        if (country != null) {
            for (School s : countryData.get(country)) {
                schoolBox.addItem(s);
            }
        }
    }

    private void calculateRealTime() {
        try {
            String scoreText = scoreField.getText();
            if (!scoreText.isEmpty()) {
                double score = Double.parseDouble(scoreText);
                School selectedSchool = (School) schoolBox.getSelectedItem();

                if (selectedSchool != null) {
                    GradeThreshold gt = selectedSchool.scale.getGrade(score);

                    avgLabel.setText(String.format("%.1f", score));
                    gradeLabel.setText(gt.grade);
                    gpaLabel.setText(String.format("%.2f", gt.gpa));

                    // Dynamic Pass/Fail based on school setting
                    statusLabel.setText(score >= selectedSchool.passMark ? "PASS" : "FAIL");
                    statusLabel.setForeground(score >= selectedSchool.passMark ? ACCENT_GREEN : Color.RED);
                }
            }
        } catch (NumberFormatException ignored) {
            resetStats();
        }
    }

    private void resetStats() {
        avgLabel.setText("--");
        gradeLabel.setText("--");
        gpaLabel.setText("--");
        statusLabel.setText("--");
        statusLabel.setForeground(Color.WHITE);
    }

    private void submitRecord() {
        if (nameField.getText().isEmpty() || subjectField.getText().isEmpty() || scoreField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name, Subject, and Score.");
            return;
        }

        tableModel.addRow(new Object[]{
                nameField.getText().toUpperCase(),
                idField.getText(),
                countryBox.getSelectedItem(),
                schoolBox.getSelectedItem(),
                subjectField.getText().toUpperCase(),
                avgLabel.getText(),
                gradeLabel.getText(),
                gpaLabel.getText(),
                statusLabel.getText()
        });

        subjectField.setText("");
        scoreField.setText("");
        resetStats();
    }

    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Select a row in the table to delete.");
        }
    }

    private void clearForm() {
        nameField.setText("");
        idField.setText("");
        subjectField.setText("");
        scoreField.setText("");
        resetStats();
        tableModel.setRowCount(0);
    }

    private JLabel createStatBox(JPanel parent, String title) {
        JPanel box = new JPanel(new GridLayout(2, 1));
        box.setBackground(CARD_BG);
        box.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));

        JLabel l = new JLabel(title, SwingConstants.CENTER);
        l.setForeground(TEXT_GRAY);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel v = new JLabel("--", SwingConstants.CENTER);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("SansSerif", Font.BOLD, 16));

        box.add(l);
        box.add(v);
        parent.add(box);

        return v;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_GRAY);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        return l;
    }

    // --- Data Classes ---
    class School {
        String name;
        GradingScale scale;
        double passMark;

        School(String n, GradingScale s, double pm) {
            name = n;
            scale = s;
            passMark = pm;
        }

        @Override
        public String toString() { return name; }
    }

    class GradingScale {
        List<GradeThreshold> thresholds;
        GradingScale(List<GradeThreshold> t) {
            // Sort thresholds descending to ensure correct matching logic
            this.thresholds = new ArrayList<>(t);
            this.thresholds.sort((a, b) -> Double.compare(b.minScore, a.minScore));
        }

        GradeThreshold getGrade(double score) {
            return thresholds.stream()
                    .filter(t -> score >= t.minScore)
                    .findFirst()
                    .orElse(new GradeThreshold("F", 0, 0));
        }
    }

    class GradeThreshold {
        String grade;
        double minScore;
        double gpa;
        GradeThreshold(String g, double m, double gp) {
            grade = g; minScore = m; gpa = gp;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new GradeVaultUI().setVisible(true));
    }
}