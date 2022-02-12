
package hs.project.medicine.datas.weather;

public class WeatherItem {

    private String baseDate;    // 발표일자
    private String baseTime;    // 발표시각

    /**
     * category 문자 값
     * TMP = 1시간 기온       ( 단위 - ℃ )
     * TMN = 일 최저기온      ( 단위 - ℃ )
     * TMX = 일 최고기온      ( 단위 - ℃ )
     * UUU = 풍속(동서성분)   ( 단위 - m/s )
     * VVV = 풍속(남북성분)   ( 단위 - m/s )
     * WAV = 파고             ( 단위 - M )
     * WSD = 풍속             ( 단위 - m/s )
     * POP = 강수확률         ( 단위 - % )
     * PTY = 강수형태         ( 코드값  :
     *                                  [초단기] 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
     *                                   [단기] 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4) )
     * PCP = 1시간 강수량     ( 단위 - 범주 (1 mm) )
     * REH = 습도             ( 단위 - % )
     * SNO = 1시간 신적설     ( 단위 - 범주 (1 cm) )
     * VEC = 풍향             ( 단위 - deg )
     * SKY = 하늘상태          ( 코드값 : 맑음(1), 구름많음(3), 흐림(4) )
     *
     * 현재 사용할 값
     * REH(습도), SKY(하늘상태), POP(강수확률), PTY(강수형태)
     */
    private String category;    // 자료구분문자
    private String fcstDate;    // 예보일자
    private String fcstTime;    // 예보시각
    private String fcstValue;   // 예보 값
    private Integer nx;         // 예보지점 X 좌표
    private Integer ny;         // 예보지점 Y 좌표

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public String getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(String baseTime) {
        this.baseTime = baseTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFcstDate() {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public String getFcstValue() {
        return fcstValue;
    }

    public void setFcstValue(String fcstValue) {
        this.fcstValue = fcstValue;
    }

    public Integer getNx() {
        return nx;
    }

    public void setNx(Integer nx) {
        this.nx = nx;
    }

    public Integer getNy() {
        return ny;
    }

    public void setNy(Integer ny) {
        this.ny = ny;
    }

}
