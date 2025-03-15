## http server
A lightweight HTTP server implementation that supports:

- **Concurrent Request Handling**: Uses a thread pool architecture to process multiple requests simultaneously
- **Dynamic Thread Pool Sizing**: Automatically scales to available processor cores


There are 3 main components:
1. **BasicHttpServer**: Manages the server socket and thread pool for request handling
2. **HttpTask**: Handles individual HTTP requests and generates responses
3. **HttpMessageParser**: Parses HTTP requests and builds HTTP responses


Just run it and make some requests to the port `8999`