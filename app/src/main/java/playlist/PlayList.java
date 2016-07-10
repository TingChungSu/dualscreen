package playlist;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 鼎鈞 on 2016/6/23.
 */
public class PlayList {

    private List<SourceData> myList = null;
    private int currentIndex = 0;
    private boolean boolPause = false;
    private boolean boolVideoPlay = false;

    public boolean isPause() {
        return boolPause;
    }

    public boolean isVideoPlay() {
        return boolVideoPlay;
    }
    public void setVideo(boolean bool) {
        this.boolVideoPlay = bool;
    }

    public void setPause(boolean bool) {
        this.boolPause = bool;
    }

    public String toJson() {
        Gson gson = new Gson();
        String strJson = gson.toJson(myList);
        strJson = "{\n\"myList\": " + strJson + "}";
        return strJson;
    }

    public void playNext() {
        currentIndex++;
        if (currentIndex >= getList().size())
            currentIndex = 0;
    }
    public void playLast(){
        currentIndex = getList().size()-1;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getNextIndex() {
        if (currentIndex + 1 >= getList().size())
            return 0;
        return currentIndex + 1;
    }

    public PlayList() {
        myList = new ArrayList<>();
    }

    public PlayList(String strJson) {
        myList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JsonObject jobj = new Gson().fromJson(strJson, JsonObject.class);
            strJson = gson.toJson(jobj.get("myList"));
            Type listType = new TypeToken<ArrayList<SourceData>>() {
            }.getType();
            myList = gson.fromJson(strJson, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SourceData> getList() {
        return myList;
    }

    public void addToBot(SourceData data) {
        myList.add(data);
    }

    public void addToTop(SourceData data) {
        myList.add(0, data);
    }

}
