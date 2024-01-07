package cz.iqlandia.iqplanetarium.chatreciever;

public record LivePageData(
        String liveID,
        String apiKey,
        String clientVersion,
        String continuation) {}
