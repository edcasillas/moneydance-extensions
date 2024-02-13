package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Utils;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.view.HomePageView;
import com.moneydance.apps.md.view.gui.MoneydanceLAF;

import javax.swing.*;
import java.awt.*;

public class SampleWidget implements HomePageView {
    private final FeatureModuleContext context;
    private JPanel panel;
    private JLabel usdLabel;
    private JLabel mxnLabel;

    public SampleWidget(FeatureModuleContext context) {
        this.context = context;
        createUI();
    }

    private void createUI() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(MoneydanceLAF.homePageBorder.getFillColor());

        usdLabel = new JLabel("USD");
        panel.add(usdLabel, BorderLayout.CENTER);

        mxnLabel = new JLabel("MXN");
        panel.add(mxnLabel, BorderLayout.AFTER_LAST_LINE);

        refresh();
    }

    @Override
    public String getID() {
        return "Sample Widget";
    }

    @Override
    public JComponent getGUIView(AccountBook accountBook) {
        return panel;
    }

    @Override
    public void setActive(boolean b) {

    }

    @Override
    public void refresh() {
        long balance = Utils.GetTotalBalance(context);
        CurrencyType baseCurrency = Utils.GetBaseCurrency(context);

        if(baseCurrency == null) {
            // TODO Handle null
            return;
        }

        usdLabel.setText(Utils.FormatSuperFancy(balance, baseCurrency));

        CurrencyType pesoCurrency = Utils.GetCurrencyByID(context,"MXN");
        long convertedBalance = Utils.ConvertBalance(balance, baseCurrency, pesoCurrency);

        if(pesoCurrency == null) {
            // TODO Handle null
            return;
        }

        mxnLabel.setText(Utils.FormatSuperFancy(convertedBalance, pesoCurrency));
    }

    @Override
    public void reset() {

    }
}
