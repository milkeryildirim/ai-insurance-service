package tech.yildirim.aiinsurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiInsuranceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AiInsuranceApplication.class, args);
  }

}
