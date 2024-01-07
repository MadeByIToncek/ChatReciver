using ChatReciver.utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;

namespace ChatReciver
{
    abstract class LiveChat
    {
        public abstract void Start(string liveId);
        public abstract void End(string? reason);
        public abstract void Chat(ChatItem chatItem);
        public abstract void Error(string error);

        private string? _liveId;
        private readonly double interval;
        private static LivePageData? _data;
        private static System.Timers.Timer? _timer;

        public LiveChat(string liveId, double interval)
        {
            this._liveId = liveId;
            this.interval = interval;
        }

        public async void Startup()
        {
            if (_liveId == null) throw new ArgumentException("LiveID cannot was null!");
            LivePageData livePageData = await Requests.FetchLivePage(_liveId);
            _liveId = livePageData.liveId;
            _data = livePageData;

            _timer = new System.Timers.Timer(interval);
            _timer.Elapsed += Execute;
            _timer.AutoReset = true;
            _timer.Enabled = true;

            if (_liveId == null) throw new ArgumentException("LiveID cannot was null!");
            Start(_liveId);
        }

        public void Stop(string reason)
        {
            if (_data != null && _timer != null)
            {
                _timer.Stop();
                _timer.Dispose();
                _timer = null;
                End(reason);
            }
        }

        async private void Execute(object? source, ElapsedEventArgs e)
        {
            if (_data == null)
            {
                string msg = "Options not found";
                Error(msg);
                Stop(msg);
                return;
            }
            ChatData data = await Requests.FetchChat(_data);
            foreach (ChatItem chatItem in data.chatItems)
            {
                Chat(chatItem);
            }

            _data.continuation = data.continuation;
        }
    }
}
