package bankApp.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bankApp.Model.BankAccount;
import bankApp.Model.Transaction;
import bankApp.Storage.FileStorage;
import bankApp.exception.AccountNotFoundException;
import bankApp.exception.InsufficientFundsException;
import bankApp.exception.InvalidAmountException;

public class BankingService {

	private final Map<String, BankAccount> accounts;
	private final List<Transaction> transactions;
	private final FileStorage fileStorage;

	public BankingService(FileStorage fileStorage) throws IOException, InvalidAmountException {
		this.accounts = new LinkedHashMap<>();
		this.transactions = new ArrayList<>();
		this.fileStorage = fileStorage;
		
		loadData();
	}

	public void createAccount(String accountNumber, String accountHolderName, BigDecimal openingBalance)
			throws InvalidAmountException {

		if (accounts.containsKey(accountNumber)) {
			throw new IllegalArgumentException("Account number already exists.");
		}

		BankAccount account = new BankAccount(accountNumber, accountHolderName, openingBalance);

		accounts.put(account.getAccountNumber(), account);

		transactions.add(new Transaction("ACCOUNT_CREATED", "-", account.getAccountNumber(), openingBalance));
	}

	public BankAccount findAccount(String accountNumber) throws AccountNotFoundException {

		BankAccount account = accounts.get(accountNumber);

		if (account == null) {
			throw new AccountNotFoundException("Account not found: " + accountNumber);
		}

		return account;
	}

	public void deposit(String accountNumber, BigDecimal amount)
	        throws AccountNotFoundException, InvalidAmountException {

	    BankAccount account = findAccount(accountNumber);

	    account.deposit(amount);

	    transactions.add(
	        new Transaction(
	            "DEPOSIT",
	            "-",
	            accountNumber,
	            amount
	        )
	    );
	}
	
	public void withdraw(String accountNumber, BigDecimal amount)
	        throws AccountNotFoundException,
	               InvalidAmountException,
	               InsufficientFundsException {

	    BankAccount account = findAccount(accountNumber);

	    account.withdraw(amount);

	    transactions.add(
	        new Transaction(
	            "WITHDRAWAL",
	            accountNumber,
	            "-",
	            amount
	        )
	    );
	}
	
	public void transfer(String fromAccountNumber,
	                     String toAccountNumber,
	                     BigDecimal amount)
	        throws AccountNotFoundException,
	               InvalidAmountException,
	               InsufficientFundsException {

	    if (fromAccountNumber.equals(toAccountNumber)) {
	        throw new IllegalArgumentException(
	            "Cannot transfer money to the same account."
	        );
	    }

	    BankAccount sourceAccount = findAccount(fromAccountNumber);
	    BankAccount targetAccount = findAccount(toAccountNumber);

	    // Validate the amount before changing either balance.
	    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new InvalidAmountException(
	            "Transfer amount must be greater than zero."
	        );
	    }

	    // Check the source balance before making any changes.
	    if (sourceAccount.getBalance().compareTo(amount) < 0) {
	        throw new InsufficientFundsException(
	            "Insufficient balance for transfer."
	        );
	    }

	    // Perform the transfer.
	    sourceAccount.withdraw(amount);
	    targetAccount.deposit(amount);

	    // Record the successful transfer.
	    transactions.add(
	        new Transaction(
	            "TRANSFER",
	            fromAccountNumber,
	            toAccountNumber,
	            amount
	        )
	    );
	}
	
	
	public List<Transaction> getTransactionHistory() {
	    return new ArrayList<>(transactions);
	}
	

	public List<Transaction> getTransactionHistory(String accountNumber)
	        throws AccountNotFoundException {

	    findAccount(accountNumber);

	    List<Transaction> accountTransactions = new ArrayList<>();

	    for (Transaction transaction : transactions) {
	        if (transaction.getFromAccount().equals(accountNumber)
	                || transaction.getToAccount().equals(accountNumber)) {
	            accountTransactions.add(transaction);
	        }
	    }

	    return accountTransactions;
	}
	
	
	public void saveData() throws IOException {

	    List<String> accountLines = new ArrayList<>();

	    for (BankAccount account : accounts.values()) {

	        String encodedName = Base64.getEncoder().encodeToString(
	            account.getAccountHolderName()
	                   .getBytes(StandardCharsets.UTF_8)
	        );

	        String line = account.getAccountNumber()
	                + "\t" + encodedName
	                + "\t" + account.getBalance().toPlainString();

	        accountLines.add(line);
	    }

	    List<String> transactionLines = new ArrayList<>();

	    for (Transaction transaction : transactions) {
	        transactionLines.add(transaction.toFileLine());
	    }

	    fileStorage.writeLines("accounts.txt", accountLines);
	    fileStorage.writeLines("transactions.txt", transactionLines);
	}
	
	public void loadData()
	        throws IOException, InvalidAmountException {

	    List<String> accountLines =
	            fileStorage.readLines("accounts.txt");

	    Map<String, BankAccount> loadedAccounts =
	            new LinkedHashMap<>();

	    for (String line : accountLines) {
	        String[] fields = line.split("\t", -1);

	        if (fields.length != 3) {
	            throw new IOException("Invalid account record.");
	        }

	        try {
	            String accountNumber = fields[0];

	            String accountHolderName = new String(
	                Base64.getDecoder().decode(fields[1]),
	                StandardCharsets.UTF_8
	            );

	            BigDecimal balance = new BigDecimal(fields[2]);

	            BankAccount account = new BankAccount(
	                accountNumber,
	                accountHolderName,
	                balance
	            );

	            if (loadedAccounts.putIfAbsent(
	                    accountNumber, account) != null) {
	                throw new IOException(
	                    "Duplicate account number in saved data: "
	                    + accountNumber
	                );
	            }

	        } catch (IllegalArgumentException exception) {
	            throw new IOException(
	                "Invalid account record in accounts.txt.", exception
	            );
	        }
	    }

	    List<String> transactionLines =
	            fileStorage.readLines("transactions.txt");

	    List<Transaction> loadedTransactions = new ArrayList<>();

	    for (String line : transactionLines) {
	        try {
	            loadedTransactions.add(
	                Transaction.fromFileLine(line)
	            );
	        } catch (RuntimeException exception) {
	            throw new IOException(
	                "Invalid transaction record in transactions.txt.",
	                exception
	            );
	        }
	    }

	    accounts.clear();
	    accounts.putAll(loadedAccounts);

	    transactions.clear();
	    transactions.addAll(loadedTransactions);
	}
	
}
