package scanner;

/**
 * StateName is considered functionally equivalent to the token types mentioned in the project writeup.
 * StateNames know if they are terminal states.  (That is, a StateName knows if it is a proper token type, or an
 * intermediate state used by the scanner.)
 */
public enum StateName {
    CHARACTER_ACCEPT(false),
    GREATER(true),
    LESSER(true),
    STRING_INTERPRET(false),
    ESCAPE_SEQUENCE(false),
    ESCAPE_C(true),
    ESCAPE_WHITESPACE(false),
    ESCAPE_D(true),
    ESCAPE_DD(true),
    ID(true),
    STRLIT(true),
    INTLIT(true),
    END(true),
    ENDIF(true),
    ENDDO(true),
    COMMA(true),
    COLON(true),
    SEMI(true),
    LPAREN(true),
    RPAREN(true),
    RBRACK(true),
    LBRACK(true),
    LBRACE(true),
    RBRACE(true),
    PERIOD(true),
    PLUS(true),
    MINUS(true),
    MULT(true),
    DIV(true),
    EQ(true),
    AND(true),
    OR(true),
    GREATEREQ(true),
    LESSEREQ(true),
    ASSIGN(true),
    NEQ(true),
    ARRAY(true),
    BREAK(true),
    BEGIN(true),
    DO(true),
    FOR(true),
    FUNCTION(true),
    IF(true),
    IN(true),
    LET(true),
    NIL(true),
    OF(true),
    TO(true),
    THEN(true),
    TYPE(true),
    VAR(true),
    WHILE(true),
    INT(true),
    ELSE(true);

    private final boolean IS_VALID_TOKEN;

    StateName(boolean isValidToken) {
        this.IS_VALID_TOKEN = isValidToken;
    }
    public boolean isTerminalState() {
        return this.IS_VALID_TOKEN;
    }

}
