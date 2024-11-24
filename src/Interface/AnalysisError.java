package Interface;

public class AnalysisError extends Exception {

    private int position;

    public AnalysisError(String msg, int position) {
        super(msg);
        this.position = position;
    }

    public AnalysisError(String msg) {
        super(msg);
        this.position = -1;
    }

    public int getPosition(String text) {
        int line = 1;
        for (int i = 0; i < position && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    public String getToken(String text) {
        // Verifica se a posição está dentro dos limites do texto
        if (position >= 0 && position < text.length()) {
            // Define os delimitadores de tokens, como espaços em branco, pontuações, etc.
            String delimiters = "\t\n\r,;()[]{}+-*/=<>!&|";
            int startPos = position;

            // Move a posição para trás até encontrar um delimitador ou o início do texto
            while (startPos > 0 && delimiters.indexOf(text.charAt(startPos - 1)) == -1) {
                startPos--;
            }

            // Move a posição para frente até encontrar um delimitador ou o fim do texto
            int endPos = position;
            while (endPos < text.length() && delimiters.indexOf(text.charAt(endPos)) == -1) {
                endPos++;
            }

            // Retorna o token entre startPos e endPos
            return text.substring(startPos, endPos);
        } else {
            return ""; // Retorna uma string vazia se a posição estiver fora dos limites
        }
    }

    public String toString() {
        return super.toString() + ", @ " + position;
    }
}
