package software.ulpgc.moneycalculator.application.date;

import software.ulpgc.moneycalculator.architecture.control.ExchangeMoneyCommand;
import software.ulpgc.moneycalculator.architecture.control.UpdateDateCommand;

public class Main {
    public static void main(String[] args) {
        Desktop desktop = new Desktop(new WebService.CurrencyLoader().loadAll());
        desktop.addCommand("exchange", new ExchangeMoneyCommand(
                desktop.moneyDialog(),
                desktop.currencyDialog(),
                new WebService.ExchangeRateLoader(),
                desktop.moneyDisplay()
        ));
        desktop.addCommand("update date", new UpdateDateCommand(
                desktop.moneyDialog(),
                desktop.currencyDialog(),
                new WebService.ExchangeRateLoader(),
                desktop.rateDateDisplay()
        ));
        desktop.setVisible(true);
    }
}
