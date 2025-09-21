package tech.yildirim.aiinsurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AiInsuranceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AiInsuranceApplication.class, args);
  }

}
