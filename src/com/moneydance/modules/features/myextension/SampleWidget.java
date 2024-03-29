package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Utils;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyType;
import com.infinitekind.moneydance.model.NetWorthCalculator;
import com.infinitekind.moneydance.model.TotalAmount;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.view.HomePageView;
import com.moneydance.apps.md.view.gui.MDFonts;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;
import com.moneydance.apps.md.view.gui.MoneydanceLAF;

import javax.swing.*;
import java.awt.*;

public class SampleWidget implements HomePageView {
    private final FeatureModuleContext context;
    private JPanel panel;
    private JLabel usdLabel;
    private JLabel mxnLabel;
    private JLabel exchangeRateLabel;

    public SampleWidget(FeatureModuleContext context) {
        this.context = context;
        createUI();
    }

    private void createUI() {
        MoneydanceGUI mdGUI = Utils.GetGUI(context);
        if(mdGUI == null) return; // TODO Handle error!

        MDFonts fonts = mdGUI.getFonts();

        // TODO How do I get a nice-looking box like the "Bank Accounts" widget?
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(MoneydanceLAF.homePageBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Column
        gbc.gridy = 0; // Row
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Span across all columns
        gbc.anchor = GridBagConstraints.NORTH; // Anchor north (top of the space)
        gbc.insets = new Insets(10, 10, 5, 10); // Top, left, bottom, right padding for the title

        // Create and style the title label
        JLabel titleLabel = new JLabel("Net Worth");

        titleLabel.setFont(fonts.header);
        titleLabel.setForeground(new Color(104, 118, 120)); // Set text color

        panel.add(titleLabel, gbc);

        gbc.gridy++; // Move to the next row for the USD label
        gbc.insets = new Insets(5, 10, 5, 10); // Adjust padding as needed

        usdLabel = new JLabel("USD");
        usdLabel.setFont(fonts.defaultText);
        //usdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usdLabel, gbc);

        gbc.gridy++; // Move to the next row for the MXN label

        mxnLabel = new JLabel("MXN");
        // mxnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(mxnLabel, gbc);

        gbc.gridy++; // Move to the next row for the exchangeRateLabel
        gbc.gridy++; // Move to the next row for the exchangeRateLabel
        exchangeRateLabel = new JLabel("USD$ 1.00 = MXN$17");
        panel.add(exchangeRateLabel);

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
        NetWorthCalculator calculator = Utils.CreateNetworthCalculator(context);
        if(calculator == null) {
            // TODO Handle null
            return;
        }

        TotalAmount netWorth = calculator.calculateTotal();

        //netWorth.getCurrency().snap

        usdLabel.setText(Utils.FormatSuperFancy(netWorth.getAmount(), netWorth.getCurrency()));

        CurrencyType pesoCurrency = Utils.GetCurrencyByID(context,"MXN");
        // TODO Check null!

        TotalAmount netWorthInPesos = netWorth.convertToCurrency(pesoCurrency);
        mxnLabel.setText(Utils.FormatSuperFancy(netWorthInPesos.getAmount(), netWorthInPesos.getCurrency()));

        exchangeRateLabel.setText("USD$ 1.00 = MXN$ " + pesoCurrency.getRate(Utils.GetBaseCurrency(context)));
    }

    @Override
    public void reset() {

    }
}
