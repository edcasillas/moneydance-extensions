package com.ecasillas.moneydance;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyTable;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.apps.md.controller.FeatureModuleContext;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.pow;

public class Utils {
    public static void IterateSubAccounts(Account parent, ActionWithArg<Account> action) {
        int sz = parent.getSubAccountCount();
        for(int i=0; i<sz; i++) {
            Account acct = parent.getSubAccount(i);
            Account.AccountType acctType = acct.getAccountType();

            if(acctType == Account.AccountType.EXPENSE || acctType == Account.AccountType.INCOME){
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
}
