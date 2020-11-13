package command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Stack;

public class CommandRegistry {
	private ObservableList<Command> commandStack = FXCollections
			.observableArrayList();

	private final Stack<Command> redoCommandStack = new Stack<>();

	public void executeCommand(Command command) {
		command.execute();
		commandStack.add(command);
		redoCommandStack.clear();
	}

	public void redo() {
		if(!redoCommandStack.isEmpty()){
			var cmd = redoCommandStack.pop();
			cmd.redo();
			commandStack.add(cmd);
		}
	}

	public void undo() {
		if(!commandStack.isEmpty()){
			var cmd = commandStack.remove(commandStack.size() - 1);
			cmd.undo();
			redoCommandStack.push(cmd);
		}
	}

	public ObservableList<Command> getCommandStack() {
		return commandStack;
	}
}
