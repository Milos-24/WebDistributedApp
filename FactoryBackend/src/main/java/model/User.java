package model;

public class User {

	public String companyName;
	public String address;
	public long phoneNumber;
	public String username;
	public String password;
	public boolean activated=false;
	public boolean blocked=false;

	
	public User()
	{
		
	}
	
	public User(String companyName, String address, long phoneNumber, String username, String password, boolean activated) {
		super();
		this.companyName = companyName;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.username = username;
		this.password = password;
		this.activated = activated;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	@Override
	public String toString() {
		return "companyName=" + companyName + ",address=" + address + ",phoneNumber=" + phoneNumber
				+ ",username=" + username + ",password=" + password + ",activated=" + activated + ",blocked=" + blocked;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setActivated(boolean activated)
	{
		this.activated=activated;
	}
	public void setBlocked(boolean blocked)
	{
		this.blocked=blocked;
	}
	
}
