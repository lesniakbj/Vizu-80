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
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Vizu80GUI - The main GUI for the Visual z80 Learning Emulator <br>
 *      View component of the MVC framework, displays all of the data coming in from the CpuController 
 * 
 * @author Brendan Lesniak
 * @version 0.1.a
 * @since 0.0.a
 */
public class Vizu80GUI
{    
    /* 
     * ************************************************
     * Constant strings used throughout the application
     * ************************************************
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
    
    private static final String[] REGISTER_STRINGS = new String[] {     // Array of strings that are used in the register labels
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
        
    private static final String[] OTHER_DATA_STRINGS = new String[] {   // Array of strings that are used for extra CPU data
        "Index X:",
        "Index Y:",
        "Program Counter:",
        "Stack Pointer:",
        "Interrupt Register:",
        "Refresh Register:",
        "Indexer:"  };
        
    /* 
     * **************************************************
     * Heights & Widths of various containers and padding
     * Timer delay
     * **************************************************
     */
    private static final int CONTENT_WIDTH = 1280;                      // Pixels -- Main Content Panel
    private static final int CONTENT_HEIGHT = 768;                      // Pixels -- Main Content Panel
    private static final int FRAME_MARGIN = 10;                         // Pixels -- Margin Main Content Panel
    private static final int BAR_HEIGHT = 22;                           // Pixels -- StatusBar & MenuBar
    private static final int STATUS_PADDING = 30;                       // Pixels -- Status Bar right item padding
    private static final int PANEL_WIDTH = CONTENT_WIDTH / 2;           // Pixels -- Width of inset panel (upper)
    private static final int PANEL_HEIGHT = (CONTENT_HEIGHT / 4) * 3;   // Pixels -- Height of inset panel (upper)
    private static final int SUB_PANEL_HEIGHT = (CONTENT_HEIGHT / 4);   // Pixels -- Height of inset panel (lower)
    private static final int CONTROL_HEIGHT = 125;                      // Pixels -- Height of control panel
    private static final int LEFT_OFFSET = 10;                          // Pixels -- Left offset for register display items
    private static final int TOP_OFFSET = 20;                           // Pixels -- Top offset for register display items
    private static final int UPDATE_SPEED = 1000;                       // Milliseconds  -- CPU Update rate
    
    /* 
     * *********************************************************************
     * Colors & Fonts used in the GUI themeing -- Similar to a Windows theme
     * *********************************************************************
     */
    private static final Color COLOR_MENU_BAR = new Color(242, 238, 230);               // Color for the menu and status bars
    private static final Color COLOR_PANEL_BACKGROUND = new Color(232, 228, 220);       // Color for the content panels
    
    private static final Font TITLE_FONT = new Font("Tahoma", Font.PLAIN, 20);          // Panel title font
    private static final Font SUB_TITLE_FONT = new Font("Tahoma", Font.PLAIN, 16);      // Register panel content font
    private static final Font SUB_FONT = new Font("Tahoma", Font.PLAIN, 12);            // Used in frame count
    

    
    /* 
     * ****************************************
     * Extras - No category, don't belong to GUI components
     * ****************************************
     */
    private static Timer updateTimer;                       // CPU simulation timer
    private static GridBagConstraints con;                  // Used to layout items in the GridBagLayout                      
    private static DataController cpuController;            // Controller component -- controls CPU simulation and data retrival
    private static DataPack cpuDataPack;                    // Data pack recieved from the CPU Controller     
    private static int[] cpuRegisterData, cpuFullData, cpuOtherData;     // Personal copy of register and other (pointers, counters, etc.) data
    private static volatile boolean isRunning;              // Is the simulation currently running. . . 
    private static volatile boolean startUpDone;            // Is the initial start up done. . . 
    private static int dataCount;                           // Current data frame we are observing
    private static int totalDataCount;                      // Total number of data frame retrieved
    
    
    
    /* 
     * ****************************************
     * All GUI Components - Below is the scene graph of components
     * ****************************************
     */    
    private static JFrame mainFrame;                                                // Main content frame, holds status bars and the main content panel
    
    
    private static BackgroundMenuBar menuBar;                                       // Main menu bar -- Custom background 
    private static JMenu fileMenu, optionsMenu;                                     // Menu bar options
    private static JMenuItem aboutItem, exitItem, settingsItem, updateItem;         // Items within the menu bar
    
    
    private static JStatusBar statusBar;                                            // Lower status bar -- Contains messages and statuses
    private static JLabel messageLabel, animationStatusLabel, cpuStatusLabel;       // All labels associated with the status bar
    

