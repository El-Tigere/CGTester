package cgtester;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

import com.jogamp.opengl.awt.GLJPanel;

import cgtester.TesterState.VertexAttributes;

public class TesterWindow extends JFrame {
    
    private TesterState testerState;
    
    private GroupLayout l;
    
    private Component glJPanel;
    
    private ButtonGroup vaGroup; // vertex attribute button group
    private JRadioButton[] vaButtons;
    
    public TesterWindow() {
        this.glJPanel = new Container();
        
        testerState = TesterState.get();
        
        initComponents();
        
        setLayout();
    }
    
    public void setGlPanel(Component glJPanel) {
        l.replace(this.glJPanel, glJPanel);
        this.glJPanel = glJPanel;
    }
    
    private void initComponents() {
        // vertex Attributes
        vaGroup = new ButtonGroup();
        vaButtons = new JRadioButton[4];
        vaButtons[0] = new JRadioButton("UV", false);
        addButtonListener(vaButtons[0], () -> testerState.setVertexAttributes(VertexAttributes.UV));
        vaButtons[1] = new JRadioButton("Position, UV", false);
        addButtonListener(vaButtons[1], () -> testerState.setVertexAttributes(VertexAttributes.POS_UV));
        vaButtons[2] = new JRadioButton("Position, Normal, UV", true);
        addButtonListener(vaButtons[2], () -> testerState.setVertexAttributes(VertexAttributes.POS_NORMAL_UV));
        vaButtons[3] = new JRadioButton("Position, Normal, Color", false);
        addButtonListener(vaButtons[3], () -> testerState.setVertexAttributes(VertexAttributes.POS_NORMAL_COLOR));
        
        for(JRadioButton b : vaButtons) vaGroup.add(b);
    }
    
    private void addButtonListener(AbstractButton button, Runnable runnable) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
                glJPanel.requestFocus();
            }
        });
    }
    
    private void setLayout() {
        Container contentPane = getContentPane();
        l = new GroupLayout(contentPane);
        contentPane.setLayout(l);
        
        l.setHorizontalGroup(l.createSequentialGroup()
            .addGap(10)
            .addGroup(l.createSequentialGroup()
                .addComponent(glJPanel)
                .addGap(10)
                .addGroup(l.createParallelGroup()
                    .addComponent(vaButtons[0])
                    .addComponent(vaButtons[1])
                    .addComponent(vaButtons[2])
                    .addComponent(vaButtons[3])))
            .addGap(10)
        );
        l.setVerticalGroup(l.createSequentialGroup()
            .addGap(10)
            .addGroup(l.createParallelGroup()
                .addComponent(glJPanel)
                .addGroup(l.createSequentialGroup()
                    .addComponent(vaButtons[0])
                    .addComponent(vaButtons[1])
                    .addComponent(vaButtons[2])
                    .addComponent(vaButtons[3])))
            .addGap(10)
        );
    }
    
}
