package myvideoview;


import com.google.gson.Gson;

/**
 * Created by 鼎鈞 on 2016/6/23.
 * Download/RivaGreen/to-01.jpg ~ to-14.jpg
 * Download\RivaGreen\done.mpg
 */

public class SourceData {
    private String strImei;
    private String strPlayCode;
    private int intFileSeq;
    private String strFileName;
    private String strFileType;
    private int screenNum;
    private String strPath;

    //dataType: video && image
    //num: screennum 0/1
    public SourceData(String dataType, String path) {
        if (dataType.trim().toLowerCase().equals("ani"))
            this.strFileType = "video";
        else
            this.strFileType = "image";

        this.strPath = path;
    }

    public String toJson() {
        Gson gson = new Gson();
        String strJson = gson.toJson(this);
        return strJson;
    }

    public boolean isVedio() {
        return this.strFileType.equals("video");
    }

    public boolean isImage() {
        return this.strFileType.equals("image");
    }

    public int getScreenNum() {
        return this.screenNum;
    }

    public String getPath() {
        return this.strPath;
    }

}
