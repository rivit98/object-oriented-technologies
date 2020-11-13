package command;

import model.Account;
import model.Transaction;

public class AddTransactionCommand implements Command {
    private Transaction transaction;
    private Account account;

    public AddTransactionCommand(Transaction transaction, Account account) {
        this.transaction = transaction;
        this.account = account;
    }

    @Override
    public void execute() {
        account.addTransaction(transaction);
    }

    @Override
    public String getName() {
        return "New transaction: " + transaction.toString();
    }

    @Override
    public void undo() {
        account.removeTransaction(transaction);
    }

    @Override
    public void redo() {
        execute();
    }
}
