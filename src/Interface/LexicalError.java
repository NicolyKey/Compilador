package Interface;

public class LexicalError extends AnalysisError
{
    public LexicalError(String msg, int position)
	 {
        super(msg, position);
    }

    public LexicalError(String msg, String palavra_reservada_inválida, int start)
    {
        super(msg);
    }

}
