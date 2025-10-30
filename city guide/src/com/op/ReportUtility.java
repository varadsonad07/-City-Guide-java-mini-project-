package com.op;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReportUtility {

    private static final String BOOKMARK_FILE = "city_guide_bookmarks.txt";

    // --- Bookmark Functionality ---
    public static void saveBookmark(String city) {
        try (PrintWriter out = new PrintWriter(new FileWriter(BOOKMARK_FILE, true))) {
            out.println(city.toUpperCase());
        } catch (IOException e) {
            System.err.println("Error saving bookmark: " + e.getMessage());
        }
    }

    public static List<String> loadBookmarks() {
        List<String> bookmarks = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(BOOKMARK_FILE))) {
                // Read all lines, filter out duplicates, and collect
                bookmarks = Files.lines(Paths.get(BOOKMARK_FILE))
                                 .map(String::trim)
                                 .distinct()
                                 .collect(Collectors.toList());
            }
        } catch (IOException e) {
            System.err.println("Error loading bookmarks: " + e.getMessage());
        }
        return bookmarks;
    }
    
    // --- Export Report Functionality ---
    public static String exportReport(CityData cityData, List<String> currentBookmarks) {
        String filename = cityData.getCityName().replace(" ", "_") + "_Guide_Report.txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("--- CITY GUIDE + TRAVEL REPORT ---");
            writer.println("Generated for: " + cityData.getCityName());
            writer.println("Date: " + java.time.LocalDateTime.now());
            writer.println("-----------------------------------");
            
            writer.println("\n[1] LIVE WEATHER AND FORECAST");
            writer.println("Current Conditions: " + cityData.getLiveWeather());
            writer.println("5-Day Forecast: " + cityData.getFiveDayForecast());

            writer.println("\n[2] TOP PLACES");
            writer.println(cityData.getTopAttractions().replace(", ", "\n - "));
            writer.println("\nMap Link: " + cityData.getMapUrl());
            
            writer.println("\n[3] TRAVEL ADVISORY");
            writer.println("Safety Tips: " + cityData.getTravelSafetyTips());

            writer.println("\n[4] SAVED BOOKMARKS");
            if (currentBookmarks.isEmpty()) {
                writer.println("No cities bookmarked.");
            } else {
                currentBookmarks.forEach(writer::println);
            }
            
            return "SUCCESS: Report exported to " + new File(filename).getAbsolutePath();

        } catch (IOException e) {
            return "ERROR: Failed to export report: " + e.getMessage();
        }
    }
}
