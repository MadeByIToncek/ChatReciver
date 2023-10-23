using Newtonsoft.Json.Linq;
using System.Net;
using System.Net.Http.Headers;
using System.Text;

namespace ChatReciver.utils
{
    internal class HTTPManager
    {
        private HttpClient _client;
        public HTTPManager(HttpClient _client) {
            this._client = _client;
        }

        //Done
        public async Task<string> GetAsyncString(string uri)
        {
            return await _client.GetStringAsync(uri);
        }

        //Done
        public async Task<JObject> GetAsyncJObject(string uri)
        {
            return JObject.Parse((await _client.GetStringAsync(uri)));
        }

        public async Task<string> PostAsync(string uri, string body)
        {
            var content = new StringContent(body, Encoding.UTF8, MediaTypeHeaderValue.Parse(""));
            var response = await _client.PostAsync(uri, content);
            return await response.Content.ReadAsStringAsync();
        }
        public async Task<JObject> PostAsyncJObject(string uri, string body)
        {
            var content = new StringContent(body, Encoding.UTF8, MediaTypeHeaderValue.Parse(""));
            var response = await _client.PostAsync(uri, content);
            return JObject.Parse(response.Content.ReadAsStringAsync().Result);
        }
    }
}
