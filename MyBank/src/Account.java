import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class Account {
	private String name;
	private String uuid;
	private User holder;
	private ArrayList<Transaction> transactions;
	
	public Account(String name, User holder, Bank theBank) {
		this.name = name; //account name
		this.holder = holder;
		
		this.uuid = theBank.getNewAccountUUID();
		//transactions
		
		this.transactions = new ArrayList<Transaction>();
		
		
	}

	public String getUUID() {
		
		return this.uuid;
	}

	public String getSummaryLine() {
	double balance = this.getBalance();
	if (balance>=0) {
		return String.format("%s : $%.02f : %s", this.uuid, balance,this.name);
	}
	else {
		return String.format("%s : $%.02f : %s", this.uuid, balance,this.name);
	}
		
	}

	public double getBalance() {
		double balance = 0;
		for(Transaction t: this.transactions) {
			balance  += t.getAmount();
		}
		return balance;
	}

	public void printTransHistory() {
		System.out.printf("\ntransaction history for account%s\n",this.uuid);

		
		try {
			//write the transaction
			
			FileOutputStream fileOutputStream = new FileOutputStream("transactions.txt", true);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			for (int i = transactions.size()-1; i>=0 ;i--) {
				System.out.println(this.transactions.get(i).getSummaryLine());
				objectOutputStream.writeBytes(this.transactions.get(i).getSummaryLine());
				objectOutputStream.writeBytes("\n");
			}
			objectOutputStream.flush();
			objectOutputStream.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addTransaction(double amount, String memo) {
		Transaction newTrans = new Transaction(amount, memo ,this);
		this.transactions.add(newTrans);
		
	}
	
	

}
