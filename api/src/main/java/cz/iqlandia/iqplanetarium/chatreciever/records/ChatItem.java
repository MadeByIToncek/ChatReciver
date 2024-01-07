package cz.iqlandia.iqplanetarium.chatreciever.records;

import java.time.LocalDateTime;
import java.util.Optional;

public record ChatItem(
        String id,
        Author author,
        String message,
        LocalDateTime timestamp) {

    public record Author(
       String name,
       Optional<ImageItem> thumbnail,
       String channelID,
       Optional<Badge> badge,
       boolean isVerified,
       boolean isOwner,
       boolean isModerator
    ) {
        public record Badge(
                ImageItem thumbnail,
                String Label) {
        }
    }

    public record ImageItem(
            String url,
            String alt
    ) {}
}
