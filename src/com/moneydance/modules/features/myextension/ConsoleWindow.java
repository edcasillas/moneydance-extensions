package com.moneydance.modules.features.myextension;

import com.ecasillas.moneydance.Action;
import com.moneydance.awt.AwtUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleWindow
  extends JFrame
  implements ActionListener
{
  private JTextArea textArea;
  private JButton clearButton;
  private JButton closeButton;
  private JTextField inputArea;

  private Action onClose;

  public ConsoleWindow(String title, Action onClose, StringBuffer acctStr) {
    super(title);
    this.onClose = onClose;

    textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setText(acctStr.toString());
    inputArea = new JTextField();
    inputArea.setEditable(true);
    clearButton = new JButton("Clear");
    closeButton = new JButton("Close");

    JPanel p = new JPanel(new GridBagLayout());
    p.setBorder(new EmptyBorder(10,10,10,10));
    p.add(new JScrollPane(textArea), AwtUtil.getConstraints(0,0,1,1,4,1,true,true));
    p.add(Box.createVerticalStrut(8), AwtUtil.getConstraints(0,2,0,0,1,1,false,false));
    p.add(clearButton, AwtUtil.getConstraints(0,3,1,0,1,1,false,true));
    p.add(closeButton, AwtUtil.getConstraints(1,3,1,0,1,1,false,true));
    getContentPane().add(p);

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    enableEvents(WindowEvent.WINDOW_CLOSING);
    closeButton.addActionListener(this);
    clearButton.addActionListener(this);
        
    PrintStream c = new PrintStream(new ConsoleStream());

    setSize(500, 400);
    AwtUtil.centerWindow(this);
  }

  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if(src==closeButton) {
      onClose.Invoke();
    }
    if(src==clearButton) {
      textArea.setText("");
    }
  }

  public final void processEvent(AWTEvent evt) {
    if(evt.getID()==WindowEvent.WINDOW_CLOSING) {
      onClose.Invoke();
      return;
    }
    if(evt.getID()==WindowEvent.WINDOW_OPENED) {
    }
    super.processEvent(evt);
  }
  
  private class ConsoleStream
    extends OutputStream
    implements Runnable
  {    
    public void write(int b)
      throws IOException
    {
      textArea.append(String.valueOf((char)b));
      repaint();
    }

    public void write(byte[] b)
      throws IOException
    {
      textArea.append(new String(b));
      repaint();
    }
    public void run() {
      textArea.repaint();
    }
  }

  void goAway() {
    setVisible(false);
    dispose();
  }
}
