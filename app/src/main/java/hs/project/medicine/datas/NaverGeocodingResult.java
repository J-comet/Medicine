package hs.project.medicine.datas;

public class NaverGeocodingResult {
    /**
     * OK, INVALID_REQUEST, SYSTEM_ERROR, INVALID_ADDRESS
     */
    private String status;
    private String errorMessage;
    private double x;  // 경도
    private double y;  // 위도
    private String roadAddress;  // 도로명 주소
    private String jibunAddress; // 지번 주소

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getJibunAddress() {
        return jibunAddress;
    }

    public void setJibunAddress(String jibunAddress) {
        this.jibunAddress = jibunAddress;
    }

    public String toString() {
        return "status:" + status + ", errorMessage=" + errorMessage + ", x=" + x + ", y=" + y + ", roadAddr=" + roadAddress + ", jibunAddr=" + jibunAddress;
    }
}
