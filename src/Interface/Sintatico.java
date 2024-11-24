package Interface;

import java.util.Stack;

public class Sintatico implements Constants {

    private Stack stack = new Stack();
    private Token currentToken;
    private Token previousToken;
    private Lexico scanner;
    private Semantico semanticAnalyser;

    private static final boolean isTerminal(int x) {
        return x < FIRST_NON_TERMINAL;
    }

    private static final boolean isNonTerminal(int x) {
        return x >= FIRST_NON_TERMINAL && x < FIRST_SEMANTIC_ACTION;
    }

    private static final boolean isSemanticAction(int x) {
        return x >= FIRST_SEMANTIC_ACTION;
    }

    public String getToken() {
        if (currentToken != null) {
            // Retorna apenas o lexema (token real)
            return currentToken.getLexeme();
        }
        return ""; // Retorna uma string vazia se o token for nulo
    }

    public String getTokenClassName() {
        if (currentToken != null) {
            int id = currentToken.getId(); // Obtém o ID do token atual
            switch (id) {
                // Palavras reservadas
                case Constants.t_main:
                case Constants.t_read:
                case Constants.t_true:
                case Constants.t_false:
                case Constants.t_end:
                case Constants.t_write:
                case Constants.t_writeln:
                case Constants.t_if:
                case Constants.t_elif:
                case Constants.t_else:
                case Constants.t_repeat:
                case Constants.t_until:
                case Constants.t_while:
                    return "pr"; // Palavras reservadas

                // Identificadores e constantes
                case Constants.t_id:
                    return "id";
                case Constants.t_cte_int:
                    return "cte_int";
                case Constants.t_cte_real:
                    return "cte_real";
                case Constants.t_string:
                    return "string";

                // Operadores lógicos
                case Constants.t_TOKEN_20:
                    return "simbolo especial"; // &&
                case Constants.t_TOKEN_21:
                    return "simbolo especial"; // ||
                case Constants.t_TOKEN_22:
                    return "simbolo especial"; // !

                // Operadores relacionais
                case Constants.t_TOKEN_23:
                    return "simbolo especial"; // ==
                case Constants.t_TOKEN_24:
                    return "simbolo especial"; // !=
                case Constants.t_TOKEN_25:
                    return "simbolo especial"; // <
                case Constants.t_TOKEN_26:
                    return "simbolo especial"; // >

                // Operadores aritméticos
                case Constants.t_TOKEN_27:
                    return "simbolo especial"; // +
                case Constants.t_TOKEN_28:
                    return "simbolo especial"; // -
                case Constants.t_TOKEN_29:
                    return "simbolo especial"; // *
                case Constants.t_TOKEN_30:
                    return "simbolo especial"; // /

                // Delimitadores
                case Constants.t_TOKEN_31:
                    return "simbolo especial"; // ,
                case Constants.t_TOKEN_32:
                    return "simbolo especial"; // ;
                case Constants.t_TOKEN_33:
                    return "simbolo especial"; // =
                case Constants.t_TOKEN_34:
                    return "simbolo especial"; // (
                case Constants.t_TOKEN_35:
                    return "simbolo especial"; // )

                default:
                    return "token_invalido"; // Para qualquer token não mapeado
            }
        }
        return "token_nulo"; // Caso não haja token atual
    }

    private boolean step() throws LexicalError, SyntaticError, SemanticError {
        if (currentToken == null) {
            int pos = 0;
            if (previousToken != null) {
                pos = previousToken.getPosition() + previousToken.getLexeme().length();
            }

            currentToken = new Token(DOLLAR, "EOF", pos);
        }

        int x = ((Integer) stack.pop()).intValue();
        int a = currentToken.getId();

        if (x == EPSILON) {
            return false;
        } else if (isTerminal(x)) {
            if (x == a) {
                if (stack.empty()) {
                    return true;
                } else {
                    previousToken = currentToken;
                    currentToken = scanner.nextToken();
                    return false;
                }
            } else {
                throw new SyntaticError(PARSER_ERROR[x], currentToken.getPosition());
            }
        } else if (isNonTerminal(x)) {
            if (pushProduction(x, a)) {
                return false;
            } else {
                throw new SyntaticError(PARSER_ERROR[x], currentToken.getPosition());
            }
        } else // isSemanticAction(x)
        {
            semanticAnalyser.executeAction(x - FIRST_SEMANTIC_ACTION, previousToken);
            return false;
        }
    }

    private boolean pushProduction(int topStack, int tokenInput) {
        int p = PARSER_TABLE[topStack - FIRST_NON_TERMINAL][tokenInput - 1];
        if (p >= 0) {
            int[] production = PRODUCTIONS[p];
            //empilha a produção em ordem reversa
            for (int i = production.length - 1; i >= 0; i--) {
                stack.push(new Integer(production[i]));
            }
            return true;
        } else {
            return false;
        }
    }

    public void parse(Lexico scanner, Semantico semanticAnalyser) throws LexicalError, SyntaticError, SemanticError {
        this.scanner = scanner;
        this.semanticAnalyser = semanticAnalyser;

        stack.clear();
        stack.push(new Integer(DOLLAR));
        stack.push(new Integer(START_SYMBOL));

        currentToken = scanner.nextToken();

        while (!step())
            ;
    }
}
