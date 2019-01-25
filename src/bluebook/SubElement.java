package bluebook;


public class SubElement {
	
	private int ID;
	private String name; 
	private int quantity;
	private double mass_basic;
	private double mass_MGA;
	private double mass_MER;
	private double mass_PMR;
	private double power_consumption;
	private double power_margin;
	
	private String comments;
	
	
	public SubElement(int ID, String name, int quantity, double mass_basic, double mass_MGA, double mass_MER, double mass_PMR, double power_consumption, double power_margin, String comments){
		this.ID = ID;
		this.name = name; 
		this.quantity = quantity; 
		this.mass_basic = mass_basic;
		this.mass_MGA = mass_MGA;
		this.mass_MER = mass_MER;
		this.mass_PMR = mass_PMR;
		this.power_consumption = power_consumption;
		this.power_margin = power_margin; 
		
		this.comments = comments; 
	}


	public int get_ID() {
		return ID;
	}
	
	public String get_name() {
		return name;
	}
	
	public int get_quantity(){
		return quantity; 
	}
	
	public double getmass_basic() {
		return mass_basic;
	}
	
	public double getmass_MGA() {
		return mass_MGA;
	}
	
	public double getmass_MER() {
		return mass_MER;
	}
	
	public double getmass_PMR() {
		return mass_PMR;
	}
	
	public double getpower_consumption() {
		return power_consumption;
	}
	
	public double getpower_margin() {
		return power_margin;
	}
	
	public String getcomments() {
		return comments; 
	}
	
	
	public void set_ID(int NewValue){
		ID = NewValue;
	}
	
	public void set_name(String NewValue){
		name = NewValue; 
	}
	
	public void set_quantity(int NewValue){
		quantity = NewValue; 
	}
	
	public void setmass_basic(double NewValue){
		mass_basic = NewValue; 
	}
	
	public void setmass_MGA(double NewValue){
		mass_MGA = NewValue;
	}
	
	public void setmass_MER(double NewValue){
		mass_MER = NewValue;
	}
	
	public void setmass_PMR(double NewValue){
		mass_PMR = NewValue;
	}
	
	public void setpower_consumption(double NewValue){
		power_consumption = NewValue;
	}
	
	public void setpower_margin(double NewValue){
		power_margin = NewValue;
	}
	
	public void setcomments(String NewValue){
		comments = NewValue;
	}
	
	
	
}