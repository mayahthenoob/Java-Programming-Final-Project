
import javax.swing.*;
import java.awt.event.*;

public class StudentGradeSystem extends JFrame implements ActionListener {

    // GUI Components
    JLabel lblName, lblMath, lblEnglish, lblScience, lblResult;
    JTextField txtName, txtMath, txtEnglish, txtScience;
    JButton btnCalculate;

    public StudentGradeSystem() {

        // Frame settings
        setTitle("Student Grade Management System");
        setSize(400, 350);
        setLayout(null);

        // Labels
        lblName = new JLabel("Student Name:");
        lblName.setBounds(30, 30, 120, 25);
        add(lblName);

        lblMath = new JLabel("Math:");
        lblMath.setBounds(30, 70, 120, 25);
        add(lblMath);

        lblEnglish = new JLabel("English:");
        lblEnglish.setBounds(30, 110, 120, 25);
        add(lblEnglish);

        lblScience = new JLabel("Science:");
        lblScience.setBounds(30, 150, 120, 25);
        add(lblScience);

        lblResult = new JLabel("Result:");
        lblResult.setBounds(30, 230, 300, 25);
        add(lblResult);

        // Text Fields
        txtName = new JTextField();
        txtName.setBounds(150, 30, 150, 25);
        add(txtName);

        txtMath = new JTextField();
        txtMath.setBounds(150, 70, 150, 25);
        add(txtMath);

        txtEnglish = new JTextField();
        txtEnglish.setBounds(150, 110, 150, 25);
        add(txtEnglish);

        txtScience = new JTextField();
        txtScience.setBounds(150, 150, 150, 25);
        add(txtScience);

        // Button
        btnCalculate = new JButton("Calculate");
        btnCalculate.setBounds(120, 190, 120, 30);
        add(btnCalculate);

        btnCalculate.addActionListener(this);

        setVisible(true);
    }

    // Action when button is clicked
    public void actionPerformed(ActionEvent e) {
        try {
            String name = txtName.getText();
            int math = Integer.parseInt(txtMath.getText());
            int english = Integer.parseInt(txtEnglish.getText());
            int science = Integer.parseInt(txtScience.getText());

            int total = math + english + science;
            double average = total / 3.0;

            String grade;

            if (average >= 80)
                grade = "A";
            else if (average >= 60)
                grade = "B";
            else if (average >= 50)
                grade = "C";
            else
                grade = "FAIL";

            lblResult.setText("Result: " + name +
                    " | Total: " + total +
                    " | Avg: " + average +
                    " | Grade: " + grade);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        }
    }

    public static void main(String[] args) {
        new StudentGradeSystem();
    }
}
