/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ccf.transport.http

import java.io.IOException
import java.net.URL

import ccf.transport.Connection
import ccf.transport.json.{JsonEncoder, JsonDecoder}
import ccf.transport.{ConnectionException, InvalidRequestException}
import ccf.transport.{Encoder, Decoder}
import ccf.transport.{Request, Response}

object HttpConnection {
  private val timeoutMillis = 1000
  def create(url: URL) = new HttpConnection(url, new DispatchHttpClient(timeoutMillis), JsonDecoder, JsonEncoder)
}

class HttpConnection(url: URL, client: HttpClient, decoder: Decoder, encoder: Encoder) extends Connection {
  def send(request: Request): Option[Response] = try {
    decoder.decodeResponse(post(request))
  } catch {
    case e: IOException => throw new ConnectionException(e.toString)
  }
  private def post(request: Request) = client.post(requestUrl(request), encoder.encodeRequest(request))
  private def requestUrl(request: Request) = new URL(url, request.header("type").getOrElse(requestTypeMissing))
  private def requestTypeMissing = throw new InvalidRequestException("Request header \"type\" missing")
}
