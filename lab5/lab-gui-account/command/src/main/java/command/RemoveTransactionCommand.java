package command;

import model.Account;
import model.Transaction;

import java.util.List;

public class RemoveTransactionCommand implements Command {
    private List<Transaction> transaction;
    private Account account;

    public RemoveTransactionCommand(List<Transaction> transaction, Account account) {
        this.transaction = transaction;
        this.account = account;
    }

    @Override
    public void execute() {
        transaction.forEach(t -> {
            account.removeTransaction(t);
        });
    }

    @Override
    public String getName() {
        return transaction.size() + " transactions removed";
    }
}
