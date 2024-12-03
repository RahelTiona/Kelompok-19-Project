package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import config.UserDAO;
import model.User;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Register New Account");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("src/assets/image/background.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        mainPanel.setLayout(null);

        // Title REGISTER - centered and higher
        JLabel titleLabel = new JLabel("REGISTER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 30, 400, 50);
        mainPanel.add(titleLabel);

        // Starting position for components
        int startY = 180;
        int spacing = 70;

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setBounds(50, startY, 100, 25);
        mainPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, startY + 25, 300, 35);
        styleTextField(usernameField);
        mainPanel.add(usernameField);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.BLACK);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setBounds(50, startY + spacing, 100, 25);
        mainPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, startY + spacing + 25, 300, 35);
        styleTextField(emailField);
        mainPanel.add(emailField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setBounds(50, startY + spacing * 2, 100, 25);
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, startY + spacing * 2 + 25, 300, 35);
        styleTextField(passwordField);
        mainPanel.add(passwordField);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(Color.BLACK);
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmLabel.setBounds(50, startY + spacing * 3, 150, 25);
        mainPanel.add(confirmLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(50, startY + spacing * 3 + 25, 300, 35);
        styleTextField(confirmPasswordField);
        mainPanel.add(confirmPasswordField);

        // Register Button
        JButton registerButton = createStyledButton("Register", 50, 500, 140, 40);
        registerButton.addActionListener(e -> handleRegistration());
        mainPanel.add(registerButton);

        // Back Button
        JButton backButton = createStyledButton("Back", 210, 500, 140, 40);
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        mainPanel.add(backButton);

        add(mainPanel);
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private JButton createStyledButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(getForeground());
                FontMetrics fm = g.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(getText(), textX, textY);
            }
        };
        
        button.setBounds(x, y, width, height);
        button.setBackground(Color.YELLOW);
        button.setForeground(Color.BLACK);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 150));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.YELLOW);
            }
        });
        
        return button;
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        try {
            // Check if username or email already exists
            if (userDAO.isUsernameTaken(username)) {
                showError("Username already taken");
                return;
            }

            if (userDAO.isEmailTaken(email)) {
                showError("Email already registered");
                return;
            }

            // Create new user
            User newUser = new User(0, username, "", email);
            if (userDAO.registerUser(newUser, password)) {
                showSuccess("Registration successful! Please login.");
                dispose();
                new LoginFrame().setVisible(true);
            } else {
                showError("Registration failed");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // Clear all fields when frame becomes visible
            usernameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }
}