package command;

import model.Account;
import model.Transaction;

import java.util.List;

public class RemoveTransactionCommand implements Command {
    private final List<Transaction> transaction;
    private final Account account;

    public RemoveTransactionCommand(List<Transaction> transaction, Account account) {
        this.transaction = transaction;
        this.account = account;
    }

    @Override
    public void execute() {
        transaction.forEach(account::removeTransaction);
    }

    @Override
    public String getName() {
        return transaction.size() + " transactions removed";
    }

    @Override
    public void undo() {
        transaction.forEach(account::addTransaction);
    }

    @Override
    public void redo() {
        execute();
    }
}
