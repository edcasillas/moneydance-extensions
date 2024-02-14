package com.ecasillas.moneydance;

import com.infinitekind.moneydance.model.*;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.Main;
import com.moneydance.apps.md.view.MoneydanceUI;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.pow;

public class Utils {
    public static Main GetMain(FeatureModuleContext context) {
        if(context instanceof Main) return (Main) context;
        return null;
    }

    public static MoneydanceGUI GetGUI(FeatureModuleContext context) {
        Main main = GetMain(context);
        if(main != null) {
            MoneydanceUI ui = main.getUI();
            if(ui instanceof MoneydanceGUI){
                return (MoneydanceGUI) ui;
            }
        }
        return null;
    }

    public static void IterateSubAccounts(Account parent, ActionWithArg<Account> action) {
        int sz = parent.getSubAccountCount();
        for(int i=0; i<sz; i++) {
            Account acct = parent.getSubAccount(i);

            if(!acct.shouldBeIncludedInNetWorth()){ // Excludes EXPENSE, INCOME and ROOT account types.
                continue;
            }
            action.Invoke(acct);
            IterateSubAccounts(acct, action);
        }
    }

    public static long ConvertBalance(long balance, CurrencyType from, CurrencyType to) {
        if (from == to) {
            return balance;
        }
        double convertibleBalance = balance / pow(10, from.getDecimalPlaces());
        double convertedBalance = to.getRate(from) * convertibleBalance;
        return to.parse(Double.toString(convertedBalance), '.');
    }

    /**
     * Retrieves the total balance of all accounts in the main currency.
     * @param context
     * @return
     */
    public static long GetTotalBalance(FeatureModuleContext context){
        AccountBook book = context.getCurrentAccountBook();
        if(book == null) return 0;

        CurrencyTable currencies = book.getCurrencies();
        CurrencyType mainCurrency = currencies.getBaseType();

        AtomicLong totalBalance = new AtomicLong();

        IterateSubAccounts(book.getRootAccount(), account -> {
            long b = ConvertBalance(account.getBalance(), account.getCurrencyType(), mainCurrency);
            totalBalance.addAndGet(b);
        });

        return totalBalance.get();
    }

    public static CurrencyType GetBaseCurrency(FeatureModuleContext context) {
        AccountBook book = context.getCurrentAccountBook();
        if(book == null) return null;

        CurrencyTable currencies = book.getCurrencies();
        return currencies.getBaseType();
    }

    public static CurrencyType GetCurrencyByID(FeatureModuleContext context, String currencyID) {
        AccountBook book = context.getCurrentAccountBook();
        if(book == null) return null;

        CurrencyTable currencies = book.getCurrencies();
        return currencies.getCurrencyByIDString(currencyID);
    }

    public static String FormatSuperFancy(long amount, CurrencyType currency) {
        return currency.getIDString() + currency.formatFancy(amount, '.');
    }
}
