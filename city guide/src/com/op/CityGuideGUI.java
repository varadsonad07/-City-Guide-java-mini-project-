package com.op;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.List;

public class CityGuideGUI extends JFrame {

    private JTextField cityInput;
    private JTextArea displayArea;
    private CityData currentCity;

    public CityGuideGUI() {
        // --- Frame Setup ---
        setTitle("CITY GUIDE + (Java Mini Project)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // --- Components ---

        // Top Panel for Input
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new JLabel("Enter City (e.g., Delhi, Mumbai):"));
        cityInput = new JTextField(20);
        JButton searchButton = new JButton("Search City");
        inputPanel.add(cityInput);
        inputPanel.add(searchButton);
        add(inputPanel, BorderLayout.NORTH);

        // Center Area for Details
        displayArea = new JTextArea("Welcome to City Guide +! Enter an Indian city name (e.g., Delhi, Mumbai, Jaipur) and click Search.\n\n" +
                                    "⚠️ Note: This is a conceptual demo. Replace API keys and add JSON parsing library (like Gson) for real data fetch.", 25, 60);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Bottom Panel for Options (Menu)
        JPanel optionsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton detailsButton = new JButton("1. Get Details");
        JButton mapButton = new JButton("2. Open Map");
        JButton bookmarkButton = new JButton("3. Bookmark City");
        JButton exportButton = new JButton("4. Export Report");
        
        optionsPanel.add(detailsButton);
        optionsPanel.add(mapButton);
        optionsPanel.add(bookmarkButton);
        optionsPanel.add(exportButton);
        
        add(optionsPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCityData(cityInput.getText());
            }
        });

        detailsButton.addActionListener(e -> showDetails());
        mapButton.addActionListener(e -> openMap());
        bookmarkButton.addActionListener(e -> bookmarkCity());
        exportButton.addActionListener(e -> exportReport());
        
        // Final setup
        setVisible(true);
        loadCityData("Delhi"); // Load a default city on startup
    }

    private void loadCityData(String cityName) {
        if (cityName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid city name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // This initiates the (simulated) API call
        currentCity = CityData.fetchData(cityName);
        
        // Update display with initial summary
        displayArea.setText("");
        displayArea.append("CITY: " + currentCity.getCityName().toUpperCase() + " (India)\n");
        displayArea.append("------------------------------------------\n");
        displayArea.append("Current Weather: " + currentCity.getLiveWeather() + "\n");
        displayArea.append("Top Attractions: " + currentCity.getTopAttractions().substring(0, Math.min(currentCity.getTopAttractions().length(), 60)) + "...\n");
        displayArea.append("\nReady for action. Use the buttons below to explore!\n");
    }
    
    // 1. Get Details (Weather and Top Places)
    private void showDetails() {
        if (currentCity == null) {
            displayArea.setText("No city loaded. Please search for a city first.");
            return;
        }

        displayArea.setText("");
        displayArea.append("--- Details for " + currentCity.getCityName().toUpperCase() + " ---\n");
        displayArea.append("\n[WEATHER & FORECAST (OpenWeatherMap)]\n");
        displayArea.append("Live: " + currentCity.getLiveWeather() + "\n");
        displayArea.append("5-Day Forecast: " + currentCity.getFiveDayForecast() + "\n");
        
        displayArea.append("\n[TOP PLACES (Geoapify)]\n");
        displayArea.append("Attractions: \n - " + currentCity.getTopAttractions().replace(", ", "\n - ") + "\n");
        
        displayArea.append("\n[TRAVEL ADVISORY]\n");
        displayArea.append("Safety Tips: " + currentCity.getTravelSafetyTips() + "\n");
    }

    // 2. Open Map (Map Integration)
    private void openMap() {
        if (currentCity == null) {
            JOptionPane.showMessageDialog(this, "No city loaded. Please search for a city first.", "Action Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String mapUrl = currentCity.getMapUrl();
            Desktop.getDesktop().browse(new URI(mapUrl));
            JOptionPane.showMessageDialog(this, "Map for " + currentCity.getCityName() + " opened in browser.", "Map Integration", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error opening map. Please check your system settings or connectivity.", "Map Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 3. Bookmark City
    private void bookmarkCity() {
        if (currentCity == null) {
            JOptionPane.showMessageDialog(this, "No city loaded. Please search for a city first.", "Action Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ReportUtility.saveBookmark(currentCity.getCityName());
        List<String> bookmarks = ReportUtility.loadBookmarks();
        
        displayArea.setText("--- Bookmarks Updated ---\n");
        displayArea.append("Saved city: " + currentCity.getCityName() + "\n");
        displayArea.append("\nALL BOOKMARKED CITIES (" + bookmarks.size() + "): \n");
        bookmarks.forEach(city -> displayArea.append(" - " + city + "\n"));
    }

    // 4. Export Report
    private void exportReport() {
        if (currentCity == null) {
            JOptionPane.showMessageDialog(this, "No city loaded. Please search for a city first.", "Action Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<String> bookmarks = ReportUtility.loadBookmarks();
        String result = ReportUtility.exportReport(currentCity, bookmarks);
        
        JOptionPane.showMessageDialog(this, result, "Export Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CityGuideGUI());
    }
}