package com.op;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CityData {
    // ⚠️ REPLACE THESE PLACEHOLDERS WITH YOUR ACTUAL API KEYS!
    private static final String OPENWEATHERMAP_API_KEY = "f3e4205d9b8aff2e96e974732bc40877";
    private static final String GEOAPIFY_API_KEY = "7f068a6ae7534fb9ae613e1a2c6c7d5c";
    // -------------------------------------------------------------
    
    private String cityName;
    private String liveWeather;
    private String fiveDayForecast;
    private String topAttractions;
    private String travelSafetyTips = "Be cautious in crowded areas. Validate all tourist booking agents. India is generally safe, but local research is advised.";

    private CityData(String cityName) {
        this.cityName = cityName;
    }

    // Static method to fetch data (simulating API calls)
    public static CityData fetchData(String city) {
        // ... (rest of the fetchData method logic remains the same)
        CityData data = new CityData(city);
        
        try {
            // 1. Fetch Weather Data (Live & 5-Day Forecast)
            String weatherJson = fetchDataFromApi(getOpenWeatherMapUrl(city));
            
            // --- SIMULATED PARSING ---
            if (weatherJson.contains("404")) {
                 data.liveWeather = "Error: City not found or API key issue.";
                 data.fiveDayForecast = "N/A";
            } else {
                 // In a real app, you would parse the JSON here
                 data.liveWeather = simulateWeather(city);
                 data.fiveDayForecast = simulateForecast(city);
            }
            // -------------------------

            // 2. Fetch Top Attractions (Points of Interest - POI)
            String attractionsJson = fetchDataFromApi(getGeoapifyPlacesUrl(city));
            
            // --- SIMULATED PARSING ---
            if (attractionsJson.contains("[]") || attractionsJson.contains("401")) {
                data.topAttractions = "No popular attractions found or API key error.";
            } else {
                // In a real app, you would parse the JSON here
                data.topAttractions = simulateAttractions(city); 
            }
            // -------------------------

        } catch (Exception e) {
            System.err.println("API call error for " + city + ": " + e.getMessage());
            data.liveWeather = "Connection/API error.";
            data.topAttractions = "Connection/API error.";
            data.fiveDayForecast = "N/A";
        }

        return data;
    }

    // Helper method to execute HTTP request
    private static String fetchDataFromApi(String apiUrl) throws Exception {
        if (OPENWEATHERMAP_API_KEY.contains("YOUR_") || GEOAPIFY_API_KEY.contains("YOUR_")) {
             throw new Exception("API Keys are missing. Please replace placeholders in CityData.java.");
        }
        
        // ... (HTTP connection logic remains the same as provided previously)
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } else {
            return "{\"cod\":" + responseCode + ", \"message\":\"API error.\"}";
        }
    }
    
    // --- API URL BUILDERS (Require API Keys to function) ---
    
    private static String getOpenWeatherMapUrl(String city) {
        try {
            String encodedCity = URLEncoder.encode(city + ", IN", StandardCharsets.UTF_8.toString());
            return "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + 
                   "&appid=" + OPENWEATHERMAP_API_KEY + "&units=metric";
        } catch (Exception e) { return ""; }
    }

    private static String getGeoapifyPlacesUrl(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            return "https://api.geoapify.com/v2/places?categories=tourism.sights&filter=place:" + encodedCity + 
                   "&limit=10&apiKey=" + GEOAPIFY_API_KEY;
        } catch (Exception e) { return ""; }
    }

    // --- SIMULATED DATA FOR DEMO PURPOSES (Used until API keys are provided) ---

    private static String simulateWeather(String city) {
        if (city.equalsIgnoreCase("Delhi")) return "28°C, Haze 🌫️, Humidity 70%, Wind 8 km/h";
        if (city.equalsIgnoreCase("Mumbai")) return "30°C, Clear ✨, Humidity 85%, Wind 15 km/h";
        if (city.equalsIgnoreCase("Bangalore")) return "24°C, Pleasant 🌤️, Humidity 55%, Wind 10 km/h";
        if (city.equalsIgnoreCase("Jaipur")) return "35°C, Sunny ☀️, Dry, Wind 12 km/h";
        return "Simulated: " + city + ", 25°C, Variable Clouds.";
    }
    
    private static String simulateForecast(String city) {
        if (city.equalsIgnoreCase("Delhi")) return "Day 1: 30°C, Day 2: 29°C, Day 3: 28°C, Day 4: 31°C, Day 5: 30°C.";
        if (city.equalsIgnoreCase("Mumbai")) return "Day 1: 31°C, Day 2: 30°C (Rain), Day 3: 29°C (Rain), Day 4: 30°C, Day 5: 31°C.";
        if (city.equalsIgnoreCase("Bangalore")) return "Day 1: 25°C, Day 2: 26°C, Day 3: 25°C, Day 4: 24°C, Day 5: 26°C.";
        return "Simulated 5-Day: Generally stable temperatures and conditions.";
    }
    
    private static String simulateAttractions(String city) {
        if (city.equalsIgnoreCase("Delhi")) return "Red Fort, Qutub Minar, India Gate, Lotus Temple, Humayun's Tomb";
        if (city.equalsIgnoreCase("Mumbai")) return "Gateway of India, Marine Drive, Chhatrapati Shivaji Terminus (CST), Elephanta Caves";
        if (city.equalsIgnoreCase("Bangalore")) return "Lal Bagh Botanical Garden, Bangalore Palace, Vidhana Soudha, Cubbon Park";
        if (city.equalsIgnoreCase("Jaipur")) return "Hawa Mahal, Amer Fort, City Palace, Jantar Mantar, Nahargarh Fort";
        return "Simulated: Popular Tourist Spots (e.g., City Palace, Main Market)";
    }
    
    // --- Getters and Map URL ---

    public String getMapUrl() {
        return "https://www.google.com/maps/search/" + this.cityName.replace(" ", "+") + "+India+tourist+attractions";
    }

    public String getCityName() { return cityName; }
    public String getLiveWeather() { return liveWeather; }
    public String getFiveDayForecast() { return fiveDayForecast; }
    public String getTopAttractions() { return topAttractions; }
    public String getTravelSafetyTips() { return travelSafetyTips; }
}