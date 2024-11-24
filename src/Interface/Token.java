package Interface;

public class Token {

    private int id;
    private String lexeme;
    private int position;

    public Token(int id, String lexeme, int position) {
        this.id = id;
        this.lexeme = lexeme;
        this.position = position;
    }

    public final int getId() {
        return id;
    }

    public final String getLexeme() {
        return lexeme;
    }

    public final int getPosition() {
        return position;
    }

    public String toString() {
        return id + " ( " + lexeme + " ) @ " + position;
    }

    
     // Método para validar o lexeme
    private void validateLexeme() throws LexicalError {

        if (getTokenClassName().equals("pr")) {
            switch (lexeme) {
                case "String":
                case "main":
                case "read":
                case "true":
                case "false":
                case "end":
                case "write":
                case "writeln":
                case "if":
                case "elif":
                case "else":
                case "repeat":
                case "until":
                case "while":
                    // Lexeme válido, continua execução
                    break;
                default:
                    throw new LexicalError("palavra reservada invalida");
            }
        }
    }

    // Mantém o método de nome de classe para retornar o tipo de token
    public String getTokenClassName() {
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
                return "pr";

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

}
