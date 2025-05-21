package com.vti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;



public class URLAutomationAppAI extends JFrame {
    private JTextArea urlTextArea;
    private JButton importButton;
    private JButton startButton;
    private JTextField usernameField;
    private JTextField passwordField;
    private JCheckBox saveCredentialsCheckbox;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JComboBox<String> themeComboBox;
    private Color primaryColor = new Color(25, 118, 210);
    private Color accentColor = new Color(66, 165, 245);
    private Color backgroundColor = new Color(245, 245, 245);
    private Color textColor = new Color(33, 33, 33);
    private Color successColor = new Color(76, 175, 80);
    private Color errorColor = new Color(244, 67, 54);
    private List<String> urls = new ArrayList();
    private Font mainFont = new Font("Segoe UI", 0, 14);
    private Font headerFont = new Font("Segoe UI", 1, 22);
    private Font buttonFont = new Font("Segoe UI", 1, 14);
    private JSpinner threadCountSpinner;

    public URLAutomationAppAI() {
        this.setTitle("URL Automation Tool");
        this.setSize(800, 650);
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout(15, 15));
        this.getContentPane().setBackground(this.backgroundColor);
        this.setupUIComponents();
        this.setupChromeDriver();
        this.setLocationRelativeTo((Component)null);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                URLAutomationAppAI.this.cleanupResources();
            }
        });
    }

    private void setupUIComponents() {
        JPanel headerPanel = this.createHeaderPanel();
        JPanel contentPanel = this.createContentPanel();
        JPanel buttonPanel = this.createButtonPanel();
        JPanel statusPanel = this.createStatusPanel();
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(this.backgroundColor);
        mainPanel.add(headerPanel, "North");
        mainPanel.add(contentPanel, "Center");
        mainPanel.add(buttonPanel, "South");
        this.add(mainPanel, "Center");
        this.add(statusPanel, "South");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(this.backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel titleLabel = new JLabel("URL Automation Tool", 2);
        titleLabel.setFont(this.headerFont);
        titleLabel.setForeground(this.primaryColor);
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(2));
        rightHeaderPanel.setBackground(this.backgroundColor);
        String[] themes = new String[]{"Modern Blue", "Dark Mode", "Light Mode"};
        this.themeComboBox = new JComboBox(themes);
        this.themeComboBox.setFont(this.mainFont);
        this.themeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                URLAutomationAppAI.this.changeTheme((String)URLAutomationAppAI.this.themeComboBox.getSelectedItem());
            }
        });
        rightHeaderPanel.add(new JLabel("Theme:"));
        rightHeaderPanel.add(this.themeComboBox);
        headerPanel.add(titleLabel, "West");
        headerPanel.add(rightHeaderPanel, "East");
        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.setBackground(this.backgroundColor);
        JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
        urlPanel.setBackground(this.backgroundColor);
        JLabel urlLabel = new JLabel("Enter URLs (one per line):");
        urlLabel.setFont(this.mainFont);
        urlLabel.setForeground(this.textColor);
        this.urlTextArea = new JTextArea(12, 40);
        this.urlTextArea.setFont(new Font("Consolas", 0, 14));
        this.urlTextArea.setLineWrap(true);
        this.urlTextArea.setWrapStyleWord(true);
        this.urlTextArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(this.accentColor, 1), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JScrollPane scrollPane = new JScrollPane(this.urlTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        urlPanel.add(urlLabel, "North");
        urlPanel.add(scrollPane, "Center");
        contentPanel.add(urlPanel, "Center");
        contentPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JPanel credentialsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        credentialsPanel.setBackground(this.backgroundColor);
        credentialsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(this.accentColor, 1), "Login Credentials", 1, 2, this.mainFont, this.textColor));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(this.mainFont);
        usernameLabel.setForeground(this.textColor);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(this.mainFont);
        passwordLabel.setForeground(this.textColor);
        this.usernameField = new JTextField(20);
        this.usernameField.setFont(this.mainFont);
        this.usernameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(this.accentColor, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.passwordField = new JPasswordField(20);
        this.passwordField.setFont(this.mainFont);
        this.passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(this.accentColor, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        credentialsPanel.add(usernameLabel);
        credentialsPanel.add(this.usernameField);
        credentialsPanel.add(passwordLabel);
        credentialsPanel.add(this.passwordField);
        JPanel loginPanel = new JPanel(new BorderLayout(10, 10));
        loginPanel.setBackground(this.backgroundColor);
        loginPanel.add(credentialsPanel, "Center");
        contentPanel.add(loginPanel, "North");

        // Add thread count configuration
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.setBackground(this.backgroundColor);
        configPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(this.accentColor, 1), "Configuration", 1, 2, this.mainFont, this.textColor));
        JLabel threadCountLabel = new JLabel("Thread Count:");
        threadCountLabel.setFont(this.mainFont);
        threadCountLabel.setForeground(this.textColor);
        this.threadCountSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 20, 1));
        this.threadCountSpinner.setFont(this.mainFont);
        configPanel.add(threadCountLabel);
        configPanel.add(this.threadCountSpinner);
        contentPanel.add(configPanel, "South");

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(1, 15, 0));
        buttonPanel.setBackground(this.backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        this.importButton = this.createStyledButton("Import URLs from File", "/icons/import.png");
        this.startButton = this.createStyledButton("Start Automation", "/icons/play.png");
        JButton clearButton = this.createStyledButton("Clear All", "/icons/clear.png");
        clearButton.setBackground(new Color(250, 250, 250));
        clearButton.setForeground(this.textColor);
        buttonPanel.add(this.importButton);
        buttonPanel.add(this.startButton);
        buttonPanel.add(clearButton);
        this.importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                URLAutomationAppAI.this.importURLsFromFile();
            }
        });
        this.startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                URLAutomationAppAI.this.startAutomation();
            }
        });
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                URLAutomationAppAI.this.urlTextArea.setText("");
                URLAutomationAppAI.this.statusLabel.setText("Status: Ready");
                URLAutomationAppAI.this.progressBar.setValue(0);
                URLAutomationAppAI.this.progressLabel.setText("0/0");
            }
        });
        return buttonPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 5));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        this.statusLabel = new JLabel("Status: Ready", 2);
        this.statusLabel.setFont(this.mainFont);
        this.statusLabel.setForeground(this.textColor);
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setStringPainted(false);
        this.progressBar.setPreferredSize(new Dimension(150, 20));
        this.progressBar.setForeground(this.primaryColor);
        this.progressBar.setBackground(new Color(220, 220, 220));
        this.progressBar.setBorder(BorderFactory.createEmptyBorder());
        this.progressLabel = new JLabel("0/0", 0);
        this.progressLabel.setFont(this.mainFont);
        this.progressLabel.setForeground(this.textColor);
        this.progressLabel.setPreferredSize(new Dimension(60, 20));
        JPanel rightStatusPanel = new JPanel(new FlowLayout(2, 10, 0));
        rightStatusPanel.setBackground(new Color(240, 240, 240));
        rightStatusPanel.add(this.progressBar);
        rightStatusPanel.add(this.progressLabel);
        statusPanel.add(this.statusLabel, "West");
        statusPanel.add(rightStatusPanel, "East");
        return statusPanel;
    }

    private JButton createStyledButton(String text, String iconPath) {
        final JButton button = new JButton(text);
        button.setFont(this.buttonFont);
        button.setForeground(Color.WHITE);
        button.setBackground(this.primaryColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(12));
        button.setPreferredSize(new Dimension(200, 40));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(URLAutomationAppAI.this.accentColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(URLAutomationAppAI.this.primaryColor);
            }
        });
        return button;
    }

    private void changeTheme(String themeName) {
        switch (themeName) {
            case "Dark Mode":
                this.primaryColor = new Color(66, 66, 66);
                this.accentColor = new Color(100, 100, 100);
                this.backgroundColor = new Color(33, 33, 33);
                this.textColor = new Color(245, 245, 245);
                break;
            case "Light Mode":
                this.primaryColor = new Color(96, 125, 139);
                this.accentColor = new Color(144, 164, 174);
                this.backgroundColor = new Color(250, 250, 250);
                this.textColor = new Color(33, 33, 33);
                break;
            default:
                this.primaryColor = new Color(25, 118, 210);
                this.accentColor = new Color(66, 165, 245);
                this.backgroundColor = new Color(245, 245, 245);
                this.textColor = new Color(33, 33, 33);
        }

        this.getContentPane().setBackground(this.backgroundColor);
        this.updateComponentColors(this.getContentPane());
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentColors(Container container) {
        Component[] var2 = container.getComponents();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component c = var2[var4];
            if (c instanceof JPanel) {
                c.setBackground(this.backgroundColor);
                this.updateComponentColors((Container)c);
            } else if (c instanceof JButton) {
                JButton button = (JButton)c;
                if (!button.getText().equals("Clear All")) {
                    button.setBackground(this.primaryColor);
                }
            } else if (c instanceof JLabel) {
                c.setForeground(this.textColor);
            } else if (c instanceof Container) {
                this.updateComponentColors((Container)c);
            }
        }

    }

    private void importURLsFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == 0) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                StringBuilder content = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                reader.close();
                this.urlTextArea.setText(content.toString());
                this.statusLabel.setText("Status: URLs imported successfully");
            } catch (Exception var7) {
                Exception ex = var7;
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
                this.statusLabel.setText("Status: Error importing URLs");
            }
        }

    }

    /*private void setupChromeDriver() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            String os = System.getProperty("os.name").toLowerCase();
            String chromeDriverResource;
            String chromeDriverName;
            
            // Xác định ChromeDriver phù hợp với hệ điều hành
            if (os.contains("win")) {
                chromeDriverResource = "/drivers/chromedriver.exe";
                chromeDriverName = "chromedriver.exe";
            } else if (os.contains("mac")) {
                chromeDriverResource = "/drivers/chromedriver";
                chromeDriverName = "chromedriver";
            } else {
                chromeDriverResource = "/drivers/chromedriver";
                chromeDriverName = "chromedriver";
            }

            File chromeDriverFile = new File(tempDir + File.separator + chromeDriverName);
            
            // Kiểm tra nếu ChromeDriver đã tồn tại
            if (chromeDriverFile.exists()) {
                System.out.println("ChromeDriver already exists at: " + chromeDriverFile.getAbsolutePath());
            } else {
                // Giải nén ChromeDriver từ resources
                try (InputStream in = URLAutomationAppAI.class.getResourceAsStream(chromeDriverResource)) {
                    if (in == null) {
                        // Nếu không tìm thấy resource, yêu cầu người dùng chọn file ChromeDriver
                        int choice = JOptionPane.showConfirmDialog(
                            this,
                            "ChromeDriver không được tìm thấy trong ứng dụng. Bạn có muốn chọn file ChromeDriver thủ công không?",
                            "ChromeDriver Not Found",
                            JOptionPane.YES_NO_OPTION);
                            
                        if (choice == JOptionPane.YES_OPTION) {
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle("Chọn ChromeDriver");
                            
                            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = fileChooser.getSelectedFile();
                                Files.copy(selectedFile.toPath(), chromeDriverFile.toPath(), 
                                    StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("ChromeDriver copied from: " + selectedFile.getAbsolutePath());
                            } else {
                                throw new IOException("ChromeDriver not selected by user");
                            }
                        } else {
                            throw new IOException("ChromeDriver resource not found: " + chromeDriverResource);
                        }
                    } else {
                        Files.copy(in, chromeDriverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("ChromeDriver extracted to: " + chromeDriverFile.getAbsolutePath());
                    }
                }
            }

            // Đặt quyền thực thi cho ChromeDriver trên macOS và Linux
            if (!os.contains("win")) {
                chromeDriverFile.setExecutable(true);
            }

            // Cấu hình đường dẫn ChromeDriver
            System.setProperty("webdriver.chrome.driver", chromeDriverFile.getAbsolutePath());
            System.out.println("ChromeDriver set up at: " + chromeDriverFile.getAbsolutePath());
            statusLabel.setText("Status: ChromeDriver ready");
            
        } catch (IOException e) {
            System.err.println("Failed to set up ChromeDriver from resources: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to set up ChromeDriver: " + e.getMessage() + 
                "\n\nPlease download ChromeDriver manually from https://chromedriver.chromium.org/downloads " +
                "and place it in the same directory as this application.", 
                "ChromeDriver Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/

    /*private void setupChromeDriver() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            String os = System.getProperty("os.name").toLowerCase();
            String chromeDriverResource;
            String chromeDriverName;

            if (os.contains("win")) {
                chromeDriverResource = "/drivers/chromedriver.exe";
                chromeDriverName = "chromedriver.exe";
            } else if (os.contains("mac")) {
                chromeDriverResource = "/drivers/chromedriver";
                chromeDriverName = "chromedriver";
            } else {
                chromeDriverResource = "/drivers/chromedriver";
                chromeDriverName = "chromedriver";
            }

            File chromeDriverFile = new File(tempDir + File.separator + chromeDriverName);

            // Luôn giải nén ChromeDriver từ resources, ghi đè nếu file đã tồn tại
            try (InputStream in = URLAutomationAppAI.class.getResourceAsStream(chromeDriverResource)) {
                if (in == null) {
                    // Nếu không tìm thấy resource, yêu cầu người dùng chọn file ChromeDriver
                    int choice = JOptionPane.showConfirmDialog(
                            this,
                            "ChromeDriver không được tìm thấy trong ứng dụng. Bạn có muốn chọn file ChromeDriver thủ công không?",
                            "ChromeDriver Not Found",
                            JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Chọn ChromeDriver");

                        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            Files.copy(selectedFile.toPath(), chromeDriverFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("ChromeDriver copied from: " + selectedFile.getAbsolutePath());
                        } else {
                            throw new IOException("ChromeDriver not selected by user");
                        }
                    } else {
                        throw new IOException("ChromeDriver resource not found: " + chromeDriverResource);
                    }
                } else {
                    // Sử dụng StandardCopyOption.REPLACE_EXISTING để ghi đè file cũ nếu có
                    Files.copy(in, chromeDriverFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("ChromeDriver extracted/updated to: " + chromeDriverFile.getAbsolutePath());
                }
            }

            if (!os.contains("win")) {
                chromeDriverFile.setExecutable(true);
            }

            System.setProperty("webdriver.chrome.driver", chromeDriverFile.getAbsolutePath());
            System.out.println("ChromeDriver set up at: " + chromeDriverFile.getAbsolutePath());
            statusLabel.setText("Status: ChromeDriver ready");

        } catch (IOException e) {
            System.err.println("Failed to set up ChromeDriver: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to set up ChromeDriver: " + e.getMessage() +
                            "\n\nPlease download ChromeDriver manually from https://chromedriver.chromium.org/downloads " +
                            "and place it in the same directory as this application.",
                    "ChromeDriver Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/

    private void setupChromeDriver() {
        try {
            System.out.println("Setting up ChromeDriver using auto-detection...");
            String tempDir = System.getProperty("java.io.tmpdir");
            String os = System.getProperty("os.name").toLowerCase();
            
            // Cập nhật trạng thái
            if (statusLabel != null) {
                statusLabel.setText("Status: Đang phát hiện phiên bản Chrome...");
            }
            
            // Phát hiện phiên bản Chrome đã cài đặt
            String chromeVersion = detectChromeVersion();
            System.out.println("Detected Chrome version: " + chromeVersion);
            
            if (statusLabel != null) {
                statusLabel.setText("Status: Đang tải ChromeDriver cho Chrome " + chromeVersion + "...");
            }
            
            // Xác định URL tải ChromeDriver dựa trên phiên bản Chrome
            String driverUrl = getCompatibleChromeDriverUrl(chromeVersion);
            System.out.println("ChromeDriver download URL: " + driverUrl);
            
            // Tải và giải nén ChromeDriver
            String driverPath = downloadAndExtractChromeDriver(driverUrl, tempDir, os);
            
            // Thiết lập đường dẫn
            System.setProperty("webdriver.chrome.driver", driverPath);
            System.out.println("ChromeDriver set up at: " + driverPath);
            
            if (statusLabel != null) {
                statusLabel.setText("Status: ChromeDriver ready");
            }
        } catch (Exception e) {
            handleChromeDriverSetupError(e);
        }
    }
    
    private String detectChromeVersion() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            
            if (os.contains("win")) {
                // Windows: Sử dụng registry để lấy phiên bản Chrome
                process = Runtime.getRuntime().exec(
                    "reg query \"HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon\" /v version");
            } else if (os.contains("mac")) {
                // macOS
                process = Runtime.getRuntime().exec(
                    "/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome --version");
            } else {
                // Linux
                process = Runtime.getRuntime().exec("google-chrome --version");
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String version = "";
            
            while ((line = reader.readLine()) != null) {
                if (os.contains("win") && line.contains("version")) {
                    // Trích xuất phiên bản từ registry output (REG_SZ    107.0.5304.107)
                    version = line.trim().split("\\s+")[2];
                } else if (line.toLowerCase().contains("chrome")) {
                    // Trích xuất phiên bản từ "Google Chrome XX.X.XXXX.XX"
                    version = line.replaceAll(".*Chrome\\s+", "").replaceAll("\\s.*", "");
                }
            }
            
            // Chỉ lấy phiên bản chính (major version)
            if (version.contains(".")) {
                return version.split("\\.")[0];
            }
            return version;
        } catch (Exception e) {
            System.err.println("Error detecting Chrome version: " + e.getMessage());
            e.printStackTrace();
            // Phiên bản mặc định nếu không thể xác định
            return "114"; 
        }
    }
    
    private String getCompatibleChromeDriverUrl(String chromeVersion) {
        // Bảng ánh xạ phiên bản Chrome với phiên bản ChromeDriver đã biết cho các phiên bản mới nhất
        // Cập nhật bảng này khi có phiên bản Chrome mới
        java.util.Map<String, String> knownVersions = new java.util.HashMap<>();
        knownVersions.put("136", "136.0.7103.9400");
        knownVersions.put("135", "135.0.7049.9500");
        knownVersions.put("134", "134.0.6998.16500");
        knownVersions.put("133", "133.0.6943.14100");
        knownVersions.put("132", "132.0.6868.10300");
        knownVersions.put("131", "131.0.6844.16000");
        knownVersions.put("130", "130.0.6736.14800");
        knownVersions.put("129", "129.0.6707.7100");
        knownVersions.put("128", "128.0.6631.10400");
        knownVersions.put("127", "127.0.6649.15100");
        knownVersions.put("126", "126.0.6478.14400");
        knownVersions.put("125", "125.0.6422.11300");
        knownVersions.put("114", "114.0.5735.90");
        
        String os = System.getProperty("os.name").toLowerCase();
        String platform = os.contains("win") ? "win32" : os.contains("mac") ? "mac64" : "linux64";
        
        // Trường hợp 1: Kiểm tra xem có phiên bản đã biết hay không
        if (knownVersions.containsKey(chromeVersion)) {
            String driverVersion = knownVersions.get(chromeVersion);
            System.out.println("Using known ChromeDriver version " + driverVersion + " for Chrome " + chromeVersion);
            return "https://chromedriver.storage.googleapis.com/" + driverVersion + "/chromedriver_" + platform + ".zip";
        }
        
        // Trường hợp 2: Thử API thông thường của Google
        try {
            URL url = new URL("https://chromedriver.storage.googleapis.com/LATEST_RELEASE_" + chromeVersion);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String driverVersion = reader.readLine();
            reader.close();
            
            System.out.println("Found ChromeDriver version " + driverVersion + " via Google API");
            return "https://chromedriver.storage.googleapis.com/" + driverVersion + "/chromedriver_" + platform + ".zip";
        } catch (Exception e) {
            System.err.println("Error getting ChromeDriver URL from Google API: " + e.getMessage());
            
            // Trường hợp 3: Thử lấy phiên bản mới nhất
            try {
                URL url = new URL("https://chromedriver.storage.googleapis.com/LATEST_RELEASE");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String latestVersion = reader.readLine();
                reader.close();
                
                System.out.println("Using latest available ChromeDriver version: " + latestVersion);
                return "https://chromedriver.storage.googleapis.com/" + latestVersion + "/chromedriver_" + platform + ".zip";
            } catch (Exception e2) {
                System.err.println("Error getting latest ChromeDriver version: " + e2.getMessage());
                
                // Trường hợp 4: Dùng phiên bản đã biết mới nhất
                String newestKnownVersion = knownVersions.get("136"); // Lấy phiên bản mới nhất từ bảng
                System.out.println("Falling back to newest known ChromeDriver version: " + newestKnownVersion);
                return "https://chromedriver.storage.googleapis.com/" + newestKnownVersion + "/chromedriver_" + platform + ".zip";
            }
        }
    }
    
    private String downloadAndExtractChromeDriver(String driverUrl, String tempDir, String os) throws Exception {
        String driverZip = tempDir + File.separator + "chromedriver_" + System.currentTimeMillis() + ".zip";
        String driverExe = tempDir + File.separator + (os.contains("win") ? "chromedriver.exe" : "chromedriver");
        
        // Tải file ChromeDriver.zip
        URL url = new URL(driverUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(driverZip)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        
        // Giải nén file zip
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(driverZip))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory() && entry.getName().contains("chromedriver")) {
                    try (FileOutputStream out = new FileOutputStream(driverExe)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        
        // Đặt quyền thực thi cho file trên macOS/Linux
        if (!os.contains("win")) {
            File driverFile = new File(driverExe);
            if (!driverFile.setExecutable(true)) {
                System.err.println("Warning: Could not set executable permission for ChromeDriver");
            }
        }
        
        // Xóa file zip tạm thời
        new File(driverZip).delete();
        
        return driverExe;
    }
    
    private void handleChromeDriverSetupError(Exception e) {
        System.err.println("Failed to set up ChromeDriver: " + e.getMessage());
        e.printStackTrace();
        
        if (statusLabel != null) {
            statusLabel.setText("Status: Lỗi cài đặt ChromeDriver");
        }
        
        // Hiển thị tùy chọn cho người dùng tải và chọn ChromeDriver thủ công
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Không thể tự động cài đặt ChromeDriver. Nguyên nhân có thể là:\n" +
            "1. Không có kết nối internet\n" +
            "2. Google Chrome chưa được cài đặt\n" +
            "3. Phiên bản Chrome quá mới chưa có ChromeDriver tương thích\n\n" +
            "Bạn có muốn tải ChromeDriver thủ công không?",
            "ChromeDriver Setup Error",
            JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Mở trang web ChromeDriver để người dùng tải xuống
                Desktop.getDesktop().browse(new URI("https://chromedriver.chromium.org/downloads"));
                
                JOptionPane.showMessageDialog(this,
                    "1. Tải ChromeDriver phù hợp với phiên bản Chrome của bạn.\n" +
                    "2. Giải nén file zip để có được file chromedriver.exe (Windows) hoặc chromedriver (Mac/Linux).\n" +
                    "3. Chọn file này trong hộp thoại tiếp theo.",
                    "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
                
                // Hiển thị FileChooser để người dùng chọn ChromeDriver đã tải
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn ChromeDriver đã tải");
                
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String driverPath = selectedFile.getAbsolutePath();
                    
                    // Đặt quyền thực thi nếu cần
                    if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                        selectedFile.setExecutable(true);
                    }
                    
                    System.setProperty("webdriver.chrome.driver", driverPath);
                    System.out.println("ChromeDriver manually set to: " + driverPath);
                    
                    if (statusLabel != null) {
                        statusLabel.setText("Status: ChromeDriver đã được thiết lập thủ công");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Không thể mở trình duyệt hoặc thiết lập ChromeDriver: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void startAutomation() {
        String urlContent = this.urlTextArea.getText().trim();
        if (urlContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one URL");
        } else {
            this.urls.clear();
            String[] urlLines = urlContent.split("\\n");
            String[] var3 = urlLines;
            int var4 = urlLines.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String url = var3[var5];
                if (!url.trim().isEmpty()) {
                    this.urls.add(url.trim());
                }
            }

            if (this.urls.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No valid URLs found");
            } else {
                try {
                    this.statusLabel.setText("Status: Setting up WebDriver...");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments(new String[]{"--no-sandbox"});
                    options.addArguments(new String[]{"--disable-dev-shm-usage"});
                    options.addArguments(new String[]{"--remote-allow-origins=*"});
                    options.addArguments(new String[]{"--disable-extensions"});
                    options.addArguments(new String[]{"--disable-popup-blocking"});
                    options.addArguments(new String[]{"--start-maximized"});
                    this.statusLabel.setText("Status: Initializing Chrome...");

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            URLAutomationAppAI.this.progressLabel.setText("0/" + URLAutomationAppAI.this.urls.size());
                        }
                    });
                    (new Thread(new Runnable() {
                        public void run() {
                            try {
                                URLAutomationAppAI.this.processURLs();
                            } catch (Exception var5) {
                                final Exception e = var5;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        JOptionPane.showMessageDialog(URLAutomationAppAI.this, "Error during automation: " + e.getMessage());
                                        URLAutomationAppAI.this.statusLabel.setText("Status: Error during automation");
                                    }
                                });
                            }
                        }
                    })).start();
                    this.statusLabel.setText("Status: Automation started");
                } catch (Exception var8) {
                    JOptionPane.showMessageDialog(this, "Error initializing WebDriver: " + var8.getMessage() + "\n\nPlease ensure Chrome is installed and your system has internet access.");
                    this.statusLabel.setText("Status: Error initializing WebDriver");
                }
            }
        }
    }

    private void processURLs() {
        File outputFile = new File("danh_sach_url_da_dung.txt");
        int numThreads = (int) threadCountSpinner.getValue(); // Số lượng URL xử lý đồng thời
        List<Thread> threads = new ArrayList<>();
        List<WebDriver> drivers = new ArrayList<>();

        try {
            (new FileWriter(outputFile, false)).close();
            final FileWriter writer = new FileWriter(outputFile, true);

            // Chia danh sách URL thành các phần nhỏ
            int urlsPerThread = (int) Math.ceil((double) urls.size() / numThreads);
            
            // Tạo và khởi động các luồng
            for (int i = 0; i < numThreads && i * urlsPerThread < urls.size(); i++) {
                final int threadIndex = i;
                final int startIndex = i * urlsPerThread;
                final int endIndex = Math.min(startIndex + urlsPerThread, urls.size());
                
                Thread thread = new Thread(() -> {
                    WebDriver threadDriver = null;
                    try {
                        // Tạo ChromeDriver riêng cho mỗi luồng
                        ChromeOptions options = new ChromeOptions();
                        options.addArguments("--no-sandbox");
                        options.addArguments("--disable-dev-shm-usage");
                        options.addArguments("--remote-allow-origins=*");
                        options.addArguments("--disable-extensions");
                        options.addArguments("--disable-popup-blocking");
                        options.addArguments("--start-maximized");
                        
                        threadDriver = new ChromeDriver(options);
                        synchronized (drivers) {
                            drivers.add(threadDriver);
                        }

                        // Xử lý các URL được phân công cho luồng này
                        for (int j = startIndex; j < endIndex; j++) {
                            final int urlIndex = j;
                            final String url = urls.get(j);
                            final int progress = (int)((float)(urlIndex + 1) / (float)urls.size() * 100.0F);

                            SwingUtilities.invokeLater(() -> {
                                statusLabel.setText("Status: Processing URL " + (urlIndex + 1) + " of " + urls.size());
                                progressBar.setValue(progress);
                                progressLabel.setText(urlIndex + 1 + "/" + urls.size());
                            });

                            try {
                                threadDriver.get(url);
                                updateStatus("Waiting for page to load completely: " + url, StatusType.INFO);
                                ((JavascriptExecutor) threadDriver).executeScript("window.scrollBy(0, 100)", new Object[0]);
                                Thread.sleep(7000L);
                                updateStatus("Page loaded successfully: " + url, StatusType.SUCCESS);

                                try {
                                    threadDriver.findElement(By.xpath("//button[@type='button']")).click();
                                    Thread.sleep(7000L);

                                    try {
                                        threadDriver.findElement(By.xpath("//input[contains(@name, 'tid')]"))
                                                .sendKeys(usernameField.getText());
                                        threadDriver.findElement(By.xpath("//input[contains(@name, 'tpasswd')]"))
                                                .sendKeys(passwordField.getText());
                                    } catch (Exception ignored) {
                                    }

                                    threadDriver.findElement(By.xpath("//button[@type='submit']")).click();
                                    Thread.sleep(7000L);
                                    WebElement errorIcon = threadDriver.findElement(By.xpath("//img[@alt='errorIcon']"));
                                    String pageSource = threadDriver.getPageSource().toLowerCase();
                                    if (pageSource.contains("alt=\"erroricon\"") && pageSource.contains("移行可能なポイントがありません。") || errorIcon.isDisplayed()) {
                                        synchronized (writer) {
                                            writer.write(urlIndex + 1 + ".\t" + url + "\n");
                                            writer.flush();
                                        }
                                    }

                                    Thread.sleep(7000L);
                                } catch (Exception e) {
                                    updateStatus("No checkbox found or could not be clicked on: " + url, StatusType.WARNING);
                                }

                                Thread.sleep(2000L);
                            } catch (Exception e) {
                                updateStatus("Error processing URL: " + url + " - " + e.getMessage(), StatusType.ERROR);
                                SwingUtilities.invokeLater(() -> 
                                    JOptionPane.showMessageDialog(URLAutomationAppAI.this, 
                                        "Error processing URL " + url + ": " + e.getMessage()));
                            }
                        }
                    } catch (Exception e) {
                        updateStatus("Thread " + threadIndex + " error: " + e.getMessage(), StatusType.ERROR);
                    } finally {
                        if (threadDriver != null) {
                            try {
                                threadDriver.quit();
                                synchronized (drivers) {
                                    drivers.remove(threadDriver);
                                }
                            } catch (Exception e) {
                                System.err.println("Error quitting WebDriver in thread " + threadIndex + ": " + e.getMessage());
                            }
                        }
                    }
                });

                threads.add(thread);
                thread.start();
            }

            // Đợi tất cả các luồng hoàn thành
            for (Thread thread : threads) {
                thread.join();
            }

        } catch (IOException | InterruptedException e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
        }

        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Status: Automation completed");
            progressBar.setValue(100);
            progressLabel.setText(urls.size() + "/" + urls.size());
            JOptionPane.showMessageDialog(this, "URL automation completed!");
        });
    }

    private void updateStatus(final String message, final StatusType type) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (type) {
                    case SUCCESS:
                        URLAutomationAppAI.this.statusLabel.setForeground(URLAutomationAppAI.this.successColor);
                        break;
                    case ERROR:
                        URLAutomationAppAI.this.statusLabel.setForeground(URLAutomationAppAI.this.errorColor);
                        break;
                    case WARNING:
                        URLAutomationAppAI.this.statusLabel.setForeground(new Color(255, 152, 0));
                        break;
                    default:
                        URLAutomationAppAI.this.statusLabel.setForeground(URLAutomationAppAI.this.textColor);
                }

                URLAutomationAppAI.this.statusLabel.setText("Status: " + message);
                System.out.println(message);
            }
        });
    }

    private void cleanupResources() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
            } else {
                process = Runtime.getRuntime().exec("pkill -f chromedriver");
            }

            process.waitFor();
            System.out.println("ChromeDriver processes terminated");
        } catch (Exception e) {
            System.err.println("Error terminating ChromeDriver processes: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final URLAutomationAppAI app = new URLAutomationAppAI();
                app.setVisible(true);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        if (app != null) {
                            app.cleanupResources();
                        }

                    }
                });
            }
        });
    }

    private static enum StatusType {
        INFO,
        SUCCESS,
        ERROR,
        WARNING;

        private StatusType() {
        }
    }
}
