package org.tmpl;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRates {
    private static ExchangeRates instance;
    private final List<Rate> EXCHANGE_RATES;
    private final LocalDate DATE;
    private final String URL = "http://api.nbp.pl/api/exchangerates/tables/A?format=json";

    @SneakyThrows
    private ExchangeRates() {
        JSONObject response = fetchTable();
        this.EXCHANGE_RATES = fetchRates(response);
        this.DATE = fetchDate(response);
    }

    public static synchronized ExchangeRates getInstance() {
        if (instance == null) {
            instance = new ExchangeRates();
        }
        return instance;
    }

    public LocalDate getDate() {
        return this.DATE;
    }

    public double getRateByCode(String code) {
        Optional<Rate> rate = EXCHANGE_RATES.stream()
                .filter(r -> r.code().equalsIgnoreCase(code))
                .findFirst();
        return rate.map(Rate::mid).orElse(-1.0);
    }

    public double getRateByName(String name) {
        Optional<Rate> rate = EXCHANGE_RATES.stream()
                .filter(r -> r.currency().equalsIgnoreCase(name))
                .findFirst();
        return rate.map(Rate::mid).orElse(-1.0);
    }

    public List<Rate> getRatesAbove(double threshold) {
        return EXCHANGE_RATES.stream()
                .filter(rate -> rate.mid() > threshold)
                .collect(Collectors.toList());
    }

    public List<Rate> getRatesBelow(double threshold) {
        return EXCHANGE_RATES.stream()
                .filter(rate -> rate.mid() < threshold)
                .collect(Collectors.toList());
    }

    private List<Rate> fetchRates(JSONObject table) {
        JSONArray jsonArray = table.getJSONArray("rates");
        List<Rate> rateList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Rate rate = new Rate(
                    jsonObject.getString("currency"),
                    jsonObject.getString("code"),
                    jsonObject.getDouble("mid")
            );
            rateList.add(rate);
        }
        return rateList;
    }

    private LocalDate fetchDate(JSONObject table) {
        String rawDate = table.getString("effectiveDate");
        return LocalDate.parse(rawDate);
    }

    @SneakyThrows
    private JSONObject fetchTable() { //todo refactor
        HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONArray jsonResponse = new JSONArray(response.toString());
            return jsonResponse.getJSONObject(0);
        } else {
            throw new Exception("Failed to fetch current rates!");
        }
    }

    public record Rate(String currency, String code, double mid) {
        @Override
        public String toString() {
            return String.format("#%s (%s): %.4f PLN", currency, code, mid);
        }
    }
}