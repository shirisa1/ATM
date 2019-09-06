import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;


public class ATM implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) throws ClassNotFoundException {
		char opt = '\0';
		int age;
		double annualIncome =0;
		Scanner scanner = new Scanner(System.in);
		Bank theBank = new Bank("Chase Bank");
		System.out.println("**********************");
		System.out.println("Welcome to Chase Bank!");
		System.out.println("**********************\n");
		System.out.println("Let's get you started!\n");
		System.out.println("Enter your first name ");
		String firstName = scanner.nextLine();
		System.out.println("Enter your last name");
		String lastName = scanner.nextLine();
		System.out.println("Enter your salary");
		double salary = scanner.nextDouble();
		System.out.println(firstName.toUpperCase() + ", How old are you?");
		age = scanner.nextInt();
		String pinString = null;
		if (validateAge(age)) {
			
			System.out.println("Enter your pin/password");
			Scanner scanner2 = new Scanner(System.in);
			pinString = scanner2.nextLine();
			
			System.out.println(firstName+ ", What type of account would you like to create?");
			System.out.println("A. Savings \t B. Checkings \t C. Salary");
			System.out.println("Press Any Other Key To Exit");
			accountMaker(opt, theBank, firstName, lastName, annualIncome, salary,pinString);
			validateAge(age);
			databaseUtilConnection(firstName,lastName,age,salary);
			User curUser;
			while (true) {
				// stay in login prompt until successful
				curUser = ATM.mainMenuPrompt(theBank, scanner);

				// stay in main menu until user quits
				ATM.printUserMenu(curUser, scanner);

			}
			
		}else {
			System.err.print("You are too young! Come back when you are 18");
		}
		
		

	}

	private static void accountMaker(int opt, Bank theBank, String firstName, String lastName, double annualIncome, double salary,String pinString) {
	
		Scanner scanner4 = new Scanner(System.in);
		opt = scanner4.next().charAt(0);
		switch (opt) {
		case 'A'://savings account

			User aUser = theBank.addUser(firstName, lastName, pinString);
			Account newAccount = new Account("Savings", aUser, theBank);
			aUser.addAccount(newAccount);
			theBank.addAccount(newAccount);
		
			break;
		case 'B'://current account
			User aUser1 = theBank.addUser(firstName, lastName, pinString);
			annIncomeValidate(aUser1, theBank, annualIncome);

			break;
		case 'C'://salary account
			User aUser2 = theBank.addUser(firstName, lastName, pinString);
			salaryValidate(aUser2, theBank, salary);
		
			break;

		default:
			String y ="Sad to see you go, goodbye!";
			FunctionalInterface fobj = (String x)->System.out.println(x);
			fobj.abstractFun(y);
			System.exit(0);
		}
		
	}

	private static boolean validateAge(int age) {
		if (age>=18) {
			return true;
		}else {
			return false;
		}
		
	}

	private static void databaseUtilConnection(String firstName, String lastName, int age, double salary) throws ClassNotFoundException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@oracle.cccswnvlzm9u.us-east-2.rds.amazonaws.com:1521:orcl", "admin", "12345678");
			connection.setAutoCommit(false);
			String query = "insert into BankUser(firstName,lastName,age,salary)values (?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, firstName);
			statement.setString(2, lastName);
			statement.setInt(3, age);
			statement.setDouble(4, salary);
			statement.executeUpdate();
			System.out.println("Please wait for a moment");
			connection.commit();
			connection.close();
			

		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void printUserMenu(User theUser, Scanner scanner) {
		theUser.printAccountsSummary();
		int choice;
		do {
			System.out.printf("Welcome %s, what would you like to do?\n", theUser.getFirstName());
			System.out.println(" 1) Show account transaction history");
			System.out.println(" 2) Withdraw");
			System.out.println(" 3) Deposit");
			System.out.println(" 4) Transfer");
			System.out.println(" 5) Quit");
			System.out.println();
			System.out.println("enter choice");
			choice = scanner.nextInt();
			if (choice < 1 || choice > 5) {
				System.out.println("Invalid choice. please choose 1-5");

			}
			// New User Hirisave,Sachin with ID 802294 created.
		} while (choice < 1 || choice > 5);
		// switch process for choice
		switch (choice) {
		case 1:
			ATM.showTransHistory(theUser, scanner);
			break;
		case 2:
			ATM.withdrawFunds(theUser, scanner);
			break;
		case 3:
			ATM.depositFunds(theUser, scanner);
			break;
		case 4:
			ATM.transferFunds(theUser, scanner);
			break;
		case 5:
			scanner.nextLine();
			break;
		}
		// show menu if user wants to quit
		if (choice != 5) {
			ATM.printUserMenu(theUser, scanner);

		}
	}

	private static void depositFunds(User theUser, Scanner scanner) {

		int toAcct;
		double amount;
		double acctBal;
		String memo;

		do {
			System.out.printf("Enter the number (1-%d) of the account\n" + "to deposit in: ", theUser.numAccounts());
			toAcct = scanner.nextInt() - 1;
			if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account, please try again");
			}

		} while (toAcct < 0 || toAcct >= theUser.numAccounts());
		acctBal = theUser.getAcctBalance(toAcct);
		do {
			System.out.printf("Enter the amount to deposit(max $%.02f): $", acctBal);
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("amount must be greater than 0");

			}
		} while (amount < 0);
		scanner.nextLine();
		System.out.println("Enter a memo");
		memo = scanner.nextLine();
		theUser.addAcctTransaction(toAcct, amount, memo);

	}

	private static void withdrawFunds(User theUser, Scanner scanner) {
		int fromAcct;
		double amount;
		double acctBal;
		String memo;

		do {
			System.out.printf("Enter the number (1-%d) of the account\n" + "to transfer from: ", theUser.numAccounts());
			fromAcct = scanner.nextInt() - 1;
			if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account, please try again");
			}

		} while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
		acctBal = theUser.getAcctBalance(fromAcct);
		do {
			System.out.printf("Enter the amount to withdraw(max $%.02f): $", acctBal);
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("amount must be greater than 0");

			} else if (amount > acctBal) {
				System.out.printf("Amount must not be greater than\n" + "balance of $%.02f.\n", acctBal);
			}
		} while (amount < 0 || amount > acctBal);
		scanner.nextLine();
		System.out.println("Enter a memo");
		memo = scanner.nextLine();
		theUser.addAcctTransaction(fromAcct, -1 * amount, memo);

	}

	private static void transferFunds(User theUser, Scanner scanner) {
		int fromAcct;
		int toAcct;
		double amount;
		double acctBal;

		do {
			System.out.printf("Enter the number (1-%d) of the account\n" + "to withdraw from: ", theUser.numAccounts());
			fromAcct = scanner.nextInt() - 1;
			if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account, please try again");
			}

		} while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
		acctBal = theUser.getAcctBalance(fromAcct);

		do {
			System.out.printf("Enter the number (1-%d) of the account\n" + "to transfer to: ", theUser.numAccounts());
			toAcct = scanner.nextInt() - 1;
			if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account, please try again");
			}

		} while (toAcct < 0 || toAcct >= theUser.numAccounts());

		do {
			System.out.printf("Enter the amount to transfer(max $%.02f): $", acctBal);
			amount = scanner.nextDouble();
			if (amount < 0) {
				System.out.println("amount must be greater than 0");

			} else if (amount > acctBal) {
				System.out.printf("Amount must not be greater than\n" + "balance of $%.02f.\n", acctBal);
			}
		} while (amount < 0 || amount > acctBal);

		theUser.addAcctTransaction(fromAcct, -1 * amount,
				String.format("Transfer to account %s", theUser.getAcctUUID(toAcct)));
		theUser.addAcctTransaction(toAcct, amount,
				String.format("Transfer to account %s", theUser.getAcctUUID(fromAcct)));
	}

	private static void showTransHistory(User theUser, Scanner scanner) {
		int theAcct;
		do {
			System.out.printf("Enter the number (1-%d) of the account" + " whose transaction you want to see:",
					theUser.numAccounts());
			theAcct = scanner.nextInt() - 1;
			if (theAcct < 0 || theAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account, Please try again");

			}
		} while (theAcct < 0 || theAcct >= theUser.numAccounts());
		theUser.printAcctTransHistory(theAcct);

	}

	private static User mainMenuPrompt(Bank theBank, Scanner scanner) {
		String userIDString;
		String pinString;
		User authUser;
		Scanner scanner1 = new Scanner(System.in);
		// prompt user for ID and pin
		do {
			System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
			System.out.print("Enter user ID:");
			userIDString = scanner1.nextLine();
			System.out.print("Enter the Password: ");
			pinString = scanner1.nextLine();

			// get user object for ID and pin combo
			authUser = theBank.userLogin(userIDString, pinString);
			if (authUser == null) {
				System.out.println("Incorrect User ID/pin " + "please try again.");
			}
		} while (authUser == null);
		
		return authUser;
		
	}
	
	private static void salaryValidate(User theUser,Bank theBank, double salary) {
		if (salary>=5000) {
			System.out.println("Wonderful, you meet our salary account requirements ");
			System.out.println("Please Deposit $5000 here");
			Scanner sc2 =new Scanner(System.in);
			double minBal = sc2.nextDouble();
			if (minBal>=5000) {
				Account newAccount2 = new Account("Salary", theUser, theBank);
				theUser.addAccount(newAccount2);
				theBank.addAccount(newAccount2);
				theUser.addAcctTransaction(1, 5000.00d, "First Deposit");
			}else {
				System.err.println("Initial Deposit must be $5000");
				System.exit(0);
			}
		}else {
			System.out.println("Your salary is not greater than $5000 to create salary account");
			System.exit(0);
		}
	}
	
	private static void annIncomeValidate(User theUser, Bank theBank, double annualIncome) {
		System.out.println("What is your Annual Income?");
		Scanner sc1 = new Scanner(System.in);
		annualIncome = sc1.nextDouble();
		if (annualIncome >=30000) {
			System.out.println("Wonderful, do initial deposit of [$15000 or more] to create your account ");
			System.out.println("Deposit 15000 here");
			double minBal = sc1.nextDouble();
			if (minBal>=15000) {
			
			Account newAccount1 = new Account("Checking", theUser, theBank);
			theUser.addAccount(newAccount1);
			theBank.addAccount(newAccount1);
			theUser.addAcctTransaction(1, 15000.00d, "First Deposit");
			}else {
				System.err.println("Initial Deposit must be $15000");
				System.exit(0);
			}
			
		}else {
			System.err.println("To create Current account you must have annual income of $30000");
			System.exit(0);
		}
		
		
	}
	
}


