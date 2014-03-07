package src;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Vizu80GUI
{    
    /* 
     * ****************************************
     * Static Final Strings - Descriptions, Titles, Phrases...
     * ****************************************
     */
    private static final String PROJECT_TITLE = "Vizu-80 - The Visual z80 Learning Emulator";
    private static final String INITIAL_STATUS_MESSAGE = "Hello! Welcome to Vizu-80!";
    private static final String FILE_ACCESSIBILITY_STRING = "File Menu -- Learn about or exit the application";
    private static final String OPTIONS_ACCESSIBILITY_STRING = "Options Menu -- Change options in the application";
    private static final String ANIM_STATUS_MESSAGE = "Animation Status: ";
    private static final String CPU_STATUS_MESSAGE = "CPU Status: ";
    private static final String ANIMATION_TITLE = "CPU Animation:";
    private static final String OPCODE_TITLE = "Opcode Interpreter:";
    private static final String INTERNALS_TITLE = "CPU Internal State:";
    private static final String MEMORY_TITLE = "System RAM State:";
    private static final String CONTROL_TITLE = "Emulation Control:";
    private static final String ABOUT_PLAY = "Play / Pause the emulation and related visualizations.";
    private static final String ABOUT_NEXT = "One Step - Executes one step of the instruction pipline.";
    private static final String ABOUT_BACK = "Back Step - Reverses the instruction pipline, one instruction.";
    
    private static final String[] REGISTER_STRINGS = new String[] {
        "Register A:",
        "Register F:",
        "Register B:",
        "Register C:",
        "Register D:",
        "Register E:",
        "Register H:",
        "Register L:",
        "Ghost A':",
        "Ghost F':",
        "Ghost B':",
        "Ghost C':",
        "Ghost D':",
        "Ghost E':",
        "Ghost H':",
        "Ghost L':" };
    
    /* 
     * ****************************************
     * Static Final Integers - Heights, Widths, Times...
     * ****************************************
     */
    private static final int CONTENT_WIDTH = 1280; // Pixels
    private static final int CONTENT_HEIGHT = 768; // Pixels
    private static final int BAR_HEIGHT = 22; // Pixels
    private static final int STATUS_PADDING = 30; // Pixels
    private static final int UPDATE_SPEED = 2500; // Milliseconds ... 2.5 Seconds
    private static final int FRAME_MARGIN = 10; // Pixels
    private static final int PANEL_WIDTH = CONTENT_WIDTH / 2; // Pixels
    private static final int PANEL_HEIGHT = (CONTENT_HEIGHT / 4) * 3; // Pixels
    private static final int SUB_PANEL_HEIGHT = (CONTENT_HEIGHT / 4); // Pixels
    private static final int CONTROL_HEIGHT = 125; // Pixels
    private static final int CONTROL_GAP = 20;
    private static final int LEFT_OFFSET = 20;
    private static final int TOP_OFFSET = 50;
   
    /* 
     * ****************************************
     * Static Final Colors - Colors used for the overall theme
     * ****************************************
     */
    private static final Color COLOR_MENU_BAR = new Color(242, 238, 230);
    private static final Color COLOR_FRAME_BORDER = new Color(180, 180, 180);
    private static final Color COLOR_CONTENT_BACKGROUND = new Color(232, 228, 220);
    private static final Color COLOR_PANEL_BACKGROUND = new Color(232, 228, 220);
    
    private static final Font TITLE_FONT = new Font("Tahoma", Font.PLAIN, 20);
    private static final Font SUB_TITLE_FONT = new Font("Tahoma", Font.PLAIN, 16);
    

    
    /* 
     * ****************************************
     * Extras - No category, don't belong to GUI components
     * ****************************************
     */
    private static Timer updateTimer;
    private static GridBagConstraints con;
    private static ImageFilter theFilter;
    private static DataController cpuController;
    private static volatile boolean isRunning;
    private static volatile boolean started;
    
    
    /* 
     * ****************************************
     * All GUI Components - Below is the scene graph of components
     * ****************************************
     */
    
    // The main - "High Level" - container, holds all other components
    // Top of the GUI Scene Graph / Tree
    private static JFrame mainFrame;
    
    // Within the 1st layer of the Scene Graph
    private static BackgroundMenuBar menuBar;
    private static JMenu fileMenu, optionsMenu;
    private static JMenuItem aboutItem, exitItem, settingsItem, updateItem;
    
    // Also within the 1st layer of the Scene Graph
    private static JStatusBar statusBar;
    private static JLabel messageLabel, animationStatusLabel,cpuStatusLabel;
    
    // Main Content Container
    // Final component of the 1st layer of the Scene Graph
    private static JPanel contentPanel;
        
    // CPU / Animation Status Panel - Panel of the GUI, shows current status of CPU components / Animation
    // 2nd layer of the Scene Graph - Under contentPanel
    private static JPanel cpuPanel, animPanel, cpuPanelExtra, animPanelExtra;
    
    
    // Within the 3rd layer of the Scene Graph - One into the respective containers
    private static JLabel animTitle, cpuTitle, cpuExtraTitle, animExtraTitle;
    private static JPanel animContentPanel, animControlPanel, cpuContentPanel;   
    
    // 5th Layer of the Scene Graph -- Controls / Container
    private static JLabel animControlTitle;
    private static JPanel controlContainer;
    private static JButton controlNext, controlBack, controlPlay;
    private static ImageIcon nextImage, backImage, playImage;
    
    
    // 5th Layer of the Scene Graph -- CPU Dislay Contents
    private static JLabel[] registersLabel;
    private static JLabel[] registersContent;
    private static JLabel flagsBinary, ghostFlagsBinary;
    
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
               started = false;
               createAndShowGUI();
            }
        });
    }
    
    private static void createAndShowGUI()
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mainFrame = new JFrame(PROJECT_TITLE);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ActionListener updateLabels = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(!started)
                {
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Initialization done!");
                    cpuStatusLabel.setText(CPU_STATUS_MESSAGE + "Initialization done!");
                }
                
                started = true;
            }
        };
        
        updateTimer = new Timer(UPDATE_SPEED, updateLabels);
        updateTimer.start();
        
        addMenuComponents(mainFrame);
        addStatusBarComponents(mainFrame);
        addContentPanel(mainFrame);
        initializeContentPanels();
        
        isRunning = false;
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    private static void addMenuComponents(JFrame theFrame)
    {
        menuBar = new BackgroundMenuBar();
        menuBar.setColor(COLOR_MENU_BAR);
        menuBar.setOpaque(true);
        menuBar.setPreferredSize(new Dimension(theFrame.getWidth(), BAR_HEIGHT));
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(FILE_ACCESSIBILITY_STRING);
        fileMenu.setBackground(COLOR_MENU_BAR);
        
        aboutItem = new JMenuItem("About");
        aboutItem.setBackground(COLOR_MENU_BAR);
        aboutItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // About Pop-Up
            }
        });
        
        exitItem = new JMenuItem("Exit");
        exitItem.setBackground(COLOR_MENU_BAR);
        exitItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int picked = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Are you sure?",
                                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                                                
                if(picked == 0)
                    shutdown();
            }
        });
        
        fileMenu.add(aboutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        optionsMenu = new JMenu("Options");
        optionsMenu.setBackground(COLOR_MENU_BAR);
        
        settingsItem = new JMenuItem("Settings");
        settingsItem.setBackground(COLOR_MENU_BAR);
        settingsItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // new OptionsFrame / Panel (Tabbed Pane)
            }
        });
        
        updateItem = new JMenuItem("Check for Updates...");
        updateItem.setBackground(COLOR_MENU_BAR);
        updateItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Perform network update checks
            }
        });
        
        optionsMenu.add(settingsItem);
        optionsMenu.addSeparator();
        optionsMenu.add(updateItem);
               
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        
        theFrame.setJMenuBar(menuBar);
    }
    
    private static void addStatusBarComponents(JFrame theFrame)
    {
        statusBar = new JStatusBar(BAR_HEIGHT, COLOR_MENU_BAR);
        
        messageLabel = new JLabel(INITIAL_STATUS_MESSAGE);
        messageLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
        statusBar.setLeftComponent(messageLabel);
        
        animationStatusLabel = new JLabel(ANIM_STATUS_MESSAGE);
        animationStatusLabel.setHorizontalAlignment(JLabel.LEFT);
        animationStatusLabel.setBorder(new EmptyBorder(2, 0, 0, STATUS_PADDING));
        statusBar.addRightComponent(animationStatusLabel);
        
        cpuStatusLabel = new JLabel(CPU_STATUS_MESSAGE);
        cpuStatusLabel.setHorizontalAlignment(JLabel.CENTER);
        cpuStatusLabel.setBorder(new EmptyBorder(2, 0, 0, STATUS_PADDING));
        statusBar.addRightComponent(cpuStatusLabel);
        
        theFrame.add(statusBar, BorderLayout.SOUTH);
    }
    
    private static void addContentPanel(JFrame theFrame)
    {
        contentPanel = new JPanel();
        contentPanel.setOpaque(true);
        contentPanel.setBackground(COLOR_CONTENT_BACKGROUND);
        contentPanel.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        contentPanel.setBorder(new EmptyBorder(FRAME_MARGIN, FRAME_MARGIN, FRAME_MARGIN, FRAME_MARGIN));
        
        contentPanel.setLayout(new GridBagLayout());
        con = new GridBagConstraints();
        addSplitPanels(contentPanel);
        
        theFrame.add(contentPanel, BorderLayout.CENTER);
    }
    
    private static void addSplitPanels(JPanel thePanel)
    {
        animPanel = new JPanel();
        animTitle = new JLabel(ANIMATION_TITLE);
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 0.1;
        con.weighty = 0.1;
        con.anchor = GridBagConstraints.NORTHWEST;
        con.ipadx = PANEL_WIDTH;
        con.ipady = PANEL_HEIGHT;
        con.insets = new Insets(5, 5, 5, 5);
        thePanel.add(animPanel, con);
        
        cpuPanel = new JPanel();
        cpuTitle = new JLabel(INTERNALS_TITLE);
        con.gridx = 1;
        con.gridy = 0;
        con.weightx = 0.5;
        con.weighty = 1.0;
        thePanel.add(cpuPanel, con);
        
        animPanelExtra = new JPanel();
        animExtraTitle = new JLabel(OPCODE_TITLE);
        con.gridx = 0;
        con.gridy = 1;
        con.ipady = SUB_PANEL_HEIGHT;
        thePanel.add(animPanelExtra, con);
        
        cpuPanelExtra = new JPanel();
        cpuExtraTitle = new JLabel(MEMORY_TITLE);
        con.gridx = 1;
        con.gridy = 1;
        thePanel.add(cpuPanelExtra, con);
    }
    
    private static void addPanelTitles()
    {
        insertTitle(animPanel, animTitle, COLOR_PANEL_BACKGROUND, new BevelBorder(BevelBorder.LOWERED), TITLE_FONT);
        insertTitle(cpuPanel, cpuTitle, COLOR_PANEL_BACKGROUND, new BevelBorder(BevelBorder.LOWERED), TITLE_FONT);
        insertTitle(animPanelExtra, animExtraTitle, COLOR_PANEL_BACKGROUND, new BevelBorder(BevelBorder.LOWERED), TITLE_FONT);
        insertTitle(cpuPanelExtra, cpuExtraTitle, COLOR_PANEL_BACKGROUND, new BevelBorder(BevelBorder.LOWERED), TITLE_FONT);
    }
    
    private static void insertTitle(JPanel thePanel, JLabel theTitle, Color bg, Border bord, Font font)
    {
        thePanel.setBackground(bg);
        thePanel.setBorder(bord);
        thePanel.setLayout(new BorderLayout());
        thePanel.setOpaque(true);
        
        theTitle.setFont(font);
        theTitle.setHorizontalAlignment(JLabel.CENTER);
        
        thePanel.add(theTitle, BorderLayout.NORTH);
        
        
        /*
        if(theTitle.getText().equals(ANIMATION_TITLE))
        {
        thePanel.setLayout(new GridBagLayout());
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 0.1;
        con.weighty = 0.1;
        con.anchor = GridBagConstraints.PAGE_START;
        }
        else
        {
        }
        */
    }
    
    private static void initializeContentPanels()
    {
        addPanelTitles();
        addAnimPanelContent();
        addCpuPanelContent();
        addOpcodePanelContent();
        addMemoryPanelContent();
    }
    
    private static void addCpuPanelContent()
    {
        cpuContentPanel = new JPanel();
        cpuContentPanel.setBackground(COLOR_PANEL_BACKGROUND);
        cpuContentPanel.setLayout(null);
        
        initCpuComponents();
        
        cpuPanel.add(cpuContentPanel);
    }
    
    /**
     * PLEASE PLEASE PLEASE!!!! DO NOT EVER DO THIS... EVER!
     */
    private static void initCpuComponents()
    {
        registersLabel = new JLabel[16];
        registersContent = new JLabel[16];
        flagsBinary = new JLabel("0b00000000");
        ghostFlagsBinary = new JLabel("0b00000000");
        
        int count = 0;
        Insets insets = cpuContentPanel.getInsets();
        Dimension sizeLabel, sizeContent, sizeBinary;
        
        /*
         * REGISTER A -- ACCUMULATOR
         */
        registersLabel[0] = new JLabel(REGISTER_STRINGS[0]);
        registersContent[0] = new JLabel("0x00");
        registersLabel[0].setFont(SUB_TITLE_FONT);
        registersContent[0].setFont(SUB_TITLE_FONT);
        registersContent[0].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[0].setHorizontalAlignment(JLabel.CENTER);

        sizeLabel = registersLabel[0].getPreferredSize();
        sizeContent = registersContent[0].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[0]);
        cpuContentPanel.add(registersContent[0]);

        
        registersLabel[0].setBounds(insets.left + LEFT_OFFSET, insets.top + TOP_OFFSET,
                                                   sizeLabel.width, sizeLabel.height);
       
        registersContent[0].setBounds(registersLabel[0].getX() + registersLabel[0].getWidth() + 10,
                                        registersLabel[0].getY() - 7, sizeContent.width + 10, sizeContent.height + 10); 
                                       
                                                  
        /*
         *  REGISTER F -- FLAGS
         */
        registersLabel[1] = new JLabel(REGISTER_STRINGS[1]);
        registersContent[1] = new JLabel("0x00");
        registersLabel[1].setFont(SUB_TITLE_FONT);
        registersContent[1].setFont(SUB_TITLE_FONT);
        registersContent[1].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[1].setHorizontalAlignment(JLabel.CENTER);
                
        flagsBinary.setFont(SUB_TITLE_FONT);
        flagsBinary.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        flagsBinary.setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[1].getPreferredSize();
        sizeContent = registersContent[1].getPreferredSize();
        sizeBinary = flagsBinary.getPreferredSize();
        
        cpuContentPanel.add(registersLabel[1]);
        cpuContentPanel.add(registersContent[1]);
        cpuContentPanel.add(flagsBinary);
        
        registersLabel[1].setBounds(registersContent[0].getX() + registersContent[0].getWidth() + 40, 
                                registersLabel[0].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[1].setBounds(registersLabel[1].getX() + registersLabel[1].getWidth() + 10,
                                        registersLabel[0].getY() - 7, sizeContent.width + 10, sizeContent.height + 10); 
        
        flagsBinary.setBounds(registersContent[1].getX() + registersContent[1].getWidth() + 10,
                                registersContent[1].getY(), sizeBinary.width + 10, sizeBinary.height + 10);
                                        
        /*
         *  REGISTER B -- Top Half BC
         */
        registersLabel[2] = new JLabel(REGISTER_STRINGS[2]);
        registersContent[2] = new JLabel("0x00");
        registersLabel[2].setFont(SUB_TITLE_FONT);
        registersContent[2].setFont(SUB_TITLE_FONT);
        registersContent[2].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[2].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[2].getPreferredSize();
        sizeContent = registersContent[2].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[2]);
        cpuContentPanel.add(registersContent[2]);
        
        registersLabel[2].setBounds(insets.left + LEFT_OFFSET, insets.top + TOP_OFFSET + registersLabel[0].getHeight() + 30, 
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[2].setBounds(registersLabel[0].getX() + registersLabel[0].getWidth() + 10,
                                        registersLabel[2].getY() - 7, sizeContent.width + 10, sizeContent.height + 10); 
                                 
        /*
         *  REGISTER C -- Bottom Half BC
         */
        registersLabel[3] = new JLabel(REGISTER_STRINGS[3]);
        registersContent[3] = new JLabel("0x00");
        registersLabel[3].setFont(SUB_TITLE_FONT);
        registersContent[3].setFont(SUB_TITLE_FONT);
        registersContent[3].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[3].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[3].getPreferredSize();
        sizeContent = registersContent[3].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[3]);
        cpuContentPanel.add(registersContent[3]);
        
        registersLabel[3].setBounds(registersContent[2].getX() + registersContent[2].getWidth() + 40 - 1,
                                registersLabel[2].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[3].setBounds(registersLabel[3].getX() + registersLabel[3].getWidth() + 10,
                                        registersLabel[3].getY() - 7, sizeContent.width + 10, sizeContent.height + 10); 
                                        
                                        
        /*
         *  REGISTER D -- Top Half DE
         */
        registersLabel[4] = new JLabel(REGISTER_STRINGS[4]);
        registersContent[4] = new JLabel("0x00");
        registersLabel[4].setFont(SUB_TITLE_FONT);
        registersContent[4].setFont(SUB_TITLE_FONT);
        registersContent[4].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[4].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[4].getPreferredSize();
        sizeContent = registersContent[4].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[4]);
        cpuContentPanel.add(registersContent[4]);
        
        registersLabel[4].setBounds(insets.left + LEFT_OFFSET, registersLabel[2].getY() + registersLabel[2].getHeight() + 30,
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[4].setBounds(registersLabel[4].getX() + registersLabel[4].getWidth() + 10,
                                        registersLabel[4].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                        
                                        
        /*
         *  REGISTER E -- Bottom Half DE
         */
        registersLabel[5] = new JLabel(REGISTER_STRINGS[5]);
        registersContent[5] = new JLabel("0x00");
        registersLabel[5].setFont(SUB_TITLE_FONT);
        registersContent[5].setFont(SUB_TITLE_FONT);
        registersContent[5].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[5].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[5].getPreferredSize();
        sizeContent = registersContent[5].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[5]);
        cpuContentPanel.add(registersContent[5]);
        
        registersLabel[5].setBounds(registersContent[4].getX() + registersContent[4].getWidth() + 40,
                                registersLabel[4].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[5].setBounds(registersLabel[5].getX() + registersLabel[5].getWidth() + 10,
                                        registersLabel[5].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                        
        /*
         *  REGISTER H -- Top Half HL
         */
        registersLabel[6] = new JLabel(REGISTER_STRINGS[6]);
        registersContent[6] = new JLabel("0x00");
        registersLabel[6].setFont(SUB_TITLE_FONT);
        registersContent[6].setFont(SUB_TITLE_FONT);
        registersContent[6].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[6].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[6].getPreferredSize();
        sizeContent = registersContent[6].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[6]);
        cpuContentPanel.add(registersContent[6]);
        
        registersLabel[6].setBounds(insets.left + LEFT_OFFSET, registersLabel[4].getY() + registersLabel[4].getHeight() + 30,
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[6].setBounds(registersLabel[6].getX() + registersLabel[6].getWidth() + 10,
                                        registersLabel[6].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                        
        /*
         *  REGISTER L -- Bottom Half HL
         */
        registersLabel[7] = new JLabel(REGISTER_STRINGS[7]);
        registersContent[7] = new JLabel("0x00");
        registersLabel[7].setFont(SUB_TITLE_FONT);
        registersContent[7].setFont(SUB_TITLE_FONT);
        registersContent[7].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[7].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[7].getPreferredSize();
        sizeContent = registersContent[7].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[7]);
        cpuContentPanel.add(registersContent[7]);
        
        registersLabel[7].setBounds(registersContent[6].getX() + registersContent[6].getWidth() + 40,
                                registersLabel[6].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[7].setBounds(registersLabel[7].getX() + registersLabel[7].getWidth() + 11,
                                        registersLabel[7].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);                                
                                        
                                        
        /*
        *  Ghost A -- Top Half AF'
        */
        registersLabel[8] = new JLabel(REGISTER_STRINGS[8]);
        registersContent[8] = new JLabel("0x00");
        registersLabel[8].setFont(SUB_TITLE_FONT);
        registersContent[8].setFont(SUB_TITLE_FONT);
        registersContent[8].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[8].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[8].getPreferredSize();
        sizeContent = registersContent[8].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[8]);
        cpuContentPanel.add(registersContent[8]);
        
        registersLabel[8].setBounds(insets.left + LEFT_OFFSET, registersLabel[6].getY() + registersLabel[6].getHeight() + 100,
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[8].setBounds(registersLabel[8].getX() + registersLabel[8].getWidth() + 23,
                                        registersLabel[8].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
        
        /*
         *  Ghost F -- Bottom Half AF'
         */
        registersLabel[9] = new JLabel(REGISTER_STRINGS[9]);
        registersContent[9] = new JLabel("0x00");
        registersLabel[9].setFont(SUB_TITLE_FONT);
        registersContent[9].setFont(SUB_TITLE_FONT);
        registersContent[9].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[9].setHorizontalAlignment(JLabel.CENTER);
        
        ghostFlagsBinary.setFont(SUB_TITLE_FONT);
        ghostFlagsBinary.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        ghostFlagsBinary.setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[9].getPreferredSize();
        sizeContent = registersContent[9].getPreferredSize();
        sizeBinary = ghostFlagsBinary.getPreferredSize();
        
        cpuContentPanel.add(registersLabel[9]);
        cpuContentPanel.add(registersContent[9]);
        cpuContentPanel.add(ghostFlagsBinary);
        
        registersLabel[9].setBounds(registersContent[8].getX() + registersContent[8].getWidth() + 40,
                                registersLabel[8].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[9].setBounds(registersLabel[9].getX() + registersLabel[9].getWidth() + 24,
                                        registersLabel[9].getY() - 7, sizeContent.width + 10, sizeContent.height + 10); 
        
        ghostFlagsBinary.setBounds(registersContent[9].getX() + registersContent[9].getWidth() + 10,
                                registersContent[9].getY(), sizeBinary.width + 10, sizeBinary.height + 10);
             
        /*
        *  Ghost B -- Top Half BC'
        */
        registersLabel[10] = new JLabel(REGISTER_STRINGS[10]);
        registersContent[10] = new JLabel("0x00");
        registersLabel[10].setFont(SUB_TITLE_FONT);
        registersContent[10].setFont(SUB_TITLE_FONT);
        registersContent[10].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[10].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[10].getPreferredSize();
        sizeContent = registersContent[10].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[10]);
        cpuContentPanel.add(registersContent[10]);
        
        registersLabel[10].setBounds(insets.left + LEFT_OFFSET, registersLabel[8].getY() + registersLabel[8].getHeight() + 30,
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[10].setBounds(registersLabel[10].getX() + registersLabel[10].getWidth() + 25,
                                        registersLabel[10].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                        
                                        
                                        
        /*
        *  Ghost C -- Bottom Half BC'
        */
        registersLabel[11] = new JLabel(REGISTER_STRINGS[11]);
        registersContent[11] = new JLabel("0x00");
        registersLabel[11].setFont(SUB_TITLE_FONT);
        registersContent[11].setFont(SUB_TITLE_FONT);
        registersContent[11].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[11].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[11].getPreferredSize();
        sizeContent = registersContent[11].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[11]);
        cpuContentPanel.add(registersContent[11]);
        
        registersLabel[11].setBounds(registersContent[10].getX() + registersContent[10].getWidth() + 40,
                                registersLabel[10].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[11].setBounds(registersLabel[11].getX() + registersLabel[11].getWidth() + 23,
                                        registersLabel[11].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                        
        /*
        *  Ghost D -- Top Half DE'
        */
        registersLabel[12] = new JLabel(REGISTER_STRINGS[12]);
        registersContent[12] = new JLabel("0x00");
        registersLabel[12].setFont(SUB_TITLE_FONT);
        registersContent[12].setFont(SUB_TITLE_FONT);
        registersContent[12].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[12].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[12].getPreferredSize();
        sizeContent = registersContent[12].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[12]);
        cpuContentPanel.add(registersContent[12]);
        
        registersLabel[12].setBounds(insets.left + LEFT_OFFSET, registersLabel[10].getY() + registersLabel[10].getHeight() + 30,
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[12].setBounds(registersLabel[12].getX() + registersLabel[12].getWidth() + 23,
                                        registersLabel[12].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
        
                                        
        /*
        *  Ghost E -- Bottom Half DE'
        */
        registersLabel[13] = new JLabel(REGISTER_STRINGS[13]);
        registersContent[13] = new JLabel("0x00");
        registersLabel[13].setFont(SUB_TITLE_FONT);
        registersContent[13].setFont(SUB_TITLE_FONT);
        registersContent[13].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[13].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[13].getPreferredSize();
        sizeContent = registersContent[13].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[13]);
        cpuContentPanel.add(registersContent[13]);
        
        registersLabel[13].setBounds(registersContent[12].getX() + registersContent[12].getWidth() + 40,
                                registersLabel[12].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[13].setBounds(registersLabel[13].getX() + registersLabel[13].getWidth() + 24,
                                        registersLabel[13].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                 
                                        
        /*
         *  Ghost H -- Top Half HL'
         */
        registersLabel[14] = new JLabel(REGISTER_STRINGS[14]);
        registersContent[14] = new JLabel("0x00");
        registersLabel[14].setFont(SUB_TITLE_FONT);
        registersContent[14].setFont(SUB_TITLE_FONT);
        registersContent[14].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[14].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[14].getPreferredSize();
        sizeContent = registersContent[14].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[14]);
        cpuContentPanel.add(registersContent[14]);
        
        registersLabel[14].setBounds(insets.left + LEFT_OFFSET, registersLabel[12].getY() + registersLabel[12].getHeight() + 30,
                                        sizeLabel.width, sizeLabel.height);
       
        registersContent[14].setBounds(registersLabel[14].getX() + registersLabel[14].getWidth() + 23,
                                        registersLabel[14].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
        
                                        
        /*
        *  Ghost L -- Bottom Half HL'
        */
        registersLabel[15] = new JLabel(REGISTER_STRINGS[15]);
        registersContent[15] = new JLabel("0x00");
        registersLabel[15].setFont(SUB_TITLE_FONT);
        registersContent[15].setFont(SUB_TITLE_FONT);
        registersContent[15].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        registersContent[15].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[15].getPreferredSize();
        sizeContent = registersContent[15].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[15]);
        cpuContentPanel.add(registersContent[15]);
        
        registersLabel[15].setBounds(registersContent[14].getX() + registersContent[14].getWidth() + 40,
                                registersLabel[14].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[15].setBounds(registersLabel[15].getX() + registersLabel[15].getWidth() + 25,
                                        registersLabel[15].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);
                                 
    }
    
    private static void addMemoryPanelContent()
    {
        JPanel test = new JPanel();
        test.setBackground(COLOR_PANEL_BACKGROUND);
        
        animPanelExtra.add(test);
    }
    
    private static void addOpcodePanelContent()
    {
        JPanel test = new JPanel();
        test.setBackground(COLOR_PANEL_BACKGROUND);
        
        cpuPanelExtra.add(test);
    }
    
    private static void addAnimPanelContent()
    {
        animContentPanel = new JPanel();
        animContentPanel.setBackground(COLOR_PANEL_BACKGROUND);
        animPanel.add(animContentPanel, BorderLayout.CENTER);
        
        animControlPanel = new JPanel();
        animControlTitle = new JLabel(CONTROL_TITLE);
        animControlPanel.setPreferredSize(new Dimension(animPanel.getWidth(), CONTROL_HEIGHT));
        insertTitle(animControlPanel, animControlTitle, COLOR_PANEL_BACKGROUND, new BevelBorder(BevelBorder.LOWERED), SUB_TITLE_FONT);
       
        addControls(animControlPanel);
        animPanel.add(animControlPanel, BorderLayout.SOUTH);
    }
    
    
    private static void addControls(JPanel thePanel)
    {
        controlContainer = new JPanel();
        controlNext = new JButton("");
        controlBack = new JButton("");
        controlPlay = new JButton("");
        
        filterAndAddImages();
        addListeners();
        
        controlContainer.setBackground(COLOR_PANEL_BACKGROUND);
        controlContainer.setLayout(new GridLayout(0, 3));
        
        controlContainer.add(controlBack);
        controlContainer.add(controlPlay);
        controlContainer.add(controlNext);
        
        thePanel.add(controlContainer);
    }
    
    private static void filterAndAddImages()
    {
        playImage = new ImageIcon("src/res/images/play_pause.png");
        nextImage = new ImageIcon("src/res/images/right_arrow.png");
        backImage = new ImageIcon("src/res/images/left_arrow.png");
        
        controlBack.setIcon(backImage);
        controlNext.setIcon(nextImage);
        controlPlay.setIcon(playImage);       
                
        controlBack.setOpaque(false);
        controlNext.setOpaque(false);
        controlPlay.setOpaque(false);
    }
    
    private static void addListeners()
    {
        /* ****************************************
         * Play Button Listeners
         * ****************************************
         */
        controlPlay.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(isRunning)
                {
                    // Pause Simulation
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Paused! (User Initiated)");
                    isRunning = false;
                }
                else
                {
                    // Start Simulation
                    // If First Run....
                    //animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "ANIMATION STARTING!");
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Running! . . . Ok!");
                    isRunning = true;
                }
            }
        });
        
        controlPlay.addMouseListener(new MouseAdapter()
        {
            public void  mouseEntered(MouseEvent e)
            {
               messageLabel.setText(ABOUT_PLAY);
            }
        });
        
        /* ****************************************
         * Next Button Listeners
         * ****************************************
         */
        controlNext.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e)
            {
                messageLabel.setText(ABOUT_NEXT);
            }            
        });
        
        
        /* ****************************************
         *  Back Button Listeners
         * ****************************************
         */
        controlBack.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e)
            {
                messageLabel.setText(ABOUT_BACK);
            }            
        });
    }
    
    private static void shutdown()
    {
        mainFrame.dispose();
        System.exit(0);
    }
}