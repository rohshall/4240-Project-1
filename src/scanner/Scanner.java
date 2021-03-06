package scanner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import parser.RuleResolver;

/**
 * The scanner is responsible for taking a filename corresponding to the input
 * program, and using the {@link scanner.TokenResolver} to compose a list of
 * {@link scanner.State} objects. These {@link scanner.State} objects are the
 * tokens referred to in the assignment writeup.
 */
public class Scanner {
    /**
     * This method reads through a program text, and uses the
     * {@link scanner.TokenResolver} to create a list of tokens. TODO
     * Application's main method should not be in Scanner.
     * 
     * @param args
     * @throws IOException
     */

    private String filename;
    private char _currentChar;
    private int _line;
    private int _column;
    BufferedReader _br;
    TokenResolver _resolver;
    boolean done = false;
    static boolean debug = true;
    boolean firstToken = true;

    // The oldState refers to the state of the TokenResolver before reading in
    // the most recent character,
    // whereas newState refers to the state afterwards.
    State oldState, newState = null;

    public Scanner(String inputFileName) throws IOException {
        filename = inputFileName;
        _resolver = new TokenResolver();
        _br = new BufferedReader(new FileReader(inputFileName));
        // This function changes the file the buffered reader points at.
        cleanComments();

        _currentChar = (char) _br.read();
        _line = 1;
        _column = 1;
        if (_currentChar == '\n') {
            _line++;
            _column = 0;
        }
    }

    public Token getNextToken() throws IOException, ScannerException {
        Token cur;
        if (done)
            return new Token(RuleResolver.EOF_SYMBOL, RuleResolver.EOF_SYMBOL, _line, _column);

        // We want the TokenResolver to reset its state after we add a token.
        _resolver.reset();

        // While the character assigned to _currentChar isn't the EOF character
        while (_currentChar != (char) -1) {
            oldState = _resolver.getState();
            // The tokenize method returns the state of the TokenResolver, so we
            // don't need a separate call.
            newState = _resolver.tokenize(_currentChar);

            // The state of the TokenResolver will only be null if the character
            // most recently received does not
            // have a valid transition defined from the state the TokenResolver
            // before receiving that character.
            // If this is the case, then the state prior to receiving the most
            // recent character must refer to a
            // token.
            // TODO This assumes that the input program will never be
            // syntactically incorrect. This will need to change.
            if (newState == null) {
                if (oldState.getStateName().isTerminalState()) {
                    // Note that we're making a new state. That's very
                    // important, because states are mutated
                    // as the application runs, and we don't want our list of
                    // tokens to be corrupted.
                    cur = new Token(oldState.getStateName().name(),
                            oldState.getDisplayText(), _line, _column-1);
                    if (debug) {
                        if (!firstToken) {
                            System.out.print(' ');
                        } else firstToken = false;
                        System.out.print(cur.getType());
                    }
                    return cur;
                }
                // The CHARACTER_ACCEPT state is not a terminal state, whereas
                // ID is. However, it's not always
                // clear that we are dealing with an ID until the newState
                // becomes null.
                else {
                    cur = new Token(StateName.ID.name(), oldState.getDisplayText(), _line, _column-1);
                    // If the token value is empty, it means our scanner did not find a state for collected input.
                    // So, we throw an exception. Otherwise, we can just return the token.
                    if (cur.getValue().isEmpty()) {
                        throw new ScannerException(filename, _line, _column, "\""+_currentChar+"\" does not begin a valid token.");
                    }
                    if (debug) {
                        if (!firstToken) {
                            System.out.print(' ');
                        } else firstToken = false;
                        System.out.print(StateName.ID.name());
                    }
                    return cur;
                }
            }
            _currentChar = (char) _br.read();
            _column++;
            if (_currentChar == '\n') {
                _line++;
                _column = 0;
            }
        }
        _br.close();
        done = true;
        System.out.print('\n');
        if (newState.getStateName().name().equals("CHARACTER_ACCEPT")) {
            return new Token(RuleResolver.EOF_SYMBOL, RuleResolver.EOF_SYMBOL, _line, _column-1);
        }
        return new Token(newState.getStateName().name(),
                newState.getDisplayText(), _line, _column-1);
    }

    // TODO this will throw an IOException (or some exception, anyway) if given
    // an input program with fewer than two characters in it. That's pretty
    // dumb.
    private void cleanComments() throws IOException {
        boolean parsingComment = false;
        PrintWriter writer = new PrintWriter("clean.txt", "UTF-8");
        ArrayList<String> symbols = new ArrayList<String>();
        String c = String.valueOf((char) _br.read());
        symbols.add(c);
        while (!c.equals(String.valueOf((char) -1))) {
            if (parsingComment) {
                String scan = "";
                while (!scan.endsWith("*/")) {
                    c = String.valueOf((char) _br.read());
                    if (c.equals("\n"))
                        symbols.add(c);
                    scan += c;
                }
                parsingComment = false;
            }
            c = String.valueOf((char) _br.read());
            if (c.equals("*") && symbols.get(symbols.size() - 1).equals("/")) {
                symbols.remove(symbols.size() - 1);
                parsingComment = true;
                continue;
            }
            if (!c.equals(String.valueOf((char) -1)))
                symbols.add(c);
        }
        for (String s : symbols) {
            writer.print(s);
        }
        writer.close();
        _br.close();
        _br = new BufferedReader(new FileReader("clean.txt"));
    }

    public void afterError() throws IOException {
        firstToken = true;
        _currentChar = (char) _br.read();
        if (_currentChar == '\n') {
            _line++;
            _column = 0;
        }
    }

    // DEPRECATED but do not remove!! Useful for printing/debugging
    public List<Token> getTokens() throws IOException, ScannerException {
        List<Token> tokens = new ArrayList<Token>();
        Token token = getNextToken();
        while (!token.getType().equals(RuleResolver.EOF_SYMBOL)) {
            tokens.add(token);
            token = getNextToken();
        }
        return tokens;
    }
    
    // DEPRECATED but do not remove!! Useful for printing/debugging
    public static void main(String[] args) throws IOException, ScannerException{
        Scanner scan = new Scanner("test_input/ex1.tiger");
        List<Token> tokens = scan.getTokens();
        for (Token tok : tokens) {
            System.out.println(tok.allDetails());
        }
    }
}
