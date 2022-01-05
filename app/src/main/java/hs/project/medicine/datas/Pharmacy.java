package hs.project.medicine.datas;

// 약국위치 xml 데이터
public class Pharmacy {

    String dutyAddr;   // 주소
    String dutyNamel;   // 기관명
    String dutyTel1;  // 대표전화
    double wgs84Lon;  // 병원경도
    double wgs84Lat;  // 병원위도

    public String getDutyAddr() {
        return dutyAddr;
    }

    public void setDutyAddr(String dutyAddr) {
        this.dutyAddr = dutyAddr;
    }

    public String getDutyNamel() {
        return dutyNamel;
    }

    public void setDutyNamel(String dutyNamel) {
        this.dutyNamel = dutyNamel;
    }

    public String getDutyTel1() {
        return dutyTel1;
    }

    public void setDutyTel1(String dutyTel1) {
        this.dutyTel1 = dutyTel1;
    }

    public double getWgs84Lon() {
        return wgs84Lon;
    }

    public void setWgs84Lon(double wgs84Lon) {
        this.wgs84Lon = wgs84Lon;
    }

    public double getWgs84Lat() {
        return wgs84Lat;
    }

    public void setWgs84Lat(double wgs84Lat) {
        this.wgs84Lat = wgs84Lat;
    }
}
