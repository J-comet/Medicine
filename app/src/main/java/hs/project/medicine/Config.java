package hs.project.medicine;

public class Config {

    public static String URL_GET_EASY_DRUG = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";
    public static String URL_GET_MEDICINE_STORE = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire";
    public static String URL_GET_NAVER_GEOCODE = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";
    public static String URL_GET_VILLAGE_FCST = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";  // 단기예보조회

    public static String URL_PRIVACY = "https://sites.google.com/view/private-medicare/index";

    public interface MAIN_BOTTOM_MENU {
        String HOME = "home";
        String SEARCH = "search";
        String MAP = "map";
        String USER_LIST = "user_list";
    }

    public interface PREFERENCE_KEY {
        String USER_LIST = "user_list";
        String ALARM_LIST = "alarm_list";
        String DAY_OF_WEEK = "day_of_the_week";
    }
}
