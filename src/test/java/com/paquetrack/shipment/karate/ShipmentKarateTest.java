package com.paquetrack.shipment.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ShipmentKarateTest {

    @LocalServerPort
    private int port;

    @Karate.Test
    Karate testShipment() {
        return Karate.run("classpath:karate/shipment/shipment.feature")
                .systemProperty("local.server.port", String.valueOf(port));
    }
}
