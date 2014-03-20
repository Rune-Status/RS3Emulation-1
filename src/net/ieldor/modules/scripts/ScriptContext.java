package net.ieldor.modules.scripts;

import net.scapeemulator.game.button.ButtonDispatcher;
import net.scapeemulator.game.button.ButtonHandler;
import net.scapeemulator.game.command.CommandDispatcher;
import net.scapeemulator.game.command.CommandHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hadyn Richard
 */
public final class ScriptContext {

    /**
     * The list of button handlers.
     */
    private List<ButtonHandler> buttonHandlers = new LinkedList<>();

    /**
     * The list of command handlers.
      */
    private List<CommandHandler> commandHandlers = new LinkedList<>();

    /**
     * Constructs a new {@link ScriptContext};
     */
    public ScriptContext() {}

    /**
     * Adds a button dispatcher decorator to the list of decorators.
     * @param handler The button handler to add.
     */
    public void addButtonHandler(ButtonHandler handler) {
        buttonHandlers.add(handler);
    }

    /**
     * Decorates a button dispatcher with all the button handlers registered to the context.
     * @param dispatcher The dispatcher to decorate.
     */
    public void decorateButtonDispatcher(ButtonDispatcher dispatcher) {
        for(ButtonHandler handler : buttonHandlers) {
            dispatcher.bind(handler);
        }
    }

    /**
     * Adds a button dispatcher decorator to the list of decorators.
     * @param handler The button handler to add.
     */
    public void addCommandHandler(CommandHandler handler) {
        commandHandlers.add(handler);
    }

    /**
     * Decorates a button dispatcher with all the button handlers registered to the context.
     * @param dispatcher The dispatcher to decorate.
     */
    public void decorateCommandDispatcher(CommandDispatcher dispatcher) {
        for(CommandHandler handler : commandHandlers) {
            dispatcher.bind(handler);
        }
    }
}