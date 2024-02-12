/************************************************************\
 *      Copyright (C) 2016 The Infinite Kind, Limited       *
\************************************************************/

package com.moneydance.modules.features.myextension;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyTable;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;

import java.io.*;
import java.awt.*;

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
    if(book !=null) {
      Account rootAccount = book.getRootAccount();
      acctStr.append(rootAccount.getFullAccountName());
      acctStr.append("\n");
      addSubAccounts(book.getRootAccount(), acctStr, 1);
    }
    return acctStr;
  }

  public static void addSubAccounts(Account parentAcct, StringBuffer acctStr, int indentLevel) {
    int sz = parentAcct.getSubAccountCount();
    for(int i=0; i<sz; i++) {
      Account acct = parentAcct.getSubAccount(i);
      Account.AccountType acctType = acct.getAccountType();

      if(acctType == Account.AccountType.EXPENSE || acctType == Account.AccountType.INCOME){
        continue;
      }

      acctStr.append(" ".repeat(Math.max(0, indentLevel + 1)));
      appendAccountText(acctStr, acct);

      acctStr.append("\n");
      addSubAccounts(acct, acctStr, indentLevel + 1);
    }
  }

  private static void appendAccountText(StringBuffer sb, Account account){
    CurrencyType currencyType = account.getCurrencyType();

    sb.append(account.getFullAccountName());
    sb.append(" (");
    sb.append(account.getAccountType());
    sb.append("): ");
    sb.append(currencyType.formatFancy(account.getBalance(), '.'));
  }

  private double calculateNetWorthInPesos(StringBuffer sb) {
    double netWorthInPesos = 0.0;
    AccountBook book = getContext().getCurrentAccountBook();
    CurrencyTable currencies = book.getCurrencies();
    CurrencyType mainCurrency = currencies.getBaseType();
    CurrencyType pesoCurrency = currencies.getCurrencyByIDString("MXN"); // Assuming "MXN" is the correct ID for Mexican Pesos

    for (Account account : book.getRootAccount().getSubAccounts()) {
      double balance = account.getBalance();
      if (!account.getCurrencyType().equals(pesoCurrency)) {
        // Convert balance to pesos
        balance = mainCurrency.getRate(pesoCurrency) * balance;
      }
      netWorthInPesos += balance;
    }

    return netWorthInPesos;
  }

}