    private static JPanel contentPanel;                                             // Main content panel, holds all of the other components
    private static JPanel cpuPanel, animPanel, cpuPanelExtra, animPanelExtra;       // 4 cpu / emulation place holder panels, added to the main panel
    private static JLabel animTitle, cpuTitle, cpuExtraTitle, animExtraTitle;       // Title labels for each content panel    
    private static JPanel animContentPanel, cpuContentPanel;                        // Content panels for each place holder
    

    private static JLabel animControlTitle;                                         // Control panel title label
    private static JPanel controlContainer, animControlPanel;                       // Control panel place holder and content panel
    private static JButton controlNext, controlBack, controlPlay;                   // Control buttons
    private static ImageIcon nextImage, backImage, playImage;                       // Images for the control buttons
    
    
    private static JLabel[] registersLabel;                                         // Array of labels for all of the registers in the CPU
    private static JLabel[] registersContent;                                       // Array of content for all of the registers in the CPU
    private static JLabel[] fullLabel;
    private static JLabel[] fullContent;
    private static JLabel flagsBinary, ghostFlagsBinary;                            // Label displaying binary representation of the CPU Flags & Flags'
    private static JLabel[] othersLabel;                                            // Array of labels for all extra data content
    private static JLabel[] othersContent;                                          // Array of content for all the extra data
    private static JLabel frameCountLabel;                                          // Label for displaying the current data frame
    
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
        
