package technica.com.greencity;

/**
 * Created by Aman on 6/15/2017.
 */

public class RequestHelper {
    public String sr_no, plantType, plantName, urlImage, completeAddress, status;

    public RequestHelper(String sr_no, String plantType, String plantName, String urlImage, String completeAddress, String status) {
        this.sr_no = sr_no;
        this.plantType = plantType;
        this.plantName = plantName;
        this.urlImage = urlImage;
        this.completeAddress = completeAddress;
        this.status = status;
    }

    public String getSr_no() {
        return sr_no;
    }

    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        this.completeAddress = completeAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
