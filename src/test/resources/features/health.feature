Feature: Health component
  As a platform operator
  I want a health endpoint
  So that Cloud Run and the gateway can probe the service

  Scenario: health reports ok
    When I GET "/health"
    Then the response status is 200
    And the JSON field "status" is "ok"
    And the JSON field "service" is "catalog-service"
