package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.tmpl.ExchangeRates;

import java.time.LocalDate;
import java.util.List;

public class ExchangeRatesSteps {
    private final ExchangeRates response = ExchangeRates.getInstance();

    @Given("I fetched the exchange rates")
    public void i_fetched_the_exchange_rates() {
        Assertions.assertNotNull(response);
    }

    @When("The exchange rates are up to date")
    public void the_exchange_rates_are_up_to_date() {
        Assertions.assertEquals(LocalDate.now().minusDays(1), response.getDate());
    }

    @Then("I display the rate for currency code {string}")
    public void i_display_the_rate_for_currency_code(String code) {
        double rate = response.getRateByCode(code);
        Assertions.assertTrue(rate != -1, String.format("Currency code '%s' not found!%n", code));

        System.out.println("Rate for currency code " + code + ": " + rate);
    }

    @Then("I display the rate for currency name {string}")
    public void i_display_the_rate_for_currency_name(String name) {
        double rate = response.getRateByName(name);
        Assertions.assertTrue(rate != -1, String.format("Currency name '%s' not found!%n", name));
        System.out.println("Rate for currency name " + name + ": " + rate);
    }

    @Then("I display currencies with rates above {double}")
    public void i_display_currencies_with_rates_above(double threshold) {
        List<ExchangeRates.Rate> currencies = response.getRatesAbove(threshold);
        if (!currencies.isEmpty()) {
            System.out.println("Currencies with rates above " + threshold + ": " + currencies);
        } else {
            System.out.printf("No currency with rate below %.2f was found!%n", threshold);
        }
    }

    @Then("I display currencies with rates below {double}")
    public void i_display_currencies_with_rates_below(double threshold) {
        List<ExchangeRates.Rate> currencies = response.getRatesBelow(threshold);
        if (!currencies.isEmpty()) {
            System.out.println("Currencies with rates below " + threshold + ": " + currencies);
        } else {
            System.out.printf("No currency with rate below %.2f was found!%n", threshold);
        }
    }
}
