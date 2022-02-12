
package hs.project.medicine.datas.weather;

public class WeatherBody {

    private String dataType;
    private WeatherItems weatherItems;
    private Integer pageNo;
    private Integer numOfRows;
    private Integer totalCount;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public WeatherItems getItems() {
        return weatherItems;
    }

    public void setItems(WeatherItems weatherItems) {
        this.weatherItems = weatherItems;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(Integer numOfRows) {
        this.numOfRows = numOfRows;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
