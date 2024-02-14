package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Utils;
import com.infinitekind.moneydance.model.AccountBook;
import com.infinitekind.moneydance.model.CurrencyType;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.controller.Main;
import com.moneydance.apps.md.controller.Util;
import com.moneydance.apps.md.view.HomePageView;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;
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
        //MoneydanceGUI gui = Utils.GetGUI(context);
        //com.moneydance.apps.md.view.gui.MDFonts fonts = gui.getFonts(); // FIXME Does not work because MDFonts is not public
        // if(gui == null) return; // TODO Handle error!

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

        titleLabel.setFont(//gui.getFonts()
                titleLabel.getFont().deriveFont(Font.BOLD, 14f)); // Adjust font size and style
        titleLabel.setForeground(new Color(104, 118, 120)); // Set text color
        //titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the title

        // Optional: add some padding around the title
        //titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Top, left, bottom, right padding

        panel.add(titleLabel, gbc);

        gbc.gridy++; // Move to the next row for the USD label
        gbc.insets = new Insets(5, 10, 5, 10); // Adjust padding as needed

        usdLabel = new JLabel("USD");
        //usdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usdLabel, gbc);

        gbc.gridy++; // Move to the next row for the MXN label

        mxnLabel = new JLabel("MXN");
        // mxnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(mxnLabel, gbc);

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
