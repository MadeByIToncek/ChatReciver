using System.Net;

namespace ChatReciver.utils
{
    public class ChatData
    {
        public ChatItem[] chatItems;
        public string continuation;
    }

    public class ChatItem
    {
        public string id;
        public Author author;
        public string message;
        public Superchat? superchat;
        public bool isMembership;
        public bool isVerified;
        public bool isOwner;
        public bool isModerator;
        public DateTime timestamp;
    }

    public class Author
    {
        public string name;
        public string thumbnail;
        public string channelID;
        public string badgeThumbnail;
    }

    public class Superchat
    {
        public string amount;
        public string color;
        public string sticker;
    }
}