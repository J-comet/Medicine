package hs.project.medicine.datas;

import java.util.List;

public class Body {

    private Integer pageNo;
    private Integer totalCount;
    private Integer numOfRows;
    private List<MedicineItem> items = null;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(Integer numOfRows) {
        this.numOfRows = numOfRows;
    }

    public List<MedicineItem> getItems() {
        return items;
    }

    public void setItems(List<MedicineItem> items) {
        this.items = items;
    }

}
