package perftest.server

import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.{HttpHandler, HttpExchange}
import java.net.InetSocketAddress

import ccf.transport.Response
import ccf.transport.json.JsonFormatter

class HttpRequestHandler extends HttpHandler {
  def handle(exchange: HttpExchange) { 
    try {
      val response = Response(Map[String, String](), Some((0 to 1023).map(x => 0).mkString("")))
      val body = JsonFormatter.formatResponse(response)
      exchange.sendResponseHeaders(200, body.length)
      exchange.getResponseBody.write(body.getBytes)
    } finally {
      exchange.getResponseBody.close
    }
  }
}

object Server {
  def main(args: Array[String]) {
    if (args.length > 0) run(args(0)) else println("Usage: Server [IP ADDR]")
  }
  private def run(ipAddress: String) {
    val server = HttpServer.create(new InetSocketAddress(ipAddress, 8080), 0)
    server.createContext("/perftest", new HttpRequestHandler)
    server.start
  }
}
