/*
====================================================================================


									| BlueBook BETA | 

Scope: 
> Multi purpose systems engineering tool for space applications
> Analyse and superwise mass growth behaviour of space systems with strong propulsive 
  requirements in the wake of systems development
> Outline the effect of dry mass changes on wet mass
> Calculate performance indicators


Version 1.04
Author: Max Braun
Date: 30/04/2018


ChangeLog

> Version 0.00 
	by Max Braun, 12/01/18
	First draft - integration of existing tools in new framework
	Build up and integration of bottom up algorithm
	
> Version 1.01 ALPHA
	by Max Braun 14/01/18
	First stable runs top/down and bottom up with streamlined algorithms
	New data package format allows to export multiple sources to one file 
	Redesigned user interface allows for more detail in systems design
	19/02/18 Bottom|Up and Top|Down functions show convergence in results for all 
			 implemented margin scenarios
			 Losses are computed at the correct position and in the correct amount
			 for all cases 

> Version 1.02 ALPHA
	by Max Braun 22/02/2018
	Anchored performance for Top|Down and Bottom|Up algorithms w and w/o all included 
	options.
	Extended input section with downgrade-compatible import design
	Detailed Fuel overview section
	Added Performance Parameters
	
> Version 1.03 ALPHA
	by Max Braun 03/04/2018
	Charts create/update works without delays and/or errors 
	Individual Crosshair feature for each chart
	Extended set of charts 
	Extended use of action listener/ Developing setting options
	
> Version 1.04 BETA 
	by Max Braun 25/04/2018
	Chart setup finalized and free of errors
	Improved runtime
	Consolidated system of action listener
	Setup section/Propulsion area including propellant library/Summary area/ 
	
	
=====================================================================================
 */
package bluebook;
import java.io.*;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
//import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

//import javax.imageio.ImageIO;

//import java.sql.*;
//import java.util.Scanner;

//import javax.swing.ImageIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//import java.awt.Color;
import java.awt.event.ActionListener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
//import java.awt.FlowLayout;
import java.awt.Font;
//import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.LayoutManager;
//import java.awt.Paint;
import java.awt.event.ActionEvent;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
//import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
//import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
//import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
//import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Crosshair;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
//import org.jfree.chart.renderer.category.StandardBarPainter;
//import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;




//import javax.swing.JFrame;
//import java.lang.ProcessBuilder;

//import javax.imageio.ImageIO;

public class BlueBook_main implements  ActionListener {

    static // Definition of global values and items that are part of the GUI.
	String PROJECT_TITLE = "  BlueBook BETA  V1.05";
    //public static URL url_smalllogo  = BlueBook_main.class.getResource("/resources/logo.png");
    static public File url_smalllogo = new File("C://bb_logo.png"); 
    //public static String PATH_LOGO = "C:\\logo2.1.jpg";
    public static String BB_delimiter = ",";
    String redScoreAmount = "void";
    int blueScoreAmount = 0;
    public static double E = 2.7182818284590452353602874713527; 	// Euler number
    public static double g_0 = 9.80665;   							// Average gravitational factor [m/s^2]
    public static double gas_constant = 8314;    					// Universal gas constant [Si]
    public static double d = 0.8387636; // See lunar lander function 
    static double margin_prop = 0;         // Fuel margin [percent]
    static double m_init;
    static double m_dry;
    static double m_wet; 
    static double m_payload;
    double boiloff_rate = 0;
    int int_child_number = 1;
    static double store_user_input = 0 ; 
    
    public static JLabel l_loading_progress; 
    public static JFrame loading_frame;

    public static int app_template = 0 ; 
    
    public static JTextArea textArea = new JTextArea();
    public static TextAreaOutputStream  taOutputStream = new TextAreaOutputStream(textArea, ""); 
    
    public static double b_l = 0.5;
    public static double b_u = 1.5;				// lower/upper boundaries
    
    double glob_m_fuel = 0;
    double glob_m_dry =  0 ;
    static double global_pmr =0;
    double global_sc_wom = 0 ; 
    
    static int x_init = 1570;
    static int y_init = 830 ;
    
    static int frame_x = 0;
    static int frame_y = 0; 
    
    
    public static File myfile_opened; 
    public static String myfile_name =""; 
    
    public static String PN_T1_00 = "Overview";
    public static String PN_T1_01 = "S/C Equipment List";
    public static String PN_T1_02 = "S/C Propulsion";
    public static String PN_T1_03 = "S/C Power";
    
    
    public String content, lae_content, lde_content;

    public static JFrame MAIN_frame;
    
    public static JFrame frame_settings;
    public static JFrame frame_AddEngine;
    public static JFrame EngineeringModel_frame; 
    public static JPanel resPanel;
	public static JPanel mainPanel1;
	public static JPanel mainPanel2;
	public static JPanel mainPanel3;
	public static JPanel mainPanel31;
	public static JPanel mainPanel32;
	public static JPanel CasePanel;
	public static JPanel mar_pan_pl;
	public static JPanel mar_pan_sc;
	public static JPanel resPanel2;
	public static JPanel add_panel;
	public static JPanel resPanel41;
	public static JPanel add_opt_panel,add_opt_panel2,add_opt_panel3,plotsub_P132;
	public static JPanel P1_page04; 
	public static JScrollPane TabPanel;
	public static JPanel P1_page01, P1_page02,P1_page03, mari_bo, mari_sl, mari_mar, mari_bo2, mari_sl2, mari_mar2;
	public static JLabel rp2_1, rp2_2,rp2_3, rp2_4,rp2_5, rp2_6, rp2_7, rp2_8, rp2_9, rp2_10,FileLabel,pg_l8, mari_pmr, Lres5;
	public static JLabel rp4_1, rp4_2,rp4_3, rp4_4,rp4_5, rp4_6, rp4_7, rp4_8, rp4_9,rp4_10,rp4_11,rp4_12, rp4_13, rp4_14, rp4_15, rp4_16,rp4_17,rp4_18,rp4_19,rp4_21,rp4_22,rp2_d1,rp2_d2;
	public static JPanel plotsub_21, plotsub_22; 
	public static JPanel plotsub_31, plotsub_32;
	public static JPanel plotsub_41, plotsub_42, plotsub_43, plotsub_44,plotsub_45,plotsub_46, plotsub_47, plotsub_48,plotsub_P131;
	public static JPanel plotsub_421, plotsub_422;
	public static JLabel Linp1, Linp2, Lres1, Lres2, Lres3, Lres4, Res1, Res2, Res3, Res4,  boiloff_label, boiloff_label2, steering_label, margin_label , FuelMar_label, FuelMar_label2;
	public static JLabel boiloff_label4, steering_label4, margin_label4;
	public static JLabel Res41, Res42, Res43, Res44, Res45;
	public static JLabel  cd_l4, cd_l5, cd_l6,cd_l7;
	public static JLabel sc_1, sc_2, sc_3, sc_4,sc_5,sc_6, sc_7, sc_8, sc_9, sc_10,sc_11,sc_12,sc_13,sc_14; 
	public static JLabel  pl_2, pl_3, pl_4,pl_10,pl_11,pl_12,pl_13,pl_14; 
	public static JLabel  en_1 ;
	public static JButton redButton, blueButton, resetButton, greenButton, purpleButton, yellowButton,refButton;
	public static JButton sc_mark, sc_unmark;
	public static JButton moveup, movedown,button_int,deltav_load;
	public static JButton pl_add, pl_delete, pl_up, pl_down, pl_update, pl_load;
	public static JButton sc_add, sc_delete, sc_up, sc_down, sc_update, sc_load;
    public static JButton en_add, en_delete, en_update;
    public static JTextField   Minit, Mpayload, BoilOff_TF, FuelMar_TF,sc_TF_01,sc_TF_02,sc_TF_03,rp4_20;
    public static JTextField cd_tf1,analysis_TF_01;
    public static JLabel sum_title, add2_01, add2_02, add2_03, add2_04, add2_05, add2_06, add2_07,add2_08;
    JPanel analysis_infoboard;
    static JLabel aib_md, aib_md2;
	static JLabel aib_mw, aib_mw2;
	static JLabel aib_mp, aib_mp2;
	static JLabel aib_mtw, aib_mtw2;
	static JLabel aib_mpay, aib_mpay2,aib_warning; 
	public static JCheckBox d_cap; 


    public static JCheckBox pg_cb1;
    public static JTextField pg_t2,pg_t3,pg_t4,pg_t5,pg_t6;
	
	static BorderLayout BB_BL = new BorderLayout();
	

	Marker marker44;
	
	public static JCheckBox boiloff_box, steering_box, margin_box;
	public static JCheckBox boiloff_box4, steering_box4, margin_box4;
	public static JCheckBox sc_cb1,sc_cb2,sc_cb3, sc_cb4,sc_cb5,sc_cb6,sc_cb51,sc_cb61,a_cb1;
	public static JCheckBox cb_sub41,cb_sub42,cb_sub43,cb_sub44,cb_sub45,cb_sub46,cb_sub47,cb_sub48,sc_cb_1,deltav_cb_1;
	public static JCheckBox pl_cb1,pl_cb2,pl_cb3,sc_cb53,pl_cb_1;
	public static JCheckBoxMenuItem  cbMenuItem_BO,cbMenuItem_SL,cbMenuItem_MAR;
	public static JTable table;		// Delta-V Top-Down section	
	public static JTable table2;	// Delta-V Element definition section (Master)
	public static JTable table3;	// Payload table
	public static JTable table4;	// Delta-V Bottom-Up section
	public static JTable table5;	// S/C table
	public static JTable table6;	// Propulsion table
	public static JTable table7;	// Propulsion Bottom-Up table
	public static JTable table8;	// Power table 
	public static DefaultTableModel model, model2, model3, model4, model5, model6, model7, model8;
	
    String[] columns = { "Event", "Delta-V [m/s]", "ISP [s]", "Steering loss margin [%]", "Margin [%]", "Final Delta-V [m/s]", "Time T+ [days] ",  "Add. Propellant [kg]", "Add. Propellant to compensate Boil-off",  "Propellant mass [kg]", "S/C mass [kg] |pre|", "S/C mass [kg] |post|", "Tank content [kg]"};
    String[] columns2 = { "Event", "Delta-V [m/s]", "ISP [s]", "Steering [%]", "Margin [%]", "   ", "Time T+ [days] ",  "Add. Propellant [kg]", "Engine [-]","INC"};
    String[] columns3 = { "Element", "Quantity", "Mass [kg]", "MGA [%]", "MGA [kg]","MER [%]","MER [kg]","PMR [kg]","Mass w/M [kg]","INC", "ID"};
    String[] columns4 = { "Event", "Delta-V [m/s]", "ISP [s]", "Steering loss margin [%]", "Margin [%]", "Final Delta-V [m/s]", "Time T+ [days] ",  "Add. Propellant [kg]", "Add. Propellant to compensate Boil-off",  "Propellant mass [kg]", "S/C mass [kg] |pre|", "S/C mass [kg] |post|", "Tank content [kg]"};
    String[] columns5 = { "Element", "Quantity", "Mass [kg]", "MGA [%]", "MGA [kg]","MER [%]","MER [kg]","PMR [kg]","Mass w/M [kg]","INC", "ID"};
    String[] columns6 = { "Element", "ISP", "Conditioning Propellant", "Thrust [N]", "O/F", "\u03C1 (Fuel) [kg/m3]" , "\u03C1 (Oxidizer) [kg/m3]", "Conditioning Pressurant"};
    String[] columns7 = { "Propulsion Element", "Prop. Total [kg]", "Fuel Total [kg]", "Oxidizer Total [kg]", "Fuel Total [litre]", "Oxidizer Total [litre]", "Pressurant [kg]", "Pressurant [litre]"};
    String[] columns8 = { "Element", "Quantity", "Power consumption [W]", "Power provision [W]", "Capacity [Ws]", "Capacity margin [%]" ,"Capacity w/m [Ws]", "INC", "ID"};
    Object[] row  = new Object[13];
    Object[] row2 = new Object[10];
    Object[] row3 = new Object[11];
    Object[] row4 = new Object[13];
    Object[] row5 = new Object[11];
    Object[] row6 = new Object[8];
    Object[] row7 = new Object[11];
    Object[] row8 = new Object[9];
    
    public static JTabbedPane tabbedPane; 		// Main Tabbed pane
    public static JTabbedPane subtabPane11;       // Subtabbed pane - for Pane 1 Input definition
    public static JTabbedPane subtabPane1; 		// Subtabbed pane - Plot area in pane 2  XY chart
    public static JTabbedPane subtabPane3; 		// Subtabbed pane2 - Plot area in pane 2 bar chart
    public static JTabbedPane subtabPane31; 	
    public static JTabbedPane subtabPane4; 		// Subtabbed pane - Plot area in pane 3  XY chart
    public static JTabbedPane subtabPane41; 		// Subtabbed pane - Plot area in pane 3 bar chart
    public static JTabbedPane subtabPane_P3; 
    
    public static JPanel ChildPanel1;
    
    public static JMenuBar menuBar, menuBar2;
    public static JMenu menu, menu2, menu21, menu22, menu3, menu4;
    public static JMenuItem mI1, mI2, mI3, mI4,mI5;
    public static JMenuItem mII1;
    public static JMenuItem mIII1, mIII2;
    public static JMenuItem mI21, mI22,mI23,mI24,mI25; 
    
    public static boolean a_complex = false; 
	
	BufferedImage myImage ;
	
	static ChartPanel chartPanel ;
	static ChartPanel chartPanel2;
	static ChartPanel chartPanel3;
	static ChartPanel chartPanel4;
	static ChartPanel chartPanel31;
	
	public static ChartPanel CP_Convergence;
	public static JPanel mainPanelh5 ;
	
    JFreeChart chart, chart2, chart3;
	static JFreeChart chart131;
	JFreeChart chart4;
	JFreeChart chart5;
	JFreeChart chart6;
	JFreeChart chart7;
	JFreeChart chart8;
	static JFreeChart chart41;
	JFreeChart chart9;
	JFreeChart chart10;
	static JFreeChart chart11;
	JFreeChart chart12;
	static JFreeChart chart13;
	static JFreeChart chart101;
	static JFreeChart chart111;
	static JFreeChart chart14;
	static JFreeChart chart15;
	static JFreeChart chart16;
	
	static JFreeChart chartA3_1;
	static JFreeChart chartA3_2;
	static JFreeChart chartA3_3;
	static JFreeChart chartA3_4;
	
    
    boolean chart4_fd = true;
    boolean chart5_fd = true;
    boolean chart6_fd = true;
    boolean chart7_fd = true;
    boolean chart8_fd = true;
    boolean chart9_fd = true;
    boolean chart10_fd = true;
    boolean chart11_fd = true;
    boolean chart12_fd = true;
    boolean chart13_fd = true;
    boolean chart14_fd = true;
    boolean chart15_fd = true;
    boolean chart16_fd = true;
    
 
    
    static boolean chartA1_fd = true;
    static boolean chartA2_fd = true;
    static boolean chartA3_fd = true;
    
    static ChartPanel CP4;
    static ChartPanel CP6;
    static ChartPanel CP8;
    static ChartPanel CP9;
    static ChartPanel CP10;
    static ChartPanel CP11;
    static ChartPanel CP12;
    static ChartPanel CP13;
    static ChartPanel CP14;
    static ChartPanel CP15;
    static ChartPanel CP16;
    
    static ChartPanel CP_A31;
    static ChartPanel CP_A32;
    static ChartPanel CP_A33;
    static ChartPanel CP_A34;
    
    private static Crosshair xCrosshair_4;
    private static Crosshair yCrosshair_4;
    
    private static Crosshair xCrosshair_6;
    private static Crosshair yCrosshair_6;
    
    private static Crosshair xCrosshair_8;
    private static Crosshair yCrosshair_8;
    
    private static Crosshair xCrosshair_9;
    private static Crosshair yCrosshair_9;
    
    private static Crosshair xCrosshair_10;
    private static Crosshair yCrosshair_10;
    
    private static Crosshair xCrosshair_11;
    private static Crosshair yCrosshair_11;
    
    private static Crosshair xCrosshair_12;
    private static Crosshair yCrosshair_12;
    
    private static Crosshair xCrosshair_13;
    private static Crosshair yCrosshair_13;
    
    private static Crosshair xCrosshair_A1;
    private static Crosshair yCrosshair_A1;
    private static Crosshair xCrosshair_A2;
    private static Crosshair yCrosshair_A2;
    
    private static Crosshair xCrosshair_A3;
    private static Crosshair yCrosshair_A3;
    private static Crosshair xCrosshair_A4;
    private static Crosshair yCrosshair_A4;
    
    private static Crosshair xCrosshair_A3_1;
    private static Crosshair yCrosshair_A3_1;
    private static Crosshair xCrosshair_A3_2;
    private static Crosshair yCrosshair_A3_2;
    private static Crosshair xCrosshair_A3_3;
    private static Crosshair yCrosshair_A3_3;
    private static Crosshair xCrosshair_A3_4;
    private static Crosshair yCrosshair_A3_4;
    
    private static Crosshair xCrosshair_A3_4PMF;
    private static Crosshair yCrosshair_A3_4PMF;
    
    private static Crosshair xCrosshair_14;
    private static Crosshair yCrosshair_14;
    
    private static Crosshair xCrosshair_15;
    private static Crosshair yCrosshair_15;
    
    private static Crosshair xCrosshair_16;
    private static Crosshair yCrosshair_16;
   
   // ChartPanel CP4 = new ChartPanel(chart4);;// = new ChartPanel(chart4);
    Marker marker1=null;
    Marker marker2, marker3, marker4;

    DefaultTableXYDataset 	result1  = new DefaultTableXYDataset();
    DefaultTableXYDataset 	result2  = new DefaultTableXYDataset();
    DefaultCategoryDataset 	result3 = new DefaultCategoryDataset();
    DefaultTableXYDataset  	result4 = new DefaultTableXYDataset();
    static DefaultTableXYDataset  	result41 = new DefaultTableXYDataset();
    DefaultCategoryDataset 	result5 = new DefaultCategoryDataset();
    DefaultTableXYDataset  	result6 = new DefaultTableXYDataset();
    DefaultCategoryDataset	result7 = new DefaultCategoryDataset();
    DefaultTableXYDataset 	result8  = new DefaultTableXYDataset();
    DefaultTableXYDataset 	result9  = new DefaultTableXYDataset();
    DefaultTableXYDataset 	result10 = new DefaultTableXYDataset();
    static DefaultTableXYDataset 	result11 = new DefaultTableXYDataset();
    DefaultTableXYDataset 	result12 = new DefaultTableXYDataset();
    static DefaultTableXYDataset 	result13 = new DefaultTableXYDataset();
    static DefaultTableXYDataset 	result101 = new DefaultTableXYDataset();
    static DefaultTableXYDataset 	result14 = new DefaultTableXYDataset();
    static DefaultTableXYDataset result11_A2 = new DefaultTableXYDataset();
    static DefaultTableXYDataset 	result15 = new DefaultTableXYDataset();
    static DefaultTableXYDataset result11_A2_2 = new DefaultTableXYDataset();
    
    static DefaultTableXYDataset result11_A3_1 = new DefaultTableXYDataset();
    static DefaultTableXYDataset result11_A3_2 = new DefaultTableXYDataset();
    static DefaultTableXYDataset result11_A3_3 = new DefaultTableXYDataset();
    static DefaultTableXYDataset result11_A3_4 = new DefaultTableXYDataset();
    static DefaultTableXYDataset result11_A3_41 = new DefaultTableXYDataset();
    
    static DefaultTableXYDataset 	result16 = new DefaultTableXYDataset();
    
    static DefaultTableXYDataset RS_convergence = new DefaultTableXYDataset();
    
    // Color Themes: 
    // Default Color Theme
    public static int gg = 235;
    public static Color l_c = new Color(0,0,0);    			// Label Color
   	public static Color bc_c = new Color(255,255,255);			// Background Color
   	public static Color w_c = new Color(gg,gg,gg);				// Box background color
   	public static Color t_c = new Color(255,255,255);				// Table background color
    
    // Bluebook default: 
   	/*
   	Color l_c = new Color(240,240,240);    			// Label Color
   	Color bc_c = new Color(135,142,150);			// Background Color
   	Color w_c = new Color(83,95,108);				// Box background color
   	Color t_c = new Color(113,119,125);				// Table background color
   	*/
   	
    static DecimalFormat df = new DecimalFormat("#.#");
    static DecimalFormat df2 = new DecimalFormat("#");
    Font menufont = new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 12);
    Font labelfont_small = new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 9);
    Font labelfont_small_bold = new Font("Verdana", Font.BOLD, 9);
    Font labelfont_verysmall = new Font("Verdana", Font.BOLD, 7);
    
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "CascadeFiles", "csv");

    int posx_21 = 0;
    int posy_21 = 434;
    int extx_21 = 1110;
    int exty_21 = 310;
    
    int posx_31 = 1120;
    int posy_31 = 270;
    int extx_31 = 210;
    int exty_31 = 475; 
    
    int posx_41 = 730;
    int posy_41 = 5;
    int extx_41 = 600;
    int exty_41 = 517; 
    
    int posx_42 = 5;
    int posy_42 = 520;
    int extx_42 = 710;
    int exty_42 = 210;
    
    int extx_main = 1650;
    int exty_main = 900; 
    
	public static int ext_hx_settings = 495;
	public static int ext_hy_settings = 495;
    
   public static final LayoutManager grid =  null;
   
   public static boolean exp_mar_plan = true;
   
   public static boolean BO_stall  = false;
   public static boolean SL_stall  = false;
   public static boolean MAR_stall = false;
   
   public static double Convergence_Criterea = 0.00000001;
   public static int MAX_ITERATION = 3000;
   public static int MIN_ITERATION = 500; 
   public static int chart_resolution = 15; 
   
   public static double ISP_STEERING = 285;
   public static boolean auto_RCS = true; 
   
   public static boolean b_cap = true; 
   //---------------------------------------------------------
   // Propulsion Library: 
 //---------------------------------------------------------
   public static String[] choices_ox = {"LOX","NTO","MON-3","MON-6"};
   public static String[] choices_fuel = { "H2","CH4", "RP-1","MMH","UDMH"};
   
	double[][] DATA_OF = {
			{5,2.8,2.3,1.2,1.2},
			{0,0,3.5,1.7,1.7},
			{0,0,4,2.1,2.1},
			{0,0,4.4,2.1,2.1},	
	};
   
	double[][] DATA_ISP = {
			{450,350,340,320,320},
			{0,300,300,320,320},
			{0,300,300,320,320},
			{0,300,300,320,320},	
	};
	
	double[] DATA_DENSITY_OXIDIZER = {
			1140,		// LOX
			1450,		//NTO
			1450,		//MON3
			1450,		//MON6
	};
	double[] DATA_DENSITY_FUEL = {
			71,			//H2
			423,		//CH4
			820,		//RP-1
			866,		//MMH
			786,		//UDMH
	};
   
   public static int ox_row = 0; 
   public static int fuel_col = 0;
   
   
   private static List<SubElement> PAYLOAD_LIST = new ArrayList<SubElement>(); 
   private static List<SubElement> SC_LIST = new ArrayList<SubElement>(); 
   
   
   public void UPDATE_PAYLOAD_LIST(SubElement blk){
	   SubElement newSubElement = blk; 	   
	   if (PAYLOAD_LIST.size()==0){
			  // New item -> add to list  
			  BlueBook_main.PAYLOAD_LIST.add(newSubElement); 
	   } else {
		boolean element_exist = false   ;
	  for(int i=0; i<PAYLOAD_LIST.size(); i++){
		  int ID_LIST = PAYLOAD_LIST.get(i).get_ID();
		  int ID_ELEMENT = newSubElement.get_ID();
				  if (ID_LIST == ID_ELEMENT){
					  // item exists -> Update
					  PAYLOAD_LIST.get(i).set_name(newSubElement.get_name());
					  PAYLOAD_LIST.get(i).set_quantity(newSubElement.get_quantity());
					  PAYLOAD_LIST.get(i).setmass_basic(newSubElement.getmass_basic());
					  PAYLOAD_LIST.get(i).setmass_MGA(newSubElement.getmass_MGA());
					  PAYLOAD_LIST.get(i).setmass_MER(newSubElement.getmass_MER());
					  PAYLOAD_LIST.get(i).setmass_PMR(newSubElement.getmass_PMR());
					  PAYLOAD_LIST.get(i).setpower_consumption(newSubElement.getpower_consumption());
					  PAYLOAD_LIST.get(i).setpower_margin(newSubElement.getpower_margin());
					  PAYLOAD_LIST.get(i).setcomments(newSubElement.getcomments());
					  element_exist = true;
				  } 
	  }
		if (element_exist == false ){
			  // New item -> add to list  
			  BlueBook_main.PAYLOAD_LIST.add(newSubElement);
		}	  
	   } 
   }
   
   public void UPDATE_SC_LIST(SubElement blk){
	   SubElement newSubElement = blk; 	   
	   if (SC_LIST.size()==0){
			  // New item -> add to list  
			  BlueBook_main.SC_LIST.add(newSubElement); 
	   } else {
		boolean element_exist = false   ;
	  for(int i=0; i<SC_LIST.size(); i++){
		  int ID_LIST = SC_LIST.get(i).get_ID();
		  int ID_ELEMENT = newSubElement.get_ID();
				  if (ID_LIST == ID_ELEMENT){
					  // item exists -> Update
					  SC_LIST.get(i).set_name(newSubElement.get_name());
					  SC_LIST.get(i).setmass_basic(newSubElement.getmass_basic());
					  SC_LIST.get(i).set_quantity(newSubElement.get_quantity());
					  SC_LIST.get(i).setmass_MGA(newSubElement.getmass_MGA());
					  SC_LIST.get(i).setmass_MER(newSubElement.getmass_MER());
					  SC_LIST.get(i).setmass_PMR(newSubElement.getmass_PMR());
					  SC_LIST.get(i).setpower_consumption(newSubElement.getpower_consumption());
					  SC_LIST.get(i).setpower_margin(newSubElement.getpower_margin());
					  SC_LIST.get(i).setcomments(newSubElement.getcomments());
					  element_exist = true;
				  } 
	  }
		if (element_exist == false ){
			  // New item -> add to list  
			  BlueBook_main.SC_LIST.add(newSubElement);
		}	  
	   } 
   }

   class VerticalMenuBar extends JMenuBar {
   	LayoutManager grid = new GridLayout(0,1);
   	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public VerticalMenuBar() {
   	    setLayout(grid);
   	  }
   	}
   
   
   public JPanel createHelpPane () throws IOException{
   	JPanel MainGUI = new JPanel();
   	MainGUI = new JPanel();
   	MainGUI.setLayout(null);
   	//----------------------------------------------------------------
	JScrollPane TabPanelh = new JScrollPane();
	int ext_hx = 650;
	int ext_hy = 450;
	TabPanelh.setLayout(null);
	TabPanelh.setLocation(5,20);
	TabPanelh.setSize(ext_hx, ext_hy);
	TabPanelh.setBorder(BorderFactory.createLineBorder(Color.black));
	//TabPanel.setBackground(Color.red);
	MainGUI.add(TabPanelh);
	
	JTabbedPane tabbedPaneh = new JTabbedPane();
	tabbedPaneh.setLocation(0, 0);
	tabbedPaneh.setSize(ext_hx, ext_hy);
	//-----------------------------------------------------
	JPanel mainPanelh1 = new JPanel();
	mainPanelh1.setLayout(null);
	mainPanelh1.setLocation(0, 0);
	mainPanelh1.setSize(ext_hx, ext_hy);
	JPanel mainPanelh2 = new JPanel();
	mainPanelh2.setLayout(null);
	mainPanelh2.setLocation(0, 0);
	mainPanelh2.setSize(ext_hx, ext_hy);
	
	//JTable myTable = new JTable((TableModel) new MyTableModel());
	JTable myTable = new JTable(){

        private static final long serialVersionUID = 1L;

        /*@Override
        public Class getColumnClass(int column) {
        return getValueAt(0, column).getClass();
        }*/
	      public boolean isCellEditable(int row, int column){  
	          return false;  
	      }
	      
    }; ;
    String[] columnsh = { "Acronym", "Explanation"};
    DefaultTableModel modelh = new DefaultTableModel();
    modelh.setColumnIdentifiers(columnsh);
    myTable.setModel(modelh);
    int tablewidth2 = 630;
    int tablehight2 = 450;
    
    JPanel panel2 = new JPanel();
    panel2.setLayout(null);
    panel2.setLocation(2,2);
    panel2.setSize(tablewidth2-20,tablehight2-5);
    mainPanelh1.add(panel2);
    
    
    myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    myTable.getColumnModel().getColumn(0).setPreferredWidth(120);
    myTable.getColumnModel().getColumn(1).setPreferredWidth(460);
      
    JScrollPane spTable2 = new JScrollPane(myTable);
    spTable2.setLocation(0,0);
    spTable2.setSize(tablewidth2-50,tablehight2-15);
    panel2.add(spTable2);
    
    
    
    
    Object[] rowh  = new Object[2];
	rowh[0] = "BO"; rowh[1] = "Include boil-off losses (green-included/red-excluded)";
	modelh.addRow(rowh);
	rowh[0] = "D2W"; rowh[1] = "Change in wet mass for given change in dry mass (kg wet per kg dry)";
	modelh.addRow(rowh);
	rowh[0] = "\u03B5"; rowh[1] = "Structural factor ";
	modelh.addRow(rowh);
	rowh[0] = "INC"; rowh[1] = "Include/Exclude Row (selected/non-selected)";
	modelh.addRow(rowh);
	rowh[0] = "ISP"; rowh[1] = "Specific Impulse";
	modelh.addRow(rowh);
	rowh[0] = "\u03BB"; rowh[1] = "Payload factor";
	modelh.addRow(rowh);
	rowh[0] = "MAR"; rowh[1] = "Include deltaV margin  (green-included/red-excluded)";
	modelh.addRow(rowh);
	rowh[0] = "MD"; rowh[1] = "S/C Dry Mass (without payload) ";
	modelh.addRow(rowh);
	rowh[0] = "ME"; rowh[1] = "Main Engine ";
	modelh.addRow(rowh);
	rowh[0] = "MER "; rowh[1] = "Maturity Estimate Reserve";
	modelh.addRow(rowh);
	rowh[0] = "MGA "; rowh[1] = "Maximum Growth Allowance";
	modelh.addRow(rowh);
	rowh[0] = "MP"; rowh[1] = "Payload Mass ";
	modelh.addRow(rowh);
	rowh[0] = "PMR"; rowh[1] = "Program Managers Reserve";
	modelh.addRow(rowh);
	rowh[0] = "MPR"; rowh[1] = "Propellant Mass";
	modelh.addRow(rowh);
	rowh[0] = "MR"; rowh[1] = "Structural Coefficient";
	modelh.addRow(rowh);
	rowh[0] = "MSW"; rowh[1] = "S/C Wet mass (excluding payload)";
	modelh.addRow(rowh);
	rowh[0] = "MW"; rowh[1] = "Total Wet mass (including payload)";
	modelh.addRow(rowh);
	rowh[0] = "O/F"; rowh[1] = "Fuel to Oxidizer Ratio";
	modelh.addRow(rowh);
	rowh[0] = "PL"; rowh[1] = "Payload";
	modelh.addRow(rowh);
	rowh[0] = "plan_01"; rowh[1] = "ECSS margin plan";
	modelh.addRow(rowh);
	rowh[0] = "plan_02"; rowh[1] = "NASA Exploration margin plan";
	modelh.addRow(rowh);
	rowh[0] = "PMF"; rowh[1] = "Propellant mass fraction";
	modelh.addRow(rowh);
	rowh[0] = "RCS"; rowh[1] = "Reaction Control System";
	modelh.addRow(rowh);
	rowh[0] = "S/C"; rowh[1] = "Spacecraft";
	modelh.addRow(rowh);
	rowh[0] = "SL"; rowh[1] = "Include steering losses (green-included/red-excluded)";
	modelh.addRow(rowh);
	rowh[0] = "Steering losses "; rowh[1] = "Losses due to imperfect main engine burns";
	modelh.addRow(rowh);
	rowh[0] = "w M "; rowh[1] = "With margin";
	modelh.addRow(rowh);
	rowh[0] = "w/o M "; rowh[1] = "Without margin";
	modelh.addRow(rowh);
    //------------------------------------------------------------------------
	tabbedPaneh.addTab("Acronymns" , null, mainPanelh1, null);
	tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_1);
	//tabbedPaneh.addTab("TBD" , null, mainPanelh2, null);
	//tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_2);

    
	tabbedPaneh.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    TabPanelh.add(tabbedPaneh);

   	
   	//----------------------------------------------------------------
    MainGUI.setOpaque(true);
    return MainGUI;
   	
   }
   
   public static String createFilePath(TreePath treePath) {
	    StringBuilder sb = new StringBuilder();
	    Object[] nodes = treePath.getPath();
	    for(int i=0;i<nodes.length;i++) {
	        sb.append(File.separatorChar).append(nodes[i].toString()); 
	    } 
	    return sb.toString();
	}
   

    
    public JPanel createContentPane () throws IOException{
    	
		JPanel TOP_GUI = new JPanel();
		TOP_GUI.setBackground(Color.white);
		TOP_GUI.setLayout(new BorderLayout());

    	JPanel MainGUI = new JPanel();
    	MainGUI = new JPanel();
    	MainGUI.setBackground(bc_c);
    	MainGUI.setPreferredSize(new Dimension(extx_main, exty_main));
    	MainGUI.setLayout(null);
    	
    	TabPanel = new JScrollPane();
    	TabPanel.setLayout(null);
    	TabPanel.setLocation(0,20);
    	TabPanel.setSize(extx_main, exty_main);
    	TabPanel.setBackground(bc_c);
    	TabPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    	//TabPanel.setBackground(Color.red);
    	MainGUI.add(TabPanel);
    	
    	tabbedPane = new JTabbedPane();
    	tabbedPane.setLocation(0, 0);
    	tabbedPane.setSize(extx_main, exty_main);
    	//-----------------------------------------------------
    	mainPanel1 = new JPanel(BB_BL);
    	mainPanel1.setLayout(null);
    	mainPanel1.setLocation(0, 0);
    	mainPanel1.setSize(900, exty_main);
    	//mainPanel1.setLayout(BorderLayout.CENTER);
    	mainPanel1.setBackground(bc_c);
    	mainPanel2 = new JPanel(BB_BL);
    	mainPanel2.setLayout(null);
    	mainPanel2.setLocation(0, 0);
    	mainPanel2.setSize(900, exty_main);
    	mainPanel2.setBackground(bc_c);
    	mainPanel3 = new JPanel(BB_BL);
    	mainPanel3.setLayout(null);
    	mainPanel3.setLocation(0, 0);
    	mainPanel3.setSize(900, exty_main);
    	mainPanel3.setBackground(bc_c);
    	JPanel mainPanel4 = new JPanel();
    	mainPanel4.setLayout(null);
    	mainPanel4.setLocation(0, 0);
    	mainPanel4.setSize(900, exty_main);
    	mainPanel4.setBackground(bc_c);
    	//----------------------------------------------------- 
        //Create horizontal menu bar.
        menuBar = new JMenuBar();
        menuBar.setLocation(0, 0);
        menuBar.setSize(extx_main, 20);
        MainGUI.add(menuBar);

        //Build the first menu.
        menu = new JMenu("File ");
        menu.setForeground(Color.black);
        menuBar.add(menu);
        menuBar.setBackground(new Color(184,207,229));
        mI1 = new JMenuItem("Open             ");
        mI1.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menu.add(mI1);
        mI1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                              Import_Case();
                    } });
        mI2 = new JMenuItem("Save As                ");
        mI2.setForeground(Color.black);
        mI2.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menu.add(mI2);
        
        mI2.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   		   SaveAs_Case();
                    } });
        mI4 = new JMenuItem("Save");
        mI4.setFont(menufont);
        menu.add(mI4);
        mI4.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                               try {
								Save_Case();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    } });
        mI5 = new JMenuItem("New");
        menu.add(mI5);
        mI5.setFont(menufont);
        mI5.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                          PrepareNewFile();     
                    } });
        menu.addSeparator();
        mI3 = new JMenuItem("Exit");
        mI3.setFont(menufont);
        menu.add(mI3);
        mI3.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                    } });
/*
        menu2 = new JMenu("Create");
        menu2.setForeground(Color.gray);
        menuBar.add(menu2);
        mII1 = new JMenuItem("Create Child|S/C");
        mII1.setForeground(Color.gray);
        mII1.setFont(menufont);
        menu2.add(mII1);
        mII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                       //CREATE_ChILDSC_TEMPLATE();     
                    } });
        mII1 = new JMenuItem("Link Child|S/C");
        mII1.setForeground(Color.gray);
        menu2.add(mII1); 
        mII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {

                    } });
*/
        menu3 = new JMenu("Flight");
        menu3.setForeground(Color.black);
        menuBar.add(menu3);
        mIII1 = new JMenuItem("Open Delta-V Dataset   ");
        mIII1.setForeground(Color.black);
        //mIII1.setFont(menufont);
        menu3.add(mIII1);
        mIII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   Import_DeltaV();
                    } });
        mIII2 = new JMenuItem("Save As Dataset   ");
        mIII2.setForeground(Color.black);
        menu3.add(mIII2); 
        mIII2.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   SaveAs_DeltaV();
                    } });
        
        menu3.addSeparator();
        cbMenuItem_BO = new JCheckBoxMenuItem("Include Boil_off Losses");
        cbMenuItem_BO.addItemListener(new ItemListener() {
         	 public void itemStateChanged(ItemEvent e) {
         		 if (BO_stall == false){
            		try {
              			 UPDATE_BottomUp();
              			 UPDATE_TopDown();
                   	} catch (NullPointerException e1) {

                       }
         		 }
         	 }
                    });
        cbMenuItem_BO.setMnemonic(KeyEvent.VK_C);
        menu3.add(cbMenuItem_BO);
        cbMenuItem_SL = new JCheckBoxMenuItem("Include Steering Losses");
        cbMenuItem_SL.addItemListener(new ItemListener() {
        	 public void itemStateChanged(ItemEvent e) {
        		 if (SL_stall == false){
            		try {
              			 UPDATE_BottomUp();
              			 UPDATE_TopDown();
                   	} catch (NullPointerException e1) {

                       }
        		 }
        	 }
                   });
        cbMenuItem_SL.setMnemonic(KeyEvent.VK_C);
        menu3.add(cbMenuItem_SL);
        cbMenuItem_MAR = new JCheckBoxMenuItem("Include Delta-V Margin");
        cbMenuItem_MAR.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		if (MAR_stall == false){
       		try {
   			 UPDATE_BottomUp();
   			 UPDATE_TopDown();
        	} catch (NullPointerException e1) {

            }
       		}
       	 }
                  });
        cbMenuItem_MAR.setMnemonic(KeyEvent.VK_C);
        cbMenuItem_MAR.setSelected(true);
        menu3.add(cbMenuItem_MAR);
        menu4 = new JMenu("BlueBook");
        menu4.setForeground(Color.black);
        menuBar.add(menu4);
        mIII1 = new JMenuItem("Settings                           ");
        mIII1.setForeground(Color.black);
        //mIII1.setFont(menufont);
        menu4.add(mIII1);
        mIII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                       JFrame.setDefaultLookAndFeelDecorated(false);
                       frame_settings = new JFrame( PROJECT_TITLE + " | Settings ");

                       //Create and set up the content pane.
                       BlueBook_main demo = new BlueBook_main();
                       try {
                    	   frame_settings.setContentPane(demo.createSettingsPane());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

                      // frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                       frame_settings.setSize(ext_hx_settings, ext_hy_settings);
                       frame_settings.setVisible(true);
                       frame_settings.setLocationRelativeTo(null);
                       BufferedImage myImage;
					try {
						myImage = ImageIO.read(url_smalllogo);
						frame_settings.setIconImage(myImage);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                       
                    } });
        mIII1 = new JMenuItem("Help      ");
        mIII1.setForeground(Color.black);
        //mIII1.setFont(menufont);
        menu4.add(mIII1);
        mIII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                       JFrame.setDefaultLookAndFeelDecorated(false);
                       JFrame frame2 = new JFrame(PROJECT_TITLE + " | Help ");

                       //Create and set up the content pane.
                       BlueBook_main demo = new BlueBook_main();
                       try {
						frame2.setContentPane(demo.createHelpPane());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

                      // frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                       frame2.setSize(650, 500);
                       frame2.setVisible(true);
                       frame2.setLocationRelativeTo(null);
                    } });
        menu4.addSeparator();
        mIII1 = new JMenuItem("Console  ");
        mIII1.setForeground(Color.black);
        //mIII1.setFont(menufont);
        menu4.add(mIII1);
        mIII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   EngineeringModel_window(); 
                    } });
        menu4.addSeparator();
        JMenuItem BB_LL = new JMenuItem("void  ");
        BB_LL.setForeground(Color.black);
        menu4.add(BB_LL);
        BB_LL.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {

                    } });
        //-----------------------------------------------------
        // create Vertical MenuBar
        /*
        menuBar2 = new VerticalMenuBar();
        menuBar2.setLocation(1355, 0);
        menuBar2.setSize(25, 100);
        MainGUI.add(menuBar2);

        //Build the first menu.
        menu21 = new JMenu("\u25BA");
        menu22 = new JMenu("\u270E");
        menuBar2.add(menu21);
        menuBar2.add(menu22);
        menuBar2.setBackground(new Color(184,207,229));
        mI21 = new JMenuItem("Export Delta-V Definition to csv");
        mI21.setForeground(Color.gray);
        menu21.add(mI21);
        mI21.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                              
                    } });
        mI22 = new JMenuItem("Export Payload Definition to csv");
        mI22.setForeground(Color.gray);
        menu21.add(mI22);
        mI22.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                              
                    } });
        mI23 = new JMenuItem("Export S/C Definition to csv");
        mI23.setForeground(Color.gray);
        menu21.add(mI23);
        mI23.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                              
                    } });
        		//00000000000000000
        mI24 = new JMenuItem("Export Top|Down Table to csv");
        mI24.setForeground(Color.gray);
        menu22.add(mI24);
        mI24.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                              
                    } });
        mI25 = new JMenuItem("Export Bottom|Up PMF-Chart to csv");
        mI25.setForeground(Color.black);
        menu22.add(mI25);
        mI25.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                        try {
							ExportBU_PMF2csv();
						} catch (FileNotFoundException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}      
                    } });
         */           
    	//-----------------------------------------------------        
        // Panel |1| Entry
        
        P1_page01 = new JPanel();
        P1_page01.setLayout(null);
        P1_page01.setLocation(0, 0);
        P1_page01.setSize(1350, 900);
        P1_page01.setBackground(bc_c);
    	//mainPanel1.add(P1_page01);
    	
        P1_page02 = new JPanel();
        P1_page02.setLayout(null);
        P1_page02.setLocation(0, 0);
        P1_page02.setSize(1350, 900);
        P1_page02.setBackground(bc_c);
        P1_page02.setForeground(l_c);
        
        P1_page03 = new JPanel();
        P1_page03.setLayout(null);
        P1_page03.setLocation(0, 0);
        P1_page03.setSize(1350, 900);
        P1_page03.setBackground(bc_c);
        P1_page03.setForeground(l_c);

        
        P1_page04 = new JPanel();
        P1_page04.setLayout(null);
        P1_page04.setLocation(0, 0);
        P1_page04.setSize(1350, 900);
        P1_page04.setBackground(bc_c);
        P1_page04.setForeground(l_c);


        			File[] roots = File.listRoots();
        			FileTreeNode rootTreeNode = new FileTreeNode(roots);
        			JTree TREE = new JTree(rootTreeNode);
        			TREE.setCellRenderer(new FileTreeCellRenderer());
        			TREE.setRootVisible(false);
        			/*
        			TREE.addTreeSelectionListener(new TreeSelectionListener(){

						@Override
						public void valueChanged(TreeSelectionEvent e) {
							String value = "";
					          TreePath treepath = e.getPath();
					            Object elements[] = treepath.getPath();
					               for (int i = 0, n = elements.length; i < n; i++) {
					           value+=elements[i]+"\\";  }
					        System.out.println(value);
						}
        				
        			}); */
        			  TREE.addMouseListener(new MouseAdapter() {
        			      public void mouseClicked(MouseEvent me) {
        			    	    TreePath tp = TREE.getPathForLocation(me.getX(), me.getY());
        			    	    if (tp != null) {
        			    	    	String path = createFilePath(tp);
        			    	      System.out.println("" + path);
        			    	    }
        			      }
        			    });
        			  


						
                    JScrollPane Explorer = new JScrollPane(TREE);
            		//Explorer.setLayout(null);
            		Explorer.setLocation(1350, 0);
            		Explorer.setSize(400, 900);
            		Explorer.setBackground(Color.blue);
            		Explorer.setForeground(l_c);
        			Explorer.setBorder(new EmptyBorder(0, 0, 0, 0));
            		TabPanel.add(Explorer);
                
        subtabPane11 = new JTabbedPane();
        subtabPane11.setLocation(0, 0);
        subtabPane11.setSize(1350, 900);
        //subtabPane11.setBackground(bc_c);

        subtabPane11.addTab(PN_T1_00 , null, P1_page01, null);
        subtabPane11.setMnemonicAt(0, KeyEvent.VK_1);
        subtabPane11.addTab(PN_T1_01 , null, P1_page02, null);
        subtabPane11.setMnemonicAt(0, KeyEvent.VK_2);
        subtabPane11.addTab(PN_T1_02 , null, P1_page03, null);
        subtabPane11.setMnemonicAt(0, KeyEvent.VK_3);
        /*
        subtabPane11.addTab(PN_T1_03 , null, P1_page04, null);
        subtabPane11.setMnemonicAt(0, KeyEvent.VK_4);
        subtabPane11.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	OVERVIEW_UPDATE();
            	FNC_POWER_UPDATE();
        }                
        });
        */
        subtabPane11.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        mainPanel1.add(subtabPane11);

        CasePanel = new JPanel();
        CasePanel.setLayout(null);
        CasePanel.setLocation(5, 295);
        CasePanel.setSize(150,250);
        CasePanel.setBackground(w_c);
        CasePanel.setBorder(BorderFactory.createLineBorder(new Color(0,0,200)));
        P1_page01.add(CasePanel);
        
        add_panel = new JPanel();
        add_panel.setLayout(null);
        add_panel.setLocation(260, 250);
        add_panel.setSize(150,400);
        add_panel.setBackground(w_c);
        add_panel.setBorder(BorderFactory.createLineBorder(l_c));
        P1_page03.add(add_panel);
       
        add_opt_panel = new JPanel();
        add_opt_panel.setLayout(null);
        add_opt_panel.setLocation(25,250);
        add_opt_panel.setSize(220,400);
        add_opt_panel.setBackground(w_c);
        add_opt_panel.setBorder(BorderFactory.createLineBorder(l_c));
        P1_page03.add(add_opt_panel);
        
        
        add_opt_panel3 = new JPanel();
        add_opt_panel3.setLayout(null);
        add_opt_panel3.setLocation(5,550);
        add_opt_panel3.setSize(150,145);
        add_opt_panel3.setBackground(w_c);
        add_opt_panel3.setBorder(BorderFactory.createLineBorder(l_c));
        P1_page01.add(add_opt_panel3);
        
        
        
        cd_l7 = new JLabel("Boil-off");
        cd_l7.setLocation(5,5);
        cd_l7.setSize(50, 30);
        cd_l7.setHorizontalAlignment(0);
        cd_l7.setForeground(l_c);
        add_opt_panel.add(cd_l7);
        
        BoilOff_TF = new JTextField("0.025");
        BoilOff_TF.setLocation(55, 8);
        BoilOff_TF.setSize(40, 20);
        BoilOff_TF.setHorizontalAlignment(0);
        BoilOff_TF.setForeground(Color.black);
        
        add_opt_panel.add(BoilOff_TF );
        
        boiloff_label2 = new JLabel("[%/day]");
        boiloff_label2.setLocation(100, 7);
        boiloff_label2.setSize(50, 20);
        boiloff_label2.setHorizontalAlignment(0);
        boiloff_label2.setForeground(l_c);
        boiloff_label2.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel.add(boiloff_label2);
        
        
        FuelMar_label2 = new JLabel("Propellant Margin");
        FuelMar_label2.setLocation(5, 35);
        FuelMar_label2.setSize(100, 20);
        FuelMar_label2.setHorizontalAlignment(0);
        FuelMar_label2.setHorizontalAlignment(SwingConstants.LEFT);
        FuelMar_label2.setForeground(l_c);
        add_opt_panel.add(FuelMar_label2);
        
        FuelMar_TF = new JTextField("1.5");
        FuelMar_TF.setLocation(115, 37);
        FuelMar_TF.setSize(40, 20);
        FuelMar_TF.setHorizontalAlignment(0);
        FuelMar_TF.setForeground(Color.black);
        add_opt_panel.add(FuelMar_TF );
        
        FuelMar_label = new JLabel("[%]");
        FuelMar_label.setLocation(160, 35);
        FuelMar_label.setSize(50, 20);
        FuelMar_label.setHorizontalAlignment(0);
        FuelMar_label.setForeground(l_c);
        FuelMar_label.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel.add(FuelMar_label);
        
        //-----------------------------------------
        int add2_width = 343; 
        
        add_opt_panel2 = new JPanel();
        add_opt_panel2.setLayout(null);
        add_opt_panel2.setLocation(5,5);
        add_opt_panel2.setSize(add2_width,280);
        add_opt_panel2.setBackground(w_c);
        add_opt_panel2.setBorder(BorderFactory.createLineBorder(l_c));
        P1_page01.add(add_opt_panel2);
        
        sum_title = new JLabel("Overview");
        sum_title.setLocation(0,0);
        sum_title.setSize(add2_width, 30);
        sum_title.setForeground(l_c);
        sum_title.setHorizontalAlignment(SwingConstants.CENTER);
        add_opt_panel2.add(sum_title);
        
        int sp_add2 = 15; 
        int sp_step = 15;
        int add2_rowheight = 25;
        
        add2_01 = new JLabel("RCS set: ");
        add2_01.setLocation(2,sp_step+sp_add2*0);
        add2_01.setSize(add2_width, add2_rowheight);
        add2_01.setForeground(l_c);
        add2_01.setFont(labelfont_small);
        add2_01.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_01);
        
        add2_02 = new JLabel("Set residual propellant: " + FuelMar_TF.getText() + " %");
        add2_02.setLocation(2,sp_step+sp_add2*1);
        add2_02.setSize(add2_width, add2_rowheight);
        add2_02.setForeground(l_c);
        add2_02.setFont(labelfont_small);
        add2_02.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_02);
        
        add2_03 = new JLabel("Set residual propellant: " + BoilOff_TF.getText() + " % per day");
        add2_03.setLocation(2,sp_step+sp_add2*2);
        add2_03.setSize(add2_width, add2_rowheight);
        add2_03.setForeground(l_c);
        add2_03.setFont(labelfont_small);
        add2_03.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_03);
        
        add2_04 = new JLabel("Boil-off INC: ");
        add2_04.setLocation(2,sp_step+sp_add2*3);
        add2_04.setSize(add2_width, add2_rowheight);
        add2_04.setForeground(l_c);
        add2_04.setFont(labelfont_small);
        add2_04.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_04);
        
        add2_05 = new JLabel("Steering INC: ");
        add2_05.setLocation(2,sp_step+sp_add2*4);
        add2_05.setSize(add2_width, add2_rowheight);
        add2_05.setForeground(l_c);
        add2_05.setFont(labelfont_small);
        add2_05.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_05);
        
        add2_06 = new JLabel("Delta-V margin INC: ");
        add2_06.setLocation(2,sp_step+sp_add2*5);
        add2_06.setSize(add2_width, add2_rowheight);
        add2_06.setForeground(l_c);
        add2_06.setFont(labelfont_small);
        add2_06.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_06);
        
        add2_07 = new JLabel("S/C margin plan: ");
        add2_07.setLocation(2,sp_step+sp_add2*6);
        add2_07.setSize(add2_width, add2_rowheight);
        add2_07.setForeground(l_c);
        add2_07.setFont(labelfont_small);
        add2_07.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_07);
        
        add2_08 = new JLabel("Case: ");
        add2_08.setLocation(2,sp_step+sp_add2*7);
        add2_08.setSize(add2_width, add2_rowheight);
        add2_08.setForeground(l_c);
        add2_08.setFont(labelfont_small);
        add2_08.setHorizontalAlignment(SwingConstants.LEFT);
        add_opt_panel2.add(add2_08);
        
        
        //-----------------------------------------
        
        JLabel pg_l1 = new JLabel("Pressure System");
        pg_l1.setLocation(8,2);
        pg_l1.setSize(140, 30);
        pg_l1.setHorizontalAlignment(0);
        pg_l1.setForeground(l_c);
        add_panel.add(pg_l1);
        
        pg_cb1 = new JCheckBox();
        pg_cb1.setLocation(10,12);
        pg_cb1.setSize(10, 10);
        pg_cb1.setHorizontalAlignment(0);
        pg_cb1.setSelected(true);
        add_panel.add(pg_cb1);
        
        JLabel pg_l2 = new JLabel("T closing [K]");
        pg_l2.setLocation(2,30);
        pg_l2.setSize(140, 25);
        pg_l2.setHorizontalAlignment(0);
        pg_l2.setForeground(l_c);
        add_panel.add(pg_l2); 
        pg_t2 = new JTextField("80");
        pg_t2.setLocation(2,55);
        pg_t2.setSize(140, 25);
        pg_t2.setHorizontalAlignment(0);
        pg_t2.setForeground(l_c);
        pg_t2.setBackground(bc_c);
        add_panel.add(pg_t2);
        JLabel pg_l3 = new JLabel("T initial [K]");
        pg_l3.setLocation(2,55+25);
        pg_l3.setSize(140, 25);
        pg_l3.setHorizontalAlignment(0);
        pg_l3.setForeground(l_c);
        add_panel.add(pg_l3);
         pg_t3 = new JTextField("270");
        pg_t3.setLocation(2,55+50);
        pg_t3.setSize(140, 25);
        pg_t3.setHorizontalAlignment(0);
        pg_t3.setForeground(l_c);
        pg_t3.setBackground(bc_c);
        add_panel.add(pg_t3);
        JLabel pg_l4 = new JLabel("P closing [Pa]");
        pg_l4.setLocation(2,105+25);
        pg_l4.setSize(140, 25);
        pg_l4.setHorizontalAlignment(0);
        pg_l4.setForeground(l_c);
        add_panel.add(pg_l4);
        pg_t4 = new JTextField("500000");
        pg_t4.setLocation(2,105+50);
        pg_t4.setSize(140, 25);
        pg_t4.setHorizontalAlignment(0);
        pg_t4.setForeground(l_c);
        pg_t4.setBackground(bc_c);
        add_panel.add(pg_t4);
        JLabel pg_l5 = new JLabel("P initial [Pa]");
        pg_l5.setLocation(2,165+25);
        pg_l5.setSize(140, 25);
        pg_l5.setHorizontalAlignment(0);
        pg_l5.setForeground(l_c);
        add_panel.add(pg_l5);
        pg_t5 = new JTextField("21000000");
        pg_t5.setLocation(2,165+50);
        pg_t5.setSize(140, 25);
        pg_t5.setHorizontalAlignment(0);
        pg_t5.setForeground(l_c);
        pg_t5.setBackground(bc_c);
        add_panel.add(pg_t5);
        JLabel pg_l6 = new JLabel("<html>Molecular mass<br/>of the pressurant [-]</html>");
        pg_l6.setLocation(2,215+25);
        pg_l6.setSize(140, 50);
        pg_l6.setHorizontalAlignment(0);
        pg_l6.setForeground(l_c);
        add_panel.add(pg_l6);
        pg_t6 = new JTextField("4.003");
        pg_t6.setLocation(2,215+75);
        pg_t6.setSize(140, 25);
        pg_t6.setHorizontalAlignment(0);
        pg_t6.setForeground(l_c);
        pg_t6.setBackground(bc_c);
        add_panel.add(pg_t6);
        
        FileLabel = new JLabel("CASE : " + myfile_name);
        FileLabel.setLocation(5,695);
        FileLabel.setSize(250, 30);
        FileLabel.setHorizontalAlignment(0);
        FileLabel.setForeground(l_c);
        FileLabel.setHorizontalAlignment(SwingConstants.LEFT);
        FileLabel.setBackground(bc_c);
        P1_page01.add(FileLabel);
        
        JLabel cd_lm = new JLabel(" - S/C MAIN - ");
        cd_lm.setLocation(15, 5);
        cd_lm.setSize(120, 20);
        cd_lm.setHorizontalAlignment(0);
        //cd_lm.setHorizontalAlignment(SwingConstants.LEFT);
        cd_lm.setForeground(l_c);
        CasePanel.add(cd_lm);
        
        cd_l5 = new JLabel("Delta-V");
        cd_l5.setLocation(880, 0);
        cd_l5.setSize(120, 30);
        cd_l5.setHorizontalAlignment(0);
        cd_l5.setForeground(l_c);
        P1_page01.add(cd_l5);
        
       

        Linp1 = new JLabel("S/C Control [kg]");
        Linp1.setLocation(8,30);
        Linp1.setSize(140, 30);
        Linp1.setHorizontalAlignment(0);
        Linp1.setForeground(l_c);
        CasePanel.add(Linp1);
        Minit = new JTextField("-");
        Minit.setLocation(5, 65);
        Minit.setSize(140, 30);
        Minit.setHorizontalAlignment(0);
        Minit.setForeground(Color.black);
        CasePanel.add(Minit );
        Linp2 = new JLabel("S/C PAYLOAD [kg]");
        Linp2.setLocation(8, 95);
        Linp2.setSize(120, 30);
        Linp2.setHorizontalAlignment(0);
        Linp2.setForeground(l_c);
        CasePanel.add(Linp2);
        Mpayload = new JTextField("-");
        Mpayload.setLocation(5, 130);
        Mpayload.setSize(140, 30);
        Mpayload.setHorizontalAlignment(0);
        Mpayload.setForeground(Color.black);
        CasePanel.add(Mpayload );
        cd_l6 = new JLabel("S/C DRY [kg]");
        cd_l6.setLocation(5,165);
        cd_l6.setSize(140, 30);
        cd_l6.setHorizontalAlignment(0);
        cd_l6.setForeground(l_c);
        CasePanel.add(cd_l6);
        cd_tf1 = new JTextField("-");
        cd_tf1.setLocation(5, 200);
        cd_tf1.setSize(140, 30);
        cd_tf1.setHorizontalAlignment(0);
        cd_tf1.setForeground(Color.black);
        CasePanel.add(cd_tf1 );
        
        
        int tablewidth2 = 880;
        int tablehight2 = 250;
        JPanel panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setLocation(440,25);
        panel2.setSize(tablewidth2,tablehight2);
        P1_page01.add(panel2);
        
        table2 = new JTable(){
            private static final long serialVersionUID = 1L;
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 9:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
            
        	@Override
        	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	            Component comp = super.prepareRenderer(renderer, row, col);
	           // Object value = getModel().getValueAt(row, 9);
	            try {
	            //Tables_SYNC();
	            //FNC_EN_UPDATE();
	            } catch (NullPointerException e) {
	            	
	            }
	            return comp;
	        }
        	
        }; ;
        @SuppressWarnings("serial")
		Action action2 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                @SuppressWarnings("unused")
				TableCellListener tcl2 = (TableCellListener)e.getSource();
               // System.out.println("Row   : " + tcl.getRow());
               // System.out.println("Column: " + tcl.getColumn());
               // System.out.println("Old   : " + tcl.getOldValue());
               // System.out.println("New   : " + tcl.getNewValue());
                Tables_SYNC();
            }
        };
    @SuppressWarnings("unused")
	TableCellListener tcl2 = new TableCellListener(table2, action2);
        
        
        model2 = new DefaultTableModel();
        model2.setColumnIdentifiers(columns2);
        table2.setModel(model2);
        table2.setForeground(l_c);
        
        ((JTable) table2).setFillsViewportHeight(true);
        table2.setBackground(t_c);
        
        table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table2.getColumnModel().getColumn(0).setPreferredWidth(230);
        table2.getColumnModel().getColumn(1).setPreferredWidth(100);
        table2.getColumnModel().getColumn(2).setPreferredWidth(70);
        table2.getColumnModel().getColumn(3).setPreferredWidth(100);
        table2.getColumnModel().getColumn(4).setPreferredWidth(70);
        table2.getColumnModel().getColumn(5).setPreferredWidth(25);
        table2.getColumnModel().getColumn(6).setPreferredWidth(90);
        table2.getColumnModel().getColumn(7).setPreferredWidth(80);     
        table2.getColumnModel().getColumn(8).setPreferredWidth(80);   
        table2.getColumnModel().getColumn(9).setPreferredWidth(35);   
        JScrollPane spTable2 = new JScrollPane(table2);
        spTable2.setLocation(0,0);
        spTable2.setSize(tablewidth2,tablehight2);
        panel2.add(spTable2);
        
    	
        blueButton = new JButton("Add");
        blueButton.setLocation(228+125, 25);
        blueButton.setSize(85, 30);
        blueButton.addActionListener(this);
        P1_page01.add(blueButton);
        
        greenButton = new JButton("Delete");
        greenButton.setLocation(228+125, 60);
        greenButton.setSize(85, 30);
        greenButton.addActionListener(this);
        P1_page01.add(greenButton);
        
        moveup = new JButton("up");
        moveup.setLocation(228+125, 110);
        moveup.setSize(85, 30);
        moveup.addActionListener(this);
        P1_page01.add(moveup);
        
        movedown = new JButton("down");
        movedown.setLocation(228+125, 145);
        movedown.setSize(85, 30);
        movedown.addActionListener(this);
        P1_page01.add(movedown);
        
        button_int = new JButton("\u21cc");
        button_int.setLocation(228+125, 185);
        button_int.setSize(85, 30);
        button_int.setForeground(Color.blue);
        button_int.addActionListener(this);
        P1_page01.add(button_int);
       
        mar_pan_pl = new JPanel();
        mar_pan_pl.setLayout(null);
        mar_pan_pl.setLocation(163, 295);
        mar_pan_pl.setSize(185,400);
        mar_pan_pl.setBackground(w_c);
        mar_pan_pl.setBorder(BorderFactory.createLineBorder(l_c));
        P1_page01.add(mar_pan_pl);
        
        int pl_res_y = 190;
        pl_10 = new JLabel("PL Total w/o M [kg]:");
        pl_10.setLocation(5, pl_res_y + 35);
        pl_10.setSize(150, 20);
        pl_10.setHorizontalAlignment(0);
        pl_10.setForeground(l_c);
        pl_10.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_10);
        
        pl_11 = new JLabel("PL Total w M [kg]:");
        pl_11.setLocation(5, pl_res_y + 70);
        pl_11.setSize(150, 20);
        pl_11.setHorizontalAlignment(0);
        pl_11.setForeground(l_c);
        pl_11.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_11);
        
        pl_12 = new JLabel("Total MGA [kg]:");
        pl_12.setLocation(5, pl_res_y + 105);
        pl_12.setSize(150, 20);
        pl_12.setHorizontalAlignment(0);
        pl_12.setForeground(l_c);
        pl_12.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_12);
        
        pl_13 = new JLabel("Total MER [kg]:");
        pl_13.setLocation(5, pl_res_y + 140);
        pl_13.setSize(150, 20);
        pl_13.setHorizontalAlignment(0);
        pl_13.setForeground(l_c);
        pl_13.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_13);
        
        pl_14 = new JLabel("Total PMR [kg]:");
        pl_14.setLocation(5, pl_res_y + 172);
        pl_14.setSize(150, 20);
        pl_14.setHorizontalAlignment(0);
        pl_14.setForeground(l_c);
        pl_14.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_14);
        
        deltav_load = new JButton("\u25bc");
        deltav_load.setLocation(1235, 5);
        deltav_load.setSize(50, 20);
        deltav_load.addActionListener(this);
        P1_page01.add(deltav_load);
        
        deltav_cb_1 = new JCheckBox();
        deltav_cb_1.setLocation(1292, 10);
        deltav_cb_1.setSize(15, 15);
        deltav_cb_1.setSelected(true);
        deltav_cb_1.addItemListener(new ItemListener() {
          	 public void itemStateChanged(ItemEvent e) {
          		CHANGE_SELECT_TABLE2();
          	 }
                     });
        deltav_cb_1.setHorizontalAlignment(0);
        P1_page01.add(deltav_cb_1);
        
        
        int tablewidth3 = 895;
        int tablehight3 = 400;
        JPanel panel3 = new JPanel();
        panel3.setLayout(null);
        panel3.setLocation(440,295);
        panel3.setBackground(Color.white);
        panel3.setSize(tablewidth3,tablehight3);
        P1_page01.add(panel3);
        
        table3 = new JTable(){

                private static final long serialVersionUID = 1L;

                /*@Override
                public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
                }*/
                @SuppressWarnings({ "unchecked", "rawtypes" })
    			@Override
                public Class getColumnClass(int column) {
                    switch (column) {
                        case 9:
                            return Boolean.class;    
                        default:
                            return String.class;
                    }
                }
            	@Override
            	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
    	            Component comp = super.prepareRenderer(renderer, row, col);
    	            Object value = getModel().getValueAt(row, col);
    	            try {
    	            	//FNC_PL_UPDATE();
    	                if (value.equals(true)) {
    	                    comp.setBackground(Color.gray);
    	                } else {
    	                   // comp.setBackground(new Color(245,245,245));
    	                }
    	                
    	            } catch (NullPointerException e) {
    	            	
    	            }
    	            return comp;
    	        }

            }; ;
            @SuppressWarnings("serial")
			Action action3 = new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    @SuppressWarnings("unused")
					TableCellListener tcl3 = (TableCellListener)e.getSource();
                   // System.out.println("Row   : " + tcl.getRow());
                   // System.out.println("Column: " + tcl.getColumn());
                   // System.out.println("Old   : " + tcl.getOldValue());
                   // System.out.println("New   : " + tcl.getNewValue());
                    FNC_PL_UPDATE();
                }
            };
        @SuppressWarnings("unused")
		TableCellListener tcl3 = new TableCellListener(table3, action3);
        model3 = new DefaultTableModel(){

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
				if (column == 4 || column == 6 || column == 8 || column == 10 ){
					 return false;
				} else {
					return true; 
				}
            }
        };
        model3.setColumnIdentifiers(columns3);
        table3.setModel(model3);
        
        ((JTable) table3).setFillsViewportHeight(true);
        table3.setBackground(t_c);
        table3.setForeground(l_c);
        
        table3.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table3.getColumnModel().getColumn(0).setPreferredWidth(184);
        table3.getColumnModel().getColumn(1).setPreferredWidth(60);
        table3.getColumnModel().getColumn(2).setPreferredWidth(73);
        table3.getColumnModel().getColumn(3).setPreferredWidth(90);
        table3.getColumnModel().getColumn(4).setPreferredWidth(85);
        table3.getColumnModel().getColumn(5).setPreferredWidth(85);
        table3.getColumnModel().getColumn(6).setPreferredWidth(85);
        table3.getColumnModel().getColumn(7).setPreferredWidth(80); 
        table3.getColumnModel().getColumn(8).setPreferredWidth(105); 
        table3.getColumnModel().getColumn(9).setPreferredWidth(30);
        JScrollPane spTable3 = new JScrollPane(table3, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spTable3.setLocation(0,0);
        spTable3.setSize(tablewidth3,tablehight3);
        panel3.add(spTable3);
        
        cd_l4 = new JLabel("Payload");
        cd_l4.setLocation(880, 269);
        cd_l4.setSize(120, 30);
        cd_l4.setHorizontalAlignment(0);
        cd_l4.setForeground(l_c);
        P1_page01.add(cd_l4);
        
        int pl_y = 260;
        
        pl_add = new JButton("Add");
        pl_add.setLocation(228+125, 35+ pl_y);
        pl_add.setSize(85, 30);
        pl_add.addActionListener(this);
        P1_page01.add(pl_add);
        
        pl_delete = new JButton("Delete");
        pl_delete.setLocation(228+125, 70+ pl_y);
        pl_delete.setSize(85, 30);
        pl_delete.addActionListener(this);
        P1_page01.add(pl_delete);
        
        pl_up = new JButton("up");
        pl_up.setLocation(228+125, 115+ pl_y);
        pl_up.setSize(85, 30);
        pl_up.addActionListener(this);
        P1_page01.add(pl_up);
        
        pl_down = new JButton("down");
        pl_down.setLocation(228+125, 150+ pl_y);
        pl_down.setSize(85, 30);
        pl_down.addActionListener(this);
        P1_page01.add(pl_down);
        /*
        JButton interim_button = new JButton("TEST");
        interim_button.setLocation(228+125, 185+ pl_y);
        interim_button.setSize(85, 30);
        interim_button.addActionListener(new ActionListener() {
         	 public void actionPerformed(ActionEvent e) {
         		FNC_PL_TEST();
         	 }
                    });
        P1_page01.add(interim_button);
*/
        pl_load = new JButton("\u25bc");
        pl_load.setLocation(1233, pl_y+15);
        pl_load.setSize(50, 20);
        pl_load.addActionListener(this);
        P1_page01.add(pl_load);
        
        int cb_x =5;
        int pl_sy = 15;
        pl_cb1 = new JCheckBox();
        pl_cb1.setLocation(cb_x, pl_sy+0);
        pl_cb1.setSize(10, 10);
        pl_cb1.setSelected(true);
        pl_cb1.addItemListener(new ItemListener() {
          	 public void itemStateChanged(ItemEvent e) {
      			 FNC_PL_UPDATE();
          	 }
                     });
        pl_cb1.setHorizontalAlignment(0);
        mar_pan_pl.add(pl_cb1);
        pl_cb2 = new JCheckBox();
        pl_cb2.setLocation(cb_x, pl_sy+40);
        pl_cb2.setSize(10, 10);
        pl_cb2.setSelected(true);
        pl_cb2.addItemListener(new ItemListener() {
          	 public void itemStateChanged(ItemEvent e) {
      			 FNC_PL_UPDATE();
          	 }
                     });
        pl_cb2.setHorizontalAlignment(0);
        mar_pan_pl.add(pl_cb2);
        pl_cb3 = new JCheckBox();
        pl_cb3.setLocation(cb_x, pl_sy+80);
        pl_cb3.setSize(10, 10);
        pl_cb3.setSelected(true);
        pl_cb3.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
   			 FNC_PL_UPDATE();
       	 }
                  });
        pl_cb3.setHorizontalAlignment(0);
        mar_pan_pl.add(pl_cb3);
        pl_2 = new JLabel("Include MGA on Payload");
        pl_2.setLocation(cb_x+20, pl_sy-5);
        pl_2.setSize(170, 20);
        pl_2.setHorizontalAlignment(0);
        pl_2.setForeground(l_c);
        pl_2.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_2);
        pl_3 = new JLabel("Include MER on Payload");
        pl_3.setLocation(cb_x+20, pl_sy+35);
        pl_3.setSize(170, 20);
        pl_3.setHorizontalAlignment(0);
        pl_3.setForeground(l_c);
        pl_3.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_3);
        pl_4 = new JLabel("Include PMR on Payload");
        pl_4.setLocation(cb_x+20, pl_sy+75);
        pl_4.setSize(170, 20);
        pl_4.setHorizontalAlignment(0);
        pl_4.setForeground(l_c);
        pl_4.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_pl.add(pl_4);
        
        pl_cb_1 = new JCheckBox();
        pl_cb_1.setLocation(1292, 280);
        pl_cb_1.setSize(15, 15);
        pl_cb_1.setSelected(true);
        pl_cb_1.addItemListener(new ItemListener() {
          	 public void itemStateChanged(ItemEvent e) {
          		CHANGE_SELECT_TABLE3();
          	 }
                     });
        pl_cb_1.setHorizontalAlignment(0);
        P1_page01.add(pl_cb_1);
        
        // S/C 
        
        mar_pan_sc = new JPanel();
        mar_pan_sc.setLayout(null);
        mar_pan_sc.setLocation(5, 30);
        mar_pan_sc.setSize(220,600);
        mar_pan_sc.setBackground(w_c);
        mar_pan_sc.setBorder(BorderFactory.createLineBorder(l_c));
        P1_page02.add(mar_pan_sc);
        
        
        int sc_res_y = 350;
        sc_10 = new JLabel("S/C Total w/o M [kg]:");
        sc_10.setLocation(5, sc_res_y + 35);
        sc_10.setSize(150, 20);
        sc_10.setHorizontalAlignment(0);
        sc_10.setForeground(l_c);
        sc_10.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_10);
        
        sc_11 = new JLabel("S/C Total w M [kg]:");
        sc_11.setLocation(5, sc_res_y + 70);
        sc_11.setSize(150, 20);
        sc_11.setHorizontalAlignment(0);
        sc_11.setForeground(l_c);
        sc_11.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_11);
        
        sc_12 = new JLabel("Total MGA [kg]:");
        sc_12.setLocation(5, sc_res_y + 105);
        sc_12.setSize(150, 20);
        sc_12.setHorizontalAlignment(0);
        sc_12.setForeground(l_c);
        sc_12.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_12);
        
        sc_13 = new JLabel("Total MER [kg]:");
        sc_13.setLocation(5, sc_res_y + 140);
        sc_13.setSize(150, 20);
        sc_13.setHorizontalAlignment(0);
        sc_13.setForeground(l_c);
        sc_13.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_13);
        
        sc_14 = new JLabel("Total PMR [kg]:");
        sc_14.setLocation(5, sc_res_y + 172);
        sc_14.setSize(150, 20);
        sc_14.setHorizontalAlignment(0);
        sc_14.setForeground(l_c);
        sc_14.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_14);
        
        int tablewidth5 = 925;
        int tablehight5 = 600;
        JPanel panel5 = new JPanel();
        panel5.setLayout(null);
        panel5.setLocation(315,35);
        panel5.setSize(tablewidth5,tablehight5);
        P1_page02.add(panel5);
        
        table5 = new JTable(){

            private static final long serialVersionUID = 1L;

            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/
            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 9:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        	@Override
        	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	            Component comp = super.prepareRenderer(renderer, row, col);
	            Object value = getModel().getValueAt(row, col);
	            String val_is = String.valueOf(value);
	            try {
	                if (value.equals(true)) {
	                    comp.setBackground(Color.gray);
	                    //FNC_SC_UPDATE();
	                } else if (val_is.contains("Structure") || val_is.contains("structure")){
	                	comp.setBackground(Color.black);
	                    comp.setForeground(Color.white);
	                } else if (val_is.contains("Propulsion") || val_is.contains("propulsion")){
	                	comp.setBackground(Color.green);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("I/F") || val_is.contains("Interface")){
	                	comp.setBackground(new Color(255, 102, 102));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Thermal") || val_is.contains("thermal")){
	                	comp.setBackground(new Color(108, 166, 89));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Mechanism") || val_is.contains("mechanism")){
	                	comp.setBackground(new Color(184, 138, 122));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Auxiliary") || val_is.contains("aux")){
	                	comp.setBackground(new Color(255, 242, 204));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("GNC") || val_is.contains("AOCS")){
	                	comp.setBackground(new Color(19, 0, 89));
	                    comp.setForeground(Color.white);
	                } else if (val_is.contains("C&T")){
	                	comp.setBackground(Color.red);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Power")){
	                	comp.setBackground(Color.yellow);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("C&DH")){
	                	comp.setBackground(Color.orange);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("PMR")){
	                	comp.setBackground(Color.white);
	                    comp.setForeground(Color.gray);
	                } else {
	                comp.setBackground(t_c);
	                comp.setForeground(Color.black);
	                }
	                //FNC_SC_UPDATE();
	            } catch (NullPointerException e) {
	            	
	            }
	            return comp;
	        }
        }; ;
        
        
        @SuppressWarnings("serial")
		Action action5 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                @SuppressWarnings("unused")
				TableCellListener tcl5 = (TableCellListener)e.getSource();
               // System.out.println("Row   : " + tcl.getRow());
               // System.out.println("Column: " + tcl.getColumn());
               // System.out.println("Old   : " + tcl.getOldValue());
               // System.out.println("New   : " + tcl.getNewValue());
                FNC_SC_UPDATE();
            }
        };
    @SuppressWarnings("unused")
	TableCellListener tcl5 = new TableCellListener(table5, action5);
        
        model5 = new DefaultTableModel(){

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
				if (column == 4 || column == 6 || column == 8 || column == 10 ){
					 return false;
				} else {
					return true; 
				}
            }
        };
        model5.setColumnIdentifiers(columns3);
        table5.setModel(model5);
        ((JTable) table5).setFillsViewportHeight(true);
        table5.setBackground(t_c);
        table5.setForeground(l_c);
        
        table5.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table5.getColumnModel().getColumn(0).setPreferredWidth(214);
        table5.getColumnModel().getColumn(1).setPreferredWidth(70);
        table5.getColumnModel().getColumn(3).setPreferredWidth(70);
        table5.getColumnModel().getColumn(4).setPreferredWidth(90);
        table5.getColumnModel().getColumn(5).setPreferredWidth(90);
        table5.getColumnModel().getColumn(6).setPreferredWidth(90);
        table5.getColumnModel().getColumn(7).setPreferredWidth(90);
        table5.getColumnModel().getColumn(8).setPreferredWidth(90); 
        table5.getColumnModel().getColumn(9).setPreferredWidth(30); 
        table5.getColumnModel().getColumn(10).setPreferredWidth(35); 
        JScrollPane spTable5 = new JScrollPane(table5, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spTable5.setLocation(0,0);
        spTable5.setSize(tablewidth5,tablehight5);
        panel5.add(spTable5);
        
        sc_1 = new JLabel("S/C Equipment list");
        sc_1.setLocation(710, 5);
        sc_1.setSize(120, 30);
        sc_1.setHorizontalAlignment(0);
        sc_1.setForeground(l_c);
        P1_page02.add(sc_1);
        
        int sc_y = 0;
        
        sc_add = new JButton("Add");
        sc_add.setLocation(228, 35+ sc_y);
        sc_add.setSize(85, 30);
        sc_add.addActionListener(this);
        P1_page02.add(sc_add);
        
        sc_delete = new JButton("Delete");
        sc_delete.setLocation(228, 70+ sc_y);
        sc_delete.setSize(85, 30);
        sc_delete.addActionListener(this);
        P1_page02.add(sc_delete);
        
        sc_up = new JButton("up");
        sc_up.setLocation(228, 115+ sc_y);
        sc_up.setSize(85, 30);
        sc_up.addActionListener(this);
        P1_page02.add(sc_up);
        
        sc_down = new JButton("down");
        sc_down.setLocation(228, 150+ sc_y);
        sc_down.setSize(85, 30);
        sc_down.addActionListener(this);
        P1_page02.add(sc_down);

        sc_load = new JButton("\u25bc");
        sc_load.setLocation(1107, 15);
        sc_load.setSize(50, 20);
        sc_load.addActionListener(this);
        P1_page02.add(sc_load);
        

        int cb_sc_x = 10;
        
        sc_cb1 = new JCheckBox();
        sc_cb1.setLocation(cb_sc_x, sc_y+20);
        sc_cb1.setSize(10, 10);
        sc_cb1.setHorizontalAlignment(0);
        sc_cb1.setSelected(true);
        sc_cb1.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
   			 FNC_SC_UPDATE();
       	 }
                  });
        mar_pan_sc.add(sc_cb1);
        sc_cb2 = new JCheckBox();
        sc_cb2.setLocation(cb_sc_x, sc_y+60);
        sc_cb2.setSize(10, 10);
        sc_cb2.setSelected(true);
        sc_cb2.addItemListener(new ItemListener() {
        	 public void itemStateChanged(ItemEvent e) {
    			 FNC_SC_UPDATE();
        	 }
                   });
        sc_cb2.setHorizontalAlignment(0);
        mar_pan_sc.add(sc_cb2);
        sc_cb3 = new JCheckBox();
        sc_cb3.setLocation(cb_sc_x, sc_y+100);
        sc_cb3.setSize(10, 10);
        sc_cb3.setSelected(true);
        sc_cb3.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
   			 FNC_SC_UPDATE();
       	 }
                  });
        sc_cb3.setHorizontalAlignment(0);
        mar_pan_sc.add(sc_cb3);
        sc_2 = new JLabel("Include MGA");
        sc_2.setLocation(cb_sc_x+20, sc_y+15);
        sc_2.setSize(100, 20);
        sc_2.setHorizontalAlignment(0);
        sc_2.setForeground(l_c);
        sc_2.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_2);
        sc_3 = new JLabel("Include MER");
        sc_3.setLocation(cb_sc_x+20, sc_y+55);
        sc_3.setSize(100, 20);
        sc_3.setHorizontalAlignment(0);
        sc_3.setForeground(l_c);
        sc_3.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_3);
        sc_4 = new JLabel("Include PMR");
        sc_4.setLocation(cb_sc_x+20, sc_y+95);
        sc_4.setSize(100, 20);
        sc_4.setHorizontalAlignment(0);
        sc_4.setForeground(l_c);
        sc_4.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_4);
        
        
        sc_5 = new JLabel("Set Margin on control mass:");
        sc_5.setLocation(cb_sc_x+15, sc_y+145);
        sc_5.setSize(170, 20);
        sc_5.setHorizontalAlignment(0);
        sc_5.setForeground(l_c);
        sc_5.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_5);
        
        sc_cb4 = new JCheckBox();
        sc_cb4.setLocation(cb_sc_x, sc_y+150);
        sc_cb4.setSize(10, 10);
        sc_cb4.setHorizontalAlignment(0);
        sc_cb4.setSelected(false);
        sc_cb4.addItemListener(new ItemListener() {
         	 public void itemStateChanged(ItemEvent e) {
     			 FNC_SC_UPDATE();
         	 }
                    });
        mar_pan_sc.add(sc_cb4);
        
        sc_TF_01 = new JTextField("0");
        sc_TF_01.setLocation(cb_sc_x, sc_y+170);
        sc_TF_01.setSize(40, 20);
        sc_TF_01.setHorizontalAlignment(0);
        sc_TF_01.setForeground(Color.black);
        sc_TF_01.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
       			 FNC_SC_UPDATE();
           	 }
                      });
        mar_pan_sc.add(sc_TF_01);
        
        sc_6 = new JLabel("Percent");
        sc_6.setLocation(cb_sc_x+45, sc_y+170);
        sc_6.setSize(100, 20);
        sc_6.setHorizontalAlignment(0);
        sc_6.setForeground(l_c);
        sc_6.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_6);
        
        
        sc_7 = new JLabel("Set uniform margin on MGA:");
        sc_7.setLocation(cb_sc_x+15, sc_y+205);
        sc_7.setSize(200, 20);
        sc_7.setHorizontalAlignment(0);
        sc_7.setForeground(l_c);
        sc_7.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_7);
        sc_cb51 = new JCheckBox();
        sc_cb51.setLocation(cb_sc_x, sc_y+210);
        sc_cb51.setSize(10, 10);
        sc_cb51.setHorizontalAlignment(0);
        sc_cb51.setSelected(false);
        sc_cb51.addItemListener(new ItemListener() {
          	 public void itemStateChanged(ItemEvent e) {
      			 FNC_SC_UPDATE();
          	 }
                     });
        mar_pan_sc.add(sc_cb51);
        
        sc_TF_02 = new JTextField("0");
        sc_TF_02.setLocation(cb_sc_x, sc_y+230);
        sc_TF_02.setSize(40, 20);
        sc_TF_02.setHorizontalAlignment(0);
        sc_TF_02.setForeground(Color.black);
        sc_TF_02.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
       			 FNC_SC_UPDATE();
           	 }
                      });
        mar_pan_sc.add(sc_TF_02);
        
        sc_7 = new JLabel("Percent");
        sc_7.setLocation(cb_sc_x+45, sc_y+230);
        sc_7.setSize(100, 20);
        sc_7.setHorizontalAlignment(0);
        sc_7.setForeground(l_c);
        sc_7.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_7);
        
        sc_8 = new JLabel("Set uniform margin on MER:");
        sc_8.setLocation(cb_sc_x+15, sc_y+250);
        sc_8.setSize(200, 20);
        sc_8.setHorizontalAlignment(0);
        sc_8.setForeground(l_c);
        sc_8.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_8);
        sc_cb61 = new JCheckBox();
        sc_cb61.setLocation(cb_sc_x, sc_y+255);
        sc_cb61.setSize(10, 10);
        sc_cb61.setHorizontalAlignment(0);
        sc_cb61.setSelected(false);
        sc_cb61.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
   			 FNC_SC_UPDATE();
       	 }
                  });
        mar_pan_sc.add(sc_cb61);
        
        sc_TF_03 = new JTextField("0");
        sc_TF_03.setLocation(cb_sc_x, sc_y+275);
        sc_TF_03.setSize(40, 20);
        sc_TF_03.setHorizontalAlignment(0);
        sc_TF_03.setForeground(Color.black);
        sc_TF_03.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
       			 FNC_SC_UPDATE();
           	 }
                      });
        mar_pan_sc.add(sc_TF_03);
        
        sc_9 = new JLabel("Percent");
        sc_9.setLocation(cb_sc_x+45, sc_y+275);
        sc_9.setSize(100, 20);
        sc_9.setHorizontalAlignment(0);
        sc_9.setForeground(l_c);
        sc_9.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_9);
        
        JLabel sc_19 = new JLabel("<html>  &nbsp; Apply Exploration Margin <br/> &nbsp; Management Plan</html>");
        sc_19.setLocation(cb_sc_x+10, sc_y+320);
        sc_19.setSize(180, 50);
        sc_19.setHorizontalAlignment(0);
        sc_19.setForeground(l_c);
        sc_19.setBackground(w_c);
        sc_19.setOpaque(true);
        sc_19.setBorder(BorderFactory.createLineBorder(Color.red));
        sc_19.setHorizontalAlignment(SwingConstants.LEFT);
        mar_pan_sc.add(sc_19);
        
        
        sc_cb53 = new JCheckBox();
        sc_cb53.setLocation(cb_sc_x-5, sc_y+333);
        sc_cb53.setSize(10, 10);
        sc_cb53.setHorizontalAlignment(0);
        sc_cb53.setSelected(true);
        sc_cb53.setOpaque(true);
        sc_cb53.addItemListener(new ItemListener() {
        	 public void itemStateChanged(ItemEvent e) {
    			 //FNC_SC_UPDATE();
        		 if (sc_cb53.isSelected()){
        			 sc_19.setBorder(BorderFactory.createLineBorder(Color.red));
        			 exp_mar_plan = true;
        		 } else {
        			 sc_19.setBorder(BorderFactory.createLineBorder(Color.black));
        			 exp_mar_plan = false;
        		 }
    			 FNC_SC_UPDATE();
        	 }
                   });
        mar_pan_sc.add(sc_cb53);
        
        sc_cb_1 = new JCheckBox();
        sc_cb_1.setLocation(1205, 20);
        sc_cb_1.setSize(15, 15);
        sc_cb_1.setSelected(true);
        sc_cb_1.addItemListener(new ItemListener() {
          	 public void itemStateChanged(ItemEvent e) {
          		CHANGE_SELECT_TABLE5();
          	 }
                     });
        sc_cb_1.setHorizontalAlignment(0);
        P1_page02.add(sc_cb_1);
        
        // Engine definition
        int tablewidth6 = 950;
        int tablehight6 = 150;
        JPanel panel6 = new JPanel();
        panel6.setLayout(null);
        panel6.setLocation(25,25);
        panel6.setSize(tablewidth6,tablehight6);
        P1_page03.add(panel6);
        
        table6 = new JTable();
        model6 = new DefaultTableModel();
        model6.setColumnIdentifiers(columns6);
        table6.setModel(model6);
        ((JTable) table6).setFillsViewportHeight(true);
        table6.setBackground(t_c);
        table6.setForeground(l_c);
        
        
        @SuppressWarnings("serial")
		Action action6 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                @SuppressWarnings("unused")
				TableCellListener tcl6 = (TableCellListener)e.getSource();
               // System.out.println("Row   : " + tcl.getRow());
               // System.out.println("Column: " + tcl.getColumn());
               // System.out.println("Old   : " + tcl.getOldValue());
               // System.out.println("New   : " + tcl.getNewValue());
                FNC_EN_UPDATE();
                Tables_SYNC();
            }
        };
    @SuppressWarnings("unused")
	TableCellListener tcl6 = new TableCellListener(table6, action6);
        
        table6.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table6.getColumnModel().getColumn(0).setPreferredWidth(150);
        table6.getColumnModel().getColumn(1).setPreferredWidth(70);
        table6.getColumnModel().getColumn(2).setPreferredWidth(150);
        table6.getColumnModel().getColumn(3).setPreferredWidth(70);
        table6.getColumnModel().getColumn(4).setPreferredWidth(70);
        table6.getColumnModel().getColumn(5).setPreferredWidth(140);
        table6.getColumnModel().getColumn(6).setPreferredWidth(160);
        table6.getColumnModel().getColumn(7).setPreferredWidth(160);
        
        JScrollPane spTable6 = new JScrollPane(table6);
        spTable6.setLocation(0,0);
        spTable6.setSize(tablewidth6,tablehight6);
        panel6.add(spTable6);
        
        en_1 = new JLabel("Propulsive Elements");
        en_1.setLocation(250, 0);
        en_1.setSize(120, 30);
        en_1.setHorizontalAlignment(0);
        en_1.setForeground(l_c);
        P1_page03.add(en_1);
        
        
        en_add = new JButton("Add");
        en_add.setLocation(25, 185);
        en_add.setSize(60, 30);
        en_add.addActionListener(this);
        en_add.setForeground(Color.blue);
        P1_page03.add(en_add);
        
        en_delete = new JButton("Delete");
        en_delete.setLocation(95, 185);
        en_delete.setSize(70, 30);
        en_delete.addActionListener(this);
        P1_page03.add(en_delete);
        
        subtabPane_P3 = new JTabbedPane();
        subtabPane_P3.setLocation(420, 180);
        subtabPane_P3.setSize(extx_41-45, exty_41-45);
        
    	plotsub_P131 = new JPanel();
    	plotsub_P131.setLayout(null);
    	plotsub_P131.setLocation(posx_41, posy_41);
    	plotsub_P131.setSize(extx_41-45, exty_41-45);
    	plotsub_P131.setBackground(Color.white);
    	
    	plotsub_P132 = new JPanel();
    	plotsub_P132.setLayout(null);
    	plotsub_P132.setLocation(posx_41, posy_41);
    	plotsub_P132.setSize(extx_41-45, exty_41-45);
    	plotsub_P132.setBackground(Color.white);


    	subtabPane_P3.addTab("Main Engine - MW" , null, plotsub_P131, null);
    	subtabPane_P3.setMnemonicAt(0, KeyEvent.VK_1);
    	subtabPane_P3.addTab("Main Engine - D-MD", null, plotsub_P132, null);
    	subtabPane_P3.setMnemonicAt(0, KeyEvent.VK_2);
    	subtabPane_P3.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    	P1_page03.add(subtabPane_P3);
    	
    	//------------------------------------------------------------------------------
    	//Panel 1.4 Power systems
        int tablewidth8 = 1027;
        int tablehight8 = 600;
        JPanel panel8 = new JPanel();
        panel8.setLayout(null);
        panel8.setLocation(15,35);
        panel8.setSize(tablewidth8,tablehight8);
        P1_page04.add(panel8);
        
        table8 = new JTable(){

            private static final long serialVersionUID = 1L;

            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/
            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 7:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        	@Override
        	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	            Component comp = super.prepareRenderer(renderer, row, col);
	            Object value = getModel().getValueAt(row, col);
	            String val_is = String.valueOf(value);
	            try {
	                if (value.equals(true)) {
	                    comp.setBackground(Color.gray);
	                    //FNC_SC_UPDATE();
	                } else if (val_is.contains("Structure") || val_is.contains("structure")){
	                	comp.setBackground(Color.black);
	                    comp.setForeground(Color.white);
	                } else if (val_is.contains("Propulsion") || val_is.contains("propulsion")){
	                	comp.setBackground(Color.green);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("I/F") || val_is.contains("Interface")){
	                	comp.setBackground(new Color(255, 102, 102));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Thermal") || val_is.contains("thermal")){
	                	comp.setBackground(new Color(108, 166, 89));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Mechanism") || val_is.contains("mechanism")){
	                	comp.setBackground(new Color(184, 138, 122));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Auxiliary") || val_is.contains("aux")){
	                	comp.setBackground(new Color(255, 242, 204));
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("GNC") || val_is.contains("AOCS")){
	                	comp.setBackground(new Color(19, 0, 89));
	                    comp.setForeground(Color.white);
	                } else if (val_is.contains("C&T")){
	                	comp.setBackground(Color.red);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("Power")){
	                	comp.setBackground(Color.yellow);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("C&DH")){
	                	comp.setBackground(Color.orange);
	                    comp.setForeground(Color.black);
	                } else if (val_is.contains("PMR")){
	                	comp.setBackground(Color.white);
	                    comp.setForeground(Color.gray);
	                } else {
	                comp.setBackground(t_c);
	                comp.setForeground(Color.black);
	                }
	                //FNC_SC_UPDATE();
	            } catch (NullPointerException e) {
	            	
	            }
	            return comp;
	        }
        }; ;
        
        
        @SuppressWarnings("serial")
		Action action8 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                @SuppressWarnings("unused")
				TableCellListener tcl8 = (TableCellListener)e.getSource();
               // System.out.println("Row   : " + tcl.getRow());
               // System.out.println("Column: " + tcl.getColumn());
               // System.out.println("Old   : " + tcl.getOldValue());
               // System.out.println("New   : " + tcl.getNewValue());
                FNC_POWER_UPDATE();
            }
        };
    @SuppressWarnings("unused")
	TableCellListener tcl8 = new TableCellListener(table8, action8);
        
        model8 = new DefaultTableModel(){

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
				if (column == 0 || column == 1 || column == 8 ){
					 return false;
				} else {
					return true; 
				}
            }
        };
        model8.setColumnIdentifiers(columns8);
        table8.setModel(model8);
        ((JTable) table8).setFillsViewportHeight(true);
        table8.setBackground(t_c);
        table8.setForeground(l_c);
        
        table8.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table8.getColumnModel().getColumn(0).setPreferredWidth(214);
        table8.getColumnModel().getColumn(1).setPreferredWidth(60);
        table8.getColumnModel().getColumn(2).setPreferredWidth(150);
        table8.getColumnModel().getColumn(3).setPreferredWidth(145);
        table8.getColumnModel().getColumn(4).setPreferredWidth(120);
        table8.getColumnModel().getColumn(5).setPreferredWidth(140);
        table8.getColumnModel().getColumn(6).setPreferredWidth(140);
        table8.getColumnModel().getColumn(7).setPreferredWidth(40);
        table8.getColumnModel().getColumn(8).setPreferredWidth(50); 


        JScrollPane spTable8 = new JScrollPane(table8, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spTable8.setLocation(0,0);
        spTable8.setSize(tablewidth8,tablehight8);
        panel8.add(spTable8);
        
        JLabel l_power_01 = new JLabel("Power Equipment");
        l_power_01.setLocation(620, 5);
        l_power_01.setSize(120, 30);
        l_power_01.setHorizontalAlignment(0);
        l_power_01.setForeground(l_c);
        P1_page04.add(l_power_01);
    	//-----------------------------------------------------        
        // Panel |2| Delta-V 
        resPanel = new JPanel();
        resPanel.setLayout(null);
        resPanel.setLocation(5, 290);
        resPanel.setSize(700, 138);
        resPanel.setBackground(w_c);
        resPanel.setBorder(BorderFactory.createLineBorder(l_c));
        mainPanel2.add(resPanel);
        resPanel2 = new JPanel();
        resPanel2.setLayout(null);
        resPanel2.setLocation(710, 290);
        resPanel2.setSize(400, 138);
        resPanel2.setBackground(w_c);
        resPanel2.setBorder(BorderFactory.createLineBorder(l_c));
        mainPanel2.add(resPanel2);

        redButton = new JButton("Update");
        redButton.setLocation(5, 20);
        redButton.setSize(90, 30);
        redButton.addActionListener(this);
        mainPanel2.add(redButton);
        
        
        
        rp2_5 = new JLabel("Max. total system mass [kg]");
        rp2_5.setLocation(5, 75);
        rp2_5.setSize(200, 20);
        rp2_5.setHorizontalAlignment(0);
        rp2_5.setForeground(l_c);
        rp2_5.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel.add(rp2_5);
        
        rp2_6 = new JLabel("-");
        rp2_6.setLocation(205, 75);
        rp2_6.setSize(87, 30);
        rp2_6.setHorizontalAlignment(0);
        rp2_6.setForeground(l_c);
        resPanel.add(rp2_6);
        
        rp2_3 = new JLabel("PMF [%]");
        rp2_3.setLocation(5, 5);
        rp2_3.setSize(100, 20);
        rp2_3.setHorizontalAlignment(0);
        rp2_3.setForeground(l_c);
        rp2_3.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel2.add(rp2_3);
        rp2_4 = new JLabel("-");
        rp2_4.setLocation(5, 30);
        rp2_4.setSize(50, 20);
        rp2_4.setHorizontalAlignment(0);
        rp2_4.setForeground(l_c);
        rp2_4.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel2.add(rp2_4);
        
        
     
        JLabel ll_X2 = new JLabel("Included Losses:");
        ll_X2.setLocation(5, 75);
        ll_X2.setSize(190, 20);
        ll_X2.setHorizontalAlignment(0);
        ll_X2.setFont(labelfont_small);
        ll_X2.setForeground(l_c);
        ll_X2.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanel2.add(ll_X2);
        
        boiloff_label = new JLabel("BO");
        boiloff_label.setLocation(5, 95);
        boiloff_label.setSize(190, 20);
        boiloff_label.setHorizontalAlignment(0);
        boiloff_label.setHorizontalAlignment(SwingConstants.LEFT);
        boiloff_label.setForeground(l_c);
        mainPanel2.add(boiloff_label);
        
        
        steering_label = new JLabel("SL");
        steering_label.setLocation(5, 120);
        steering_label.setSize(190, 20);
        steering_label.setHorizontalAlignment(0);
        steering_label.setForeground(l_c);
        steering_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanel2.add(steering_label);
        
        
        margin_label = new JLabel("MAR");
        margin_label.setLocation(5, 145);
        margin_label.setSize(190, 20);
        margin_label.setHorizontalAlignment(0);
        margin_label.setHorizontalAlignment(SwingConstants.LEFT);
        margin_label.setForeground(l_c);
        mainPanel2.add(margin_label);
        
        JPanel mar_indicator2 = new JPanel();
        mar_indicator2.setLayout(null);
        mar_indicator2.setLocation(670, 5);
        mar_indicator2.setSize(20, 60);
        mar_indicator2.setBackground(bc_c);
        //mar_indicator2.setBorder(BorderFactory.createLineBorder(l_c));
        resPanel.add(mar_indicator2);
        
        mari_bo2 = new JPanel();
        mari_bo2.setLayout(null);
        mari_bo2.setLocation(0, 0);
        mari_bo2.setSize(19, 19);
        mari_bo2.setBackground(l_c);
        mari_bo2.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator2.add(mari_bo2);
        mari_sl2 = new JPanel();
        mari_sl2.setLayout(null);
        mari_sl2.setLocation(0, 20);
        mari_sl2.setSize(19, 19);
        mari_sl2.setBackground(l_c);
        mari_sl2.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator2.add(mari_sl2);
        mari_mar2 = new JPanel();
        mari_mar2.setLayout(null);
        mari_mar2.setLocation(0, 40);
        mari_mar2.setSize(19, 19);
        mari_mar2.setBackground(l_c);
        mari_mar2.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator2.add(mari_mar2);
        
        //--------------------------------------------------
        Lres1 = new JLabel("Residual propellant mass [kg] ");
        Lres1.setLocation(5, 0);
        Lres1.setSize(200, 30);
        Lres1.setForeground(l_c);
        Lres1.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(Lres1);
        Lres2 = new JLabel("Total propellant mass [kg] ");
        Lres2.setLocation(5, 35);
        Lres2.setSize(200, 30);
        Lres2.setForeground(l_c);
        Lres2.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(Lres2);

        Lres4 = new JLabel("Total Delta-V [m/s] ");
        Lres4.setLocation(5, 105);
        Lres4.setSize(200, 30);
        Lres4.setForeground(l_c);
        Lres4.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(Lres4);
        
        
        Lres3 = new JLabel("S/C maximum dry mass [kg] ");
        Lres3.setLocation(350, 0);
        Lres3.setSize(200, 30);
        Lres3.setForeground(l_c);
        Lres3.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(Lres3);
        Res3 = new JLabel("-");
        Res3.setLocation(535, 0);
        Res3.setSize(87, 30);
        Res3.setForeground(Color.red);
        Res3.setHorizontalAlignment(0);
        resPanel.add(Res3);
        rp2_d1 = new JLabel("-");
        rp2_d1.setLocation(605, 0);
        rp2_d1.setSize(60, 30);
        rp2_d1.setForeground(Color.red);
        rp2_d1.setHorizontalAlignment(0);
        resPanel.add(rp2_d1);
        rp2_1 = new JLabel("For set payload [kg]");
        rp2_1.setLocation(350, 35);
        rp2_1.setSize(200, 20);
        rp2_1.setForeground(l_c);
        rp2_1.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(rp2_1);
        rp2_2 = new JLabel("-");
        rp2_2.setLocation(555, 35);
        rp2_2.setSize(87, 30);
        rp2_2.setHorizontalAlignment(0);
        rp2_2.setForeground(l_c);
        resPanel.add(rp2_2);
        
        
        rp2_7 = new JLabel("Payload maximum [kg] ");
        rp2_7.setLocation(350, 70);
        rp2_7.setSize(200, 30);
        rp2_7.setForeground(l_c);
        rp2_7.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(rp2_7);
        rp2_8 = new JLabel("-");
        rp2_8.setLocation(535, 70);
        rp2_8.setSize(87, 30);
        rp2_8.setForeground(Color.red);
        rp2_8.setHorizontalAlignment(0);
        resPanel.add(rp2_8);
        rp2_d2 = new JLabel("-");
        rp2_d2.setLocation(605, 70);
        rp2_d2.setSize(60, 30);
        rp2_d2.setForeground(Color.red);
        rp2_d2.setHorizontalAlignment(0);
        resPanel.add(rp2_d2);
        rp2_9 = new JLabel("For set S/C dry mass [kg]");
        rp2_9.setLocation(350, 105);
        rp2_9.setSize(200, 20);
        rp2_9.setHorizontalAlignment(0);
        rp2_9.setForeground(l_c);
        rp2_9.setHorizontalAlignment(JLabel.LEFT);
        resPanel.add(rp2_9);
        rp2_10 = new JLabel("-");
        rp2_10.setLocation(555, 105);
        rp2_10.setSize(87, 30);
        rp2_10.setHorizontalAlignment(0);
        rp2_10.setForeground(l_c);
        resPanel.add(rp2_10);

        //------------------------------------------------
        Res1 = new JLabel("-");
        Res1.setLocation(205, 0);
        Res1.setSize(87, 30);
        Res1.setForeground(l_c);
        Res1.setHorizontalAlignment(0);
        resPanel.add(Res1);
        Res2 = new JLabel("-");
        Res2.setLocation(205, 35);
        Res2.setSize(87, 30);
        Res2.setForeground(l_c);
        Res2.setHorizontalAlignment(0);
        resPanel.add(Res2);

        Res4 = new JLabel("-");
        Res4.setLocation(205, 105);
        Res4.setSize(87, 30);
        Res4.setForeground(l_c);
        Res4.setHorizontalAlignment(0);
        resPanel.add(Res4);

        
        int tablewidth = 1230;
        int tablehight = 250;
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setLocation(100,20);
        panel.setSize(tablewidth,tablehight);
        mainPanel2.add(panel);
        
        table = new JTable();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        table.setModel(model);
        ((JTable) table).setFillsViewportHeight(true);
        table.setBackground(t_c);
        table.setForeground(l_c);
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(190);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(60);
        table.getColumnModel().getColumn(8).setPreferredWidth(60);
        table.getColumnModel().getColumn(9).setPreferredWidth(130);
        table.getColumnModel().getColumn(10).setPreferredWidth(135);
        table.getColumnModel().getColumn(11).setPreferredWidth(135);
        table.getColumnModel().getColumn(12).setPreferredWidth(100);       
        JScrollPane spTable = new JScrollPane(table);
        spTable.setLocation(0,0);
        spTable.setSize(tablewidth,tablehight);
        panel.add(spTable);
        
        // Initialize Plot Area
        subtabPane1 = new JTabbedPane();
        subtabPane1.setLocation(posx_21, posy_21);
        subtabPane1.setSize(extx_21, exty_21);
        
    	plotsub_21 = new JPanel();
    	plotsub_21.setLayout(null);
    	plotsub_21.setLocation(posx_21, posy_21);
    	plotsub_21.setSize(extx_21, exty_21);
    	plotsub_21.setBackground(Color.white);
    	plotsub_22 = new JPanel();
    	plotsub_22.setLayout(null);
    	plotsub_22.setLocation(posx_21, posy_21);
    	plotsub_22.setSize(extx_21, exty_21);
    	plotsub_22.setBackground(Color.white);

        subtabPane1.addTab("Tank" , null, plotsub_21, null);
        subtabPane1.setMnemonicAt(0, KeyEvent.VK_1);
        subtabPane1.addTab("S/C" , null, plotsub_22, null);
        subtabPane1.setMnemonicAt(0, KeyEvent.VK_2);
        
        
        subtabPane3 = new JTabbedPane();
        subtabPane3.setLocation(posx_31, posy_31);
        subtabPane3.setSize(extx_31, exty_31);
        
    	plotsub_31 = new JPanel();
    	plotsub_31.setLayout(null);
    	plotsub_31.setLocation(0, 0);
    	plotsub_31.setBackground(Color.white);
    	plotsub_31.setSize(extx_31, exty_31);
    	plotsub_32 = new JPanel();
    	plotsub_32.setLayout(null);
    	plotsub_32.setLocation(0, 0);
    	plotsub_32.setBackground(Color.white);
    	plotsub_32.setSize(extx_31, exty_31);

        subtabPane3.addTab("S/C" , null, plotsub_31, null);
        subtabPane3.setMnemonicAt(0, KeyEvent.VK_1);
        subtabPane3.addTab("Margin" , null, plotsub_32, null);
        subtabPane3.setMnemonicAt(0, KeyEvent.VK_2);
        
        //CreateChart_03();
        
        mainPanel2.add(subtabPane1);
        mainPanel2.add(subtabPane3);
        //------------------------------------------------------------------------
        // Pane 3 - Editor : 
        
        subtabPane31 = new JTabbedPane();
        subtabPane31.setLocation(0, 0);
        subtabPane31.setSize(1350, 900);
        //subtabPane11.setBackground(bc_c);
        
        mainPanel31 = new JPanel();
        mainPanel31.setLayout(null);
        mainPanel31.setLocation(0, 0);
        mainPanel31.setBackground(Color.white);
        mainPanel31.setSize(extx_31, 800);
        
        mainPanel32 = new JPanel();
        mainPanel32.setLayout(null);
        mainPanel32.setLocation(0, 0);
        mainPanel32.setBackground(Color.white);
        mainPanel32.setSize(extx_31, 800);
        
        analysis_TF_01 = new JTextField("0.00");
        analysis_TF_01.setLocation(5, 700);
        analysis_TF_01.setSize(50, 25);
        analysis_TF_01.setHorizontalAlignment(0);
        analysis_TF_01.setForeground(Color.black);
        mainPanel31.add(analysis_TF_01);
        
        
        JButton confirm_PMF = new JButton("Confirm PMF");
        confirm_PMF.setFont(labelfont_small);
        confirm_PMF.setLocation(60, 700);
        confirm_PMF.setSize(150, 25);
        confirm_PMF.addActionListener(new ActionListener() {
         	 public void actionPerformed(ActionEvent e) {
         		UPDATE_Analysis();
         	 }
                    });
        mainPanel31.add(confirm_PMF);
        /*
        a_cb1 = new JCheckBox("Include variable PMF analysis");
        a_cb1.setLocation(235, 700);
        a_cb1.setSize(250, 15);
        a_cb1.setBackground(bc_c);
        a_cb1.setSelected(false);
        a_cb1.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
   			if (a_cb1.isSelected()){
   				a_complex = true;
   			} else {
   				a_complex = false; 
   			}
   			UPDATE_Analysis();
       	 }
                  });
        a_cb1.setHorizontalAlignment(0);
        mainPanel31.add(a_cb1);
        */

        subtabPane31.addTab("Overview" , null, mainPanel3, null);
        subtabPane31.setMnemonicAt(0, KeyEvent.VK_1);
        subtabPane31.addTab("Analysis" , null, mainPanel31, null);
        subtabPane31.setMnemonicAt(0, KeyEvent.VK_2);
        subtabPane31.addTab("Payload-Scaling" , null, mainPanel32, null);
        subtabPane31.setMnemonicAt(0, KeyEvent.VK_3);
        subtabPane31.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int ACTIVE_TAB = subtabPane31.getSelectedIndex();
		                if (ACTIVE_TAB == 1){
		                	//UPDATE_Analysis();
		                } else if (ACTIVE_TAB == 2){
		                	
		                }
        }                
        });
        
        //______________________________________________________________
        // Analysis area sideboard
        
        //Create horizontal menu bar.
        JMenuBar ScalingBar = new JMenuBar();
        ScalingBar.setLocation(0, 0);
        ScalingBar.setSize(extx_main, 20);
        mainPanel32.add(ScalingBar);

        //Build the first menu.
        JMenu menuScaling = new JMenu("Payload Scaling ");
        menuScaling.setForeground(Color.black);
        ScalingBar.add(menuScaling);
        ScalingBar.setBackground(new Color(184,207,229));
        JMenuItem  mS1 = new JMenuItem("Start Calculation            ");
        mS1.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuScaling.add(mS1);
        mS1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   UPDATE_PL_Scale();   
                    } });
        menuScaling.addSeparator();
        mIII1 = new JMenuItem("Scaling - convergence history  ");
        mIII1.setForeground(Color.black);
        //mIII1.setFont(menufont);
        menuScaling.add(mIII1);
        mIII1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   Convergence_window();
                    } });
        menuScaling.addSeparator();

        
        
        int aib_dx = 170;
        int aib_dy = 260;
        analysis_infoboard = new JPanel();
        analysis_infoboard.setLayout(null);
        analysis_infoboard.setLocation(1350, 20);
        analysis_infoboard.setBackground(Color.white);
        analysis_infoboard.setBorder(BorderFactory.createLineBorder(Color.black));
        analysis_infoboard.setSize(aib_dx, aib_dy);
        mainPanel32.add(analysis_infoboard);
        
        int dspace = 18;
       JLabel aib_title = new JLabel("Scaled S/C select (model 1): ");
       aib_title.setLocation(2, 2 + 25 *0);
       aib_title.setSize(aib_dx, 20);
       aib_title.setForeground(l_c);
       aib_title.setFont(labelfont_small);
       aib_title.setHorizontalAlignment(JLabel.LEFT);
        analysis_infoboard.add(aib_title);
        aib_md = new JLabel("");
        aib_md.setLocation(2, 27 + dspace *0);
        aib_md.setSize(aib_dx, 20);
        aib_md.setForeground(l_c);
        aib_md.setFont(labelfont_small);
        aib_md.setHorizontalAlignment(JLabel.LEFT);
        analysis_infoboard.add(aib_md);
        aib_mw = new JLabel("");
        aib_mw.setLocation(2, 27 + dspace *1);
        aib_mw.setSize(aib_dx, 20);
        aib_mw.setForeground(l_c);
        aib_mw.setFont(labelfont_small);
        aib_mw.setHorizontalAlignment(JLabel.LEFT);
        analysis_infoboard.add(aib_mw);
        aib_mp = new JLabel("");
        aib_mp.setLocation(2, 27 + dspace *2);
        aib_mp.setSize(aib_dx, 20);
        aib_mp.setForeground(l_c);
        aib_mp.setFont(labelfont_small);
        aib_mp.setHorizontalAlignment(JLabel.LEFT);
        analysis_infoboard.add(aib_mp);
        aib_mtw = new JLabel("");
        aib_mtw.setLocation(2, 27 + dspace *3);
        aib_mtw.setSize(aib_dx, 20);
        aib_mtw.setForeground(l_c);
        aib_mtw.setFont(labelfont_small);
        aib_mtw.setHorizontalAlignment(JLabel.LEFT);
        analysis_infoboard.add(aib_mtw);
        aib_mpay = new JLabel("");
        aib_mpay.setLocation(2, 27 + dspace *4);
        aib_mpay.setSize(aib_dx, 20);
        aib_mpay.setForeground(l_c);
        aib_mpay.setFont(labelfont_small_bold);
        aib_mpay.setHorizontalAlignment(JLabel.LEFT);
        analysis_infoboard.add(aib_mpay);
        
        JLabel aib_title2 = new JLabel("Scaled S/C select (model 2): ");
        aib_title2.setLocation(2, 27 + dspace *6);
        aib_title2.setSize(aib_dx, 20);
        aib_title2.setForeground(l_c);
        aib_title2.setFont(labelfont_small);
        aib_title2.setHorizontalAlignment(JLabel.LEFT);
         analysis_infoboard.add(aib_title2);
         aib_md2 = new JLabel("");
         aib_md2.setLocation(2, 27 + dspace *7);
         aib_md2.setSize(aib_dx, 20);
         aib_md2.setForeground(l_c);
         aib_md2.setFont(labelfont_small);
         aib_md2.setHorizontalAlignment(JLabel.LEFT);
         analysis_infoboard.add(aib_md2);
         aib_mw2 = new JLabel("");
         aib_mw2.setLocation(2, 27 + dspace *8);
         aib_mw2.setSize(aib_dx, 20);
         aib_mw2.setForeground(l_c);
         aib_mw2.setFont(labelfont_small);
         aib_mw2.setHorizontalAlignment(JLabel.LEFT);
         analysis_infoboard.add(aib_mw2);
         aib_mp2 = new JLabel("");
         aib_mp2.setLocation(2, 27 + dspace *9);
         aib_mp2.setSize(aib_dx, 20);
         aib_mp2.setForeground(l_c);
         aib_mp2.setFont(labelfont_small);
         aib_mp2.setHorizontalAlignment(JLabel.LEFT);
         analysis_infoboard.add(aib_mp2);
         aib_mtw2 = new JLabel("");
         aib_mtw2.setLocation(2, 27 + dspace *10);
         aib_mtw2.setSize(aib_dx, 20);
         aib_mtw2.setForeground(l_c);
         aib_mtw2.setFont(labelfont_small);
         aib_mtw2.setHorizontalAlignment(JLabel.LEFT);
         analysis_infoboard.add(aib_mtw2);
         aib_mpay2 = new JLabel("");
         aib_mpay2.setLocation(2, 27 + dspace *11);
         aib_mpay2.setSize(aib_dx, 20);
         aib_mpay2.setForeground(l_c);
         aib_mpay2.setFont(labelfont_small);
         aib_mpay2.setHorizontalAlignment(JLabel.LEFT);
         analysis_infoboard.add(aib_mpay2);
         
         aib_warning = new JLabel("");
         aib_warning.setLocation(1300, 330);
         aib_warning.setSize(aib_dx, 20);
         aib_warning.setForeground(l_c);
         aib_warning.setFont(labelfont_small);
         aib_warning.setHorizontalAlignment(JLabel.LEFT);
         mainPanel32.add(aib_warning);
        //______________________________________________________________

         
        refButton = new JButton("Update");
        refButton.setLocation(5, 20);
        refButton.setSize(90, 30);
        refButton.addActionListener(this);
        mainPanel3.add(refButton);
        
        
        
        int tablewidth4 = 620;
        int tablehight4 = 250;
        JPanel panel4 = new JPanel();
        panel4.setLayout(null);
        panel4.setLocation(100,20);
        panel4.setSize(tablewidth4,tablehight4);
        mainPanel3.add(panel4);
        
        table4 = new JTable();
        model4 = new DefaultTableModel();
        model4.setColumnIdentifiers(columns4);
        table4.setModel(model4);
        ((JTable) table4).setFillsViewportHeight(true);
        table4.setBackground(t_c);
        table4.setForeground(l_c);
        
        table4.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table4.getColumnModel().getColumn(0).setPreferredWidth(190);
        table4.getColumnModel().getColumn(1).setPreferredWidth(50);
        table4.getColumnModel().getColumn(2).setPreferredWidth(50);
        table4.getColumnModel().getColumn(3).setPreferredWidth(50);
        table4.getColumnModel().getColumn(4).setPreferredWidth(70);
        table4.getColumnModel().getColumn(5).setPreferredWidth(110);
        table4.getColumnModel().getColumn(6).setPreferredWidth(90);
        table4.getColumnModel().getColumn(7).setPreferredWidth(60);
        table4.getColumnModel().getColumn(8).setPreferredWidth(60);
        table4.getColumnModel().getColumn(9).setPreferredWidth(130);
        table4.getColumnModel().getColumn(10).setPreferredWidth(135);
        table4.getColumnModel().getColumn(11).setPreferredWidth(135);
        table4.getColumnModel().getColumn(12).setPreferredWidth(100);       
        JScrollPane spTable4 = new JScrollPane(table4);
        spTable4.setLocation(0,0);
        spTable4.setSize(tablewidth4,tablehight4);
        panel4.add(spTable4);
        
        
        subtabPane4 = new JTabbedPane();
        subtabPane4.setLocation(posx_41, posy_41);
        subtabPane4.setSize(extx_41, exty_41);
        
    	plotsub_41 = new JPanel();
    	plotsub_41.setLayout(null);
    	plotsub_41.setLocation(posx_41, posy_41);
    	plotsub_41.setSize(extx_41, exty_41);
    	plotsub_41.setBackground(Color.white);
    	plotsub_42 = new JPanel();
    	plotsub_42.setLayout(null);
    	plotsub_42.setLocation(posx_41, posy_41);
    	plotsub_42.setSize(extx_41, exty_41);
    	plotsub_42.setBackground(Color.white);
    	plotsub_43 = new JPanel();
    	plotsub_43.setLayout(null);
    	plotsub_43.setLocation(posx_41, posy_41);
    	plotsub_43.setSize(extx_41, exty_41);
    	plotsub_43.setBackground(Color.white);
    	plotsub_44 = new JPanel();
    	plotsub_44.setLayout(null);
    	plotsub_44.setLocation(posx_41, posy_41);
    	plotsub_44.setSize(extx_41, exty_41);
    	plotsub_44.setBackground(Color.white);
    	plotsub_45 = new JPanel();
    	plotsub_45.setLayout(null);
    	plotsub_45.setLocation(posx_41, posy_41);
    	plotsub_45.setSize(extx_41, exty_41);
    	plotsub_45.setBackground(Color.white);
    	plotsub_46 = new JPanel();
    	plotsub_46.setLayout(null);
    	plotsub_46.setLocation(posx_41, posy_41);
    	plotsub_46.setSize(extx_41, exty_41);
    	plotsub_46.setBackground(Color.white);
    	plotsub_47 = new JPanel();
    	plotsub_47.setLayout(null);
    	plotsub_47.setLocation(posx_41, posy_41);
    	plotsub_47.setSize(extx_41, exty_41);
    	plotsub_47.setBackground(Color.white);
    	plotsub_48 = new JPanel();
    	plotsub_48.setLayout(null);
    	plotsub_48.setLocation(posx_41, posy_41);
    	plotsub_48.setSize(extx_41, exty_41);
    	plotsub_48.setBackground(Color.white);
    	  
        cb_sub41 = new JCheckBox("Show Marker") ;
        cb_sub41.setLocation(300, 0);
        cb_sub41.setSize(110, 20);
        cb_sub41.setSelected(true);
        cb_sub41.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub41.isSelected()){
           	    	Chart4_ResetMarker();
           	    	} else {
           	        	try {
           	            	final XYPlot plot = (XYPlot) chart4.getPlot();
           	            	plot.clearRangeMarkers();
           	            	plot.clearDomainMarkers();
           	        	} catch (NullPointerException e21) {
           	            }
       	    	}
       	 } });
        cb_sub41.setBackground(Color.white);
        plotsub_41.add(cb_sub41);
        cb_sub42 = new JCheckBox("Show Marker") ;
        cb_sub42.setLocation(300, 0);
        cb_sub42.setSize(110, 25);
        cb_sub42.setSelected(true);
        cb_sub42.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub42.isSelected()){
       	    	Chart6_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart6.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub42.setBackground(Color.white);
        plotsub_42.add(cb_sub42);
        cb_sub43 = new JCheckBox("Show Marker") ;
        cb_sub43.setLocation(300, 0);
        cb_sub43.setSize(110, 25);
        cb_sub43.setSelected(true);
        cb_sub43.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub43.isSelected()){
       	    	Chart8_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart8.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub43.setBackground(Color.white);
        plotsub_43.add(cb_sub43);
        cb_sub44 = new JCheckBox("Show Marker") ;
        cb_sub44.setLocation(300, 0);
        cb_sub44.setSize(110, 25);
        cb_sub44.setSelected(true);
        cb_sub44.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub44.isSelected()){
       	    	Chart9_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart9.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub44.setBackground(Color.white);
        plotsub_44.add(cb_sub44);
        cb_sub45 = new JCheckBox("Show Marker") ;
        cb_sub45.setLocation(300, 0);
        cb_sub45.setSize(110, 25);
        cb_sub45.setSelected(true);
        cb_sub45.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub45.isSelected()){
       	    	Chart10_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart10.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub45.setBackground(Color.white);
        plotsub_45.add(cb_sub45);
        cb_sub46 = new JCheckBox("Show Marker") ;
        cb_sub46.setLocation(250, 5);
        cb_sub46.setSize(110, 25);
        cb_sub46.setSelected(true);
        cb_sub46.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub46.isSelected()){
       	    	Chart11_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart11.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub46.setBackground(Color.white);
        plotsub_46.add(cb_sub46); 
        cb_sub47 = new JCheckBox("Show Marker") ;
        cb_sub47.setLocation(250, 5);
        cb_sub47.setSize(110, 25);
        cb_sub47.setSelected(true);
        cb_sub47.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub47.isSelected()){
       	    	Chart12_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart12.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub47.setBackground(Color.white);
        plotsub_47.add(cb_sub47); 
        cb_sub48 = new JCheckBox("Show Marker") ;
        cb_sub48.setLocation(250, 5);
        cb_sub48.setSize(110, 25);
        cb_sub48.setSelected(true);
        cb_sub48.addItemListener(new ItemListener() {
       	 public void itemStateChanged(ItemEvent e) {
       		 if(cb_sub48.isSelected()){
       	    	Chart12_ResetMarker();
       	    	} else {
       	        	try {
       	            	final XYPlot plot = (XYPlot) chart13.getPlot();
       	            	plot.clearRangeMarkers();
       	            	plot.clearDomainMarkers();
       	        	} catch (NullPointerException e21) {
       	            }
       	    	}
       	 } });
        cb_sub48.setBackground(Color.white);
        plotsub_48.add(cb_sub48); 


        subtabPane4.addTab("PMF - MW" , null, plotsub_41, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_1);
        subtabPane4.addTab("MD - MW" , null, plotsub_42, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_2);
        subtabPane4.addTab("\u03B5 - MW" , null, plotsub_43, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_3);
        subtabPane4.addTab("\u03B5 - \u03BB" , null, plotsub_44, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_4);
        subtabPane4.addTab("MD - MP" , null, plotsub_45, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_5);
        subtabPane4.addTab("MP - MW" , null, plotsub_46, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_6);
        subtabPane4.addTab("MD - MPR" , null, plotsub_47, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_7);
        subtabPane4.addTab("MD - MWS" , null, plotsub_48, null);
        subtabPane4.setMnemonicAt(0, KeyEvent.VK_8);
        
        mainPanel3.add(subtabPane4); 
        
        resPanel41 = new JPanel();
        resPanel41.setLayout(null);
        resPanel41.setLocation(5, 275);
        resPanel41.setSize(712, 245);
        resPanel41.setBackground(w_c);
        resPanel41.setBorder(BorderFactory.createLineBorder(l_c));
        mainPanel3.add(resPanel41);
        
        JPanel mar_indicator = new JPanel();
        mar_indicator.setLayout(null);
        mar_indicator.setLocation(680, 5);
        mar_indicator.setSize(20, 80);
        mar_indicator.setBackground(bc_c);
        //mar_indicator.setBorder(BorderFactory.createLineBorder(l_c));
        resPanel41.add(mar_indicator);
        
        mari_bo = new JPanel();
        mari_bo.setLayout(null);
        mari_bo.setLocation(0, 0);
        mari_bo.setSize(19, 19);
        mari_bo.setBackground(l_c);
        mari_bo.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator.add(mari_bo);
        mari_sl = new JPanel();
        mari_sl.setLayout(null);
        mari_sl.setLocation(0, 20);
        mari_sl.setSize(19, 19);
        mari_sl.setBackground(l_c);
        mari_sl.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator.add(mari_sl);
        mari_mar = new JPanel();
        mari_mar.setLayout(null);
        mari_mar.setLocation(0, 40);
        mari_mar.setSize(19, 19);
        mari_mar.setBackground(l_c);
        mari_mar.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator.add(mari_mar);
        mari_pmr = new JLabel("pmr");
        mari_pmr.setLayout(null);
        mari_pmr.setLocation(0, 60);
        mari_pmr.setSize(19, 19);
        mari_pmr.setBackground(l_c);
        mari_pmr.setFont(labelfont_verysmall);
        mari_pmr.setOpaque(true);
        mari_pmr.setBorder(BorderFactory.createLineBorder(Color.black));
        mar_indicator.add(mari_pmr);
        
 
        rp4_3 = new JLabel("PMF [%]");
        rp4_3.setLocation(375, 0);
        rp4_3.setSize(200, 30);
        rp4_3.setHorizontalAlignment(0);
        rp4_3.setHorizontalAlignment(SwingConstants.LEFT);
        rp4_3.setForeground(l_c);
        resPanel41.add(rp4_3);
        
        rp4_4 = new JLabel("-");
        rp4_4.setLocation(520, 0);
        rp4_4.setSize(87, 30);
        rp4_4.setHorizontalAlignment(0);
        rp4_4.setForeground(l_c);
        //rp2_4.setForeground(Color.red);
        rp4_4.setHorizontalAlignment(0);
        
        resPanel41.add(rp4_4);
        
        
        rp4_1 = new JLabel("Payload [kg]");
        rp4_1.setLocation(5, 175);
        rp4_1.setSize(200, 30);
        rp4_1.setHorizontalAlignment(0);
        rp4_1.setHorizontalAlignment(SwingConstants.LEFT);
        rp4_1.setForeground(l_c);
        resPanel41.add(rp4_1);
        
        rp4_2 = new JLabel("-");
        rp4_2.setLocation(205, 175);
        rp4_2.setSize(87, 30);
        rp4_2.setHorizontalAlignment(0);
        rp4_2.setHorizontalAlignment(0);
        rp4_2.setForeground(l_c);
        resPanel41.add(rp4_2);
        
        rp4_5 = new JLabel("Total Wet mass [kg]");
        rp4_5.setLocation(5, 210);
        rp4_5.setSize(200, 30);
        rp4_5.setHorizontalAlignment(0);
        rp4_5.setForeground(l_c);
        rp4_5.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_5);
        
        rp4_6 = new JLabel("-");
        rp4_6.setLocation(205, 210);
        rp4_6.setSize(87, 30);
        rp4_6.setHorizontalAlignment(0);
        rp4_6.setForeground(Color.red);
        rp4_6.setHorizontalAlignment(0);
        resPanel41.add(rp4_6);
        
        rp4_14 = new JLabel("-");
        rp4_14.setLocation(300, 210);
        rp4_14.setSize(50, 30);
        rp4_14.setHorizontalAlignment(0);
        rp4_14.setForeground(l_c);
        rp4_14.setHorizontalAlignment(0);
        resPanel41.add(rp4_14);
        
        
        rp4_7 = new JLabel("D2W Factor [-]");
        rp4_7.setLocation(375, 140);
        rp4_7.setSize(200, 30);
        rp4_7.setHorizontalAlignment(0);
        rp4_7.setForeground(l_c);
        rp4_7.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_7);
        
        rp4_8 = new JLabel("-");
        rp4_8.setLocation(520, 140);
        rp4_8.setSize(87, 30);
        rp4_8.setHorizontalAlignment(0);
        rp4_8.setForeground(l_c);
        rp4_8.setHorizontalAlignment(0);
        resPanel41.add(rp4_8);
        
        rp4_9 = new JLabel("System MARGIN DRY [%]");
        rp4_9.setLocation(375, 175);
        rp4_9.setSize(200, 30);
        rp4_9.setHorizontalAlignment(0);
        rp4_9.setForeground(l_c);
        rp4_9.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_9);
        
        rp4_10 = new JLabel("-");
        rp4_10.setLocation(520, 175);
        rp4_10.setSize(87, 30);
        rp4_10.setHorizontalAlignment(0);
        rp4_10.setForeground(l_c);
        rp4_10.setHorizontalAlignment(0);
        resPanel41.add(rp4_10);
        
        rp4_21 = new JLabel("Flexible MER [%]");
        rp4_21.setLocation(375, 210);
        rp4_21.setSize(200, 30);
        rp4_21.setHorizontalAlignment(0);
        rp4_21.setForeground(l_c);
        rp4_21.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_21);
        
        rp4_22 = new JLabel("-");
        rp4_22.setLocation(520, 210);
        rp4_22.setSize(87, 30);
        rp4_22.setHorizontalAlignment(0);
        rp4_22.setForeground(l_c);
        rp4_22.setHorizontalAlignment(0);
        resPanel41.add(rp4_22);
      
        rp4_11 = new JLabel("\u03B5 [%]");
        rp4_11.setLocation(375, 35);
        rp4_11.setSize(200, 30);
        rp4_11.setHorizontalAlignment(0);
        rp4_11.setForeground(l_c);
        rp4_11.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_11);
        
        rp4_12 = new JLabel("-");
        rp4_12.setLocation(520, 35);
        rp4_12.setSize(87, 30);
        rp4_12.setHorizontalAlignment(0);
        rp4_12.setForeground(l_c);
        rp4_12.setHorizontalAlignment(0);
        resPanel41.add(rp4_12);
     
        rp4_15 = new JLabel("\u03BB [%]");
        rp4_15.setLocation(375, 70);
        rp4_15.setSize(200, 30);
        rp4_15.setHorizontalAlignment(0);
        rp4_15.setForeground(l_c);
        rp4_15.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_15);
        
        rp4_16 = new JLabel("-");
        rp4_16.setLocation(520, 70);
        rp4_16.setSize(87, 30);
        rp4_16.setHorizontalAlignment(0);
        rp4_16.setForeground(l_c);
        rp4_16.setHorizontalAlignment(0);
        resPanel41.add(rp4_16);
        
        rp4_17 = new JLabel("MR [-]");
        rp4_17.setLocation(375, 105);
        rp4_17.setSize(200, 30);
        rp4_17.setHorizontalAlignment(0);
        rp4_17.setForeground(l_c);
        rp4_17.setHorizontalAlignment(SwingConstants.LEFT);
        resPanel41.add(rp4_17);
        
        rp4_18 = new JLabel("-");
        rp4_18.setLocation(520, 105);
        rp4_18.setSize(87, 30);
        rp4_18.setHorizontalAlignment(0);
        rp4_18.setForeground(l_c);
        rp4_18.setHorizontalAlignment(0);
        resPanel41.add(rp4_18);
        
        JLabel ll_1 = new JLabel("Included Losses:");
        ll_1.setLocation(5, 75);
        ll_1.setSize(190, 20);
        ll_1.setHorizontalAlignment(0);
        ll_1.setFont(labelfont_small);
        ll_1.setForeground(l_c);
        ll_1.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanel3.add(ll_1);
        
        boiloff_label4 = new JLabel("BO");
        boiloff_label4.setLocation(5, 95);
        boiloff_label4.setSize(190, 20);
        boiloff_label4.setHorizontalAlignment(0);
        boiloff_label4.setHorizontalAlignment(SwingConstants.LEFT);
        boiloff_label4.setForeground(l_c);
        mainPanel3.add(boiloff_label4);
        
        
        steering_label4 = new JLabel("SL");
        steering_label4.setLocation(5, 120);
        steering_label4.setSize(190, 20);
        steering_label4.setHorizontalAlignment(0);
        steering_label4.setForeground(l_c);
        steering_label4.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanel3.add(steering_label4);
        
        
        margin_label4 = new JLabel("MAR");
        margin_label4.setLocation(5, 145);
        margin_label4.setSize(190, 20);
        margin_label4.setHorizontalAlignment(0);
        margin_label4.setHorizontalAlignment(SwingConstants.LEFT);
        margin_label4.setForeground(l_c);
       // margin_label4.setBackground(new Color(50,50,50));
        mainPanel3.add(margin_label4);
        
     
        //--------------------------------------------------
        Lres1 = new JLabel("Residual propellant mass [kg] ");
        Lres1.setLocation(5, 0);
        Lres1.setSize(200, 30);
        Lres1.setForeground(l_c);
        Lres1.setHorizontalAlignment(JLabel.LEFT);
        resPanel41.add(Lres1);
        Lres2 = new JLabel("Total propellant mass [kg] ");
        Lres2.setLocation(5, 35);
        Lres2.setSize(200, 30);
        Lres2.setForeground(l_c);
        Lres2.setHorizontalAlignment(JLabel.LEFT);
        resPanel41.add(Lres2);
        Lres3 = new JLabel("S/C Dry mass [kg] ");
        Lres3.setLocation(5, 70);
        Lres3.setSize(200, 30);
        Lres3.setForeground(l_c);
        Lres3.setHorizontalAlignment(JLabel.LEFT);
        resPanel41.add(Lres3);
        
        Lres5 = new JLabel("S/C Wet mass [kg] ");
        Lres5.setLocation(5, 105);
        Lres5.setSize(200, 30);
        Lres5.setForeground(l_c);
        Lres5.setHorizontalAlignment(JLabel.LEFT);
        resPanel41.add(Lres5);

        Lres4 = new JLabel("Total Delta-V [m/s] ");
        Lres4.setLocation(5, 140);
        Lres4.setSize(200, 30);
        Lres4.setForeground(l_c);
        Lres4.setHorizontalAlignment(JLabel.LEFT);
        resPanel41.add(Lres4);
        
        Res41 = new JLabel("-");
        Res41.setLocation(205, 0);
        Res41.setSize(87, 30);
        Res41.setForeground(l_c);
        Res41.setHorizontalAlignment(0);
        resPanel41.add(Res41);
        Res42 = new JLabel("-");
        Res42.setLocation(205, 35);
        Res42.setSize(87, 30);
        Res42.setForeground(l_c);
        Res42.setHorizontalAlignment(0);
        resPanel41.add(Res42);
        Res43 = new JLabel("-");
        Res43.setLocation(205, 70);
        Res43.setSize(87, 30);
        Res43.setForeground(l_c);
        Res43.setHorizontalAlignment(0);
        resPanel41.add(Res43);
        rp4_13 = new JLabel("-");
        rp4_13.setLocation(300, 70);
        rp4_13.setSize(50, 30);
        rp4_13.setHorizontalAlignment(0);
        rp4_13.setForeground(l_c);
        rp4_13.setHorizontalAlignment(0);
        resPanel41.add(rp4_13);

        Res44 = new JLabel("-");
        Res44.setLocation(205, 140);
        Res44.setSize(87, 30);
        Res44.setForeground(l_c);
        Res44.setHorizontalAlignment(0);
        resPanel41.add(Res44);
        
        Res45 = new JLabel("-");
        Res45.setLocation(205, 105);
        Res45.setSize(87, 30);
        Res45.setForeground(l_c);
        Res45.setHorizontalAlignment(0);
        resPanel41.add(Res45);
        
        subtabPane41 = new JTabbedPane();
        subtabPane41.setLocation(posx_42, posy_42);
        subtabPane41.setSize(extx_42, exty_42);
        
    	plotsub_421 = new JPanel();
    	plotsub_421.setLayout(null);
    	plotsub_421.setLocation(0, 0);
    	plotsub_421.setSize(extx_31, exty_31);
    	plotsub_421.setBackground(Color.white);
    	plotsub_422 = new JPanel();
    	plotsub_422.setLayout(null);
    	plotsub_422.setLocation(0, 0);
    	plotsub_422.setSize(extx_31, exty_31);
    	plotsub_422.setBackground(Color.white);

        subtabPane41.addTab("Margin" , null, plotsub_422, null);
        subtabPane41.setMnemonicAt(0, KeyEvent.VK_1);
        subtabPane41.addTab("S/C" , null, plotsub_421, null);
        subtabPane41.setMnemonicAt(0, KeyEvent.VK_2);
        

     	mainPanel3.add(subtabPane41);
     	
    	JPanel ressub_41 = new JPanel();
    	ressub_41.setLayout(null);
    	ressub_41.setLocation(730, 542);
    	ressub_41.setSize(600, 187);
    	ressub_41.setBackground(w_c);
    	ressub_41.setBorder(BorderFactory.createLineBorder(l_c));
    	mainPanel3.add(ressub_41);
    	
        int tablewidth7 = 590;
        int tablehight7 = 120;
        JPanel panel7 = new JPanel();
        panel7.setLayout(null);
        panel7.setLocation(5,5);
        panel7.setSize(tablewidth7,tablehight7);
        ressub_41.add(panel7);
        
        table7 = new JTable();
        model7 = new DefaultTableModel();
        model7.setColumnIdentifiers(columns7);
        table7.setModel(model7);
        
        ((JTable) table7).setFillsViewportHeight(true);
        table7.setBackground(t_c);
        table7.setForeground(l_c);
        
        table7.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table7.getColumnModel().getColumn(0).setPreferredWidth(190);
        table7.getColumnModel().getColumn(1).setPreferredWidth(120);
        table7.getColumnModel().getColumn(2).setPreferredWidth(120);
        table7.getColumnModel().getColumn(3).setPreferredWidth(120);   
        table7.getColumnModel().getColumn(4).setPreferredWidth(120);
        table7.getColumnModel().getColumn(5).setPreferredWidth(120); 
        table7.getColumnModel().getColumn(6).setPreferredWidth(120);
        table7.getColumnModel().getColumn(7).setPreferredWidth(120); 
        JScrollPane spTable7 = new JScrollPane(table7);
        spTable7.setLocation(0,0);
        spTable7.setSize(tablewidth7,tablehight7);
        panel7.add(spTable7);
        
        JLabel pg_l7 = new JLabel("Total pressurant mass [kg]: ");
        pg_l7.setLocation(5, 160);
        pg_l7.setSize(160, 25);
        pg_l7.setForeground(l_c);
        pg_l7.setHorizontalAlignment(0);
        ressub_41.add(pg_l7);
        
        pg_l8 = new JLabel("-");
        pg_l8.setLocation(170, 160);
        pg_l8.setSize(80, 25);
        pg_l8.setForeground(l_c);
        pg_l8.setHorizontalAlignment(JLabel.LEFT);
        ressub_41.add(pg_l8);
        
        

        //------------------------------------------------------------------------
        tabbedPane.addTab("Case Definition"+"\u2713" , null, mainPanel1, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.addTab("Top"+"|"+"Down"+"\u2193" , null, mainPanel2, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);
        tabbedPane.addTab("Bottom"+"|"+"Up"+"\u2191" , null, subtabPane31, null);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_3);
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int ACTIVE_TAB = tabbedPane.getSelectedIndex();
		                if (ACTIVE_TAB == 0){

		                } else if (ACTIVE_TAB == 1) {
		                	UPDATE_TopDown();
		                } else if (ACTIVE_TAB == 2) {
		                	UPDATE_BottomUp();
		            	}
        }                
        });
       
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        TabPanel.add(tabbedPane);
       // tabbedPane.addTab("3|DOF" , null, mainPanel4, null);
       // tabbedPane.setMnemonicAt(0, KeyEvent.VK_4);
       // MainGUI.setSize(x_init,y_init);
        
        JScrollPane scroll = new JScrollPane(MainGUI, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(x_init, y_init));
    	
        TOP_GUI.add(scroll);
        
        TOP_GUI.setOpaque(true);
        return TOP_GUI;
    }
    
    
    public void CHANGE_SELECT_TABLE2(){
    	if (model2.getRowCount()>0){
    		boolean select = false; 
    		if (deltav_cb_1.isSelected()==true){
    			select = true;
    		} else {
    			select = false; 
    		}
    	for (int i = 0; i < model2.getRowCount(); i++) {
    		model2.setValueAt(select, i,9 );
    	}
    	}
    }
    
    public void CHANGE_SELECT_TABLE3(){
    	if (model3.getRowCount()>0){
    		boolean select = false; 
    		if (pl_cb_1.isSelected()==true){
    			select = true;
    		} else {
    			select = false; 
    		}
    	for (int i = 0; i < model3.getRowCount(); i++) {
    		model3.setValueAt(select, i, 9);
    	}
    	}
    }
    
    public void CHANGE_SELECT_TABLE5(){
    	if (model5.getRowCount()>0){
    		boolean select = false; 
    		if (sc_cb_1.isSelected()==true){
    			select = true;
    		} else {
    			select = false; 
    		}
    	for (int i = 0; i < model5.getRowCount(); i++) {
    		model5.setValueAt(select, i, 9);
    	}
    	}
    }
    
    public void EngineeringModel_window(){
		//---------------------------------------------------------------------------------------
		JFrame.setDefaultLookAndFeelDecorated(false);
        EngineeringModel_frame = new JFrame("Engineering Model");
//-----------------------------------------------------------------------------------------------
        //JTextArea textArea = new JTextArea();
        //textArea.setSize(900-20,600-20);
        //testArea.setLocation(5,5);
        JPanel JP_EnginModel = new JPanel();
        JP_EnginModel.setPreferredSize(new Dimension(900,600));
        JP_EnginModel.setLocation(0, 30);
        
        
        JButton interim_button = new JButton("Show Engineering Model");
        interim_button.setLocation(0,0);
        interim_button.setSize(130, 25);
        interim_button.addActionListener(new ActionListener() {
         	 public void actionPerformed(ActionEvent e) {
         		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         		Date date = new Date();
         		System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
             	for (int i = 0; i < PAYLOAD_LIST.size(); i++) {
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "ID: " + PAYLOAD_LIST.get(i).get_ID());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "Name: " + PAYLOAD_LIST.get(i).get_name());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "Quantity: " + PAYLOAD_LIST.get(i).get_quantity());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "Basic mass: " + PAYLOAD_LIST.get(i).getmass_basic());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "MGA: " + PAYLOAD_LIST.get(i).getmass_MGA());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "MER: " + PAYLOAD_LIST.get(i).getmass_MER());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|Payload|" + "PMR: " + PAYLOAD_LIST.get(i).getmass_PMR());
            		System.out.println("---------------------------------------------------------------------------");
            	}
            	
            	for (int i = 0; i < SC_LIST.size(); i++) {
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "ID: " + SC_LIST.get(i).get_ID());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "Name: " + SC_LIST.get(i).get_name());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "Quantity: " + SC_LIST.get(i).get_quantity());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "Basic mass: " + SC_LIST.get(i).getmass_basic());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "MGA: " + SC_LIST.get(i).getmass_MGA());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "MER: " + SC_LIST.get(i).getmass_MER());
            		System.out.println("" + myfile_name.split("[.]")[0] + "|S/C|" + "PMR: " + SC_LIST.get(i).getmass_PMR());
            		System.out.println("---------------------------------------------------------------------------");
            	}
         	 }
                    });
        JP_EnginModel.add(interim_button);
        
        taOutputStream = null; 
        taOutputStream = new TextAreaOutputStream(textArea, ""); 
        JScrollPane JSP_EnginModel = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JSP_EnginModel.setPreferredSize(new Dimension(900-10,600-45));
        JSP_EnginModel.setLocation(5, 35);
        JP_EnginModel.add(JSP_EnginModel);
        System.setOut(new PrintStream(taOutputStream));
/*
        int timerDelay = 1000;
        new Timer(timerDelay , new ActionListener() {
           int count = 0;
           @Override
           public void actionPerformed(ActionEvent arg0) {

              // though this outputs via System.out.println, it actually displays
              // in the JTextArea:
              System.out.println("Count is now: " + count + " seconds");
              count++;
           }
           
        }).start();*/
//-----------------------------------------------------------------------------------------------
        BufferedImage myImage;
		try {
			myImage = ImageIO.read(url_smalllogo);
			EngineeringModel_frame.setIconImage(myImage);
		} catch (IOException eSFGGGERE) {
			// TODO Auto-generated catch block
			eSFGGGERE.printStackTrace();
			System.out.println("Image not found");
		}
		
		EngineeringModel_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		EngineeringModel_frame.setContentPane(JP_EnginModel);
		EngineeringModel_frame.pack();
		EngineeringModel_frame.setVisible(true);
		EngineeringModel_frame.setLocationRelativeTo(null);
		//---------------------------------------------------------------------------------------
    } 
    
    
    public void CreateChart_01() {
    	XYSeries xyseries = new XYSeries("TankHistory", false, false); 
        Double var_x = (double) -2, var_y = (double) -2 ; 
       // double var_y_po = -1 ; 
        double var_x_po = -1 ; 
        double x_int=-1;
    	int rowCount = model.getRowCount();
    	for (int i=0;i<rowCount-1;i++) {
    		var_x = Double.parseDouble ((String) model.getValueAt(i, 6));
    		var_x_po = Double.parseDouble ((String) model.getValueAt(i+1, 6));

    		if ( var_x != x_int && var_x != var_x_po) {
    		var_y = Double.parseDouble ((String) model.getValueAt(i, 12)); 
			 xyseries.add(var_x, var_y); 
			 xyseries.add(var_x_po-0.02, var_y);
			 }
    		x_int =  var_x;
    		}

    	if (rowCount > 0 ){
    	var_x = Double.parseDouble ((String) model.getValueAt(rowCount-1, 6));
		var_y = Double.parseDouble ((String) model.getValueAt(rowCount-1, 12));
		xyseries.add(var_x+0.001, var_y); } 

        result1.addSeries(xyseries); 
        //-----------------------------------------------------------------------------------
		chart = ChartFactory.createXYLineChart("", "Time [days]", "Tank content [kg] ", result1, PlotOrientation.VERTICAL, false, false, false); 
        XYPlot xyplot = (XYPlot)chart.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
	
		chart.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(new Color(238,238,238));
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		plot.setBackgroundPaint(Color.white);
		//ValueAxis domain = xyplot.getDomainAxis();
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		//rangeAxis.setRange(-20, 90);
		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new java.awt.BorderLayout());
		//jPanel1.setLocation(0, 464);
		jPanel1.setLocation(0,0);
		jPanel1.setSize(extx_21, exty_21-50);
		plotsub_21.add(jPanel1);
		ChartPanel CP = new ChartPanel(chart);
		
		jPanel1.add(CP,BorderLayout.CENTER);
		jPanel1.validate();
		    }
    
    public void CreateChart_02() {
    	XYSeries xyseries = new XYSeries("SCHistory", false, false); 
        Double var_x = (double) -2, var_y = (double) -2; 
        double var_x_po = -1 ; 
        double x_int=-1;
    	int rowCount = model.getRowCount();
    	for (int i=0;i<rowCount-1;i++) {
    		var_x = Double.parseDouble ((String) model.getValueAt(i, 6));
    		var_x_po = Double.parseDouble ((String) model.getValueAt(i+1, 6));

    		if ( var_x != x_int && var_x != var_x_po) {
    		var_y = Double.parseDouble ((String) model.getValueAt(i, 10)); 
			 xyseries.add(var_x, var_y); 
			 xyseries.add(var_x_po-0.02, var_y);
			 }
    		x_int =  var_x;
        	//System.out.println(i + " | " + var_x + " - " + var_y );
    		}

    	if (rowCount > 0 ){
    	var_x = Double.parseDouble ((String) model.getValueAt(rowCount-1, 6));
		var_y = Double.parseDouble ((String) model.getValueAt(rowCount-1, 11));
		xyseries.add(var_x+0.001, var_y); } 
    	result2.addSeries(xyseries); 
        //-----------------------------------------------------------------------------------
		chart2 = ChartFactory.createXYLineChart("", "Time [days]", "S/C mass [kg] ", result2, PlotOrientation.VERTICAL, false, false, false);  
        XYPlot xyplot = (XYPlot)chart2.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
	
		chart2.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
final XYPlot plot = (XYPlot) chart2.getPlot();
plot.setForegroundAlpha(0.5f);
plot.setBackgroundPaint(new Color(238,238,238));
plot.setDomainGridlinePaint(new Color(220,220,220));
plot.setRangeGridlinePaint(new Color(220,220,220));
plot.setBackgroundPaint(Color.white);
final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
JPanel jPanel2 = new JPanel();
jPanel2.setLayout(new java.awt.BorderLayout());
jPanel2.setLocation(0,0);
jPanel2.setSize(extx_21, exty_21-20);
plotsub_22.add(jPanel2);
ChartPanel CP2 = new ChartPanel(chart2);

jPanel2.add(CP2,BorderLayout.CENTER);
jPanel2.validate();
    }
    
    public void CreateChart_03() {
    	result3.addValue((long) glob_m_dry, "Dry Mass", "");
    	result3.addValue((long) m_payload, "Payload Mass", "");
    	result3.addValue((long) glob_m_fuel, "Propellant Mass", "");
        //-----------------------------------------------------------------------------------
		chart3 = ChartFactory.createStackedBarChart("", "S/C", "Mass [kg] ", result3, PlotOrientation.VERTICAL, true, false, false); 

		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		chart3.setBackgroundPaint(Color.white);
		renderer.setSeriesPaint(0, Color.black);
		renderer.setSeriesPaint(1, Color.gray);
		renderer.setSeriesPaint(2, Color.blue);
		//renderer.setBarPainter(new StandardBarPainter());
		Font finalfont = new Font("Tahoma", Font.BOLD, 14);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelFont(finalfont);
		renderer.setBaseItemLabelPaint(Color.white);
		CategoryPlot plot = (CategoryPlot) chart3.getPlot();
	    plot.setRenderer(renderer);
	    plot.setBackgroundPaint(Color.white);
	
		JPanel jPanel3 = new JPanel();
		jPanel3.setLayout(new java.awt.BorderLayout());
		jPanel3.setLocation(5,0);
		jPanel3.setSize(extx_31-25, exty_31-50);
		plotsub_31.add(jPanel3);
		ChartPanel CP3 = new ChartPanel(chart3);
		//CP3.setSize(extx_31-30, exty_31-35);
		jPanel3.add(CP3,BorderLayout.CENTER);
		jPanel3.validate();
 }
   public DefaultTableXYDataset AddDataset_04(){
	//DefaultTableXYDataset  	result4 = new DefaultTableXYDataset();
   	XYSeries xyseries10 = new XYSeries("", false, false); 
    Double var_x = (double) 0, var_y = (double) 0 ; 
    m_dry = Double.parseDouble(cd_tf1.getText());
 	m_payload = Double.parseDouble(Mpayload.getText());
 	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
 	if (cbMenuItem_BO.isSelected()==true){
 		With_boiloff = true;
 	}
 	if (cbMenuItem_MAR.isSelected()==true){
 		With_DV_margin = true;
 	}
 	if (cbMenuItem_SL.isSelected()==true){
 		With_SteeringLosses = true;
 	}
	if (sc_cb4.isSelected() == true){
		wet_margin = true;
	}
    double int_per = 0 ; 						// Interim percentage of (given) dry mass 
	double[] m_dry_int = null ;                 // Interim dry mass 
	m_dry_int = new double[chart_resolution];
	double[] m_wet_int = null ;                 // Interim wet mass 
	m_wet_int = new double[chart_resolution];
	double[] PMF = null ;                 		// PMF 
	PMF = new double[chart_resolution];
	
    for(int i=0;i<chart_resolution;i++){
    	int_per = (b_u-b_l)/chart_resolution * i + b_l;
    	m_dry_int[i] = int_per * m_dry;
    	m_wet_int[i] = CalcBottomUp_Wet(m_dry_int[i],m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
    	//System.out.println(With_boiloff + " | " + With_DV_margin + " | " + With_SteeringLosses );
    	PMF[i] = (m_wet_int[i]-m_dry_int[i]-m_payload)/(m_wet_int[i]-m_payload);
    	var_x = PMF[i]*100;
    	var_y = m_wet_int[i];
    	xyseries10.add(var_x, var_y);
    }    
    result4.addSeries(xyseries10);   
    return result4;
   }
    public void CreateChart_04() {
    	result4.removeAllSeries();
    	result4 = AddDataset_04();
        //-----------------------------------------------------------------------------------
        chart4 = ChartFactory.createScatterPlot("", "PMF [%]", "Total wet mass [kg] ", result4, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot plot = (XYPlot)chart4.getXYPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        plot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );	
		chart4.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 	
		plot.getDomainAxis().setLabelFont(font3);
		plot.getRangeAxis().setLabelFont(font3);
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220)); 
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setInverted(true);
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,20);
		jPanel4.setSize(extx_41, exty_41-40);
		plotsub_41.add(jPanel4);
		CP4 = new ChartPanel(chart4);
		CP4.setMouseWheelEnabled(true);
		CP4.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                // ignore
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                Rectangle2D dataArea = BlueBook_main.CP4.getScreenDataArea();
                JFreeChart chart = event.getChart();
                XYPlot plot = (XYPlot) chart.getPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
                BlueBook_main.xCrosshair_4.setValue(x);
                BlueBook_main.yCrosshair_4.setValue(y);
            }
    });
        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        xCrosshair_4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_4.setLabelVisible(true);
        yCrosshair_4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_4.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair_4);
        crosshairOverlay.addRangeCrosshair(yCrosshair_4);
        CP4.addOverlay(crosshairOverlay);

		jPanel4.add(CP4,BorderLayout.CENTER);
		jPanel4.validate();	
		chart4_fd = false;
    }
    
    
    public void Chart4_ResetMarker() {
		chart4.getXYPlot().clearDomainMarkers();
		chart4.getXYPlot().clearRangeMarkers();
		    	if(cb_sub41.isSelected()){
		        m_dry = Double.parseDouble(cd_tf1.getText());
		     	m_payload = Double.parseDouble(Mpayload.getText());
		     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
		     	if (cbMenuItem_BO.isSelected()==true){
		     		With_boiloff = true;
		     	}
		     	if (cbMenuItem_MAR.isSelected()==true){
		     		With_DV_margin = true;
		     	}
		     	if (cbMenuItem_SL.isSelected()==true){
		     		With_SteeringLosses = true;
		     	}
		    	if (sc_cb4.isSelected() == true){
		    		wet_margin = true;
		    	}
		    	double mark_pfm_m = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, wet_margin);
		    	double mark_pmf = (mark_pfm_m-m_payload-m_dry)/(mark_pfm_m-m_payload)*100;
		    	double mark_minit = Double.parseDouble(Minit.getText());
		    	double mark_minit_pmf = ((mark_minit-m_payload-CalcTopDown_Dry(mark_minit,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses))/(mark_minit-m_payload))*100;

		    	marker1 = new ValueMarker(mark_minit);
		    	marker1.setOutlinePaint(Color.red);
		    	marker1.setPaint(Color.red);
		    	marker1.setStroke(new BasicStroke(1.0f));
		    	chart4.getXYPlot().addRangeMarker(marker1);
		    	
		    	//if (sc_cb4.isSelected() == false){
		    	marker3 = new ValueMarker(mark_minit_pmf);
		    	marker3.setOutlinePaint(Color.red);
		    	marker3.setPaint(Color.red);
		    	marker3.setStroke(new BasicStroke(1.0f));
		    	chart4.getXYPlot().addDomainMarker(marker3);
		    	//}

		    	marker2 = new ValueMarker(mark_pmf);
		    	marker2.setOutlinePaint(Color.blue);
		    	marker2.setPaint(Color.blue);
		    	marker2.setStroke(new BasicStroke(1.0f));
		    	chart4.getXYPlot().addDomainMarker(marker2);

		    	marker4 = new ValueMarker(mark_pfm_m);
		    	marker4.setOutlinePaint(Color.blue);
		    	marker4.setPaint(Color.blue);
		    	marker4.setStroke(new BasicStroke(1.0f));
		    	chart4.getXYPlot().addRangeMarker(marker4);
		    	}
    }
    
    public void AddDataset_05(){
    	result5.addValue((long) m_dry, "Dry Mass", "");
    	result5.addValue((long) m_payload, "Payload Mass", "");
    	result5.addValue((long) glob_m_fuel, "Propellant Mass", "");	
    }
    
    public void CreateChart_05() {
    	AddDataset_05();
        //-----------------------------------------------------------------------------------
		chart5 = ChartFactory.createStackedBarChart("", "", "Mass [kg] ", result5, PlotOrientation.HORIZONTAL, true, false, false); 
		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		chart5.setBackgroundPaint(Color.white);
		renderer.setSeriesPaint(0, Color.black);
		renderer.setSeriesPaint(1, Color.gray);
		renderer.setSeriesPaint(2, Color.blue);
		//renderer.setBarPainter(new StandardBarPainter());
		Font finalfont = new Font("Tahoma", Font.BOLD, 12);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelFont(finalfont);
		renderer.setBaseItemLabelPaint(Color.white);
		CategoryPlot plot = (CategoryPlot) chart5.getPlot();
	    plot.setRenderer(renderer);
		Font AxisLabelFont = new Font("Tahoma", Font.BOLD, 10);
	    plot.getDomainAxis().setLabelFont(AxisLabelFont);
	    plot.getRangeAxis().setLabelFont(AxisLabelFont);
	    plot.setBackgroundPaint(Color.white);
		JPanel jPanel3 = new JPanel();
		jPanel3.setLayout(new java.awt.BorderLayout());
		jPanel3.setLocation(0,0);
		jPanel3.setSize(extx_42-30, exty_42-50);
		plotsub_421.add(jPanel3);
		ChartPanel CP3 = new ChartPanel(chart5);
		jPanel3.add(CP3,BorderLayout.CENTER);
		jPanel3.validate();
		chart5_fd=false;
 }
    public void Chart5_ResetMarker(){
		chart5.getCategoryPlot().clearDomainMarkers();
		chart5.getCategoryPlot().clearRangeMarkers();
		Marker marker = new ValueMarker(m_init);
		marker.setOutlinePaint(Color.red);
		marker.setPaint(Color.red);
		marker.setStroke(new BasicStroke(3.0f));
		chart5.getCategoryPlot().addRangeMarker(marker, Layer.FOREGROUND);
    }
    
  public void AddDataset_06(){
  	XYSeries xyseries10 = new XYSeries("", false, false); 
    Double var_x = (double) 0, var_y = (double) 0 ; 
    m_dry = Double.parseDouble(cd_tf1.getText());
 	m_payload = Double.parseDouble(Mpayload.getText());
 	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin=false ;
 	if (cbMenuItem_BO.isSelected()==true){
 		With_boiloff = true;
 	}
 	if (cbMenuItem_MAR.isSelected()==true){
 		With_DV_margin = true;
 	}
 	if (cbMenuItem_SL.isSelected()==true){
 		With_SteeringLosses = true;
 	}
	if (sc_cb4.isSelected() == true){
		wet_margin = true;
	}
    double int_per = 0 ; 						// Interim percentage of (given) dry mass 
	double[] m_dry_int = null ;                 // Interim dry mass 
	m_dry_int = new double[chart_resolution];
	double[] m_wet_int = null ;                 // Interim wet mass 
	m_wet_int = new double[chart_resolution];
    for(int i=0;i<chart_resolution;i++){
    	int_per = (b_u-b_l)/chart_resolution * i + b_l;
    	m_dry_int[i] = int_per * m_dry;
    	m_wet_int[i] = CalcBottomUp_Wet(m_dry_int[i], m_payload,With_DV_margin, With_boiloff, With_SteeringLosses, wet_margin);
    	var_x = m_dry_int[i];
    	var_y = m_wet_int[i];
    	xyseries10.add(var_x, var_y);
    }
    result6.addSeries(xyseries10); 
  }
    public void CreateChart_06() {
    	AddDataset_06();
        //-----------------------------------------------------------------------------------
		//chart4 = ChartFactory.createXYLineChart("", "Time [days]", "S/C mass [kg] ", dataset2, PlotOrientation.VERTICAL, false, false, false);  
		chart6 = ChartFactory.createScatterPlot("", "Dry Mass [kg]", "Total wet mass [kg] ", result6, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart6.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
	
		chart6.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);

		
final XYPlot plot = (XYPlot) chart6.getPlot();
plot.setForegroundAlpha(0.5f);
plot.setBackgroundPaint(new Color(238,238,238));
plot.setDomainGridlinePaint(new Color(220,220,220));
plot.setRangeGridlinePaint(new Color(220,220,220));
plot.setBackgroundPaint(Color.white);
final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
JPanel jPanel4 = new JPanel();
jPanel4.setLayout(new java.awt.BorderLayout());
jPanel4.setLocation(0,20);
jPanel4.setSize(extx_41, exty_41-40);
plotsub_42.add(jPanel4);
CP6 = new ChartPanel(chart6);
CP6.setMouseWheelEnabled(true);
CP6.addChartMouseListener(new ChartMouseListener() {
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = BlueBook_main.CP6.getScreenDataArea();
        JFreeChart chart = event.getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        BlueBook_main.xCrosshair_6.setValue(x);
        BlueBook_main.yCrosshair_6.setValue(y);
    }
});
CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
xCrosshair_6 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
xCrosshair_6.setLabelVisible(true);
yCrosshair_6 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
yCrosshair_6.setLabelVisible(true);
crosshairOverlay.addDomainCrosshair(xCrosshair_6);
crosshairOverlay.addRangeCrosshair(yCrosshair_6);
CP6.addOverlay(crosshairOverlay);

jPanel4.add(CP6,BorderLayout.CENTER);
jPanel4.validate();
chart6_fd=false;
    }
    
    public void Chart6_ResetMarker() {
		chart6.getXYPlot().clearDomainMarkers();
		chart6.getXYPlot().clearRangeMarkers();
    	XYPlot plot = (XYPlot) chart6.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}

    	double mark_minit = Double.parseDouble(Minit.getText());
    	double mark_minit_dry = CalcTopDown_Dry(mark_minit,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses);
    	double mark_dry = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
    	Marker marker = new ValueMarker(mark_minit);
    	marker.setOutlinePaint(Color.red);
    	marker.setPaint(Color.red);
    	marker.setStroke(new BasicStroke(1.0f));
    	plot.addRangeMarker(marker, Layer.FOREGROUND);
    	if (sc_cb4.isSelected() == false){
    	Marker marker4 = new ValueMarker(mark_minit_dry);
    	marker4.setOutlinePaint(Color.red);
    	marker4.setPaint(Color.red);
    	marker4.setStroke(new BasicStroke(1.0f));
    	plot.addDomainMarker(marker4, Layer.FOREGROUND);
    	}

    	Marker marker2 = new ValueMarker(m_dry);
    	marker2.setOutlinePaint(Color.blue);
    	marker2.setPaint(Color.blue);
    	marker2.setStroke(new BasicStroke(1.0f));
    	plot.addDomainMarker(marker2, Layer.FOREGROUND);
    	Marker marker3 = new ValueMarker(mark_dry);
    	marker3.setOutlinePaint(Color.blue);
    	marker3.setPaint(Color.blue);
    	marker3.setStroke(new BasicStroke(1.0f));
    	plot.addRangeMarker(marker3, Layer.FOREGROUND);
        
    }
    
    public void AddDataset_07(){
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false, wet_margin = false  ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}

    	double m_sc_total_wm = 0;
    	for (int i = 0; i < model5.getRowCount(); i++) {
    		double mga =0, mer = 0 , pmr = 0;
    		double m_org = Double.parseDouble((String) model5.getValueAt(i, 1)) * Double.parseDouble((String) model5.getValueAt(i, 2));
    		if (sc_cb1.isSelected()){
    			mga = Double.parseDouble((String) model5.getValueAt(i, 3));
    		}
    		if (sc_cb2.isSelected()){
    			mer = Double.parseDouble((String) model5.getValueAt(i, 5));
    		}
    		if (sc_cb3.isSelected() && sc_cb53.isSelected() == false){
    			pmr = Double.parseDouble((String) model5.getValueAt(i, 7));
    		}
    		double m_wM = m_org + (m_org*mga/100) + (m_org*mer/100) + pmr ;
    		if ((Boolean) model5.getValueAt(i, 9) == true){
    			m_sc_total_wm = m_sc_total_wm + m_wM;
    		}
    	}
    	
    	double m_sc_total_wom = 0;
    	for (int i = 0; i < model5.getRowCount(); i++) {
    		if ((Boolean) model5.getValueAt(i, 9) == true){
    		m_sc_total_wom = m_sc_total_wom + (Double.parseDouble((String) model5.getValueAt(i, 1)) * Double.parseDouble((String) model5.getValueAt(i, 2)) );
    		}	
    	}
    	
    	double set1= m_payload; 
    	double set3= m_sc_total_wom;
    	double set4= m_sc_total_wm - m_sc_total_wom;
    	double set5= CalcBottomUp_Wet(m_sc_total_wom,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, false)- m_sc_total_wom - m_payload;
    	double set6= ( CalcBottomUp_Wet(m_sc_total_wm,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, false) - m_sc_total_wm - m_payload)  - (CalcBottomUp_Wet(m_sc_total_wom,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,false) - m_sc_total_wom - m_payload);
    	double set7= ( CalcBottomUp_Wet(m_sc_total_wm,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, wet_margin))  - (CalcBottomUp_Wet(m_sc_total_wm,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,false));

    	rp4_10.setText("" + df.format((set4)/(m_sc_total_wom)*100));
    	//System.out.println(set3 + " | " + set4 + " | " +set5 + " | " +set6 + " | ");
    	result7.addValue((long) set1, "Payload", "");
    	result7.addValue((long) set3, "Dry Mass w/o Margin", "");
    	result7.addValue((long) set4, "Dry Mass Margin", "");
    	result7.addValue((long) set5, "Propellant Mass w/o Margin", "");
    	result7.addValue((long) set6, "Propellant Mass Margin", "");
    	result7.addValue((long) set7, "MER wet", "");
    }
    
    public void CreateChart_07() {
    	AddDataset_07();
        //-----------------------------------------------------------------------------------
		chart7 = ChartFactory.createStackedBarChart("", "", "Mass [kg] ", result7, PlotOrientation.HORIZONTAL, true, false, false); 
		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		chart7.setBackgroundPaint(Color.white);
		renderer.setSeriesPaint(0, Color.orange);
		renderer.setSeriesPaint(1, new Color(32,32,32));
		renderer.setSeriesPaint(2, new Color(160,160,160));
		renderer.setSeriesPaint(3, new Color(0,0,153));
		renderer.setSeriesPaint(4, new Color(102,178,255));
		//renderer.setBarPainter(new StandardBarPainter());
		Font finalfont = new Font("Tahoma", Font.BOLD, 10);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelFont(finalfont);
		renderer.setBaseItemLabelPaint(Color.white);
		CategoryPlot plot = (CategoryPlot) chart7.getPlot();
	    plot.setRenderer(renderer);
		Font AxisLabelFont = new Font("Tahoma", Font.BOLD, 10);
	    plot.getDomainAxis().setLabelFont(AxisLabelFont);
	    plot.getRangeAxis().setLabelFont(AxisLabelFont);
	    plot.setBackgroundPaint(Color.white);
		
		JPanel jPanel7 = new JPanel();
		jPanel7.setLayout(new java.awt.BorderLayout());
		jPanel7.setLocation(0,0);
		jPanel7.setSize(extx_42-30, exty_42-50);
		plotsub_422.add(jPanel7);
		ChartPanel CP7 = new ChartPanel(chart7);
		
		jPanel7.add(CP7,BorderLayout.CENTER);
		jPanel7.validate();
		chart7_fd=false;
 }
    
    public void Chart7_ResetMarker() {
		chart7.getCategoryPlot().clearDomainMarkers();
		chart7.getCategoryPlot().clearRangeMarkers();
		
		Marker marker = new ValueMarker(m_init);
		marker.setOutlinePaint(Color.red);
		marker.setPaint(Color.red);
		marker.setStroke(new BasicStroke(3.0f));
		chart7.getCategoryPlot().addRangeMarker(marker, Layer.FOREGROUND);
		
    }
    public void AddDataset_08(){
    	XYSeries xyseries10 = new XYSeries("", false, false); 
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 						// Interim percentage of (given) dry mass 
    	double[] m_dry_int = null ;                 // Interim dry mass 
    	m_dry_int = new double[chart_resolution];
    	double[] m_wet_int = null ;                 // Interim wet mass 
    	m_wet_int = new double[chart_resolution];
    	double[] EPSILON = null ;                 		// PMF 
    	EPSILON = new double[chart_resolution];
    	
        for(int i=0;i<chart_resolution;i++){
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	m_dry_int[i] = int_per * m_dry;
        	m_wet_int[i] = CalcBottomUp_Wet(m_dry_int[i],m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
        	//System.out.println(With_boiloff + " | " + With_DV_margin + " | " + With_SteeringLosses );
        	EPSILON[i] = (m_dry_int[i])/(m_wet_int[i]-m_payload);
        	var_x = EPSILON[i]*100;
        	var_y = m_wet_int[i];
        	xyseries10.add(var_x, var_y);
        }
        
        result8.addSeries(xyseries10); 
    }
    public void CreateChart_08() {
  	  	AddDataset_08();
        //-----------------------------------------------------------------------------------
		//chart4 = ChartFactory.createXYLineChart("", "Time [days]", "S/C mass [kg] ", dataset2, PlotOrientation.VERTICAL, false, false, false);  
        chart8 = ChartFactory.createScatterPlot("", "Structural Coefficient [%]", "Total wet mass [kg] ", result8, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart8.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
	
		chart8.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart8.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,20);
		jPanel4.setSize(extx_41, exty_41-40);
		plotsub_43.add(jPanel4);
		CP8 = new ChartPanel(chart8);
		CP8.setMouseWheelEnabled(true);
		CP8.addChartMouseListener(new ChartMouseListener() {
		    @Override
		    public void chartMouseClicked(ChartMouseEvent event) {
		        // ignore
		    }
		
		    @Override
		    public void chartMouseMoved(ChartMouseEvent event) {
		        Rectangle2D dataArea = BlueBook_main.CP8.getScreenDataArea();
		        JFreeChart chart = event.getChart();
		        XYPlot plot = (XYPlot) chart.getPlot();
		        ValueAxis xAxis = plot.getDomainAxis();
		        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
		                RectangleEdge.BOTTOM);
		        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
		        BlueBook_main.xCrosshair_8.setValue(x);
		        BlueBook_main.yCrosshair_8.setValue(y);
		    }
		});
		CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		xCrosshair_8 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
		xCrosshair_8.setLabelVisible(true);
		yCrosshair_8 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
		yCrosshair_8.setLabelVisible(true);
		crosshairOverlay.addDomainCrosshair(xCrosshair_8);
		crosshairOverlay.addRangeCrosshair(yCrosshair_8);
		CP8.addOverlay(crosshairOverlay);

		jPanel4.add(CP8,BorderLayout.CENTER);
		jPanel4.validate();
		chart8_fd=false;
    }
    
    public void Chart8_ResetMarker() {
    	
		chart8.getXYPlot().clearDomainMarkers();
		chart8.getXYPlot().clearRangeMarkers();
    	XYPlot plot = (XYPlot) chart8.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
    	//double mark_pmf = Double.parseDouble(rp4_4.getText());
    	double mark_pfm_m = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
    	double mark_pmf = (m_dry)/(mark_pfm_m-m_payload)*100;
    	double mark_minit = Double.parseDouble(Minit.getText());
    	double mark_minit_pmf = ((CalcTopDown_Dry(mark_minit,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses))/(mark_minit-m_payload))*100;
    	Marker marker = new ValueMarker(mark_minit);
    	marker.setOutlinePaint(Color.red);
    	marker.setPaint(Color.red);
    	marker.setStroke(new BasicStroke(1.0f));
    	plot.addRangeMarker(marker, Layer.FOREGROUND);
    	if (sc_cb4.isSelected() == false){
    	Marker marker3 = new ValueMarker(mark_minit_pmf);
    	marker3.setOutlinePaint(Color.red);
    	marker3.setPaint(Color.red);
    	marker3.setStroke(new BasicStroke(1.0f));
    	plot.addDomainMarker(marker3, Layer.FOREGROUND);
    	}
    	Marker marker2 = new ValueMarker(mark_pmf);
    	marker2.setOutlinePaint(Color.blue);
    	marker2.setPaint(Color.blue);
    	marker2.setStroke(new BasicStroke(1.0f));
    	plot.addDomainMarker(marker2, Layer.FOREGROUND);
    	Marker marker4 = new ValueMarker(mark_pfm_m);
    	marker4.setOutlinePaint(Color.blue);
    	marker4.setPaint(Color.blue);
    	marker4.setStroke(new BasicStroke(1.0f));
    	plot.addRangeMarker(marker4, Layer.FOREGROUND);
        
    }
 
  public void AddDataset_09(){
  	XYSeries xyseries10 = new XYSeries("", false, false); 
    Double var_x = (double) 0, var_y = (double) 0 ; 
    m_dry = Double.parseDouble(cd_tf1.getText());
 	m_payload = Double.parseDouble(Mpayload.getText());
 	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
 	if (cbMenuItem_BO.isSelected()==true){
 		With_boiloff = true;
 	}
 	if (cbMenuItem_MAR.isSelected()==true){
 		With_DV_margin = true;
 	}
 	if (cbMenuItem_SL.isSelected()==true){
 		With_SteeringLosses = true;
 	}
	if (sc_cb4.isSelected() == true){
		wet_margin = true;
	}
    double int_per = 0 ; 						// Interim percentage of (given) dry mass 
	double[] m_dry_int = null ;                 // Interim dry mass 
	m_dry_int = new double[chart_resolution];
	double[] m_wet_int = null ;                 // Interim wet mass 
	m_wet_int = new double[chart_resolution];
	double[] EPSILON = null ;                 		// PMF 
	EPSILON = new double[chart_resolution];
	double[] LAMBDA = null ;                 		// PMF 
	LAMBDA = new double[chart_resolution];
    for(int i=0;i<chart_resolution;i++){
    	int_per = (b_u-b_l)/chart_resolution * i + b_l;
    	m_dry_int[i] = int_per * m_dry;
    	m_wet_int[i] = CalcBottomUp_Wet(m_dry_int[i],m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
    	//System.out.println(With_boiloff + " | " + With_DV_margin + " | " + With_SteeringLosses );
    	EPSILON[i] = (m_dry_int[i])/(m_wet_int[i]-m_payload);
    	LAMBDA[i] = m_payload/(m_wet_int[i]);
    	var_x = EPSILON[i]*100;
    	var_y = LAMBDA[i]*100;
    	xyseries10.add(var_x, var_y);
    }
    
    result9.addSeries(xyseries10); 
  }
    public void CreateChart_09() {
    	AddDataset_09();
        //-----------------------------------------------------------------------------------
		//chart4 = ChartFactory.createXYLineChart("", "Time [days]", "S/C mass [kg] ", dataset2, PlotOrientation.VERTICAL, false, false, false);  
        chart9 = ChartFactory.createScatterPlot("", "Structural Coefficient [%]", "Payload Ratio [%] ", result9, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart9.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
	
		chart9.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
final XYPlot plot = (XYPlot) chart9.getPlot();
plot.setForegroundAlpha(0.5f);
//plot.setBackgroundPaint(new Color(238,238,238));
plot.setBackgroundPaint(Color.white);
plot.setDomainGridlinePaint(new Color(220,220,220));
plot.setRangeGridlinePaint(new Color(220,220,220));
final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

JPanel jPanel4 = new JPanel();
jPanel4.setLayout(new java.awt.BorderLayout());
jPanel4.setLocation(0,20);
jPanel4.setSize(extx_41, exty_41-40);
plotsub_44.add(jPanel4);
CP9 = new ChartPanel(chart9);
CP9.setMouseWheelEnabled(true);
	CP9.addChartMouseListener(new ChartMouseListener() {
	    @Override
	    public void chartMouseClicked(ChartMouseEvent event) {
	        // ignore
	    }
	
	    @Override
	    public void chartMouseMoved(ChartMouseEvent event) {
	        Rectangle2D dataArea = BlueBook_main.CP9.getScreenDataArea();
	        JFreeChart chart = event.getChart();
	        XYPlot plot = (XYPlot) chart.getPlot();
	        ValueAxis xAxis = plot.getDomainAxis();
	        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
	                RectangleEdge.BOTTOM);
	        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
	        BlueBook_main.xCrosshair_9.setValue(x);
	        BlueBook_main.yCrosshair_9.setValue(y);
	    }
	});
	CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
	xCrosshair_9 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	xCrosshair_9.setLabelVisible(true);
	yCrosshair_9 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	yCrosshair_9.setLabelVisible(true);
	crosshairOverlay.addDomainCrosshair(xCrosshair_9);
	crosshairOverlay.addRangeCrosshair(yCrosshair_9);
	CP9.addOverlay(crosshairOverlay);

jPanel4.add(CP9,BorderLayout.CENTER);
jPanel4.validate();
chart9_fd=false;
    }  
    
    public void Chart9_ResetMarker() {
    	chart9.getXYPlot().clearDomainMarkers();
    	chart9.getXYPlot().clearRangeMarkers();
    	XYPlot plot = (XYPlot) chart9.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
    	//double mark_pmf = Double.parseDouble(rp4_4.getText());
    	double Is_x = (m_dry)/(CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin)-m_payload)*100;
    	double Is_y = (m_payload)/(CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin))*100;
    	double Target_x 	= CalcTopDown_Dry((Double.parseDouble(Minit.getText())),m_payload, With_DV_margin, With_boiloff, With_SteeringLosses)/(Double.parseDouble(Minit.getText())-m_payload)*100       ;
    	double Target_y 	= m_payload/(Double.parseDouble(Minit.getText()))*100;
    	Marker marker = new ValueMarker(Target_y);
    	marker.setOutlinePaint(Color.red);
    	marker.setPaint(Color.red);
    	marker.setStroke(new BasicStroke(1.0f));
    	plot.addRangeMarker(marker, Layer.FOREGROUND);
    	if (sc_cb4.isSelected() == false){
    	Marker marker3 = new ValueMarker(Target_x);
    	marker3.setOutlinePaint(Color.red);
    	marker3.setPaint(Color.red);
    	marker3.setStroke(new BasicStroke(1.0f));
    	plot.addDomainMarker(marker3, Layer.FOREGROUND);
    	}

    	Marker marker2 = new ValueMarker(Is_x);
    	marker2.setOutlinePaint(Color.blue);
    	marker2.setPaint(Color.blue);
    	marker2.setStroke(new BasicStroke(1.0f));
    	plot.addDomainMarker(marker2, Layer.FOREGROUND);
    	Marker marker4 = new ValueMarker(Is_y);
    	marker4.setOutlinePaint(Color.blue);
    	marker4.setPaint(Color.blue);
    	marker4.setStroke(new BasicStroke(1.0f));
    	plot.addRangeMarker(marker4, Layer.FOREGROUND);
        
    }
 public void AddDataset_10(){
 	XYSeries xyseries10 = new XYSeries("", false, false); 
    Double var_x = (double) 0, var_y = (double) 0 ; 
    m_dry = Double.parseDouble(cd_tf1.getText());
 	m_payload = Double.parseDouble(Mpayload.getText());
 	m_init = Double.parseDouble(Minit.getText());
 	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ;
 	if (cbMenuItem_BO.isSelected()==true){
 		With_boiloff = true;
 	}
 	if (cbMenuItem_MAR.isSelected()==true){
 		With_DV_margin = true;
 	}
 	if (cbMenuItem_SL.isSelected()==true){
 		With_SteeringLosses = true;
 	}
    double int_per = 0 ; 			// Interim percentage of (given) dry mass 
	double dry_f; 
	double m_pay_int =0 ; 
    for(int i=0;i<chart_resolution;i++){
    	int_per = (b_u-b_l)/chart_resolution * i + b_l;
    	m_pay_int = int_per * m_payload;
    	dry_f = CalcTopDown_Dry(m_init-global_pmr,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses);
    	var_x = dry_f;
    	var_y = m_pay_int;
    	xyseries10.add(var_x, var_y);
    }
    result10.addSeries(xyseries10); 
 }
    public void CreateChart_10() {
    	AddDataset_10();
        //-----------------------------------------------------------------------------------
        chart10 = ChartFactory.createScatterPlot("", "S/C Dry limit [kg]", "Payload mass [kg]", result10, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart10.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );

		chart10.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart10.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,20);
		jPanel4.setSize(extx_41, exty_41-40);
		plotsub_45.add(jPanel4);
		CP10 = new ChartPanel(chart10);
		CP10.setMouseWheelEnabled(true);
		CP10.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                // ignore
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                Rectangle2D dataArea = BlueBook_main.CP10.getScreenDataArea();
                JFreeChart chart = event.getChart();
                XYPlot plot = (XYPlot) chart.getPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
                BlueBook_main.xCrosshair_10.setValue(x);
                BlueBook_main.yCrosshair_10.setValue(y);
            }
    });
        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        xCrosshair_10 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_10.setLabelVisible(true);
        yCrosshair_10 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_10.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair_10);
        crosshairOverlay.addRangeCrosshair(yCrosshair_10);
        CP10.addOverlay(crosshairOverlay);
        
jPanel4.add(CP10,BorderLayout.CENTER);
jPanel4.validate();
chart10_fd=false;
    }
 
    public void Chart10_ResetMarker() {
    	chart10.getXYPlot().clearDomainMarkers();
    	chart10.getXYPlot().clearRangeMarkers();
    	if(cb_sub45.isSelected()){
    		XYPlot plot = (XYPlot) chart10.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}

		double Is_y = m_payload;
		double Is_x = CalcTopDown_Dry(m_init-global_pmr,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses);
		
		Marker marker2 = new ValueMarker(Is_x);
		marker2.setOutlinePaint(Color.blue);
		marker2.setPaint(Color.blue);
		marker2.setStroke(new BasicStroke(1.0f));
		plot.addDomainMarker(marker2, Layer.FOREGROUND);
		Marker marker4 = new ValueMarker(Is_y);
		marker4.setOutlinePaint(Color.blue);
		marker4.setPaint(Color.blue);
		marker4.setStroke(new BasicStroke(1.0f));
		plot.addRangeMarker(marker4, Layer.FOREGROUND);
    	}
    }
    public static void AddDataset_11(){	
    	XYSeries xyseries11 = new XYSeries("", false, false); 
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	m_init = Double.parseDouble(Minit.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
        double m_wet_int_orig = 0 ; 
        double m_pay_int = 0 ; 
	            for(int i=0;i<chart_resolution;i++){
	            	int_per = (b_u-b_l)/chart_resolution * i + b_l;
	            	m_pay_int = int_per * m_payload;
	        	m_wet_int_orig = CalcBottomUp_Wet(m_dry,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
	        	var_x = m_pay_int;
	        	var_y = m_wet_int_orig;
	        	xyseries11.add(var_x, var_y);
	            }
        result11.addSeries(xyseries11); 
    }
    public void CreateChart_11() {
    	AddDataset_11();
        //-----------------------------------------------------------------------------------
        chart11 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "Launch mass [kg]", result11, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart11.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        //renderer.setSeriesPaint( 1 , Color.gray);
		chart11.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart11.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		//rangeAxis.setRange(8400, 13000);
		//final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		//domainAxis.setInverted(true);
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,40);
		jPanel4.setSize(extx_41, exty_41-80);
		plotsub_46.add(jPanel4);
		CP11 = new ChartPanel(chart11);
		CP11.setMouseWheelEnabled(true);
		CP11.addChartMouseListener(new ChartMouseListener() {
	    @Override
	    public void chartMouseClicked(ChartMouseEvent event) {
	        // ignore
	    }
	
	    @Override
	    public void chartMouseMoved(ChartMouseEvent event) {
	        Rectangle2D dataArea = BlueBook_main.CP11.getScreenDataArea();
	        XYPlot plot = (XYPlot) chart11.getPlot();
	        ValueAxis xAxis = plot.getDomainAxis();
	        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
	                RectangleEdge.BOTTOM);
	        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
	        BlueBook_main.xCrosshair_11.setValue(x);
	        BlueBook_main.yCrosshair_11.setValue(y);
	    }
	});
	CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
	xCrosshair_11 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	xCrosshair_11.setLabelVisible(true);
	yCrosshair_11 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	yCrosshair_11.setLabelVisible(true);
	crosshairOverlay.addDomainCrosshair(xCrosshair_11);
	crosshairOverlay.addRangeCrosshair(yCrosshair_11);
	CP11.addOverlay(crosshairOverlay);
	
	jPanel4.add(CP11,BorderLayout.CENTER);
	jPanel4.validate();
	chart11_fd=false;
    }
    
    public void Chart11_ResetMarker() {
    	chart11.getXYPlot().clearDomainMarkers();
    	chart11.getXYPlot().clearRangeMarkers();
    	if(cb_sub46.isSelected()){
    		XYPlot plot = (XYPlot) chart11.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}

double Is_x = m_payload;
double Is_y = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
double Target_x 	= (CalcTopDown_Dry(m_init,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses)+m_payload)-m_dry ;
double Target_y 	= m_init;
Marker marker = new ValueMarker(Target_y);
marker.setOutlinePaint(Color.red);
marker.setPaint(Color.red);
marker.setStroke(new BasicStroke(1.0f));
plot.addRangeMarker(marker, Layer.FOREGROUND);
if (sc_cb4.isSelected() == false){
Marker marker3 = new ValueMarker(Target_x);
marker3.setOutlinePaint(Color.red);
marker3.setPaint(Color.red);
marker3.setStroke(new BasicStroke(1.0f));
plot.addDomainMarker(marker3, Layer.FOREGROUND);
}

Marker marker2 = new ValueMarker(Is_x);
marker2.setOutlinePaint(Color.blue);
marker2.setPaint(Color.blue);
marker2.setStroke(new BasicStroke(1.0f));
plot.addDomainMarker(marker2, Layer.FOREGROUND);
Marker marker4 = new ValueMarker(Is_y);
marker4.setOutlinePaint(Color.blue);
marker4.setPaint(Color.blue);
marker4.setStroke(new BasicStroke(1.0f));
plot.addRangeMarker(marker4, Layer.FOREGROUND);

        	
    	}
    }   
 
    public void AddDataset_12(){	
    	XYSeries xyseries10 = new XYSeries("", false, false);  
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
    	double m_wet_int = 0;
    	double m_dry_int =0 ; 
        for(int i=0;i<chart_resolution;i++){
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	m_dry_int = int_per * m_dry;
        	m_wet_int = CalcBottomUp_Wet(m_dry_int,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
        	var_x = m_dry_int;
        	var_y = m_wet_int-m_payload-m_dry_int;
        	xyseries10.add(var_x, var_y);
        }
        
        result12.addSeries(xyseries10); 
    }
    public void CreateChart_12() {
    	AddDataset_12(); 
        chart12 = ChartFactory.createScatterPlot("", "Dry mass [kg]", "Propellant mass [kg]", result12, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart12.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        renderer.setSeriesPaint( 1 , Color.gray);
		chart12.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart12.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		//rangeAxis.setRange(8400, 13000);
		//final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		//domainAxis.setInverted(true);
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,40);
		jPanel4.setSize(extx_41, exty_41-80);
		plotsub_47.add(jPanel4);
		CP12 = new ChartPanel(chart12);
		CP12.setMouseWheelEnabled(true);
		CP12.addChartMouseListener(new ChartMouseListener() {
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = BlueBook_main.CP11.getScreenDataArea();
        XYPlot plot = (XYPlot) chart12.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        BlueBook_main.xCrosshair_12.setValue(x);
        BlueBook_main.yCrosshair_12.setValue(y);
    }
});
CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
xCrosshair_12 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
xCrosshair_12.setLabelVisible(true);
yCrosshair_12 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
yCrosshair_12.setLabelVisible(true);
crosshairOverlay.addDomainCrosshair(xCrosshair_12);
crosshairOverlay.addRangeCrosshair(yCrosshair_12);
CP12.addOverlay(crosshairOverlay);

jPanel4.add(CP12,BorderLayout.CENTER);
jPanel4.validate();
chart12_fd=false;
    }
    
    public void Chart12_ResetMarker() {
    	chart12.getXYPlot().clearDomainMarkers();
    	chart12.getXYPlot().clearRangeMarkers();
    	if(cb_sub47.isSelected()){
    		XYPlot plot = (XYPlot) chart12.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}

		double Is_x = m_dry;
		double Is_y = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin)-m_dry-m_payload;
		
		Marker marker2 = new ValueMarker(Is_x);
		marker2.setOutlinePaint(Color.blue);
		marker2.setPaint(Color.blue);
		marker2.setStroke(new BasicStroke(1.0f));
		plot.addDomainMarker(marker2, Layer.FOREGROUND);
		Marker marker4 = new ValueMarker(Is_y);
		marker4.setOutlinePaint(Color.blue);
		marker4.setPaint(Color.blue);
		marker4.setStroke(new BasicStroke(1.0f));
		plot.addRangeMarker(marker4, Layer.FOREGROUND);

        	
    	}
    }   
    
    
    public static void AddDataset_13(){	
    	XYSeries xyseries10 = new XYSeries("", false, false);  
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
    	double m_wet_int = 0;
    	double m_dry_int =0 ; 
        for(int i=0;i<chart_resolution;i++){
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	m_dry_int = int_per * m_dry;
        	m_wet_int = CalcBottomUp_Wet(m_dry_int,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
        	var_x = m_dry_int;
        	var_y = m_wet_int-m_payload;
        	xyseries10.add(var_x, var_y);
        }
        
        result13.addSeries(xyseries10); 
    }
    public void CreateChart_13() {
    	AddDataset_13(); 
        chart13 = ChartFactory.createScatterPlot("", "Dry mass [kg]", "Wet mass [kg]", result13, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart13.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        renderer.setSeriesPaint( 1 , Color.gray);
		chart13.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart13.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		//rangeAxis.setRange(8400, 13000);
		//final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		//domainAxis.setInverted(true);
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,40);
		jPanel4.setSize(extx_41, exty_41-80);
		plotsub_48.add(jPanel4);
		CP13 = new ChartPanel(chart13);
		CP13.setMouseWheelEnabled(true);
		CP13.addChartMouseListener(new ChartMouseListener() {
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = BlueBook_main.CP13.getScreenDataArea();
        XYPlot plot = (XYPlot) chart13.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        BlueBook_main.xCrosshair_13.setValue(x);
        BlueBook_main.yCrosshair_13.setValue(y);
    }
	});
	CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
	xCrosshair_13 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	xCrosshair_13.setLabelVisible(true);
	yCrosshair_13 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	yCrosshair_13.setLabelVisible(true);
	crosshairOverlay.addDomainCrosshair(xCrosshair_13);
	crosshairOverlay.addRangeCrosshair(yCrosshair_13);
	CP13.addOverlay(crosshairOverlay);
	
	jPanel4.add(CP13,BorderLayout.CENTER);
	jPanel4.validate();
	chart13_fd=false;
    }
    
    public void Chart13_ResetMarker() {
    	chart13.getXYPlot().clearDomainMarkers();
    	chart13.getXYPlot().clearRangeMarkers();
    	if(cb_sub48.isSelected()){
    		XYPlot plot = (XYPlot) chart13.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}

		double Is_x = m_dry;
		double Is_y = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin)-m_payload;
		
		Marker marker2 = new ValueMarker(Is_x);
		marker2.setOutlinePaint(Color.blue);
		marker2.setPaint(Color.blue);
		marker2.setStroke(new BasicStroke(1.0f));
		plot.addDomainMarker(marker2, Layer.FOREGROUND);
		Marker marker4 = new ValueMarker(Is_y);
		marker4.setOutlinePaint(Color.blue);
		marker4.setPaint(Color.blue);
		marker4.setStroke(new BasicStroke(1.0f));
		plot.addRangeMarker(marker4, Layer.FOREGROUND); 	
    	}
    }  
    public static DefaultTableXYDataset AddDataset_041(){
    	//DefaultTableXYDataset  	result4 = new DefaultTableXYDataset();
       	XYSeries xyseries10 = new XYSeries("", false, false); 
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 						// Interim percentage of (given) dry mass 
    	double[] m_dry_int = null ;                 // Interim dry mass 
    	m_dry_int = new double[chart_resolution];
    	double[] m_wet_int = null ;                 // Interim wet mass 
    	m_wet_int = new double[chart_resolution];
    	double[] PMF = null ;                 		// PMF 
    	PMF = new double[chart_resolution];
    	
        for(int i=0;i<chart_resolution;i++){
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	m_dry_int[i] = int_per * m_dry;
        	m_wet_int[i] = CalcBottomUp_Wet(m_dry_int[i],m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
        	//System.out.println(With_boiloff + " | " + With_DV_margin + " | " + With_SteeringLosses );
        	PMF[i] = (m_wet_int[i]-m_dry_int[i]-m_payload)/(m_wet_int[i]-m_payload);
        	var_y = PMF[i]*100;
        	var_x = m_dry_int[i];
        	xyseries10.add(var_x, var_y);
        }    
        result41.addSeries(xyseries10);   
        return result41;
       }
    public static void CreateChart_A01() {
        //-----------------------------------------------------------------------------------
    	AddDataset_13();
        //-----------------------------------------------------------------------------------
    	chart131 = ChartFactory.createScatterPlot("", "S/C DRY mass [kg]", "S/C WET mass [kg]", result13, PlotOrientation.VERTICAL, false, false, false); 
        XYLineAndShapeRenderer renderer131 = new XYLineAndShapeRenderer( );
        renderer131.setSeriesPaint( 0 , Color.BLACK );
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 	
		chart131.getXYPlot().getDomainAxis().setLabelFont(font3);
		chart131.getXYPlot().getRangeAxis().setLabelFont(font3);
        chart131.getXYPlot().setRenderer(0, renderer131); 
		chart131.setBackgroundPaint(Color.white);
		chart131.getXYPlot().setForegroundAlpha(0.5f);
		chart131.getXYPlot().setBackgroundPaint(Color.white);
		chart131.getXYPlot().setDomainGridlinePaint(new Color(220,220,220));
		chart131.getXYPlot().setRangeGridlinePaint(new Color(220,220,220));
		chart131.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanel = new ChartPanel(chart131);
        chartPanel.setSize(580,300);
        chartPanel.setLocation(588, 5);
        mainPanel31.add(chartPanel);
        //-----------------------------------------------------------------------------------
    	AddDataset_041();
        //-----------------------------------------------------------------------------------
		//chart4 = ChartFactory.createXYLineChart("", "Time [days]", "S/C mass [kg] ", dataset2, PlotOrientation.VERTICAL, false, false, false);  
        chart41 = ChartFactory.createScatterPlot("", "S/C DRY mass [kg]", "PMF [%]", result41, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart41.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
		chart41.setBackgroundPaint(Color.white);		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart41.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanel2 = new ChartPanel(chart41);
        chartPanel2.setSize(590,300);
        chartPanel2.setLocation(2, 5);
        mainPanel31.add(chartPanel2);

        chartPanel2.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                // ignore
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                Rectangle2D dataArea = BlueBook_main.chartPanel2.getScreenDataArea();
                JFreeChart chart = event.getChart();
                XYPlot plot = (XYPlot) chart.getPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
                BlueBook_main.xCrosshair_A1.setValue(x);
                BlueBook_main.yCrosshair_A1.setValue(y);
                
                //===================================================
                double xx = xCrosshair_A1.getValue();
                double yy = DatasetUtilities.findYValue((chart131.getXYPlot()).getDataset(), 0, x);
                BlueBook_main.xCrosshair_A2.setValue(xx);
                BlueBook_main.yCrosshair_A2.setValue(yy);
            }
    });
        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        xCrosshair_A1 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A1.setLabelVisible(true);
        yCrosshair_A1 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A1.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair_A1);
        crosshairOverlay.addRangeCrosshair(yCrosshair_A1);
        chartPanel2.addOverlay(crosshairOverlay);
      //===================================================
        CrosshairOverlay crosshairOverlay2 = new CrosshairOverlay();
        xCrosshair_A2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A2.setLabelVisible(true);
        yCrosshair_A2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A2.setLabelVisible(true);
        crosshairOverlay2.addDomainCrosshair(xCrosshair_A2);
        crosshairOverlay2.addRangeCrosshair(yCrosshair_A2);
        chartPanel.addOverlay(crosshairOverlay2);
        
        chartA1_fd = false;
    }
    
    public static double LunarLander_PMF_FunctionOfVehicleMass(double vehicleWET){
    	double limit_PMF_max = 0.79; 
    	double limit_PMF_min = 0.51; 
    	double a = -277991.1; 
    	double b = 0.816703; 
    	double c = 0.000000004360789; 
    	//double d = 0.8387636; 
    	double m_REF = 15334;				// Apollo wet [kg]
		double x = vehicleWET/m_REF;
		double exp = Math.pow((x/c),b);
		double target_PMF_int = d + (a - d )/(1+exp);
		if (b_cap == true) {
		if ( target_PMF_int>limit_PMF_max) {
			return limit_PMF_max; 
		} else if ( target_PMF_int<limit_PMF_min) {
			return limit_PMF_min; 
		} else {
			return target_PMF_int;
		} } else {
			return target_PMF_int;
		}
    }
    public static void AddDataset_111(){
    	double target_PMF = Double.parseDouble(analysis_TF_01.getText())/100;
    	XYSeries xyseries10 = new XYSeries("Constant S/C mass performance (PMF " + df.format(target_PMF*100) + "%)", false, false); 
    	XYSeries xyseries10_2 = new XYSeries("Maximum S/C mass to meet total (WET) mass constraint", false, false);
    	XYSeries xyseries10_3 = new XYSeries("Variable S/C mass performance", false, false); 
    	XYSeries xyseries11 = new XYSeries("Constant S/C mass performance (PMF " + df.format(target_PMF*100) + "%)", false, false); 
    	XYSeries xyseries11_2 = new XYSeries("Constant S/C (DRY) mass", false, false); 
    	XYSeries xyseries11_3 = new XYSeries("Variable S/C mass performance)", false, false); 
    	XYSeries xyseries11_4 = new XYSeries("Predicted variable PMF [%])", false, false); 
        Double var_x = (double) 0, var_y = (double) 0 ; 
    	double dry_f; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	m_init = Double.parseDouble(Minit.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
    	//double dry_f = 1; 
    	double m_pay_int =0 ; 

		        for(int i=0;i<chart_resolution;i++){
		        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
		        	m_pay_int = int_per * m_payload;
		        	//--------------------------------------------------------------------------------------------------------------------------
		        	
		        	double m_wet_interim=0;
		        	double m_w_0=0;
		        	double m_w_00=0;
		        	double m_dry_interim=0;
		        	double target_PMF_int = target_PMF; 
		        	int k=0;
		            int l=0; 
		            if (a_complex == true) {
		        	m_wet_interim = CalcBottomUp_Wet(m_dry,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
		        	//m_w_0 = m_wet_interim;
		        		while (Math.sqrt((m_w_00-m_wet_interim)*(m_w_00-m_wet_interim))>Convergence_Criterea && l<500){
		        			m_w_00 = m_wet_interim;
		        		    m_w_0=0;
									        	while (Math.sqrt((m_w_0-m_wet_interim)*(m_w_0-m_wet_interim))>Convergence_Criterea && k<500){	
									        	m_w_0 = m_wet_interim;
									        	m_dry_interim = (m_wet_interim-m_pay_int) * (1 - target_PMF_int);
									        	m_wet_interim = CalcBottomUp_Wet(m_dry_interim,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
									        	k++;
									        	}       	
									    k=0;
									    m_w_0=0;
							    		target_PMF_int = LunarLander_PMF_FunctionOfVehicleMass(m_wet_interim);
							        	
									        	while (Math.sqrt((m_w_0-m_wet_interim)*(m_w_0-m_wet_interim))>Convergence_Criterea && k<500){	
									        	m_w_0 = m_wet_interim;
									        	m_dry_interim = (m_wet_interim-m_pay_int) * (1 - target_PMF_int);
									        	m_wet_interim = CalcBottomUp_Wet(m_dry_interim,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
									        	k++;
									        	}
		        		l++;
		        		} 	
		        		//System.out.println(l + " | " + (m_w_00 - m_wet_interim) + " | " + target_PMF_int + " | " + m_wet_interim);
					//---------------------------------------------------------------------------------------------------------------------------
				var_x = m_pay_int;
				var_y = m_wet_interim;
				xyseries11_3.add(var_x, var_y);
				
				var_x = m_pay_int;
				var_y = m_dry_interim;
				xyseries10_3.add(var_x, var_y);
				
				var_x = m_pay_int;
				var_y = target_PMF_int*100;
				xyseries11_4.add(var_x, var_y);
		            }
				//---------------------------------------------------------------------------------------------------------------------------
				m_wet_interim = CalcBottomUp_Wet(m_dry,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
	        	while (Math.sqrt((m_w_0-m_wet_interim)*(m_w_0-m_wet_interim))>Convergence_Criterea && k<500){	
	        	m_w_0 = m_wet_interim;
	        	m_dry_interim = (m_wet_interim-m_pay_int) * (1 - target_PMF);
	        	m_wet_interim = CalcBottomUp_Wet(m_dry_interim,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
	        	k++;
	        	}
	        	//System.out.println(k + " | " + target_PMF + " | " + m_wet_interim);
				var_x = m_pay_int;
				var_y = m_wet_interim;
				xyseries11.add(var_x, var_y);
				
				var_x = m_pay_int;
				var_y = m_dry_interim;
				xyseries10.add(var_x, var_y);
		        }
		      //---------------------------------------------------------------------------------------------------------------------------
		        double m_wet_int_orig = 0 ; 
			            for(int i=0;i<chart_resolution;i++){
			            	int_per = (b_u-b_l)/chart_resolution * i + b_l;
			            	m_pay_int = int_per * m_payload;
			        	m_wet_int_orig = CalcBottomUp_Wet(m_dry,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
			        	var_x = m_pay_int;
			        	var_y = m_wet_int_orig;
			        	xyseries11_2.add(var_x, var_y);
			            }
			            
			            for(int i=0;i<chart_resolution;i++){
			            	int_per = (b_u-b_l)/chart_resolution * i + b_l;
			            	m_pay_int = int_per * m_payload;
			            	dry_f = CalcTopDown_Dry(m_init-global_pmr,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses);
			            	var_y = dry_f;
			            	var_x = m_pay_int;
			            	xyseries10_2.add(var_x, var_y);
			            }
			            
		result101.addSeries(xyseries10);
		result101.addSeries(xyseries10_2);
            
        result11_A2.addSeries(xyseries11); 
        result11_A2.addSeries(xyseries11_2); 

        if (a_complex == true ){
        	result101.addSeries(xyseries10_3);	
            result11_A2.addSeries(xyseries11_3); 
            
            result11_A2_2.addSeries(xyseries11_4); 
        }
    }
    
    public static void Chart_A02_UPDATE_INFOBOARD(double md, double mpay, double mtw, double md2, double mtw2) {
    	double mw = mtw - mpay; 
    	double mp = mtw - mpay - md;
    	aib_md.setText("S/C dry mass [kg]: " + df.format(md));
    	aib_mw.setText("S/C wet mass [kg]: " + df.format(mw));
    	aib_mp.setText("S/C prop. mass [kg]: " + df.format(mp));
    	aib_mtw.setText("Launch mass [kg]: " + df.format(mtw));
    	aib_mpay.setText("Payload mass [kg]: " + df.format(mpay));
    	
    	aib_md2.setText("S/C dry mass [kg]: " + df.format(md2));
    	aib_mw2.setText("S/C wet mass [kg]: " + df.format(mtw2 - mpay));
    	aib_mp2.setText("S/C prop. mass [kg]: " + df.format( mtw2 - mpay - md2));
    	aib_mtw2.setText("Launch mass [kg]: " + df.format(mtw2));
    	aib_mpay2.setText("PMF [%]: " + df.format( (mtw2 - mpay - md2)/(mtw2 - mpay)*100)  );
    }
    public static void CreateChart_A02() {
        //-----------------------------------------------------------------------------------
    	//AddDataset_101();
        //-----------------------------------------------------------------------------------
    	chart101 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "System mass [kg]", result101, PlotOrientation.VERTICAL, true, false, false); 
        XYLineAndShapeRenderer renderer131 = new XYLineAndShapeRenderer( );
        renderer131.setSeriesPaint( 0 , Color.BLACK );
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 	
		chart101.getXYPlot().getDomainAxis().setLabelFont(font3);
		chart101.getXYPlot().getRangeAxis().setLabelFont(font3);
		chart101.getXYPlot().setRenderer(0, renderer131); 
		chart101.setBackgroundPaint(Color.white);
		chart101.getXYPlot().setForegroundAlpha(0.5f);
		chart101.getXYPlot().setBackgroundPaint(Color.white);
		chart101.getXYPlot().setDomainGridlinePaint(new Color(220,220,220));
		chart101.getXYPlot().setRangeGridlinePaint(new Color(220,220,220));
		chart101.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanel3 = new ChartPanel(chart101);
        chartPanel3.setSize(586,350);
        chartPanel3.setLocation(2, 300);
        mainPanel31.add(chartPanel3);
        //-----------------------------------------------------------------------------------
    	//AddDataset_111();
        //-----------------------------------------------------------------------------------
        chart111 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "Launch mass [kg]", result11_A2, PlotOrientation.VERTICAL, true, false, false); 
		XYPlot xyplot = (XYPlot)chart111.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        //renderer.setSeriesPaint( 1 , Color.gray);
		chart111.setBackgroundPaint(Color.white);		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart111.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
	    plot.setDataset(0, result11_A2);
	    plot.setDataset(1, result11_A2_2);
	    
	    XYLineAndShapeRenderer splinerenderer_1 = new XYLineAndShapeRenderer();
	    splinerenderer_1.setSeriesPaint(0, Color.BLACK);
	    //splinerenderer_1.setSeriesPaint(1, Color.RED);
	    //splinerenderer_1.setSeriesPaint(2, Color.GREEN);
	    //customize the plot with renderers and axis
	    plot.setRenderer(0, splinerenderer_1);//use default fill paint for first series
	    XYLineAndShapeRenderer splinerenderer_2 = new XYLineAndShapeRenderer();
	    splinerenderer_2.setSeriesPaint(0, Color.GREEN);
	    //splinerenderer_2.setSeriesPaint(1, Color.RED);
	    //splinerenderer_2.setSeriesPaint(2, Color.GREEN);
	    //splinerenderer_2.setSeriesPaint(3, Color.BLUE);
	    plot.setRenderer(1, splinerenderer_2);
		
		   plot.setRangeAxis(0, new NumberAxis("Launch mass [kg]"));
		   plot.setRangeAxis(1, new NumberAxis("PMF [%]"));
		   
		   plot.getRangeAxis(1).setRange(71, 78);
		   //plot.getRangeAxis(1).setAutoRange(true);
		   
		    //Map the data to the appropriate axis
		    plot.mapDatasetToRangeAxis(0, 0);
		    plot.mapDatasetToRangeAxis(1, 1); 
     
        chartPanel4 = new ChartPanel(chart111);
        chartPanel4.setSize(580,340);
        chartPanel4.setLocation(588, 300);
        mainPanel31.add(chartPanel4);

        chartPanel3.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                // Update inforboard
                Rectangle2D dataArea = BlueBook_main.chartPanel3.getScreenDataArea();
                //JFreeChart chart = event.getChart();
                XYPlot plot = chart101.getXYPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(chart101.getXYPlot().getDataset(), 0, x);
                BlueBook_main.xCrosshair_A3.setValue(x);
                BlueBook_main.yCrosshair_A3.setValue(y);
                
                //===================================================
                double xx = xCrosshair_A3.getValue();
                double yy = DatasetUtilities.findYValue((chart111.getXYPlot()).getDataset(), 0, x);
                BlueBook_main.xCrosshair_A4.setValue(xx);
                BlueBook_main.yCrosshair_A4.setValue(yy);
            	
            	//Chart_A02_UPDATE_INFOBOARD( y, x, yy);
            	
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                Rectangle2D dataArea = BlueBook_main.chartPanel3.getScreenDataArea();
                //JFreeChart chart = event.getChart();
                XYPlot plot = chart101.getXYPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(chart101.getXYPlot().getDataset(), 0, x);
                BlueBook_main.xCrosshair_A3.setValue(x);
                BlueBook_main.yCrosshair_A3.setValue(y);
                
                //===================================================
                double xx = xCrosshair_A3.getValue();
                double yy = DatasetUtilities.findYValue((chart111.getXYPlot()).getDataset(), 0, x);
                BlueBook_main.xCrosshair_A4.setValue(xx);
                BlueBook_main.yCrosshair_A4.setValue(yy);
            }
    });
        CrosshairOverlay crosshairOverlay3 = new CrosshairOverlay();
        xCrosshair_A3 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A3.setLabelVisible(true);
        yCrosshair_A3 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A3.setLabelVisible(true);
        crosshairOverlay3.addDomainCrosshair(xCrosshair_A3);
        crosshairOverlay3.addRangeCrosshair(yCrosshair_A3);
        chartPanel3.addOverlay(crosshairOverlay3);
      //===================================================
        CrosshairOverlay crosshairOverlay4 = new CrosshairOverlay();
        xCrosshair_A4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A4.setLabelVisible(true);
        yCrosshair_A4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A4.setLabelVisible(true);
        crosshairOverlay4.addDomainCrosshair(xCrosshair_A4);
        crosshairOverlay4.addRangeCrosshair(yCrosshair_A4);
        chartPanel4.addOverlay(crosshairOverlay4);       
        chartA2_fd = false;
    }
    //==========================================================================================================
    public void Convergence_window(){
		//---------------------------------------------------------------------------------------
		JFrame.setDefaultLookAndFeelDecorated(true);
        loading_frame = new JFrame("Convergence");
//-----------------------------------------------------------------------------------------------
        String y_name = "Result Value";
        String x_name = "Iteration [-]";
    	JFreeChart chart_Convergence = ChartFactory.createScatterPlot("", x_name, y_name, RS_convergence, PlotOrientation.VERTICAL, true, false, false); 
        XYLineAndShapeRenderer renderer131 = new XYLineAndShapeRenderer( );
        renderer131.setSeriesPaint( 0 , Color.BLACK );
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		renderer131.setSeriesPaint( 2 , Color.gray );
		chart_Convergence.getXYPlot().getDomainAxis().setLabelFont(font3);
		chart_Convergence.getXYPlot().getRangeAxis().setLabelFont(font3);
		chart_Convergence.getXYPlot().setRenderer(0, renderer131); 
		chart_Convergence.setBackgroundPaint(Color.white);
		chart_Convergence.getXYPlot().setForegroundAlpha(0.5f);
		chart_Convergence.getXYPlot().setBackgroundPaint(Color.white);
		chart_Convergence.getXYPlot().setDomainGridlinePaint(new Color(220,220,220));
		chart_Convergence.getXYPlot().setRangeGridlinePaint(new Color(220,220,220));
		//chart_Convergence.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		LogAxis logAxis = new LogAxis(y_name);
		logAxis.setBase(10);
		logAxis.setTickUnit(new NumberTickUnit(2));
		logAxis.setMinorTickMarksVisible(true);
		logAxis.setAutoRange(true);
		chart_Convergence.getXYPlot().setRangeAxis(logAxis);
		
		CP_Convergence = new ChartPanel(chart_Convergence);
		CP_Convergence.setPreferredSize(new Dimension(900,600));
		CP_Convergence.setLocation(0, 0);
		loading_frame.add(CP_Convergence);	
//-----------------------------------------------------------------------------------------------
        BufferedImage myImage;
		try {
			myImage = ImageIO.read(url_smalllogo);
			loading_frame.setIconImage(myImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loading_frame.pack();
		loading_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		loading_frame.setLocationRelativeTo(null);
        loading_frame.setContentPane(BlueBook_main.CP_Convergence);
		loading_frame.setVisible(true);
		//---------------------------------------------------------------------------------------
    } 
    //==========================================================================================================
    public static void AddDataset_A03(){
		aib_warning.setText("");
		RS_convergence.removeAllSeries();
    	double target_PMF = Double.parseDouble(analysis_TF_01.getText())/100;
    	XYSeries xyseriesA3_1 = new XYSeries("Constant S/C mass performance (PMF " + df.format(target_PMF*100) + "%)", false, false); 
    	XYSeries xyseriesA3_2 = new XYSeries("Maximum S/C mass to meet total (WET) mass constraint", false, false);
    	XYSeries xyseriesA3_3 = new XYSeries("Variable S/C mass performance", false, false); 
    	XYSeries xyseriesA3_4 = new XYSeries("Constant S/C mass performance (PMF " + df.format(target_PMF*100) + "%)", false, false); 
    	XYSeries xyseriesA3_5 = new XYSeries("Constant S/C (DRY) mass", false, false); 
    	XYSeries xyseriesA3_6 = new XYSeries("Variable S/C mass performance)", false, false); 
    	XYSeries xyseriesA3_7 = new XYSeries("Predicted shifted PMF [%]", false, false); 
    	
    	XYSeries xyseriesA3_8 = new XYSeries("Constant S/C (DRY) mass", false, false); 

        Double var_x = (double) 0, var_y = (double) 0 ; 
    	double dry_f; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	m_init = Double.parseDouble(Minit.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
    	double m_ref =  CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
    	double m_pay_int =0 ; 
    	double m_wet_interim=m_ref;
    	double m_w_0=0;
    	double m_w_01=0;
    	double m_w_00=0;
    	double[] res_history = null ;                 // Interim dry mass 
    	res_history = new double[100];
    	double m_i_01 = 0; 
    	double m_dry_interim=0;
    	double target_PMF_int = 0.6;
    	int inner_conv_limit = 1000; 
    	boolean inner_convergence = false; 
    	boolean calc_stop = false;
		        for(int i=0;i<chart_resolution;i++){
		        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
		        	m_pay_int = int_per * m_payload;
	        		//Write Update to Icon: 
	        		MAIN_frame.setTitle(df2.format((( (double) (i+1) )/chart_resolution)*100) + "% " + PROJECT_TITLE + " | " + myfile_name.split("[.]")[0]);
		        	//--------------------------------------------------------------------------------------------------------------------------
	        		XYSeries xyseriesA3_C = new XYSeries("Data point " + i , false, false); 
	        		int m=0; 
		            int l=0; 
		            calc_stop = false; 
		        	//m_wet_interim = 10; //CalcBottomUp_Wet((int_per * m_dry),m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
		            m_w_00 = 0 ; 
		        		while (( Math.sqrt((m_w_00-m_wet_interim)*(m_w_00-m_wet_interim))>Convergence_Criterea || (inner_convergence == false) ) &&  (l<100) && (calc_stop == false)){
		        			m_w_00 = m_wet_interim;
		        		    m_w_01=0;
		        		    m=0;
		        		    m_i_01 = 10;
				target_PMF_int = LunarLander_PMF_FunctionOfVehicleMass(m_wet_interim);
									        	while (Math.sqrt((m_w_01-m_i_01)*(m_w_01-m_i_01))>Convergence_Criterea && m<inner_conv_limit){	
									        	m_w_01 = m_i_01;
									        	m_dry_interim = (m_i_01-m_pay_int) * (1 - target_PMF_int);
									        	m_i_01 = CalcBottomUp_Wet(m_dry_interim,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
									        	m++;
									        	} 
									        		m_wet_interim = m_i_01; 
									        		if (m==1000 ){ //|| k==1000){
									        			inner_convergence = false;
									        		} else {
									        			inner_convergence = true; 
									        		}
									        		res_history[l] = m_wet_interim;
									        		// Function to protect against endless iteration for cases where calculation (constantly) oscillates between two solutions
									        		if (l > 2){
											        		if ( res_history[l] == res_history[l-2]) {
											        			calc_stop= true;
											        			aib_warning.setText("Warning: No Convergence");
											        			aib_warning.setForeground(Color.red);
											        		}
									        		}
						var_x = (double) l;
						var_y = m_wet_interim;
						xyseriesA3_C.add(var_x, var_y);
System.out.println(i + "-"+ l +  " | " + df.format(target_PMF_int*100) + " | " + df.format(m_wet_interim) + " | "  + m);
						l++;
		        		}
												var_x = m_pay_int;
												var_y = m_wet_interim;
												xyseriesA3_3.add(var_x, var_y);
												
												var_x = m_pay_int;
												var_y = m_dry_interim;
												xyseriesA3_6.add(var_x, var_y);
												
												var_x = m_pay_int;
												var_y = target_PMF_int*100;
												xyseriesA3_7.add(var_x, var_y);	
												
												RS_convergence.addSeries(xyseriesA3_C);
				//---------------------------------------------------------------------------------------------------------------------------
		        }
				for(int i=0;i<chart_resolution;i++){
		        int_per = (b_u-b_l)/chart_resolution * i + b_l;
		        m_pay_int = int_per * m_payload;
				int n = 0 ; 
				m_wet_interim = CalcBottomUp_Wet(m_dry,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
				        	while (Math.sqrt((m_w_0-m_wet_interim)*(m_w_0-m_wet_interim))>Convergence_Criterea && n<1000){	
				        	m_w_0 = m_wet_interim;
				        	m_dry_interim = (m_wet_interim-m_pay_int) * (1 - target_PMF);
				        	m_wet_interim = CalcBottomUp_Wet(m_dry_interim,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
				        	n++;
				        	}
				var_x = m_pay_int;
				var_y = m_dry;
				xyseriesA3_8.add(var_x, var_y);
	        	
				var_x = m_pay_int;
				var_y = m_wet_interim;
				xyseriesA3_1.add(var_x, var_y);
				
				var_x = m_pay_int;
				var_y = m_dry_interim;
				xyseriesA3_4.add(var_x, var_y);
		        }
		      //---------------------------------------------------------------------------------------------------------------------------
		        		double m_wet_int_orig = 0 ; 
			            for(int i=0;i<chart_resolution;i++){
			            	int_per = (b_u-b_l)/chart_resolution * i + b_l;
			            	m_pay_int = int_per * m_payload;
			        	m_wet_int_orig = CalcBottomUp_Wet(m_dry,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
			        	var_x = m_pay_int;
			        	var_y = m_wet_int_orig;
			        	xyseriesA3_5.add(var_x, var_y);
			            }
			            
			            for(int i=0;i<chart_resolution;i++){
			            	int_per = (b_u-b_l)/chart_resolution * i + b_l;
			            	m_pay_int = int_per * m_payload;
			            	dry_f = CalcTopDown_Dry(m_init-global_pmr,m_pay_int, With_DV_margin, With_boiloff, With_SteeringLosses);
			            	var_y = dry_f;
			            	var_x = m_pay_int;
			            	xyseriesA3_2.add(var_x, var_y);
			            }
			            
		result11_A3_1.addSeries(xyseriesA3_4);
		result11_A3_1.addSeries(xyseriesA3_8);
		result11_A3_1.addSeries(xyseriesA3_2);
            
		result11_A3_2.addSeries(xyseriesA3_1); 
		result11_A3_2.addSeries(xyseriesA3_5); 

		result11_A3_3.addSeries(xyseriesA3_6);
		result11_A3_3.addSeries(xyseriesA3_8);
		result11_A3_3.addSeries(xyseriesA3_2); 
            
        result11_A3_4.addSeries(xyseriesA3_3); 
        result11_A3_4.addSeries(xyseriesA3_5); 
        
        result11_A3_41.addSeries(xyseriesA3_7);
        
        MAIN_frame.setTitle("" + PROJECT_TITLE + " | " + myfile_name.split("[.]")[0]);
       
    }

    public static void CreateChart_A03() {
        //-----------------------------------------------------------------------------------
    	//AddDataset_101();
        //-----------------------------------------------------------------------------------
    	chartA3_1 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "System mass [kg]", result11_A3_1, PlotOrientation.VERTICAL, true, false, false); 
        XYLineAndShapeRenderer renderer131 = new XYLineAndShapeRenderer( );
        renderer131.setSeriesPaint( 0 , Color.BLACK );
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		renderer131.setSeriesPaint( 2 , Color.gray );
		chartA3_1.getXYPlot().getDomainAxis().setLabelFont(font3);
		chartA3_1.getXYPlot().getRangeAxis().setLabelFont(font3);
		chartA3_1.getXYPlot().setRenderer(0, renderer131); 
		chartA3_1.setBackgroundPaint(Color.white);
		chartA3_1.getXYPlot().setForegroundAlpha(0.5f);
		chartA3_1.getXYPlot().setBackgroundPaint(Color.white);
		chartA3_1.getXYPlot().setDomainGridlinePaint(new Color(220,220,220));
		chartA3_1.getXYPlot().setRangeGridlinePaint(new Color(220,220,220));
		chartA3_1.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		CP_A31 = new ChartPanel(chartA3_1);
		CP_A31.setSize(586,350);
		CP_A31.setLocation(2, 5);
		mainPanel32.add(CP_A31);
        //-----------------------------------------------------------------------------------
    	chartA3_2 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "Launch mass [kg]", result11_A3_2, PlotOrientation.VERTICAL, true, false, false); 
    	 XYLineAndShapeRenderer renderer132 = new XYLineAndShapeRenderer( );
    	renderer132.setSeriesPaint( 0 , Color.BLACK );	
		chartA3_2.getXYPlot().getDomainAxis().setLabelFont(font3);
		chartA3_2.getXYPlot().getRangeAxis().setLabelFont(font3);
		chartA3_2.getXYPlot().setRenderer(0, renderer132); 
		chartA3_2.setBackgroundPaint(Color.white);
		chartA3_2.getXYPlot().setForegroundAlpha(0.5f);
		chartA3_2.getXYPlot().setBackgroundPaint(Color.white);
		chartA3_2.getXYPlot().setDomainGridlinePaint(new Color(220,220,220));
		chartA3_2.getXYPlot().setRangeGridlinePaint(new Color(220,220,220));
		chartA3_2.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		CP_A32 = new ChartPanel(chartA3_2);
		CP_A32.setSize(736,350);
		CP_A32.setLocation(590, 5);
		mainPanel32.add(CP_A32);
        //-----------------------------------------------------------------------------------
    	chartA3_3 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "System mass [kg]", result11_A3_3, PlotOrientation.VERTICAL, true, false, false); 
    	 XYLineAndShapeRenderer renderer133 = new XYLineAndShapeRenderer( );
    	 renderer133.setSeriesPaint( 0 , Color.BLACK );
    	 renderer133.setSeriesPaint( 2 , Color.gray );
        chartA3_3.getXYPlot().getDomainAxis().setLabelFont(font3);
        chartA3_3.getXYPlot().getRangeAxis().setLabelFont(font3);
        chartA3_3.getXYPlot().setRenderer(0, renderer133); 
        chartA3_3.setBackgroundPaint(Color.white);
        chartA3_3.getXYPlot().setForegroundAlpha(0.5f);
        chartA3_3.getXYPlot().setBackgroundPaint(Color.white);
        chartA3_3.getXYPlot().setDomainGridlinePaint(new Color(220,220,220));
        chartA3_3.getXYPlot().setRangeGridlinePaint(new Color(220,220,220));
        chartA3_3.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        CP_A33 = new ChartPanel(chartA3_3);
        CP_A33.setSize(586,350);
        CP_A33.setLocation(2, 370);
        mainPanel32.add(CP_A33);
		//-----------------------------------------------------------------------------------
        chartA3_4 = ChartFactory.createScatterPlot("", "Payload mass [kg]", "Launch mass [kg]", result11_A3_4, PlotOrientation.VERTICAL, true, false, false); 
		XYPlot xyplot = (XYPlot)chartA3_4.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        //renderer.setSeriesPaint( 1 , Color.gray);
        chartA3_4.setBackgroundPaint(Color.white);		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chartA3_4.getPlot();
		plot.setForegroundAlpha(0.5f);
		//plot.setBackgroundPaint(new Color(238,238,238));
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
	    plot.setDataset(0, result11_A3_4);
	    plot.setDataset(1, result11_A3_41);
	    
	    XYLineAndShapeRenderer splinerenderer_1 = new XYLineAndShapeRenderer();
	    splinerenderer_1.setSeriesPaint(0, Color.BLACK);
	    //splinerenderer_1.setSeriesPaint(1, Color.RED);
	    //splinerenderer_1.setSeriesPaint(2, Color.GREEN);
	    //customize the plot with renderers and axis
	    plot.setRenderer(0, splinerenderer_1);//use default fill paint for first series
	    XYLineAndShapeRenderer splinerenderer_2 = new XYLineAndShapeRenderer();
	    splinerenderer_2.setSeriesPaint(0, Color.GREEN);
	    //splinerenderer_2.setSeriesPaint(1, Color.RED);
	    //splinerenderer_2.setSeriesPaint(2, Color.GREEN);
	    //splinerenderer_2.setSeriesPaint(3, Color.BLUE);
	    plot.setRenderer(1, splinerenderer_2);
		
		   plot.setRangeAxis(0, new NumberAxis("Launch mass [kg]"));
		   plot.setRangeAxis(1, new NumberAxis("PMF [%]"));
		   
		   
           double y2max = 80;
           double y2min = 70;
		   plot.getRangeAxis(1).setRange(y2min, y2max);
		   //plot.getRangeAxis(1).setAutoRange(true);
		   
		    //Map the data to the appropriate axis
		    plot.mapDatasetToRangeAxis(0, 0);
		    plot.mapDatasetToRangeAxis(1, 1); 

     
		    CP_A34 = new ChartPanel(chartA3_4);
		    CP_A34.setSize(780,360);
		    CP_A34.setLocation(588, 370);
        mainPanel32.add(CP_A34);

        CP_A31.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                // Update inforboard
                Rectangle2D dataArea = BlueBook_main.CP_A31.getScreenDataArea();
                //JFreeChart chart = event.getChart();
                XYPlot plot = chartA3_1.getXYPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(chartA3_1.getXYPlot().getDataset(), 0, x);
               // BlueBook_main.xCrosshair_A3_1.setValue(x);
               // BlueBook_main.yCrosshair_A3_1.setValue(y);
                //===================================================
                //double xx = xCrosshair_A3_1.getValue();
                double yy = DatasetUtilities.findYValue((chartA3_2.getXYPlot()).getDataset(), 0, x);
                //BlueBook_main.xCrosshair_A3_2.setValue(xx);
               // BlueBook_main.yCrosshair_A3_2.setValue(yy);
                //===================================================
                //double xxx = xCrosshair_A3_1.getValue();
                double yyy = DatasetUtilities.findYValue((chartA3_3.getXYPlot()).getDataset(), 0, x);
               // BlueBook_main.xCrosshair_A3_3.setValue(xxx);
               // BlueBook_main.yCrosshair_A3_3.setValue(yyy);
                //===================================================
                //double xxxx = xCrosshair_A3_1.getValue();
                double yyyy = DatasetUtilities.findYValue((chartA3_4.getXYPlot()).getDataset(), 0, x);
               // BlueBook_main.xCrosshair_A3_4.setValue(xxxx);
               // BlueBook_main.yCrosshair_A3_4.setValue(yyyy);
                //===================================================
            	
            	Chart_A02_UPDATE_INFOBOARD( y, x, yy, yyy, yyyy);
            	
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                Rectangle2D dataArea = BlueBook_main.CP_A31.getScreenDataArea();
                //JFreeChart chart = event.getChart();
                XYPlot plot = chartA3_1.getXYPlot();
                ValueAxis xAxis = plot.getDomainAxis();
                double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                        RectangleEdge.BOTTOM);
                double y = DatasetUtilities.findYValue(chartA3_1.getXYPlot().getDataset(), 0, x);
                BlueBook_main.xCrosshair_A3_1.setValue(x);
                BlueBook_main.yCrosshair_A3_1.setValue(y);
                //===================================================
                double xx = xCrosshair_A3_1.getValue();
                double yy = DatasetUtilities.findYValue((chartA3_2.getXYPlot()).getDataset(), 0, x);
                BlueBook_main.xCrosshair_A3_2.setValue(xx);
                BlueBook_main.yCrosshair_A3_2.setValue(yy);
                //===================================================
                double xxx = xCrosshair_A3_1.getValue();
                double yyy = DatasetUtilities.findYValue((chartA3_3.getXYPlot()).getDataset(), 0, x);
                BlueBook_main.xCrosshair_A3_3.setValue(xxx);
                BlueBook_main.yCrosshair_A3_3.setValue(yyy);
                //===================================================
                double xxxx = xCrosshair_A3_1.getValue();
                double yyyy = DatasetUtilities.findYValue((chartA3_4.getXYPlot()).getDataset(), 0, x);
                BlueBook_main.xCrosshair_A3_4.setValue(xxxx);
                BlueBook_main.yCrosshair_A3_4.setValue(yyyy);
                //===================================================
                //double xPMF = xCrosshair_A3_1.getValue();
                double yint = DatasetUtilities.findYValue((chartA3_4.getXYPlot()).getDataset(1), 0, x);
                double ymax = chartA3_4.getXYPlot().getRangeAxis(0).getUpperBound();
                double ymin = chartA3_4.getXYPlot().getRangeAxis(0).getLowerBound();
                double y_per = (yint-y2min) / (y2max - y2min);
                double yPMF = y_per * (ymax-ymin) + ymin; 
                BlueBook_main.xCrosshair_A3_4PMF.setValue(0);
                BlueBook_main.yCrosshair_A3_4PMF.setValue(yPMF);
            }
    });
        CrosshairOverlay crosshairOverlay3 = new CrosshairOverlay();
        xCrosshair_A3_1 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A3_1.setLabelVisible(true);
        yCrosshair_A3_1 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A3_1.setLabelVisible(true);
        crosshairOverlay3.addDomainCrosshair(xCrosshair_A3_1);
        crosshairOverlay3.addRangeCrosshair(yCrosshair_A3_1);
        CP_A31.addOverlay(crosshairOverlay3);
      //===================================================
        CrosshairOverlay crosshairOverlay4 = new CrosshairOverlay();
        xCrosshair_A3_2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A3_2.setLabelVisible(true);
        yCrosshair_A3_2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A3_2.setLabelVisible(true);
        crosshairOverlay4.addDomainCrosshair(xCrosshair_A3_2);
        crosshairOverlay4.addRangeCrosshair(yCrosshair_A3_2);
        CP_A32.addOverlay(crosshairOverlay4); 
        //===================================================
        CrosshairOverlay crosshairOverlay5 = new CrosshairOverlay();
        xCrosshair_A3_3 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A3_3.setLabelVisible(true);
        yCrosshair_A3_3 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A3_3.setLabelVisible(true);
        crosshairOverlay5.addDomainCrosshair(xCrosshair_A3_3);
        crosshairOverlay5.addRangeCrosshair(yCrosshair_A3_3);
        CP_A33.addOverlay(crosshairOverlay5); 
        //===================================================
        CrosshairOverlay crosshairOverlay6 = new CrosshairOverlay();
        xCrosshair_A3_4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair_A3_4.setLabelVisible(true);
        yCrosshair_A3_4 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair_A3_4.setLabelVisible(true);
        crosshairOverlay6.addDomainCrosshair(xCrosshair_A3_4);
        crosshairOverlay6.addRangeCrosshair(yCrosshair_A3_4);
        CP_A34.addOverlay(crosshairOverlay6); 
        //===================================================
        CrosshairOverlay crosshairOverlayPMF = new CrosshairOverlay();
        xCrosshair_A3_4PMF = new Crosshair(Double.NaN, Color.GREEN, new BasicStroke(0f));
        //xCrosshair_A3_4PMF.setLabelVisible(true);
        yCrosshair_A3_4PMF = new Crosshair(Double.NaN, Color.GREEN, new BasicStroke(0f));
        //yCrosshair_A3_4PMF.setLabelVisible(true);
        crosshairOverlayPMF.addDomainCrosshair(xCrosshair_A3_4PMF);
        crosshairOverlayPMF.addRangeCrosshair(yCrosshair_A3_4PMF);
        CP_A34.addOverlay(crosshairOverlayPMF); 
        
        chartA3_fd = false;
    }
    //==========================================================================================================
    
    //==========================================================================================================
    public static void AddDataset_14(){	
    	XYSeries xyseries10 = new XYSeries("", false, false);  
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ,wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
    	double m_wet_int = 0;
    	String n_name = null ;               
    	double n_isp = 0 ;  
    	double ISP_orig = 0 ; 
    	int n_en = model6.getRowCount();
    	String en_int = "ME";
    	for (int k = 0; k < n_en; k++) {
        	n_name = (String) table6.getModel().getValueAt(k, 0);
        	if (n_name.equals(en_int)) {
        		ISP_orig = Double.parseDouble((String) table6.getModel().getValueAt(k, 1));
        	}
    	}
           for(int i=0;i<chart_resolution;i++){       
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	n_isp = ISP_orig*int_per;
		        	for (int j = 0; j < model4.getRowCount(); j++) {
		        		n_name = (String) table2.getModel().getValueAt(j, 8);
				        	if (n_name.equals(en_int)) {
				        	table4.setValueAt("" + n_isp , j, 2);
				        	}
		        	}
		   m_wet_int = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
		   var_x = n_isp;
		   var_y = m_wet_int;
		   xyseries10.add(var_x, var_y);
           }
       	for (int j = 0; j < model4.getRowCount(); j++) {
    		n_name = (String) table2.getModel().getValueAt(j, 8);
	        	if (n_name.equals(en_int)) {
	        	table4.setValueAt("" + ISP_orig , j, 2);
	        	}
    	}
        result14.addSeries(xyseries10); 
    }
    public void CreateChart_14() {
    	AddDataset_14(); 
        chart14 = ChartFactory.createScatterPlot("", "Main Engine ISP [s]", "Wet mass [kg]", result14, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart14.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        renderer.setSeriesPaint( 1 , Color.gray);
		chart14.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart14.getPlot();
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,5);
		jPanel4.setSize(extx_41-45, exty_41-85);
		plotsub_P131.add(jPanel4);
		CP14 = new ChartPanel(chart14);
		CP14.setMouseWheelEnabled(true);
		CP14.addChartMouseListener(new ChartMouseListener() {
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = BlueBook_main.CP14.getScreenDataArea();
        XYPlot plot = (XYPlot) chart14.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        BlueBook_main.xCrosshair_14.setValue(x);
        BlueBook_main.yCrosshair_14.setValue(y);
    }
	});
	CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
	xCrosshair_14 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	xCrosshair_14.setLabelVisible(true);
	yCrosshair_14 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	yCrosshair_14.setLabelVisible(true);
	crosshairOverlay.addDomainCrosshair(xCrosshair_14);
	crosshairOverlay.addRangeCrosshair(yCrosshair_14);
	CP14.addOverlay(crosshairOverlay);
	
	jPanel4.add(CP14,BorderLayout.CENTER);
	jPanel4.validate();
	chart14_fd=false;
    }
    
    public void Chart14_ResetMarker() {
    	chart14.getXYPlot().clearDomainMarkers();
    	chart14.getXYPlot().clearRangeMarkers();
    		XYPlot plot = (XYPlot) chart14.getPlot();
        m_dry = Double.parseDouble(cd_tf1.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false , wet_margin= false;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
    	String n_name = null ;               
    	double ISP_orig = 0 ; 
    	int n_en = model6.getRowCount();
    	String en_int = "ME";
    	for (int k = 0; k < n_en; k++) {
        	n_name = (String) table6.getModel().getValueAt(k, 0);
        	if (n_name.equals(en_int)) {
        		ISP_orig = Double.parseDouble((String) table6.getModel().getValueAt(k, 1));
        	}
    	}
    	
		double Is_x = ISP_orig;
		double Is_y = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
		Marker marker2 = new ValueMarker(Is_x);
		marker2.setOutlinePaint(Color.blue);
		marker2.setPaint(Color.blue);
		marker2.setStroke(new BasicStroke(1.0f));
		plot.addDomainMarker(marker2, Layer.FOREGROUND);
		Marker marker4 = new ValueMarker(Is_y);
		marker4.setOutlinePaint(Color.blue);
		marker4.setPaint(Color.blue);
		marker4.setStroke(new BasicStroke(1.0f));
		plot.addRangeMarker(marker4, Layer.FOREGROUND); 		
    }  
    
    
    
    public static void AddDataset_15(){	
    	XYSeries xyseries10 = new XYSeries("", false, false);  
        Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
     	m_init = Double.parseDouble(Minit.getText());
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ;//wet_margin=false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	double m_dry_ref = CalcTopDown_Dry(m_init,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses);
        double int_per = 0 ; 			// Interim percentage of (given) dry mass 
    	double m_dry_int = 0 ; 
    	String n_name = null ;               
    	double n_isp = 0 ;  
    	double ISP_orig = 0 ; 
    	int n_en = model6.getRowCount();
    	String en_int = "ME";
    	for (int k = 0; k < n_en; k++) {
        	n_name = (String) table6.getModel().getValueAt(k, 0);
        	if (n_name.equals(en_int)) {
        		ISP_orig = Double.parseDouble((String) table6.getModel().getValueAt(k, 1));
        	}
    	}
           for(int i=0;i<chart_resolution;i++){       
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	n_isp = ISP_orig*int_per;
        	// Set ISP for ME to new interim value 
		        	for (int j = 0; j < model4.getRowCount(); j++) {
		        		n_name = (String) table2.getModel().getValueAt(j, 8);
				        	if (n_name.equals(en_int)) {
				        	table4.setValueAt("" + n_isp , j, 2);
				        	}
		        	}
		  // m_wet_int = CalcBottomUp_Wet(m_dry,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses,wet_margin);
		   m_dry_int = CalcTopDown_Dry(m_init,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses);
		   var_x = n_isp;
		   var_y = m_dry_int - m_dry_ref;
		   xyseries10.add(var_x, var_y);
           }
        // Reset ISP table to inital values:    
       	for (int j = 0; j < model4.getRowCount(); j++) {
    		n_name = (String) table2.getModel().getValueAt(j, 8);
	        	if (n_name.equals(en_int)) {
	        	table4.setValueAt("" + ISP_orig , j, 2);
	        	}
    	}
        result15.addSeries(xyseries10); 
    }
    
    public void CreateChart_15() {
    	AddDataset_15(); 
        chart15 = ChartFactory.createScatterPlot("", "Main Engine ISP [s]", "Delta S/C DRY mass [kg]", result15, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart15.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        renderer.setSeriesPaint( 1 , Color.gray);
		chart15.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart15.getPlot();
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,5);
		jPanel4.setSize(extx_41-45, exty_41-85);
		plotsub_P132.add(jPanel4);
		CP15 = new ChartPanel(chart15);
		CP15.setMouseWheelEnabled(true);
		CP15.addChartMouseListener(new ChartMouseListener() {
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = BlueBook_main.CP15.getScreenDataArea();
        XYPlot plot = (XYPlot) chart15.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        BlueBook_main.xCrosshair_15.setValue(x);
        BlueBook_main.yCrosshair_15.setValue(y);
    }
	});
	CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
	xCrosshair_15 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	xCrosshair_15.setLabelVisible(true);
	yCrosshair_15 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	yCrosshair_15.setLabelVisible(true);
	crosshairOverlay.addDomainCrosshair(xCrosshair_15);
	crosshairOverlay.addRangeCrosshair(yCrosshair_15);
	CP15.addOverlay(crosshairOverlay);
	
	jPanel4.add(CP15,BorderLayout.CENTER);
	jPanel4.validate();
	chart15_fd=false;
    }
    
    public void Chart15_ResetMarker() {
    	chart15.getXYPlot().clearDomainMarkers();
    	chart15.getXYPlot().clearRangeMarkers();
    	XYPlot plot = (XYPlot) chart15.getPlot();
    	String n_name = null ;               
    	double ISP_orig = 0 ; 
    	int n_en = model6.getRowCount();
    	String en_int = "ME";
    	for (int k = 0; k < n_en; k++) {
        	n_name = (String) table6.getModel().getValueAt(k, 0);
        	if (n_name.equals(en_int)) {
        		ISP_orig = Double.parseDouble((String) table6.getModel().getValueAt(k, 1));
        	}
    	}
		double Is_x = ISP_orig;
		double Is_y = 0;
		Marker marker2 = new ValueMarker(Is_x);
		marker2.setOutlinePaint(Color.blue);
		marker2.setPaint(Color.blue);
		marker2.setStroke(new BasicStroke(1.0f));
		plot.addDomainMarker(marker2, Layer.FOREGROUND);
		Marker marker4 = new ValueMarker(Is_y);
		marker4.setOutlinePaint(Color.blue);
		marker4.setPaint(Color.blue);
		marker4.setStroke(new BasicStroke(1.0f));
		plot.addRangeMarker(marker4, Layer.FOREGROUND); 		
    } 
    
    
    
    public static void AddDataset_16(){	
    	XYSeries xyseries10 = new XYSeries("", false, false); 
    	double int_PMF = 0;
    	double wet_perc = 0;
	    	for(int i=10;i<101;i++){
	    		wet_perc =  (((double) i)/100);
//System.out.println(wet_perc);
	    		int_PMF = LunarLander_PMF_FunctionOfVehicleMass(wet_perc * 15334);
	    		xyseries10.add(wet_perc, int_PMF*100);
	    	}
        result16.addSeries(xyseries10); 
    }
    
    public void CreateChart_16() {
    	AddDataset_16(); 
        chart16 = ChartFactory.createScatterPlot("", "Lander mass [-] * Dimensionless, ref. to Apollo total wet mass", "PMF [%]", result16, PlotOrientation.VERTICAL, false, false, false); 
		XYPlot xyplot = (XYPlot)chart16.getPlot(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        xyplot.setRenderer(0, renderer); 
        renderer.setSeriesPaint( 0 , Color.BLACK );
        renderer.setSeriesPaint( 1 , Color.gray);
		chart16.setBackgroundPaint(Color.white);
		Font font3 = new Font("Dialog", Font.PLAIN, 12); 
		
		xyplot.getDomainAxis().setLabelFont(font3);
		xyplot.getRangeAxis().setLabelFont(font3);
		
		final XYPlot plot = (XYPlot) chart16.getPlot();
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(new Color(220,220,220));
		plot.setRangeGridlinePaint(new Color(220,220,220));
		plot.getRangeAxis().setRange(50, 85);
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JPanel jPanel4 = new JPanel();
		jPanel4.setLayout(new java.awt.BorderLayout());
		jPanel4.setLocation(0,35);
		jPanel4.setSize(470, 350);
		mainPanelh5.add(jPanel4);
		CP16 = new ChartPanel(chart16);
		CP16.setMouseWheelEnabled(true);
		CP16.addChartMouseListener(new ChartMouseListener() {
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = BlueBook_main.CP16.getScreenDataArea();
        XYPlot plot = (XYPlot) chart16.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        BlueBook_main.xCrosshair_16.setValue(x);
        BlueBook_main.yCrosshair_16.setValue(y);
    }
	});
	CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
	xCrosshair_16 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	xCrosshair_16.setLabelVisible(true);
	yCrosshair_16 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
	yCrosshair_16.setLabelVisible(true);
	crosshairOverlay.addDomainCrosshair(xCrosshair_16);
	crosshairOverlay.addRangeCrosshair(yCrosshair_16);
	CP16.addOverlay(crosshairOverlay);
	
	jPanel4.add(CP16,BorderLayout.CENTER);
	jPanel4.validate();
	chart16_fd=false;
    }
    
 
 public void Set_D2W(){
  	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false, wet_margin=false  ;
  	if (cbMenuItem_BO.isSelected()==true){
  		With_boiloff = true;
  	}
  	if (cbMenuItem_MAR.isSelected()==true){
  		With_DV_margin = true;
  	}
  	if (cbMenuItem_SL.isSelected()==true){
  		With_SteeringLosses = true;
  	}
	if (sc_cb4.isSelected() == true){
		wet_margin = true;
	}
	 m_dry = Double.parseDouble(cd_tf1.getText());
	 double md_l = 0.8 * m_dry;
	 double md_u = 1.2 * m_dry;
	 double mw_l = CalcBottomUp_Wet(md_l,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, wet_margin);
	 double mw_u = CalcBottomUp_Wet(md_u,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, wet_margin);
	 double D2W = (mw_u - mw_l)/(md_u - md_l);
	 rp4_8.setText("" + df.format(D2W));
	 
 }
 
 public static double[] m_boiloff(int n_rows, double[] m_tank_sum, double[] t_days, double m_prop_total, double bo_rate, boolean td_switch) {
 	double[] m_bo= null ;                 // Boil of correction vector [kg]
 	m_bo = new double[n_rows];
 	for(int i =0 ; i < n_rows; i++){
		if (i>0){
						if (td_switch == false ){
						m_bo[i]= m_tank_sum[i] * (  Math.pow(((bo_rate+100)/100), (t_days[i]-t_days[i-1])) - 1);
						} else {
						m_bo[i]= m_tank_sum[i-1] * (  Math.pow(((bo_rate+100)/100), (t_days[i]-t_days[i-1])) - 1);	
						}
		//	System.out.println(df.format(m_tank_sum[i]) + " | " + (t_days[i]-t_days[i-1]) + " | " +  df.format(m_bo[i]));
		} else { 
		m_bo[i]= m_prop_total * (t_days[i])*bo_rate/100;
		}
	}	
 	return m_bo;
	}
 
    public void CalcCascade_TD(){
    	int n_rows = model.getRowCount(); 
    	m_init = Double.parseDouble(Minit.getText()) - global_pmr;
    	m_dry = Double.parseDouble(cd_tf1.getText());
    	rp2_6.setText("" + m_init);
    	m_payload = Double.parseDouble(Mpayload.getText());
    	rp2_2.setText("" + m_payload);
    	Res3.setText("" + df.format(m_dry));
    	m_payload = Double.parseDouble(Mpayload.getText());
    	double bo_rate = Double.parseDouble(BoilOff_TF.getText());
    	margin_prop = Double.parseDouble(FuelMar_TF.getText());
    	double m_prop_total=0;
    	double[] isp = null;
    	isp = new double[n_rows];
    	double[] add_loss = null ; 
    	add_loss = new double[n_rows];
    	double[] m_prop_burn = null ;             // Prop mass for alloc. burn
    	m_prop_burn = new double[n_rows];
    	double[] m_prop_int = null ; 			 // Prop mass for alloc. burn plus losses
    	m_prop_int = new double[n_rows];
    	double[] m_tank_sum = null ; 
    	m_tank_sum = new double[n_rows];
    	double[] m_bo_corr = null ;              // Boil-off correction
    	m_bo_corr = new double[n_rows];
    	double[] m_SC_c = null ;                 // Current spacecraft mass 
    	m_SC_c = new double[n_rows];
    	double[] m_SC_c_pre = null ;                 // Current spacecraft mass prior to burn
    	m_SC_c_pre = new double[n_rows];
    	double[] t_days = null ;                 // Time from launch 
    	t_days = new double[n_rows];
    	double[] m_steering = null ;                 // Propellant mass allocatated ass steering losses
    	m_steering = new double[n_rows];
    	double[] mar_sl = null ;                 // Margin for steering losses
    	mar_sl = new double[n_rows];
    	double[] mar_gen = null ;                 // Margin for delta-V 
    	mar_gen = new double[n_rows];
    	double[] deltav = null ;                 // Margin for delta-V 
    	deltav = new double[n_rows];
    	double m_loss_sum = 0 ;
    	double total_corr = 0 ;
    	double total_corr_mo = 0;
    	double m_total_steering_loss=0; 
    	double m_tank=0;
    	double total_deltav = 0; 
    	double m_residual_prop=0;
    	double total_add_loss=0;
    	double m_SC = 0;
    	double m_l_dry;
    	int k =0;
    	
    	
		for(int i =0 ; i < n_rows; i++){ // Read data from table 
        	t_days[i] = Double.parseDouble((String) model.getValueAt(i, 6));
        	isp[i]    	=  Double.parseDouble((String) model.getValueAt(i, 2));
        	add_loss[i] =  Double.parseDouble((String) model.getValueAt(i, 7));
        	if (cbMenuItem_MAR.isSelected()==true){
        		mari_mar2.setBackground(Color.green);
        	mar_gen[i] = (Double.parseDouble((String) model.getValueAt(i, 4))+100)/100;
        	} else {
        	mar_gen[i] = 1;	
        	mari_mar2.setBackground(Color.red);
        	}
        	deltav[i] =  Double.parseDouble((String) model.getValueAt(i, 1)) * mar_gen[i];
        	total_deltav = total_deltav + deltav[i];
        	model.setValueAt("" + df.format(deltav[i]), i, 5);
    		total_add_loss = total_add_loss + add_loss[i] ;
        	if(cbMenuItem_SL.isSelected()==true){
        		mar_sl[i] = Double.parseDouble((String) model.getValueAt(i, 3))/100; 
        		mari_sl2.setBackground(Color.green);
        	} else {
        		mar_sl[i] = 0;
        		mari_sl2.setBackground(Color.red);
        	}
        	model.setValueAt("0", i, 8);
		}
    	
		m_SC = m_init;

    	while( (Math.sqrt(Math.pow((total_corr-total_corr_mo), 2.0)) < Convergence_Criterea && k > MIN_ITERATION && k < MAX_ITERATION) == false ) {
										    		if(m_prop_total>0 && cbMenuItem_BO.isSelected()==true){
										    			total_corr = 0;
											            m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, true) ;
										        	for(int i =0 ; i < n_rows; i++){
										        		total_corr = total_corr + m_bo_corr[i];
										        		
										        	}			
										        	}
		m_total_steering_loss = 0 ;								    		
		m_prop_total=m_residual_prop;	
		for(int i = 0 ; i < n_rows; i++){
			m_loss_sum = add_loss[i] + m_bo_corr[i];
			if (i==0){
				m_SC_c_pre[i] = m_SC-m_loss_sum;
				m_prop_burn[i] = (m_SC_c_pre[i])*(1-Math.exp(-deltav[i]/(isp[i]*g_0)));
				m_steering[i] = (m_SC_c_pre[i]-m_prop_burn[i])*(1-Math.exp(-(mar_sl[i]*deltav[i])/(ISP_STEERING*g_0)));
				m_SC_c[i]= (m_SC) - m_prop_burn[i] - m_steering[i] - m_loss_sum;
				m_prop_int[i] =  m_prop_burn[i] + m_steering[i] + add_loss[i] + m_bo_corr[i];
				m_tank_sum[i] = m_tank ;
			}else {
				m_SC_c_pre[i] =  m_SC_c[i-1]-m_loss_sum;
				m_prop_burn[i] = (m_SC_c_pre[i])*(1-Math.exp(-deltav[i]/(isp[i]*g_0)));
				m_steering[i] = (m_SC_c_pre[i]-m_prop_burn[i])*(1-Math.exp(-(mar_sl[i]*deltav[i])/(isp[i]*g_0)));
				m_SC_c[i] = m_SC_c[i-1] - m_prop_burn[i] - m_steering[i] - m_loss_sum;
				m_prop_int[i] =  m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_tank_sum[i] =  m_tank_sum[i-1] - m_prop_int[i];	
			}
			if (i==0){
			m_prop_total = m_residual_prop + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];	
			} else {
			m_prop_total = m_prop_total + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
			}
			m_total_steering_loss=m_total_steering_loss + m_steering[i];

			model.setValueAt("" + df.format(m_prop_burn[i]), i, 9);
			model.setValueAt("" + df.format(m_SC_c_pre[i]), i, 10);
			model.setValueAt("" + df.format(m_SC_c[i]), i, 11);

			model.setValueAt("" + df.format(m_tank_sum[i]), i, 12);

		}
		m_tank = m_prop_total;
		m_residual_prop = m_tank * (margin_prop)/100;
									        if(m_prop_total>0  && cbMenuItem_BO.isSelected()==true){
									        	total_corr_mo = 0;
									        	m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, true) ;
									        	for(int i =0 ; i < n_rows; i++){
									    		total_corr_mo = total_corr_mo + m_bo_corr[i];
									    		
									    		if(cbMenuItem_BO.isSelected()==true){
									    		model.setValueAt("" + df.format(m_bo_corr[i]), i, 8);
									    		} 
									        }
									    	} 	
	k++;	
    }
    m_l_dry = m_SC - m_payload - m_prop_total;
    	glob_m_fuel = m_prop_total;
    	glob_m_dry = m_l_dry;
		Res1.setText("" + df.format(m_residual_prop));
		Res4.setText("" + df.format(total_deltav));
		rp2_4.setText("" + df.format(m_prop_total/(m_init-m_payload)*100));
		m_wet = m_dry + m_prop_total + m_payload;
		Res3.setText("" + df.format(m_l_dry));
		Res2.setText("" + df.format(m_prop_total));
		rp2_8.setText("" + df.format(m_SC - m_prop_total - m_dry));
		rp2_10.setText("" + df.format(m_dry));
		if ((m_l_dry -  m_dry)>0){
		rp2_d1.setText("+" + df.format(m_l_dry -  m_dry));
		rp2_d1.setForeground(new Color(50,205,50));
		} else {
		rp2_d1.setText("" + df.format(m_l_dry -  m_dry));
		rp2_d1.setForeground(Color.red);
		}
		if (((m_SC - m_prop_total - m_dry)-m_payload)>0){
		rp2_d2.setText("+" + df.format((m_SC - m_prop_total - m_dry)-m_payload));
		rp2_d2.setForeground(new Color(50,205,50));
		} else {
		rp2_d2.setText("" + df.format((m_SC - m_prop_total - m_dry)-m_payload));
		rp2_d2.setForeground(Color.red);
		}
    	if(cbMenuItem_SL.isSelected()==true){
    	steering_label.setText("SL ( " + df.format(m_total_steering_loss ) + " kg ) ");
    	} else  {
    	steering_label.setText("SL( " + df.format(0) + " kg ) ");	
    	}
    	if(cbMenuItem_BO.isSelected()==true){
    	boiloff_label.setText("BO ( " + df.format(total_corr_mo) + " kg ) ");
    	mari_bo2.setBackground(Color.green);  
    	} else {
    	boiloff_label.setText("BO ( " + df.format(0) + " kg ) ");
    	mari_bo2.setBackground(Color.red);
    	}
    }
    
    public void CalcCascade_BU(){
    	int n_rows = model4.getRowCount();
    	m_init = Double.parseDouble(Minit.getText());
    	m_dry = Double.parseDouble(cd_tf1.getText());
    	Res43.setText("" + df.format(m_dry));
    	m_payload = Double.parseDouble(Mpayload.getText());
    	rp4_2.setText("" + m_payload);
    	double bo_rate = Double.parseDouble(BoilOff_TF.getText());
    	margin_prop = Double.parseDouble(FuelMar_TF.getText());
    	double mar_wet = 0;
    	if (sc_cb4.isSelected()){
    	mar_wet = Double.parseDouble(sc_TF_01.getText())/100;
    	}
    	double m_prop_total=0;
    	double[] isp = null;
    	isp = new double[n_rows];
    	double[] add_loss = null ; 
    	add_loss = new double[n_rows];
    	double[] m_prop_burn = null ;             // Prop mass for alloc. burn
    	m_prop_burn = new double[n_rows];
    	double[] m_prop_int = null ; 			 // Prop mass for alloc. burn plus losses
    	m_prop_int = new double[n_rows];
    	double[] m_tank_sum = null ; 
    	m_tank_sum = new double[n_rows];
    	double[] m_bo_corr = null ;              // Boil-off correction
    	m_bo_corr = new double[n_rows];
    	double[] m_SC_c = null ;                 // Current spacecraft mass 
    	m_SC_c = new double[n_rows];
    	double[] m_SC_c_pre = null ;                 // Current spacecraft mass prior to burn
    	m_SC_c_pre = new double[n_rows];
    	double[] t_days = null ;                 // Time from launch 
    	t_days = new double[n_rows];
    	double[] m_steering = null ;                 // Propellant mass allocatated ass steering losses
    	m_steering = new double[n_rows];
    	double[] mar_sl = null ;                 // Margin for steering losses
    	mar_sl = new double[n_rows];
    	double[] mar_gen = null ;                 // Margin for delta-V 
    	mar_gen = new double[n_rows];
    	double[] deltav = null ;                 // Margin for delta-V 
    	deltav = new double[n_rows];
    	int[] nr_engine = null ;                 // Engine type Number
    	nr_engine = new int[model4.getRowCount()];
    	double total_corr = 0 ;
    	double total_corr_mo = 0;
    	double m_total_steering_loss=0; 
    	double m_tank =0;
    	double total_deltav = 0; 
    	double m_residual_prop=0;
    	double total_add_loss=0;
    	double m_SC = 0;
    	int k =0;    	
    	
    	if (model7.getRowCount()>0){
    	for (int i = model7.getRowCount(); i >= 1; i--) {
    		model7.removeRow(i-1);
    	}
    	}
    	
    	double[] PROP_engine = null ;                 // Propellant amount per engine 
    	PROP_engine = new double[model6.getRowCount()+1];    	
    	
		for(int i =0 ; i < n_rows; i++){ // Read data from table 
			nr_engine[i]=-1;
        	t_days[i] = Double.parseDouble((String) model4.getValueAt(i, 6));
        	isp[i]    	=  Double.parseDouble((String) model4.getValueAt(i, 2));
        	add_loss[i] =  Double.parseDouble((String) model4.getValueAt(i, 7));
        	if (cbMenuItem_MAR.isSelected()==true){
        	mar_gen[i] = (Double.parseDouble((String) model4.getValueAt(i, 4))+100)/100;
        	mari_mar.setBackground(Color.green);
        	} else {
        	mar_gen[i] = 1;	
        	mari_mar.setBackground(Color.red);
        	}
        	deltav[i] =  Double.parseDouble((String) model4.getValueAt(i, 1)) * mar_gen[i];
        	total_deltav = total_deltav + deltav[i];
        	model4.setValueAt("" + df.format(deltav[i]), i, 5);
    		total_add_loss = total_add_loss + add_loss[i] ;
        	if(cbMenuItem_SL.isSelected()==true){
        		mar_sl[i] = Double.parseDouble((String) model4.getValueAt(i, 3))/100; 
        		mari_sl.setBackground(Color.green);
        	} else {
        		mar_sl[i] = 0;
        		mari_sl.setBackground(Color.red);
        	}
        	
        	for(int j=0;j<model6.getRowCount();j++){
        		try {
        		String string_01 = (String) model2.getValueAt(i, 8);
        		String string_02 = (String) model6.getValueAt(j, 0);
        		if ((string_01).equals(string_02)) {
        			nr_engine[i]=j;	
        		} 
        		} catch (NullPointerException e){
        			System.out.println("Value not found ");
        		}
        	}
        	model4.setValueAt("0", i, 8);
		}
		m_SC = m_payload + m_dry ;
    	
    	while( (Math.sqrt(Math.pow((total_corr-total_corr_mo), 2.0)) < Convergence_Criterea && k > MIN_ITERATION && k < MAX_ITERATION) == false ) {
        	
									    		if(m_prop_total>0 && cbMenuItem_BO.isSelected() ==true){
									    			total_corr = 0;
									        	m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, false) ;
									        	for(int i =0 ; i < n_rows; i++){
									        		total_corr = total_corr + m_bo_corr[i];
									        	}
									    		}
    	
		m_total_steering_loss = 0 ;								    		
		m_prop_total=m_residual_prop;
		m_SC = m_payload + m_dry;
		for(int j=0;j<model6.getRowCount();j++){
		PROP_engine[j] = 0;
		}
		for(int i = n_rows-1 ; i > -1; i--){
			if (i==n_rows-1){
				m_steering[i] = (m_SC+ m_residual_prop)*(Math.exp((mar_sl[i]*deltav[i])/(ISP_STEERING*g_0))-1);
				m_prop_burn[i] = (m_SC+ m_residual_prop + m_steering[i])*(Math.exp(deltav[i]/(isp[i]*g_0))-1);
				m_SC_c[i]= (m_SC+ m_residual_prop) + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_prop_int[i] =  m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_tank_sum[i] = m_prop_int[i] + m_residual_prop;
				m_SC_c_pre[i] = (m_SC+ m_residual_prop);
			}else {
				m_steering[i] = (m_SC_c[i+1])*(Math.exp((mar_sl[i]*deltav[i])/(isp[i]*g_0))-1);
				m_prop_burn[i] = (m_SC_c[i+1]+m_steering[i])*(Math.exp(deltav[i]/(isp[i]*g_0))-1);
				m_SC_c[i]= m_SC_c[i+1] + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_prop_int[i] =  m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_tank_sum[i]=m_tank_sum[i+1] + m_prop_int[i];
				m_SC_c_pre[i]= m_SC_c[i+1];	
			}
			if (i==n_rows-1){
			m_prop_total = m_residual_prop + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];	
			} else {
			m_prop_total = m_prop_total + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
			}
			m_total_steering_loss=m_total_steering_loss + m_steering[i];
			
										if (nr_engine[i]>-1){
											if (i==n_rows-1){
											PROP_engine[0]	= PROP_engine[0] + m_residual_prop;
											}
										PROP_engine[nr_engine[i]] = PROP_engine[nr_engine[i]] + m_prop_int[i];	// Propellant split
										} 

			model4.setValueAt("" + df.format(m_prop_burn[i]), i, 9);
			model4.setValueAt("" + df.format(m_SC_c_pre[i]+m_prop_burn[i]), i, 10);
			model4.setValueAt("" + df.format(m_SC_c_pre[i]), i, 11);
			if (i>0) {
			model4.setValueAt("" + df.format(m_tank_sum[i]), i-1, 12);	
			} 
			if (i==(n_rows-1)){
			model4.setValueAt("" + df.format(m_residual_prop), i, 12);
			}
		}
		m_tank = m_prop_total ;
		m_residual_prop = m_tank * (margin_prop)/100;
		
											if(m_prop_total>0  && cbMenuItem_BO.isSelected()==true){	
												total_corr_mo =0;
									        	m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, false) ;
									        	for(int i =0 ; i < n_rows; i++){
									    		total_corr_mo = total_corr_mo + m_bo_corr[i];
									    		if(cbMenuItem_BO.isSelected()==true){
									    		model4.setValueAt("" + df.format(m_bo_corr[i]), i, 8);
									    		} 
									        }
									    	} 	
	k++;	
    }

    	glob_m_fuel = m_prop_total;
    	double wet_mar = (m_dry + m_prop_total)*mar_wet;
		Res41.setText("" + df.format(m_residual_prop));
		Res44.setText("" + df.format(total_deltav));
		rp4_4.setText("" + df.format(glob_m_fuel/(m_dry+m_prop_total)*100));
		analysis_TF_01.setText("" + df.format(glob_m_fuel/(m_dry+m_prop_total)*100));
		m_wet = m_dry + glob_m_fuel + m_payload + wet_mar;
		if(m_wet<m_init){
		rp4_6.setText("" + df.format(m_wet));
		rp4_6.setForeground(Color.green);
		} else {
		rp4_6.setText("" + df.format(m_wet));
		rp4_6.setForeground(Color.red);
		}
		rp4_16.setText("" + df.format((m_payload/m_wet)*100));
		rp4_12.setText("" + df.format(m_dry/(m_dry+glob_m_fuel)*100));
		
     	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false ;
     	if (cbMenuItem_BO.isSelected()==true){
     		With_boiloff = true;
     	}
     	if (cbMenuItem_MAR.isSelected()==true){
     		With_DV_margin = true;
     	}
     	if (cbMenuItem_SL.isSelected()==true){
     		With_SteeringLosses = true;
     	}
    	double m_dry_max = CalcTopDown_Dry(m_init-global_pmr,m_payload, With_DV_margin, With_boiloff, With_SteeringLosses);
		if ( ((m_dry_max-m_dry)/m_dry) > 0 ){
		rp4_22.setText("+" + df.format(((m_dry_max-m_dry)/global_sc_wom)*100));
		} else {
		rp4_22.setText("" + df.format(((m_dry_max-m_dry)/global_sc_wom)*100));	
		}
		Res45.setText("" + df.format(m_wet -  m_payload));
		double epsilon = m_dry/(m_dry+glob_m_fuel); 
		double lambda = m_payload/m_wet; 
		rp4_18.setText("" + df.format( (1+lambda)/(lambda + epsilon)));
		Res42.setText("" + df.format(glob_m_fuel));
		double m_pg_total = 0; 
    	for(int j=0; j < model6.getRowCount();j++){
    		try {
    		double OF = Double.parseDouble((String) model6.getValueAt(j, 4));
    		double mf = PROP_engine[j]/(OF+1);
    		double mo = OF * mf; 
    		double rho_f = Double.parseDouble((String) model6.getValueAt(j, 5)); 
    		double rho_o   = Double.parseDouble((String) model6.getValueAt(j, 6)); 
    		double v_f = mf/rho_f*1000;
    		double v_o = mo/rho_o*1000; 
			double m_pg = 0 ;
			double v_pg = 0 ;
    		if (pg_cb1.isSelected()){
    			double tc = Double.parseDouble(pg_t2.getText());
    			double ti = Double.parseDouble(pg_t3.getText()); 
    			double pc = Double.parseDouble(pg_t4.getText()); 
    			double pi = Double.parseDouble(pg_t5.getText()); 
    			double M  = Double.parseDouble(pg_t6.getText()); 
    		m_pg = (pc*(v_f+v_o)/1000*M)/(gas_constant*tc);    // Pressurant mass [kg]
    		v_pg = (m_pg*gas_constant*ti)/(pi*M)*1000;    // Pressurant volume [litre]
    		} else {
    		m_pg = 0 ;
    		v_pg = 0 ; 
    		}
    		m_pg_total = m_pg_total + m_pg; 
    		row7[0] = (String) model6.getValueAt(j, 0);
        	row7[1] = "" + df.format(PROP_engine[j]);
        	row7[2] = "" + df.format(mf);
        	row7[3] = "" + df.format(mo);
        	row7[4] = "" + df.format(v_f);
        	row7[5] = "" + df.format(v_o);
        	row7[6] = "" + df.format(m_pg);
        	row7[7] = "" + df.format(v_pg);
        	model7.addRow(row7);
    		} catch (NullPointerException e){
    			System.out.println("Value not found ");
    		}
    	}	
    	pg_l8.setText("" + df.format(m_pg_total));
		double wet_diff = (m_wet-m_init)/m_init*100;
		if (wet_diff>0) 
		{
			rp4_14.setText("+" + df.format(wet_diff));
			rp4_14.setForeground(Color.red);
		} else if (wet_diff == 0) {
			rp4_14.setText("+/-" + df.format(wet_diff));
			rp4_14.setForeground(Color.blue);
		} else {
			rp4_14.setText("" + df.format(wet_diff));
			rp4_14.setForeground(Color.green);
		}
     	//double m_dry_max = CalcTopDown_Dry(m_init, With_DV_margin, With_boiloff, With_SteeringLosses);
     	double dry_diff = (m_dry - m_dry_max) / m_dry_max *100;
		if (dry_diff>0) 
		{
			rp4_13.setText("+" + df.format(dry_diff));
			rp4_13.setForeground(Color.red);
		} else if (dry_diff == 0) {
			rp4_13.setText("+/-" + df.format(dry_diff));
			rp4_13.setForeground(Color.blue);
		} else {
			rp4_13.setText("" + df.format(dry_diff));
			rp4_13.setForeground(Color.green);
		} 
		if (sc_cb4.isSelected()){
			rp4_13.setText("-");
			rp4_13.setForeground(Color.black);
		}
    	if(cbMenuItem_SL.isSelected()==true){
    	steering_label4.setText("SL ( " + df.format(m_total_steering_loss ) + " kg ) ");
    	steering_label4.setForeground(l_c);
    	
    	} else  {
    	steering_label4.setText("");	
    	//steering_label4.setForeground(Color.red);
    	}
    	if(cbMenuItem_BO.isSelected()==true){
    	boiloff_label4.setText("BO ( " + df.format(total_corr_mo) + " kg ) "); 
    	boiloff_label4.setForeground(l_c);
    	mari_bo.setBackground(Color.green);
    	} else {
    	boiloff_label4.setText(""); 	
    	//boiloff_label4.setForeground(Color.red);
    	mari_bo.setBackground(Color.red);
    	}
    	if (cbMenuItem_MAR.isSelected()==true){
    		margin_label4.setText("MAR");
    		margin_label4.setForeground(l_c);	
    	} else {
    		margin_label4.setText("");
    		//margin_label4.setForeground(Color.red);	
    	}
    }
    
    public static double CalcBottomUp_Wet(double M_DRY, double M_PAYLOAD, boolean With_DV_margin, boolean With_boiloff, boolean With_SteeringLosses, boolean wet_margin){
    	int n_rows = model4.getRowCount(); 
    	double bo_rate = Double.parseDouble(BoilOff_TF.getText());
    	margin_prop = Double.parseDouble(FuelMar_TF.getText());
    	double mar_wet=0;
    	if (wet_margin == true){
    		mar_wet = Double.parseDouble(sc_TF_01.getText())/100;
    	}
    	
    	double m_prop_total=0;
    	double[] isp = null;
    	isp = new double[n_rows];
    	double[] add_loss = null ; 
    	add_loss = new double[n_rows];
    	double[] m_prop_burn = null ;             // Prop mass for alloc. burn
    	m_prop_burn = new double[n_rows];
    	double[] m_prop_int = null ; 			 // Prop mass for alloc. burn plus losses
    	m_prop_int = new double[n_rows];
    	double[] m_tank_sum = null ; 
    	m_tank_sum = new double[n_rows];
    	double[] m_bo_corr = null ;              // Boil-off correction
    	m_bo_corr = new double[n_rows];
    	double[] m_SC_c = null ;                 // Current spacecraft mass 
    	m_SC_c = new double[n_rows];
    	double[] m_SC_c_pre = null ;                 // Current spacecraft mass prior to burn
    	m_SC_c_pre = new double[n_rows];
    	double[] t_days = null ;                 // Time from launch 
    	t_days = new double[n_rows];
    	double[] m_steering = null ;                 // Propellant mass allocatated ass steering losses
    	m_steering = new double[n_rows];
    	double[] mar_sl = null ;                 // Margin for steering losses
    	mar_sl = new double[n_rows];
    	double[] mar_gen = null ;                 // Margin for delta-V 
    	mar_gen = new double[n_rows];
    	double[] deltav = null ;                 // Margin for delta-V 
    	deltav = new double[n_rows];
    	double total_corr = 0 ;
    	double total_corr_mo = 0;
    	double m_total_steering_loss=0; 
    	double m_tank=0;
    	double total_deltav = 0; 
    	double m_residual_prop=0;
    	double total_add_loss=0;
    	double m_SC = 0;
    	double m_fuel_margin=0;
    	int k =0;

		for(int i =0 ; i < n_rows; i++){ // Read data from table 
        	t_days[i] = Double.parseDouble((String) model4.getValueAt(i, 6));
        	isp[i]    	=  Double.parseDouble((String) model4.getValueAt(i, 2));
        	add_loss[i] =  Double.parseDouble((String) model4.getValueAt(i, 7));
        	if (With_DV_margin==true){
        	mar_gen[i] = (Double.parseDouble((String) model4.getValueAt(i, 4))+100)/100;
        	} else {
        	mar_gen[i] = 1;	
        	}
        	deltav[i] =  Double.parseDouble((String) model4.getValueAt(i, 1)) * mar_gen[i];
        	total_deltav = total_deltav + deltav[i];
    		total_add_loss = total_add_loss + add_loss[i] ;
        	if(With_SteeringLosses==true){
        		mar_sl[i] = Double.parseDouble((String) model4.getValueAt(i, 3))/100; 
        	} else {
        		mar_sl[i] = 0;
        	}
		}
    	
		m_SC = M_PAYLOAD + M_DRY + m_fuel_margin;
    	while( (Math.sqrt(Math.pow((total_corr-total_corr_mo), 2.0)) < Convergence_Criterea && k > MIN_ITERATION && k < MAX_ITERATION) == false ) {
    	
										    		if(m_prop_total>0 && With_boiloff ==true){
										    			total_corr = 0;
										        	m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, false) ;
										        	for(int i =0 ; i < n_rows; i++){
										        		total_corr = total_corr + m_bo_corr[i];
										        	}
										    		}
		m_total_steering_loss = 0 ;								    		
		m_prop_total=m_residual_prop;	
		//m_SC = m_payload + M_DRY + (m_prop_total + M_DRY + m_payload)*mar_wet ;
		for(int i = n_rows-1 ; i > -1; i--){
			if (i==n_rows-1){
				m_steering[i] = (m_SC+ m_residual_prop)*(Math.exp((mar_sl[i]*deltav[i])/(ISP_STEERING*g_0))-1);
				m_prop_burn[i] = (m_SC+ m_residual_prop + m_steering[i])*(Math.exp(deltav[i]/(isp[i]*g_0))-1);
				m_SC_c[i]= (m_SC+ m_residual_prop) + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_prop_int[i] =  m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_tank_sum[i] = m_prop_int[i] + m_residual_prop;
				m_SC_c_pre[i] = (m_SC+ m_residual_prop);
			}else {
				m_steering[i] = (m_SC_c[i+1])*(Math.exp((mar_sl[i]*deltav[i])/(isp[i]*g_0))-1);
				m_prop_burn[i] = (m_SC_c[i+1]+m_steering[i])*(Math.exp(deltav[i]/(isp[i]*g_0))-1);
				m_SC_c[i]= m_SC_c[i+1] + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_prop_int[i] =  m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_tank_sum[i]=m_tank_sum[i+1] + m_prop_int[i];
				m_SC_c_pre[i]= m_SC_c[i+1];	
			}
			if (i==n_rows-1){
			m_prop_total = m_residual_prop + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];	
			} else {
			m_prop_total = m_prop_total + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
			}
			m_total_steering_loss=m_total_steering_loss + m_steering[i];
		}
		m_tank = m_prop_total;
		m_residual_prop = m_tank * (margin_prop)/100;
											if(m_prop_total>0  && With_boiloff==true){	
												total_corr_mo = 0;
											m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, false) ;
									        	for(int i =0 ; i < n_rows; i++){
									    		total_corr_mo = total_corr_mo + m_bo_corr[i];
									        }
									    	} 
	k++;	
    }
    
    	double m_prop_corr = m_prop_total + (M_DRY + m_fuel_margin + m_prop_total)*mar_wet;
    	m_wet = M_PAYLOAD + M_DRY + m_fuel_margin + m_prop_corr;
		return m_wet;
    }
    
    public static double CalcTopDown_Dry(double M_WET, double M_PAYLOAD, boolean With_DV_margin, boolean With_boiloff, boolean With_SteeringLosses){
    	int n_rows = model4.getRowCount(); 
    	double bo_rate = Double.parseDouble(BoilOff_TF.getText());
    	margin_prop = Double.parseDouble(FuelMar_TF.getText());
    	double m_prop_total=0;
    	double[] isp = null;
    	isp = new double[n_rows];
    	double[] add_loss = null ; 
    	add_loss = new double[n_rows];
    	double[] m_prop_burn = null ;             // Prop mass for alloc. burn
    	m_prop_burn = new double[n_rows];
    	double[] m_prop_int = null ; 			 // Prop mass for alloc. burn plus losses
    	m_prop_int = new double[n_rows];
    	double[] m_tank_sum = null ; 
    	m_tank_sum = new double[n_rows];
    	double[] m_bo_corr = null ;              // Boil-off correction
    	m_bo_corr = new double[n_rows];
    	double[] m_SC_c = null ;                 // Current spacecraft mass 
    	m_SC_c = new double[n_rows];
    	double[] m_SC_c_pre = null ;                 // Current spacecraft mass prior to burn
    	m_SC_c_pre = new double[n_rows];
    	double[] t_days = null ;                 // Time from launch 
    	t_days = new double[n_rows];
    	double[] m_steering = null ;                 // Propellant mass allocatated ass steering losses
    	m_steering = new double[n_rows];
    	double[] mar_sl = null ;                 // Margin for steering losses
    	mar_sl = new double[n_rows];
    	double[] mar_gen = null ;                 // Margin for delta-V 
    	mar_gen = new double[n_rows];
    	double[] deltav = null ;                 // Margin for delta-V 
    	deltav = new double[n_rows];
    	double m_loss_sum = 0;
    	double total_corr = 0 ;
    	double total_corr_mo = 0;
    	double m_total_steering_loss=0; 
    	double m_tank=0;
    	double total_deltav = 0; 
    	double m_residual_prop=0;
    	double total_add_loss=0;
    	double m_SC = 0;
    	int k =0;
    	
    	
		for(int i   = 0 ; i < n_rows; i++){ // Read data from table 
        	t_days[i]   = Double.parseDouble((String) model4.getValueAt(i, 6));
        	isp[i]    	=  Double.parseDouble((String) model4.getValueAt(i, 2));
        	add_loss[i] =  Double.parseDouble((String) model4.getValueAt(i, 7));
        	if (With_DV_margin==true){
        	mar_gen[i] = (Double.parseDouble((String) model4.getValueAt(i, 4))+100)/100;
        	} else {
        	mar_gen[i] = 1;	
        	}
        	deltav[i] =  Double.parseDouble((String) model4.getValueAt(i, 1)) * mar_gen[i];
        	total_deltav = total_deltav + deltav[i];
        	model4.setValueAt("" + df.format(deltav[i]), i, 5);
    		total_add_loss = total_add_loss + add_loss[i] ;
        	if(With_SteeringLosses==true){
        		mar_sl[i] = Double.parseDouble((String) model4.getValueAt(i, 3))/100; 
        	} else {
        		mar_sl[i] = 0;
        	}
		}
    	
		m_SC = M_WET;

    	while( (Math.sqrt(Math.pow((total_corr-total_corr_mo), 2.0)) < Convergence_Criterea && k > MIN_ITERATION && k < MAX_ITERATION) == false ) {
    	
										    		if(m_prop_total>0 && With_boiloff==true){
										    			total_corr = 0;
											    	m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, true) ;
										        	for(int i =0 ; i < n_rows; i++){
										        		total_corr = total_corr + m_bo_corr[i];
										        	}								    		}
		m_total_steering_loss = 0 ;								    		
		m_prop_total=m_residual_prop;	
		for(int i = 0 ; i < n_rows; i++){
			m_loss_sum = add_loss[i] + m_bo_corr[i];
			if (i==0){
				m_SC_c_pre[i] = m_SC-m_loss_sum;
				m_prop_burn[i] = (m_SC_c_pre[i])*(1-Math.exp(-deltav[i]/(isp[i]*g_0)));
				m_steering[i] = (m_SC_c_pre[i]-m_prop_burn[i])*(1-Math.exp(-(mar_sl[i]*deltav[i])/(ISP_STEERING*g_0)));
				m_SC_c[i]= (m_SC) - m_prop_burn[i] - m_steering[i] - m_loss_sum;
				m_prop_int[i] =  m_prop_burn[i] + m_steering[i] + add_loss[i] + m_bo_corr[i];
				m_tank_sum[i] = m_tank ;
			}else {
				m_SC_c_pre[i] =  m_SC_c[i-1]-m_loss_sum;
				m_prop_burn[i] = (m_SC_c_pre[i])*(1-Math.exp(-deltav[i]/(isp[i]*g_0)));
				m_steering[i] = (m_SC_c_pre[i]-m_prop_burn[i])*(1-Math.exp(-(mar_sl[i]*deltav[i])/(isp[i]*g_0)));
				m_SC_c[i] = m_SC_c[i-1] - m_prop_burn[i] - m_steering[i] - m_loss_sum;
				m_prop_int[i] =  m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
				m_tank_sum[i] =  m_tank_sum[i-1] - m_prop_int[i];	
			}
			if (i==0){
			m_prop_total = m_residual_prop + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];	
			} else {
			m_prop_total = m_prop_total + m_prop_burn[i] + add_loss[i] + m_bo_corr[i] + m_steering[i];
			}
			m_total_steering_loss=m_total_steering_loss + m_steering[i];
		}
		m_tank = m_prop_total;
		m_residual_prop = m_tank * (margin_prop)/100;
									        if(m_prop_total>0  && With_boiloff ==true){
									        	total_corr_mo = 0;
									        	m_bo_corr = m_boiloff(n_rows, m_tank_sum, t_days, m_prop_total,bo_rate, true) ;
									        	for(int i =0 ; i < n_rows; i++){
									    		total_corr_mo = total_corr_mo + m_bo_corr[i];
									        }
									    	} 	
	k++;	
    }
    	double m_dry = M_WET - m_prop_total - M_PAYLOAD;
    	return m_dry;
    }
    
    
    public void UPDATE_BottomUp(){
    	System.out.println("Bottom-UP update select");
    	OVERVIEW_UPDATE();
    	if (auto_RCS == true){
    		FIND_RCS(); 
    	}
    	CalcCascade_BU();
    	Set_D2W();

    	if (chart4_fd == true){
	        result4.removeAllSeries();
	    	CreateChart_04();
    	} else{
			result4.removeAllSeries();
			AddDataset_04();
    	}
    	
    	if (chart5_fd == true){
	        result5.clear();
	    	CreateChart_05();
			Chart5_ResetMarker();
    	} else{
			result5.clear();
			AddDataset_05();
			Chart5_ResetMarker();
    	}
    	
    	if (chart6_fd == true){
	        result6.removeAllSeries();
	    	CreateChart_06();
    	} else{
			result6.removeAllSeries();
			AddDataset_06();
    	}

    	if (chart7_fd == true){
	        result7.clear();
	    	CreateChart_07();
			Chart7_ResetMarker();
    	} else{
			result7.clear();
			AddDataset_07();
			Chart7_ResetMarker();
    	}
    	
    	if (chart8_fd == true){
	        result8.removeAllSeries();
	    	CreateChart_08();
    	} else{
			result8.removeAllSeries();
			AddDataset_08();
    	}

    	if (chart9_fd == true){
	        result9.removeAllSeries();
	    	CreateChart_09();
    	} else{
			result9.removeAllSeries();
			AddDataset_09();
    	}

    	if (chart10_fd == true){
	        result10.removeAllSeries();
	    	CreateChart_10();
    	} else{
			result10.removeAllSeries();
			AddDataset_10();
    	}
    	
    	if (chart11_fd == true){
	        result11.removeAllSeries();
	        AddDataset_11();
	    	CreateChart_11();
    	} else{
			result11.removeAllSeries();
			AddDataset_11();
    	}
    	
    	if (chart12_fd == true){
	        result12.removeAllSeries();
	    	CreateChart_12();
    	} else{
			result12.removeAllSeries();
			AddDataset_12();
    	}
    	if (chart13_fd == true){
	        result13.removeAllSeries();
	    	CreateChart_13();
    	} else{
			result13.removeAllSeries();
			AddDataset_13();
    	}
    	
    	if (chart14_fd == true){
	        result14.removeAllSeries();
	        AddDataset_14();
	    	CreateChart_14();
	    	Chart14_ResetMarker();
    	} else{
			result14.removeAllSeries();
			AddDataset_14();
			Chart14_ResetMarker();
    	}
    	
    	if (chart15_fd == true){
	        result15.removeAllSeries();
	        AddDataset_15();
	    	CreateChart_15();
	    	Chart15_ResetMarker();
    	} else{
			result15.removeAllSeries();
			AddDataset_15();
			Chart15_ResetMarker();
    	}
    	
    	if(cb_sub41.isSelected()==false){
        	try {
        		chart4.getXYPlot().clearRangeMarkers();
        		chart4.getXYPlot().clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart4_ResetMarker();
    	}
    	if(cb_sub42.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart6.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart6_ResetMarker();
    	}
    	if(cb_sub43.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart8.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart8_ResetMarker();
    	}
    	if(cb_sub44.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart9.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart9_ResetMarker();
    	}
    	if(cb_sub45.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart10.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart10_ResetMarker();
    	}
    	if(cb_sub46.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart11.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart11_ResetMarker();
    	}
    	if(cb_sub47.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart12.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart12_ResetMarker();
    	}
    	if(cb_sub48.isSelected()==false){
        	try {
            	final XYPlot plot2 = (XYPlot) chart13.getPlot();
            	plot2.clearRangeMarkers();
            	plot2.clearDomainMarkers();
        	} catch (NullPointerException e) { }
    	} else {
    		Chart13_ResetMarker();
    	} 
    }
    public void UPDATE_TopDown(){
    	if (auto_RCS == true){
    		FIND_RCS(); 
    	}
    	  CalcCascade_TD();
      	result1.removeAllSeries();
    	  CreateChart_01();
      	result2.removeAllSeries();
    	  CreateChart_02();
      	result3.clear();
    	  CreateChart_03();		
    }
    
    public void UPDATE_Analysis(){
    	if (chartA1_fd == true){
	        result13.removeAllSeries();
	        result41.removeAllSeries();
			AddDataset_13();
			AddDataset_041();
	        CreateChart_A01();
    	} else{
			result13.removeAllSeries();
			result41.removeAllSeries();
			AddDataset_13();
			AddDataset_041();
    	}
    	if (chartA2_fd == true){
	        result101.removeAllSeries();
	        result11_A2.removeAllSeries();
			AddDataset_111();
	        CreateChart_A02();
    	} else{
			result101.removeAllSeries();
			result11_A2.removeAllSeries();
			AddDataset_111();
    	}
    }
    
    public void UPDATE_PL_Scale(){
    	//Loading_window();
    	if (chartA3_fd == true){
	        result11_A3_1.removeAllSeries();
	        result11_A3_2.removeAllSeries();
	        result11_A3_3.removeAllSeries();
	        result11_A3_4.removeAllSeries();
	        result11_A3_41.removeAllSeries();
			AddDataset_A03();
	        CreateChart_A03();
    	} else {
	        result11_A3_1.removeAllSeries();
	        result11_A3_2.removeAllSeries();
	        result11_A3_3.removeAllSeries();
	        result11_A3_4.removeAllSeries();
	        result11_A3_41.removeAllSeries();
	        AddDataset_A03();
    	}
    }

    public void actionPerformed(ActionEvent e)  {
        if(e.getSource() == redButton)
        {   
        	UPDATE_TopDown();	
        }
        
        else if (e.getSource() == button_int){
        	FNC_EN_UPDATE();
        	FNC_PL_UPDATE();
        	FNC_SC_UPDATE();
        	FNC_POWER_UPDATE();
        	Tables_SYNC();
System.out.println("" + myfile_opened);
        }
        
        else if (e.getSource() == sc_load){
        	//Import_SC();
        }
        
        else if (e.getSource() == pl_load){
        	//Import_PL();
        }
        
        else if (e.getSource() == deltav_load){
        	Import_DeltaV();
        }
        
        else if(e.getSource() == refButton)
        {    
        	UPDATE_BottomUp();
        }
            else if(e.getSource() == blueButton)
        {

            	row2[0] = "---";
            	row2[1] = "1500";
            	row2[2] = "340";
            	row2[3] = "2.5";
            	row2[4] = "5";
            	row2[5] = "-";
            	row2[6] = "1";
            	row2[7] = "0";	
            	row2[9] = true;
            	model2.addRow(row2);
            	row4[0] = "---";
            	row4[1] = "1500";
            	row4[2] = "340";
            	row4[3] = "2.5";
            	row4[4] = "5";
            	row4[5] = "-";
            	row4[6] = "1";
            	row4[7] = "0";	
            	model4.addRow(row4);
            	row[0] = "---";
            	row[1] = "1500";
            	row[2] = "340";
            	row[3] = "2.5";
            	row[4] = "5";
            	row[5] = "-";
            	row[6] = "1";
            	row[7] = "0";	
            	model.addRow(row);
        }
        
            else if(e.getSource() == pl_add)
        {
            	row3[0] = "Payload Element";
            	row3[1] = "1";
            	row3[2] = "150";
            	row3[3] = "0.0";
            	row3[4] = "0.0";
            	row3[5] = "0.0";
            	row3[6] = "0.0";
            	row3[7] = "0.0";
            	row3[8] = "-";
            	row3[9] = true;
            	row3[10] = "" + model3.getRowCount();
            	model3.addRow(row3);
            	FNC_PL_UPDATE();
        }
            else if(e.getSource() == sc_add)
        {
            	row5[0] = "S/C Subsystem";
            	row5[1] = "1";
            	row5[2] = "150";
            	row5[3] = "0.0";
            	row5[4] = "0.0";
            	row5[5] = "0.0";
            	row5[6] = "0.0";
            	row5[7] = "0.0";
            	row5[8] = "150";
            	row5[9] = true; 
            	row5[10] = "" + model5.getRowCount();
            	model5.addRow(row5);
            	FNC_SC_UPDATE();
        }
            else if(e.getSource() == en_add)
        {
            	/*
            	row6[0] = "Engine Sys";
            	row6[1] = "350";
            	row6[2] = "0.0";
            	model6.addRow(row6);
            	FNC_EN_UPDATE();
            	*/
                JFrame.setDefaultLookAndFeelDecorated(true);
                frame_AddEngine = new JFrame( PROJECT_TITLE + " | Add Propulsion System ");

                //Create and set up the content pane.
                BlueBook_main demo = new BlueBook_main();
                try {
                	frame_AddEngine.setContentPane(demo.createAddPropulsionSystemPane());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

               // frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame_AddEngine.setSize(ext_hx_settings, ext_hy_settings);
                frame_AddEngine.setVisible(true);
                frame_AddEngine.setLocationRelativeTo(null);
                BufferedImage myImage;
				try {
					myImage = ImageIO.read(url_smalllogo);
					frame_AddEngine.setIconImage(myImage);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        }
        
            else if(e.getSource() == greenButton)
            {
            	int i = table2.getSelectedRow();
            	if (i >= 0){
            		model2.removeRow(i);
            		model4.removeRow(i);
            		model.removeRow(i);
            	}
            	else{
            		System.out.println("Delete Error");
            	}
            }
        
            else if(e.getSource() == pl_delete)
            {
            	int i = table3.getSelectedRow();
            	if (i >= 0){
            		model3.removeRow(i);
            	}
            	else{
            		System.out.println("Delete Error");
            	}
            }
        
            else if(e.getSource() == sc_delete)
            {
            	int i = table5.getSelectedRow();
            	if (i >= 0){
            		model5.removeRow(i);
            	}
            	else{
            		System.out.println("Delete Error");
            	}
            }
            else if(e.getSource() == en_delete)
            {
            	int i = table6.getSelectedRow();
            	if (i >= 0){
            		model6.removeRow(i);
            	}
            	else{
            		System.out.println("Delete Error");
            	}
            }
            else if(e.getSource() == moveup){
                DefaultTableModel model2 =  (DefaultTableModel)table2.getModel();
                int[] rows2 = table2.getSelectedRows();
                model2.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]-1);
                table2.setRowSelectionInterval(rows2[0]-1, rows2[rows2.length-1]-1);
                //----------------------------------------------------------------------
                DefaultTableModel model =  (DefaultTableModel)table.getModel();
                int[] rows = table2.getSelectedRows();
                model.moveRow(rows[0],rows[rows.length-1],rows[0]-1);
                table.setRowSelectionInterval(rows[0]-1, rows[rows.length-1]-1);
                //----------------------------------------------------------------------
                DefaultTableModel model4 =  (DefaultTableModel)table4.getModel();
                int[] rows4 = table2.getSelectedRows();
                model4.moveRow(rows4[0],rows4[rows4.length-1],rows4[0]-1);
                table4.setRowSelectionInterval(rows4[0]-1, rows4[rows4.length-1]-1);
              //----------------------------------------------------------------------
            }
            else if(e.getSource() == movedown){
                DefaultTableModel model2 =  (DefaultTableModel)table2.getModel();
                int[] rows2 = table2.getSelectedRows();
                model2.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]+1);
                table2.setRowSelectionInterval(rows2[0]+1, rows2[rows2.length-1]+1);
                //------------------------------------------------------------------
                DefaultTableModel model =  (DefaultTableModel)table.getModel();
                int[] rows = table2.getSelectedRows();
                model.moveRow(rows[0],rows[rows.length-1],rows[0]+1);
                table.setRowSelectionInterval(rows[0]+1, rows[rows.length-1]+1);
                //------------------------------------------------------------------
                DefaultTableModel model4 =  (DefaultTableModel)table4.getModel();
                int[] rows4 = table2.getSelectedRows();
                model4.moveRow(rows4[0],rows4[rows4.length-1],rows4[0]+1);
                table4.setRowSelectionInterval(rows4[0]+1, rows4[rows4.length-1]+1);
                //------------------------------------------------------------------
            }
        
            else if(e.getSource() == pl_down){
                DefaultTableModel model3 =  (DefaultTableModel)table3.getModel();
                int[] rows2 = table3.getSelectedRows();
                model3.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]+1);
                table3.setRowSelectionInterval(rows2[0]+1, rows2[rows2.length-1]+1);
            }
            else if(e.getSource() == pl_up){
                DefaultTableModel model3 =  (DefaultTableModel)table3.getModel();
                int[] rows = table3.getSelectedRows();
                model3.moveRow(rows[0],rows[rows.length-1],rows[0]-1);
                table3.setRowSelectionInterval(rows[0]-1, rows[rows.length-1]-1);
            }
            else if(e.getSource() == pl_update){
            	FNC_PL_UPDATE();	
            }
            else if(e.getSource() == sc_down){
                DefaultTableModel model5 =  (DefaultTableModel)table5.getModel();
                int[] rows2 = table5.getSelectedRows();
                model5.moveRow(rows2[0],rows2[rows2.length-1],rows2[0]+1);
                table5.setRowSelectionInterval(rows2[0]+1, rows2[rows2.length-1]+1);
            }
            else if(e.getSource() == sc_up){
                DefaultTableModel model5 =  (DefaultTableModel)table5.getModel();
                int[] rows = table5.getSelectedRows();
                model5.moveRow(rows[0],rows[rows.length-1],rows[0]-1);
                table5.setRowSelectionInterval(rows[0]-1, rows[rows.length-1]-1);
            }
            else if(e.getSource() == sc_update){
            	FNC_SC_UPDATE();		
            }
            else if(e.getSource() == en_update){
            	FNC_EN_UPDATE();
            }

 }
    
    public void FNC_PL_UPDATE(){
    	//DELETE_MARKER();
		double mga =0; double mer = 0 ; double pmr = 0;
		double m_org=0;
    	double m_pl_total = 0;
    	double m_pl_wo = 0;
    	double total_mga = 0;
    	double total_mer = 0;
    	double total_mr = 0 ; 
    	int is_quantity = 0 ; 
    	int ID = 0 ;
    	String name = "";
		String comments = "" ;
		double power1 = 0 ;
		double power2 = 0 ;
    	for (int i = 0; i < model3.getRowCount(); i++) {
    		mga =0; mer = 0 ; pmr = 0;
    		m_org = Double.parseDouble((String) model3.getValueAt(i, 1)) * Double.parseDouble((String) model3.getValueAt(i, 2));
    		if (pl_cb1.isSelected()){
    		mga = Double.parseDouble((String) model3.getValueAt(i, 3));
    		}
    		if (pl_cb2.isSelected()){
    		 mer = Double.parseDouble((String) model3.getValueAt(i, 5));
    		}
    		if (pl_cb3.isSelected()){
    		 pmr = Double.parseDouble((String) model3.getValueAt(i, 7));
    		}
    		//double m_wM = (m_org*(100+mga)/100*(100+mer)/100) + pmr ;
    		double m_wM = m_org + (m_org*(mga)/100+m_org*(mer)/100) + pmr ;
    		table3.setValueAt("" + df.format((m_org*mga/100)) , i, 4);
    		//table3.setValueAt("" + df.format(((m_org+(m_org*mga/100))*mer/100)) , i, 5);
    		table3.setValueAt("" + df.format(((m_org)*mer/100)) , i, 6);
    		is_quantity = Integer.parseInt((String) model3.getValueAt(i, 1));
    		
    		if ((Boolean) model3.getValueAt(i, 9) == true){
    		table3.setValueAt("" + df.format(m_wM) , i, 8);
    		m_pl_total = m_pl_total + m_wM;
    		} else {
        		table3.setValueAt(0 , i, 8);
    		}
    		m_pl_wo = m_pl_wo + m_org;
    		total_mga = total_mga + (m_org*mga/100);
    		total_mer = total_mer + (m_org*mer/100);
    		total_mr = total_mr + Double.parseDouble((String) model3.getValueAt(i, 7));
    		//-------------------------------------------------------------------------------------------------	
    		try {
    		ID = Integer.parseInt( (String) model3.getValueAt(i, 10));
    	    name = (String) model3.getValueAt(i, 0);
    		SubElement newSubElement = new SubElement(ID, name, is_quantity, m_org, mga, mer, pmr, power1, power2, comments);
    		newSubElement.set_ID(ID);
    		newSubElement.set_name(name);
    		newSubElement.set_quantity(is_quantity);
    		newSubElement.setmass_basic(m_org);
    		newSubElement.setmass_MGA(mga);
    		newSubElement.setmass_MER(mer);
    		newSubElement.setmass_PMR(pmr);
    		
    		UPDATE_PAYLOAD_LIST(newSubElement);	
    		} catch (NumberFormatException e113){
    			
    		}
    		//-------------------------------------------------------------------------------------------------	
    		}
    	Mpayload.setText("" + df.format(m_pl_total));
    	pl_10.setText("Total w/o M [kg]: " + df.format(m_pl_wo));
    	pl_11.setText("Total w M [kg]: " + df.format(m_pl_total));
    	pl_12.setText("Total MGA [kg]: " + df.format(total_mga));
    	pl_13.setText("Total MER [kg]: " + df.format(total_mer));
    	pl_14.setText("Total PMR [kg]: " + df.format(total_mr));
	}
	
    
    public void FNC_SC_UPDATE(){
    	//DELETE_MARKER();
		double mga =0; double mer = 0 ; double pmr = 0;
		double m_org=0;
    	int ID = 0 ;
    	String name = "";
    	int is_quantity =0;
		String comments = "" ;
		double power1 = 0 ;
		double power2 = 0 ;
    	double m_sc_total = 0;
    	double m_sc_wo = 0;
    	double total_mga = 0;
    	double total_mer = 0;
    	double total_mr = 0 ; 
    	for (int i = 0; i < model5.getRowCount(); i++) {
			mga =0; mer = 0; pmr = 0;
			is_quantity = Integer.parseInt((String) model5.getValueAt(i, 1));
    		m_org = Double.parseDouble((String) model5.getValueAt(i, 1)) * Double.parseDouble((String) model5.getValueAt(i, 2));
    		if (sc_cb51.isSelected()==true){ 
    				mga = Double.parseDouble((String) sc_TF_02.getText());
    				table5.setValueAt("" + df.format(mga), i, 3);
    		} else if (sc_cb1.isSelected()==true){
    	    		mga = Double.parseDouble((String) model5.getValueAt(i, 3));    				
    		}
    		if (sc_cb61.isSelected()==true){
    				mer = Double.parseDouble((String) sc_TF_03.getText());
    				table5.setValueAt("" + df.format(mer), i, 5);
    		} else if (sc_cb2.isSelected()){
    				mer = Double.parseDouble((String) model5.getValueAt(i, 5));
    		}
    		if (sc_cb3.isSelected() && sc_cb53.isSelected() == false){
    		 pmr = Double.parseDouble((String) model5.getValueAt(i, 7));
    		}
    		if ( exp_mar_plan == true){
    			/*
		    		double m_wM = (m_org + (m_org*(mga)/100)) + (m_org + (m_org*(mga)/100))*(mer)/100 ;
		    		table5.setValueAt("" + df.format((m_org*mga/100)), i, 3);
		    		table5.setValueAt("" + df.format(((m_org + (m_org*(mga)/100))*mer/100)) , i, 5);
		    		if ((Boolean) model5.getValueAt(i, 8) == true){
		    		table5.setValueAt("" + df.format(m_wM) , i, 7);
		    		m_sc_wo = m_sc_wo + m_org;
		    		total_mga = total_mga + (m_org*mga/100);
		    		total_mer = total_mer + ((m_org + (m_org*(mga)/100))*mer/100);
		    		total_mr = total_mr + Double.parseDouble((String) model5.getValueAt(i, 6));
		    		m_sc_total = m_sc_total + m_wM;
		    		} else {
		        		table5.setValueAt(0 , i, 7);
		    		}	
		    		*/
    			double m_wM = (m_org + (m_org*(mga)/100)) + (m_org *(mer)/100) ;
	    		table5.setValueAt("" + df.format((m_org*mga/100)), i, 4);
	    		table5.setValueAt("" + df.format((m_org*mer/100)) , i, 6);
	    		if ((Boolean) model5.getValueAt(i, 9) == true){
	    		table5.setValueAt("" + df.format(m_wM) , i, 8);
	    		m_sc_wo = m_sc_wo + m_org;
	    		total_mga = total_mga + (m_org*mga/100);
	    		total_mer = total_mer + (m_org*mer/100);
	    		total_mr = total_mr + Double.parseDouble((String) model5.getValueAt(i, 7));
	    		m_sc_total = m_sc_total + m_wM;
	    		} else {
	        		table5.setValueAt(0 , i, 8);
	    		}
		    		
    		} else {
    			/*
		    		double m_wM = m_org+ (m_org*(mga)/100+m_org*(mer)/100) + Double.parseDouble((String) model5.getValueAt(i, 6)) ;
		    		table5.setValueAt("" + df.format((m_org*mga/100)), i, 3);
		    		table5.setValueAt("" + df.format((m_org*mer/100)) , i, 5);
		    		if ((Boolean) model5.getValueAt(i, 8) == true){
		    		table5.setValueAt("" + df.format(m_wM) , i, 7);
		    		m_sc_wo = m_sc_wo + m_org;
		    		total_mga = total_mga + (m_org*mga/100);
		    		total_mer = total_mer + (m_org*mer/100);
		    		total_mr = total_mr + Double.parseDouble((String) model5.getValueAt(i, 6));
		    		m_sc_total = m_sc_total + m_wM;
		    		} else {
		        		table5.setValueAt(0 , i, 7);
		    		*/	
	    		double m_wM = m_org+ (m_org*(mga)/100) + (m_org + (m_org*(mga)/100))*(mer)/100  + Double.parseDouble((String) model5.getValueAt(i, 7)) ;
	    		table5.setValueAt("" + df.format((m_org*mga/100)), i, 4);
	    		table5.setValueAt("" + df.format(((m_org + (m_org*(mga)/100))*mer/100)) , i, 6);
	    		if ((Boolean) model5.getValueAt(i, 9) == true){
	    		table5.setValueAt("" + df.format(m_wM) , i, 8);
	    		m_sc_wo = m_sc_wo + m_org;
	    		total_mga = total_mga + (m_org*mga/100);
	    		total_mer = total_mer + ((m_org + (m_org*(mga)/100))*mer/100);
	    		total_mr = total_mr + Double.parseDouble((String) model5.getValueAt(i, 8));
	    		m_sc_total = m_sc_total + m_wM;
	    		} else {
	        		table5.setValueAt(0 , i, 8);
	    		}
    		}
    		
    		//-------------------------------------------------------------------------------------------------	
    		try {
    		ID = Integer.parseInt( (String) model5.getValueAt(i, 10));
    	    name = (String) model5.getValueAt(i, 0);
    		SubElement newSubElement = new SubElement(ID, name, is_quantity,  m_org, mga, mer, pmr, power1, power2, comments);
    		newSubElement.set_ID(ID);
    		newSubElement.set_name(name);
    		newSubElement.set_quantity(is_quantity);
    		newSubElement.setmass_basic(m_org);
    		newSubElement.setmass_MGA(mga);
    		newSubElement.setmass_MER(mer);
    		newSubElement.setmass_PMR(pmr);
    		
    		UPDATE_SC_LIST(newSubElement);	
    		} catch (NumberFormatException e113){
    			
    		}
    		//-------------------------------------------------------------------------------------------------	
    	}
    	cd_tf1.setText("" + df.format(m_sc_total));
    	sc_10.setText("Total w/o M [kg]: " + df.format(m_sc_wo));
    	sc_11.setText("Total w M [kg]: " + df.format(m_sc_total));
    	sc_12.setText("Total MGA [kg]: " + df.format(total_mga));
    	sc_13.setText("Total MER [kg]: " + df.format(total_mer));
    	sc_14.setText("Total PMR [kg]: " + df.format(total_mr));
    	if(sc_cb53.isSelected()){
    	global_pmr = total_mr; 
    	mari_pmr.setBackground(Color.blue);
    	mari_pmr.setForeground(l_c);
    	} else {
    	global_pmr = 0;	
    	mari_pmr.setBackground(l_c);
    	mari_pmr.setForeground(Color.black);
    	}
    	global_sc_wom = m_sc_wo;
    }
    
    public void FNC_EN_UPDATE(){
    	int n_en = model6.getRowCount();
    	String n_name = null ;               
    	double n_isp = 0 ;               
    	double n_cl = 0 ;               
    	String en_int = "";
    	
    	for (int j = 0; j < model2.getRowCount(); j++) {
    	en_int = (String) table2.getModel().getValueAt(j, 8);
		    	for (int i = 0; i < n_en; i++) {
		    	n_name = (String) table6.getModel().getValueAt(i, 0);
				    	if (n_name.equals(en_int)) {
				    	n_isp = Double.parseDouble((String) table6.getModel().getValueAt(i, 1));
				    	n_cl = Double.parseDouble((String) table6.getModel().getValueAt(i, 2));
				    	table2.setValueAt("" + n_isp , j, 2);
				    	table2.setValueAt("" + n_cl , j, 7);
				    	}
		    	}
    	}
    }
    
    public void FNC_POWER_UPDATE(){
    	String string = "";
    	for (int i = 0; i < model5.getRowCount(); i++) {
    		String Element_NAME = (String) table5.getValueAt(i, 0);
    		int Element_QUANTITY = Integer.parseInt((String) table5.getValueAt(i, 1));
    		int Element_ID = Integer.parseInt((String) table5.getValueAt(i, 10));
    		boolean CheckBox =  (boolean) table5.getValueAt(i, 9);
    		boolean element_exists = false; 
			    		for (int j = 0; j < model8.getRowCount(); j++) {
			    			string = (String) table8.getModel().getValueAt(j, 0);	// Table2 | Case Definition | is master table 
			    			if (string.equals(Element_NAME)){
			    				table8.setValueAt(Element_NAME, j, 0);
			    				table8.setValueAt(Element_QUANTITY, j, 1);
			    				table8.setValueAt(Element_ID, j, 8);
			    				if (CheckBox == true) {
			    					table8.setValueAt(true, j, 7);
			    				} else {
			    					table8.setValueAt(false, j, 7);
			    				}
			    				element_exists = true; 
			    			}
			    		}
			if (element_exists == false ){
			Add_Power_ROW(Element_NAME, Element_QUANTITY, Element_ID);
			}
    	}
    }
    
    public void Add_Power_ROW(String Element_NAME, int Element_QUANTITY, int Element_ID){
    	row8[0] = "" + Element_NAME;
    	row8[1] = "" + Element_QUANTITY; 
    	row8[7] = true;
    	row8[8] = "" + Element_ID;	
    	model8.addRow(row8);
    }
    
    public void OVERVIEW_UPDATE(){
    	System.out.println("Overview select");
    	boolean check = false; 
        if (auto_RCS==true){
        	check  = FIND_RCS();
        }
    	if (check) {
    	add2_01.setText("Reaction Control System: RCS found; ISP = " + ISP_STEERING + " seconds");
    	} else {
    		if (auto_RCS == false) {
    	add2_01.setText("Reaction Control System: Manual ISP : " + ISP_STEERING + " seconds");	
    		} else {
    	add2_01.setText("Reaction Control System: No RCS found, default ISP = " + ISP_STEERING + "s");		
    		}
    	}
    	add2_02.setText("Set residual propellant: " + FuelMar_TF.getText() + " %");
    	add2_03.setText("Set residual propellant: " + BoilOff_TF.getText() + " %/day");
    	if (cbMenuItem_BO.isSelected()==true){
    	add2_04.setText("Boil-off included: yes");
    	} else {
    	add2_04.setText("Boil-off included: no");		
    	}
    	if (cbMenuItem_MAR.isSelected()==true){
    	add2_05.setText("Steering included: yes");
    	} else {
    	add2_05.setText("Steering included: no");	
    	}
    	if (cbMenuItem_SL.isSelected()==true){
    	add2_06.setText("Delta-V margin included: yes");
    	} else {
        add2_06.setText("Delta-V margin included: no");
    	}
    	if ( exp_mar_plan == true){
    	add2_07.setText("S/C margin plan: plan_02");
    	} else {
        add2_07.setText("S/C margin plan: plan_01");	
    	}
    	add2_08.setText("Case: " + myfile_name.split("[.]")[0]);

    }
    
    public void Tables_SYNC() {
    	System.out.println("SYNC select");
    	if (model.getRowCount()>0){
    	for (int i = model.getRowCount(); i >= 1; i--) {
    		model.removeRow(i-1);
    	}
    	}
    	if (model4.getRowCount()>0){
    	for (int i = model4.getRowCount(); i >= 1; i--) {
    		model4.removeRow(i-1);
    	}
    	}
    	String string = "";
    	for (int i = 0; i < model2.getRowCount(); i++) {
    		if ((boolean) table2.getModel().getValueAt(i, 9) ==  true ){
    		for (int j = 0; j < 8; j++) {
    			string = (String) table2.getModel().getValueAt(i, j);	// Table2 | Case Definition | is master table 
    			row[j] = string;
    			row4[j] = string;
    		}
    		model.addRow(row);
    		model4.addRow(row4);
    		}
    	} 
    }
    
    public boolean FIND_RCS(){
    	String n_name = null ;
    	boolean found_success = false;
    	double n_isp = 0 ;                  	
    	for (int i = 0; i < model6.getRowCount(); i++) {
    	n_name = (String) table6.getModel().getValueAt(i, 0);
    	if (n_name.equals("RCS")) {
    	n_isp = Double.parseDouble((String) table6.getModel().getValueAt(i, 1));
    	}
    	}
    	if (n_isp == 0){
    		// No RCS found
    		found_success = false;
    	} else {
    		ISP_STEERING = n_isp; 
    		found_success = true;
    	}
    	return found_success; 
    }
    
    public void SaveAs_Case(){
        try {
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    	JFileChooser fileChooser = new JFileChooser(myfile);
    	if (fileChooser.showSaveDialog(purpleButton) == JFileChooser.APPROVE_OPTION) {
    	 // File filer = fileChooser.getSelectedFile();
    	}
        File file = fileChooser.getSelectedFile() ;
        String filePath = file.getAbsolutePath();
        filePath = filePath.replaceAll(".csv", "");
            file = new File(filePath + ".csv");
    		myfile_opened = file;
            myfile_name = fileChooser.getSelectedFile().getName();
            myfile_name = myfile_name.replaceAll(".csv", "");
            FileLabel.setText("CASE : " + myfile_name);
            MAIN_frame.setTitle("" + PROJECT_TITLE + " | " + myfile_name);
    	PrintWriter os;
		try {
			os = new PrintWriter(file);
			String head_line = Minit.getText() + BB_delimiter + Mpayload.getText() + BB_delimiter + cd_tf1.getText() + BB_delimiter + BoilOff_TF.getText() + BB_delimiter + FuelMar_TF.getText() + BB_delimiter + cbMenuItem_BO.isSelected() + BB_delimiter + cbMenuItem_SL.isSelected() + BB_delimiter + cbMenuItem_MAR.isSelected() + BB_delimiter; 
			os.print(head_line);
			os.println("");
        	for (int i = 0; i < model2.getRowCount(); i++) {  // 					DELTA-V TABLE
        		os.print("|DELTAV|" + BB_delimiter);
        	    for (int col = 0; col < model2.getColumnCount(); col++) {
        	        os.print(model2.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model3.getRowCount(); i++) {  // 					Payload TABLE
        		os.print("|PL|" + BB_delimiter);
        	    for (int col = 0; col < model3.getColumnCount(); col++) {
        	        os.print(model3.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model5.getRowCount(); i++) {  // 					S/C TABLE
        		os.print("|SC|" + BB_delimiter);
        	    for (int col = 0; col < model5.getColumnCount(); col++) {;
        	        os.print(model5.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model6.getRowCount(); i++) {  // 					Engine TABLE
        		os.print("|EN|" + BB_delimiter);
        	    for (int col = 0; col < model6.getColumnCount(); col++) {;
        	        os.print(model6.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	
            os.close();
            //System.out.println("Done!");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Error 01");
		}
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("Error 02");
		}
    }
    
    
    public void Save_Case() throws FileNotFoundException{
    	if ( myfile_name.isEmpty()==false) {
        	//File myfile = myfile_opened;
        	//String filePath = myfile.getAbsolutePath();
            File file = myfile_opened ; //new File(filePath + "\\" + myfile_name);
            String Filepath = file.getAbsolutePath(); 
            Filepath = Filepath.replaceAll(".csv","");
            file = new File(Filepath+".csv");
            PrintWriter os;
			os = new PrintWriter(file);
			String head_line = Minit.getText() + BB_delimiter + Mpayload.getText() + BB_delimiter + cd_tf1.getText() + BB_delimiter + BoilOff_TF.getText() + BB_delimiter + FuelMar_TF.getText() + BB_delimiter + cbMenuItem_BO.isSelected() + BB_delimiter + cbMenuItem_SL.isSelected() + BB_delimiter + cbMenuItem_MAR.isSelected() + BB_delimiter;   
			os.print(head_line);
			os.println("");
        	for (int i = 0; i < model2.getRowCount(); i++) {  // 					DELTA-V TABLE
        		os.print("|DELTAV|" + BB_delimiter);
        	    for (int col = 0; col < model2.getColumnCount(); col++) {
        	        os.print(model2.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model3.getRowCount(); i++) {  // 					Payload TABLE
        		os.print("|PL|" + BB_delimiter);
        	    for (int col = 0; col < model3.getColumnCount(); col++) {
        	        os.print(model3.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model5.getRowCount(); i++) {  // 					S/C TABLE
        		os.print("|SC|" + BB_delimiter);
        	    for (int col = 0; col < model5.getColumnCount(); col++) {;
        	        os.print(model5.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}
        	for (int i = 0; i < model6.getRowCount(); i++) {  // 					Engine TABLE
        		os.print("|EN|" + BB_delimiter);
        	    for (int col = 0; col < model6.getColumnCount(); col++) {;
        	        os.print(model6.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}	
            os.close();
    		}
    }
    
    public void Import_Case() {
  	  // sc_cb61.setSelected(false);
 	   //sc_cb51.setSelected(false);
 	   //sc_cb4.setSelected(false);
 	   sc_cb1.setSelected(true);
 	   sc_cb2.setSelected(true);
 	   sc_cb3.setSelected(true);
 	   //sc_cb53.setSelected(true);
 	  // pl_cb_1.setSelected(true);
 	  // sc_cb_1.setSelected(true);
 	   //deltav_cb_1.setSelected(true);
 	   pl_cb1.setSelected(true);
 	   pl_cb2.setSelected(true);
 	   pl_cb3.setSelected(true);
 	  // cbMenuItem_BO.setSelected(false);
 	  // cbMenuItem_MAR.setSelected(true);
 	   //cbMenuItem_SL.setSelected(false);
	   Minit.setText("0");
	   cd_tf1.setText("0");
	   Mpayload.setText("0");
	   //--------------------------------------------------------
    	if (model.getRowCount()>0){
    	for (int i = model.getRowCount(); i >= 1; i--) {
    		model.removeRow(i-1);
    		model2.removeRow(i-1);
    		model4.removeRow(i-1);
    	}
    	}
    	if (model3.getRowCount()>0){
    	for (int i = model3.getRowCount(); i >= 1; i--) {
    		model3.removeRow(i-1);
    	}
    	}
    	if (model5.getRowCount()>0){
    	for (int i = model5.getRowCount(); i >= 1; i--) {
    		model5.removeRow(i-1);
    	}
    	}
    	if (model6.getRowCount()>0){
    	for (int i = model6.getRowCount(); i >= 1; i--) {
    		model6.removeRow(i-1);
    	}
    	}
        try {
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			JFileChooser chooser = new JFileChooser(myfile);
			
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(yellowButton);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	//System.out.println(chooser.getSelectedFile().getName());
        }
    	   try{
    		   //System.out.println(chooser.getSelectedFile().getName());
    		   
    		   String import_file = chooser.getSelectedFile().getName();
    		   myfile_opened = chooser.getSelectedFile();
    		   myfile_name = import_file;
    		   FileLabel.setText("CASE : " + myfile_name.split("[.]")[0]);
    		   MAIN_frame.setTitle("" + PROJECT_TITLE + " | " + myfile_name.split("[.]")[0]);
    		    FileInputStream fstream = new FileInputStream(import_file);
    		          DataInputStream in = new DataInputStream(fstream);
    		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		          String strLine;
    		          int index=0; 
    		          while ((strLine = br.readLine()) != null)   {
    		        	  if (index == 0 ){
    		        		  String[] tokens = strLine.split(BB_delimiter);
    		        		  Minit.setText(tokens[0]);
    		        		  Mpayload.setText(tokens[1]);
    		        		  if (tokens.length>2){
    		        		  cd_tf1.setText(tokens[2]);
    		        		  }
    		        		  if (tokens.length>3){
    		        		  BoilOff_TF.setText(tokens[3]);
    		        		  }
    		        		  if (tokens.length>4){
    		        		  FuelMar_TF.setText(tokens[4]);
    		        		  }
    		        		  if (tokens.length>5){
    		        			  BO_stall = true; 
	    		        		  if (tokens[5].equals("true") ){
	    		        		  cbMenuItem_BO.setSelected(true);
	    		        		  } else {
	    		        		  cbMenuItem_BO.setSelected(false);
	    		        		  }
	    		        		  BO_stall = false; 
    		        		  }
    		        		  if (tokens.length>6){
    		        			  SL_stall = true; 
	    		        		  if (tokens[6].equals("true") ){
	    		        		  cbMenuItem_SL.setSelected(true);
	    		        		  } else {
	    		        		  cbMenuItem_SL.setSelected(false);
	    		        		  }
	    		        		  SL_stall = false; 
    		        		  }
    		        		  if (tokens.length>7){
    		        			  MAR_stall = true; 
	    		        		  if (tokens[7].equals("true") ){
	    		        		  cbMenuItem_MAR.setSelected(true);
	    		        		  }else {
	    		        		  cbMenuItem_MAR.setSelected(false);
	    		        		  }
	    		        		  MAR_stall = false; 
    		        		  }
    		        	  } else {
    		        		String[] tokens = strLine.split(BB_delimiter);
    		        		if (tokens[0].equals("|DELTAV|")) {                 // Delta-V INPUT 		        	  
    		        			for (int i = 0;i<model.getColumnCount();i++){
    		        				if (i<tokens.length-2){	
    		        					row[i] = (String) tokens[i+1];	
    		        				}
    		        			}
    		            	
			        			for (int i = 0;i<model2.getColumnCount();i++){
    		        				if (i<tokens.length-1){	
    		        					row2[i] = (String) tokens[i+1];	
    		        					if (i==5){
    		        						row2[i] = "";	
    		        					}
    		        					if(i == 9 ){
    		        						if(tokens[i+1].equals("true")){
    		        						row2[i] = true;
    		        						} else {
    		        					    row2[i] = false; 
    		        						}
    		        					}
    		        				}
			        			}
    		            	
			        			for (int i = 0;i<model4.getColumnCount();i++){
    		        				if (i<tokens.length-2){	
    		        					row4[i] = (String) tokens[i+1];	
    		        				}	
			        			}
    		            	model.addRow(row);
    		            	model2.addRow(row2);
    		            	model4.addRow(row4);
    		        		} else if (tokens[0].equals("|PL|")) {	 			// Payload INPUT 	
        		            	row3[0] = tokens[1];
        		            	row3[1] = tokens[2];
        		            	row3[2] = tokens[3];
        		            	row3[3] = tokens[4];
        		            	row3[4] = tokens[5];
        		            	row3[5] = tokens[6];
        		            	row3[6] = tokens[7];
        		            	row3[7] = tokens[8];
        		            	row3[8] = tokens[9];
						    	String str_int = tokens[10];
						    	if (str_int.equals("true")) {
						    		row3[9] = true;
						    	} else {
						    		row3[9] = false;
						    	}
						    	if(tokens.length > 10){
						    	row3[10] = tokens[11];
						    	}
        		            	model3.addRow(row3);
    		        		} else if (tokens[0].equals("|SC|")) {					// S/C INPUT 	
        		            	row5[0] = tokens[1];
        		            	row8[0] = tokens[1];
        		            	row5[1] = tokens[2];
        		            	row8[1] = tokens[2];
        		            	row5[2] = tokens[3];
        		            	row5[3] = tokens[4];
        		            	row5[4] = tokens[5];
        		            	row5[5] = tokens[6];
        		            	row5[6] = tokens[7];
        		            	row5[7] = tokens[8];
        		            	row5[8] = tokens[9];
						    	String str_int = tokens[10];
						    	if (str_int.equals("true")) {
						    		row5[9] = true;
						    		row8[7] = true;
						    	} else {
						    		row5[9] = false;
						    		row8[7] = false;
						    	}
						    	if(tokens.length > 10){
						    	row5[10] = tokens[11];
						    	row8[8] = tokens[11];
						    	}
        		            	model5.addRow(row5);
        		            	model8.addRow(row8);
    		        		} else if (tokens[0].equals("|EN|")) {	
			        			for (int i = 0;i<model6.getColumnCount();i++){
    		        				if (i<tokens.length-1){	
    		        					row6[i] = (String) tokens[i+1];	
    		        				}
			        			}
        		            	model6.addRow(row6);
    		        		} else {
    		        			System.out.println("Read Error - Line identifier not found");
    		        		}
    		        	  }
    		            	index++;
    		          }
    		          in.close();
    		          }catch (FileNotFoundException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        FNC_PL_UPDATE();
        FNC_SC_UPDATE();
        FNC_EN_UPDATE();
        FNC_POWER_UPDATE();
        UPDATE_BottomUp();
        UPDATE_TopDown();
    }
    
    public void SaveAs_DeltaV(){
    	int n_rows = model2.getRowCount(); 
    	if ( n_rows > 0 ) {
        try {
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			
    	JFileChooser fileChooser = new JFileChooser(myfile);
    	if (fileChooser.showSaveDialog(purpleButton) == JFileChooser.APPROVE_OPTION) {
    	 // File filer = fileChooser.getSelectedFile();
    	}
        File file = fileChooser.getSelectedFile() ;
        String filePath = file.getAbsolutePath();
            file = new File(filePath + ".csv");
            myfile_name = fileChooser.getSelectedFile().getName();
            FileLabel.setText("CASE : " + myfile_name);
    	PrintWriter os;
		try {
			os = new PrintWriter(file);
			String head_line = Minit.getText() + ";" + Mpayload.getText() + BB_delimiter + cd_tf1.getText() + BB_delimiter + BoilOff_TF.getText() + BB_delimiter + FuelMar_TF.getText() + BB_delimiter + cbMenuItem_BO.isSelected() + BB_delimiter + cbMenuItem_SL.isSelected() + BB_delimiter + cbMenuItem_MAR.isSelected() + BB_delimiter;    
			os.print(head_line);
			os.println("");
        	for (int i = 0; i < model2.getRowCount(); i++) {  // 					DELTA-V TABLE
        		os.print("|DELTAV|" + BB_delimiter);
        	    for (int col = 0; col < model2.getColumnCount(); col++) {
        	        os.print(model2.getValueAt(i, col));
        	        os.print(BB_delimiter);
        	    }
        	    os.println("");
        	}        	
            os.close();
            //System.out.println("Done!");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error File not found");
			e1.printStackTrace();
		}
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("Error URI exception ");
		}
    	}
    }
    
    public void Import_DeltaV() {
    	if (model.getRowCount()>0){
    	for (int i = model.getRowCount(); i >= 1; i--) {
    		model2.removeRow(i-1);
    	}
    	}
        try {
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			JFileChooser chooser = new JFileChooser(myfile);
			
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(yellowButton);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	//System.out.println(chooser.getSelectedFile().getName());
        }
    	   try{
    		   //System.out.println(chooser.getSelectedFile().getName());
    		   String import_file = chooser.getSelectedFile().getName();
    		   myfile_opened = chooser.getCurrentDirectory();
    		   myfile_name = import_file;
    		   //FileLabel.setText("CASE : " + myfile_name);
    		    FileInputStream fstream = new FileInputStream(import_file);
    		          DataInputStream in = new DataInputStream(fstream);
    		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		          String strLine;
    		          int index=0; 
    		          while ((strLine = br.readLine()) != null)   {
    		        	  if (index == 0 ){
    		        		  String[] tokens = strLine.split(BB_delimiter);
    		        		 // Minit.setText(tokens[0]);
    		        		 // Mpayload.setText(tokens[1]);
    		        		  if (tokens.length>2){
    		        		 // cd_tf1.setText(tokens[2]);
    		        		  }
    		        	  } else {
    		        		String[] tokens = strLine.split(BB_delimiter);
    		        		if (tokens[0].equals("|DELTAV|")) {                 // Delta-V INPUT 		        	  
   		            	
			        			for (int i = 0;i<model2.getColumnCount();i++){
    		        				if (i<tokens.length-1){	
    		        					row2[i] = (String) tokens[i+1];	
    		        					if (i==5){
    		        						row2[i] = "";	
    		        					}
    		        					if(i == 9 ){
    		        						if(tokens[i+1].equals("true")){
    		        						row2[i] = true;
    		        						} else {
    		        					    row2[i] = false; 
    		        						}
    		        					}
    		        				}
			        			}
    		            	model2.addRow(row2);
    		        		} else {
    		        			System.out.println("Read Error - Line identifier not found");
    		        		}
    		        	  }
    		            	index++;
    		          }
    		          in.close();
    		          }catch (FileNotFoundException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    }
    
    public void Import_SC() {
    	if (model5.getRowCount()>0){
    	for (int i = model5.getRowCount(); i >= 1; i--) {
    		model5.removeRow(i-1);
    	}
    	}
        try {
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			JFileChooser chooser = new JFileChooser(myfile);
			
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(yellowButton);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	//System.out.println(chooser.getSelectedFile().getName());
        }
    	   try{
    		   //System.out.println(chooser.getSelectedFile().getName());
    		   String import_file = chooser.getSelectedFile().getName();
    		   myfile_opened = chooser.getCurrentDirectory();
    		   myfile_name = import_file;
    		   //FileLabel.setText("CASE : " + myfile_name);
    		    FileInputStream fstream = new FileInputStream(import_file);
    		          DataInputStream in = new DataInputStream(fstream);
    		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		          String strLine;
    		          while ((strLine = br.readLine()) != null)   {
    		        		String[] tokens = strLine.split(",");	
    		        		int drop = 0;
    		        		row5[8] = true;
			        			for (int i = 0;i<(model5.getColumnCount());i++){
		    		        				if (i<tokens.length-1 && i != 8 ){	
		    		        					row5[i] = (String) tokens[i];
		    		        					drop =1;
		    		        				}
			        			}
			        			if (drop == 1 ){
		        				row5[8] = true;
		        				model5.addRow(row5); 
			        			}
    		          }
    		          in.close();
    		          } catch (FileNotFoundException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    }
        
        public void Import_PL(){
        	if (model3.getRowCount()>0){
            	for (int i = model3.getRowCount(); i >= 1; i--) {
            		model3.removeRow(i-1);
            	}
            	}
                try {
                	File myfile;
        			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        			JFileChooser chooser = new JFileChooser(myfile);
        			
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(yellowButton);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                	//System.out.println(chooser.getSelectedFile().getName());
                }
            	   try{
            		   //System.out.println(chooser.getSelectedFile().getName());
            		   String import_file = chooser.getSelectedFile().getName();
            		   myfile_opened = chooser.getCurrentDirectory();
            		   myfile_name = import_file;
            		   //FileLabel.setText("CASE : " + myfile_name);
            		    FileInputStream fstream = new FileInputStream(import_file);
            		          DataInputStream in = new DataInputStream(fstream);
            		          BufferedReader br = new BufferedReader(new InputStreamReader(in));
            		          String strLine;
            		          while ((strLine = br.readLine()) != null)   {
            		        		String[] tokens = strLine.split(BB_delimiter);	
            		        		int drop = 0;
            		        		row3[8] = true;
        			        			for (int i = 0;i<(model3.getColumnCount());i++){
        		    		        				if (i<tokens.length-1 && i != 8 ){	
        		    		        					row3[i] = (String) tokens[i];
        		    		        					drop =1;
        		    		        				}
        			        			}
        			        			if (drop == 1 ){
        		        				row3[8] = true;
        		        				model3.addRow(row3); 
        			        			}
            		          }
            		          in.close();
            		          } catch (FileNotFoundException e1) {
            						// TODO Auto-generated catch block
            						e1.printStackTrace();
            					} catch (IOException e1) {
        						// TODO Auto-generated catch block
        						e1.printStackTrace();
        					}
        		} catch (URISyntaxException e2) {
        			// TODO Auto-generated catch block
        			e2.printStackTrace();
        		}
        }
    
   public void PrepareNewFile(){
	   //-------------------------------------------------------
	   myfile_name = "";
   	if (model.getRowCount()>0){
   	for (int i = model.getRowCount(); i >= 1; i--) {
   		model.removeRow(i-1);
   		model2.removeRow(i-1);
   		model4.removeRow(i-1);
   	}
   	}
   	if (model3.getRowCount()>0){
   	for (int i = model3.getRowCount(); i >= 1; i--) {
   		model3.removeRow(i-1);
   	}
   	}
   	if (model5.getRowCount()>0){
   	for (int i = model5.getRowCount(); i >= 1; i--) {
   		model5.removeRow(i-1);
   	}
   	}
   	if (model6.getRowCount()>0){
   	for (int i = model6.getRowCount(); i >= 1; i--) {
   		model6.removeRow(i-1);
   	}
   	}
   	//-------------------------------------------------------------
	  // sc_cb61.setSelected(false);
	   //sc_cb51.setSelected(false);
	   //sc_cb4.setSelected(false);
	   sc_cb1.setSelected(true);
	   sc_cb2.setSelected(true);
	   sc_cb3.setSelected(true);
	   //sc_cb53.setSelected(true);
	  // pl_cb_1.setSelected(true);
	  // sc_cb_1.setSelected(true);
	   //deltav_cb_1.setSelected(true);
	   pl_cb1.setSelected(true);
	   pl_cb2.setSelected(true);
	   pl_cb3.setSelected(true);
	  // cbMenuItem_BO.setSelected(false);
	  // cbMenuItem_MAR.setSelected(true);
	   //cbMenuItem_SL.setSelected(false);
	   Minit.setText("0");
	   cd_tf1.setText("0");
	   Mpayload.setText("0");
	 //-------------------------------------------------------------
    try {
    	File myfile;
		myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		JFileChooser fileChooser = new JFileChooser(myfile);
		if (fileChooser.showSaveDialog(purpleButton) == JFileChooser.APPROVE_OPTION) {
			// File filer = fileChooser.getSelectedFile();
		}
		File file = fileChooser.getSelectedFile() ;
		String filePath = file.getAbsolutePath();
        file = new File(filePath + ".csv");
		myfile_opened = file;
        myfile_name = fileChooser.getSelectedFile().getName() + ".csv";
        FileLabel.setText("CASE : " + myfile_name);
        MAIN_frame.setTitle("" + PROJECT_TITLE + " | " + myfile_name.split("[.]")[0]);
        PrintWriter os;
	try {
		os = new PrintWriter(file);
		os.close();
        //System.out.println("Done!");
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	} catch (URISyntaxException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
   	
    }
    
    public void ExportBU_PMF2csv() throws FileNotFoundException, URISyntaxException{
       // Double var_x = (double) 0, var_y = (double) 0 ; 
        m_dry = Double.parseDouble(cd_tf1.getText());
     	m_payload = Double.parseDouble(Mpayload.getText());
      	boolean With_DV_margin= false , With_boiloff= false , With_SteeringLosses = false, wet_margin = false  ;
      	if (cbMenuItem_BO.isSelected()==true){
      		With_boiloff = true;
      	}
      	if (cbMenuItem_MAR.isSelected()==true){
      		With_DV_margin = true;
      	}
      	if (cbMenuItem_SL.isSelected()==true){
      		With_SteeringLosses = true;
      	}
    	if (sc_cb4.isSelected() == true){
    		wet_margin = true;
    	}
        double int_per = 0 ; 						// Interim percentage of (given) dry mass 
    	double[] m_dry_int = null ;                 // Interim dry mass 
    	m_dry_int = new double[chart_resolution];
    	double[] m_wet_int = null ;                 // Interim wet mass 
    	m_wet_int = new double[chart_resolution];
    	double[] PMF = null ;                 		// PMF 
    	PMF = new double[chart_resolution];
        	File myfile;
			myfile = new File(BlueBook_main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			
    	JFileChooser fileChooser = new JFileChooser(myfile);
    	if (fileChooser.showSaveDialog(purpleButton) == JFileChooser.APPROVE_OPTION) {
    	 // File filer = fileChooser.getSelectedFile();
    	}
        File file = fileChooser.getSelectedFile();
        String filePath = file.getAbsolutePath();
        if(!filePath.endsWith(".jpg")) {
            file = new File(filePath + ".csv");
        }
    	PrintWriter os;
			os = new PrintWriter(file);
			String head_line = "Dry_Mass_[kg]"  + BB_delimiter + "Wet_Mass_[kg]" + BB_delimiter + "PMF_[percent]"  + BB_delimiter;    
			os.print(head_line);
			os.println("");
        for(int i=0;i<chart_resolution;i++){
        	int_per = (b_u-b_l)/chart_resolution * i + b_l;
        	m_dry_int[i] = int_per * m_dry;
        	m_wet_int[i] = CalcBottomUp_Wet(m_dry_int[i],m_payload, With_DV_margin, With_boiloff, With_SteeringLosses, wet_margin);
        	PMF[i] = (m_wet_int[i]-m_dry_int[i]-m_payload)/(m_wet_int[i]-m_payload);
        	String str_line = String.valueOf(m_dry_int[i]) + BB_delimiter +  String.valueOf(m_wet_int[i]) + BB_delimiter +  String.valueOf(PMF[i]*100)+ BB_delimiter;
        	os.println(str_line);
        	//------------------------------
        	//os.println("");
        }
        os.close();
    }

    private static void createAndShowGUI() throws IOException{

       // JFrame.setDefaultLookAndFeelDecorated(true);
        MAIN_frame = new JFrame("" + PROJECT_TITLE);
        //frame.setLayout(BB_BL);
        //Create and set up the content pane.
        BlueBook_main demo = new BlueBook_main();
        JPanel tp = demo.createContentPane();
        //tp.setPreferredSize(new java.awt.Dimension(x_init, y_init));
        //frame.setContentPane(demo.createContentPane());
        
        MAIN_frame.add(tp, BorderLayout.CENTER);
        MAIN_frame.pack();
        MAIN_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(x_init, y_init);
        MAIN_frame.setVisible(true);
        MAIN_frame.setLocationRelativeTo(null);
        
        BufferedImage myImage = ImageIO.read(url_smalllogo);
        MAIN_frame.setIconImage(myImage);
        
        //new FormResize();
    }

    public static void main(String[] args) throws IOException {
    	  // PAYLOAD_LIST = new ArrayList<SubElement>();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createAndShowGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
    
    public JPanel createSettingsPane () throws IOException{
	   	JPanel MainGUI = new JPanel();
	   	MainGUI = new JPanel();
	   	MainGUI.setLayout(null);
	   	//----------------------------------------------------------------
		JScrollPane TabPanelh = new JScrollPane();
		TabPanelh.setLayout(null);
		TabPanelh.setLocation(5,5);
		TabPanelh.setSize(ext_hx_settings, ext_hy_settings);
		TabPanelh.setBorder(BorderFactory.createLineBorder(Color.black));
		//TabPanel.setBackground(Color.red);
		MainGUI.add(TabPanelh);
		
		JTabbedPane tabbedPaneh = new JTabbedPane();
		tabbedPaneh.setLocation(0, 0);
		tabbedPaneh.setSize(ext_hx_settings, ext_hy_settings);
		//-----------------------------------------------------
		JPanel mainPanelh1 = new JPanel();
		mainPanelh1.setLayout(null);
		mainPanelh1.setLocation(0, 0);
		mainPanelh1.setSize(ext_hx_settings, ext_hy_settings);
		JPanel mainPanelh2 = new JPanel();
		mainPanelh2.setLayout(null);
		mainPanelh2.setLocation(0, 0);
		mainPanelh2.setSize(ext_hx_settings, ext_hy_settings);
		JPanel mainPanelh3 = new JPanel();
		mainPanelh3.setLayout(null);
		mainPanelh3.setLocation(0, 0);
		mainPanelh3.setSize(ext_hx_settings, ext_hy_settings);
		JPanel mainPanelh4 = new JPanel();
		mainPanelh4.setLayout(null);
		mainPanelh4.setLocation(0, 0);
		mainPanelh4.setSize(ext_hx_settings, ext_hy_settings);
		mainPanelh5 = new JPanel();
		mainPanelh5.setLayout(null);
		mainPanelh5.setLocation(0, 0);
		mainPanelh5.setSize(ext_hx_settings, ext_hy_settings);
		
		//----------------------------------------------------------------------------------
		// Panel 3 General 
        JLabel interim_label_l2 = new JLabel("Current work directory: ");
        interim_label_l2.setLocation(5,5);
        interim_label_l2.setSize(550, 25);
        interim_label_l2.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh3.add(interim_label_l2);
        
        
        JLabel interim_label = new JLabel("" + myfile_opened);
        interim_label.setLocation(5,35);
        interim_label.setSize(550, 25);
        interim_label.setFont(labelfont_small);
        interim_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh3.add(interim_label);
        
        
        JLabel Del_title = new JLabel("Set File delimiter");
        Del_title.setLocation(5,67);
        Del_title.setSize(100, 25);
        Del_title.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh3.add(Del_title);
        
        JTextField del_field = new JTextField("" + BB_delimiter);
        del_field.setLocation(2,100);
        del_field.setSize(30, 25);
        del_field.setHorizontalAlignment(0);
        mainPanelh3.add(del_field);
        
        JLabel del_field_label = new JLabel("File Delimiter");
        del_field_label.setLocation(42,100);
        del_field_label.setSize(150, 25);
        del_field_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh3.add(del_field_label);
        

        
        JButton ApplyButton1 = new JButton("Apply");
        ApplyButton1.setLocation(ext_hx_settings-240, ext_hy_settings-100);
        ApplyButton1.setSize(85, 30);
        ApplyButton1.addActionListener(this);
        mainPanelh3.add(ApplyButton1);
        ApplyButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	interim_label.setText("" + myfile_opened);
            	BB_delimiter = del_field.getText();

             } });
        JButton ApplyCloseButton1 = new JButton("Apply and Close");
        ApplyCloseButton1.setLocation(ext_hx_settings-150, ext_hy_settings-100);
        ApplyCloseButton1.setSize(125, 30);
        ApplyCloseButton1.addActionListener(this);
        mainPanelh3.add(ApplyCloseButton1);
        ApplyCloseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	interim_label.setText("" + myfile_opened);
            	BB_delimiter = del_field.getText();
            	
            	frame_settings.dispose();

             } });
        
		//--------------------------------------------------------------------------------
        //        Tab 2 Appearance
		JPanel app_panel = new JPanel();
		app_panel.setLayout(null);
		app_panel.setLocation(5, 5);
		app_panel.setSize(ext_hx_settings, ext_hy_settings);
		mainPanelh1.add(app_panel);
		
        JLabel title_label_02 = new JLabel("Set General Appearance Template");
        title_label_02.setLocation(5,2);
        title_label_02.setSize(200, 25);
        title_label_02.setHorizontalAlignment(0);
        app_panel.add(title_label_02);
		
		
        JRadioButton option1 = new JRadioButton("Bright - Default");
        option1.setForeground(Color.black);
        option1.setLocation(5, 35);
        option1.setSize(150,20);
        JRadioButton option2 = new JRadioButton("Dark");
        option2.setForeground(Color.black);
        option2.setLocation(5, 55);
        option2.setSize(150,20);
        
        if (app_template == 0 ){
        option1.setSelected(true);
        } else if ( app_template == 1 ){
        option2.setSelected(true);
        }
 
        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);

        app_panel.add(option1);
        app_panel.add(option2);
        
        option1.setMnemonic(KeyEvent.VK_C);
        option2.setMnemonic(KeyEvent.VK_M);

        option1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {         
                l_c = new Color(0,0,0);    					// Label Color
               	bc_c = new Color(255,255,255);				// Background Color
               	w_c = new Color(gg,gg,gg);					// Box background color
               	t_c = new Color(255,255,255);				// Table background color
               	CHANGE_APP_TEMPLATE();
               	app_template = 0 ; 
            }           
         });
        
        option2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {         
                l_c = new Color(245,245,245);    			// Label Color
               	bc_c = new Color(95,95,95);					// Background Color
               	w_c = new Color(152,152,152);				// Box background color
               	t_c = new Color(211,211,211);				// Table background color
               	CHANGE_APP_TEMPLATE();
               	app_template = 1 ;
            }           
         });


        //---------------------------------------------------------------------------------------
        //    Tab 2 Chart
        JLabel title_label = new JLabel("Set Chart limits");
        title_label.setLocation(5,2);
        title_label.setSize(100, 25);
        title_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh2.add(title_label);
        
        JTextField bu_field = new JTextField("" + b_u);
        bu_field.setLocation(2,35);
        bu_field.setSize(50, 25);
        bu_field.setHorizontalAlignment(0);
        mainPanelh2.add(bu_field);
        
        JLabel bu_field_label = new JLabel("Upper Limit [%]");
        bu_field_label.setLocation(62,35);
        bu_field_label.setSize(150, 25);
        bu_field_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh2.add(bu_field_label);

        JTextField bl_field = new JTextField("" + b_l);
        bl_field.setLocation(2,65);
        bl_field.setSize(50, 25);
        bl_field.setHorizontalAlignment(0);
        mainPanelh2.add(bl_field);
        
        JLabel bl_field_label = new JLabel("Lower Limit [%]");
        bl_field_label.setLocation(62,65);
        bl_field_label.setSize(150, 25);
        bl_field_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh2.add(bl_field_label);
        
        JLabel title_labeldat = new JLabel("Set number of data points to compute:");
        title_labeldat.setLocation(5,95);
        title_labeldat.setSize(280, 25);
        title_labeldat.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh2.add(title_labeldat);
        
        
        JTextField datdot = new JTextField("" + chart_resolution);
        datdot.setLocation(2,125);
        datdot.setSize(50, 25);
        datdot.setHorizontalAlignment(0);
        mainPanelh2.add(datdot);
        
        JLabel datdot_label = new JLabel("Data points [-]");
        datdot_label.setLocation(62,125);
        datdot_label.setSize(150, 25);
        datdot_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh2.add(datdot_label);
        
        
        JButton ApplyButton = new JButton("Apply");
        ApplyButton.setLocation(ext_hx_settings-240, ext_hy_settings-100);
        ApplyButton.setSize(85, 30);
        ApplyButton.addActionListener(this);
        mainPanelh2.add(ApplyButton);
        ApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	b_u = Double.parseDouble(bu_field.getText());
            	b_l = Double.parseDouble(bl_field.getText());
            	chart_resolution = Integer.valueOf(datdot.getText());
            	UPDATE_BottomUp();
             } });
        JButton ApplyCloseButton = new JButton("Apply and Close");
        ApplyCloseButton.setLocation(ext_hx_settings-150, ext_hy_settings-100);
        ApplyCloseButton.setSize(125, 30);
        ApplyCloseButton.addActionListener(this);
        mainPanelh2.add(ApplyCloseButton);
        ApplyCloseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	b_u = Double.parseDouble(bu_field.getText());
            	b_l = Double.parseDouble(bl_field.getText());
            	chart_resolution = Integer.valueOf(datdot.getText());
            	frame_settings.dispose();
            	UPDATE_BottomUp();
             } });
        
        //------------------------------------------------------------------------
        // Tab 4   Simulation
        
        
        JLabel title_label4 = new JLabel("Set convergence limits");
        title_label4.setLocation(30,2);
        title_label4.setSize(180, 25);
        title_label4.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh4.add(title_label4);
        
        JTextField Convergence = new JTextField("" + Convergence_Criterea);
        Convergence.setLocation(2,35);
        Convergence.setSize(100, 25);
        Convergence.setHorizontalAlignment(0);
        mainPanelh4.add(Convergence);
        
        JLabel Convergence_label = new JLabel("Convergence criteria [-]");
        Convergence_label.setLocation(112,35);
        Convergence_label.setSize(150, 25);
        Convergence_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh4.add(Convergence_label);
        
        JTextField max_it = new JTextField("" + MAX_ITERATION);
        max_it.setLocation(2,65);
        max_it.setSize(100, 25);
        max_it.setHorizontalAlignment(0);
        mainPanelh4.add(max_it);
        
        JLabel max_it_label = new JLabel("Maximum Iterations [-]");
        max_it_label.setLocation(112,65);
        max_it_label.setSize(150, 25);
        max_it_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh4.add(max_it_label);

        JTextField min_it = new JTextField("" + MIN_ITERATION);
        min_it.setLocation(2,95);
        min_it.setSize(100, 25);
        min_it.setHorizontalAlignment(0);
        mainPanelh4.add(min_it);
        
        JLabel min_it_label = new JLabel("Minimum Iterations [-]");
        min_it_label.setLocation(112,95);
        min_it_label.setSize(150, 25);
        min_it_label.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh4.add(min_it_label);
        
        if (auto_RCS==true){
        	FIND_RCS();
        }
        JTextField tf_st_isp = new JTextField("" + ISP_STEERING);
        tf_st_isp.setLocation(2,140);
        tf_st_isp.setSize(100, 25);
        tf_st_isp.setHorizontalAlignment(0);
        mainPanelh4.add(tf_st_isp);
        
        JLabel l_st_isp = new JLabel("Steering ISP [s]");
        l_st_isp.setLocation(112,140);
        l_st_isp.setSize(150, 25);
        l_st_isp.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh4.add(l_st_isp);
        
        JCheckBox auto_rcs = new JCheckBox("Use auto RCS finder");
        auto_rcs.setLocation(240,145);
        auto_rcs.setSize(170, 15);
        auto_rcs.setHorizontalAlignment(0);
        if (auto_RCS == true){
        	auto_rcs.setSelected(true);
        } else {
        	auto_rcs.setSelected(false);
        }
        auto_rcs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(auto_rcs.isSelected()){
            		auto_RCS = true ;
            	} else { 
            		auto_RCS = false;
            	}	
             } });
        mainPanelh4.add(auto_rcs);
 
        JButton ApplyButton4 = new JButton("Apply");
        ApplyButton4.setLocation(ext_hx_settings-240, ext_hy_settings-100);
        ApplyButton4.setSize(85, 30);
        ApplyButton4.addActionListener(this);
        mainPanelh4.add(ApplyButton4);
        ApplyButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Convergence_Criterea = Double.parseDouble(Convergence.getText());
            	MAX_ITERATION = Integer.valueOf(max_it.getText());
            	MIN_ITERATION = Integer.valueOf(min_it.getText());
            	ISP_STEERING = Double.valueOf(tf_st_isp.getText());
            	
            	UPDATE_BottomUp();
             } });
        JButton ApplyCloseButton4 = new JButton("Apply and Close");
        ApplyCloseButton4.setLocation(ext_hx_settings-150, ext_hy_settings-100);
        ApplyCloseButton4.setSize(125, 30);
        ApplyCloseButton4.addActionListener(this);
        mainPanelh4.add(ApplyCloseButton4);
        ApplyCloseButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Convergence_Criterea = Double.parseDouble(Convergence.getText());
            	MAX_ITERATION = Integer.valueOf(max_it.getText());
            	MIN_ITERATION = Integer.valueOf(min_it.getText());
            	ISP_STEERING = Double.valueOf(tf_st_isp.getText());
            	
            	frame_settings.dispose();
            	UPDATE_BottomUp();
             } });
      //------------------------------------------------------------------------
        
        JTextField tf5_1 = new JTextField("" + d);
        tf5_1.setLocation(2,5);
        tf5_1.setSize(100, 25);
        tf5_1.setHorizontalAlignment(0);
        tf5_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	d = Double.valueOf(tf5_1.getText());
            	if (chart16_fd == true){
        	        result16.removeAllSeries();
        			AddDataset_16();
        	        CreateChart_16();
            	} else{
        	        result16.removeAllSeries();
        			AddDataset_16();
            	}
            } });
        mainPanelh5.add(tf5_1);
        
        JLabel l5_1 = new JLabel("Lunar Lander Trimming Parameter (d) [-]");
        l5_1.setLocation(112,5);
        l5_1.setSize(350, 25);
        l5_1.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh5.add(l5_1);
        
        
        JCheckBox d_default = new JCheckBox("Default");
        d_default.setLocation(360,5);
        d_default.setSize(130, 15);
        d_default.setHorizontalAlignment(0);
        d_default.setSelected(false);
        d_default.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(d_default.isSelected()){
            		d = 0.8387636;
            		store_user_input = Double.valueOf(tf5_1.getText());
            		tf5_1.setText("" + d);
            	} else { 
            		tf5_1.setText("" + store_user_input);
            		d = Double.valueOf(tf5_1.getText());
            	}
            	// Update Plot 16
            	if (chart16_fd == true){
        	        result16.removeAllSeries();
        			AddDataset_16();
        	        CreateChart_16();
            	} else{
        	        result16.removeAllSeries();
        			AddDataset_16();
            	}
             } });
        mainPanelh5.add(d_default);
        
        
        JCheckBox d_cap = new JCheckBox("Use PMF-limit (cap)");
        d_cap.setLocation(5,ext_hy_settings-90);
        d_cap.setSize(250, 15);
        d_cap.setHorizontalAlignment(0);
        d_cap.setSelected(true);
        d_cap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(d_cap.isSelected()){
            		b_cap = true;
            	} else { 
            		b_cap = false;
            	}
            	// Update Plot 16
            	if (chart16_fd == true){
        	        result16.removeAllSeries();
        			AddDataset_16();
        	        CreateChart_16();
            	} else{
        	        result16.removeAllSeries();
        			AddDataset_16();
            	}
             } });
        mainPanelh5.add(d_cap);
        
        
        JButton ApplyButton5 = new JButton("Apply");
        ApplyButton5.setLocation(ext_hx_settings-240, ext_hy_settings-100);
        ApplyButton5.setSize(85, 30);
        ApplyButton5.addActionListener(this);
        mainPanelh5.add(ApplyButton5);
        ApplyButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
            	//d_default.setSelected(false);
            	d = Double.valueOf(tf5_1.getText());
            	store_user_input = Double.valueOf(tf5_1.getText());
            	
            	if (chart16_fd == true){
        	        result16.removeAllSeries();
        			AddDataset_16();
        	        CreateChart_16();
            	} else{
        	        result16.removeAllSeries();
        			AddDataset_16();
            	}
            	
             } });
        JButton ApplyCloseButton5 = new JButton("Apply and Close");
        ApplyCloseButton5.setLocation(ext_hx_settings-150, ext_hy_settings-100);
        ApplyCloseButton5.setSize(125, 30);
        ApplyCloseButton5.addActionListener(this);
        mainPanelh5.add(ApplyCloseButton5);
        ApplyCloseButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	d = Double.valueOf(tf5_1.getText());
            	
            	if (chart16_fd == true){
        	        result16.removeAllSeries();
        			AddDataset_16();
        	        CreateChart_16();
            	} else{
        	        result16.removeAllSeries();
        			AddDataset_16();
            	}
            	frame_settings.dispose();
             } });
        
    	if (chart16_fd == true){
	        result16.removeAllSeries();
			AddDataset_16();
	        CreateChart_16();
    	} else{
	        result16.removeAllSeries();
			AddDataset_16();
    	}
	    //------------------------------------------------------------------------
		tabbedPaneh.addTab("General" , null, mainPanelh3, null);
		tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_3);
		tabbedPaneh.addTab("Appearance" , null, mainPanelh1, null);
		tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPaneh.addTab("Charts" , null, mainPanelh2, null);
		tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_2);
		tabbedPaneh.addTab("Simulation" , null, mainPanelh4, null);
		tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_4);
		tabbedPaneh.addTab("Scale-Sim" , null, mainPanelh5, null);
		tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_4);
	    
		tabbedPaneh.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	    TabPanelh.add(tabbedPaneh);

	   	
	   	//----------------------------------------------------------------
	    MainGUI.setOpaque(true);
	    return MainGUI;
	   	
	   }
    
    public JPanel createAddPropulsionSystemPane () throws IOException{
	   	JPanel MainGUI = new JPanel();
	   	MainGUI = new JPanel();
	   	MainGUI.setLayout(null);
	   	//----------------------------------------------------------------
		JScrollPane TabPanelh = new JScrollPane();
		TabPanelh.setLayout(null);
		TabPanelh.setLocation(5,5);
		TabPanelh.setSize(ext_hx_settings, ext_hy_settings);
		TabPanelh.setBorder(BorderFactory.createLineBorder(Color.black));
		//TabPanel.setBackground(Color.red);
		MainGUI.add(TabPanelh);
		
		JTabbedPane tabbedPaneh = new JTabbedPane();
		tabbedPaneh.setLocation(0, 0);
		tabbedPaneh.setSize(ext_hx_settings, ext_hy_settings);
		//-----------------------------------------------------
		JPanel mainPanelh1 = new JPanel();
		mainPanelh1.setLayout(null);
		mainPanelh1.setLocation(0, 0);
		mainPanelh1.setSize(ext_hx_settings, ext_hy_settings);
		JPanel mainPanelh2 = new JPanel();
		mainPanelh2.setLayout(null);
		mainPanelh2.setLocation(0, 0);
		mainPanelh2.setSize(ext_hx_settings, ext_hy_settings);
		

		//----------------------------------------------------------------------------------
		// Panel 1 General 
        JLabel l_element = new JLabel(" Element ");
        l_element.setLocation(70, 3);
        l_element.setSize(80, 25);
        l_element.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_element);


        JTextField tf_element = new JTextField("Propulsion Element");
        tf_element.setLocation(155,3);
        tf_element.setSize(180, 25);
        //tf_element.setBackground(Color.gray);
        tf_element.setForeground(Color.red);
        tf_element.setHorizontalAlignment(0);
        mainPanelh1.add(tf_element);
		
	    
        JLabel l_of = new JLabel("O/F [-]");
        l_of.setLocation(185,50);
        l_of.setSize(50, 25);
        l_of.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_of);

        JTextField tf_of = new JTextField("" + DATA_OF[0][0]);
        tf_of.setLocation(235,50);
        tf_of.setSize(40, 25);
        tf_of.setHorizontalAlignment(0);
        mainPanelh1.add(tf_of);
        
        JLabel l_isp = new JLabel(" ISP [s]");
        l_isp.setLocation(280,50);
        l_isp.setSize(50, 25);
        l_isp.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_isp);

        JTextField tf_isp = new JTextField("" + DATA_ISP[0][0]);
        tf_isp.setLocation(330,50);
        tf_isp.setSize(40, 25);
        tf_isp.setHorizontalAlignment(0);
        mainPanelh1.add(tf_isp);
        
        JLabel l_pl = new JLabel(" Propellant loss per start-up [kg]");
        l_pl.setLocation(5,120);
        l_pl.setSize(200, 25);
        l_pl.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_pl);
        JTextField tf_pl = new JTextField("0");
        tf_pl.setLocation(210,120);
        tf_pl.setSize(40, 25);
        tf_pl.setHorizontalAlignment(0);
        mainPanelh1.add(tf_pl);
        
        JLabel l_ppl = new JLabel(" Pressurant loss per start-up [kg]");
        l_ppl.setLocation(5,160);
        l_ppl.setSize(200, 25);
        l_ppl.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_ppl);
        JTextField tf_ppl = new JTextField("0");
        tf_ppl.setLocation(210,160);
        tf_ppl.setSize(40, 25);
        tf_ppl.setHorizontalAlignment(0);
        mainPanelh1.add(tf_ppl);
        
        
        JLabel l_dox = new JLabel(" Density Oxidizer [kg/m\u00b3]");
        l_dox.setLocation(5,200);
        l_dox.setSize(200, 25);
        l_dox.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_dox);
        JTextField tf_dox = new JTextField("" + DATA_DENSITY_OXIDIZER[0]);
        tf_dox.setLocation(210,200);
        tf_dox.setSize(55, 25);
        tf_dox.setHorizontalAlignment(0);
        mainPanelh1.add(tf_dox);
        
        JLabel l_dfuel = new JLabel(" Density Fuel [kg/m\u00b3]");
        l_dfuel.setLocation(5,230);
        l_dfuel.setSize(200, 25);
        l_dfuel.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_dfuel);
        JTextField tf_dfuel = new JTextField("" + DATA_DENSITY_FUEL[0]);
        tf_dfuel.setLocation(210,230);
        tf_dfuel.setSize(55, 25);
        tf_dfuel.setHorizontalAlignment(0);
        mainPanelh1.add(tf_dfuel);
        
        
        JLabel l_ox = new JLabel("Oxidizer");
        l_ox.setLocation(5,40);
        l_ox.setSize(70, 25);
        l_ox.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_ox);
        
       // int ox_row = 0; 
       // int fuel_col = 0; 
        
	    final JComboBox<String> cb_ox = new JComboBox<String>(choices_ox);
	    cb_ox.setVisible(true);
	    cb_ox.setSize(100, 25);
	    cb_ox.setLocation(75, 40);
	    cb_ox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            for(int i=0;i<choices_ox.length;i++){
            	if(cb_ox.getSelectedItem().equals(choices_ox[i])){
            	 ox_row = i ; 
            	}
            }
//System.out.println("" + ox_row);
tf_of.setText("" + DATA_OF[ox_row][fuel_col]);
tf_isp.setText("" + DATA_ISP[ox_row][fuel_col]);
tf_dox.setText("" + DATA_DENSITY_OXIDIZER[ox_row]);
tf_dfuel.setText("" + DATA_DENSITY_FUEL[fuel_col]);
             } });
	    mainPanelh1.add(cb_ox);

        JLabel l_fuel = new JLabel("Fuel");
        l_fuel.setLocation(5,70);
        l_fuel.setSize(70, 25);
        l_fuel.setHorizontalAlignment(SwingConstants.LEFT);
        mainPanelh1.add(l_fuel);

	    final JComboBox<String> cb_fuel = new JComboBox<String>(choices_fuel);
	    cb_fuel.setVisible(true);
	    cb_fuel.setSize(100, 25);
	    cb_fuel.setLocation(75, 70);
	    cb_fuel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(int i=0;i<choices_fuel.length;i++){
                	if(cb_fuel.getSelectedItem().equals(choices_fuel[i])){
                		fuel_col = i ; 
                	}
                }
 //System.out.println("" + fuel_col);
    tf_of.setText("" + DATA_OF[ox_row][fuel_col]);
    tf_isp.setText("" + DATA_ISP[ox_row][fuel_col]);
    tf_dox.setText("" + DATA_DENSITY_OXIDIZER[ox_row]);
    tf_dfuel.setText("" + DATA_DENSITY_FUEL[fuel_col]);
             } });
	    mainPanelh1.add(cb_fuel);
		
        //............................................................................
        JButton ApplyButton4 = new JButton("Add");
        ApplyButton4.setLocation(ext_hx_settings-240, ext_hy_settings-100);
        ApplyButton4.setSize(85, 30);
        ApplyButton4.addActionListener(this);
        mainPanelh1.add(ApplyButton4);
        ApplyButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	row6[0] = tf_element.getText();
            	row6[1] = tf_isp.getText();
            	row6[2] = tf_pl.getText();
            	row6[3] = "";
            	row6[4] = tf_of.getText();
            	row6[5] = tf_dfuel.getText();
            	row6[6] = tf_dox.getText();
            	row6[7] = tf_ppl.getText();
            	model6.addRow(row6);
            	FNC_EN_UPDATE();
             } });
        JButton ApplyCloseButton4 = new JButton("Add and Close");
        ApplyCloseButton4.setLocation(ext_hx_settings-150, ext_hy_settings-100);
        ApplyCloseButton4.setSize(125, 30);
        ApplyCloseButton4.addActionListener(this);
        mainPanelh1.add(ApplyCloseButton4);
        ApplyCloseButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	row6[0] = tf_element.getText();
            	row6[1] = tf_isp.getText();
            	row6[2] = tf_pl.getText();
            	row6[3] = "";
            	row6[4] = tf_of.getText();
            	row6[5] = tf_dfuel.getText();
            	row6[6] = tf_dox.getText();
            	row6[7] = tf_ppl.getText();
            	model6.addRow(row6);
            	frame_AddEngine.dispose();
            	FNC_EN_UPDATE();
             } });

	    //------------------------------------------------------------------------
		tabbedPaneh.addTab("General" , null, mainPanelh1, null);
		tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_3);
		//tabbedPaneh.addTab("-" , null, mainPanelh2, null);
		//tabbedPaneh.setMnemonicAt(0, KeyEvent.VK_1);

	    
		tabbedPaneh.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	    TabPanelh.add(tabbedPaneh);

	   	
	   	//----------------------------------------------------------------
	    MainGUI.setOpaque(true);
	    return MainGUI;
	   	
	   }
    
    
    
    
public void CHANGE_APP_TEMPLATE(){
tabbedPane.setForeground(l_c);	
subtabPane11.setForeground(l_c);
resPanel.setBackground(w_c);
resPanel.setForeground(l_c);
mainPanel1.setBackground(bc_c);
mainPanel2.setBackground(bc_c);
mainPanel3.setBackground(bc_c);
mainPanel1.setForeground(l_c);
mainPanel2.setForeground(l_c);
mainPanel3.setForeground(l_c);
CasePanel.setBackground(w_c);
CasePanel.setForeground(l_c);
mar_pan_pl.setBackground(w_c);
mar_pan_pl.setForeground(l_c);;
mar_pan_sc.setBackground(w_c);
mar_pan_sc.setForeground(l_c);
resPanel2.setBackground(w_c);
resPanel2.setForeground(l_c);;
add_opt_panel.setBackground(w_c);
add_opt_panel.setForeground(l_c);
add_opt_panel2.setBackground(w_c);
add_opt_panel2.setForeground(l_c);
//add_opt_panel3.setBackground(w_c);
//add_opt_panel3.setForeground(l_c);
sum_title.setForeground(l_c);
add2_01.setForeground(l_c);
add2_01.setForeground(l_c);
add2_02.setForeground(l_c);
add2_03.setForeground(l_c);
add2_04.setForeground(l_c);
add2_05.setForeground(l_c);
add2_06.setForeground(l_c);
add2_07.setForeground(l_c);
add_panel.setBackground(w_c);
add_panel.setForeground(l_c);
resPanel41.setBackground(w_c);
resPanel41.setForeground(l_c);
TabPanel.setBackground(bc_c);
P1_page01.setBackground(bc_c);
P1_page02.setBackground(bc_c); 
P1_page03.setBackground(bc_c); 
mari_bo.setBackground(w_c);
mari_sl.setBackground(w_c);
mari_mar.setBackground(w_c);
mari_bo2.setBackground(w_c);
mari_sl2.setBackground(w_c);
mari_mar2.setBackground(w_c);
rp2_1.setForeground(l_c);
rp2_2.setForeground(l_c);
rp2_3.setForeground(l_c);
rp2_4.setForeground(l_c);
rp2_5.setForeground(l_c);
rp2_6.setForeground(l_c);
rp2_7.setForeground(l_c);
rp2_8.setForeground(l_c);
rp2_9.setForeground(l_c);
rp2_10.setForeground(l_c);
FileLabel.setForeground(l_c);
pg_l8.setForeground(l_c);
mari_pmr.setForeground(l_c);
rp4_1.setBackground(bc_c);
rp4_1.setForeground(l_c);
rp4_2.setBackground(bc_c);
rp4_3.setBackground(bc_c);
rp4_4.setBackground(bc_c);
rp4_5.setBackground(bc_c);
rp4_6.setBackground(bc_c);
rp4_7.setBackground(bc_c);
rp4_8.setBackground(bc_c);
rp4_9.setBackground(bc_c);
rp4_10.setBackground(bc_c);
rp4_11.setBackground(bc_c);
rp4_12.setBackground(bc_c);
rp4_13.setBackground(bc_c);
rp4_14.setBackground(bc_c);
rp4_15.setBackground(bc_c);
rp4_16.setBackground(bc_c);
rp4_17.setBackground(bc_c);
rp4_18.setBackground(bc_c);
rp4_21.setBackground(bc_c);
rp4_22.setBackground(bc_c);
rp4_2.setForeground(l_c);
rp4_3.setForeground(l_c);
rp4_4.setForeground(l_c);
rp4_5.setForeground(l_c);
rp4_6.setForeground(l_c);
rp4_7.setForeground(l_c);
rp4_8.setForeground(l_c);
rp4_9.setForeground(l_c);
rp4_10.setForeground(l_c);
rp4_11.setForeground(l_c);
rp4_12.setForeground(l_c);
rp4_13.setForeground(l_c);
rp4_14.setForeground(l_c);
rp4_15.setForeground(l_c);
rp4_16.setForeground(l_c);
rp4_17.setForeground(l_c);
rp4_18.setForeground(l_c);
rp4_21.setForeground(l_c);
rp4_22.setForeground(l_c);
plotsub_21.setBackground(Color.white); 
plotsub_22.setBackground(Color.white); 
plotsub_31.setBackground(Color.white);
plotsub_32.setBackground(Color.white);
plotsub_41.setBackground(Color.white); 
plotsub_42.setBackground(Color.white);
plotsub_43.setBackground(Color.white);
plotsub_44.setBackground(Color.white);
plotsub_45.setBackground(Color.white);
plotsub_46.setBackground(Color.white);
plotsub_47.setBackground(Color.white);
plotsub_421.setBackground(Color.white);
plotsub_422.setBackground(Color.white);
Linp1.setForeground(l_c);
Linp2.setForeground(l_c);
Lres1.setForeground(l_c);
Lres2.setForeground(l_c);
Lres3.setForeground(l_c);
Lres4.setForeground(l_c);
Res1.setForeground(l_c);
Res2.setForeground(l_c);
Res3.setForeground(l_c);
Res4.setForeground(l_c);
boiloff_label.setForeground(l_c);
boiloff_label2.setForeground(l_c);
steering_label.setForeground(l_c);
margin_label.setForeground(l_c);
FuelMar_label.setForeground(l_c);
FuelMar_label2.setForeground(l_c);
boiloff_label4.setForeground(l_c);
steering_label4.setForeground(l_c);
margin_label4.setForeground(l_c);
Res41.setForeground(l_c);
Res42.setForeground(l_c);
Res43.setForeground(l_c);
Res44.setForeground(l_c);
Res45.setForeground(l_c);
cd_l4.setForeground(l_c);
cd_l5.setForeground(l_c);
cd_l6.setForeground(l_c);
cd_l7.setForeground(l_c);
sc_1.setForeground(l_c);
sc_2.setForeground(l_c);
sc_3.setForeground(l_c);
sc_4.setForeground(l_c);
sc_5.setForeground(l_c);
sc_6.setForeground(l_c);
sc_7.setForeground(l_c);
sc_8.setForeground(l_c);
sc_9.setForeground(l_c);
sc_10.setForeground(l_c);
sc_11.setForeground(l_c);
sc_12.setForeground(l_c);
sc_13.setForeground(l_c);
sc_14.setForeground(l_c);
pl_2.setForeground(l_c);
pl_3.setForeground(l_c);
pl_4.setForeground(l_c);
en_1.setForeground(l_c);
Lres5.setForeground(l_c);

tabbedPane.setBackground(bc_c);; 		// Main Tabbed pane
subtabPane11.setBackground(bc_c);;       // Subtabbed pane - for Pane 1 Input definition
subtabPane1.setBackground(Color.white);; 		// Subtabbed pane - Plot area in pane 2  XY chart
subtabPane3.setBackground(Color.white);; 		// Subtabbed pane2 - Plot area in pane 2 bar chart
subtabPane4.setBackground(Color.white); 		// Subtabbed pane - Plot area in pane 3  XY chart
subtabPane41.setBackground(Color.white);; 		// Subtabbed pane - Plot area in pane 3 bar chart

table.setBackground(t_c);
table2.setBackground(t_c);
table3.setBackground(t_c);
table4.setBackground(t_c);
table5.setBackground(t_c);
table6.setBackground(t_c);
table7.setBackground(t_c);
/*
redButton 
blueButton
resetButton
greenButton
purpleButton
yellowButton
refButton
sc_mark
sc_unmark
moveup
movedown
button_int
deltav_load
pl_add
pl_delete
pl_up
pl_down
pl_update
pl_load
sc_add
sc_delete
sc_up
sc_down
sc_update
sc_load
en_add
en_delete
en_update
Minit
Mpayload
BoilOff_TF
FuelMar_TF
sc_TF_01
sc_TF_02
sc_TF_03
rp4_20
cd_tf1
pg_cb1
pg_t2
pg_t3
pg_t4
pg_t5
pg_t6
*/
}

protected static FileSystemView fsv = FileSystemView.getFileSystemView();

/**
 * Renderer for the file tree.
 * 
 * @author Kirill Grouchnikov
 */
private static class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Icon cache to speed the rendering.
	 */
	private Map<String, Icon> iconCache = new HashMap<String, Icon>();

	/**
	 * Root name cache to speed the rendering.
	 */
	private Map<File, String> rootNameCache = new HashMap<File, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		FileTreeNode ftn = (FileTreeNode) value;
		File file = ftn.file;
		String filename = "";
		if (file != null) {
			if (ftn.isFileSystemRoot) {
				// long start = System.currentTimeMillis();
				filename = this.rootNameCache.get(file);
				if (filename == null) {
					filename = fsv.getSystemDisplayName(file);
					this.rootNameCache.put(file, filename);
				}
				// long end = System.currentTimeMillis();
				// System.out.println(filename + ":" + (end - start));
			} else {
				filename = file.getName();
			}
		}
		JLabel result = (JLabel) super.getTreeCellRendererComponent(tree,
				filename, sel, expanded, leaf, row, hasFocus);
		if (file != null) {
			Icon icon = this.iconCache.get(filename);
			if (icon == null) {
				// System.out.println("Getting icon of " + filename);
				icon = fsv.getSystemIcon(file);
				this.iconCache.put(filename, icon);
			}
			result.setIcon(icon);
		}
		return result;
	}
}

/**
 * A node in the file tree.
 * 
 * @author Kirill Grouchnikov
 */
private static class FileTreeNode implements TreeNode {
	/**
	 * Node file.
	 */
	private File file;

	/**
	 * Children of the node file.
	 */
	private File[] children;

	/**
	 * Parent node.
	 */
	private TreeNode parent;

	/**
	 * Indication whether this node corresponds to a file system root.
	 */
	private boolean isFileSystemRoot;

	/**
	 * Creates a new file tree node.
	 * 
	 * @param file
	 *            Node file
	 * @param isFileSystemRoot
	 *            Indicates whether the file is a file system root.
	 * @param parent
	 *            Parent node.
	 */
	public FileTreeNode(File file, boolean isFileSystemRoot, TreeNode parent) {
		this.file = file;
		this.isFileSystemRoot = isFileSystemRoot;
		this.parent = parent;
		this.children = this.file.listFiles();
		if (this.children == null)
			this.children = new File[0];
	}

	/**
	 * Creates a new file tree node.
	 * 
	 * @param children
	 *            Children files.
	 */
	public FileTreeNode(File[] children) {
		this.file = null;
		this.parent = null;
		this.children = children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#children()
	 */
	public Enumeration<?> children() {
		final int elementCount = this.children.length;
		return new Enumeration<File>() {
			int count = 0;

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Enumeration#hasMoreElements()
			 */
			public boolean hasMoreElements() {
				return this.count < elementCount;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Enumeration#nextElement()
			 */
			public File nextElement() {
				if (this.count < elementCount) {
					return FileTreeNode.this.children[this.count++];
				}
				throw new NoSuchElementException("Vector Enumeration");
			}
		};

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getChildAt(int)
	 */
	public TreeNode getChildAt(int childIndex) {
		return new FileTreeNode(this.children[childIndex],
				this.parent == null, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	public int getChildCount() {
		return this.children.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	public int getIndex(TreeNode node) {
		FileTreeNode ftn = (FileTreeNode) node;
		for (int i = 0; i < this.children.length; i++) {
			if (ftn.file.equals(this.children[i]))
				return i;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	public TreeNode getParent() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return (this.getChildCount() == 0);
	}
}

}

