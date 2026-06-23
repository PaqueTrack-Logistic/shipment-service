Feature: Shipment Management API

  Background:
    * url baseUrl

  Scenario: CP-01-01 - Create shipment with valid data
    Given path '/api/shipments'
    And request { senderName: 'Juan Pérez', senderAddress: 'Calle 1', senderCity: 'Medellín', recipientName: 'María López', recipientAddress: 'Cra 2', recipientCity: 'Bogotá', weightKg: 2.5 }
    When method POST
    Then status 201
    And match response.id == '#notnull'
    And match response.trackingId == '#regex PQ-\\d{8}-[A-Z0-9]{6}'
    And match response.status == 'CREATED'
    And match response.senderName == 'Juan Pérez'
    And match response.recipientName == 'María López'

  Scenario: CP-01-02 - Create shipment with missing required fields
    Given path '/api/shipments'
    And request { senderAddress: 'Calle 1', senderCity: 'Medellín' }
    When method POST
    Then status 400

  Scenario: CP-02-01 - Tracking ID is generated automatically
    Given path '/api/shipments'
    And request { senderName: 'Test', senderAddress: 'Calle 1', senderCity: 'City', recipientName: 'Test2', recipientAddress: 'Cra 2', recipientCity: 'City2', weightKg: 1.0 }
    When method POST
    Then status 201
    And match response.trackingId == '#notnull'
    And match response.trackingId == '#regex PQ-\\d{8}-[A-Z0-9]{6}'

  Scenario: CP-03-01 - Get existing shipment by ID
    # First create
    Given path '/api/shipments'
    And request { senderName: 'Test', senderAddress: 'Calle 1', senderCity: 'City', recipientName: 'Test2', recipientAddress: 'Cra 2', recipientCity: 'City2', weightKg: 1.0 }
    When method POST
    Then status 201
    * def shipmentId = response.id
    # Then get
    Given path '/api/shipments', shipmentId
    When method GET
    Then status 200
    And match response.id == shipmentId

  Scenario: CP-03-02 - Get non-existing shipment
    Given path '/api/shipments/non-existent-id'
    When method GET
    Then status 404

  Scenario: CP-04-01 - Get shipment by tracking id
    Given path '/api/shipments'
    And request { senderName: 'Rastreo', senderAddress: 'Calle 9', senderCity: 'Cali', recipientName: 'Dest', recipientAddress: 'Cra 3', recipientCity: 'Cartagena', weightKg: 4.0 }
    When method POST
    Then status 201
    * def trackingId = response.trackingId
    Given path '/api/shipments/tracking', trackingId
    When method GET
    Then status 200
    And match response.trackingId == trackingId

  Scenario: CP-04-02 - Get by non-existing tracking id returns 404
    Given path '/api/shipments/tracking/PQ-00000000-NOEXIS'
    When method GET
    Then status 404

  Scenario: CP-05-01 - Search by sender name returns list
    Given path '/api/shipments'
    And request { senderName: 'BuscablePorRemitente', senderAddress: 'X', senderCity: 'Y', recipientName: 'Z', recipientAddress: 'W', recipientCity: 'V', weightKg: 1.0 }
    When method POST
    Then status 201
    Given path '/api/shipments/search'
    And param senderName = 'BuscablePorRemitente'
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: CP-05-02 - Search with both params returns 400
    Given path '/api/shipments/search'
    And param senderName = 'Juan'
    And param recipientName = 'Maria'
    When method GET
    Then status 400

  Scenario: CP-05-03 - Search with no params returns 400
    Given path '/api/shipments/search'
    When method GET
    Then status 400

  Scenario: CP-06-01 - Report with valid date range
    Given path '/api/shipments/report'
    And param from = '2026-01-01'
    And param to = '2026-12-31'
    When method GET
    Then status 200
    And match response.totalGeneral == '#number'

  Scenario: CP-06-02 - Report with from after to returns 400
    Given path '/api/shipments/report'
    And param from = '2026-12-31'
    And param to = '2026-01-01'
    When method GET
    Then status 400

  Scenario: CP-07-01 - Get shipment history
    Given path '/api/shipments'
    And request { senderName: 'ConHistorial', senderAddress: 'X', senderCity: 'Y', recipientName: 'Z', recipientAddress: 'W', recipientCity: 'V', weightKg: 1.0 }
    When method POST
    Then status 201
    * def shipmentId = response.id
    Given path '/api/shipments', shipmentId, 'history'
    When method GET
    Then status 200
