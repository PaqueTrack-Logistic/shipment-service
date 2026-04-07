@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> health() {
        return Map.of("service", "shipment-service", "status", "UP");
    }
}
