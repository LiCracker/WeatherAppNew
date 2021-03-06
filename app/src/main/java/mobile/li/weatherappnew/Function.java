package mobile.li.weatherappnew;

/**
 * Created by Li on 2017/10/28.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Function {
    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "3a3b335b2f571f559d800d785915a563";

    public static String setWeatherIcon(int actualId, long sunrise, long sunset, long rtime){
        int id = actualId / 100;
        String icon = "";
        Date sunriseF = new Date(sunrise);
        Date sunsetF = new Date(sunset);
        Date rtimeF = new Date(rtime);
        Log.i(TAG, "[setWeatherIcon]sunrise Hour: " + sunriseF.getHours()
                + " sunset Hour: " + sunsetF.getHours()
                + " Report Time Hour: " + rtimeF.getHours());

        if(actualId == 800){
            //long currentTime = new Date().getTime();
            long currentTime = rtime;
            //if(currentTime>=sunrise && currentTime<sunset) {
            HashSet<Integer> suntime = new HashSet<>();
            int sunriseHours = sunriseF.getHours();
            int sunsetHours = sunsetF.getHours();
            int rtimeHours = rtimeF.getHours();
            if(sunriseHours > sunsetHours){
                for(int i = sunriseHours; i <= 23; i++){
                    suntime.add(i);
                }
                for(int i = 0; i <= sunsetHours; i++){
                    suntime.add(i);
                }
            }else{
                for(int i = sunriseHours; i <= sunsetHours; i++){
                    suntime.add(i);
                }
            }

            if(suntime.contains(rtimeHours)){
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    public static String setWeatherIcon2(int actualId){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            icon = "&#xf00d;";
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    public static Date castDate(Date original_date){
        Date result = new Date(original_date.getTime());
        if(original_date.getMinutes() > 50){
            result.setHours(result.getHours() + 1);
            result.setMinutes(0);
        }else{
            result.setMinutes(0);
        }

        return result;
    }

    public static Date castDateCurr(Date original_date, int lon){
        Date temp = castDate(new Date(original_date.getTime() + lon * 240000L));
        Date result = new Date(original_date.getTime());
        result.setHours(temp.getHours());
//        Log.i(TAG, "[castDateCurr]original Hour: " + original_date.getHours()
//                + " Cast Hour: " + result.getHours());
        return result;
    }

    public static String CelsiusToFahrenheit(String fahrenheit){
        if(fahrenheit == null){
            return null;
        }else if(fahrenheit == ""){
            return "";
        }

        Double degree = Double.valueOf(fahrenheit.split("°")[0]);
        Double celsius = (5.0/9.0)*(degree - 32.0);
        return celsius.intValue() + "°";
    }

    public static String FahrenheitToCelsius(String celsius){
        if(celsius == null){
            return null;
        }else if(celsius == ""){
            return "";
        }

        Double degree = Double.valueOf(celsius.split("°")[0]);
        Double fahrenheit = (9.0/5.0)*degree + 32;
        return fahrenheit.intValue() + "°";
    }

    public static String CelsiusToFahrenheitDays(String fahrenheit){
        if(fahrenheit == null){
            return null;
        }else if(fahrenheit == ""){
            return "";
        }

        String[] temp = fahrenheit.substring(0, fahrenheit.length() - 2).split("/");
        Double highest = Double.valueOf(temp[0]);
        Double lowest = Double.valueOf(temp[1]);
        Double celsius_highest = (5.0/9.0)*(highest - 32.0);
        Double celsius_lowest = (5.0/9.0)*(lowest - 32.0);
        return celsius_highest.intValue() + "/" + celsius_lowest.intValue() + "°C";
    }

    public static String FahrenheitToCelsiusDays(String celsius){
        if(celsius == null){
            return null;
        }else if(celsius == ""){
            return "";
        }

        String[] temp = celsius.substring(0, celsius.length() - 2).split("/");
        Double highest = Double.valueOf(temp[0]);
        Double lowest = Double.valueOf(temp[1]);
        Double fahrenheit_highest = (9.0/5.0) * highest + 32;
        Double fahrenheit_lowest = (9.0/5.0) * lowest + 32;
        return fahrenheit_highest.intValue() + "/" + fahrenheit_lowest.intValue() + "°F";
    }

    public interface AsyncResponse {

        void processFinish(String output1, String output2, String output3, String output4, String output5, String output6, String output7, String output8);
    }

    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null){
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                    String description = details.getString("description").toUpperCase(Locale.US);
                    String temperature = String.valueOf((int)main.getDouble("temp")) + "°";
                    String humidity = main.getString("humidity") + "%";
                    String pressure = main.getString("pressure") + " hPa";
                    String updatedOn = df.format(new Date(json.getLong("dt")*1000));
                    String iconText = setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000,
                            new Date().getTime());

                    delegate.processFinish(city, description, temperature, humidity, pressure, updatedOn, iconText, ""+ (json.getJSONObject("sys").getLong("sunrise") * 1000));

                }
            } catch (JSONException e) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
        }
    }

    public static JSONObject getWeatherJSON(String lat, String lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
