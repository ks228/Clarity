package appricottsoftware.clarity.sync;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import appricottsoftware.clarity.R;
import appricottsoftware.clarity.models.Channel;
import appricottsoftware.clarity.models.Metadata;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ClarityClient {

    private boolean searchQuotaRemaining;
    private final String TAG = "ClarityClient";

    public ClarityClient() {
        searchQuotaRemaining = true;
    }
    // Insert API calls here //
    // Calls the /search endpoint (fulltextsearch)
    // Parameters //
    // offset: Offset for search results, for pagination. You'll use next_offset from response for this parameter.
    // q: Search term
    // sort_by_date: Sort by date or not? If 1, sort by date. If 0 (default), sort by relevance.
    // type: What to search: "episode" (default) or "podcast"?
    public void getFullTextSearch(String genre_ids, int offset, String q, int sort_by_date, String type, Context context, JsonHttpResponseHandler handler) {
        // Create the rest client and add header(s)
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key", context.getString(R.string.listen_notes_api_key));
        // Next, we add the parameters for the api call (see function description above)
        RequestParams params = new RequestParams();
        params.put("genre_ids", genre_ids);
        int maxLength = ClarityApp.getSession(context).getMaxLength();
        if(maxLength > 0) {
            params.put("len_max", maxLength);
        }
        params.put("offset", offset);
        params.put("q", q);
        params.put("sort_by_date", sort_by_date);
        params.put("type", type);
        client.get(context.getString(R.string.listen_notes_api_url) + "search", params, handler);
    }


    public void getTypeAheadPodcast(String text, Context context, JsonHttpResponseHandler handler) {
        Log.e(TAG, "TEXT: " + text);
        // Create the rest client and add header(s)
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key", context.getString(R.string.listen_notes_api_key));

        client.get(context.getString(R.string.listen_notes_api_typeAhead) + "?q=" + text, handler);
    }

    public void authenticateUser(String email, String password, Context context, JsonHttpResponseHandler handler) {
        // Create the rest client and add header(s)

        // Added conditional to handle this issue:
        // W/AsyncHttpRH: Current thread has not called Looper.prepare(). Forcing synchronous mode.
        if (Looper.myLooper() == null) {
            SyncHttpClient client = new SyncHttpClient();

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("email", email);
                jsonParams.put("password", password);

                StringEntity entity = new StringEntity(jsonParams.toString());
                client.post(context, context.getString(R.string.login_request_url), entity, "application/json", handler);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            try {
                client.setMaxRetriesAndTimeout(0, 1000);

                jsonParams.put("email", email);
                jsonParams.put("password", password);

                StringEntity entity = new StringEntity(jsonParams.toString());
                client.post(context, context.getString(R.string.login_request_url), entity, "application/json", handler);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerRequest(String email, String password, Context context, JsonHttpResponseHandler handler) {
        // Create the rest client and add header(s)

        // Added conditional to handle this issue:
        // W/AsyncHttpRH: Current thread has not called Looper.prepare(). Forcing synchronous mode.
        if (Looper.myLooper() == null) {
            SyncHttpClient client = new SyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("email", email);
                jsonParams.put("password", password);

                StringEntity entity = new StringEntity(jsonParams.toString());
                client.post(context, context.getString(R.string.register_request_url), entity, "application/json", handler);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            try {
                client.setMaxRetriesAndTimeout(0, 1000);

                jsonParams.put("email", email);
                jsonParams.put("password", password);

                StringEntity entity = new StringEntity(jsonParams.toString());
                client.post(context, context.getString(R.string.register_request_url), entity, "application/json", handler);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAccount(int Uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", Uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.post_delete_account_url), entity, "application/json", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void metadataUpVoteRequest(int cid, ArrayList<Integer> genres, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("cid", cid);
            JSONArray metadata = new JSONArray();

            for(Integer g : genres) {
                JSONObject elem = new JSONObject();
                elem.put("mid", g);
                metadata.put(elem);
            }

            jsonParams.put("metadata", metadata);

            StringEntity entity = new StringEntity(jsonParams.toString());
            Log.e("Client: ", jsonParams.toString());
            client.post(context, context.getString(R.string.put_likes_request_url), entity, "application/json", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void metadataDownVoteRequest(int cid, ArrayList<Integer> genres, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("cid", cid);

            JSONArray metadata = new JSONArray();
            for (Integer g : genres) {
                JSONObject elem = new JSONObject();
                elem.put("mid", g);
                metadata.put(elem);
            }

            jsonParams.put("metadata", metadata);

            StringEntity entity = new StringEntity(jsonParams.toString());
            Log.e("Client: ", jsonParams.toString());
            client.post(context, context.getString(R.string.put_dislike_request_url), entity, "application/json", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createChannel(int uid, Channel channel, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            JSONArray metadata = new JSONArray();

            // Metadata 1
            JSONObject meta = new JSONObject();
            ArrayList<Metadata> channelMetadata = channel.getMetadata();
            for(Metadata cm : channelMetadata) {
                meta.put("mid", cm.getMid());
            }
            metadata.put(meta);

            jsonParams.put("uid", uid);
            jsonParams.put("title", channel.getTitle());
            jsonParams.put("image", channel.getImage());
            jsonParams.put("metadata", metadata);

            StringEntity entity = new StringEntity(jsonParams.toString());
            Log.e("ClarityClient", "createChannel: "+ jsonParams.toString());
            client.post(context, context.getString(R.string.create_channel_request_url), entity, "application/json", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChannel(int uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.get(context, context.getString(R.string.get_channel_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEmail(int uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.get(context, context.getString(R.string.get_email_request_url), entity, "application/json", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteChannel(int uid, int cid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("cid", cid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.delete_channel_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEmail(int uid, String newEmail, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("newEmail", newEmail);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.update_email_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(int uid, String newPassword, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("newPassword", newPassword);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.update_password_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPodcastLength(int uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.get(context, context.getString(R.string.get_podcastLength_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePodcastLength(int uid, float newPodcastSpeed, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("podcastLength", newPodcastSpeed);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.update_podcastLength_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlaybackSpeed(int uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.get(context, context.getString(R.string.get_playback_speed_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePlaybackSpeed(int uid, float playbackSpeed, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("playbackSpeed", playbackSpeed);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.update_playback_speed_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCurrentChannel(int uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.get(context, context.getString(R.string.get_current_channel_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentChannel(int uid, int cid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("currentChannel", cid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.update_current_channel_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSortByDate(int uid, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.get(context, context.getString(R.string.get_sort_by_date_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSortByDate(int uid, boolean sortByDate, Context context, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();

        try {
            client.setMaxRetriesAndTimeout(1, 1000);

            jsonParams.put("uid", uid);
            jsonParams.put("sortByDate", sortByDate ? 1 : 0);

            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, context.getString(R.string.update_sort_by_date_request_url), entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSearchQuotaRemaining(Header[] headers, Context context) {
        int quota_remaining = 0;
        for(Header h : headers) {
            if(h.getName().equals(context.getString(R.string.full_text_search_quota_remaining_key))) {
                quota_remaining = Integer.parseInt(h.getValue());
                Log.e("ClarityClient", "Search quota remaining: " + quota_remaining);
            }
        }
        if(quota_remaining < 50) {
            searchQuotaRemaining = false;
        } else {
            searchQuotaRemaining = true;
        }
    }

    // If search quota is remaining, return true; if out of search quota, return false
    public boolean isSearchQuotaRemaining() {
        if(!searchQuotaRemaining) {
            Log.e("ClarityClient", "No Search quota remaining");
        }
        return searchQuotaRemaining;
    }
}