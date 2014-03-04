package src;

import src.cpu.*;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmulationGUI
{
    // GUI CONSTANTS -- STRINGS AND SIZES
    // TO-DO: Move to an external file, for quick configuration
    private static final String EMULATION_STATUS_STRING = "Emulation Status:";
    private static final String REGISTER_STATUS_STRING = "Register Status:";
    private static final String REGISTER_STRING = "<html><u>Registers:</u></html>";
    private static final String POINTER_STRING = "<html><u>CPU Pointers:</u></html>";
    private static final String INFO_STRING = "Welcome to the Visual Z80 Emulator!\nThis was entirely written in Java in order to demonstrate the capabilities of myself. Having\nnever written a GUI or CPU emulator before, I designed this to test my abilities in\ndesigning a GUI and CPU Emulator.";
    private static final int REGISTER_WIDTH = 450;
    private static final int REGISTER_HEIGHT = 600;
    private static final int BAR_HEIGHT = 24;
    
    private static final int EMULATION_SPEED = 5000;
    
    // GUI Components
    private static JMenuBar mainMenu;
    private static JMenu fileMenu, settingsMenu;
    private static JMenuItem exitItem, aboutItem;
    private static JPanel emulationPanel, registerPanel, topRegisterHost, bottomRegisterHost;
    private static RegistersPanel contentTop;
    private static PointersPanel contentBottom;
    private static JLabel emulationStatus, registerStatus, registerLabel, emulationLabel, 
                      updateText, pointerLabel, helloLabel;
    private static JFrame mainFrame;
    private static JStatusBar statusBar;
    private static Timer timer;
    
    // MOUSE CLICK Positions
    private static int regX, regY, emuX, emuY;
    
    // CPU Classes
    private static Z80Core zCPU;
    
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI()
    {   
        updateText = new JLabel("Here!");
        zCPU = new Z80Core();
           
        ActionListener updateCPU = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateText.setText(zCPU.toString());
            }
        };
        
        timer = new Timer(EMULATION_SPEED, updateCPU);
        timer.start();
        
        initFrame();
        initContentPane(mainFrame);
        initMenu(mainFrame);
        initStatusBar(mainFrame);
        finalizeInit();     
    }
    
    private static void initFrame()
    {
        mainFrame = new JFrame("Vizu-80 -- Visual Z-80 Emulator");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
    }
    
    private static void initContentPane(JFrame theFrame)
    {
        initRegisterPane(theFrame);
        initEmulationPane(theFrame);
    }
    
    private static void initRegisterPane(JFrame theFrame)
    {
        registerPanel = new JPanel(); 
        
        registerPanel.setLayout(new GridLayout(2, 1));
        registerPanel.setOpaque(true);
        registerPanel.setBackground(new Color(255, 255, 255)); // White
        registerPanel.setPreferredSize(new Dimension(REGISTER_WIDTH, REGISTER_HEIGHT));
        
        registerPanel.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent mEvent)
            {
                regX = mEvent.getX();
                regY = mEvent.getY();
                
                registerStatus.setText(REGISTER_STATUS_STRING + " (" + regX + "," + regY + ")");
                helloLabel.setText("Current State of the Registers");
            }
        });
        
        splitRegPane(registerPanel);
        
        theFrame.getContentPane().add(registerPanel, BorderLayout.EAST);
    }
    
    private static void splitRegPane(JPanel host)
    {
        topRegisterHost = new JPanel();
        bottomRegisterHost = new JPanel();
        
        topRegisterHost.setOpaque(true);
        topRegisterHost.setBackground(new Color(225, 225, 225)); // White
        topRegisterHost.setLayout(new BorderLayout());
        
        bottomRegisterHost.setOpaque(true);
        bottomRegisterHost.setBackground(new Color(225, 225, 225)); // Red
        bottomRegisterHost.setLayout(new BorderLayout());
        
        registerLabel = new JLabel(REGISTER_STRING);
        registerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        registerLabel.setPreferredSize(new Dimension(topRegisterHost.getWidth(), BAR_HEIGHT));
        registerLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        topRegisterHost.add(registerLabel, BorderLayout.NORTH);
        
        pointerLabel = new JLabel(POINTER_STRING);
        pointerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pointerLabel.setPreferredSize(new Dimension(bottomRegisterHost.getWidth(), BAR_HEIGHT));
        pointerLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        bottomRegisterHost.add(pointerLabel, BorderLayout.NORTH);
        
        contentTop = new RegistersPanel();        
        contentBottom = new PointersPanel();
        
        topRegisterHost.add(contentTop, BorderLayout.CENTER);
        bottomRegisterHost.add(contentBottom, BorderLayout.CENTER);
        
        topRegisterHost.setBorder(BorderFactory.createLoweredBevelBorder());
        bottomRegisterHost.setBorder(BorderFactory.createLoweredBevelBorder());
        host.add(topRegisterHost);
        host.add(bottomRegisterHost);
        
    }
   
    private static void initEmulationPane(JFrame theFrame)
    {
        emulationPanel = new JPanel();
        
        emulationPanel.setOpaque(true);
        emulationPanel.setBackground(new Color(225, 225, 225)); // Black
        emulationPanel.setPreferredSize(new Dimension(REGISTER_WIDTH, REGISTER_HEIGHT));
        emulationPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        emulationPanel.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent mEvent)
            {
                emuX = mEvent.getX();
                emuY = mEvent.getY();
                
                emulationStatus.setText(EMULATION_STATUS_STRING + " (" + emuX + "," + emuY + ")");
                helloLabel.setText("Visualization of the Emulation");
            }
        });
        
        theFrame.getContentPane().add(emulationPanel, BorderLayout.WEST);
    }
    
    private static void initMenu(JFrame theFrame)
    {
        // Initialize menu bar
        mainMenu = new JMenuBar();
        mainMenu.setOpaque(true);
        mainMenu.setPreferredSize(new Dimension(theFrame.getWidth(), BAR_HEIGHT));
        
        // Init file menu -- Add it to the menu
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("File Menu");
        
        aboutItem = new JMenuItem("About");
        aboutItem.getAccessibleContext().setAccessibleDescription("About the system");
        aboutItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(new JFrame(), INFO_STRING, "Vizu-80 Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        fileMenu.add(aboutItem);
        
        fileMenu.addSeparator();
        
        exitItem = new JMenuItem("Exit");
        exitItem.getAccessibleContext().setAccessibleDescription("Exits the system");
        exitItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);
        
        mainMenu.add(fileMenu);
        theFrame.setJMenuBar(mainMenu);         
    }
    
    private static void initStatusBar(JFrame theFrame)
    {
        statusBar = new JStatusBar();
        
        helloLabel = new JLabel("Hello! Welcome to Vizu-80");
        statusBar.setLeftComponent(helloLabel);
        
        statusBar.addRightComponent(updateText);
        
        emulationStatus = new JLabel(EMULATION_STATUS_STRING + " (" + emuX + "," + emuY + ")");
        emulationStatus.setHorizontalAlignment(JLabel.LEFT);
        statusBar.addRightComponent(emulationStatus);    
        
        registerStatus = new JLabel(REGISTER_STATUS_STRING + " (" + regX + "," + regY + ")");
        registerStatus.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(registerStatus);  
        
        theFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);
    }
    
    private static void finalizeInit()
    {
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true); 
    }
}