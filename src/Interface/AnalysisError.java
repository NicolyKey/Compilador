package Interface;

public class AnalysisError extends Exception
{
    private int position;

    public AnalysisError(String msg, int position)
    {
        super(msg);
        this.position = position;
    }

    public AnalysisError(String msg)
    {
        super(msg);
        this.position = -1;
    }

  public int getPosition(String text)
    {
        int line = 1;
        for (int i = 0; i < position && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    public String toString()
    {
        return super.toString() + ", @ "+position;
    }

    public String getToken(String text) {
        if (position >= 0 && position < text.length()) {
            return String.valueOf(text.charAt(position));
        } else {
            return ""; 
        }
    }
}
