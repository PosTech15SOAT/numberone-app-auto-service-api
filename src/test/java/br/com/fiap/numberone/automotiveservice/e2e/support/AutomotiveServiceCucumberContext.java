package br.com.fiap.numberone.automotiveservice.e2e.support;

import br.com.fiap.numberone.NumberoneApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
        classes = NumberoneApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class AutomotiveServiceCucumberContext {
}
