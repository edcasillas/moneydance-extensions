package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Utils;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.view.HomePageView;
import com.moneydance.apps.md.view.gui.MoneydanceLAF;

import javax.swing.*;
import java.awt.*;

public class SampleWidget implements HomePageView {
    private FeatureModuleContext context;
    private JPanel panel;
    private JLabel label;

    public SampleWidget(FeatureModuleContext context) {
        this.context = context;
        createUI();
    }

    private void createUI() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(MoneydanceLAF.homePageBorder.getFillColor());
        long balance = Utils.GetTotalBalance(context);
        label = new JLabel("This will show something " + balance);
        panel.add(label, BorderLayout.CENTER);
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
        // TODO Change contents of label
    }

    @Override
    public void reset() {

    }
}
