package hs.project.medicine.datas;

import java.io.Serializable;

public class MedicineItem implements Serializable {

    private String entpName;        // 업체명
    private String itemName;        // 제품명
    private String itemSeq;         // 품목기준코드
    private String efcyQesitm;      // 효능은 무엇?
    private String useMethodQesitm; // 사용하는 방법은?
    private Object atpnWarnQesitm;  // 약 사용전 숙지해야 될 내용
    private String atpnQesitm;      // 사용상 주의사항?
    private String intrcQesitm;     // 사용하면서 주의해야할 약 or 음식?
    private String seQesitm;        // 부작용은?
    private String depositMethodQesitm; // 보관방법은?
    private String openDe;          // 공개일자
    private String updateDe;        // 수정일자
    private String itemImage;       // 이미지

    public String getEntpName() {
        return entpName;
    }

    public void setEntpName(String entpName) {
        this.entpName = entpName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSeq() {
        return itemSeq;
    }

    public void setItemSeq(String itemSeq) {
        this.itemSeq = itemSeq;
    }

    public String getEfcyQesitm() {
        return efcyQesitm;
    }

    public void setEfcyQesitm(String efcyQesitm) {
        this.efcyQesitm = efcyQesitm;
    }

    public String getUseMethodQesitm() {
        return useMethodQesitm;
    }

    public void setUseMethodQesitm(String useMethodQesitm) {
        this.useMethodQesitm = useMethodQesitm;
    }

    public Object getAtpnWarnQesitm() {
        return atpnWarnQesitm;
    }

    public void setAtpnWarnQesitm(Object atpnWarnQesitm) {
        this.atpnWarnQesitm = atpnWarnQesitm;
    }

    public String getAtpnQesitm() {
        return atpnQesitm;
    }

    public void setAtpnQesitm(String atpnQesitm) {
        this.atpnQesitm = atpnQesitm;
    }

    public String getIntrcQesitm() {
        return intrcQesitm;
    }

    public void setIntrcQesitm(String intrcQesitm) {
        this.intrcQesitm = intrcQesitm;
    }

    public String getSeQesitm() {
        return seQesitm;
    }

    public void setSeQesitm(String seQesitm) {
        this.seQesitm = seQesitm;
    }

    public String getDepositMethodQesitm() {
        return depositMethodQesitm;
    }

    public void setDepositMethodQesitm(String depositMethodQesitm) {
        this.depositMethodQesitm = depositMethodQesitm;
    }

    public String getOpenDe() {
        return openDe;
    }

    public void setOpenDe(String openDe) {
        this.openDe = openDe;
    }

    public String getUpdateDe() {
        return updateDe;
    }

    public void setUpdateDe(String updateDe) {
        this.updateDe = updateDe;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

}
