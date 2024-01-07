package cz.iqlandia.iqplanetarium.chatreciever.records;

import java.time.LocalDateTime;
import java.util.Optional;

public record ChatItem(
        String id,
        Author author,
        String message,
        Optional<Superchat> superchat,
        boolean isMembership,
        boolean isVerified,
        boolean isOwner,
        boolean isModerator,
        LocalDateTime timestamp) {

    private record Author(
       String name,
       Optional<ImageItem> thumbnail,
       String channelID,
       Optional<Badge> badge
    ) {
        private record Badge(
                ImageItem thumbnail,
                String Label) {
        }
    }

    private record Superchat(
       String amount,
       String color,
       Optional<ImageItem> sticker
    ) {}

    public record ImageItem(
            String url,
            String alt
    ) {}
}
