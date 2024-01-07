package cz.iqlandia.iqplanetarium.chatreciever;

import java.util.Objects;

public final class LivePageData {
    public final String liveID;
    public final String apiKey;
    public final String clientVersion;
    public String continuation;

    public LivePageData(
            String liveID,
            String apiKey,
            String clientVersion,
            String continuation) {
        this.liveID = liveID;
        this.apiKey = apiKey;
        this.clientVersion = clientVersion;
        this.continuation = continuation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LivePageData) obj;
        return Objects.equals(this.liveID, that.liveID) &&
                Objects.equals(this.apiKey, that.apiKey) &&
                Objects.equals(this.clientVersion, that.clientVersion) &&
                Objects.equals(this.continuation, that.continuation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liveID, apiKey, clientVersion, continuation);
    }

    @Override
    public String toString() {
        return "LivePageData[" +
                "liveID=" + liveID + ", " +
                "apiKey=" + apiKey + ", " +
                "clientVersion=" + clientVersion + ", " +
                "continuation=" + continuation + ']';
    }
}
