package app.stockfish.exceptions;

public class StockfishInitException extends Exception {
    public StockfishInitException() {
        super();
    }

    public StockfishInitException(String message) {
        super(message);
    }

    public StockfishInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockfishInitException(Throwable cause) {
        super(cause);
    }
}
