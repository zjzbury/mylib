package red.bury.mylibrary;

import org.json.JSONException;

public interface Callback {
    void success(String json) throws Exception;
    void fail(String error);
}
