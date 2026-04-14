import javax.swing.*;
import java.awt.event.*;

public class StudentGradeSystem extends JFrame implements ActionListener {

    JLabel lblName, lblMath, lblEnglish, lblScience, lblResult;
    JTextField txtName, txtMath, txtEnglish, txtScience;
    JButton btnCalculate, btnClear;

    public StudentGradeSystem() {

        setTitle("Student Grade Management System");
        setSize(420, 350);
        setLayout(null);

        lblName = new JLabel("Student Name:");
        lblName.setBounds(30, 30, 120, 25);
        add(lblName);

        lblMath = new JLabel("Math (0-100):");
        lblMath.setBounds(30, 70, 120, 25);
        add(lblMath);

        lblEnglish = new JLabel("English (0-100):");
        lblEnglish.setBounds(30, 110, 120, 25);
        add(lblEnglish);

        lblScience = new JLabel("Science (0-100):");
        lblScience.setBounds(30, 150, 120, 25);
        add(lblScience);

        txtName = new JTextField();
        txtName.setBounds(170, 30, 180, 25);
        add(txtName);

        txtMath = new JTextField();
        txtMath.setBounds(170, 70, 180, 25);
        add(txtMath);

        txtEnglish = new JTextField();
        txtEnglish.setBounds(170, 110, 180, 25);
        add(txtEnglish);

        txtScience = new JTextField();
        txtScience.setBounds(170, 150, 180, 25);
        add(txtScience);

        btnCalculate = new JButton("Calculate");
        btnCalculate.setBounds(70, 200, 120, 30);
        add(btnCalculate);

        btnClear = new JButton("Clear");
        btnClear.setBounds(210, 200, 120, 30);
        add(btnClear);

        lblResult = new JLabel("Result:");
        lblResult.setBounds(30, 250, 350, 25);
        add(lblResult);

        btnCalculate.addActionListener(this);
        btnClear.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnCalculate) {
            try {
                String name = txtName.getText();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Enter student name!");
                    return;
                }

                int math = Integer.parseInt(txtMath.getText());
                int english = Integer.parseInt(txtEnglish.getText());
                int science = Integer.parseInt(txtScience.getText());

                // Validation
                if (math < 0 || math > 100 || english < 0 || english > 100 || science < 0 || science > 100) {
                    JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100!");
                    return;
                }

                int total = math + english + science;
                double average = total / 3.0;

                String grade;

                if (average >= 90)
                    grade = "A+";
                else if (average >= 80)
                    grade = "A";
                else if (average >= 70)
                    grade = "B";
                else if (average >= 60)
                    grade = "C";
                else
                    grade = "FAIL";

                lblResult.setText("Result: " + name +
                        " | Total: " + total +
                        " | Avg: " + String.format("%.2f", average) +
                        " | Grade: " + grade);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers!");
            }
        }

        if (e.getSource() == btnClear) {
            txtName.setText("");
            txtMath.setText("");
            txtEnglish.setText("");
            txtScience.setText("");
            lblResult.setText("Result:");
        }
    }

    public static void main(String[] args) {
        new StudentGradeSystem();
    }
}
