
package hs.project.medicine.datas.weather;

public class WeatherResponse {

    private WeatherHeader weatherHeader;
    private WeatherBody weatherBody;

    public WeatherHeader getHeader() {
        return weatherHeader;
    }

    public void setHeader(WeatherHeader weatherHeader) {
        this.weatherHeader = weatherHeader;
    }

    public WeatherBody getBody() {
        return weatherBody;
    }

    public void setBody(WeatherBody weatherBody) {
        this.weatherBody = weatherBody;
    }

}