        /*
         * Entry point of our GUI, starts the GUI on the Swing Event Dispatch Thread
         * We haven't started our simulation yet, so set all relevant flags to the false state
         */
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
               startUpDone = false;     // Start up hasn't finished yet... obviously.
               isRunning = false;       // Well... we're not quite running yet...
               createAndShowGUI();
            }
        });
    }
    
    /**
     * Initializes all GUI and simulation components, the shows the GUI.
     * 
     * @since 0.0.a
     */
    private static void createAndShowGUI()
    {
        // Initialize the main frame, give it a title, and set some properties
        JFrame.setDefaultLookAndFeelDecorated(true);
        mainFrame = new JFrame(PROJECT_TITLE);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize the CPU controller and the current data frame counts
        cpuController = new DataController();
        dataCount = 0;
        totalDataCount = 0;
        
        /* ****************
         * CPU UPDATE CYCLE
         * ****************
         * This action listioner, updateLabels, is a timed event that happens based off of UPDATE_SPEED (ie. duration)
         * Every duration, the update does the following process:
         *          1) Check to see if we are currently done with start up; if yes... else, set initialization messages
         *          2) Check to see if the simulation is currently running (determined by the Play/Pause button; if yes...
         *          3) Run one cycle of the CPU simulation, with the controller retreiving all relevant data
         *          4) Update all of the labels associated with the data
         */
        ActionListener updateLabels = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(!startUpDone)
                {
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Initialization done!");
                    cpuStatusLabel.setText(CPU_STATUS_MESSAGE + "Initialization done!");
                }
                else
                {                    
                    if(isRunning)
                    {
                        runCpu();
                        updateRegisterLabels();
                    }
                }
                
                startUpDone = true;
            }
        };
        
        // Set up the timer with the delay, and the action above; start the timer
        updateTimer = new Timer(UPDATE_SPEED, updateLabels);
        updateTimer.start();
        
        // Add and initialize all of the content panels to the main frame
        addMenuComponents(mainFrame);
        addStatusBarComponents(mainFrame);
        addContentPanel(mainFrame);
        
        // Initialize the CPU Registers content & layout
        initializeContentPanels();
        
        // Pack the frame, and display it to our user in the center of the screen
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * Initialize and add all of the menu components and items, and add them to the associated frame
     * 
     * @param theFrame
     *          The frame to add the menu components to
     * @since 0.0.a
     */
    private static void addMenuComponents(JFrame theFrame)
    {
        // Initialize and set various menu bar properties
        menuBar = new BackgroundMenuBar();
        menuBar.setColor(COLOR_MENU_BAR);
        menuBar.setOpaque(true);
        menuBar.setPreferredSize(new Dimension(theFrame.getWidth(), BAR_HEIGHT));
        
        // Initialize and set various file menu properties 
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(FILE_ACCESSIBILITY_STRING);
        fileMenu.setBackground(COLOR_MENU_BAR);
        
        // Initialize the about item, and set its background color
        aboutItem = new JMenuItem("About");
        aboutItem.setBackground(COLOR_MENU_BAR);
        
        // On click, pop up a small display panel about the emulator
        aboutItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                
            }
        });
        
        // Initialize the exit item, and set its background color
        exitItem = new JMenuItem("Exit");
        exitItem.setBackground(COLOR_MENU_BAR);
        
        // On click, pop up a warning (confirming exit). Exit if it is confirmed.
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
        
        // Add the about and exit item to the file menu
        fileMenu.add(aboutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Initialize and set various file menu properties 
        optionsMenu = new JMenu("Options");
        optionsMenu.setBackground(COLOR_MENU_BAR);
        
        // Initialize the settings item, and set its background color
        settingsItem = new JMenuItem("Settings");
        settingsItem.setBackground(COLOR_MENU_BAR);
        
        // On click, pop up an OptionsFrame / Tabbed Pane of options
        settingsItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                
            }
        });
        
        // Initialize the update item, and set its background color
        updateItem = new JMenuItem("Check for Updates...");
        updateItem.setBackground(COLOR_MENU_BAR);
        
        // On click, create a background thread to check for updates to this GUI / Emulator
        updateItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Perform network update checks
            }
        });
        
        // Add the settings and update item to the options menu
        optionsMenu.add(settingsItem);
        optionsMenu.addSeparator();
        optionsMenu.add(updateItem);
               
        // Add both the file and options menu to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        
        // Add the menu bar to the frame
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
        
        animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Beginning Initialization. . .");
        cpuStatusLabel.setText(CPU_STATUS_MESSAGE + "Beginning Initialization. . .");
        
        theFrame.add(statusBar, BorderLayout.SOUTH);
    }
    
        
    private static void initializeContentPanels()
    {
        addPanelTitles();
        addAnimPanelContent();
        addCpuPanelContent();
        addOpcodePanelContent();
        addMemoryPanelContent();
    }
    
    private static void addContentPanel(JFrame theFrame)
    {
        contentPanel = new JPanel();
        contentPanel.setOpaque(true);
        contentPanel.setBackground(COLOR_PANEL_BACKGROUND);
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
        othersLabel = new JLabel[7];
        othersContent = new JLabel[7];
        flagsBinary = new JLabel("00000000");
        ghostFlagsBinary = new JLabel("00000000");
        
        Insets insets = cpuContentPanel.getInsets();
        Dimension sizeLabel, sizeContent, sizeBinary;
        
        /*
         * REGISTER A -- ACCUMULATOR
         */
        registersLabel[0] = new JLabel(REGISTER_STRINGS[0]);
        registersContent[0] = new JLabel("0x00");
        registersLabel[0].setFont(SUB_TITLE_FONT);
        registersContent[0].setFont(SUB_TITLE_FONT);
        registersContent[0].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[1].setBorder(new BevelBorder(BevelBorder.LOWERED));
        registersContent[1].setHorizontalAlignment(JLabel.CENTER);
                
        flagsBinary.setFont(SUB_TITLE_FONT);
        flagsBinary.setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[2].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[3].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[4].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[5].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[6].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[7].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[8].setBorder(new BevelBorder(BevelBorder.LOWERED));
        registersContent[8].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[8].getPreferredSize();
        sizeContent = registersContent[8].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[8]);
        cpuContentPanel.add(registersContent[8]);
        
        registersLabel[8].setBounds(insets.left + LEFT_OFFSET, registersLabel[6].getY() + registersLabel[6].getHeight() + 50,
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
        registersContent[9].setBorder(new BevelBorder(BevelBorder.LOWERED));
        registersContent[9].setHorizontalAlignment(JLabel.CENTER);
        
        ghostFlagsBinary.setFont(SUB_TITLE_FONT);
        ghostFlagsBinary.setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[10].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[11].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[12].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[13].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[14].setBorder(new BevelBorder(BevelBorder.LOWERED));
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
        registersContent[15].setBorder(new BevelBorder(BevelBorder.LOWERED));
        registersContent[15].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = registersLabel[15].getPreferredSize();
        sizeContent = registersContent[15].getPreferredSize();
        
        cpuContentPanel.add(registersLabel[15]);
        cpuContentPanel.add(registersContent[15]);
        
        registersLabel[15].setBounds(registersContent[14].getX() + registersContent[14].getWidth() + 40,
                                registersLabel[14].getY(), sizeLabel.width, sizeLabel.height);
       
        registersContent[15].setBounds(registersLabel[15].getX() + registersLabel[15].getWidth() + 25,
                                        registersLabel[15].getY() - 7, sizeContent.width + 10, sizeContent.height + 10);

                                        
        /*
         * Adding extra CPU data.... Program Counter
         */
        othersLabel[2] = new JLabel(OTHER_DATA_STRINGS[2]);
        othersContent[2] = new JLabel("0x0000");
        othersLabel[2].setFont(SUB_TITLE_FONT);
        othersLabel[2].setHorizontalAlignment(JLabel.CENTER);
        othersContent[2].setFont(SUB_TITLE_FONT);
        othersContent[2].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[2].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel =  othersLabel[2].getPreferredSize();
        sizeContent = othersContent[2].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[2]);
        cpuContentPanel.add(othersContent[2]);
        
        othersLabel[2].setBounds(LEFT_OFFSET, 435, sizeLabel.width, sizeLabel.height);
        othersContent[2].setBounds(othersLabel[2].getX() + 28, othersLabel[2].getY() + othersLabel[2].getHeight() + 3,
                                        sizeContent.width + 10 , sizeContent.height + 10);
         
                                        
        /*
         * Adding extra CPU data.... Stack Pointer
         */
        othersLabel[3] = new JLabel(OTHER_DATA_STRINGS[3]);
        othersContent[3] = new JLabel("0x0000");
        othersLabel[3].setFont(SUB_TITLE_FONT);
        othersLabel[3].setHorizontalAlignment(JLabel.CENTER);
        othersContent[3].setFont(SUB_TITLE_FONT);
        othersContent[3].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[3].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel =  othersLabel[3].getPreferredSize();
        sizeContent = othersContent[3].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[3]);
        cpuContentPanel.add(othersContent[3]);
        
        othersLabel[3].setBounds(LEFT_OFFSET + 168, 435, sizeLabel.width, sizeLabel.height);
        othersContent[3].setBounds(othersLabel[3].getX() + 25, othersLabel[3].getY() + othersLabel[3].getHeight() + 3,
                                        sizeContent.width + 10 , sizeContent.height + 10);
                                        
                                               
        
        /*
         * Adding extra CPU data.... Index X
         */
        othersLabel[0] = new JLabel(OTHER_DATA_STRINGS[0]);
        othersContent[0] = new JLabel("0x0000");
        othersLabel[0].setFont(SUB_TITLE_FONT);
        othersLabel[0].setHorizontalAlignment(JLabel.CENTER);
        othersContent[0].setFont(SUB_TITLE_FONT);
        othersContent[0].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[0].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel =  othersLabel[0].getPreferredSize();
        sizeContent = othersContent[0].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[0]);
        cpuContentPanel.add(othersContent[0]);
        
        othersLabel[0].setBounds(LEFT_OFFSET + 338, 435, sizeLabel.width, sizeLabel.height);
        othersContent[0].setBounds(othersLabel[0].getX() - 4, othersLabel[0].getY() + othersLabel[0].getHeight() + 3,
                                        sizeContent.width + 10 , sizeContent.height + 10);
                                        
        /*
         * Adding extra CPU data.... Index Y
         */
        othersLabel[1] = new JLabel(OTHER_DATA_STRINGS[1]);
        othersContent[1] = new JLabel("0x0000");
        othersLabel[1].setFont(SUB_TITLE_FONT);
        othersLabel[1].setHorizontalAlignment(JLabel.CENTER);
        othersContent[1].setFont(SUB_TITLE_FONT);
        othersContent[1].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[1].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel =  othersLabel[1].getPreferredSize();
        sizeContent = othersContent[1].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[1]);
        cpuContentPanel.add(othersContent[1]);
        
        othersLabel[1].setBounds(LEFT_OFFSET + 475, 435, sizeLabel.width, sizeLabel.height);
        othersContent[1].setBounds(othersLabel[1].getX() - 4, othersLabel[1].getY() + othersLabel[1].getHeight() + 3,
                                        sizeContent.width + 10 , sizeContent.height + 10);
                                        
                                        
        /*
         * Adding extra CPU data.... Interrupt Register
         */
        othersLabel[4] = new JLabel(OTHER_DATA_STRINGS[4]);
        othersContent[4] = new JLabel("0x00");
        othersLabel[4].setFont(SUB_TITLE_FONT);
        othersLabel[4].setHorizontalAlignment(JLabel.CENTER);
        othersContent[4].setFont(SUB_TITLE_FONT);
        othersContent[4].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[4].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = othersLabel[4].getPreferredSize();
        sizeContent = othersContent[4].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[4]);
        cpuContentPanel.add(othersContent[4]);
        
        othersLabel[4].setBounds(LEFT_OFFSET, 510,
                                        sizeLabel.width, sizeLabel.height);
        
        othersContent[4].setBounds(othersLabel[4].getX() + 37, othersLabel[4].getY() + othersLabel[4].getHeight() + 3,
                                        sizeContent.width + 10, sizeContent.height + 10);      
                                        
                                        
        /*
         * Adding extra CPU data.... Refresh Register
         */
        othersLabel[5] = new JLabel(OTHER_DATA_STRINGS[5]);
        othersContent[5] = new JLabel("0x00");
        othersLabel[5].setFont(SUB_TITLE_FONT);
        othersLabel[5].setHorizontalAlignment(JLabel.CENTER);
        othersContent[5].setFont(SUB_TITLE_FONT);
        othersContent[5].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[5].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = othersLabel[5].getPreferredSize();
        sizeContent = othersContent[5].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[5]);
        cpuContentPanel.add(othersContent[5]);
        
        othersLabel[5].setBounds(LEFT_OFFSET + 170, 510, sizeLabel.width, sizeLabel.height);
                                        
        othersContent[5].setBounds(othersLabel[5].getX() + 33, othersLabel[5].getY() + othersLabel[5].getHeight() + 3,
                                        sizeContent.width + 10, sizeContent.height + 10);
                                        
                                        
        /*
         * Adding extra CPU data.... Indexer
         */
        othersLabel[6] = new JLabel(OTHER_DATA_STRINGS[6]);
        othersContent[6] = new JLabel("0x00");
        othersLabel[6].setFont(SUB_TITLE_FONT);
        othersLabel[6].setHorizontalAlignment(JLabel.CENTER);
        othersContent[6].setFont(SUB_TITLE_FONT);
        othersContent[6].setBorder(new BevelBorder(BevelBorder.LOWERED));
        othersContent[6].setHorizontalAlignment(JLabel.CENTER);
        
        sizeLabel = othersLabel[6].getPreferredSize();
        sizeContent = othersContent[6].getPreferredSize();
        
        cpuContentPanel.add(othersLabel[6]);
        cpuContentPanel.add(othersContent[6]);
        
        othersLabel[6].setBounds(LEFT_OFFSET + 339, 510, sizeLabel.width, sizeLabel.height);
                                        
        othersContent[6].setBounds(othersLabel[6].getX() + 5, othersLabel[6].getY() + othersLabel[6].getHeight() + 3,
                                        sizeContent.width + 10, sizeContent.height + 10);                                
                                                                                                                                
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
        Dimension sizeLabel;
        
        animContentPanel = new JPanel();
        animContentPanel.setBackground(COLOR_PANEL_BACKGROUND);
        animPanel.add(animContentPanel, BorderLayout.CENTER);
        
        animControlPanel = new JPanel();
        animControlTitle = new JLabel(CONTROL_TITLE);
        animControlPanel.setPreferredSize(new Dimension(animPanel.getWidth(), CONTROL_HEIGHT));
        
                // Frame Labels
        frameCountLabel = new JLabel("Frame number: "+ dataCount + "/" + totalDataCount);
        frameCountLabel.setFont(SUB_FONT);
        frameCountLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        sizeLabel = frameCountLabel.getPreferredSize();
        
        animControlPanel.add(frameCountLabel);
        
        frameCountLabel.setBounds(10, 2, sizeLabel.width + 50, sizeLabel.height + 5);

                 
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
        controlPlay.addMouseListener(new MouseAdapter()
        {
            public void  mouseEntered(MouseEvent e)
            {
               messageLabel.setText(ABOUT_PLAY);
            }
            
            public void mouseClicked(MouseEvent e)
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
            
            public void mouseClicked(MouseEvent e)
            {
                if(isRunning)
                {
                    isRunning = false;
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Step. . .");
                    runCpu();
                    updateRegisterLabels();
                }
                else
                {
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Step. . .");
                    runCpu();
                    updateRegisterLabels();
                }  
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
            
            public void mouseClicked(MouseEvent e)
            {
                if(isRunning)
                {
                    isRunning = false;
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Back Step. . .");
                    
                    
                    cpuDataPack = cpuController.backStep();
                    if(cpuDataPack != null)
                    {
                        cpuRegisterData = cpuDataPack.getRegisterData();
                        cpuFullData = cpuDataPack.getFullRegisterData();
                        cpuOtherData = cpuDataPack.getOtherData();
                        dataCount--;
                    }
                    updateRegisterLabels();
                }
                else
                {
                    animationStatusLabel.setText(ANIM_STATUS_MESSAGE + "Back Step. . .");
                    
                    cpuDataPack = cpuController.backStep();
                    if(cpuDataPack != null)
                    {
                        cpuRegisterData = cpuDataPack.getRegisterData();
                        cpuFullData = cpuDataPack.getFullRegisterData();
                        cpuOtherData = cpuDataPack.getOtherData();
                        dataCount--;
                    }
                    updateRegisterLabels();
                }  
            }
        });
    }
    
    private static void runCpu()
    {
        
        cpuDataPack = cpuController.nextStep();
        cpuRegisterData = cpuDataPack.getRegisterData();
        cpuFullData = cpuDataPack.getFullRegisterData();
        cpuOtherData = cpuDataPack.getOtherData();
         
        if(dataCount == totalDataCount)
        {
            totalDataCount++;
            dataCount++;
        }
        else
        {
           dataCount++; 
        }
    }
    
    private static void updateRegisterLabels()
    {
        // NORMAL REGISTERS
        registersContent[0].setText(Utils.toHex16(cpuRegisterData[0], true));         // CPU REG DATA: A BC DE HL F
        registersContent[1].setText(Utils.toHex16(cpuRegisterData[7], true));         // regCont[1] == F
        registersContent[2].setText(Utils.toHex16(cpuRegisterData[1], true)); 
        registersContent[3].setText(Utils.toHex16(cpuRegisterData[2], true));
        registersContent[4].setText(Utils.toHex16(cpuRegisterData[3], true));
        registersContent[5].setText(Utils.toHex16(cpuRegisterData[4], true));
        registersContent[6].setText(Utils.toHex16(cpuRegisterData[5], true));
        registersContent[7].setText(Utils.toHex16(cpuRegisterData[6], true));        
        
        // GHOST REGISTERS
        registersContent[8].setText(Utils.toHex16(cpuRegisterData[8], true));
        registersContent[9].setText(Utils.toHex16(cpuRegisterData[15], true));
        registersContent[10].setText(Utils.toHex16(cpuRegisterData[9], true));
        registersContent[11].setText(Utils.toHex16(cpuRegisterData[10], true));
        registersContent[12].setText(Utils.toHex16(cpuRegisterData[11], true));
        registersContent[13].setText(Utils.toHex16(cpuRegisterData[12], true));
        registersContent[14].setText(Utils.toHex16(cpuRegisterData[13], true));
        registersContent[15].setText(Utils.toHex16(cpuRegisterData[14], true));
        
        // BINARY FLAGS
        flagsBinary.setText(Utils.padBinary(Utils.toBinary(cpuRegisterData[7]), 8));
        ghostFlagsBinary.setText(Utils.padBinary(Utils.toBinary(cpuRegisterData[15]), 8));
        
        // FULL WIDTH REGISTERS
        /*
        fullContent[0].setText(Utils.toHex32(cpuFullData[0], true));
        fullContent[1].setText(Utils.toHex32(cpuFullData[1], true));
        fullContent[2].setText(Utils.toHex32(cpuFullData[2], true));
        fullContent[3].setText(Utils.toHex32(cpuFullData[3], true));
        fullContent[4].setText(Utils.toHex32(cpuFullData[4], true));
        fullContent[5].setText(Utils.toHex32(cpuFullData[5], true));  
        */
        
        // OTHER DATA -- POINTERS, COUNTERS, ETC.
        othersContent[0].setText(Utils.toHex32(cpuOtherData[0], true));
        othersContent[1].setText(Utils.toHex32(cpuOtherData[1], true));
        othersContent[2].setText(Utils.toHex32(cpuOtherData[2], true));
        othersContent[3].setText(Utils.toHex32(cpuOtherData[3], true));
        othersContent[4].setText(Utils.toHex16(cpuOtherData[4], true));
        othersContent[5].setText(Utils.toHex16(cpuOtherData[5], true));
        othersContent[6].setText(Utils.toHex16(cpuOtherData[6], true));  
        
        // FRAME COUNT 
        frameCountLabel.setText("Frame number: "+ dataCount + "/" + totalDataCount);
    }
    
    private static void shutdown()
    {
        mainFrame.dispose();
        System.exit(0);
    }
}