package tech.yildirim.aiinsurance.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

/**
 * Service class for loading and managing application resources. This service provides utility
 * methods to load resources from the classpath and convert them to string format.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

  private static final String DEFAULT_PROMPT_RESOURCE = "ai/defaultPrompt.txt";

  private final ResourceLoader resourceLoader;

  /**
   * Loads a resource from the classpath and converts it to a string.
   *
   * @param path the relative path to the resource in the classpath
   * @return the content of the resource as a UTF-8 encoded string
   * @throws IOException if the resource cannot be found or read
   */
  public String loadResourceAsString(String path) throws IOException {
    log.debug("Loading resource from path: {}", path);

    try {
      Resource resource = resourceLoader.getResource("classpath:" + path);
      if (!resource.exists()) {
        log.error("Resource not found at path: {}", path);
        throw new IOException("Resource not found: " + path);
      }
      String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
      log.debug(
          "Successfully loaded resource from path: {}, content length: {}", path, content.length());
      return content;
    } catch (IOException e) {
      log.error("Failed to load resource from path: {}", path, e);
      throw e;
    }
  }

  /**
   * Loads the default AI prompt from the predefined resource location. This is a convenience method
   * that loads the default prompt file used by the AI system.
   *
   * @return the content of the default prompt file as a string
   * @throws IOException if the default prompt resource cannot be found or read
   */
  public String loadDefaultPrompt() throws IOException {
    log.debug("Loading default prompt from: {}", DEFAULT_PROMPT_RESOURCE);
    return loadResourceAsString(DEFAULT_PROMPT_RESOURCE);
  }
}
