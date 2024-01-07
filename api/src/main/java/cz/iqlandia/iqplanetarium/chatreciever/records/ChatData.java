package cz.iqlandia.iqplanetarium.chatreciever.records;

import java.util.List;

public record ChatData(List<ChatItem> data, String continuation) {
}
