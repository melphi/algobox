package io.algobox.connector.oanada;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.oanada.domain.OandaPriceTick;
import io.algobox.price.PriceTick;
import io.algobox.util.JsonUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class OandaConnectorHelper {
  private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
  private static final String TEXT_HEARTBEAT = "HEARTBEAT";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private static final String HEADER_AUTHORIZATION = "Authorization";
  private static final Set<Integer> VALID_RESPONSES =
      ImmutableSet.of(HttpStatus.SC_OK, HttpStatus.SC_CREATED);

  private final String apiUrl;
  private final String streamingApiUrl;
  private final Header authorizationHeader;
  private final String accountNumber;

  public OandaConnectorHelper(
      String accountNumber, String apiKey, String apiUrl, String streamingApiUrl) {
    checkNotNullOrEmpty(apiKey, "Missing api key.");
    checkNotNullOrEmpty(apiUrl, "Missing api url.");
    checkNotNullOrEmpty(streamingApiUrl, "Missing streaming api url.");
    this.accountNumber = checkNotNullOrEmpty(accountNumber);
    this.apiUrl = getUrlWithoutFinalSlash(apiUrl);
    this.streamingApiUrl = getUrlWithoutFinalSlash(streamingApiUrl);
    this.authorizationHeader = new BasicHeader(HEADER_AUTHORIZATION, "Bearer " + apiKey);
  }

  public static long parseTimestamp(String date) {
    int year = Integer.parseInt(date.substring(0, 4));
    int month = Integer.parseInt(date.substring(5, 7));
    int day = Integer.parseInt(date.substring(8, 10));
    int hour = Integer.parseInt(date.substring(11, 13));
    int minute = Integer.parseInt(date.substring(14, 16));
    int seconds = Integer.parseInt(date.substring(17, 19));
    int nanoseconds = Integer.parseInt(date.substring(20, 29));
    return ZonedDateTime.of(year, month, day, hour, minute, seconds, nanoseconds, ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli();
  }

  public static PriceTick parsePriceLine(String line) {
    try {
      OandaPriceTick oandaPriceTick = JsonUtils.fromJson(line, OandaPriceTick.class);
      if (oandaPriceTick != null) {
        long time = parseTimestamp(oandaPriceTick.getTime());
        double ask = Double.parseDouble(oandaPriceTick.getCloseoutAsk());
        double bid = Double.parseDouble(oandaPriceTick.getCloseoutBid());
        return new PriceTick(oandaPriceTick.getInstrument(), time, ask, bid);
      } else {
        return null;
      }
    } catch (Exception e) {
      if (line == null || line.contains(TEXT_HEARTBEAT)) {
        return null;
      } else {
        throw new IllegalArgumentException(String.format(
            "Error while parsing line [%s]: [%s]", line, e.getMessage()), e);
      }
    }
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public <T> T doGet(String path, Class<T> clazz) throws ConnectorException {
    HttpGet httpGet = new HttpGet(getUrl(path));
    httpGet.addHeader(authorizationHeader);
    return executeAndValidate(httpGet, clazz);
  }

  public <T> T doPost(String path, Class<T> clazz, Object data) throws ConnectorException {
    checkNotNull(data);
    HttpPost httpPost = new HttpPost(getUrl(path));
    httpPost.addHeader(authorizationHeader);
    httpPost.addHeader(HEADER_CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    httpPost.setEntity(new StringEntity(JsonUtils.toJson(data), StandardCharsets.UTF_8));
    return executeAndValidate(httpPost, clazz);
  }

  public void doPut(String path) throws ConnectorException {
    doPut(path, null);
  }

  public void doPut(String path, Object data) throws ConnectorException {
    HttpPut httpPut = new HttpPut(getUrl(path));
    httpPut.addHeader(authorizationHeader);
    httpPut.addHeader(HEADER_CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    if (data != null) {
      httpPut.setEntity(new StringEntity(JsonUtils.toJson(data), StandardCharsets.UTF_8));
    }
    executeAndValidate(httpPut);
  }

  public CloseableHttpResponse getPricesStream(Collection<String> newInstruments)
      throws IOException {
    String instruments = Joiner.on("%2C").join(newInstruments);
    String url = String.format("%s/v3/accounts/%s/pricing/stream?instruments=%s&snapshot=False",
        streamingApiUrl, accountNumber, instruments);
    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader(authorizationHeader);
    CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet);
    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
      response.close();
      throw new IllegalArgumentException(String.format(
          "Invalid status code [%s].", response.getStatusLine().getStatusCode()));
    }
    return response;
  }

  private String getUrl(String path) {
    checkArgument(path.startsWith("/"));
    return apiUrl + path;
  }

  private <T> T executeAndValidate(HttpRequestBase httpRequest) throws ConnectorException {
    return executeAndValidate(httpRequest, null);
  }

  private <T> T executeAndValidate(HttpRequestBase httpRequest, Class<T> clazz)
      throws ConnectorException {
    CloseableHttpResponse response = null;
    String content;
    try {
      response = HTTP_CLIENT.execute(httpRequest);
      content = EntityUtils.toString(response.getEntity());
    } catch (Exception e) {
      String message = String.format("Error while executing http request [%s].", e.getMessage());
      throw new ConnectorException(message, e);
    }
    if (!VALID_RESPONSES.contains(response.getStatusLine().getStatusCode())) {
      String message = String.format("Invalid HTTP status [%d]: [%s]",
          response.getStatusLine().getStatusCode(), content);
      throw new ConnectorException(message);
    }
    if (clazz != null) {
      try {
        return JsonUtils.fromJson(content, clazz);
      } catch (Exception e) {
        throw new ConnectorException(e.getMessage(), e);
      }
    } else {
      return null;
    }
  }

  private String getUrlWithoutFinalSlash(String url) {
    checkNotNullOrEmpty(url);
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }
}
