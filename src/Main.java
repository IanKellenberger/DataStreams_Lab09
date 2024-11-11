import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path filePath;

    public Main() {
        setTitle("Text File Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(loadButton);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, originalScrollPane, filteredScrollPane);
        splitPane.setDividerLocation(400);

        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        loadButton.addActionListener(new LoadButtonListener());
        searchButton.addActionListener(new SearchButtonListener());
        quitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(Main.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().toPath();
                loadFile(filePath);
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (filePath != null && !searchField.getText().isEmpty()) {
                String searchString = searchField.getText();
                searchFile(filePath, searchString);
            } else {
                JOptionPane.showMessageDialog(Main.this, "Please load a file and enter a search string.");
            }
        }
    }

    private void loadFile(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            List<String> content = lines.collect(Collectors.toList());
            originalTextArea.setText(String.join("\n", content));
            filteredTextArea.setText(""); // Clear the filtered area
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
        }
    }

    private void searchFile(Path path, String searchString) {
        try (Stream<String> lines = Files.lines(path)) {
            List<String> filteredContent = lines
                    .filter(line -> line.contains(searchString))
                    .collect(Collectors.toList());
            filteredTextArea.setText(String.join("\n", filteredContent));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
