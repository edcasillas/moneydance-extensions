package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Utils;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.view.HomePageView;

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
        // TODO How do I get a nice-looking box like the "Bank Accounts" widget?
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //panel.setBackground(MoneydanceLAF.homePageBorder.getFillColor());
        // Manually selected color; adjust as needed to match Moneydance's theme
        Color backgroundColor = new Color(230, 230, 230); // A light gray as an example
        panel.setBackground(backgroundColor);

        // Apply a border to mimic Moneydance's widget styling
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), // Line border color
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Padding

        // Create and style the title label
        JLabel titleLabel = new JLabel("Net Worth");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f)); // Adjust font size and style
        titleLabel.setForeground(new Color(104, 118, 120)); // Set text color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the title

        // Optional: add some padding around the title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Top, left, bottom, right padding

        panel.add(titleLabel);

        usdLabel = new JLabel("USD");
        usdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usdLabel);

        mxnLabel = new JLabel("MXN");
        mxnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(mxnLabel);

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
