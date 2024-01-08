package cgtester;

import java.awt.Component;
import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class TesterWindow extends JFrame {
    
    Component gLPanel;
    
    public TesterWindow(Component glPanel) {
        this.gLPanel = glPanel;
        
        initComponents();
        
        setLayout();
    }
    
    private void initComponents() {
    }
    
    private void setLayout() {
        Container contentPane = getContentPane();
        GroupLayout l = new GroupLayout(contentPane);
        contentPane.setLayout(l);
        
        l.setHorizontalGroup(l.createSequentialGroup()
            .addGap(20)    
            .addComponent(gLPanel)
            .addGap(20)
        );
        l.setVerticalGroup(l.createSequentialGroup()
            .addGap(20)
            .addComponent(gLPanel)
            .addGap(10)
        );
    }
    
}
