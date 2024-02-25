package cz.iqlandia.iqplanetarium.chatreciever.records;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record ChatItem(
        String id,
        Author author,
        String message,
		ZonedDateTime timestamp) implements Comparable<ChatItem> {

	public JSONObject serialize() {
		return new JSONObject()
				.put("id", id)
				.put("author", author.serialize())
				.put("message", message)
				.put("timestamp", timestamp.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
	}

	@Override
	public int compareTo(@NotNull ChatItem o) {
		if (this.timestamp.isBefore(o.timestamp)) {
			return -1;
		} else if (this.timestamp.isEqual(o.timestamp)) {
			return 0;
		} else {
			return 1;
		}
	}

	public record Author(
       String name,
       Optional<ImageItem> thumbnail,
       String channelID,
       Optional<Badge> badge,
       boolean isVerified,
       boolean isOwner,
       boolean isModerator
    ) {
		public JSONObject serialize() {
			JSONObject thumb = null;
			JSONObject badge = null;
			if (thumbnail.isPresent()) thumb = thumbnail.get().serialize();
			if (this.badge.isPresent()) badge = this.badge.get().serialize();
			return new JSONObject()
					.put("name", name)
					.put("thumbnail", thumb)
					.put("channelID", channelID)
					.put("badge", badge)
					.put("isVerified", isVerified)
					.put("isOwner", isOwner)
					.put("isModerator", isModerator);
		}

		public record Badge(
                ImageItem thumbnail,
				String label) {
			public JSONObject serialize() {
				return new JSONObject()
						.put("thumbnail", thumbnail.serialize())
						.put("label", label);
			}
		}
    }

    public record ImageItem(
            String url,
            String alt
	) {
		public JSONObject serialize() {
			return new JSONObject()
					.put("url", url)
					.put("alt", alt);
		}
	}
}
