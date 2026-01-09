package software.ulpgc.moneycalculator.application.date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebService {
    private static final String ApiKey = "a80b5d1d19862ebdd153cf19";
    private static final String ApiUrl = "https://v6.exchangerate-api.com/v6/API-KEY/".replace("API-KEY", ApiKey);

    public static class CurrencyLoader implements software.ulpgc.moneycalculator.architecture.io.CurrencyLoader {

        @Override
        public List<Currency> loadAll() {
            try {
                return readCurrencies();
            } catch (IOException e) {
                return List.of();
            }
        }

        private List<Currency> readCurrencies() throws IOException {
            try (InputStream is = openInputStream(createConnection())) {
                return readCurrenciesWith(jsonIn(is));
            }
        }

        private List<Currency> readCurrenciesWith(String json) {
            return readCurrenciesWith(jsonObjectIn(json));
        }

        private List<Currency> readCurrenciesWith(JsonObject jsonObject) {
            return readCurrenciesWith(jsonObject.get("supported_codes").getAsJsonArray());
        }

        private List<Currency> readCurrenciesWith(JsonArray jsonArray) {
            List<Currency> list = new ArrayList<>();
            for (JsonElement item : jsonArray)
                list.add(readCurrencyWith(item.getAsJsonArray()));
            return list;
        }

        private Currency readCurrencyWith(JsonArray tuple) {
            return new Currency(
                    tuple.get(0).getAsString(),
                    tuple.get(1).getAsString()
            );
        }

        private static String jsonIn(InputStream is) throws IOException {
            return new String(is.readAllBytes());
        }

        private static JsonObject jsonObjectIn(String json) {
            return new Gson().fromJson(json, JsonObject.class);
        }

        private InputStream openInputStream(URLConnection connection) throws IOException {
            return connection.getInputStream();
        }

        private static URLConnection createConnection() throws IOException {
            URL url = new URL((ApiUrl + "codes"));
            return url.openConnection();
        }
    }

    public static class ExchangeRateLoader implements software.ulpgc.moneycalculator.architecture.io.ExchangeRateLoader {
        @Override
        public ExchangeRate load(Currency from, Currency to) {
            try {
                Map<String, Object> rateData = readRateData(new URL(ApiUrl + "pair/" + from.code() + "/" + to.code()));
                return new ExchangeRate(
                        (LocalDate) rateData.get("Date"),
                    from,
                    to,
                        (Double) rateData.get("Rate")
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Map<String, Object> readRateData(URL url) throws IOException {
            return readRateData(url.openConnection());
        }

        private Map<String, Object> readRateData(URLConnection connection) throws IOException {
            try (InputStream inputStream = connection.getInputStream()) {
                return readRateData(new String(new BufferedInputStream(inputStream).readAllBytes()));
            }
        }

        private Map<String, Object> readRateData(String json) {
            return readRateData(new Gson().fromJson(json, JsonObject.class));
        }

        private Map<String, Object> readRateData(JsonObject object) {
            HashMap<String , Object> rateData = new HashMap<>();
            rateData.put("Rate", object.get("conversion_rate").getAsDouble());
            rateData.put("Date", dateFromString(object.get("time_last_update_utc").getAsString()));
            return rateData;
        }

        private LocalDate dateFromString(String timeLastUpdateUtc) {
            return LocalDate.parse(timeLastUpdateUtc, DateTimeFormatter.RFC_1123_DATE_TIME);
        }

    }
}
