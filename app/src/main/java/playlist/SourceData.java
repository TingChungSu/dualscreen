package playlist;


import com.google.gson.Gson;

/**
 * Created by 鼎鈞 on 2016/6/23.
 * Download/RivaGreen/to-01.jpg ~ to-14.jpg
 */

public class SourceData {
    /*
    private String strImei;
    private String strPlayCode;
    private int intFileSeq;
    private String strFileName;
    */
    private String strFileType;
    private int intPauseTime;
    private String strLCDNo;
    private String strPath;
    private boolean boolHasFile;

    //dataType: ani && pic
    //num: screennum 0/1
    public SourceData() {
        this.strPath = "";
        this.boolHasFile = false;
        this.intPauseTime = 5000;
    }

    public SourceData(String dataType, String path) {
        if (dataType.trim().toLowerCase().equals("ani"))
            this.strFileType = "ani";
        else
            this.strFileType = "pic";

        this.strPath = path;
        this.boolHasFile = true;
        this.intPauseTime = 5000;
    }

    public SourceData(String dataType, String path, String lcd) {
        if (dataType.trim().toLowerCase().equals("ani"))
            this.strFileType = "ani";
        else
            this.strFileType = "pic";

        this.strPath = path;
        this.strLCDNo = lcd;
        this.boolHasFile = true;
        this.intPauseTime = 5000;
    }
    public SourceData(String dataType, String path, String lcd,int time) {
        if (dataType.trim().toLowerCase().equals("ani"))
            this.strFileType = "ani";
        else
            this.strFileType = "pic";

        this.strPath = path;
        this.strLCDNo = lcd;
        this.boolHasFile = true;
        this.intPauseTime = time;
    }

    public SourceData(int time) {
        this.strPath = "";
        this.boolHasFile = false;
        this.intPauseTime = time;
    }

    public String toJson() {
        Gson gson = new Gson();
        String strJson = gson.toJson(this);
        return strJson;
    }

    public boolean hasFile() {
        return boolHasFile;
    }

    public boolean isVedio() {
        if (boolHasFile)
            return this.strFileType.equals("ani");
        return false;
    }

    public boolean isImage() {

        if (boolHasFile)
            return this.strFileType.equals("pic");
        return false;
    }

    public String getScreenNum() {
        return this.strLCDNo;
    }

    public int getIntPauseTime() {
        return this.intPauseTime;
    }

    public String getPath() {
        return this.strPath;
    }

}
