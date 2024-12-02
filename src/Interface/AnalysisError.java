package Interface;

public class AnalysisError extends Exception {

    private int position;
    private Token token;

    public AnalysisError(String msg, int position) {
        super(msg);
        this.position = position;
    }

    public AnalysisError(String msg) {
        super(msg);
        this.position = -1;
    }

    public int getPosition() {
        return position;
    }

    public int getLinhaToken(String text) {
        int line = 1;
        for (int i = 0; i < position && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    public String getToken(String text) {
        if (position >= 0 && position < text.length()) {
            String delimiters = "\t\n\r,;()[]{}+-*/=<>!&|";
            int startPos = position;

            if (position > 0 && position < text.length() - 1) {
                char currentChar = text.charAt(position);
                char nextChar = text.charAt(position + 1);

                if ((currentChar == '&' && nextChar == '&')
                        || (currentChar == '|' && nextChar == '|')) {
                    return text.substring(position, position + 2);
                }
            }

            while (startPos > 0 && delimiters.indexOf(text.charAt(startPos - 1)) == -1) {
                startPos--;
            }

            int endPos = position;
            while (endPos < text.length() && delimiters.indexOf(text.charAt(endPos)) == -1) {
                endPos++;
            }

            return text.substring(startPos, endPos);
        } else {
            return "";
        }
    }

    public String getTokenEspecial(String text) {
        // Verifica se a posição está dentro dos limites válidos
        if (position >= 0 && position < text.length()) {
            int start = position;

            // Move o índice para trás até encontrar um espaço ou o início do texto
            while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
                start--;
            }

            // Move o índice para frente até encontrar um espaço ou o fim do texto
            int end = position;
            while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
                end++;
            }

            // Retorna o token entre as posições `start` e `end`
            return text.substring(start, end);
        } else {
            // Retorna uma string vazia se a posição estiver fora dos limites
            return "";
        }
    }

}
