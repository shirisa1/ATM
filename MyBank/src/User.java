
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;;

public class User {
	
	private String firstName;
	private String lastName;
	private String uuid;
	private byte pinHash[];
	private ArrayList<Account> accounts;
	
	
	public User(String firstName, String lastName, String pin, Bank theBank) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		
		
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				this.pinHash = md.digest(pin.getBytes());
			} catch (NoSuchAlgorithmException e) {
				System.err.println("error, No Algorithm exception");
				e.printStackTrace();
				System.exit(1);
			}
			
			this.uuid = theBank.getNewUserUUID();
			this.accounts = new ArrayList<Account>();
			
			System.out.printf("New User Profile for %s %s with ID %s is created.\n", firstName,lastName,this.uuid);;
			System.out.println("\n");
		
		
	}


	public void addAccount(Account account) {
		this.accounts.add(account);
		
	}


	public String getUUID() {
		
		return this.uuid;
	}

	
	public boolean validatePin(String pinString) {
		try {
			MessageDigest mDigest = MessageDigest.getInstance("MD5");
			return MessageDigest.isEqual(mDigest.digest(pinString.getBytes()), this.pinHash);
			
		} catch (NoSuchAlgorithmException e) {
			System.err.println("error, No Algorithm exception");
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}


	public String getFirstName() {
	
		return this.firstName;
	}


	public void printAccountsSummary() {
		System.out.printf("\n\n%s's accounts summary\n", this.firstName);
		for (int i = 0; i < this.accounts.size(); i++) {
			System.out.printf("  %d) %s\n",i+1, this.accounts.get(i).getSummaryLine());
			
		}
		System.out.println();
	}


	public int numAccounts() {
		
		return this.accounts.size();
	}


	public void printAcctTransHistory(int acctIndx) {
		this.accounts.get(acctIndx).printTransHistory();
		
		
	}


	public double getAcctBalance(int acctIndx) {
		
		return this.accounts.get(acctIndx).getBalance();
	}


	public Object getAcctUUID(int acctIndx) {
		
		return this.accounts.get(acctIndx).getUUID();
	}


	public void addAcctTransaction(int acctIdx , double amount, String memo) {
		this.accounts.get(acctIdx).addTransaction(amount, memo);
		
	}
	
	
	
	
	

}
