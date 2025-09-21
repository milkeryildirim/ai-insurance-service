package tech.yildirim.aiinsurance.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseWrapper<T> {
  private boolean success;
  private T data;
  private String errorMessage;
}
