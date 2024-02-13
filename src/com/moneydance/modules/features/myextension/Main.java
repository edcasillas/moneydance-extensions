/************************************************************\
 *      Copyright (C) 2016 The Infinite Kind, Limited       *
\************************************************************/

package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Utils;
import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyTable;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;

import java.awt.*;
import java.io.ByteArrayOutputStream;

/** Pluggable module used to give users access to a Account List
    interface to Moneydance.
*/

public class Main
  extends FeatureModule
{
  private ConsoleWindow consoleWindow = null;

  public void init() {
    // the first thing we will do is register this module to be invoked
    // via the application toolbar
    FeatureModuleContext context = getContext();
    try {
      context.registerFeature(this, "showconsole",
        getIcon("accountlist"),
        getName());
      context.registerHomePageView(this, new SampleWidget(context));
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public void cleanup() {
    closeConsole();
  }
  
  private Image getIcon(String action) {
    try {
      ClassLoader cl = getClass().getClassLoader();
      java.io.InputStream in = 
        cl.getResourceAsStream("/com/moneydance/modules/features/myextension/icon.gif");
      if (in != null) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
        byte buf[] = new byte[256];
        int n = 0;
        while((n=in.read(buf, 0, buf.length))>=0)
          bout.write(buf, 0, n);
        return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
      }
    } catch (Throwable e) { }
    return null;
  }
  
  /** Process an invokation of this module with the given URI */
  public void invoke(String uri) {
    String command = uri;
    String parameters = "";
    int theIdx = uri.indexOf('?');
    if(theIdx>=0) {
      command = uri.substring(0, theIdx);
      parameters = uri.substring(theIdx+1);
    }
    else {
      theIdx = uri.indexOf(':');
      if(theIdx>=0) {
        command = uri.substring(0, theIdx);
      }
    }

    if(command.equals("showconsole")) {
      showConsole();
    }    
  }

  public String getName() {
    return "Account List";
  }

  private synchronized void showConsole() {
    if(consoleWindow ==null) {
      StringBuffer sb = getAccountsStringBuffer();
      //StringBuffer sb = new StringBuffer();
      //double netWorth = calculateNetWorthInPesos(sb);
      consoleWindow = new ConsoleWindow("Account List Console", this::closeConsole, sb);
      consoleWindow.setVisible(true);
    }
    else {
      consoleWindow.setVisible(true);
      consoleWindow.toFront();
      consoleWindow.requestFocus();
    }
  }

  synchronized void closeConsole() {
    if(consoleWindow !=null) {
      consoleWindow.goAway();
      consoleWindow = null;
      System.gc();
    }
  }

  private StringBuffer getAccountsStringBuffer() {
    AccountBook book = getContext().getCurrentAccountBook();
    StringBuffer acctStr = new StringBuffer();
    if(book != null) {
      Account rootAccount = book.getRootAccount();
      acctStr.append(rootAccount.getFullAccountName());
      acctStr.append("\n");

      addSubAccounts(book.getRootAccount(), acctStr, 1);

      acctStr.append("\n");
      CurrencyTable currencies = book.getCurrencies();
      CurrencyType mainCurrency = currencies.getBaseType();
      CurrencyType pesoCurrency = currencies.getCurrencyByIDString("MXN");

      acctStr.append("Main currency: " + mainCurrency.getIDString() + "\n");
      acctStr.append("Main to Peso rate: " + mainCurrency.getRate(pesoCurrency) + "\n");
      acctStr.append("Peso to main rate: " + pesoCurrency.getRate(mainCurrency) + "\n");

      long totalBalance = Utils.GetTotalBalance(getContext());

      acctStr.append("\n");
      acctStr.append("TOTAL");
      acctStr.append(" ");
      acctStr.append(mainCurrency.getIDString());
      acctStr.append(mainCurrency.formatFancy(totalBalance, '.'));

      acctStr.append("\n");
      acctStr.append(" ");
      acctStr.append(pesoCurrency.getIDString());
      acctStr.append(pesoCurrency.formatFancy(Utils.ConvertBalance(totalBalance, mainCurrency, pesoCurrency), '.'));
    }
    return acctStr;
  }

  public void addSubAccounts(Account parentAcct, StringBuffer acctStr, int indentLevel) {
    int sz = parentAcct.getSubAccountCount();
    for(int i=0; i<sz; i++) {
      Account acct = parentAcct.getSubAccount(i);
      Account.AccountType acctType = acct.getAccountType();

      if(acctType == Account.AccountType.EXPENSE || acctType == Account.AccountType.INCOME){
        continue;
      }

      acctStr.append(" ".repeat(Math.max(0, indentLevel + 1)));
      appendAccountData(acct, acctStr);

      acctStr.append("\n");
      addSubAccounts(acct, acctStr, indentLevel + 1);
    }
  }

  private void appendAccountData(Account account, StringBuffer sb){
    CurrencyType currencyType = account.getCurrencyType();
    long balance = account.getBalance();

    sb.append(account.getFullAccountName());
    sb.append(" (");
    sb.append(account.getAccountType());
    sb.append("): ");
    sb.append(currencyType.getIDString());
    sb.append(currencyType.formatFancy(balance, '.'));

    CurrencyType mainCurrency = getMainCurrencyType();
    if(currencyType != mainCurrency) {
      sb.append(" - ");
      long parsedConvertedBalance = Utils.ConvertBalance(balance, currencyType, mainCurrency);
      sb.append(mainCurrency.getIDString());
      sb.append(mainCurrency.formatFancy(parsedConvertedBalance, '.'));
    }
  }

  private CurrencyType getMainCurrencyType(){
    AccountBook book = getContext().getCurrentAccountBook();
    CurrencyTable currencies = book.getCurrencies();
    CurrencyType mainCurrency = currencies.getBaseType();
    return mainCurrency;
  }
}


