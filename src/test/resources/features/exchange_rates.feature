Feature: Exchange Rates

  Scenario: Kursy walut
    Given I fetched the exchange rates
    When The exchange rates are up to date
    Then I display the rate for currency code "USD"
    And I display the rate for currency name "dolar amerykański"
    And I display currencies with rates above 5
    And I display currencies with rates below 3

  Scenario: Kurs jena JPY
    Given I fetched the exchange rates
    When The exchange rates are up to date
    Then I display the rate for currency code "JPY"
    And I display the rate for currency name "jen (Japonia)"