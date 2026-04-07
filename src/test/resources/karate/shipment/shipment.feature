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
    And request { senderName: 'Test', senderCity: 'City', recipientName: 'Test2', recipientCity: 'City2', weightKg: 1.0 }
    When method POST
    Then status 201
    And match response.trackingId == '#notnull'
    And match response.trackingId == '#regex PQ-\\d{8}-[A-Z0-9]{6}'

  Scenario: CP-03-01 - Get existing shipment by ID
    # First create
    Given path '/api/shipments'
    And request { senderName: 'Test', senderCity: 'City', recipientName: 'Test2', recipientCity: 'City2', weightKg: 1.0 }
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
