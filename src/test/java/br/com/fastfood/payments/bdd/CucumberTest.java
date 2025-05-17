package br.com.fastfood.payments.bdd;

import br.com.fastfood.payments.gateways.client.PagamentoClient;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberTest {
    @MockBean
    private PagamentoClient pagamentoClient;
}
