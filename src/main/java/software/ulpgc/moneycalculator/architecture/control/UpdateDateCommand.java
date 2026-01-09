package software.ulpgc.moneycalculator.architecture.control;

import software.ulpgc.moneycalculator.application.date.WebService;
import software.ulpgc.moneycalculator.architecture.ui.CurrencyDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDialog;
import software.ulpgc.moneycalculator.architecture.ui.RateDisplay;

public class UpdateDateCommand implements Command{
    private final MoneyDialog moneyDialog;
    private final CurrencyDialog currencyDialog;
    private final WebService.ExchangeRateLoader exchangeRateLoader;
    private final RateDisplay rateDisplay;

    public UpdateDateCommand(MoneyDialog moneyDialog, CurrencyDialog currencyDialog, WebService.ExchangeRateLoader exchangeRateLoader, RateDisplay rateDisplay) {
        this.moneyDialog = moneyDialog;
        this.currencyDialog = currencyDialog;
        this.exchangeRateLoader = exchangeRateLoader;
        this.rateDisplay = rateDisplay;
    }

    @Override
    public void execute() {
        rateDisplay.show(exchangeRateLoader.load(moneyDialog.get().currency(), currencyDialog.get()));
    }
}
