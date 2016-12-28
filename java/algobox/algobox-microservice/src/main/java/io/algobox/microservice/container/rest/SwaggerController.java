package io.algobox.microservice.container.rest;

import io.swagger.annotations.Api;
import org.glassfish.grizzly.http.util.MimeType;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Sends the Swagger static files. Similar behaviour to
 * {@link org.glassfish.grizzly.http.server.CLStaticHttpHandler}.
 */
@Api(hidden = true)
@Path("/swagger")
@PermitAll
@Singleton
public final class SwaggerController {
  private static final String SWAGGER_BASE_PATH = "static/swagger/";
  private static final CacheControl DEFAULT_CACHE_CONTROL =
      CacheControl.valueOf("public, max-age=" + 60 * 60 * 24 * 10);

  private final ClassLoader classLoader = SwaggerController.class.getClassLoader();

  @GET
  public Response getIndexContent() throws Exception {
    URI uri = new URI("/swagger/index.html");
    return Response.temporaryRedirect(uri).build();
  }

  @GET
  @Path(value = "/{path : .+}")
  public Response getContent(@PathParam(value = "path") String path) throws Exception {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    URL url = classLoader.getResource(SWAGGER_BASE_PATH + path);
    if (url == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    } else {
      switch (url.getProtocol()) {
        case "file":
          return sendFileContent(url);

        case "jar":
          return sendJarContent(url);

        default:
          throw new IllegalArgumentException(
              String.format("Unsupported protocol [%s].", url.getProtocol()));
      }
    }
  }

  private Response sendJarContent(URL url) throws IOException, URISyntaxException {
    JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
    JarEntry jarEntry = jarConnection.getJarEntry();
    JarFile jarFile = jarConnection.getJarFile();
    InputStream stream = jarFile.getInputStream(jarEntry);
    return sendResponse(url, stream);
  }

  private Response sendFileContent(URL url) throws FileNotFoundException, URISyntaxException {
    InputStream stream = new FileInputStream(url.getFile());
    return sendResponse(url, stream);
  }

  private Response sendResponse(URL url, InputStream stream) {
    return Response.ok()
        .cacheControl(DEFAULT_CACHE_CONTROL)
        .entity(stream)
        .type(getMediaType(url.getFile()))
        .build();
  }

  private String getMediaType(String path) {
    int dot = path.lastIndexOf('.');
    if (dot > 0) {
      String extension = path.substring(dot + 1);
      // TODO (robertom): Avoid to use grizzly libraries to get mime type.
      return MimeType.get(extension);
    } else {
      return MediaType.TEXT_HTML;
    }
  }
}
