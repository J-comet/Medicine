package hs.project.medicine.datas.medicine;

public class MedicineResponse {

    private MedicineHeader medicineHeader;
    private MedicineBody medicineBody;

    public MedicineHeader getHeader() {
        return medicineHeader;
    }

    public void setHeader(MedicineHeader medicineHeader) {
        this.medicineHeader = medicineHeader;
    }

    public MedicineBody getBody() {
        return medicineBody;
    }

    public void setBody(MedicineBody medicineBody) {
        this.medicineBody = medicineBody;
    }

}
