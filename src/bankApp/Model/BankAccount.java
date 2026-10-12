package bankApp.Model;

import java.math.BigDecimal;

import bankApp.exception.InsufficientFundsException;
import bankApp.exception.InvalidAmountException;

public class BankAccount
{
	private final String accountNumber;
	private final String accountHolderName;
	private BigDecimal balance;
	
	 public BankAccount(String accountNumber,
             String accountHolderName,
             BigDecimal openingBalance)
  throws InvalidAmountException {

if (accountNumber == null || accountNumber.isBlank()) {
  throw new IllegalArgumentException(
          "Account number cannot be empty");
}

if (accountHolderName == null
      || accountHolderName.isBlank()) {
  throw new IllegalArgumentException(
          "Account holder name cannot be empty");
}

if (openingBalance == null
      || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
  throw new InvalidAmountException(
          "Opening balance cannot be negative");
}
this.accountNumber = accountNumber;
this.accountHolderName = accountHolderName;
this.balance = openingBalance;
}

	 public String getAccountNumber() {
		 return accountNumber;
	 }

	 public String getAccountHolderName() {
		 return accountHolderName;
	 }

	 public BigDecimal getBalance() {
		 return balance;
	 }
	 
	 public void deposit(BigDecimal amount) throws InvalidAmountException
	 {
		 validateAmount(amount);
		balance=balance.add(amount);
	 }
	 
	 public void withdraw(BigDecimal amount) throws InvalidAmountException,InsufficientFundsException
	 {
		 validateAmount(amount);
		 
		 if(balance.compareTo(amount)<0)
		 {
			 throw new InsufficientFundsException("Insufficient account balance");
		 }
		 
		 balance=balance.subtract(amount);
	 }
	 
	 private void validateAmount(BigDecimal amount) throws InvalidAmountException
	 {
		 
		 if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0)
		 {
			 throw new InvalidAmountException("Amount must be greater then zero");
		 }
	 }

}
